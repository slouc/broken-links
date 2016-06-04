package backend

import java.net.URL

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable
import scala.io.{Codec, Source}

object Checker {

  val conf = new SparkConf().setAppName("broken-links").setMaster("local")
  val sc = new SparkContext(conf)

  def getBrokenLinks(url: String, details: Boolean = false): Set[String] = {

    val links = getLinks(url)
    val exceptionsMap = if (details) Some(mutable.Map[String, Exception]()) else None
    val brokenLinks = links.filter(check(_, exceptionsMap))

    brokenLinks match {
      case links if links.isEmpty => Set(NoResultsFound)
      case links =>
        if (details) brokenLinks.map(link => link + " ; " + exceptionsMap.get(link).getMessage() + "\n")
        else links
    }
  }

  private def getLinks(url: String): Set[String] = {
    implicit val codec = Codec("UTF-8")

    val lines = Source.fromURL(url).getLines
    val hrefLines = lines.flatMap(line => line.split("href=\""))
    val httpLines = hrefLines.filter(line => line.startsWith("http"))

    val result = sc.parallelize(httpLines.toSeq).map(line => line.split("\"")(0))
    result.collect().toSet
  }

  private def open(url: String, timeout: Int = 5000) = {
    val conn = (new URL(url)).openConnection()
    conn.setConnectTimeout(timeout)
    conn.setReadTimeout(timeout)

    val inputStream = conn.getInputStream()
    Source.fromInputStream(inputStream)
  }

  private def check(url: String, map: Option[mutable.Map[String, Exception]] = None): Boolean = {
    try {
      open(url)
      false
    } catch {
      case e: Exception => {
        if (map.isDefined) map.get += url -> e
        true
      }
    }
  }

  val NoResultsFound = "No results found!"

}
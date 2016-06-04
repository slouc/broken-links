package backend

import java.net.URL

import controllers.Application
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

import scala.collection.mutable
import scala.io.{Codec, Source}

object Checker {

  val conf = new SparkConf().setAppName("broken-links").setMaster("local")
  val sc = new SparkContext(conf)

  def getBrokenLinks(url: String, details: Boolean = false): Set[String] = {

    val links = getLinks(url)
    val brokenLinks = links.filter(check(_)).collect().toSet

    brokenLinks match {
      case links if links.isEmpty => Set(NoResultsFound)
      case links =>
        if (details) brokenLinks.map(link => link + " ; " + "TODO: REASON" + "\n")
        else links
    }
  }

  private def getLinks(url: String): RDD[String] = {
    implicit val codec = Codec("UTF-8")

    val lines = Source.fromURL(url).getLines
    val hrefLines = sc.parallelize(lines.toSeq).flatMap(line => line.split("href=\""))
    val httpLines = hrefLines.filter(line => line.startsWith("http"))

    httpLines.map(line => line.split("\"")(0))

  }

  private def open(url: String, timeout: Int = 5000) = {
    val conn = (new URL(url)).openConnection()
    conn.setConnectTimeout(timeout)
    conn.setReadTimeout(timeout)

    val inputStream = conn.getInputStream()
    Source.fromInputStream(inputStream)
  }

  private def check(url: String): Boolean = {
    try {
      open(url)
      false
    } catch {
      case e: Exception => true
    }
  }

  val NoResultsFound = "No results found!"

}
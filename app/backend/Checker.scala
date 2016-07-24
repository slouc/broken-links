package backend

import java.net.URL

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.io.{Codec, Source}

object Checker {

  val NoResultsFound = "No results found!"
  val timeout = 5000
  val conf = new SparkConf().setAppName("broken-links").setMaster("local")
  val sc = new SparkContext(conf)

  def getBrokenLinks(url: String, details: Boolean = false): Set[(String, Option[String])] = {

    val links = getLinks(url)
    val brokenLinks = links.flatMap(getBrokenLinkInfo).collect().toSet

    brokenLinks match {
      case bl if bl.isEmpty => Set((NoResultsFound, None))
      case bl if (!details) => bl.map(link => (link._1, None))
      case bl =>  bl.map(link => (link._1, Option(link._2)))
    }
  }

  private def getLinks(url: String): RDD[String] = {

    implicit val codec = Codec("UTF-8")

    val lines = Source.fromURL(url).getLines
    val hrefLines = sc.parallelize(lines.toSeq).flatMap(line => line.split("href=\""))
    val httpLines = hrefLines.filter(line => line.startsWith("http"))

    httpLines.map(line => line.split("\"")(0))
  }

  private def getBrokenLinkInfo(url: String): Option[(String, String)] = {

    try {
      val conn = (new URL(url)).openConnection()
      conn.setConnectTimeout(timeout)
      conn.setReadTimeout(timeout)
      val inputStream = conn.getInputStream()
      Source.fromInputStream(inputStream)
      None
    } catch {
      case e: Exception => Option((url, e.getMessage))
    }
  }

}
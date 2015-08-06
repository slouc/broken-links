package backend

import java.io.FileNotFoundException
import scala.io.Source
import java.net.URL
import scala.collection._
import scala.io.Codec

object Checker {

  private def open(url: String, timeout: Int = 5000) = {
    val conn = (new URL(url)).openConnection()
    conn.setConnectTimeout(timeout)
    conn.setReadTimeout(timeout)

    val inputStream = conn.getInputStream()
    Source.fromInputStream(inputStream)
  }

  private def check(url: String, map: mutable.Map[String, Exception] = null): Boolean = {
    val res: Boolean = try {
      open(url)
      false
    } catch {
      case e: Exception => {
        if (map != null) map += url -> e
        true
      }
    }
    res
  }

  def getLinks(url: String): Set[String] = {
    implicit val codec = Codec("UTF-8")
    val lines = Source.fromURL(url, "utf-8").getLines
    val hrefLines = lines.flatMap(line => line.split("href=\""))
    val httpLines = hrefLines.filter(line => line.startsWith("http"))
    httpLines.map(line => line.split("\"")(0)).toSet
  }

  def getBrokenLinks(url: String, useDetails: Boolean = false): Set[String] = {
    if (!useDetails) {
      val links = getLinks(url)
      links.filter(check(_))
    } else {
      val links = getLinks(url)
      val map = mutable.Map[String, Exception]()
      val brokenLinks = links.filter(check(_, map))
      brokenLinks.map(link => link + "\n" + map(link).getMessage() + "\n")
    }
  }

}
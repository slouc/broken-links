package backend

import java.io.FileNotFoundException
import scala.io.Source
import java.net.URL
import scala.collection._

object Checker {

  private def open(url: String) = {

    val timeout = 5000
    val conn = (new URL(url)).openConnection()
    conn.setConnectTimeout(timeout)
    conn.setReadTimeout(timeout)
    val inputStream = conn.getInputStream()

    Source.fromInputStream(inputStream)
  }

  private def check(url: String): Boolean = {
    val res: Boolean = try {
      open(url)
      false
    } catch {
      case e: Exception => true
    }
    res
  }

  private def check(url: String, map: mutable.Map[String, Exception]): Boolean = {
    val res: Boolean = try {
      open(url)
      false
    } catch {
      case e: Exception => {
        map += url -> e
        true
      }
    }
    res
  }

  def getLinks(url: String): Set[String] = {
    val lines = Source.fromURL(url).getLines
    val hrefLines = lines.flatMap(line => line.split("href=\""))
    val httpLines = hrefLines.filter(line => line.startsWith("http"))
    httpLines.map(line => line.split("\"")(0)).toSet
  }

  def getBrokenLinks(url: String): Set[String] = {
    val links = getLinks(url)
    links.filter(check _)
  }

  def getBrokenLinksDetailed(url: String): Set[String] = {
    val links = getLinks(url)
    val map = mutable.Map[String, Exception]()
    val brokenLinks = links.filter(check(_, map))
    brokenLinks.map(link => link + "\n" + map(link).getMessage() + "\n")
  }
  
}
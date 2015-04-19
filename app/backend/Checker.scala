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

  def getBrokenLinks(url: String): Set[String] = {

    val lines = Source.fromURL(url).getLines
    val hrefLines = lines.flatMap(line => line.split("href=\""))
    val httpLines = hrefLines.filter(line => line.startsWith("http"))
    val links = httpLines.map(line => line.split("\"")(0)).toSet

    println("Got " + links.size + " links.")

    val map = mutable.Map[String, Exception]()

    val filtered = links.filter(check(_, map))
    filtered.map(link => link + "\n" + map(link).getLocalizedMessage() + "\n")
    
  }
}
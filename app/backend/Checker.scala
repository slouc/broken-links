package backend

import java.io.FileNotFoundException
import scala.io.Source
import java.net.URL
import scala.collection._

object Checker {

  private def open(url: String) = {

    val timeout = 4000
    val conn = (new URL(url)).openConnection()
    conn.setConnectTimeout(timeout)
    conn.setReadTimeout(timeout)
    val inputStream = conn.getInputStream()

    Source.fromInputStream(inputStream)
  }

  private def check(url: String, map: mutable.Map[String, String]): Boolean = {
    val res: Boolean = try {
      open(url)
      false
    } catch {
      case e: Exception => {
        map += url -> ("\n" + e.getMessage() + "\n")
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

    val map = mutable.Map[String, String]()

    val filtered = links.filter(check(_, map))
    filtered.map(link => link + map(link))
    
  }
}
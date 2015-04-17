package backend

import java.io.FileNotFoundException

import scala.io.Source

object Checker {

  private def check(url: String): Boolean = {
    val res: Boolean = try {
      Source.fromURL(url)
      false
    } catch {
      case e: FileNotFoundException => true
      case _: Throwable => true
    }
    res
  }

  def getBrokenLinks(url: String): Set[String] = {

    val lines = Source.fromURL(url).getLines
    val hrefLines = lines.flatMap(line => line.split("href=\""))
    val httpLines = hrefLines.filter(line => line.startsWith("http"))
    val links = httpLines.map(line => line.split("\"")(0)).toSet
    
    links.filter(check _).toSet
  }
}
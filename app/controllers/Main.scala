package com.slouc.main

import java.io.FileNotFoundException
import scala.io.Source

/**
 *
 * @author slouc
 */
object Main {

  def main(args: Array[String]) {

    def check(url: String) = {
      try {
        Source.fromURL(url)
      } catch {
        case e: FileNotFoundException => println(url)
        case _: Throwable =>
      }
    }
    
    val lines = Source.fromURL("http://www.hashcode.hr/").getLines
    
    val hrefLines = lines.flatMap(line => line.split("href=\""))
    val httpLines = hrefLines.filter(line => line.startsWith("http"))
    val links = httpLines.map(line => line.split("\"")(0))

    links.foreach(check _)

  }
}

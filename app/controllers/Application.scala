package controllers

import org.apache.spark.SparkContext
import play.api.mvc.Controller
import play.api.mvc.Action
import org.apache.spark.SparkConf
import java.io.FileNotFoundException
import scala.io.Source

object Application extends Controller {

  val conf = new SparkConf().setAppName("playtest").setMaster("local")
  val init = println("Hello")

  def index = Action {
    main
    Ok(views.html.index())
  }

  def check(url: String) = {
    try {
      Source.fromURL(url)
    } catch {
      case e: FileNotFoundException => println(url)
      case _: Throwable =>
    }
  }

  def main() = {

    val lines = Source.fromURL("https://github.com/onevcat/Kingfisher").getLines

    val hrefLines = lines.flatMap(line => line.split("href=\""))
    val httpLines = hrefLines.filter(line => line.startsWith("http"))
    val links = httpLines.map(line => line.split("\"")(0))

    links.foreach(check _)
  }

}
package controllers

import org.apache.spark.SparkContext
import play.api.mvc.Controller
import play.api.mvc.Action
import org.apache.spark.SparkConf

object Application extends Controller {

  val conf = new SparkConf().setAppName("playtest").setMaster("local")
  val init = println("Hello")

  def index = Action {
    println("action!")
    val logFile = "/Users/sinisalouc/Documents/hashcode/ldap.rtf"
    val sc = new SparkContext(conf)
    val logData = sc.textFile(logFile, 2).cache()
    val numSparks = logData.filter(line => line.contains("Spark")).count()
    Ok(views.html.index("Lines with Spark: " + numSparks))
  }

}
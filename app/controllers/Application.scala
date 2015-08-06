package controllers

import org.apache.spark.SparkConf

import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Action
import play.api.mvc.Controller
import backend.Checker

object Application extends Controller {

  //  val conf = new SparkConf().setAppName("playtest").setMaster("local")

  def index = Action {
    Ok(views.html.index(form))
  }

  val form = Form("url" -> text)

  def submit = Action { implicit request =>
    val url = form.bindFromRequest.get
    val results = Checker.getBrokenLinks(url)
    Ok(views.html.index(form, Some(results)))
  }

  def submitDetails = Action { implicit request =>
    val url = form.bindFromRequest.get
    Ok("Results:\n\n" + Checker.getBrokenLinks(url, true).mkString("\n"))
  }

}
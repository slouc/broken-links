package controllers

import org.apache.spark.SparkConf

import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Action
import play.api.mvc.Controller
import backend.Checker

case class UrlForm(url: String, checkbox: Option[String])

object Application extends Controller {

  //  val conf = new SparkConf().setAppName("playtest").setMaster("local")

  val form = Form(mapping(
    "url" -> nonEmptyText,
    "checkbox" -> optional(text))(UrlForm.apply)(UrlForm.unapply))

  def index = Action {
    Ok(views.html.index(form))
  }

  def submit = Action { implicit request =>
    val results = form.bindFromRequest.get match {
      case UrlForm(url, Some("on")) => Checker.getBrokenLinks(url, true)
      case UrlForm(url, None)    => Checker.getBrokenLinks(url, false)
    }
    Ok(views.html.index(form, Some(results)))
  }

}
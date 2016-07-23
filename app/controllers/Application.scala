package controllers

import backend.Checker
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, optional, text}
import play.api.mvc.{Action, Controller}

case class UrlForm(url: String, checkbox: Option[String])

object Application extends Controller {

  val form = Form(mapping(
    "url" -> nonEmptyText,
    "checkbox" -> optional(text))(UrlForm.apply)(UrlForm.unapply))

  def index = Action {
    Ok(views.html.index(form))
  }

  def submit = Action { implicit request =>
    val results = form.bindFromRequest.get match {
      case UrlForm(url, Some("on")) => Checker.getBrokenLinks(url, true)
      case UrlForm(url, None)       => Checker.getBrokenLinks(url, false)
    }
    Ok(views.html.index(form, Some(results)))
  }

}

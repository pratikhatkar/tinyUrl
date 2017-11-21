package controllers

import models.{Url}
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

object Application extends Controller {

  val originalUrlForm = Form("originalUrl" -> nonEmptyText)

  val tinyUrlForm = Form("tinyUrl" -> nonEmptyText)

  def index = Action {
    Ok(views.html.index("No url to show", originalUrlForm, tinyUrlForm))
  }

  def tinyUrl = Action { implicit request =>
    originalUrlForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("No url to show", errors, errors)),
      originalUrl => {
        val tinyUrl = Url.getTinyUrl(originalUrl)
        Created(views.html.index(tinyUrl, originalUrlForm, tinyUrlForm))
      }
    )
  }

  def redirect = Action { implicit request =>
    tinyUrlForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index("No url to show", errors, errors)),
      tinyUrl =>
        Url.getOriginalUrl(tinyUrl) match {
          case Some(originalUrl) => Redirect(originalUrl)
          case _ => NotFound("404. TinyUrl not found.")
        }
    )
  }

}
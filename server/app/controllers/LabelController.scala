package controllers

import dao.LabelDAO
import javax.inject.{Inject, Singleton}
import models.Label
import play.Environment
import play.api.data.Forms._
import play.api.data._
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class LabelController @Inject()(cc: ControllerComponents, environment: Environment, labelDAO: LabelDAO) extends AbstractController(cc) {

  case class NewLabelRequest(label: String)

  def labelForm = Form(
    mapping("label" -> nonEmptyText)(NewLabelRequest.apply)(NewLabelRequest.unapply)
  )

  def postLabel = Action.async { implicit request =>
    val referer = request.headers.get("referer").get

    labelForm.bindFromRequest.fold(
      _ => {
        Future {
          BadRequest("Form error")
        }
      },
      userData => {
        val label = Label(null, userData.label)
        labelDAO.insert(label).map(_ =>
          Redirect(referer)
        )
      })
  }
}
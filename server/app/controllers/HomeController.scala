package controllers

import dao.{ImageDAO, LabelDAO, LabelHasImageDAO}
import javax.inject._
import models.Puzzle
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, imageDAO: ImageDAO, labelDAO: LabelDAO, labelHasImageDAO: LabelHasImageDAO) extends AbstractController(cc) {

  val title = "Ultimate HEIG-VD Manager 2018"
  val numberOfImages = 15

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.ImageController.getImages,
        routes.javascript.HomeController.scorePuzzle
      )
    ).as("text/javascript")
  }

  def result = Action.async {
    val images = labelHasImageDAO.list()
    images map { images =>
      Ok(views.html.resultIndex("Projet Scala - Statistics", images))
    }
  }

  /**
    * Call the "about" html template.
    */
  def about = Action {
    Ok(views.html.about(title))
  }

  implicit val puzzleReads: Reads[Puzzle] = (
    (JsPath \ "clicked").read[Seq[String]] and
      (JsPath \ "notClicked").read[Seq[String]] and
      (JsPath \ "keyword").read[Long]
    ) (Puzzle.apply _)

  def scorePuzzle = Action.async { implicit request =>
    val json: Puzzle = request.body.asJson.get.validate[Puzzle].get
    val keywordId: Long = json.keyword
    val clicked = json.clicked.map(a => a.toLong)
    val notClicked = json.notClicked.map(a => a.toLong)
    val images = imageDAO.findRandom(numberOfImages)
    val label = labelDAO.findRandom

    def returnOk(message: String) =
      for {
        images <- images
        label <- label
      } yield Ok(views.html.index(message, images, label))

    for (id <- clicked) {
      for (image <- imageDAO.findById(id)) {
        image match {
          case Some(img) =>
            img.labelId match {
              case Some(labelId) if labelId != keywordId =>
                returnOk("Vous avez commis des erreurs dans la classification!. Vous pouvez réessayer.")
              case _ => // Do nothing
            }
          case _ => // Do nothing
        }
      }
    }

    for (id <- notClicked) {
      for (image <- imageDAO.findById(id)) {
        image match {
          case Some(img) =>
            img.labelId match {
              case Some(labelId) if labelId == keywordId =>
                returnOk("Vous avez commis des erreurs dans la classification!. Vous pouvez réessayer.")
              case _ => // Do nothing
            }
          case _ => // Do nothing
        }
      }
    }

    for (id <- clicked) {
      for (image <- imageDAO.findById(id)) {
        image match {
          case Some(_) =>
            labelHasImageDAO.addAClick(id, keywordId)
          case _ => // Do nothing
        }
      }
    }

    returnOk("Merci! Vos résultats ont été validés.")
  }
}

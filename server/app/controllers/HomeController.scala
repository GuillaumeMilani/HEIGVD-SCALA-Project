package controllers

import dao.ImageDAO
import dao.LabelHasImageDAO
import javax.inject._
import models.{Image, LabelHasImage}
import dao.{ImageDAO, LabelDAO, LabelHasImageDAO}
import javax.inject._
import models.Image
import models.Puzzle
import play.api.data.Forms._
import play.api.data._
import play.Environment
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global



/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, imageDAO: ImageDAO, labelDAO: LabelDAO, environment: Environment, labelHasImageDAO: LabelHasImageDAO) extends AbstractController(cc) {

  val title = "Ultimate HEIG-VD Manager 2018"

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.ImageController.getImages,
        routes.javascript.HomeController.scorePuzzle
      )
    ).as("text/javascript")
  }

  def index = Action.async {
    val images = imageDAO.list()
    images map { images =>
      Ok(views.html.index("Salut copain", images))
    }
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

  def manageImages = Action.async {
    val imagesFuture = imageDAO.list()
    val labelsFuture = labelDAO.list()
    for {
      images <- imagesFuture
      labels <- labelsFuture
    } yield Ok(views.html.imageManager(images, labels))
  }

  implicit val puzzleReads: Reads[Puzzle] = (
    (JsPath \ "clicked").read[Seq[String]] and
      (JsPath \ "notClicked").read[Seq[String]] and
      (JsPath \ "keyword").read[String]
    ) (Puzzle.apply _)




  def scorePuzzle = Action.async { implicit request =>
    val json: Puzzle = request.body.asJson.get.validate[Puzzle].get
    val keyword = json.keyword;
    val clicked = json.clicked.map(a => a.toLong)
    val notClicked = json.notClicked.map(a => a.toLong)
    val images = imageDAO.list()

    var correct = true; //Check the user made no errors
    for(id <- clicked){
      for(image <- imageDAO.findById(id)){
        if(!image.isEmpty && !image.get.labelId.isEmpty && image.get.labelId.get != keyword){
          correct = false
          //break doesn't exist in scala because fuck you
        }
      }
    }

    if(!correct){
      images map { images =>
        Ok(views.html.index("Vous avez commis des erreurs dans la classification! Aucun point obtenu. Vous pouvez réessayer.", images))
      }
    }

    for(id <- notClicked){
      for(image <- imageDAO.findById(id)){
        if(!image.isEmpty && !image.get.labelId.isEmpty && image.get.labelId.get != keyword){
          correct = false
          //break doesn't exist in scala because fuck you
        }
      }
    }

    if(!correct){
      images map { images =>
        Ok(views.html.index("Vous avez commis des erreurs dans la classification!. Vous pouvez réessayer.", images))
      }
    }

    for(id <- clicked){
      for(image <- imageDAO.findById(id)){
        if(!image.isEmpty){
          labelHasImageDAO.addAClick(id, keyword);
        }
      }
    }
    images map { images =>
      Ok(views.html.index("Merci! Vos résultats ont été validés.", images))
    }
  }

}

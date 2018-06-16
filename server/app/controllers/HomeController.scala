package controllers

import dao.{ImageDAO, LabelDAO}
import javax.inject._
import play.Environment
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter

import scala.concurrent.ExecutionContext.Implicits.global


/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, imageDAO: ImageDAO, labelDAO: LabelDAO, environment: Environment) extends AbstractController(cc) {

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

  def scorePuzzle = Action.async { implicit request =>
    ???
  }

}

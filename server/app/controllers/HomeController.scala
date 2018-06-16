package controllers

import java.nio.file.{Files, Paths}

import dao.ImageDAO
import javax.inject._
import models.Image
import play.Environment
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, imageDAO: ImageDAO, environment: Environment) extends AbstractController(cc) {

  val title = "Ultimate HEIG-VD Manager 2018"

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        //        routes.javascript.StudentsController.getStudents,
        routes.javascript.ImageController.getImages,
        routes.javascript.HomeController.postImage,
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

  // Declare a case class that will be used in the new image's form
  case class ImageRequest(fileName: String)

  // Need to import "play.api.data._" and "play.api.data.Forms._"
  def imageForm = Form(
    mapping(
      "files" -> text,
    )(ImageRequest.apply)(ImageRequest.unapply)
  )

  /**
    * Called when the user try to post a new student from the view.
    * See https://scalaplayschool.wordpress.com/2014/08/14/lesson-4-handling-form-data-with-play-forms/ for more information
    * Be careful: if you have a "Unauthorized" error when accessing this action you have to add a "nocsrf" modifier tag
    * in the routes file above this route (see the routes file of this application for an example).
    */
  def postImage = Action(parse.multipartFormData).async { implicit request =>
    val referer = request.headers.get("referer")

    val futures = request.body.files.map(picture => {
      val filename = Paths.get(picture.filename).getFileName
      val file = environment.getFile("/public/upload")
      if (!file.exists()) {
        Files.createDirectory(file.toPath)
      }
      picture.ref.moveTo(Paths.get(s"${file.getAbsolutePath}/$filename"), replace = true)

      val newImage = Image(null, "upload/" + filename.toString, null)

      imageDAO.insert(newImage)
    })

    Future.sequence(futures).map(_ => Redirect(referer.get))
  }

  /**
    * Call the "about" html template.
    */
  def about = Action {
    Ok(views.html.about(title))
  }

  def welcome = Action.async {
    val images = imageDAO.list()
    images map { images =>
      Ok(views.html.welcomeIndex("aldo", images))
    }
  }

  def scorePuzzle = Action.async { implicit request =>
    ???
  }

}

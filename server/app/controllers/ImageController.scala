package controllers

import java.nio.file.{Files, Paths}

import dao.{ImageDAO, LabelDAO}
import javax.inject.{Inject, Singleton}
import models.Image
import play.Environment
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

@Singleton
class ImageController @Inject()(cc: ControllerComponents, environment: Environment, imageDAO: ImageDAO, labelDAO: LabelDAO) extends AbstractController(cc) {

  // Convert a image-model object into a JsValue representation, which means that we serialize it into JSON.
  implicit val imageToJson: Writes[Image] = (
    (JsPath \ "id").write[Option[Long]] and
      (JsPath \ "fileName").write[String] and
      (JsPath \ "labelId").write[Option[Long]]
    // Use the default 'unapply' method (which acts like a reverted constructor) of the Student case class if order to get
    // back the Student object's arguments and pass them to the JsValue.
    ) (unlift(Image.unapply))

  // Convert a JsValue representation into a Student-model object, which means that we deserialize the JSON.
  implicit val jsonToImage: Reads[Image] = (
    // In order to be valid, the student must have first and last names that are 2 characters long at least, as well as
    // an age that is greater than 0.
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "fileName").read[String](minLength[String](2)) and
      (JsPath \ "labelId").readNullable[Long]
    // Use the default 'apply' method (which acts like a constructor) of the Student case class with the JsValue in order
    // to construct a Student object from it.
    ) (Image.apply _)

  /**
    * This helper parses and validates JSON using the implicit `jsonToStudent` above, returning errors if the parsed
    * json fails validation.
    */
  def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  /**
    * Allows a user to manage images and labels.
    */
  def manageImages = Action.async {
    val imagesFuture = imageDAO.list()
    val labelsFuture = labelDAO.list()
    for {
      images <- imagesFuture
      labels <- labelsFuture
    } yield Ok(views.html.imageManager(images, labels))
  }

  /**
    * Redirects to the puzzle page. If there are no labels in the DB, a puzzle
    * cannot be made and the user is sent to the image manager page instead.
    */
  def getPuzzle = Action.async {
    val futureImages = imageDAO.findRandom(15)
    val futureLabel = labelDAO.findRandom

    for {
      images <- futureImages
      label <- futureLabel
    } yield if(label.isDefined) Ok(views.html.index("Salut copain", images, label.get)) else Ok(views.html.imageManager(images, Nil))
  }

  def getImages = Action.async {
    val imagesList = imageDAO.list()
    imagesList map (i => Ok(Json.toJson(i)))
  }

  /**
    * Allows the user to post images to the server. Images are stored in the 'public'
    * folder and their path is added to the database.
    * @return
    */
  def postImages = Action(parse.multipartFormData).async { implicit request =>
    val referer = request.headers.get("referer")

    val labelString = request.body.asFormUrlEncoded("label").head
    val labelId = labelString.toLong match {
      case x if x > -1 => Some(x)
      case _ => None
    }

    val futures = request.body.files
      .filter(file => file.contentType.getOrElse("").startsWith("image/"))
      .map(picture => {
        val filename = Paths.get(picture.filename).getFileName
        val uploadFolder = environment.getFile("/public/upload")
        if (!uploadFolder.exists()) {
          Files.createDirectory(uploadFolder.toPath)
        }
        picture.ref.moveTo(Paths.get(s"${uploadFolder.getAbsolutePath}/$filename"), replace = true)

        imageDAO.insert(Image(None, "upload/" + filename.toString, labelId))

      })
    Future.sequence(futures).map(_ => Redirect(referer.get))
  }

  def createImage = Action.async(validateJson[Image]) { implicit request =>
    // `request.body` contains a fully validated `Student` instance, since it has been validated by the `validateJson`
    // helper above.
    val image = request.body
    val createdImage = imageDAO.insert(image)

    createdImage.map(i =>
      Ok(
        Json.obj(
          "status" -> "OK",
          "id" -> i.id,
          "message" -> ("Image '" + i.fileName + "' saved.")
        )
      )
    )
  }

  /**
    * Get the image identified by the given ID, then return it as JSON.
    */
  def getImage(imageId: Long) = Action.async {
    val optionalImage = imageDAO.findById(imageId)

    optionalImage.map {
      case Some(s) => Ok(Json.toJson(s))
      case None =>
        // Send back a 404 Not Found HTTP status to the client if the student does not exist.
        NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("Image #" + imageId + " not found.")
        ))
    }
  }

  /**
    * Tries to update image based on ID.
    */
  def updateImage(imageId: Long) = Action.async(validateJson[Image]) { request =>
    val newImage = request.body

    // Try to edit the student, then return a 200 OK HTTP status to the client if everything worked.
    imageDAO.update(imageId, newImage).map {
      case 1 => Ok(
        Json.obj(
          "status" -> "OK",
          "message" -> ("Image '" + newImage.fileName + "' updated.")
        )
      )
      case 0 => NotFound(Json.obj(
        "status" -> "Not Found",
        "message" -> ("Image #" + imageId + " not found.")
      ))
    }
  }

  /**
    * Try to delete the student image by the given ID, and sends back a JSON response.
    */
  def deleteImage(imageId: Long) = Action.async {
    imageDAO.delete(imageId).map {
      case 1 => Ok(
        Json.obj(
          "status" -> "OK",
          "message" -> ("Image #" + imageId + " deleted.")
        )
      )
      case 0 => NotFound(Json.obj(
        "status" -> "Not Found",
        "message" -> ("Image #" + imageId + " not found.")
      ))
    }
  }

}
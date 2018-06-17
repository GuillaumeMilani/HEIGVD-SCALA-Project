package dao

import javax.inject.{Inject, Singleton}
import models.Image
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

// We use a trait component here in order to share the StudentsTable class with other DAO, thanks to the inheritance.
trait ImageComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's students table in a object-oriented entity: the Student model.
  class ImageTable(tag: Tag) extends Table[Image](tag, "image") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def filename = column[String]("filename")

    def labelId = column[Long]("label_id")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, filename, labelId.?) <> (Image.tupled, Image.unapply)
  }

}

@Singleton
class ImageDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends ImageComponent with LabelComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  // Get the object-oriented list of images directly from the query table.
  val images = TableQuery[ImageTable]
  val labels = TableQuery[LabelTable]

  /** Retrieve the list of images */
  def list(): Future[Seq[Image]] = {
    val query = images.sortBy(i => i.id)
    db.run(query.result)
  }

  def findRandom(number: Int): Future[Seq[Image]] = {
    val rand = SimpleFunction.nullary[Double]("rand")
    db.run(images.sortBy(_ => rand).take(number).result)
  }

  /** Retrieve a image from the id. */
  def findById(id: Long): Future[Option[Image]] =
    db.run(images.filter(_.id === id).result.headOption)

  def findByLabel(labelId: Long) =
    db.run(images.filter(_.id === labelId).result)

  /** Insert a new image, then return it. */
  def insert(image: Image): Future[Image] = {
    val insertQuery = images returning images.map(_.id) into ((image, id) => image.copy(Some(id)))
    db.run(insertQuery += image)
  }

  /** Update a image, then return an integer that indicate if the image was found (1) or not (0). */
  def update(id: Long, image: Image): Future[Int] = {
    val imageToUpdate: Image = image.copy(Some(id))
    db.run(images.filter(_.id === id).update(imageToUpdate))
  }

  /** Delete a image, then return an integer that indicate if the image was found (1) or not (0). */
  def delete(id: Long): Future[Int] =
    db.run(images.filter(_.id === id).delete)
}

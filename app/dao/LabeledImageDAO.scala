package dao

import javax.inject.{Inject, Singleton}
import models.{LabeledImage}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

// We use a trait component here in order to share the StudentsTable class with other DAO, thanks to the inheritance.
trait LabeledImageComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's students table in a object-oriented entity: the Student model.
  class LabeledImageTable(tag: Tag) extends Table[LabeledImage](tag, "labeledImage") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def labelId = column[Long]("labelId")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, labelId) <> (LabeledImage.tupled, LabeledImage.unapply)
  }

}

@Singleton
class LabeledImageDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends LabeledImageComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  // Get the object-oriented list of labeledImages directly from the query table.
  val labeledImages = TableQuery[LabeledImageTable]

  /** Retrieve the list of students */
  def list(): Future[Seq[LabeledImage]] = {
    val query = labeledImages.sortBy(i => i.id)
    db.run(query.result)
  }

  /** Retrieve a labeledImage from the id. */
  def findById(id: Long): Future[Option[LabeledImage]] =
    db.run(labeledImages.filter(_.id === id).result.headOption)

  /** Insert a new labeledImage, then return it. */
  def insert(labeledImage: LabeledImage): Future[LabeledImage] = {
    val insertQuery = labeledImages returning labeledImages.map(_.id) into ((labeledImage, id) => labeledImage.copy(Some(id)))
    db.run(insertQuery += labeledImage)
  }

  /** Update a labeledImage, then return an integer that indicate if the labeledImage was found (1) or not (0). */
  def update(id: Long, labeledImage: LabeledImage): Future[Int] = {
    val labeledImageToUpdate: LabeledImage = labeledImage.copy(Some(id))
    db.run(labeledImages.filter(_.id === id).update(labeledImageToUpdate))
  }

  /** Delete a labeledImage, then return an integer that indicate if the labeledImage was found (1) or not (0). */
  def delete(id: Long): Future[Int] =
    db.run(labeledImages.filter(_.id === id).delete)
}

package dao

import javax.inject.{Inject, Singleton}
import models.LabelHasImage
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

// We use a trait component here in order to share the StudentsTable class with other DAO, thanks to the inheritance.
trait LabelHasImageComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's labels table in a object-oriented entity: the Student model.
  class LabelHasImageTable(tag: Tag) extends Table[LabelHasImage](tag, "image_has_label") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def labelId = column[Long]("label_id")

    def imageId = column[Long]("image_id")

    def clicks = column[Long]("clicks")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, labelId, imageId, clicks) <> (LabelHasImage.tupled, LabelHasImage.unapply)
  }
}

@Singleton
class LabelHasImageDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, labelDAO: LabelDAO)(implicit executionContext: ExecutionContext) extends LabelHasImageComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  // Get the object-oriented list of label has images directly from the query table.
  val labelHasImages = TableQuery[LabelHasImageTable]

  /** Retrieve the list of labels */
  def list(): Future[Seq[LabelHasImage]] = {
    val query = labelHasImages.sortBy(l => l.id)
    db.run(query.result)
  }

  /** Retrieve a label has image from the id. */
  def findById(id: Long): Future[Option[LabelHasImage]] =
    db.run(labelHasImages.filter(_.id === id).result.headOption)

  /** Insert a new label, then return it. */
  def insert(labelHasImage: LabelHasImage): Future[LabelHasImage] = {
    val insertQuery = labelHasImages returning labelHasImages.map(_.id) into ((labelHasImage, id) => labelHasImage.copy(Some(id)))
    db.run(insertQuery += labelHasImage)
  }

  /** Update a label, then return an integer that indicate if the label was found (1) or not (0). */
  def update(id: Long, labelHasImage: LabelHasImage): Future[Int] = {
    val labelHasImageToUpdate: LabelHasImage = labelHasImage.copy(Some(id))
    db.run(labelHasImages.filter(_.id === id).update(labelHasImageToUpdate))
  }

  /** Delete a label, then return an integer that indicate if the label was found (1) or not (0). */
  def delete(id: Long): Future[Int] =
    db.run(labelHasImages.filter(_.id === id).delete)

  def addAClick(imageId: Long, keyword: String): Unit ={
    ??? //TODO finish this method, add a click using get then update
  }
//    for(label <- labelDAO.getIdFromKeyword(keyword)){
//      if(!label.isEmpty){
//        val id = label.get.id
//        val labelHasImage = labelHasImages.filter(a => a.imageId == imageId && a.labelId == id).result.headOption
//        for(lhi <- labelHasImage){
//          if(!lhi.isEmpty)
//          val updatedValue = lhi.copy(Some(id))
//          db.run(labelHasImages.filter(_.id === id).update(updatedValue))
//
//        }
//      }
//    }
//  }

}

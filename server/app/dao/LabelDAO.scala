package dao

import javax.inject.{Inject, Singleton}
import models.Label
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

// We use a trait component here in order to share the StudentsTable class with other DAO, thanks to the inheritance.
trait LabelComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's labels table in a object-oriented entity: the Student model.
  class LabelTable(tag: Tag) extends Table[Label](tag, "label") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def label = column[String]("label")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, label) <> (Label.tupled, Label.unapply)
  }

}

@Singleton
class LabelDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends LabelComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  // Get the object-oriented list of labels directly from the query table.
  val labels = TableQuery[LabelTable]

  /** Retrieve the list of labels */
  def list(): Future[Seq[Label]] = {
    val query = labels.sortBy(l => l.label)
    db.run(query.result)
  }

  /** Retrieve a label from the id. */
  def findById(id: Long): Future[Option[Label]] =
    db.run(labels.filter(_.id === id).result.headOption)

  /** Insert a new label, then return it. */
  def insert(label: Label): Future[Label] = {
    val insertQuery = labels returning labels.map(_.id) into ((label, id) => label.copy(Some(id)))
    db.run(insertQuery += label)
  }

  /** Update a label, then return an integer that indicate if the label was found (1) or not (0). */
  def update(id: Long, label: Label): Future[Int] = {
    val labelToUpdate: Label = label.copy(Some(id))
    db.run(labels.filter(_.id === id).update(labelToUpdate))
  }

  /** Delete a label, then return an integer that indicate if the label was found (1) or not (0). */
  def delete(id: Long): Future[Int] =
    db.run(labels.filter(_.id === id).delete)

  def randomLabel(): Future[Option[Label]] = {
    val rand = SimpleFunction.nullary[Double]("rand")
    val query = labels.sortBy(_ => rand)
    db.run(query.result.headOption)
  }
}

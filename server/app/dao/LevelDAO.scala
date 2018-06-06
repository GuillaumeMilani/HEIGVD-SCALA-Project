package dao

import javax.inject.{Inject, Singleton}
import models.Level
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

// We use a trait component here in order to share the StudentsTable class with other DAO, thanks to the inheritance.
trait LevelComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's levels table in a object-oriented entity: the Student model.
  class LevelTable(tag: Tag) extends Table[Level](tag, "level") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def name = column[String]("name")

    def nextLevelId = column[Long]("next_level")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, name, nextLevelId.?) <> (Level.tupled, Level.unapply)
  }

}

@Singleton
class LevelDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends LevelComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  // Get the object-oriented list of levels directly from the query table.
  val levels = TableQuery[LevelTable]

  /** Retrieve the list of levels */
  def list(): Future[Seq[Level]] = {
    val query = levels.sortBy(l => l.name)
    db.run(query.result)
  }

  /** Retrieve a level from the id. */
  def findById(id: Long): Future[Option[Level]] =
    db.run(levels.filter(_.id === id).result.headOption)

  /** Insert a new level, then return it. */
  def insert(level: Level): Future[Level] = {
    val insertQuery = levels returning levels.map(_.id) into ((level, id) => level.copy(Some(id)))
    db.run(insertQuery += level)
  }

  /** Update a level, then return an integer that indicate if the level was found (1) or not (0). */
  def update(id: Long, level: Level): Future[Int] = {
    val levelToUpdate: Level = level.copy(Some(id))
    db.run(levels.filter(_.id === id).update(levelToUpdate))
  }

  /** Delete a level, then return an integer that indicate if the level was found (1) or not (0). */
  def delete(id: Long): Future[Int] =
    db.run(levels.filter(_.id === id).delete)
}

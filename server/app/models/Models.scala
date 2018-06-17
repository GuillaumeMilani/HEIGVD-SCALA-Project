package models

case class Level(id: Option[Long], name: String, nextLevelId: Option[Long])

case class User(id: Option[Long], login: String, password: String, score: Int, levelId: Option[Long])

case class Label(id: Option[Long], label: String)

case class LabelHasImage(id: Option[Long], labelId: Long, imageId: Long, clicks: Long)

case class Image(id: Option[Long], fileName: String, labelId: Option[Long])

case class Puzzle(clicked: Seq[String], notClicked: Seq[String], keyword: Long)

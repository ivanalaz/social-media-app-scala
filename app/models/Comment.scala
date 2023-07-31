package models

import play.api.libs.json.{Json, OWrites}

import java.sql.Timestamp

case class Comment(id: Long, userId: Long, text: String, added: Timestamp, postId: Long)

object Comment {
  implicit val commentWrites: OWrites[Comment] = Json.writes[Comment]
}
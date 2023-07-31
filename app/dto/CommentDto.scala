package dto

import play.api.libs.json.{Json, OWrites, Reads}

import java.time.LocalDateTime

case class CommentDto(text: String)

object CommentDto {
  implicit val commentReads: Reads[CommentDto] = Json.reads[CommentDto]
  implicit val commentWrites: OWrites[CommentDto] = Json.writes[CommentDto]
}

case class CommentDisplayDto(firstName: String, lastName: String, commentId: Long, text: String,
                             added: LocalDateTime, image: String, userId: Long)

object CommentDisplayDto {
  implicit val commentWrites: OWrites[CommentDisplayDto] = Json.writes[CommentDisplayDto]
}

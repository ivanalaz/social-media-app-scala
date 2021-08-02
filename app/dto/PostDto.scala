package dto

import play.api.libs.json.{Json, OWrites, Reads}

import java.time.LocalDateTime

case class PostDto(content: String, userId: Long)

object PostDto {
  implicit val postReads: Reads[PostDto] = Json.reads[PostDto]
  implicit val postWrites: OWrites[PostDto] = Json.writes[PostDto]
}

case class PostDisplayDto(firstName: String, lastName: String, created: LocalDateTime, content: String, image: String, postId: Long, userId: Long)

object PostDisplayDto {
  implicit val postWrites: OWrites[PostDisplayDto] = Json.writes[PostDisplayDto]
}

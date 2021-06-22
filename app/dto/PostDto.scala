package dto

import play.api.libs.json.{Json, OWrites, Reads}

import java.time.LocalDateTime

case class PostDto(content: String, created: LocalDateTime, userId: Long)

object PostDto {
  implicit val postReads: Reads[PostDto] = Json.reads[PostDto]
  implicit val postWrites: OWrites[PostDto] = Json.writes[PostDto]
}

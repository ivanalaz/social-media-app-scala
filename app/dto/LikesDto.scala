package dto

import play.api.libs.json.{Json, OWrites}

case class LikesDto(userId: Long)

object LikesDto {
  implicit val likeWrites: OWrites[PostDto] = Json.writes[PostDto]
  implicit val userReads = Json.reads[LikesDto]
}

package dto

import play.api.libs.json.{Json, Reads}

case class FriendshipDto(userId: Long)

object FriendshipDto {
  implicit val friendReads: Reads[FriendshipDto] = Json.reads[FriendshipDto]
}

package dto

import play.api.libs.json.{Json, OFormat, Reads, Writes}

import java.time.LocalDate

case class UserDto(firstName: String, lastName: String, username: String, password: String, birthday: LocalDate)

object UserDto {
  implicit val userFormat: OFormat[UserDto] = Json.format[UserDto]
  implicit val userReads: Reads[UserDto] = Json.reads[UserDto]
}

case class LoginDto(username: String, password: String)

object LoginDto {
  implicit val loginReads: Reads[LoginDto] = Json.reads[LoginDto]
  implicit val LoginWrites: Writes[(LoginDto, String)] = Json.writes[(LoginDto, String)]
  implicit val LogWrites: Writes[LoginDto] = Json.writes[LoginDto]
}
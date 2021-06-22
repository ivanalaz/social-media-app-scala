package dto

import play.api.libs.json.{Json, OFormat, Reads}

import java.time.LocalDate

case class UserDto(firstName: String, lastName: String, username: String, password: String, birthday: LocalDate)

object UserDto {
  implicit val userFormat: OFormat[UserDto] = Json.format[UserDto]
  implicit val userReads: Reads[UserDto] = Json.reads[UserDto]
  // implicit val userWrites = Json.writes[User]
  /*implicit val validReads: Reads[UserDto] = (
    (JsPath \ "firstName").read[String](minLength[String](2)) and
      (JsPath \ "lastName").read[String](minLength[String](2)) and
      (JsPath \ "username").read[String](minLength[String](2)) and
      (JsPath \ "password").read[String](minLength[String](2)) and
      (JsPath \ "birthday").read[LocalDate]
    )(UserDto.apply _)*/
}

case class LoginDto(username: String, password: String)

object LoginDto {
  implicit val loginReads: Reads[LoginDto] = Json.reads[LoginDto]
}
package controllers

import auth.{AuthAction, AuthService}
import dto.{LoginDto, UserDto}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.UserService

import java.nio.file.Paths
import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class UserController @Inject()(cc: ControllerComponents, userService: UserService,
                               authService: AuthService, authAction: AuthAction) extends AbstractController(cc) {

  def login() = Action(parse.json).async { implicit request =>
    val login = request.body.validate[LoginDto]
    login.fold(
      errors => {
        Future(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      u => {
        userService.login(u).map {
          case Some(user) =>
            val token = authService.encode(user)
            Ok(s"Token: $token")
          case None => BadRequest("Wrong credentials")
        }
      }
    )
  }

  def addUser() = Action(parse.json) { implicit request =>
    val userResult = request.body.validate[UserDto]
    userResult.fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      user => {
        userService.addUser(user)
        Ok(Json.obj("message" -> "User added."))
      }
    )
  }

  def updateUser() = authAction(parse.json) { implicit request =>
    val userRes = request.body.validate[UserDto]
    userRes.fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      user => {
        val us = userService.updateUser(request.user.id, user)
        Ok(Json.obj("message" -> "User updated"))
      }
    )
  }

  def getPersonalInfo() = authAction.async { implicit request =>
    userService.getPersonalInfo(request.user.id).map { user =>
      Ok(Json.toJson(user))
    }
  }

  def allUsers() = Action.async { implicit request =>
    userService.getAllUsers().map { users =>
      Ok(Json.toJson(users))
    }
  }

  def searchUsers(name: String) = authAction.async { implicit request =>
    userService.searchUsers(name).map { user =>
      Ok(Json.toJson(user))
    }
  }

  def addFriend(friendId: Long) = authAction { implicit request =>
    userService.addFriend(friendId, request.user.id)
    Ok(Json.obj("message" -> "Friendship added."))
  }

  def acceptFriend(senderId: Long) = authAction { implicit request =>
    userService.acceptFriendship(senderId, request.user.id)
    Ok(Json.obj("message" -> "Friendship accepted."))
  }

  def declineFriendship(senderId: Long) = authAction { implicit request =>
    userService.declineFriendship(senderId, request.user.id)
    Ok(Json.obj("message" -> "Friendship declined."))
  }

  def listFriends(id: Long) = authAction.async { implicit request =>
    userService.listFriends(id).map(friends =>
      Ok(Json.toJson(friends)))
  }

  def upload(userId: Long) = authAction(parse.multipartFormData) { implicit request =>
    request.body
      .file("picture")
      .map { picture =>
        val imageName = Paths.get(picture.filename).getFileName
        userService.addImage(userId, imageName.toString)
        picture.ref.moveTo(Paths.get(s"./public/images/$imageName"), replace = true)
        Ok("File uploaded")
      }
      .getOrElse {
        BadRequest("Something's wrong")
      }
  }
}

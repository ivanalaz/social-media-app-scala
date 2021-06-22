package controllers

import auth.{AuthAction, AuthService, UserRequest}
import dto.{FriendshipDto, LoginDto, UserDto}
import models.User
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsError, JsValue, Json, Reads}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents}
import services.UserService

import java.nio.file.Paths
import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class UserController @Inject() (cc: ControllerComponents, userService: UserService,
                                authService: AuthService, authAction: AuthAction) extends AbstractController(cc) {

  def login() = Action(parse.json) { implicit request =>
    val user = request.body.validate[LoginDto]
    user.fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      u => {
        userService.login(u)
        val token = authService.encode(u)
        Ok("Token: " + token)
      }
    )
  }

  def addUser() = Action(parse.json) { implicit request =>
    /*UserForm.formReg.bindFromRequest().fold(
      errorForm => {
        Future.successful(Ok(views.html.index(errorForm)))
      },
      user => {
        val newUser = User(0, user.firstName, user.lastName, user.username, user.password, user.birthday)
        userService.addUser(newUser).map( _ =>
          Redirect(routes.HomeController.index()))
      }
    )*/
    //  val data = request.body.asJson.getOrElse(Json.obj()).as[User]//(userReads)
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

  def updateUser(id: Long) = authAction(parse.json) { implicit request =>
    val userRes = request.body.validate[UserDto]
    userRes.fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      user => {
        userService.updateUser(id, user)
        Ok(Json.obj("message" -> "User updated"))
      }
    )
  }

  def getPersonalInfo(userId: Long) = authAction.async { implicit request =>
    userService.getPersonalInfo(userId).map { user =>
      Ok(Json.toJson(user))
    }
  }

  def searchUsers(name: String) = authAction.async { implicit request =>
    userService.searchUsers(name).map { user =>
      Ok(Json.toJson(user))
    }
  }

  def addFriend(friendId: Long) = authAction(parse.json) { implicit request =>
    val friend = request.body.validate[FriendshipDto]
    friend.fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      user => {
        userService.addFriend(friendId, user)
        Ok(Json.obj("message" -> "Friendship added."))
      }
    )
  }

  def acceptFriend(senderId: Long) = authAction(parse.json) { implicit request =>
    val user = request.body.validate[FriendshipDto]
    user.fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      u => {
        userService.acceptFriendship(senderId, u)
        Ok(Json.obj("message" -> "Friendship accepted."))
      }
    )
  }

  def listFriends(id: Long) = authAction.async { implicit request =>
    userService.listFriends(id).map( friends =>
      Ok(Json.toJson(friends)))
  }

  def declineFriendship(senderId: Long) = authAction(parse.json) { implicit request =>
    val user = request.body.validate[FriendshipDto]
    user.fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      u => {
        userService.declineFriendship(senderId, u)
        Ok(Json.obj("message" -> "Friendship declined."))
      }
    )
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
        //Redirect(routes.HomeController.index()).flashing("error" -> "Missing file")
        BadRequest("Something's wrong")
      }
  }
}

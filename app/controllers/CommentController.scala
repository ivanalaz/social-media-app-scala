package controllers

import auth.AuthAction
import dto.CommentDto
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.CommentService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class CommentController @Inject() (cc: ControllerComponents, commentService: CommentService, authAction: AuthAction) extends AbstractController(cc) {

  def addComment(postId: Long) = authAction(parse.json) { implicit request =>
    val commentResult = request.body.validate[CommentDto]
    commentResult.fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      comment => {
        commentService.addComment(request.user.id, comment, postId)
        Ok(Json.obj("message" -> "Comment added."))
      }
    )
  }

  def getCommentsOnPost(postId: Long) = Action.async { implicit request =>
    commentService.getCommentsOnPost(postId).map { comment =>
      Ok(Json.toJson(comment))
    }
  }

  def deleteComment(commentId: Long) = authAction { implicit request =>
    commentService.deleteComment(commentId)
    Ok(Json.obj("message" -> "Comment deleted."))
  }

}

package controllers

import auth.AuthAction
import dto.{LikesDto, PostDto}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{LikesService, PostService}

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class PostController @Inject() (cc: ControllerComponents, postService: PostService, likesService: LikesService,  authAction: AuthAction) extends AbstractController(cc) {

  def addPost() = Action(parse.json) { request =>
    val postResult = request.body.validate[PostDto]
    postResult.fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      post => {
        postService.addPost(post, post.userId)
        Ok(Json.obj("message" -> "Post added."))
      }
    )
  }

  def editPost(postId: Long) = authAction(parse.json) { request =>
    val postRes = request.body.validate[PostDto]
    postRes.fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      post => {
        postService.editPost(postId, post)
        Ok(Json.obj("message" -> "Post edited."))
      }
    )
  }

  def like(postId: Long) = authAction(parse.json) { implicit request =>
   val user = request.body.validate[LikesDto]
    user.fold(
      _ => BadRequest(Json.obj("message" -> "")),
      u => {
        likesService.addLike(postId,u)
        Ok(Json.obj("message" -> "Like added"))
      }
    )
  }

  def dislike(postId: Long) = authAction(parse.json) { implicit request =>
    val user = request.body.validate[LikesDto]
    user.fold(
      _ => BadRequest(Json.obj("message" -> "")),
      u => {
        likesService.dislike(postId,u)
        Ok(Json.obj("message" -> "Disliked"))
      }
    )
  }

  def listPostsByUser(userId: Long) = Action.async { implicit request =>
    postService.listPostsByUser(userId).map { posts =>
      Ok(Json.toJson(posts))
    }
  }

  def listPostsByFriends(userId: Long) = authAction.async { request =>
    postService.listPostsByFriends(userId).map( posts =>
      Ok(Json.toJson(posts)))
  }

  def allPosts() = Action.async {implicit request =>
    postService.allPosts().map { posts =>
      Ok(Json.toJson(posts))
    }
  }
}

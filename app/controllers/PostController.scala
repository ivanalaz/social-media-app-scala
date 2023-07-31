package controllers

import auth.AuthAction
import dto.PostDto
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{LikesService, PostService}

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class PostController @Inject() (cc: ControllerComponents, postService: PostService, likesService: LikesService,  authAction: AuthAction) extends AbstractController(cc) {

  def addPost() = authAction(parse.json) { implicit request =>
    val postResult = request.body.validate[PostDto]
    postResult.fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      post => {
        postService.addPost(post, request.user.id)
        Ok(Json.obj("message" -> "Post added."))
      }
    )
  }

  def editPost(postId: Long) = authAction(parse.json) { implicit request =>
    val postRes = request.body.validate[PostDto]
    postRes.fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      post => {
        postService.editPost(postId, request.user.id, post)
        Ok(Json.obj("message" -> "Post edited."))
      }
    )
  }

  def deletePost(postId: Long) = authAction { implicit request =>
    postService.deletePost(postId)
    Ok(Json.obj("message" -> "Post deleted."))
  }

  def like(postId: Long) = authAction { implicit request =>
    likesService.addLike(postId, request.user.id)
    Ok(Json.obj("message" -> "Like added"))
  }

  def dislike(postId: Long) = authAction { implicit request =>
    likesService.dislike(postId, request.user.id)
    Ok(Json.obj("message" -> "Disliked"))
  }

  def likedPostsByUser() = authAction.async { implicit request =>
    likesService.likedPostsByUser(request.user.id).map(posts =>
      Ok(Json.toJson(posts)))
  }

  def numberOfLikes(postId: Long) = Action.async { implicit request =>
    likesService.numberOfLikes(postId).map(number =>
    Ok(Json.toJson(number)))
  }

  def listPostsByUser(userId: Long) = authAction.async { implicit request =>
    postService.listPostsByUser(userId).map { posts =>
      Ok(Json.toJson(posts))
    }
  }

  def listPostsByFriends() = authAction.async { request =>
    postService.listPostsByFriends(request.user.id).map( posts =>
      Ok(Json.toJson(posts)))
  }

  def allPosts() = Action.async {implicit request =>
    postService.allPosts().map { posts =>
      Ok(Json.toJson(posts))
    }
  }
}

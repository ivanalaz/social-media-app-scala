package controllers

import auth.AuthAction
import dto.{LikesDto, PostDto}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{LikesService, PostService}

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

@Singleton
class PostController @Inject() (cc: ControllerComponents, postService: PostService, likesService: LikesService,  authAction: AuthAction) extends AbstractController(cc) {

  def addPost() = authAction(parse.json) { request =>
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
/*
    likesService.addLike(LikesDto(postId, userId)) map { like =>
      Ok(Json.toJson(like))
    }*/

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

 // implicit val postWrites: OWrites[Post] = Json.writes[Post]
  def listPostsByUser(userId: Long) = authAction.async { implicit request =>
    postService.listPostsByUser(userId).map { posts =>
      Ok(Json.toJson(posts))
    }
  }

  def listPostsByFriends(userId: Long) = authAction.async { request =>
    postService.listPostsByFriends(userId).map( posts =>
      Ok(Json.toJson(posts)))
  }

}

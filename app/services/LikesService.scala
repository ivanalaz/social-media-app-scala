package services

//import dto.LikesDto
import models.{Like, LikeRepository}

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LikesService @Inject() (likes: LikeRepository) {

  def addLike(postId: Long, userId: Long): Future[String] = {
    likes.addLike(Like(postId, userId))
  }

  def dislike(postId: Long, userId: Long) = {
    likes.dislike(postId, userId)
  }

  def likedPostsByUser(userId: Long) = {
    likes.likedPostsByUser(userId).map(posts => posts.map(post => post.postId))
  }

  def numberOfLikes(postId: Long) = {
    likes.numberOfLikes(postId)
  }
}

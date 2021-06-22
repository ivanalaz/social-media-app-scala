package services

import dto.LikesDto
import models.{Like, Likes}

import javax.inject.Inject
import scala.concurrent.Future

class LikesService @Inject() (likes: Likes) {

  def addLike(postId: Long, likesDto: LikesDto): Future[String] = {
    likes.addLike(Like(postId, likesDto.userId))
  }

  def dislike(postId: Long, likesDto: LikesDto) = {
    likes.dislike(postId, likesDto.userId)
  }
}

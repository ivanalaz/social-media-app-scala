package services

import dto.PostDto
import models.{Post, Posts}

import java.sql.Timestamp
import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PostService @Inject() (posts: Posts) {

  def addPost(postDto: PostDto, userId: Long): Future[String] = {
    val post = Post(0, postDto.content, Timestamp.valueOf(LocalDateTime.now()), userId)
    posts.add(post)
  }

  def editPost(postId: Long, postDto: PostDto): Future[Int] = {
    val post = Post(postId, postDto.content, Timestamp.valueOf(LocalDateTime.now()), postDto.userId)
    posts.editPost(postId, post)
  }

  def listPostsByUser(userId: Long): Future[Seq[PostDto]] = {
    posts.listPostsByUser(userId).map(_.map(post => PostDto(post.content, post.created.toLocalDateTime, post.userId)))
  }

  def listPostsByFriends(userId: Long): Future[Seq[(Long, (String, String), LocalDateTime, String)]] = {
    posts.postsByFriends(userId).map(_.map(item => item.copy(_3 = item._3.toLocalDateTime)))
  }
}

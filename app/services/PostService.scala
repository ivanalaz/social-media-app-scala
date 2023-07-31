package services

import dto.{PostDisplayDto, PostDto}
import models.{Post, PostRepository}

import java.sql.Timestamp
import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PostService @Inject() (posts: PostRepository) {

  def addPost(postDto: PostDto, userId: Long): Future[String] = {
    val post = Post(0, postDto.content, Timestamp.valueOf(LocalDateTime.now()), userId)
    posts.add(post)
  }

  def editPost(postId: Long, userId: Long, postDto: PostDto): Future[Int] = {
    val post = Post(postId, postDto.content, Timestamp.valueOf(LocalDateTime.now()), userId)
    posts.editPost(postId, post)
  }

  def deletePost(postId: Long): Future[Int] = {
    posts.deletePost(postId)
  }

  def listPostsByUser(userId: Long): Future[Seq[PostDisplayDto]] = {
    posts.listPostsByUser(userId).map(_.map(post => PostDisplayDto(post._1, post._2, post._3.toLocalDateTime, post._4, post._5, post._6, post._7)))
   // posts.listPostsByUser(userId).map(_.map(post => post.copy(_6 = post._6.toLocalDateTime)))
  }

  def listPostsByFriends(userId: Long): Future[Seq[PostDisplayDto]] = {
    posts.postsByFriends(userId).map(_.map(post => PostDisplayDto(post._1, post._2, post._3.toLocalDateTime, post._4, post._5, post._6, post._7)))
  }

  def allPosts(): Future[Seq[PostDisplayDto]] = {
    posts.allPosts().map(_.map(post => PostDisplayDto(post._1, post._2, post._3.toLocalDateTime, post._4, post._5, post._6, post._7)))
   // posts.allPosts().map(_.map(post => post.copy(_3 = post._3.toLocalDateTime)))
  }
}

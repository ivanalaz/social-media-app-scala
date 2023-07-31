package services

import dto.{CommentDisplayDto, CommentDto}
import models.Comment
import repositories.CommentRepository

import java.sql.Timestamp
import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CommentService @Inject() (comments: CommentRepository) {

  def addComment(userId: Long, commentDto: CommentDto, postId: Long): Future[String] = {
    val comment = Comment(0, userId, commentDto.text, Timestamp.valueOf(LocalDateTime.now()), postId)
    comments.add(comment)
  }

  def getCommentsOnPost(postId: Long): Future[Seq[CommentDisplayDto]] = {
    comments.getPostComments(postId).map(_.map(comment => CommentDisplayDto(comment._1, comment._2, comment._3, comment._4,
      comment._5.toLocalDateTime, comment._6, comment._7)))
  }

  def deleteComment(commentId: Long): Future[Int] = {
    comments.deleteComment(commentId)
  }
}

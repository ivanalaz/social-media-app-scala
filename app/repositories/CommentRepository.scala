package repositories

import models.{Comment, UserTable}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.dbio.Effect
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag
import slick.sql.FixedSqlAction
import slick.sql.SqlProfile.ColumnOption.SqlType

import java.sql.Timestamp
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CommentRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  class CommentTable(tag: Tag) extends Table[Comment](tag, "comment") {
    def commentId = column[Long]("commentID", O.PrimaryKey, O.AutoInc)
    def userId = column[Long]("userID")
    def text = column[String]("text")
    def added = column[Timestamp]("added", SqlType("timestamp not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP"))
    def postId = column[Long]("postID")
    override def * = (commentId, userId, text, added, postId) <> ((Comment.apply _).tupled, Comment.unapply)
  }

  private val comments = TableQuery[CommentTable]
  private val users = TableQuery[UserTable]

  def add(comment: Comment): Future[String] = db.run {
    (comments += comment).map(_ => "Comment successfully added.")
  }

  def getPostComments(postId: Long): Future[Seq[(String, String, Long, String, Timestamp, String, Long)]] = db.run {
    //comments.filter(_.postId === postId).sortBy(_.added.desc).result
    val commentsPost = for {
      c <- comments if c.postId === postId
      u <- users if c.userId === u.userId
    } yield (u.firstName, u.lastName, c.commentId, c.text, c.added, u.imageName, u.userId)
    commentsPost/*.sortBy(_._5.desc)*/.result
  }

  def deleteComment(commentId: Long): Future[Int] = db.run {
    comments.filter(_.commentId === commentId).delete
  }
}

package models

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class Like(postId: Long, userId: Long)

class LikesTable(tag: Tag) extends Table[Like](tag, "liked_posts") {
  def postId = column[Long]("postID", O.PrimaryKey)
  def userId = column[Long]("userID", O.PrimaryKey)
  override def * = (postId, userId) <> (Like.tupled, Like.unapply)
}

class Likes @Inject() (val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  val likes = TableQuery[LikesTable]

  def addLike(like: Like): Future[String] = db.run {
    (likes += like).map(_ => "Like added.")
  }

  def dislike(postId: Long, userId: Long): Future[Int] = db.run {
    likes.filter(_.postId === postId).filter(_.userId === userId).delete
  }
}
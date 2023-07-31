package models

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{Json, OWrites}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class Like(postId: Long, userId: Long)

object Like {
  implicit val likeWrites: OWrites[Like] = Json.writes[Like]
}

class LikesTable(tag: Tag) extends Table[Like](tag, "liked_posts") {
  def postId = column[Long]("postID", O.PrimaryKey)
  def userId = column[Long]("userID", O.PrimaryKey)
  override def * = (postId, userId) <> ((Like.apply _).tupled, Like.unapply)
}

class LikeRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  val likes = TableQuery[LikesTable]

  def addLike(like: Like): Future[String] = db.run {
    (likes += like).map(_ => "Like added.")
  }

  def dislike(postId: Long, userId: Long): Future[Int] = db.run {
    likes.filter(_.postId === postId).filter(_.userId === userId).delete
  }

  def likedPostsByUser(userId: Long): Future[Seq[Like]] = db.run {
    likes.filter(_.userId === userId).result
  }

  def numberOfLikes(postId: Long): Future[Int] = db.run {
    likes.filter(_.postId === postId).length.result
  }
}
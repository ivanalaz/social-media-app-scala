package models

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{Json, OFormat, OWrites, Reads, Writes}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag
import slick.sql.SqlProfile.ColumnOption.SqlType

import java.sql.Timestamp
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class Post(id: Long, content: String, created: Timestamp, userId: Long)

object Post {
  implicit val timestampReads: Reads[Timestamp] = {
    implicitly[Reads[Long]].map(new Timestamp(_))
  }

  implicit val timestampWrites: Writes[Timestamp] = {
    implicitly[Writes[Long]].contramap(_.getTime)
  }
  implicit val postFormat: OFormat[Post] = Json.format[Post]
  implicit val postWrites: OWrites[Post] = Json.writes[Post]

}

class PostTable(tag: Tag) extends Table[Post](tag, "post") {
  def postId = column[Long]("postID", O.PrimaryKey, O.AutoInc)
  def content = column[String]("content")
  def created = column[Timestamp]("created", SqlType("timestamp not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP"))
  def userId = column[Long]("author")
  override def * = (postId, content, created, userId) <> ((Post.apply _).tupled, Post.unapply)
}

class Posts @Inject() (val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  val posts = TableQuery[PostTable]
  val friends = TableQuery[FriendshipTable]
  val users = TableQuery[UserTable]

  def add(post: Post): Future[String] = db.run {
    (posts += post).map(_ => "Post successfully added")
  }

  def editPost(postId: Long, post: Post): Future[Int] = db.run {
    posts.filter(_.postId === postId).update(post)
  }

  def getPost(postId: Long): Future[Option[Post]] = db.run {
    posts.filter(_.postId === postId).result.headOption
  }

  def allPosts() = db.run {
    val allPosts = for {
      p <- posts
      author <- users if p.userId === author.userId
    } yield (author.firstName, author.lastName, p.created, p.content, author.imageName, p.postId, author.userId)
    allPosts.sortBy(_._3.desc).result
  }

  def listPostsByUser(userId: Long): Future[Seq[(Long, String, String, Timestamp, String)]] = db.run {
    val userPosts = for {
      p <- posts if p.userId === userId
      author <- users if p.userId === author.userId
    } yield (p.postId, author.firstName, author.lastName, p.created, p.content)
    userPosts.sortBy(_._3.desc).result
  }

  def postsByFriends(userId: Long): Future[Seq[(Long, (String, String), Timestamp, String)]] = db.run {
    val friendsPosts = for {
      f <- friends
      r <- users if f.receiverId === r.userId
      s <- users if f.senderId === s.userId
      if f.confirmed === 1
      if r.userId === userId || s.userId === userId
      p <- posts
      if p.userId =!= userId && (p.userId === r.userId || p.userId === s.userId)
      author <- users
      if p.userId === author.userId
    } yield
      (p.userId, (author.firstName, author.lastName), p.created, p.content)
    friendsPosts.sortBy(_._3.desc).result
  }
}

package models

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{Json, OWrites}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class Friendship(senderId: Long, receiverId: Long, confirmed: Int)

object Friendship {
  implicit val friendWrites: OWrites[Friendship] = Json.writes[Friendship]
}

class FriendshipTable(tag: Tag) extends Table[Friendship](tag, "friends") {
  def senderId = column[Long]("userID", O.PrimaryKey)
  def receiverId = column[Long]("friendID", O.PrimaryKey)
  def confirmed = column[Int]("confirmed")
  override def * = (senderId, receiverId, confirmed) <> ((Friendship.apply _).tupled, Friendship.unapply)
}

class Friendships @Inject() (val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  val friends = TableQuery[FriendshipTable]
  val users = TableQuery[UserTable]

  def addFriend(friendship: Friendship): Future[String] = db.run {
    (friends += friendship).map(_ => "Friend added.")
  }

  def acceptFriend(senderId: Long, receiverId: Long, status: Int): Future[Int] = db.run {
    friends.filter(f => f.senderId === senderId && f.receiverId === receiverId).map(_.confirmed).update(status)
  }

  def declineFriend(senderId: Long, receiverId: Long): Future[Int] = db.run {
    friends.filter(_.senderId === senderId).filter(_.receiverId === receiverId).delete
  }

  def listFriends(userId: Long): Future[Seq[(Int, (Long, String, String, String), (Long, String, String, String))]] = db.run {
    val detailedFriendships = for {
      f <- friends
      r <- users if f.receiverId === r.userId
      s <- users if f.senderId === s.userId
      if f.confirmed === 1
      if r.userId === userId || s.userId === userId
    } yield (
      f.confirmed,
      (s.userId, s.firstName, s.lastName, s.username),
      (r.userId, r.firstName, r.lastName, r.username)
    )
    detailedFriendships.result
  }
}

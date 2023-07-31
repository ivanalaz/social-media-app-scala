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

class FriendshipRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
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

  def removeFriend(senderId: Long, receiverId: Long): Future[Int] = db.run {
    friends.filter(f => f.senderId === senderId || f.receiverId === senderId)
      .filter(f => f.senderId === receiverId || f.receiverId === receiverId).delete
  }

  def listFriends(userId: Long) = db.run {
    sql"""SELECT u.userId, u.first_name, u.last_name, u.image
          FROM user u
          WHERE userID <> $userId
          AND EXISTS(
              SELECT 1
              FROM friends f
              WHERE ((f.userID = $userId AND f.friendID = u.userID)
              OR (f.friendID = $userId AND f.userID = u.userID))
              AND confirmed = 1
              )""".as[(Long, String, String, String)]
  }

  def addedFriends(userId: Long) = db.run {
    sql"""SELECT u.userId, u.first_name, u.last_name, u.image
          FROM user u
          WHERE userID <> $userId
          AND EXISTS(
              SELECT 1
              FROM friends f
              WHERE (f.userID = $userId AND f.friendID = u.userID)
              AND confirmed = 0
              )""".as[(Long, String, String, String)]
  }

  def friendRequests(userId: Long) = db.run {
    sql"""SELECT u.userId, u.first_name, u.last_name, u.image
          FROM user u
          WHERE userID <> $userId
          AND EXISTS(
              SELECT 1
              FROM friends f
              WHERE (f.friendID = $userId AND f.userID = u.userID)
              AND confirmed = 0
              )""".as[(Long, String, String, String)]
  }

  def notFriends(userId: Long) = db.run {
    sql"""SELECT u2.userID, u2.first_name, u2.last_name, u2.image
          FROM user u1, user u2
          WHERE NOT EXISTS(SELECT 1
                 FROM friends f
                 WHERE f.userID = u1.userID AND
                       f.friendID = u2.userID)
          AND NOT EXISTS(SELECT 1
                 FROM friends f
                 WHERE f.userID = u2.userID AND
                       f.friendID = u1.userID)
          AND u1.userID != u2.userID
          AND u1.userID = $userId""".as[(Long, String, String, String)]
  }
}

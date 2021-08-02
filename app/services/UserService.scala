package services

import dto.{LoginDto, UserDto}
import models.{Friendship, Friendships, User, Users}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.Future

class UserService @Inject()(users: Users, friends: Friendships) {

  def login(loginDto: LoginDto): Future[Option[User]] = {
    users.login(loginDto.username, loginDto.password)
  }

  def addUser(userDto: UserDto): Future[String] = {
    val user = User(0, userDto.firstName, userDto.lastName, userDto.username, userDto.password, LocalDate.parse("2020-01-08"))
    users.add(user)
  }

  def updateUser(id: Long, userDto: UserDto): Future[Int] = {
    val user = User(id, userDto.firstName, userDto.lastName, userDto.username, userDto.password, /*userDto.birthday*/ LocalDate.parse("2020-01-08"))
    users.update(id, user)
  }

  def getPersonalInfo(id: Long): Future[Option[User]] = {
    users.getUser(id)
  }

  def getAllUsers(): Future[Seq[User]] = {
    users.getAllUsers()
  }

  def searchUsers(name: String): Future[Seq[User]] = {
    users.searchUsers(name)
  }

  def listFriends(userId: Long): Future[Seq[(Int, (Long, String, String, String), (Long, String, String, String))]] = {
    friends.listFriends(userId)
  }

  def addFriend(friendId: Long, userId: Long): Future[String] = {
    friends.addFriend(Friendship(userId, friendId, 0))
  }

  def acceptFriendship(senderId: Long, userId: Long): Future[Int] = {
    friends.acceptFriend(senderId, userId, 1)
  }

  def declineFriendship(senderId: Long, userId: Long): Future[Int] = {
    friends.declineFriend(senderId, userId)
  }

  def addImage(userId: Long, imageName: String): Future[Int] = {
    users.addImage(userId, imageName)
  }
}

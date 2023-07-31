package services

import dto.{LoginDto, UserDto}
import models.{Friendship, FriendshipRepository, User, UserRepository}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.Future

class UserService @Inject()(users: UserRepository, friends: FriendshipRepository) {

  def login(loginDto: LoginDto): Future[Option[User]] = {
    users.login(loginDto.username, loginDto.password)
  }

  def addUser(userDto: UserDto): Future[String] = {
    val user = User(0, userDto.firstName, userDto.lastName, userDto.username, userDto.password, userDto.birthday/*LocalDate.parse("2020-01-08")*/)
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

  def listFriends(userId: Long) = {
    friends.listFriends(userId)
  }

  def addedFriends(userId: Long) = {
    friends.addedFriends(userId)
  }

  def friendRequests(userId: Long) = {
    friends.friendRequests(userId)
  }

  def listNotFriends(userId: Long) = {
    friends.notFriends(userId)
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

  def removeFriend(friendId: Long, userId: Long) = {
    friends.removeFriend(friendId, userId)
  }

  def addImage(userId: Long, imageName: String): Future[Int] = {
    users.addImage(userId, imageName)
  }
}

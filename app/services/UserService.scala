package services

import dto.{FriendshipDto, LoginDto, UserDto}
import models.{Friendship, Friendships, User, Users}

import javax.inject.Inject
import scala.concurrent.Future

class UserService @Inject() (users: Users, friends: Friendships) {

  def login(loginDto: LoginDto) = {
    users.login(loginDto.username, loginDto.password)
  }

  def addUser(userDto: UserDto): Future[String] = {
    val user = User(0, userDto.firstName, userDto.lastName, userDto.username, userDto.password, userDto.birthday)
    users.add(user)
  }

  def updateUser(id: Long, userDto: UserDto): Future[Int] = {
    val user = User(id, userDto.firstName, userDto.lastName, userDto.username, userDto.password, userDto.birthday)
    users.update(id, user)
  }

  def getPersonalInfo(id: Long): Future[Option[User]] = {
    users.getUser(id)
  }

  def searchUsers(name: String): Future[Seq[User]] = {
    users.searchUsers(name)
  }

  def listFriends(userId: Long): Future[Seq[(Int, (Long, String, String, String), (Long, String, String, String))]] = {
    friends.listFriends(userId)
  }

  def addFriend(friendId: Long, friendsDto: FriendshipDto): Future[String] = {
    friends.addFriend(Friendship(friendsDto.userId, friendId, 0))
  }

  def acceptFriendship(senderId: Long, friendshipDto: FriendshipDto): Future[Int] = {
    friends.acceptFriend(senderId, friendshipDto.userId, 1)
  }

  def declineFriendship(senderId: Long, friendshipDto: FriendshipDto): Future[Int] = {
    friends.declineFriend(senderId, friendshipDto.userId)
  }

  def addImage(userId: Long, imageName: String): Future[Int] = {
    users.addImage(userId, imageName)
  }
}

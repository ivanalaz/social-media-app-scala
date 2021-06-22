package models

import dto.UserDto
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.mapping
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{Json, OWrites, Reads}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._
import slick.lifted.{TableQuery, Tag}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class User(id: Long, firstName: String, lastName: String, username: String,
                password: String, birthday: LocalDate, imageName: String = "default-profile-image.png")

object User {
  implicit val userWrites: OWrites[User] = Json.writes[User]
  implicit val userReads: Reads[User] = Json.reads[User]
}

object UserForm {
  val formReg: Form[UserDto] = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "birthday" -> localDate
    )(UserDto.apply)(UserDto.unapply)
  )
  val formLogin: Form[UserDataLogin] = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserDataLogin.apply)(UserDataLogin.unapply)
  )
}

// login
case class UserDataLogin(username: String, password: String)

class UserTable(tag: Tag) extends Table[User](tag, "user") {
  def userId = column[Long]("userID", O.PrimaryKey, O.AutoInc)
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def username = column[String]("username")
  def password = column[String]("password")
  def birthday = column[LocalDate]("birthday")
  def imageName = column[String]("image")
  override def * =  (userId, firstName, lastName, username, password, birthday, imageName) <> ((User.apply _).tupled, User.unapply)
}

class Users @Inject() (val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  val users = TableQuery[UserTable]

  def login(username: String, password: String): Future[Option[User]] = db.run {
    users.filter(u => u.username === username && u.password === password).result.headOption
  }

  def add(user: User): Future[String] = db.run {
    (users += user).map(_ => "User successfully added")
  }

  def update(id: Long, user: User): Future[Int] = db.run {
    users.filter(_.userId === id).update(user)
  }

  def getUser(id: Long): Future[Option[User]] = db.run {
    users.filter(_.userId === id).result.headOption
  }

  def searchUsers(name: String): Future[Seq[User]] = db.run {
    users.filter(u => u.firstName.toLowerCase.like(s"%${name.toLowerCase}%") ||
      u.lastName.toLowerCase.like(s"${name.toLowerCase}%")).result
  }

  def addImage(id: Long, imageName: String): Future[Int] = db.run {
    users.filter(u => u.userId === id).map(_.imageName).update(imageName)
  }
}
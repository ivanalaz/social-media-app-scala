package repositories

import models.{FriendshipTable, UserTable}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class FriendshipRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  val friends = TableQuery[FriendshipTable]
  val users = TableQuery[UserTable]
}

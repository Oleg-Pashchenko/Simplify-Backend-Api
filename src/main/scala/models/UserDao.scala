package models


import akka.actor.TypedActor.dispatcher
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._
class UserDao(val db: Database) {

  val delimiter = ","

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def user_id = column[String]("user_id", O.PrimaryKey)
    def login = column[String]("login")
    def password = column[String]("password")
    def name = column[String]("name")
    def surname = column[String]("surname")
    def friends = column[String]("friends")
    def * = (user_id, login, password, name, surname, friends) <> (User.tupled, User.unapply)
      }
  val users = TableQuery[Users]

  def createTableIfNotExist(): Future[Unit] = {
    val users = TableQuery[Users]
    val createTable = users.schema.createIfNotExists
    val action = createTable.transactionally
    db.run(action)

  }

  def create(user: User): Future[Unit] = {
    createTableIfNotExist().flatMap { _ =>
      val query = users += user
      val action = query.map(_ => ())
      db.run(action)
    }
  }

  def update(user: User): Future[Int] = {
    val query = users.filter(_.user_id === user.user_id).update(user)
    db.run(query)
  }

  def addFriend(user_id: String, friend_id: String): Future[Int] = {
    val friendsFuture = db.run(users.filter(_.user_id === user_id).map(_.friends).result.head)
    friendsFuture.flatMap { friends =>
      val newFriends = friends + "," + friend_id
      val query = users.filter(_.user_id === user_id).map(_.friends).update(newFriends)
      db.run(query)
    }
  }


  def getFriends(user_id: String): Future[List[User]] = {
    val query = for {
      user <- users
      if user.friends.like(s"%$user_id%")
    } yield user
    db.run(query.result).map(_.toList)
  }
}
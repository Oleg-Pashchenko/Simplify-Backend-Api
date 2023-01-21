import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import controllers.UserRoutes
import models.UserDao
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main extends App {
  implicit val system = ActorSystem()
  // Instantiate the db object
  val db = Database.forConfig("database")

  val userDao = new UserDao(db)
  userDao.createTableIfNotExist().flatMap { _ =>
    val userRoutes = new UserRoutes(userDao)
    Http().bindAndHandle(userRoutes.routes, "localhost", 8080)
  }.onComplete {
    case Success(_) => println("Server started on port 8080")
    case Failure(ex) => println(s"Error starting server: ${ex.getMessage}")
  }
}

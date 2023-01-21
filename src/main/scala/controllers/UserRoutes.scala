package controllers


import akka.actor.TypedActor.dispatcher
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import models.{User, UserDao}
import spray.json.DefaultJsonProtocol._


class UserRoutes(val userDao: UserDao) {
  implicit val userFormat = jsonFormat6(User)

  val routes = pathPrefix("users") {
    path("registration") {
      post {
        complete("6 digit unique code")
      }
    } ~
      path("update-profile-info") {
        post {
          entity(as[User]) { user =>
            complete {
              userDao.update(user).map { _ => "Success" }
            }
          }
        }
      } ~
      path("add-friend") {
        post {
          parameters("user_id", "friend_id") { (user_id, friend_id) =>
            complete {
              userDao.addFriend(user_id, friend_id).map { _ => "Success" }
            }
          }
        }
      } ~
      path("get_friends") {
        post {
          parameter("user_id") { user_id =>
            complete {
              userDao.getFriends(user_id)
            }
          }
        }
      }
  }
}
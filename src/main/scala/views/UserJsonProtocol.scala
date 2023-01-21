package views

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import models.User
import spray.json.DefaultJsonProtocol

object UserJsonProtocol extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat6(User)
}

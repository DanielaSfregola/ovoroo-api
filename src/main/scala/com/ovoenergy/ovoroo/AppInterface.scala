package com.ovoenergy.ovoroo

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

import com.typesafe.config.ConfigFactory
import spray.routing._
import spray.routing.authentication.BasicAuth

import com.ovoenergy.ovoroo.resources.UserResource

class AppInterface(implicit val executionContext: ExecutionContext) extends HttpServiceActor with CORSSupport with UserResource {

  val config = ConfigFactory.load()

  private val directory = config.getString("app.directory")

  def receive = runRoute(routes)

  val routes: Route =
    dynamic {
      path("") {
        get {
          authenticate(BasicAuth(ldapAuthenticator _, realm = "Use for OVO windows credentials")) { ldapUser =>
            //respondWithMediaType(MediaTypes.`text/html`) {
              getFromFile(s"$directory/index.html")
            }
          //}
        }
      } ~
      get { requestContext =>
        //respondWithMediaType(MediaTypes.`text/plain`) {
          println(s"$directory${requestContext.unmatchedPath}")
          getFromFile(s"$directory${requestContext.unmatchedPath}")
        //}
      }
    }

}

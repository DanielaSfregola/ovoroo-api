package com.ovoenergy.ovoroo

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

import spray.routing._
import spray.routing.authentication.BasicAuth

import com.ovoenergy.ovoroo.resources.UserResource
import com.ovoenergy.ovoroo.services.OrderService

class AppInterface(implicit val executionContext: ExecutionContext) extends HttpServiceActor with CORSSupport with UserResource {

  def receive = runRoute(routes)

  val orderService = new OrderService

  val routes: Route =
    authenticate(BasicAuth(ldapAuthenticator _, realm = "Use for OVO windows credentials")) { ldapUser =>
        pathEnd { complete(204, None) }
    }

}
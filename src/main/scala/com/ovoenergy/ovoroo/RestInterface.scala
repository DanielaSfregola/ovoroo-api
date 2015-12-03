package com.ovoenergy.ovoroo

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

import spray.http.HttpHeaders.RawHeader
import spray.routing._

import com.ovoenergy.ovoroo.resources.{OrderResource, PingResource}
import com.ovoenergy.ovoroo.services.OrderService

class RestInterface(implicit val executionContext: ExecutionContext) extends HttpServiceActor with Resources {

  def receive = runRoute(routes)

  val orderService = new OrderService

  val routes: Route =
    respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
      pingRoutes ~
      //authenticate(???) { authenticated =>
      questionRoutes
      //}
    }

}

trait Resources extends OrderResource with PingResource

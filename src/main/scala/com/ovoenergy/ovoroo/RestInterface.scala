package com.ovoenergy.ovoroo

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

import spray.routing._

import com.ovoenergy.ovoroo.resources.{OrderResource, PingResource}
import com.ovoenergy.ovoroo.services.OrderService

class RestInterface(implicit val executionContext: ExecutionContext) extends HttpServiceActor with Resources {

  def receive = runRoute(routes)

  val orderService = new OrderService

  val routes: Route =
    pingRoutes ~
    //authenticate(???) { authenticated =>
      questionRoutes
    //}

}

trait Resources extends OrderResource with PingResource

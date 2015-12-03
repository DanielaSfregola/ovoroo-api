package com.ovoenergy.ovoroo

import spray.routing._

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

import com.ovoenergy.ovoroo.resources.{PingResource, OrderResource}
import com.ovoenergy.ovoroo.services.OrderService

class RestInterface(implicit val executionContext: ExecutionContext) extends HttpServiceActor with Resources {

  def receive = runRoute(routes)

  val orderService = new OrderService

  val routes: Route = pingRoutes ~ questionRoutes

}

trait Resources extends OrderResource with PingResource

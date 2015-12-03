package com.ovoenergy.ovoroo.resources

import spray.routing._

import com.ovoenergy.ovoroo.entities.{Item, Order, User}
import com.ovoenergy.ovoroo.routing.MyHttpService
import com.ovoenergy.ovoroo.services.OrderService

trait OrderResource extends MyHttpService {

  val orderService: OrderService

  def questionRoutes: Route = pathPrefix("orders") {
      pathEnd {
        createOrder ~
        findActiveOrderPerLocation
      } ~
      pathPrefix(Segment) { id =>
        getOrder(id) ~
        assignOrder(id) ~
        addItemToOrder(id)
      }
    }

  private def createOrder =
    pathEnd {
      post {
        entity(as[Order]) { order =>
          complete(orderService.createOrder(order))
        }
      }
    }

  private def findActiveOrderPerLocation =
    get {
      parameters('location) { location =>
        complete(orderService.getActiveOrderPerLocation(location))
      }
    }

  private def getOrder(id: String) =
    get {
      complete(orderService.getOrder(id))
    }

  private def assignOrder(id: String) =
    path("owner") {
      put {
        entity(as[User]) { owner =>
          complete(orderService.addOwnerToOrder(id, owner))
        }
      }
    }

  private def addItemToOrder(id: String) =
    path("items") {
      put {
        entity(as[Item]) { item =>
          complete(orderService.addItemToOrder(id, item))
        }
      }
    }
}

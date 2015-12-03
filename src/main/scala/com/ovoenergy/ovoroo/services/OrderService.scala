package com.ovoenergy.ovoroo.services

import scala.concurrent.{ExecutionContext, Future}

import com.ovoenergy.ovoroo.entities.{User, Item, Order}

class OrderService(implicit val executionContext: ExecutionContext) {

  var orders = Vector.empty[Order]

  def createOrder(order: Order): Future[Option[Order]] = Future {
    orders.find(_.id == order.id) match {
      case Some(q) => None // Conflict! id is already taken
      case None =>
        orders = orders :+ order
        Some(order)
    }
  }

  def getOrder(id: String): Future[Option[Order]] = Future {
    orders.find(_.id == id)
  }

  def getActiveOrderPerLocation(location: String): Future[Option[Order]] = Future {
    orders.find(o => o.location == location && o.isActive)
  }

  def addOwnerToOrder(id: String, owner: User): Future[Option[Order]] =
    getOrder(id).map { maybeOrder =>
      maybeOrder.filter(_.owner.isEmpty).map { order =>
        deleteOrder(id)
        val updatedOrder = order.copy(owner = Some(owner))
        createOrder(updatedOrder)
        updatedOrder
    }
  }

  def addItemToOrder(id: String, item: Item): Future[Option[Order]] =
    getOrder(id).map { maybeOrder =>
      maybeOrder.filter(o => o.capacity > o.products.size).map { order =>
        deleteOrder(id)
        val updatedOrder = order.copy(products = order.products :+ item)
        createOrder(updatedOrder)
        updatedOrder
    }
  }

  private def deleteOrder(id: String): Unit = orders = orders.filterNot(_.id == id)

}


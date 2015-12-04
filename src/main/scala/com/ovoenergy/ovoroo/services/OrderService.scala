package com.ovoenergy.ovoroo.services

import scala.concurrent.{ExecutionContext, Future}

import com.typesafe.config.ConfigFactory

import com.ovoenergy.ovoroo.entities.{Item, Order, User}

class OrderService(implicit val executionContext: ExecutionContext) {

  var orders = Vector.empty[Order]

  val slackClient = new SlackClient

  val channel = {
    val config = ConfigFactory.load()
    config.getString("slack.channel")
  }

  def createOrder(order: Order): Future[Option[Order]] = Future {
    orders.find(_.id == order.id) match {
      case Some(q) => None // Conflict! id is already taken
      case None =>
        orders = orders :+ order
        order.owner match {
          case Some(o) => slackClient.sendMsgOrderCreated(order, channel)
          case None => slackClient.sendMsgOrderCreatedNoOwner(order, channel)
        }
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

  def monitorOrders() = Future {
    orders.map { order =>
      if (!order.isActive) {

        val requesters = order.owner.map { ownr =>
          order.products.filter(_.requester != ownr).map(_.requester)
        }.getOrElse(Seq.empty)

        if (requesters.isEmpty) {
          slackClient.sendMsgOwnerNoOrders(order)
        } else {
          slackClient.sendMsgOrderClosed(order, channel)
          slackClient.sendMsgToOwner(order)
          requesters.map(slackClient.sendMsgToRequester(order, _))
        }
        deleteOrder(order.id)
      }
    }
    Thread.sleep(60000) // 1 minute -- HORRIBLE!!!
  }


}


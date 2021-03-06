package com.ovoenergy.ovoroo.entities

import com.typesafe.config.ConfigFactory

case class OrderCreated(order: Order) {
  override def toString = {
    val url = ConfigFactory.load().getString("app.url")
    s"Coffee time! ${order.owner.get} will be doing a coffee run in ${order.duration} minutes. " +
    s"Fancy a drink delivered to your desk? Select your fave here. $url "
  }
}

case class OrderCreatedNoOwner(order: Order) {
  override def toString = {
    val url = ConfigFactory.load().getString("app.url")
    s"Coffee time! ${order.products.head.requester.name} would love a coffee in the next ${order.duration} minutes. " +
    s"Fancy a FREE coffee and an happy coworker? Click here. $url "
  }
}

case class OrderClosed(order: Order) {
  override def toString = {
    val requesters = order.owner.map { ownr =>
      order.products.filter(_.requester != ownr).map(_.requester)
    }.getOrElse(Seq.empty)
    s"That’s all folks! ${order.owner.get} will be buying coffee(s) " +
    s"for ${requesters.mkString(",")}."
  }
}

case class OrderClosedNoOrders(order: Order) {
  override def toString = {
    "Your Coffee Run session has ended. No takers unfortunately. " +
    "Get your own cup of coffee or try again later."
  }
}

case class OrderClosedPay(order: Order, user: User) {
  override def toString = {

    def calculatePricePerRequester(order: Order, requester: User): Double = {
      val ownerItems = order.products.filter(_.requester == order.owner.get)
      val requesterItems = order.products.filter(_.requester == requester)
      val requestersItemsSize = order.products.filterNot(_.requester == order.owner.get).size
      val ownerPriceContribution = ownerItems.map(_.price).sum / requestersItemsSize * requesterItems.size
      val requesterPrice = requesterItems.map(_.price).sum
      requesterPrice + ownerPriceContribution
    }

    val totPrice = calculatePricePerRequester(order, user)
    s"Hi ${user.name}, \nAren’t you lucky, your coffee will be delivered to your desk by ${order.owner.get.name}. " +
    s"Remember that you owe them £$totPrice . \nEnjoy!"
  }
}

case class OrderClosedOwner(order: Order) {
  override def toString = {
    val users = order.products.map(_.requester).toSet

    def calculatePricePerRequester(order: Order, requester: User): String = {
      val ownerItems = order.products.filter(_.requester == order.owner.get)
      val requesterItems = order.products.filter(_.requester == requester)
      val requestersItemsSize = order.products.filterNot(_.requester == order.owner.get).size
      val ownerPriceContribution = ownerItems.map(_.price).sum / requestersItemsSize * requesterItems.size
      val requesterPrice = requesterItems.map(_.price).sum
      val totPrice = requesterPrice + ownerPriceContribution
      val items = requesterItems.toString.mkString(", ")
      s"${requester.name} - $items - $totPrice"
    }

    s"Hi ${order.owner.get}, \nYour Coffee Run session has ended. " +
    s"Aren’t you lucky, you’ve had ${users.size -1} other taker(s).\nHere’s a summary of what you need to get: " +
    s"\n[name of user 2] – Selection with any extras – price including contribution for free coffee\n[name of user 3] – Selection with any extras – price including contribution for free coffee\nTotal for order £ \nRemember that you’re supposed to buy & deliver them asap. \nEnjoy your free coffee!"
  }
}



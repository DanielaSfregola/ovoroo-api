package com.ovoenergy.ovoroo.entities

import scala.util.Random

import org.joda.time.DateTime

case class Order(id: String = Random.alphanumeric.take(6).mkString,
                 owner: Option[User] = None,
                 location: String,
                 capacity: Int = 4,
                 duration: Int = 10, // in minutes
                 products: Seq[Item],
                 creationDate: DateTime = DateTime.now) {
  def isActive = creationDate.plusMinutes(duration).isAfterNow
}

case class User(name: String, email: String)

case class Item(name: String, size: Size, extras: Seq[Size] = Seq.empty, requester: User)

case class Size(name: String, price: Double)

package com.ovoenergy.ovoroo.services

import scala.concurrent.{ExecutionContext, Future}
import akka.actor.ActorSystem

import com.typesafe.config.ConfigFactory
import slack.api.SlackApiClient
import slack.rtm.SlackRtmClient

import com.ovoenergy.ovoroo.entities._

class SlackClient(implicit val ec: ExecutionContext) {

  implicit val system = ActorSystem("slack")

  val token = {
    val config = ConfigFactory.load()
    config.getString("slack.token")
  }

  val client = SlackApiClient(token)
  val rtmClient = SlackRtmClient(token)

  def findSlackIdByEmail(email: String): Future[Option[String]] =
    client.listUsers().map { users =>
      val maybeUser = users.find{ user =>
        val userEmail = user.profile.flatMap(_.email).getOrElse("")
        userEmail == email
      }
      maybeUser.map(_.id)
    }

  private def sendDM(userId: String, message: String) =
    rtmClient.sendMessage(channelId = userId, message)


  private def postMessage(channel: String, message:String) =  {
    val channelId = rtmClient.state.getChannelIdForName(channel).get
    rtmClient.sendMessage(channelId, message)
  }

  def sendMsgOrderCreated(order: Order, channel: String) = {
    val msg = OrderCreated(order).toString
    postMessage(channel, msg)
  }

  def sendMsgOrderCreatedNoOwner(order: Order, channel: String) = {
    val msg = OrderCreated(order).toString
    postMessage(channel, msg)
  }

  def sendMsgOrderClosed(order: Order, channel: String) = {
    val msg = OrderClosed(order).toString
    postMessage(channel, msg)
  }

  def sendMsgToRequester(order: Order, requester: User) = {
    val msg = OrderClosedPay(order, requester).toString
    sendDM(requester.slackId, msg)
  }

  def sendMsgToOwner(order: Order) = {
    val msg = OrderClosedNoOrders(order).toString // TODO - change
    order.owner.map { user =>
      sendDM(user.slackId, msg)
    }
  }

  def sendMsgOwnerNoOrders(order: Order) = {
    val msg = OrderClosedNoOrders(order).toString
    order.owner.map { user =>
      sendDM(user.slackId, msg)
    }
  }


}

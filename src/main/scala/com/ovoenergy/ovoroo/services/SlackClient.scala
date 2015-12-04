package com.ovoenergy.ovoroo.services

import scala.concurrent.{Future, ExecutionContext}

import com.typesafe.config.ConfigFactory
import slack.api.SlackApiClient

class SlackClient(implicit val ec: ExecutionContext) {

  private val config = ConfigFactory.load()

  val client = {
    val token = config.getString("slack.token")
    SlackApiClient(token)
  }

  def findSlackIdByEmail(email: String): Future[Option[String]] =
    client.listUsers().map { users =>
      println(users)
      val maybeUser = users.find{ user =>
        val userEmail = user.profile.flatMap(_.email).getOrElse("")
        userEmail == email
      }
      maybeUser.map(_.id)
    }
}

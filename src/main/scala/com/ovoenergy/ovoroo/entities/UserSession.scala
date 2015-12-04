package com.ovoenergy.ovoroo.entities

import scala.util.Random

case class UserSession(token: String = Random.alphanumeric.take(8).mkString, user: User)

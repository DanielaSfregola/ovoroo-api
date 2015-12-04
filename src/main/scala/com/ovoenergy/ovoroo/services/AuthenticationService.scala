package com.ovoenergy.ovoroo.services

import scala.concurrent.{ExecutionContext, Future}

import com.ovoenergy.ovoroo.entities.{User, UserSession}

class AuthenticationService(implicit val executionContext: ExecutionContext) {

  var userSessions = Vector.empty[UserSession]

  def getOrCreateUserSession(user: User): Future[UserSession] = {
    findSessionByUser(user).map { maybeSession =>
      maybeSession match {
        case Some(session) => session
        case None => createSessionForUser(user)
      }
    }
  }

  private def findSessionByUser(user: User): Future[Option[UserSession]] = Future {
    userSessions.find(_.user == user)
  }

  private def createSessionForUser(user: User): UserSession = {
    val userSession = UserSession(user = user)
    userSessions = userSessions :+ userSession
    userSession
  }

  def findSessionByToken(token: String): Future[Option[UserSession]] = Future {
    userSessions.find(_.token == token)
  }
}


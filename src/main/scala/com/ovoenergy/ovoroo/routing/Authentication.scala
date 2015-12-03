package com.ovoenergy.ovoroo.routing

import scala.concurrent.Future

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import spray.routing.authentication._

import scala.concurrent.ExecutionContext.Implicits.global

trait Authentication {


  def ldapAuthenticator(userPass: Option[UserPass]): Future[Option[String]] =
    Future {
      userPass.map { credentials =>
        val token = new UsernamePasswordAuthenticationToken(credentials.user, credentials.pass)
        //new AuthenticationProvider().authenticate(token)
      }
      if (userPass.exists(up => up.user == "John" && up.pass == "p4ssw0rd")) Some("John")
      else None
    }
}

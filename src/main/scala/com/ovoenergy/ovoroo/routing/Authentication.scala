package com.ovoenergy.ovoroo.routing

import scala.concurrent.{ExecutionContext, Future}

import com.typesafe.config.ConfigFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider
import org.springframework.security.ldap.userdetails.LdapUserDetails
import spray.routing.authentication.UserPass

trait Authentication {

  implicit def executionContext: ExecutionContext

  private val config = ConfigFactory.load()

  private val provider = {
    val domain = config.getString("ldap.domain")
    val url = config.getString("ldap.url")
    new ActiveDirectoryLdapAuthenticationProvider(domain, url)
  }

  def ldapAuthenticator(userPass: Option[UserPass]): Future[Option[LdapUserDetails]] =
    Future {
      userPass.flatMap { credentials =>
        val token = new UsernamePasswordAuthenticationToken(credentials.user, credentials.pass)
        try {
          Some(provider.authenticate(token).getPrincipal.asInstanceOf[LdapUserDetails])
        } catch {
          case ex: Exception => None
        }
      }
    }
}



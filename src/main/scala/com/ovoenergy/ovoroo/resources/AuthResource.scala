package com.ovoenergy.ovoroo.resources

import scala.concurrent.Future

import com.typesafe.config.ConfigFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider
import org.springframework.security.ldap.userdetails.LdapUserDetails
import spray.routing._

import com.ovoenergy.ovoroo.entities.{LdapCredentials, User}
import com.ovoenergy.ovoroo.routing.MyHttpService
import com.ovoenergy.ovoroo.services.{SlackClient, AuthenticationService}

trait AuthResource extends MyHttpService {

  private val config = ConfigFactory.load()

  val authenticationService: AuthenticationService

  private val slackClient = new SlackClient()

  private val provider = {
    val domain = config.getString("ldap.domain")
    val url = config.getString("ldap.url")
    new ActiveDirectoryLdapAuthenticationProvider(domain, url)
  }

  def authRoutes: Route = path("auth") {
      post {
        entity(as[LdapCredentials]) { credentials =>
          val ldapUser = authenticate(credentials)
          ldapUser match {
            case None => complete(401, None)
            case Some(lu) => complete(
              getUser(lu).map(authenticationService.getOrCreateUserSession)
            )
          }
        }
      }
  }

  private def authenticate(credentials: LdapCredentials): Option[LdapUserDetails] = {
    val ldapCredentials = new UsernamePasswordAuthenticationToken(credentials.username, credentials.password)
    try {
      val ldapUser = provider.authenticate(ldapCredentials).getPrincipal.asInstanceOf[LdapUserDetails]
      Some(ldapUser)
    } catch {
      case ex: Exception => None
    }
  }

  private def getUser(ldapUser: LdapUserDetails): Future[User] = {
    val name = ldapUser.getDn.split(",")
    .find(_.startsWith("cn=")).map(_.replaceFirst("cn=", ""))
    .getOrElse("Unknown")
    val email = s"${ldapUser.getUsername}@ovoenergy.com"
    val slackId = slackClient.findSlackIdByEmail(email).map(_.getOrElse("unknown"))
    slackId.map(User(name, email, _))
  }

}

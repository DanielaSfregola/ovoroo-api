package com.ovoenergy.ovoroo.resources

import org.springframework.security.ldap.userdetails.LdapUserDetails
import spray.routing._

import com.ovoenergy.ovoroo.entities.User
import com.ovoenergy.ovoroo.routing.MyHttpService

trait UserResource extends MyHttpService {

  def userRoutes(ldapUser: LdapUserDetails): Route = path("user") {
      val name = ldapUser.getDn.split(",")
                  .find(_.startsWith("cn=")).map(_.replaceFirst("cn=", ""))
                  .getOrElse("Unknown")
      val email = s"${ldapUser.getUsername}@ovoenergy.com"
      val user = User(ldapUser.getDn, s"${ldapUser.getUsername}@ovoenergy.com")
      complete(User(name, email))
  }

}

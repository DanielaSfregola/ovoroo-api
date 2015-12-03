package com.ovoenergy.ovoroo.resources

import org.springframework.security.ldap.userdetails.LdapUserDetails
import spray.routing._

import com.ovoenergy.ovoroo.routing.MyHttpService
import com.ovoenergy.ovoroo.services.OrderService

trait UserResource extends MyHttpService {

  val orderService: OrderService

  def userRoutes(user: LdapUserDetails): Route = path("user") {
      complete(user)
  }

}

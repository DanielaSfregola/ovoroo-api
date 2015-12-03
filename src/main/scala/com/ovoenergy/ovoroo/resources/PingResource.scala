package com.ovoenergy.ovoroo.resources

import spray.routing._

import com.ovoenergy.ovoroo.routing.MyHttpService

trait PingResource extends MyHttpService {

  def pingRoutes: Route = path("ping") {
    complete("pong")
  }

}

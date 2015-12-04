package com.ovoenergy.ovoroo

import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.can.Http

import scala.concurrent.duration._

object Main extends App {
  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")
  val adminPort = 8000


  implicit val system = ActorSystem("ovoroo-server")

  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(10 seconds)

  val server = system.actorOf(Props(new RestInterface))

  IO(Http).ask(Http.Bind(listener = server, interface = host, port = port))
    .mapTo[Http.Event]
    .map {
      case Http.Bound(address) =>
        println(s"REST interface bound to $address")
      case Http.CommandFailed(cmd) =>
        println("REST interface could not bind to " +
          s"$host:$port, ${cmd.failureMessage}")
        system.shutdown()
    }

  val api = system.actorOf(Props(new AppInterface))
  IO(Http).ask(Http.Bind(listener = api, interface = host, port = adminPort))
  .mapTo[Http.Event]
  .map {
    case Http.Bound(address) =>
      println(s"App bound to $address")
    case Http.CommandFailed(cmd) =>
      println("App could not bind to " +
      s"$host:$adminPort, ${cmd.failureMessage}")
      system.shutdown()
  }
}




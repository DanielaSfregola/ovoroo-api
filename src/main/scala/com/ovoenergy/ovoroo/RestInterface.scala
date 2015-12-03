package com.ovoenergy.ovoroo

import spray.routing._

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

import com.ovoenergy.ovoroo.resources.QuestionResource
import com.ovoenergy.ovoroo.services.QuestionService

class RestInterface(implicit val executionContext: ExecutionContext) extends HttpServiceActor with Resources {

  def receive = runRoute(routes)

  val questionService = new QuestionService

  val routes: Route = questionRoutes

}

trait Resources extends QuestionResource

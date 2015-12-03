package com.ovoenergy.ovoroo.resources

import spray.routing._

import com.ovoenergy.ovoroo.entities.{QuestionUpdate, Question}
import com.ovoenergy.ovoroo.routing.MyHttpService
import com.ovoenergy.ovoroo.services.QuestionService

trait QuestionResource extends MyHttpService {

  val questionService: QuestionService

  def questionRoutes: Route = pathPrefix("questions") {
    pathEnd {
      post {
        entity(as[Question]) { question =>
          completeWithLocationHeader(
            resourceId = questionService.createQuestion(question),
            ifDefinedStatus = 201, ifEmptyStatus = 409)
          }
        }
    } ~
    path(Segment) { id =>
      get {
        complete(questionService.getQuestion(id))
      } ~
      put {
        entity(as[QuestionUpdate]) { update =>
          complete(questionService.updateQuestion(id, update))
        }
      } ~
      delete {
        complete(204, questionService.deleteQuestion(id))
      }
    }
  }
}

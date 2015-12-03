package com.ovoenergy.ovoroo

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

import spray.http._
import spray.routing._
import spray.routing.authentication.BasicAuth

import com.ovoenergy.ovoroo.resources.{UserResource, OrderResource, PingResource}
import com.ovoenergy.ovoroo.services.OrderService

class RestInterface(implicit val executionContext: ExecutionContext) extends HttpServiceActor with CORSSupport with Resources {

  def receive = runRoute(routes)

  val orderService = new OrderService

  val routes: Route =
    pingRoutes ~
    authenticate(BasicAuth(ldapAuthenticator _, realm = "Use for OVO windows credentials")) { ldapUser =>
      cors {
        orderRoutes ~
        userRoutes(ldapUser)
      }
    }
}

trait Resources extends PingResource with OrderResource with UserResource

trait CORSSupport {
  this: HttpService =>

  private val allowOriginHeader = HttpHeaders.`Access-Control-Allow-Origin`(AllOrigins)
  private val optionsCorsHeaders = List(
    HttpHeaders.`Access-Control-Allow-Headers`("Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, User-Agent"),
    HttpHeaders.`Access-Control-Max-Age`(1728000))

  def cors[T]: Directive0 = mapRequestContext { ctx => ctx.withRouteResponseHandling({
    //It is an option requeset for a resource that responds to some other method
    case Rejected(x) if (ctx.request.method.equals(HttpMethods.OPTIONS) && !x.filter(_.isInstanceOf[MethodRejection]).isEmpty) => {
      val allowedMethods: List[HttpMethod] = x.filter(_.isInstanceOf[MethodRejection]).map(rejection=> {
        rejection.asInstanceOf[MethodRejection].supported
      })
      ctx.complete(HttpResponse().withHeaders(
        HttpHeaders.`Access-Control-Allow-Methods`(HttpMethods.OPTIONS, allowedMethods :_*) ::  allowOriginHeader ::
        optionsCorsHeaders
      ))
    }
  }).withHttpResponseHeadersMapped { headers =>
    allowOriginHeader :: headers
  }

  }
}

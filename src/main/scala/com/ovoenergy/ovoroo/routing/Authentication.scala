package com.ovoenergy.ovoroo.routing

trait Authentication {

  //val authenticator = new LdapAuthenticator()
  /*: LdapAuthenticator[Unit] = { ctx =>
    Future {
      val maybeCredentials = extractAuthQueryParam(ctx)
      maybeCredentials.fold[authentication.Authentication[Unit]](
        Left(AuthenticationFailedRejection(CredentialsMissing, List()))
      )( credentials =>
        credentials match {
          case AppCredentials(appId, appSecret) => Right()
          case _ => Left(AuthenticationFailedRejection(CredentialsRejected, List()))
        }
      )
    }
  }

  private def extractAuthQueryParam(ctx: RequestContext): Option[AppCredentials] = {
    val queryParam = ctx.request.uri.query.toMap
    for {
      id <- queryParam.get(AppIdParameter)
      secret <- queryParam.get(AppSecretParameter)
    } yield AppCredentials(id, secret)
  }*/
}

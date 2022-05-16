package co.luckywolf.valr.api

import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.none
import co.luckywolf.valr.api.ApiFunctions.matchPathToPermissionRules
import co.luckywolf.valr.api.ApiFunctions.matchPathToStartsWithMatchingRule
import co.luckywolf.valr.api.ApiFunctions.verifyUserPermissionsFor
import co.luckywolf.valr.protocol.ApiTypes
import co.luckywolf.valr.protocol.DataTypes
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.web.RoutingContext
import org.apache.commons.codec.binary.Hex


class ApiPermissionAuthorizationHandler(val accountPermissionService: (String) -> List<ApiTypes.ApiPermission>) :
  Handler<RoutingContext> {
  override fun handle(rc: RoutingContext) {

    val accountPermissions: List<ApiTypes.ApiPermission> = accountPermissionService(rc.user().getAccountId())



    matchPathToPermissionRules(
      path = rc.request().path(),
      matchingStrategy = matchPathToStartsWithMatchingRule
    ).map { p ->
      verifyUserPermissionsFor(accountPermissions, p).map {
        rc.next()
      }.mapLeft {
        rc.response().setStatusCode(403).end(JsonObject().put("error", "insufficient permissions").toBuffer())
      }
    }.getOrElse {
      rc.response().setStatusCode(403).end(JsonObject().put("error", "unknown permission rule").toBuffer())
    }
  }

}

class ApiKeyAuthenticationHandler(private val accounts: () -> Map<String, DataTypes.Account>) :
  Handler<RoutingContext> {

  companion object {
    val hmac = Crypto.Hmac512.default()
  }

  override fun handle(rc: RoutingContext) {

    val response = rc.response()
    val request = rc.request()

    ApiTypes.ApiRequestKey.create(request.headers()).map {

      val currentAccounts = accounts.invoke()

      when {
        currentAccounts.containsKey(it.key) -> {

          val account = currentAccounts[it.key]!!

          val signed = hmac.sign(
            apiKeySecret = account.apiSecret,
            timestamp = it.timestamp,
            verb = request.method().name().uppercase(),
            path = request.path(),
            body =
            if (request.method() == HttpMethod.GET) none()
            else none() //Option(request.body().toString()) --> live would sign the body of the request
          )

          when (it.signature) {
            Hex.encodeHexString(signed) -> {
              rc.setUser(
                User.create(
                  JsonObject()
                    .put("account_id", account.accountId)
                )
              )
              rc.next()
            }
            else -> {
              response.setStatusCode(401).end(JsonObject().put("error", "invalid signature").toBuffer())
            }
          }
        }
        else -> {
          response.setStatusCode(401).end(JsonObject().put("error", "unknown api key").toBuffer())
        }
      }
    }.mapLeft { err ->
      response.setStatusCode(401).end(JsonObject().put("error", err.extract().error).toBuffer())
    }
  }
}



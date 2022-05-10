package co.luckywolf.valr.api

import co.luckywolf.valr.exchange.Trade
import co.luckywolf.valr.exchange.Trade.tryExecuteOrderFor
import co.luckywolf.valr.protocol.ApiTypes
import co.luckywolf.valr.protocol.DataTypes
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.ext.auth.User
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import java.math.BigDecimal

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(ApiVerticle())
}


class ApiVerticle() : AbstractVerticle() {

  override fun start(start: Promise<Void>) {

    val router = router()

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(9876)
  }

  private fun router(): Router {

    val router = Router.router(vertx)
    router.route().handler(BodyHandler.create())

    val tradeEngine = Trade.TradeEngine()

    val accounts = mapOf(
      "api2" to DataTypes.Account("buffet", "", "".toByteArray()),
      "api2" to DataTypes.Account("musk", "", "".toByteArray())
    )

    val authenticationHandler = ApiKeyAuthenticationHandler {
      accounts
    }

    val accountPermissions = mapOf(
      "buffet" to listOf(
        ApiTypes.ApiPermission.Trade,
        ApiTypes.ApiPermission.View
      ),
      "musk" to
        listOf(
          ApiTypes.ApiPermission.Trade,
          ApiTypes.ApiPermission.View
        )
    )

    val authorizationHandler = ApiPermissionAuthorizationHandler { accountId ->
      accountPermissions[accountId].orEmpty()
    }

    router.route("v1/*").handler(
      authenticationHandler
    ).handler(
      authorizationHandler
    )

    router.get("v1/marketdata/:currencyPair/orderbook")
      .produces("application/json")
      .consumes("application/json")
      .handler {

      }

    router.post("v1/orders/limit")
      .produces("application/json")
      .consumes("application/json")
      .handler {

        val accountId = it.user().getAccountId()

        ApiTypes.PlaceLimitOrderApiRequest.create(accountId, it.bodyAsJson).map { r ->

          val limitOrderBook = tradeEngine.limitOrderBookBy(r.currentPair)

          val result = tryExecuteOrderFor(
            limitOrderBook,
            DataTypes.Order(
              DataTypes.Side.ASK,
              BigDecimal(33),
              BigDecimal(33),
              r.currentPair,
              DataTypes.TimeInForce.GTC,
              DataTypes.Trader(accountId)
            )
          )
        }

        it.next()

      }.handler {

        it.user().authorizations()

        //ApiTypes.OrderBookApiResponse()

        it.response().end();

      }.failureHandler {

      }


    return router
  }
}

fun User.getAccountId(): String {
  return this.principal().getString("account_id")
}


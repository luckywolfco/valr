package co.luckywolf.valr.api

import arrow.core.getOrElse
import arrow.core.orElse
import co.luckywolf.valr.exchange.Trade
import co.luckywolf.valr.exchange.Trade.getAsksFor
import co.luckywolf.valr.exchange.Trade.getBidsFor
import co.luckywolf.valr.exchange.Trade.getTradesFor
import co.luckywolf.valr.exchange.Trade.tryExecuteOrderFor
import co.luckywolf.valr.protocol.ApiTypes
import co.luckywolf.valr.protocol.DataTypes
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import org.joda.time.DateTime

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(ApiVerticle())
}


class ApiVerticle() : AbstractVerticle() {

  override fun start(start: Promise<Void>) {

    val router = setup()

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(9876) {
        println("Started api server on port 9876")
      }

  }

  private fun setup(): Router {

    val router = Router.router(vertx)
    router.route().handler(BodyHandler.create())

    val tradeEngine = Trade.TradeEngine(
      mutableMapOf(
        DataTypes.CurrencyPair.BTCZAR to DataTypes.LimitOrderBook(
          DataTypes.CurrencyPair.BTCZAR
        )
      )
    )

    val accounts = mapOf(
      "warren"
        to DataTypes.Account(
        "warren",
        "warren",
        "4961b74efac86b25cce8fbe4c9811c4c7a787b7a5996660afcc2e287ad864363".toByteArray()
      ),
      "elon"
        to DataTypes.Account(
        "elon",
        "elon",
        "4961b74efac86b25cce8fbe4c9811c4c7a787b7a5996660afcc2e287ad864363".toByteArray()
      )
    )

    val authenticationHandler = ApiKeyAuthenticationHandler {
      accounts
    }

    val accountPermissions = mapOf(
      "warren" to listOf(
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

    router.route("/v1/*").handler(authenticationHandler)

    router.route("/v1/*").handler(authorizationHandler)

    router.get("/v1/marketdata/:currencyPair/orderbook")
      .produces("application/json")
      .handler {

        ApiTypes.GetOrderHistoryApiRequest.create(it.pathParams(), it.request().params()).map { r ->

          val limitOrderBook = tradeEngine.limitOrderBookBy(r.currencyPair)

          val asks = getAsksFor(limitOrderBook, r.limit).map { trade ->
            trade.value.sumOf { a -> a.quantity }
            JsonObject()
              .put("side", DataTypes.Side.ASK.name)
              .put("quantity", trade.value.sumOf { a -> a.quantity }.toString())
              .put("price", trade.key.toString())
              .put("currencyPair", r.currencyPair.name.uppercase())
              .put("orderCount", trade.value.size)
          }

          val bids = getBidsFor(limitOrderBook, r.limit).map { trade ->
            trade.value.sumOf { a -> a.quantity }
            JsonObject()
              .put("side", DataTypes.Side.BID.name)
              .put("quantity", trade.value.sumOf { a -> a.quantity }.toString())
              .put("price", trade.key.toString())
              .put("currencyPair", r.currencyPair.name.uppercase())
              .put("orderCount", trade.value.size)
          }

          it.response()
            .setStatusCode(200)
            .end(
              JsonArray()
                .add(JsonObject().put("Asks", asks))
                .add(JsonObject().put("Bids", bids)).toBuffer()
            )


        }.mapLeft { err ->
          it.response()
            .setStatusCode(400)
            .end(JsonObject().put("error", err.error).toBuffer())
        }
      }

    router.get("/v1/marketdata/:currencyPair/tradehistory")
      .produces("application/json")
      .handler {

        ApiTypes.GetTradeHistoryApiRequest.create(it.pathParams(), it.request().params()).map { r ->

          val limitOrderBook = tradeEngine.limitOrderBookBy(r.currencyPair)
          val trades: List<JsonObject> = getTradesFor(limitOrderBook, r.skip, r.limit).flatMap { trade ->
            trade.quantityMatches.map { qm ->
              JsonObject()
                .put("price", trade.fillPrice.toString())
                .put("quantity", qm.taken.toString())
                .put("currencyPair", r.currencyPair.toString().uppercase())
                .put("tradedAt", DateTime(trade.timestamp.toString()))
                .put("takerSide", trade.fillSide.toString().lowercase())
                .put("sequenceId", trade.tradeId.sequence.toString())
                .put("id", trade.tradeId.id)
                .put("quoteVolume", trade.quantity.toString())
            }
          }
          it.response()
            .setStatusCode(200)
            .end(JsonArray(trades).toBuffer())
          /*
          [
  {
    "price": "510009",
    "quantity": "0.00060786",
    "currencyPair": "BTCZAR",
    "tradedAt": "2021-02-08T12:55:26.015Z",
    "takerSide": "buy",
    "sequenceId": 64139,
    "id": "60cfb19b-c651-42a6-a991-b99455d2b28c",
    "quoteVolume": "310.01407074"
  }
           */

        }.mapLeft { err ->
          it.response()
            .setStatusCode(400)
            .end(JsonObject().put("error", err.error).toBuffer())
        }
      }

    router.post("/v1/orders/limit")
      .produces("application/json")
      .consumes("application/json")
      .handler { rc ->

        val accountId = rc.user().getAccountId()

        ApiTypes.PlaceLimitOrderApiRequest.create(accountId, rc.bodyAsJson).map { r ->

          val limitOrderBook = tradeEngine.limitOrderBookBy(r.currentPair)

          val result = tryExecuteOrderFor(
            limitOrderBook,
            DataTypes.Order(
              r.side,
              r.quantity,
              r.price,
              r.currentPair,
              r.timeInForce,
              DataTypes.Trader(accountId, r.customerOrderId)
            )
          ).map {
            rc.end()

          }.mapLeft {
            rc.response()
              .setStatusCode(400)
              .end(JsonObject().put("error", it.error).toBuffer())
          }

        }.mapLeft {
          rc.response()
            .setStatusCode(400)
            .end(JsonObject().put("error", it.error).toBuffer())
        }

      }
    return router
  }
}

fun User.getAccountId(): String {
  return this.principal().getString("account_id")
}


package co.luckywolf.valr

import arrow.core.some
import co.luckywolf.valr.api.ApiVerticle
import co.luckywolf.valr.protocol.DataTypes
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(VertxExtension::class)
class TestVerticle {

  @BeforeEach
  fun deploy_verticle(vertx: Vertx, testContext: VertxTestContext) {
    println("Deploying verticle...")
    vertx.deployVerticle(ApiVerticle());

  }

  @AfterEach
  fun finish(vertx: Vertx, testContext: VertxTestContext) {
    println("Destroying verticle...")
    //vertx.close(testContext.succeeding { testContext.completeNow() })
  }

  @Test
  fun verticle_deployed(vertx: Vertx, testContext: VertxTestContext) {
    println("verticle_deployed")
    testContext.completeNow()
  }

  @Test
  fun trade_history(vertx: Vertx, testContext: VertxTestContext) {

    println("trade_history")

    vertx.createHttpClient()
      .request(
        HttpMethod.GET,
        9876,
        "127.0.0.1",
        "/v1/marketdata/${DataTypes.CurrencyPair.BTCZAR.name}/tradehistory?skip=0&limit=100"
      )
      .compose { request -> request.send() }
      .compose { response -> response.body() }
      .onSuccess { body ->
        testContext.verify {
          //assertEquals("Greetings!", body.toString())
          testContext.completeNow()
        }
      }
      .onFailure { failure -> testContext.failNow(failure) }
  }
}

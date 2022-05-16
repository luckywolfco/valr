package co.luckywolf.valr.trade

import co.luckywolf.valr.api.Crypto
import co.luckywolf.valr.protocol.DataTypes
import org.apache.commons.codec.binary.Hex
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class ApiKeyTests {

  @Test
  fun generate_trade_history_sig() {
    val hmac = Crypto.Hmac512.default()
    val sign = hmac.sign(
      apiKeySecret = "4961b74efac86b25cce8fbe4c9811c4c7a787b7a5996660afcc2e287ad864363".toByteArray(),
      timestamp = "1560007630778",
      verb = "GET",
      path = "/v1/marketdata/BTCZAR/tradehistory"
    )
    println(
      Hex.encodeHexString(sign)
    )

    val currencyPair = DataTypes.CurrencyPair.valueOf("BTCZAR")
    println(currencyPair)
  }

  @Test
  fun generate_order_book_sig() {
    val hmac = Crypto.Hmac512.default()
    val sign = hmac.sign(
      apiKeySecret = "4961b74efac86b25cce8fbe4c9811c4c7a787b7a5996660afcc2e287ad864363".toByteArray(),
      timestamp = "1560007630778",
      verb = "GET",
      path = "/v1/marketdata/BTCZAR/orderbook"
    )
    println(
      Hex.encodeHexString(sign)
    )

    val currencyPair = DataTypes.CurrencyPair.valueOf("BTCZAR")
    println(currencyPair)
  }

  @Test
  fun generate_order_sig() {
    val hmac = Crypto.Hmac512.default()
    val sign = hmac.sign(
      apiKeySecret = "4961b74efac86b25cce8fbe4c9811c4c7a787b7a5996660afcc2e287ad864363".toByteArray(),
      timestamp = "1560007630778",
      verb = "POST",
      path = "/v1/orders/limit"
    )
    println(
      Hex.encodeHexString(sign)
    )

    val currencyPair = DataTypes.CurrencyPair.valueOf("BTCZAR")
    println(currencyPair)
  }

  @Test
  fun verify_request() {
    val hmac = Crypto.Hmac512.default()
    val sign = hmac.sign(
      apiKeySecret = "4961b74efac86b25cce8fbe4c9811c4c7a787b7a5996660afcc2e287ad864363".toByteArray(),
      timestamp = "1558014486185",
      verb = "GET",
      path = "/v1/account/balances"
    )
    Assertions.assertEquals(
      Hex.encodeHexString(sign),
      "9d52c181ed69460b49307b7891f04658e938b21181173844b5018b2fe783a6d4c62b8e67a03de4d099e7437ebfabe12c56233b73c6a0cc0f7ae87e05f6289928"
    )
  }
}

class ApiPermissionTests {

  @Test
  fun match_path_starts_with_rule() {
    //assertTrue(matchPathToStartsWithMatchingRule(path = "v1/marketdata/abc", matchingRule = "v1/marketdata/"))
  }
}

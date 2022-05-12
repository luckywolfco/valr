package co.luckywolf.valr.protocol

import arrow.core.Option
import arrow.core.none
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Clock
import java.util.*


object DataTypes {

  val zero = BigDecimal(0)


  //match bid to ask
  //e.g. 100 to buy @ 10 - sell @100 @ 5
  //if buy price is >= sell price make the trade

  data class LimitOrderTrade(
    val orderId: OrderId,
    val tradeId: OrderId,
    val tradeSide: Side,
    val price: BigDecimal,
    val quantity: BigDecimal,
    val fillSide: Side,
    val fillPrice: BigDecimal,
    val fillQuantity: QuantityMatch,
    val timestamp: Long = Clock.systemUTC().millis(),
  )

  data class LimitOrderMatch(
    val orderId: OrderId,
    val tradeSide: Side,
    val price: BigDecimal,
    val quantity: BigDecimal,
    val fillSide: Side,
    val fillPrice: BigDecimal,
    val quantityMatches: List<QuantityMatch>,
    val timestamp: Long = Clock.systemUTC().millis(),
  )

  class QuantityTaken(
    val taken: BigDecimal,
    val left: BigDecimal
  )


  class MatchResult(val filled: BigDecimal, val matches: List<Match>)
  class Match(val id: String, val left: BigDecimal)
  data class QuantityMatch(val id: String, val index: Int, val taken: BigDecimal, val left: BigDecimal)

  //Have to make state mutable
  class LimitOrderBook(
    val currencyPair: CurrencyPair,
    val bids: TreeMap<BigDecimal, MutableList<Bid>> = TreeMap(HighLowPriceComparator()),
    val asks: TreeMap<BigDecimal, MutableList<Ask>> = TreeMap(LowHighPriceComparator()),
    val trades: MutableList<LimitOrderTrade> = mutableListOf()
  )

  class HighLowPriceComparator : Comparator<BigDecimal> {
    override fun compare(left: BigDecimal, right: BigDecimal): Int {

      if (left == right)
        return 0;

      if (left > right)
        return -1;

      return 1;
    }
  }

  class LowHighPriceComparator : Comparator<BigDecimal> {
    override fun compare(left: BigDecimal, right: BigDecimal): Int {

      if (left == right) {
        return 0
      }

      if (left > right)
        return 1;

      return -1;
    }
  }

  private val decimalFormat = DecimalFormat("#.#####", DecimalFormatSymbols(Locale.ENGLISH))

  fun Double.toDecimalPlaces(): Double {
    return decimalFormat.format(this).toDouble()
  }

  enum class CurrencyPair {
    BTCZAR, ETHZAR, XRPZAR
  }

  enum class TimeInForce {
    GTC,
    FOK,
    IOC
  }

  enum class Side {
    BID,
    ASK
  }

  class Account(
    val accountId: String,
    val apiKey: String,
    val apiSecret: ByteArray,
  )

  class Order(
    val side: Side,
    val quantity: BigDecimal,
    val price: BigDecimal,
    val currencyPair: CurrencyPair,
    val timeInForce: TimeInForce = TimeInForce.GTC,
    val account: Trader,
    val postOnly: Boolean = false
  )

  data class OrderId(val id: String = UUID.randomUUID().toString(), val sequence: Long)

  data class Bid(
    val bidId: OrderId,
    val quantity: BigDecimal,
    val price: BigDecimal,
    val currencyPair: CurrencyPair,
    val timeInForce: TimeInForce = TimeInForce.GTC,
    val timestamp: Long = Clock.systemUTC().millis(),
    val account: Trader,
    val postOnly: Boolean = false
  )

  class AskId(val id: String = UUID.randomUUID().toString(), val sequence: Long)

  data class Ask(
    val askId: OrderId,
    val quantity: BigDecimal,
    val price: BigDecimal,
    val currencyPair: CurrencyPair,
    val timeInForce: TimeInForce = TimeInForce.GTC,
    val timestamp: Long = Clock.systemUTC().millis(),
    val trader: Trader,
    val postOnly: Boolean = false
  )

  data class Trader(
    val accountId: String,
    val customOrderReferenceId: Option<String> = none()
  )

  sealed class ExchangeError(val error: String) {
    class CustomerOrderIdTransformError(error: String) : ExchangeError(error)
    class OrderViolatesCustomerOrderReferenceIdUniqueConstraint(error: String) : ExchangeError(error)
    class ExecuteOrderFailed(error: String) : ExchangeError(error)
  }
}


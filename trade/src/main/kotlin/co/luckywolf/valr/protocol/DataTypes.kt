package co.luckywolf.valr.protocol

import arrow.core.Option
import arrow.core.none
import org.decimal4j.util.DoubleRounder
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Clock
import java.util.*


object DataTypes {

  val zero = DoubleRounder.round(0.000000, 6)


  //match bid to ask
  //e.g. 100 to buy @ 10 - sell @100 @ 5
  //if buy price is >= sell price make the trade

  data class LimitOrderTrade(
    val orderId: OrderId,
    val tradeId: OrderId,
    val tradeSide: Side,
    val account: Trader,
    val price: Double,
    val quantity: Double,
    val fillSide: Side,
    val fillPrice: Double,
    val fillQuantity: QuantityMatch,
    val timestamp: Long = Clock.systemUTC().millis(),
  )

  data class LimitOrderMatch(
    val orderId: OrderId,
    val tradeSide: Side,
    val price: Double,
    val quantity: Double,
    val fillSide: Side,
    val fillPrice: Double,
    val quantityMatches: List<QuantityMatch>,
    val timestamp: Long = Clock.systemUTC().millis(),
  )

  class QuantityTaken(
    val taken: Double,
    val left: Double
  )


  class MatchResult(val filled: Double, val matches: List<Match>)
  class Match(val id: String, val left: Double)
  data class QuantityMatch(val id: String, val index: Int, val taken: Double, val left: Double)

  //Have to make state mutable
  class LimitOrderBook(
    val currencyPair: CurrencyPair,
    val bids: TreeMap<Double, MutableList<Bid>> = TreeMap(HighLowPriceComparator()),
    val asks: TreeMap<Double, MutableList<Ask>> = TreeMap(LowHighPriceComparator()),
    val trades: MutableList<LimitOrderTrade> = mutableListOf()
  )

  class HighLowPriceComparator : Comparator<Double> {
    override fun compare(left: Double, right: Double): Int {

      if (left.compareTo(right) == 0)
        return 0;

      if (left.compareTo(right) < 1)
        return 1;

      return -1;
    }
  }

  class LowHighPriceComparator : Comparator<Double> {
    override fun compare(left: Double, right: Double): Int {

      if (left.compareTo(right) == 0) {
        return 0
      }

      if (left.compareTo(right) < 1)
        return -1;

      return 1;
    }
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
    val quantity: Double,
    val price: Double,
    val currencyPair: CurrencyPair,
    val timeInForce: TimeInForce = TimeInForce.GTC,
    val account: Trader,
    val postOnly: Boolean = false
  )

  data class OrderId(val id: String = UUID.randomUUID().toString(), val sequence: Long)

  data class Bid(
    val bidId: OrderId,
    val quantity: Double,
    val price: Double,
    val currencyPair: CurrencyPair,
    val timeInForce: TimeInForce = TimeInForce.GTC,
    val timestamp: Long = Clock.systemUTC().millis(),
    val account: Trader,
    val postOnly: Boolean = false
  )

  class AskId(val id: String = UUID.randomUUID().toString(), val sequence: Long)

  data class Ask(
    val askId: OrderId,
    val quantity: Double,
    val price: Double,
    val currencyPair: CurrencyPair,
    val timeInForce: TimeInForce = TimeInForce.GTC,
    val timestamp: Long = Clock.systemUTC().millis(),
    val account: Trader,
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


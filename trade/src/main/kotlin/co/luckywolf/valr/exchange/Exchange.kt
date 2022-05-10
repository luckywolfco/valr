package co.luckywolf.valr.exchange

import arrow.core.*
import co.luckywolf.valr.protocol.DataTypes
import co.luckywolf.valr.protocol.DataTypes.LimitOrderBook
import co.luckywolf.valr.protocol.DataTypes.zero
import java.math.BigDecimal
import java.time.Clock
import java.util.*

//object Customer {
//  private val pattern = "a-zA-Z0-9".toRegex()
//
//  fun orderIdFor(
//    account: String,
//    orderId: String
//  ): Either<DataTypes.ExchangeError.CustomerOrderIdTransformError, DataTypes.CustomerOrderId> {
//    return when (orderId matches pattern) {
//      true -> Either.Right(DataTypes.CustomerOrderId(account, orderId))
//      else -> Either.Left(DataTypes.ExchangeError.CustomerOrderIdTransformError("$orderId is not alphanumeric"))
//    }
//  }
//}

object Trade {

  class TradeEngine(private val books: MutableMap<DataTypes.CurrencyPair, LimitOrderBook> = mutableMapOf()) {

    fun limitOrderBookBy(currencyPair: DataTypes.CurrencyPair): LimitOrderBook {
      return books.getOrPut(currencyPair) {
        LimitOrderBook(currencyPair)
      }
    }
  }

  fun printBook(book: LimitOrderBook) {
    book.bids.forEach { t, u ->
      println("Price: $t")
      u.forEach {
        println("Qty: ${it.quantity}")
      }
      println()
    }
  }

  fun addBidTo(book: LimitOrderBook, bid: DataTypes.Bid): DataTypes.Bid {

    val bids = book.bids.getOrPut(bid.price) {
      mutableListOf()
    }
    if (bids.isEmpty()) {
      bids.add(bid)
    } else {
      //update in place bid
    }
    return bid
  }

  fun addAskTo(book: LimitOrderBook, ask: DataTypes.Ask) {
    book.asks.getOrPut(ask.price) {
      mutableListOf()
    }.add(ask)
  }

//  fun getTopOfLineAsk(book: LimitOrderBook): Option<BigDecimal> {
//    return if (book.asks.isEmpty()) none() else Option.fromNullable(book.asks.firstKey())
//  }


  fun reshuffle(
    book: LimitOrderBook,
    bid: DataTypes.Bid,
    trades: List<DataTypes.LimitOrderTrade>
  ): Option<DataTypes.Bid> {

    return Some(trades.filter { it.tradeSide == DataTypes.Side.BID }.map { trade ->

      trade.quantityMatches.filter { it.left > zero }.forEach { quantityMatch ->
        val ask = book.asks[trade.fillPrice]?.get(quantityMatch.index)!!
        book.asks[trade.fillPrice]!![quantityMatch.index] = ask.copy(quantity = quantityMatch.left)
        //does ask get garbage collected?
      }

      trade.quantityMatches.filter { it.left == zero }.forEachIndexed { _, quantityMatch ->
        println(quantityMatch)
        book.asks[trade.fillPrice]?.removeIf { it.askId.id == quantityMatch.id }
      }
      if(book.asks[trade.fillPrice]!!.isEmpty())
        book.asks.remove(trade.fillPrice)

      getQuantityOutstanding(bid.quantity, trade.quantityMatches)
    }.sumOf { it }).filter { it > zero }.map {
      addBidTo(book, bid.copy(quantity = it))
    }.or(none())
  }

  //match bid to all asks
  fun matchBidToAsks(
    book: LimitOrderBook,
    bid: DataTypes.Bid,
  ): List<DataTypes.LimitOrderTrade> {

    val trades = mutableListOf<DataTypes.LimitOrderTrade>()

    if (book.asks.isEmpty())
      return trades

    fun match(
      n: Int,
      askPrice: BigDecimal,
      asks: List<DataTypes.Ask>,
      quantityRequired: BigDecimal
    ): List<DataTypes.LimitOrderTrade> {

      when {
        bid.price < askPrice || quantityRequired == zero -> {
          //done all the matching we could across the asks available
          return trades
        }
        n >= book.asks.size -> {
          return trades
        }
        else -> {
          val quantityMatches =
            matchBidQuantityToAskQuantities(bid, asks)

          val quantityOutstanding =
            getQuantityOutstanding(quantityRequired, quantityMatches)

          trades.add(
            DataTypes.LimitOrderTrade(
              bid.bidId,
              DataTypes.Side.BID,
              bid.price,
              bid.quantity,
              DataTypes.Side.ASK,
              askPrice,
              quantityMatches
            )
          )

          return if (quantityOutstanding == zero) {
            trades
          } else {

            //nasty
            return try {
              book.asks.higherEntry(askPrice).key
              val nextAskPrice = book.asks.higherEntry(askPrice).key
              val nextAsks = book.asks[nextAskPrice]!!
              match(n + 1, nextAskPrice, nextAsks, quantityOutstanding)
            } catch (_: NullPointerException) {
              trades
            }
          }
        }
      }
    }

    return match(
      n = 0,
      askPrice = book.asks.firstKey(),
      asks = book.asks[book.asks.firstKey()]!!.toList(),
      quantityRequired = bid.quantity
    )
  }

  fun getQuantityOutstanding(
    quantityRequired: BigDecimal,
    quantityMatches: List<DataTypes.QuantityMatch>
  ): BigDecimal {
    return quantityRequired - quantityMatches.sumOf { it.taken }
  }

  //match the bid to a single ask
  fun matchBidQuantityToAskQuantities(
    bid: DataTypes.Bid,
    asks: List<DataTypes.Ask>,
  ): List<DataTypes.QuantityMatch> {

    val quantityMatches = mutableListOf<DataTypes.QuantityMatch>()

    tailrec fun match(
      n: Int,
      quantityRequired: BigDecimal,
    ): List<DataTypes.QuantityMatch> {

      when {
        quantityRequired == zero -> return quantityMatches
        n >= asks.size -> return quantityMatches
        else -> {
          val taken = takeAvailableQuantityOnOffer(
            quantityRequired = quantityRequired,
            quantityAvailable = asks[n].quantity
          )

          quantityMatches.add(
            DataTypes.QuantityMatch(
              id = asks[n].askId.id,
              index = n,
              taken = taken.taken,
              left = taken.left
            )
          )

          return match(n + 1, quantityRequired - taken.taken)
        }
      }

    }
    return match(0, bid.quantity)


  }


  fun remove(
    ask: MutableList<DataTypes.Ask>,
    match: List<DataTypes.Match>
  ): List<DataTypes.Ask> {

    match.forEachIndexed { index, it ->
      when (it.left) {
        zero -> ask.removeAt(index)
        else -> {
          ask[index] = ask[index].copy(quantity = it.left)
        }
      }
    }
    return ask
  }


  fun takeAvailableQuantityOnOffer(
    quantityRequired: BigDecimal,
    quantityAvailable: BigDecimal
  ): DataTypes.QuantityTaken {

    return if (quantityRequired >= quantityAvailable) {
      DataTypes.QuantityTaken(
        taken = quantityAvailable,
        left = zero
      )
    } else {
      DataTypes.QuantityTaken(
        quantityRequired,
        quantityAvailable - quantityRequired
      )
    }
  }


  fun isOrderUniqueForAccount(
    book: LimitOrderBook,
    order: DataTypes.Order
  ): Boolean {

    return true
  }

  fun getBidsFor(book: LimitOrderBook): List<DataTypes.Bid> {

    return emptyList()
  }

  fun getAsksFor(book: LimitOrderBook): List<DataTypes.Ask> {
    return emptyList()
  }

  /*
    The customerOrderId has to be unique across all open orders for a given account. If you do reuse an id value
    that is currently an active open order, your order will not be placed (you can check the status of an order using the order status API call).
   */
  fun tryExecuteOrderFor(
    book: LimitOrderBook,
    order: DataTypes.Order,
  ): Either<DataTypes.ExchangeError, DataTypes.OrderExecuted> {


    return when (order.side) {
      DataTypes.Side.BID -> {

        val unique: Option<Boolean> = order.account.customOrderReferenceId.map {
          isOrderUniqueForAccount(book, order)
        }.orElse { Option(true) }

        val result = unique.map {

          val bid = DataTypes.Bid(
            bidId = DataTypes.OrderId(
              UUID.randomUUID().toString(),
              Clock.systemUTC().millis()
            ),
            quantity = order.quantity,
            price = order.price,
            currencyPair = order.currencyPair,
            timeInForce = order.timeInForce,
            account = order.account
          )

          Either.Right(DataTypes.OrderExecuted(bid.bidId))

        }.getOrElse {
          Either.Left(
            DataTypes.ExchangeError.OrderViolatesCustomerOrderReferenceIdUniqueConstraint(
              "${order.account.accountId} has existing order with client id ${order.account.customOrderReferenceId.map { it }}"
            )
          )
        }

        result
      }

      DataTypes.Side.ASK -> {

        val ask = DataTypes.Ask(
          askId = DataTypes.AskId(UUID.randomUUID().toString(), Clock.systemUTC().millis()),
          quantity = order.quantity,
          price = order.price,
          currencyPair = order.currencyPair,
          timeInForce = order.timeInForce,
          trader = order.account
        )

        Either.Right(DataTypes.OrderExecuted(DataTypes.OrderId("", 9)))
      }
    }
  }

}

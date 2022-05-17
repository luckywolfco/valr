package co.luckywolf.valr.exchange

import arrow.core.Either
import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.orElse
import co.luckywolf.valr.exchange.Asks.matchAskToBids
import co.luckywolf.valr.exchange.Bids.matchBidToAsks
import co.luckywolf.valr.protocol.DataTypes
import co.luckywolf.valr.protocol.DataTypes.LimitOrderBook
import co.luckywolf.valr.protocol.DataTypes.zero
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicLong

object Trade {

  private val sequence: AtomicLong = AtomicLong(0L)

//  fun BigDecimal.toDecimalPlaces(decimalPlaces: Int = 6): BigDecimal {
//    return BigDecimalRounder.round(this, decimalPlaces)
//  }

  class TradeEngine(private val books: MutableMap<DataTypes.CurrencyPair, LimitOrderBook> = mutableMapOf()) {

    fun limitOrderBookBy(currencyPair: DataTypes.CurrencyPair): LimitOrderBook {
      return books.getOrPut(currencyPair) {
        LimitOrderBook(currencyPair)
      }
    }
  }

  fun printBookToConsole(book: LimitOrderBook) {
    println("------------------------------Book-----------------------------")
    printBook(book, { p, b -> println(b) },
      { p, a -> println(a) },
      { t -> println(t) })
  }

  fun printBook(
    book: LimitOrderBook,
    bid: (BigDecimal, DataTypes.Bid) -> Unit,
    ask: (BigDecimal, DataTypes.Ask) -> Unit,
    trade: (DataTypes.LimitOrderTrade) -> Unit
  ) {
    book.bids.forEach { (t, u) ->
      u.forEach {
        bid(t, it)
      }
    }

    book.asks.forEach { (t, u) ->
      u.forEach {
        ask(t, it)
      }
    }

    book.trades.forEach {
      trade(it)
    }

  }

  fun getQuantityOutstanding(
    quantityRequired: BigDecimal,
    quantityMatches: List<DataTypes.QuantityMatch>
  ): BigDecimal {
    return quantityRequired - quantityMatches.sumOf { it.taken }
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

  fun getTradesFor(book: LimitOrderBook, skip: Int, limit: Int): List<DataTypes.LimitOrderTrade> {
    return book.trades.take(limit)
  }


  /*
    The customerOrderId has to be unique across all open orders for a given account. If you do reuse an id value
    that is currently an active open order, your order will not be placed (you can check the status of an order using the order status API call).
   */
  fun tryPlaceOrderFor(
    book: LimitOrderBook,
    order: DataTypes.Order,
  ): Either<DataTypes.ExchangeError, DataTypes.OrderId> {


    return when (order.side) {
      DataTypes.Side.BID -> {

        val unique: Option<Boolean> = order.account.customOrderReferenceId.map {
          isOrderUniqueForAccount(book, order)
        }.orElse { Option(true) }

        unique.map {

          val bid = DataTypes.Bid(
            bidId = DataTypes.OrderId(
              sequence = sequence.incrementAndGet()
            ),
            quantity = order.quantity,
            price = order.price,
            currencyPair = order.currencyPair,
            timeInForce = order.timeInForce,
            account = order.account
          )

          Bids.reshuffle(book, bid, matchBidToAsks(book, bid))
          Either.Right(bid.bidId)

        }.getOrElse {
          Either.Left(
            DataTypes.ExchangeError.OrderViolatesCustomerOrderReferenceIdUniqueConstraint(
              "${order.account.accountId} has existing bid order with client id ${order.account.customOrderReferenceId.map { it }}"
            )
          )
        }
      }

      DataTypes.Side.ASK -> {

        val unique: Option<Boolean> = order.account.customOrderReferenceId.map {
          isOrderUniqueForAccount(book, order)
        }.orElse { Option(true) }

        unique.map {

          val ask = DataTypes.Ask(
            askId = DataTypes.OrderId(
              sequence = sequence.incrementAndGet()
            ),
            quantity = order.quantity,
            price = order.price,
            currencyPair = order.currencyPair,
            timeInForce = order.timeInForce,
            account = order.account
          )

          Asks.reshuffle(book, ask, matchAskToBids(book, ask))
          Either.Right(ask.askId)

        }.getOrElse {
          Either.Left(
            DataTypes.ExchangeError.OrderViolatesCustomerOrderReferenceIdUniqueConstraint(
              "${order.account.accountId} has existing ask order with client id ${order.account.customOrderReferenceId.map { it }}"
            )
          )
        }
      }
    }
  }
}

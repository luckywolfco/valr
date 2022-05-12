package co.luckywolf.valr.exchange

import arrow.core.*
import arrow.core.Option.Companion.fromNullable
import co.luckywolf.valr.protocol.DataTypes
import co.luckywolf.valr.protocol.DataTypes.LimitOrderBook
import co.luckywolf.valr.protocol.DataTypes.zero
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicLong

object Trade {

  private val sequence: AtomicLong = AtomicLong(0L)

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

    book.bids.getOrPut(bid.price) {
      mutableListOf()
    }.add(bid)

    return bid
  }

  fun removeBidFrom(book: LimitOrderBook, bid: DataTypes.Bid): Option<MutableList<DataTypes.Bid>> {
    return fromNullable(book.bids.remove(bid.price))
  }

  fun addAskTo(book: LimitOrderBook, ask: DataTypes.Ask): DataTypes.Ask {
    book.asks.getOrPut(ask.price) {
      mutableListOf()
    }.add(ask)

    return ask
  }


  //Try hold off state changes for as long as possible
  fun reshuffle(
    book: LimitOrderBook,
    bid: DataTypes.Bid,
    matches: List<DataTypes.LimitOrderMatch>
  ): List<DataTypes.LimitOrderMatch> {

    Some(matches.filter { it.tradeSide == DataTypes.Side.BID }.map { match ->

      //each match is effectively a trade
      book.trades.addAll(match.quantityMatches.map {
        DataTypes.LimitOrderTrade(
          orderId = bid.bidId,
          tradeId = DataTypes.OrderId(sequence = sequence.incrementAndGet()),
          tradeSide = DataTypes.Side.BID,
          price = bid.price,
          quantity = bid.quantity,
          fillSide = DataTypes.Side.ASK,
          fillPrice = match.fillPrice,
          fillQuantity = it
        )
      })

      match.quantityMatches.filter { it.left > zero }.forEach { quantityMatch ->
        val price = fromNullable(book.asks[match.fillPrice]?.get(quantityMatch.index))
        price.map {
          book.asks[match.fillPrice]?.set(quantityMatch.index, it.copy(quantity = quantityMatch.left))
        }
      }

      match.quantityMatches.filter { it.left == zero }.forEachIndexed { _, quantityMatch ->
        book.asks[match.fillPrice]?.removeIf { it.askId.id == quantityMatch.id }
      }

      //if all asks for that price has been filled remove the price entirely
      fromNullable(book.asks[match.fillPrice]).map {
        if (it.isEmpty())
          book.asks.remove(match.fillPrice)
      }

      getQuantityOutstanding(bid.quantity, match.quantityMatches)

    }.sumOf { it }).map {
      when {
        it == zero && matches.isNotEmpty() -> {
          removeBidFrom(book, bid)
        }
        matches.isEmpty() -> {
          addBidTo(book, bid)
        }
        else -> {
          addBidTo(book, bid.copy(quantity = it))
        }
      }
    }
    return matches
  }


  fun reshuffle(
    book: LimitOrderBook,
    ask: DataTypes.Ask,
    trades: List<DataTypes.LimitOrderMatch>
  ): Option<DataTypes.Ask> {

    return Some(trades.filter { it.tradeSide == DataTypes.Side.ASK }.map { trade ->

      //update ask with partially filled bids
      trade.quantityMatches.filter { it.left > zero }.forEach { quantityMatch ->
        val price = fromNullable(book.bids[trade.fillPrice]?.get(quantityMatch.index))
        price.map {
          book.bids[trade.fillPrice]?.set(quantityMatch.index, it.copy(quantity = quantityMatch.left))
        }
      }

      trade.quantityMatches.filter { it.left == zero }.forEachIndexed { _, quantityMatch ->
        book.asks[trade.fillPrice]?.removeIf { it.askId.id == quantityMatch.id }
      }

      //if all asks for that price has been filled remove the price entirely
      fromNullable(book.asks[trade.fillPrice]).map {
        if (it.isEmpty())
          book.asks.remove(trade.fillPrice)
      }

      getQuantityOutstanding(ask.quantity, trade.quantityMatches)

    }.sumOf { it }).map {
      if (it == zero)
        addAskTo(book, ask)
      else
        addAskTo(book, ask.copy(quantity = it))
    }
  }


  fun matchBidToAsks(
    book: LimitOrderBook,
    bid: DataTypes.Bid,
  ): List<DataTypes.LimitOrderMatch> {

    val trades = mutableListOf<DataTypes.LimitOrderMatch>()

    fun match(
      n: Int,
      askPrice: BigDecimal,
      asks: List<DataTypes.Ask>,
      quantityRequired: BigDecimal
    ): List<DataTypes.LimitOrderMatch> {

      when {
        book.asks.isEmpty() -> {
          return trades
        }
        bid.price < askPrice || quantityRequired == zero -> {
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
            DataTypes.LimitOrderMatch(
              orderId = bid.bidId,
              tradeSide = DataTypes.Side.BID,
              price = bid.price,
              quantity = bid.quantity,
              fillSide = DataTypes.Side.ASK,
              fillPrice = askPrice,
              quantityMatches = quantityMatches
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

  //match ask to all bids
  fun matchAskToBids(
    book: LimitOrderBook,
    ask: DataTypes.Ask,
  ): List<DataTypes.LimitOrderMatch> {

    val trades = mutableListOf<DataTypes.LimitOrderMatch>()

    if (book.bids.isEmpty())
      return trades

    fun match(
      n: Int,
      bidPrice: BigDecimal,
      bids: List<DataTypes.Bid>,
      quantityRequired: BigDecimal
    ): List<DataTypes.LimitOrderMatch> {

      when {
        ask.price > bidPrice || quantityRequired == zero -> {
          return trades
        }
        n >= book.asks.size -> {
          return trades
        }
        else -> {
          val quantityMatches =
            matchAskQuantityToBidQuantities(ask, bids)

          val quantityOutstanding =
            getQuantityOutstanding(quantityRequired, quantityMatches)

          trades.add(
            DataTypes.LimitOrderMatch(
              ask.askId,
              DataTypes.Side.ASK,
              ask.price,
              ask.quantity,
              DataTypes.Side.ASK,
              bidPrice,
              quantityMatches
            )
          )

          return if (quantityOutstanding == zero) {
            trades
          } else {

            //nasty
            return try {
              book.bids.higherEntry(bidPrice).key
              val nextBidPrice = book.bids.higherEntry(bidPrice).key
              val nextBids = book.bids[nextBidPrice]!!
              match(n + 1, nextBidPrice, nextBids, quantityOutstanding)
            } catch (_: NullPointerException) {
              trades
            }
          }
        }
      }
    }

    return match(
      n = 0,
      bidPrice = book.bids.firstKey(),
      bids = book.bids[book.bids.firstKey()]!!.toList(),
      quantityRequired = ask.quantity
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

  fun matchAskQuantityToBidQuantities(
    ask: DataTypes.Ask,
    bids: List<DataTypes.Bid>,
  ): List<DataTypes.QuantityMatch> {

    val quantityMatches = mutableListOf<DataTypes.QuantityMatch>()

    tailrec fun match(
      n: Int,
      quantityRequired: BigDecimal,
    ): List<DataTypes.QuantityMatch> {

      when {
        quantityRequired == zero -> return quantityMatches
        n >= bids.size -> return quantityMatches
        else -> {
          val taken = takeAvailableQuantityOnOffer(
            quantityRequired = quantityRequired,
            quantityAvailable = bids[n].quantity
          )

          quantityMatches.add(
            DataTypes.QuantityMatch(
              id = bids[n].bidId.id,
              index = n,
              taken = taken.taken,
              left = taken.left
            )
          )

          return match(n + 1, quantityRequired - taken.taken)
        }
      }
    }
    return match(0, ask.quantity)
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

  fun getBidsFor(
    book: LimitOrderBook,
    limit: Int
  ): List<MutableMap.MutableEntry<BigDecimal, MutableList<DataTypes.Bid>>> {
    return book.bids.entries.take(limit)

  }

  fun getAsksFor(
    book: LimitOrderBook,
    limit: Int
  ): List<MutableMap.MutableEntry<BigDecimal, MutableList<DataTypes.Ask>>> {
    return book.asks.entries.take(limit)
  }

  /*
    The customerOrderId has to be unique across all open orders for a given account. If you do reuse an id value
    that is currently an active open order, your order will not be placed (you can check the status of an order using the order status API call).
   */
  fun tryExecuteOrderFor(
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

          reshuffle(book, bid, matchBidToAsks(book, bid))
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
            trader = order.account
          )

          reshuffle(book, ask, matchAskToBids(book, ask))
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

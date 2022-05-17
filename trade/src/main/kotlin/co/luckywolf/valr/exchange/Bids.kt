package co.luckywolf.valr.exchange

import arrow.core.Option
import arrow.core.Some
import co.luckywolf.valr.protocol.DataTypes
import co.luckywolf.valr.protocol.DataTypes.zero
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicLong

object Bids {

  private val sequence: AtomicLong = AtomicLong(0L)

  //match the bid to a single ask
  fun matchBidQuantityToAskQuantities(
    quantityRequired: BigDecimal,
    asks: List<DataTypes.Ask>,
  ): List<DataTypes.QuantityMatch> {

    val quantityMatches = mutableListOf<DataTypes.QuantityMatch>()

    tailrec fun match(
      n: Int,
      remainingRequired: BigDecimal,
    ): List<DataTypes.QuantityMatch> {

      when {
        remainingRequired == zero -> return quantityMatches
        n >= asks.size -> return quantityMatches
        else -> {
          val taken = Trade.takeAvailableQuantityOnOffer(
            quantityRequired = remainingRequired,
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

          return match(n + 1, (remainingRequired - taken.taken))
        }
      }
    }
    return match(0, quantityRequired)
  }

  fun matchBidToAsks(
    book: DataTypes.LimitOrderBook,
    bid: DataTypes.Bid,
  ): List<DataTypes.LimitOrderMatch> {

    val trades = mutableListOf<DataTypes.LimitOrderMatch>()

    if (book.asks.isEmpty())
      return trades

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
        bid.price < askPrice || quantityRequired == DataTypes.zero -> {
          return trades
        }
        n >= book.asks.size -> {
          return trades
        }
        else -> {
          val quantityMatches =
            matchBidQuantityToAskQuantities(quantityRequired, asks)

          val quantityOutstanding =
            Trade.getQuantityOutstanding(quantityRequired, quantityMatches)

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

  //Try hold off state changes for as long as possible
  fun reshuffle(
    book: DataTypes.LimitOrderBook,
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
          bid.account,
          price = bid.price,
          quantity = bid.quantity,
          fillSide = DataTypes.Side.ASK,
          fillPrice = match.fillPrice,
          fillQuantity = it
        )
      })

      match.quantityMatches.filter { it.left > zero }.forEach { quantityMatch ->
        val price = Option.fromNullable(book.asks[match.fillPrice]?.get(quantityMatch.index))
        price.map {
          book.asks[match.fillPrice]?.set(quantityMatch.index, it.copy(quantity = quantityMatch.left))
        }
      }

      match.quantityMatches.filter { it.left == zero }.forEachIndexed { _, quantityMatch ->
        book.asks[match.fillPrice]?.removeIf { it.askId.id == quantityMatch.id }
      }

      //if all asks for that price has been filled remove the price entirely
      Option.fromNullable(book.asks[match.fillPrice]).map {
        if (it.isEmpty())
          book.asks.remove(match.fillPrice)
      }

      Trade.getQuantityOutstanding(bid.quantity, match.quantityMatches)

    }.sumOf { it }).map {
      when {
        //fully filled with trades and matches then remove
        (bid.quantity - it == zero || it == zero) && matches.isNotEmpty()
          && matches.flatMap { qm -> qm.quantityMatches }.isNotEmpty() -> {
          removeBidFrom(book, bid)
        }
        //no matches just add to book
        matches.isEmpty() -> {
          addBidTo(book, bid)
        }
        //matches but only partially filled
        else -> {
          addBidTo(book, bid.copy(quantity = it))
        }
      }
    }
    return matches
  }

  fun addBidTo(book: DataTypes.LimitOrderBook, bid: DataTypes.Bid): DataTypes.Bid {

    book.bids.getOrPut(bid.price) {
      mutableListOf()
    }.add(bid)

    return bid
  }

  fun removeBidFrom(book: DataTypes.LimitOrderBook, bid: DataTypes.Bid): Option<MutableList<DataTypes.Bid>> {
    return Option.fromNullable(book.bids.remove(bid.price))
  }


  fun getBidsFor(
    book: DataTypes.LimitOrderBook,
    limit: Int
  ): List<MutableMap.MutableEntry<BigDecimal, MutableList<DataTypes.Bid>>> {
    return book.bids.entries.take(limit)

  }
}

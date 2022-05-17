package co.luckywolf.valr.exchange

import arrow.core.Option
import co.luckywolf.valr.exchange.Trade.getQuantityOutstanding
import co.luckywolf.valr.protocol.DataTypes
import co.luckywolf.valr.protocol.DataTypes.zero
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicLong

object Asks {

  private val sequence: AtomicLong = AtomicLong(0L)

  fun getAsksFor(
    book: DataTypes.LimitOrderBook,
    limit: Int
  ): List<MutableMap.MutableEntry<BigDecimal, MutableList<DataTypes.Ask>>> {
    return book.asks.entries.take(limit)
  }

  //match ask to all bids
  fun matchAskToBids(
    book: DataTypes.LimitOrderBook,
    ask: DataTypes.Ask,
  ): List<DataTypes.LimitOrderMatch> {

    val trades = mutableListOf<DataTypes.LimitOrderMatch>()

    if (book.bids.isEmpty())
      return trades

    tailrec fun match(
      n: Int,
      bidPrice: BigDecimal,
      bids: List<DataTypes.Bid>,
      quantityRequired: BigDecimal
    ): List<DataTypes.LimitOrderMatch> {

      when {

        book.bids.isEmpty() -> {
          return trades
        }
        //sell at R5  buy at R10
        ask.price > bidPrice || quantityRequired == zero -> {
          return trades
        }
        n >= book.bids.size -> {
          return trades
        }
        else -> {
          val quantityMatches =
            matchAskQuantityToBidQuantities(quantityRequired, bids)

          //val quantityOutstanding =
          //  getQuantityOutstanding(quantityRequired, quantityMatches)

          trades.add(
            DataTypes.LimitOrderMatch(
              ask.askId,
              DataTypes.Side.ASK,
              ask.price,
              ask.quantity,
              DataTypes.Side.BID,
              bidPrice,
              quantityMatches
            )
          )

          //nasty
          val result = try {
            book.bids.higherEntry(bidPrice).key
            true

          } catch (_: NullPointerException) {
            false
          }

          return if (result) {
            val nextBidPrice = book.bids.higherEntry(bidPrice).key
            val nextBids = book.bids[nextBidPrice]!!

            match(
              n + 1, nextBidPrice, nextBids,
              getQuantityOutstanding(quantityRequired, quantityMatches)
            )
          } else match(
            n + 1, bidPrice, bids,
            getQuantityOutstanding(quantityRequired, quantityMatches)
          )
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

  fun matchAskQuantityToBidQuantities(
    quantityRequired: BigDecimal,
    bids: List<DataTypes.Bid>,
  ): List<DataTypes.QuantityMatch> {

    val quantityMatches = mutableListOf<DataTypes.QuantityMatch>()

    tailrec fun match(
      n: Int,
      remainingRequired: BigDecimal,
    ): List<DataTypes.QuantityMatch> {

      when {
        remainingRequired == zero -> return quantityMatches
        n >= bids.size -> return quantityMatches
        else -> {
          val taken = Trade.takeAvailableQuantityOnOffer(
            quantityRequired = remainingRequired,
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

          return match(n + 1, remainingRequired - taken.taken)
        }
      }
    }
    return match(0, quantityRequired)
  }

  fun reshuffle(
    book: DataTypes.LimitOrderBook,
    ask: DataTypes.Ask,
    matches: List<DataTypes.LimitOrderMatch>
  ): List<DataTypes.LimitOrderMatch> {

    matches.map { match ->

      //each match is effectively a trade
      book.trades.addAll(match.quantityMatches.map {
        DataTypes.LimitOrderTrade(
          orderId = ask.askId,
          tradeId = DataTypes.OrderId(sequence = sequence.incrementAndGet()),
          tradeSide = DataTypes.Side.ASK,
          ask.account,
          price = ask.price,
          quantity = ask.quantity,
          fillSide = DataTypes.Side.BID,
          fillPrice = match.fillPrice,
          fillQuantity = it
        )
      })

      match.quantityMatches.filter { it.left > zero }.forEach { quantityMatch ->
        val price = Option.fromNullable(book.bids[match.fillPrice]?.get(quantityMatch.index))
        price.map {
          book.bids[match.fillPrice]?.set(quantityMatch.index, it.copy(quantity = quantityMatch.left))
        }
      }

      match.quantityMatches.filter { it.left == zero }.forEachIndexed { _, quantityMatch ->
        book.bids[match.fillPrice]?.removeIf { it.bidId.id == quantityMatch.id }
      }

      //if all bids for that price has been filled remove the price entirely
      Option.fromNullable(book.bids[match.fillPrice]).map {
        if (it.isEmpty())
          book.bids.remove(match.fillPrice)
      }
      match
    }

    when {
        matches.isEmpty() -> {
          addAskTo(book, ask)
        }
        else -> {
          val outstanding = getQuantityOutstanding(ask.quantity, matches.flatMap { it.quantityMatches })
          if (outstanding > zero) {
            addAskTo(book, ask.copy(quantity = outstanding))
          } else {
            removeAskFrom(book, ask)
          }
        }
    }
    return matches
  }


  fun removeAskFrom(book: DataTypes.LimitOrderBook, ask: DataTypes.Ask): Option<MutableList<DataTypes.Ask>> {
    return Option.fromNullable(book.asks.remove(ask.price))
  }

  fun addAskTo(book: DataTypes.LimitOrderBook, ask: DataTypes.Ask): DataTypes.Ask {
    book.asks.getOrPut(ask.price) {
      mutableListOf()
    }.add(ask)

    return ask
  }

}



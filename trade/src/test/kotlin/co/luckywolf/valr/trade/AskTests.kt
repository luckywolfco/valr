package co.luckywolf.valr.trade

import arrow.core.some
import co.luckywolf.valr.exchange.Asks.matchAskQuantityToBidQuantities
import co.luckywolf.valr.exchange.Asks.matchAskToBids
import co.luckywolf.valr.exchange.Asks.reshuffle
import co.luckywolf.valr.exchange.Trade
import co.luckywolf.valr.exchange.Trade.getQuantityOutstanding
import co.luckywolf.valr.exchange.Trade.printBookToConsole
import co.luckywolf.valr.protocol.DataTypes
import co.luckywolf.valr.protocol.DataTypes.zero
import co.luckywolf.valr.trade.TestData.ask_100_at_R100
import co.luckywolf.valr.trade.TestData.ask_10_at_R10
import co.luckywolf.valr.trade.TestData.ask_10_at_R5
import co.luckywolf.valr.trade.TestData.ask_14_R6
import co.luckywolf.valr.trade.TestData.ask_29_at_R26
import co.luckywolf.valr.trade.TestData.ask_30_R20
import co.luckywolf.valr.trade.TestData.ask_49_at_R20
import co.luckywolf.valr.trade.TestData.ask_49_at_R30
import co.luckywolf.valr.trade.TestData.ask_50_at_R7
import co.luckywolf.valr.trade.TestData.ask_5_R5
import co.luckywolf.valr.trade.TestData.ask_7_R20
import co.luckywolf.valr.trade.TestData.bid_10_at_R10
import co.luckywolf.valr.trade.TestData.bid_10_at_R20
import co.luckywolf.valr.trade.TestData.bid_12_at_R6
import co.luckywolf.valr.trade.TestData.bid_200_at_20
import co.luckywolf.valr.trade.TestData.bid_24_at_R26
import co.luckywolf.valr.trade.TestData.bid_2_at_R7
import co.luckywolf.valr.trade.TestData.bid_30_R20
import co.luckywolf.valr.trade.TestData.bid_50_at_R100
import co.luckywolf.valr.trade.TestData.bid_5_at_R90
import co.luckywolf.valr.trade.TestData.bid_7_R20
import org.decimal4j.immutable.Decimal2f
import org.decimal4j.util.DoubleRounder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.RoundingMode


class AskTests {

  @Test
  fun double() {
    val z = 0.000000
    val z2: Double = DoubleRounder.round(0.0,6)
    println(z2)
    println("same "  +z.compareTo(z2))
    val bd = DoubleRounder.round(9500.111111,6)
    val bd2 = DoubleRounder.round(9500.111111,6)
    println(bd.compareTo(bd2))
    println(bd.plus(bd2))
    //bd.scale(4)
    //println(bd.scale)
    //val d = DoubleRounder.round(0.434343434.toDouble(),4)
     //bd.setScale(4)
    println(bd)


  }

  @Test
  fun place_bid_order() {

    val book = DataTypes.LimitOrderBook(DataTypes.CurrencyPair.BTCZAR)
    Trade.tryPlaceOrderFor(
      book,
      DataTypes.Order(
        DataTypes.Side.ASK,
        quantity = BigDecimal(0.50000),
        price = BigDecimal(0.50000),
        currencyPair = DataTypes.CurrencyPair.BTCZAR,
        timeInForce = DataTypes.TimeInForce.GTC,
        account = DataTypes.Trader("333"),
      )
    )
    Trade.tryPlaceOrderFor(
      book,
      DataTypes.Order(
        DataTypes.Side.ASK,
        quantity = BigDecimal(0.45),
        price = BigDecimal(0.45),
        currencyPair = DataTypes.CurrencyPair.BTCZAR,
        timeInForce = DataTypes.TimeInForce.GTC,
        account = DataTypes.Trader("333"),
      )
    )

    Assertions.assertEquals(book.asks.size, 2)

    Trade.tryPlaceOrderFor(
      book,
      DataTypes.Order(
        DataTypes.Side.ASK,
        quantity = BigDecimal(0.42),
        price = BigDecimal(0.42),
        currencyPair = DataTypes.CurrencyPair.BTCZAR,
        timeInForce = DataTypes.TimeInForce.GTC,
        account = DataTypes.Trader("333"),
      )
    )

    Assertions.assertEquals(book.asks.size, 3)

    Trade.tryPlaceOrderFor(
      book,
      DataTypes.Order(
        DataTypes.Side.BID,
        quantity = BigDecimal(0.30),
        price = BigDecimal(0.42),
        currencyPair = DataTypes.CurrencyPair.BTCZAR,
        timeInForce = DataTypes.TimeInForce.GTC,
        account = DataTypes.Trader("333"),
      )
    )

    printBookToConsole(book)
  }


  @Test
  fun match_ask_quantity_to_bid_quantities() {

    val matches_a: List<DataTypes.QuantityMatch> =
      matchAskQuantityToBidQuantities(
        ask_50_at_R7.quantity,
        listOf(
          bid_10_at_R20,
          bid_7_R20,
          bid_200_at_20
        )
      )

    //matches_a.forEach { println(it) }

    Assertions.assertEquals(matches_a[0].taken, 10.toBigDecimal())
    Assertions.assertEquals(matches_a[0].left, zero)
    Assertions.assertEquals(matches_a[0].index, 0)

    Assertions.assertEquals(matches_a[1].taken, 7.toBigDecimal())
    Assertions.assertEquals(matches_a[1].left, 0.toBigDecimal())
    Assertions.assertEquals(matches_a[1].index, 1)

    Assertions.assertEquals(matches_a[2].taken, 33.toBigDecimal())
    Assertions.assertEquals(matches_a[2].left, 167.toBigDecimal())
    Assertions.assertEquals(matches_a[2].index, 2)

    Assertions.assertEquals(
      getQuantityOutstanding(ask_50_at_R7.quantity, matches_a), zero
    )

  }

  //
  @Test
  fun add_ask_shuffle() {
    val book = DataTypes.LimitOrderBook(DataTypes.CurrencyPair.BTCZAR)
    book.bids[bid_7_R20.price] = mutableListOf(bid_7_R20, bid_30_R20) //37

    reshuffle(
      book,
      ask_49_at_R20.copy(askId = DataTypes.OrderId(id = "liverpool", sequence = 9)),
      listOf(
        DataTypes.LimitOrderMatch(
          DataTypes.OrderId(id = "liverpool", sequence = 9),
          tradeSide = DataTypes.Side.ASK,
          price = ask_49_at_R20.price,
          quantity = ask_49_at_R20.quantity,
          fillSide = DataTypes.Side.BID,
          fillPrice = 20.toBigDecimal(),
          quantityMatches = listOf(
            DataTypes.QuantityMatch(bid_7_R20.bidId.id, 0, 7.toBigDecimal(), 0.toBigDecimal()),
            DataTypes.QuantityMatch(bid_30_R20.bidId.id, 1, 30.toBigDecimal(), 0.toBigDecimal())
          )
        )
      )
    )
    Assertions.assertEquals(0, book.bids.size)
    Assertions.assertEquals(2, book.trades.size)
    Assertions.assertEquals(1, book.asks.size)

    Assertions.assertEquals(12.toBigDecimal(), book.asks[ask_49_at_R20.price]!![0].quantity)
    Assertions.assertEquals(bid_7_R20.quantity, book.trades[0].fillQuantity.taken)
    Assertions.assertEquals(bid_30_R20.quantity, book.trades[1].fillQuantity.taken)
    printBookToConsole(book)
  }

  @Test
  fun add_ask_with_existing_asks_bids_shuffle() {
    val book = DataTypes.LimitOrderBook(DataTypes.CurrencyPair.BTCZAR)
    book.asks[ask_7_R20.price] = mutableListOf(ask_7_R20, ask_30_R20) //sell R20 -> 7, 30
    book.bids[bid_10_at_R10.price] = mutableListOf(bid_10_at_R10) //buy R10 -> 10
    book.bids[bid_24_at_R26.price] = mutableListOf(bid_24_at_R26) //buy R26 -> 24 (fill)

    reshuffle(
      book,
      ask_5_R5.copy(askId = DataTypes.OrderId(id = "liverpool", sequence = 9)), //sell R5 -> 5
      listOf(
        DataTypes.LimitOrderMatch(
          DataTypes.OrderId(id = "liverpool", sequence = 9),
          tradeSide = DataTypes.Side.ASK,
          price = ask_5_R5.price,
          quantity = ask_5_R5.quantity,
          fillSide = DataTypes.Side.BID,
          fillPrice = 26.toBigDecimal(),
          quantityMatches = listOf(
            DataTypes.QuantityMatch(bid_24_at_R26.bidId.id, 0, 5.toBigDecimal(), 19.toBigDecimal())
          )
        )
      )
    )

    Assertions.assertEquals(2, book.bids.size)
    Assertions.assertEquals(1, book.trades.size)
    Assertions.assertEquals(1, book.asks.size)

    Assertions.assertEquals(19.toBigDecimal(), book.bids[bid_24_at_R26.price]!![0].quantity)

    Assertions.assertEquals(10.toBigDecimal(), book.bids[bid_10_at_R10.price]!![0].quantity)
    Assertions.assertEquals(10.toBigDecimal(), book.bids[bid_10_at_R10.price]!![0].price)


    printBookToConsole(book)
  }

  //
  @Test
  fun add_ask_shuffle_zero_bids() {
    val book = DataTypes.LimitOrderBook(DataTypes.CurrencyPair.BTCZAR)

    reshuffle(
      book,
      ask_49_at_R30.copy(askId = DataTypes.OrderId(id = "liverpool", sequence = 9)),
      listOf(
        DataTypes.LimitOrderMatch(
          DataTypes.OrderId(id = "liverpool", sequence = 9),
          tradeSide = DataTypes.Side.ASK,
          price = 30.toBigDecimal(),
          quantity = 49.toBigDecimal(),
          fillSide = DataTypes.Side.ASK,
          fillPrice = 0.toBigDecimal(),
          quantityMatches = listOf()
        )
      )
    )

    Assertions.assertEquals(0, book.bids.size)
    Assertions.assertEquals(0, book.trades.size)
    Assertions.assertEquals(1, book.asks.size)

    Assertions.assertEquals(49.toBigDecimal(), book.asks[ask_49_at_R30.price]!![0].quantity)

    printBookToConsole(book)
  }

  @Test
  fun zero_ask_shuffle() {
    val book = DataTypes.LimitOrderBook(DataTypes.CurrencyPair.BTCZAR)
    book.bids[bid_7_R20.price] = mutableListOf(bid_7_R20) //match 0
    book.bids[bid_24_at_R26.price] = mutableListOf(bid_24_at_R26) //second -> match all 24
    book.bids[bid_5_at_R90.price] = mutableListOf(bid_5_at_R90) //first -> match all 5


    reshuffle(
      book,
      ask_29_at_R26.copy(askId = DataTypes.OrderId(id = "liverpool", sequence = 9)),
      listOf(
        DataTypes.LimitOrderMatch(
          DataTypes.OrderId(id = "liverpool", sequence = 9),
          tradeSide = DataTypes.Side.ASK,
          price = ask_29_at_R26.price,
          quantity = ask_29_at_R26.quantity,
          fillSide = DataTypes.Side.BID,
          fillPrice = bid_5_at_R90.price,
          quantityMatches = listOf(
            DataTypes.QuantityMatch(bid_5_at_R90.bidId.id, 0, bid_5_at_R90.quantity, 0.toBigDecimal())
          )
        ),
        DataTypes.LimitOrderMatch(
          DataTypes.OrderId(id = "liverpool", sequence = 9),
          tradeSide = DataTypes.Side.ASK,
          price = ask_29_at_R26.price,
          quantity = ask_29_at_R26.quantity,
          fillSide = DataTypes.Side.BID,
          fillPrice = bid_24_at_R26.price,
          quantityMatches = listOf(
            DataTypes.QuantityMatch(bid_24_at_R26.bidId.id, 0, bid_24_at_R26.quantity, 0.toBigDecimal())
          )
        )
      )
    )


    Assertions.assertEquals(0, book.asks.size)
    Assertions.assertEquals(2, book.trades.size)
    Assertions.assertEquals(1, book.bids.size)

    Assertions.assertEquals("liverpool", book.trades[0].orderId.id)
    Assertions.assertEquals("liverpool", book.trades[1].orderId.id)

    Assertions.assertEquals(7.toBigDecimal(), book.bids[20.toBigDecimal()]!![0].quantity)

    printBookToConsole(book)
  }

  @Test
  fun more_bids_than_ask() {

    val book = DataTypes.LimitOrderBook(DataTypes.CurrencyPair.BTCZAR)
    book.bids[bid_12_at_R6.price] = mutableListOf(bid_12_at_R6) //total 12
    book.bids[bid_2_at_R7.price] = mutableListOf(
      bid_2_at_R7,
      bid_2_at_R7
    ) //total 4 (match first)


    val matches_a = matchAskToBids(book, ask_14_R6)

    //matches_a.forEach { println(it) }

    Assertions.assertEquals(2, matches_a.size)

    Assertions.assertEquals(2, matches_a[0].quantityMatches.size)

    Assertions.assertEquals(0, matches_a[0].quantityMatches[0].index)
    Assertions.assertEquals(2.toBigDecimal(), matches_a[0].quantityMatches[0].taken)
    Assertions.assertEquals(zero, matches_a[0].quantityMatches[0].left)

    Assertions.assertEquals(1, matches_a[0].quantityMatches[1].index)
    Assertions.assertEquals(2.toBigDecimal(), matches_a[0].quantityMatches[1].taken)
    Assertions.assertEquals(zero, matches_a[0].quantityMatches[1].left)

    Assertions.assertEquals(1, matches_a[1].quantityMatches.size)

    Assertions.assertEquals(0, matches_a[1].quantityMatches[0].index)
    Assertions.assertEquals(10.toBigDecimal(), matches_a[1].quantityMatches[0].taken)
    Assertions.assertEquals(2.toBigDecimal(), matches_a[1].quantityMatches[0].left)

    Assertions.assertEquals(
      zero,
      getQuantityOutstanding(14.toBigDecimal(),
        matches_a.flatMap { it.quantityMatches })
    )

  }

  @Test
  fun fill_ask_until_max_bid_price() {
    val book = DataTypes.LimitOrderBook(DataTypes.CurrencyPair.BTCZAR)
    book.bids[bid_50_at_R100.price] = mutableListOf(bid_50_at_R100)
    book.bids[bid_10_at_R20.price] = mutableListOf(bid_10_at_R20)

    val matches_a = matchAskToBids(book, ask_100_at_R100)

    matches_a.forEach { println(it) }

    Assertions.assertEquals(1, matches_a.size)
    Assertions.assertEquals(1, matches_a[0].quantityMatches.size)

    Assertions.assertEquals(
      50.toBigDecimal(), getQuantityOutstanding(100.toBigDecimal(),
        matches_a.flatMap { it.quantityMatches })
    )
  }


  @Test
  fun fill_zero_ask_to_bid_price() {
    val book = DataTypes.LimitOrderBook(DataTypes.CurrencyPair.BTCZAR)
    book.bids[bid_2_at_R7.price] = mutableListOf(bid_2_at_R7)

    val matches_a = matchAskToBids(book, ask_10_at_R10)

    matches_a.forEach { println(it) }

    Assertions.assertEquals(0, matches_a.size)

    Assertions.assertEquals(
      10.toBigDecimal(), getQuantityOutstanding(10.toBigDecimal(),
        matches_a.flatMap { it.quantityMatches })
    )
  }

  @Test
  fun match_bid_to_empty_asks() {
    val book = DataTypes.LimitOrderBook(DataTypes.CurrencyPair.BTCZAR)

    val matches_a = matchAskToBids(book, ask_10_at_R5)

    matches_a.forEach { println(it) }

    Assertions.assertEquals(0, matches_a.size)

    Assertions.assertEquals(
      10.toBigDecimal(), getQuantityOutstanding(10.toBigDecimal(),
        matches_a.flatMap { it.quantityMatches })
    )
  }

}

package co.luckywolf.valr.trade

import co.luckywolf.valr.exchange.Trade
import co.luckywolf.valr.exchange.Trade.matchBidQuantityToAskQuantities
import co.luckywolf.valr.exchange.Trade.matchBidToAsks
import co.luckywolf.valr.exchange.Trade.printBook
import co.luckywolf.valr.exchange.Trade.reshuffle
import co.luckywolf.valr.protocol.DataTypes
import co.luckywolf.valr.protocol.DataTypes.zero
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ExchangeTests {

//  @Test
//  fun get_matches() {
//    val matches = matche(
//      DataTypes.Bid(
//        DataTypes.OrderId(sequence = 1),
//        20.toBigDecimal(),
//        10.toBigDecimal(),
//        currencyPair = DataTypes.CurrencyPair.BTCZAR,
//        account = DataTypes.Trader(""),
//      ),
//      listOf(
//        DataTypes.Ask(
//          DataTypes.AskId(sequence = 1),
//          8.toBigDecimal(),
//          10.toBigDecimal(),
//          currencyPair = DataTypes.CurrencyPair.BTCZAR,
//          trader = DataTypes.Trader(""),
//        ),
//        DataTypes.Ask(
//          DataTypes.AskId(sequence = 1),
//          18.toBigDecimal(),
//          10.toBigDecimal(),
//          currencyPair = DataTypes.CurrencyPair.BTCZAR,
//          trader = DataTypes.Trader(""),
//        )
//      )
//    )
//    matches.matches.forEach { println("${it.id} -  ${it.left}") }
//  }

  @Test
  fun place_bid_order() {

    val book = DataTypes.LimitOrderBook(DataTypes.CurrencyPair.BTCZAR)
    Trade.tryExecuteOrderFor(
      book,
      DataTypes.Order(
        DataTypes.Side.BID,
        quantity = BigDecimal(100),
        price = BigDecimal(20),
        currencyPair = DataTypes.CurrencyPair.BTCZAR,
        timeInForce = DataTypes.TimeInForce.GTC,
        account = DataTypes.Trader("333"),
      )
    )
    printBook(book)
  }

  @Test
  fun match_bid_to_ask() {

    val matches: List<DataTypes.QuantityMatch> =
      matchBidQuantityToAskQuantities(
        TestData.bid_50_at_R100,
        listOf(TestData.ask_10_at_R20, TestData.ask_7_R20, TestData.ask_200_at_20)
      )

    matches.forEach { println(it) }

    Assertions.assertEquals(matches[0].taken, 10.toBigDecimal())
    Assertions.assertEquals(matches[0].left, zero)
    Assertions.assertEquals(matches[1].taken, 7.toBigDecimal())
    Assertions.assertEquals(matches[1].left, 0.toBigDecimal())
    Assertions.assertEquals(matches[1].index, 1)
    Assertions.assertEquals(matches[2].taken, 33.toBigDecimal())
    Assertions.assertEquals(matches[2].left, 167.toBigDecimal())
    Assertions.assertEquals(matches[2].index, 2)

  }

  @Test
  fun shuffles() {
    val book = DataTypes.LimitOrderBook(DataTypes.CurrencyPair.BTCZAR)
    book.asks[TestData.ask_7_R20.price] = mutableListOf(TestData.ask_7_R20, TestData.ask_30_R20)

    reshuffle(
      book,
      TestData.bid_49_at_R30,
      listOf(
        DataTypes.LimitOrderMatch(
          DataTypes.OrderId(sequence = 9),
          tradeSide = DataTypes.Side.BID,
          price = 30.toBigDecimal(),
          quantity = 49.toBigDecimal(),
          fillSide = DataTypes.Side.ASK,
          fillPrice = 20.toBigDecimal(),
          quantityMatches = listOf(
            DataTypes.QuantityMatch(TestData.ask_7_R20.askId.id, 0, 7.toBigDecimal(), 0.toBigDecimal()),
            DataTypes.QuantityMatch(TestData.ask_30_R20.askId.id, 1, 42.toBigDecimal(), 0.toBigDecimal())
          )
        )
      )
    )
    book.asks.forEach { t, u: MutableList<DataTypes.Ask> ->
      u.forEach { println(it) }
    }

    book.bids.forEach { t, u: MutableList<DataTypes.Bid> ->
      u.forEach { println(it) }
    }

    book.trades.forEach {
      println(it)
    }
  }

  @Test
  fun match_bid_to_asks() {

    val book = DataTypes.LimitOrderBook(DataTypes.CurrencyPair.BTCZAR)
    book.asks[TestData.ask_7_R20.price] = mutableListOf(TestData.ask_7_R20, TestData.ask_30_R20)
    book.asks[TestData.ask_22_at_R22.price] = mutableListOf(TestData.ask_22_at_R22)

    val matchBidToAsks = matchBidToAsks(book, TestData.bid_49_at_R30)
    matchBidToAsks.forEach { println(it) }

    //println(getQuantityOutstanding(25.toBigDecimal(),matches))

  }

  @Test
  fun matche() {
    val book = DataTypes.LimitOrderBook(
      DataTypes.CurrencyPair.BTCZAR,
    )
    val bid1 = DataTypes.Bid(
      DataTypes.OrderId(sequence = 1),
      BigDecimal(300),
      price = BigDecimal(500),
      DataTypes.CurrencyPair.BTCZAR,
      account = DataTypes.Trader("tag")
    )
    val bid2 = DataTypes.Bid(
      DataTypes.OrderId(sequence = 2),
      BigDecimal(300),
      price = BigDecimal(299),
      DataTypes.CurrencyPair.BTCZAR,
      account = DataTypes.Trader("tag2")
    )
    val bid3 = DataTypes.Bid(
      DataTypes.OrderId(sequence = 3),
      BigDecimal(300),
      price = BigDecimal(299),
      DataTypes.CurrencyPair.BTCZAR,
      account = DataTypes.Trader("tag3")
    )

    val ask1 = DataTypes.Ask(
      DataTypes.OrderId(sequence = 1),
      BigDecimal(300),
      price = BigDecimal(500),
      DataTypes.CurrencyPair.BTCZAR,
      trader = DataTypes.Trader("tag")
    )
    val ask2 = DataTypes.Ask(
      DataTypes.OrderId(sequence = 2),
      BigDecimal(300),
      price = BigDecimal(299),
      DataTypes.CurrencyPair.BTCZAR,
      trader = DataTypes.Trader("tag2")
    )
    val ask3 = DataTypes.Ask(
      DataTypes.OrderId(sequence = 3),
      BigDecimal(300),
      price = BigDecimal(299),
      DataTypes.CurrencyPair.BTCZAR,
      trader = DataTypes.Trader("tag3")
    )

//    val entries = DataTypes.LimitBookEntries()
//    entries.add(bid1)
//    entries.add(bid2)
//    entries.add(bid3)
//
//    entries.add(ask1)
//    entries.add(ask2)
//    entries.add(ask3)
//
//    entries.printBids()
//    entries.printAsks()


  }

  @Test
  fun get_qty() {

//    val t1 = takeMaxQuantityAvailable(20.10.toBigDecimal(), 10.9.toBigDecimal())
//    Assertions.assertEquals(t1.first, 10.9.toBigDecimal())
//    Assertions.assertEquals(t1.second, 0.toBigDecimal())
////
////    println(takeMaxQuantityAvailable(20.toDouble(),20.toDouble()))
//
//    val t2 = takeMaxQuantityAvailable(5.2.toBigDecimal(), 20.6.toBigDecimal())
//    println(t2)
//    Assertions.assertEquals(t2.first, 5.2.toBigDecimal())
//    Assertions.assertEquals(t2.second, 15.4.toBigDecimal())
////
////
////    println(takeMaxQuantityAvailable(5.toBigDecimal(),2.toBigDecimal()))
////    println(takeMaxQuantityAvailable(5.toBigDecimal(),0.toBigDecimal()))
////    println(takeMaxQuantityAvailable(5.toBigDecimal(),6.toBigDecimal()))
//    println(takeMaxQuantityAvailable(0.5.toBigDecimal(), 0.6.toBigDecimal()))
  }

}

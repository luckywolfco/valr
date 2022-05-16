package co.luckywolf.valr.trade

import co.luckywolf.valr.protocol.DataTypes
import java.math.BigDecimal

object TestData {

  val bid_10_at_R10 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(10),
    price = BigDecimal(10),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_14_at_R25 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(14),
    price = BigDecimal(25),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_10_at_R25 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(10),
    price = BigDecimal(25),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_10_at_R10 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(10),
    price = BigDecimal(10),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_3_at_R5 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(3),
    price = BigDecimal(5),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_10_at_R5 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(10),
    price = BigDecimal(5),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_10_at_R5 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(10),
    price = BigDecimal(5),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_50_at_R100 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(50),
    price = BigDecimal(100),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_100_at_R100 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(100),
    price = BigDecimal(100),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_51_at_R100 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(51),
    price = BigDecimal(100),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_50_at_R100 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(50),
    price = BigDecimal(100),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_50_1_at_R100 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 2),
    BigDecimal(50),
    price = BigDecimal(299),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )
  val bid_49_at_R30 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 3),
    BigDecimal(49),
    price = BigDecimal(30),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val ask_49_at_R30 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 3),
    BigDecimal(49),
    price = BigDecimal(30),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val bid_29_at_R26 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 3),
    BigDecimal(29),
    price = BigDecimal(26),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val bid_12_at_R33 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 3),
    BigDecimal(12),
    price = BigDecimal(33),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val ask_12_at_R33 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 3),
    BigDecimal(12),
    price = BigDecimal(33),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val ask_29_at_R26 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 3),
    BigDecimal(29),
    price = BigDecimal(26),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val ask_10_at_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(10),
    price = BigDecimal(20),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_10_at_R20 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(10),
    price = BigDecimal(20),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_2_at_R7 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(2),
    price = BigDecimal(7),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_12_at_R6 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(12),
    price = BigDecimal(6),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_2_at_R7 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(2),
    price = BigDecimal(7),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_22_at_R22 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(22),
    price = BigDecimal(22),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_22_at_R22 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(22),
    price = BigDecimal(22),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_24_at_R26 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(24),
    price = BigDecimal(26),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_5_at_R90 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(5),
    price = BigDecimal(90),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_5_at_R90 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(5),
    price = BigDecimal(90),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_30_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(30),
    price = BigDecimal(20),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_14_R6 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(14),
    price = BigDecimal(6),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val bid_30_R20 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(30),
    price = BigDecimal(20),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_42_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(42),
    price = BigDecimal(20),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_3_R89 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(3),
    price = BigDecimal(89),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val bid_3_R89 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(3),
    price = BigDecimal(89),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_7_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(7),
    price = BigDecimal(20),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val bid_7_R20 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(7),
    price = BigDecimal(20),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_3_R8 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(3),
    price = BigDecimal(8),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_5_R5 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(5),
    price = BigDecimal(5),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_200_at_20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(200),
    price = BigDecimal(20),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_200_at_20 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(200),
    price = BigDecimal(20),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )
}

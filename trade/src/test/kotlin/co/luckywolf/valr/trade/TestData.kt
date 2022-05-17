package co.luckywolf.valr.trade

import co.luckywolf.valr.protocol.DataTypes

object TestData {

  val bid_10_at_R10 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    10.toBigDecimal(),
    price = 10.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_14_at_R25 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    14.toBigDecimal(),
    price = 25.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_10_at_R25 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    10.toBigDecimal(),
    price = 25.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_10_at_R10 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    10.toBigDecimal(),
    price = 10.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_3_at_R5 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    3.toBigDecimal(),
    price = 5.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_10_at_R5 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    10.toBigDecimal(),
    price = 5.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_0_10_at_R5 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    0.10.toBigDecimal(),
    price = 5.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_10_at_R5 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    10.toBigDecimal(),
    price = 5.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_50_at_R100 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    50.toBigDecimal(),
    price = 100.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_100_at_R100 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    100.toBigDecimal(),
    price = 100.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_51_at_R100 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    51.toBigDecimal(),
    price = 100.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_50_at_R100 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    50.toBigDecimal(),
    price = 100.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_50_at_R7 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    50.toBigDecimal(),
    price = 7.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_50_1_at_R100 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 2),
    50.toBigDecimal(),
    price = 299.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )
  val bid_49_at_R30 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 3),
    49.toBigDecimal(),
    price = 30.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val ask_49_at_R30 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 3),
    49.toBigDecimal(),
    price = 30.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val ask_49_at_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 3),
    49.toBigDecimal(),
    price = 20.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val bid_29_at_R26 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 3),
    29.toBigDecimal(),
    price = 26.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val bid_12_at_R33 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 3),
    12.toBigDecimal(),
    price = 33.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val ask_12_at_R33 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 3),
    12.toBigDecimal(),
    price = 33.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val ask_29_at_R26 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 3),
    29.toBigDecimal(),
    price = 26.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val ask_10_at_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    10.toBigDecimal(),
    price = 20.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_10_at_R20 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    10.toBigDecimal(),
    price = 20.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_2_at_R7 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    2.toBigDecimal(),
    price = 7.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_12_at_R6 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    12.toBigDecimal(),
    price = 6.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_2_at_R7 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    2.toBigDecimal(),
    price = 7.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_22_at_R22 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    22.toBigDecimal(),
    price = 22.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_22_at_R22 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    22.toBigDecimal(),
    price = 22.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_24_at_R26 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    24.toBigDecimal(),
    price = 26.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_5_at_R90 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    5.toBigDecimal(),
    price = 90.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_5_at_R90 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    5.toBigDecimal(),
    price = 90.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_30_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    30.toBigDecimal(),
    price = 20.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_0_55_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    0.55.toBigDecimal(),
    price = 20.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_14_R6 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    14.toBigDecimal(),
    price = 6.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val bid_30_R20 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    30.toBigDecimal(),
    price = 20.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_42_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    42.toBigDecimal(),
    price = 20.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_3_R89 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    3.toBigDecimal(),
    price = 89.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val bid_3_R89 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    3.toBigDecimal(),
    price = 89.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_7_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    7.toBigDecimal(),
    price = 20.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val bid_7_R20 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    7.toBigDecimal(),
    price = 20.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_3_R8 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    3.toBigDecimal(),
    price = 8.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_5_R5 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    5.toBigDecimal(),
    price = 5.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_200_at_20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    200.toBigDecimal(),
    price = 20.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_200_at_20 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    200.toBigDecimal(),
    price = 20.toBigDecimal(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )
}

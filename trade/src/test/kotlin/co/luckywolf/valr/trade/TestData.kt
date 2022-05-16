package co.luckywolf.valr.trade

import co.luckywolf.valr.exchange.Trade.toDecimalPlaces
import co.luckywolf.valr.protocol.DataTypes

object TestData {

  val bid_10_at_R10 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    10.toDouble().toDecimalPlaces(),
    price = 10.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_14_at_R25 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    14.toDouble().toDecimalPlaces(),
    price = 25.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_10_at_R25 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    10.toDouble().toDecimalPlaces(),
    price = 25.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_10_at_R10 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    10.toDouble().toDecimalPlaces(),
    price = 10.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_3_at_R5 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    3.toDouble().toDecimalPlaces(),
    price = 5.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_10_at_R5 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    10.toDouble().toDecimalPlaces(),
    price = 5.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_0_10_at_R5 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    0.10.toDouble().toDecimalPlaces(),
    price = 5.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_10_at_R5 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    10.toDouble().toDecimalPlaces(),
    price = 5.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_50_at_R100 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    50.toDouble().toDecimalPlaces(),
    price = 100.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_100_at_R100 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    100.toDouble().toDecimalPlaces(),
    price = 100.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_51_at_R100 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    51.toDouble().toDecimalPlaces(),
    price = 100.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_50_at_R100 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    50.toDouble().toDecimalPlaces(),
    price = 100.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_50_at_R7 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    50.toDouble().toDecimalPlaces(),
    price = 7.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_50_1_at_R100 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 2),
    50.toDouble().toDecimalPlaces(),
    price = 299.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )
  val bid_49_at_R30 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 3),
    49.toDouble().toDecimalPlaces(),
    price = 30.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val ask_49_at_R30 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 3),
    49.toDouble().toDecimalPlaces(),
    price = 30.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val ask_49_at_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 3),
    49.toDouble().toDecimalPlaces(),
    price = 20.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val bid_29_at_R26 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 3),
    29.toDouble().toDecimalPlaces(),
    price = 26.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val bid_12_at_R33 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 3),
    12.toDouble().toDecimalPlaces(),
    price = 33.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val ask_12_at_R33 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 3),
    12.toDouble().toDecimalPlaces(),
    price = 33.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val ask_29_at_R26 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 3),
    29.toDouble().toDecimalPlaces(),
    price = 26.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag3")
  )

  val ask_10_at_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    10.toDouble().toDecimalPlaces(),
    price = 20.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_10_at_R20 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    10.toDouble().toDecimalPlaces(),
    price = 20.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_2_at_R7 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    2.toDouble().toDecimalPlaces(),
    price = 7.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_12_at_R6 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    12.toDouble().toDecimalPlaces(),
    price = 6.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_2_at_R7 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    2.toDouble().toDecimalPlaces(),
    price = 7.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_22_at_R22 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    22.toDouble().toDecimalPlaces(),
    price = 22.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_22_at_R22 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    22.toDouble().toDecimalPlaces(),
    price = 22.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_24_at_R26 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    24.toDouble().toDecimalPlaces(),
    price = 26.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_5_at_R90 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    5.toDouble().toDecimalPlaces(),
    price = 90.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_5_at_R90 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    5.toDouble().toDecimalPlaces(),
    price = 90.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val ask_30_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    30.toDouble().toDecimalPlaces(),
    price = 20.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_0_55_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    0.55.toDouble().toDecimalPlaces(),
    price = 20.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_14_R6 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    14.toDouble().toDecimalPlaces(),
    price = 6.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val bid_30_R20 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    30.toDouble().toDecimalPlaces(),
    price = 20.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_42_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    42.toDouble().toDecimalPlaces(),
    price = 20.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_3_R89 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    3.toDouble().toDecimalPlaces(),
    price = 89.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val bid_3_R89 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    3.toDouble().toDecimalPlaces(),
    price = 89.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_7_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    7.toDouble().toDecimalPlaces(),
    price = 20.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val bid_7_R20 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    7.toDouble().toDecimalPlaces(),
    price = 20.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_3_R8 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    3.toDouble().toDecimalPlaces(),
    price = 8.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_5_R5 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    5.toDouble().toDecimalPlaces(),
    price = 5.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag2")
  )

  val ask_200_at_20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    200.toDouble().toDecimalPlaces(),
    price = 20.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )

  val bid_200_at_20 = DataTypes.Bid(
    DataTypes.OrderId(sequence = 1),
    200.toDouble().toDecimalPlaces(),
    price = 20.toDouble().toDecimalPlaces(),
    DataTypes.CurrencyPair.BTCZAR,
    account = DataTypes.Trader("tag")
  )
}

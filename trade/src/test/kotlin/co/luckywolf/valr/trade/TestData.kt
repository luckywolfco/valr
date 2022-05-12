package co.luckywolf.valr.trade

import co.luckywolf.valr.protocol.DataTypes
import java.math.BigDecimal

object TestData {

  val bid_50_at_R100 = DataTypes.Bid(
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

  val ask_10_at_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(10),
    price = BigDecimal(20),
    DataTypes.CurrencyPair.BTCZAR,
    trader = DataTypes.Trader("tag")
  )

  val ask_22_at_R22 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(22),
    price = BigDecimal(22),
    DataTypes.CurrencyPair.BTCZAR,
    trader = DataTypes.Trader("tag")
  )

  val ask_30_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(30),
    price = BigDecimal(20),
    DataTypes.CurrencyPair.BTCZAR,
    trader = DataTypes.Trader("tag2")
  )

  val ask_7_R20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(7),
    price = BigDecimal(20),
    DataTypes.CurrencyPair.BTCZAR,
    trader = DataTypes.Trader("tag2")
  )

  val ask_200_at_20 = DataTypes.Ask(
    DataTypes.OrderId(sequence = 1),
    BigDecimal(200),
    price = BigDecimal(20),
    DataTypes.CurrencyPair.BTCZAR,
    trader = DataTypes.Trader("tag")
  )
}

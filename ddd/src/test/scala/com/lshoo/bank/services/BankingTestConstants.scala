package com.lshoo.bank.services

import java.util.Currency

import com.lshoo.bank.money.Money

/**
  * Test constants used by more than one test class for tests related to banking.
  */
object BankingTestConstants {
  /* Constant(s): */
  val BANK_ACCOUNT_NUMBER = "123.123"
  val BANK_ACCOUNT_NUMBER_BAD_FORMAT = "123-123"

  val CURRENCY_TWD = Currency.getInstance("TWD")
  val CURRENCY_SEK = Currency.getInstance("SEK")
  val CURRENCY_USD_NOT_REGISTERED = Currency.getInstance("USD")

  val EXCHANGE_RATE_SEK_TWD: BigDecimal = 4.0
  val EXCHANGE_RATE_TWD_SEK: BigDecimal = 5.0

  val MONEY_200_TWD = new Money(200.0, CURRENCY_TWD)
  val MONEY_100_3_TWD = new Money(100.3, CURRENCY_TWD)
  val MONEY_50_1_TWD = new Money(50.1, CURRENCY_TWD)
  val MONEY_50_2_TWD = new Money(50.2, CURRENCY_TWD)
  val MONEY_0_TWD = new Money(0.0, CURRENCY_TWD)

  val MONEY_10_SEK = new Money(10.0, CURRENCY_SEK)
  val MONEY_40_TWD = new Money(40.0, CURRENCY_TWD)
  val MONEY_160_TWD = new Money(160.0, CURRENCY_TWD)
  val MONEY_10_USD_NOT_REGISTERED = new Money(10.0, CURRENCY_USD_NOT_REGISTERED)
}

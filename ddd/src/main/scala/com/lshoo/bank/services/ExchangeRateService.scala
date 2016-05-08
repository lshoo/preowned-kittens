package com.lshoo.bank.services

import java.util.Currency

import com.lshoo.bank.money.Money

import scala.collection.mutable
/**
  * Please doc ...
  */
class ExchangeRateService {

  /* Fields: */
  val exchangeRates: mutable.Map[(Currency, Currency), Seq[BigDecimal]] =
    mutable.Map.empty[(Currency, Currency), Seq[BigDecimal]]

  /**
    *
    * @param from
    * @param to
    * @param rateValue
    */
  def registerExchangeRate(from: Currency, to: Currency, rateValue: BigDecimal): Unit = {
    val theOldRateValuesOption = exchangeRates.get((from, to))
    theOldRateValuesOption match {
      case None =>
        exchangeRates((from, to)) = Seq(rateValue)

      case Some(values) =>
        exchangeRates((from, to)) =  rateValue +: values
    }
  }

  /**
    *
    * @param inMoney
    * @param toCurrency
    * @return
    */
  def exchange(inMoney: Money, toCurrency: Currency): Option[Money] = {
    if (inMoney.currency == toCurrency) Some(inMoney)
    else exchangeRates.get((inMoney.currency, toCurrency)).map { rates =>
      new Money(rates.head * inMoney.amount, toCurrency)
    }
  }


}

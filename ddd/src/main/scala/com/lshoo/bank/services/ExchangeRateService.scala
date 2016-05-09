package com.lshoo.bank.services

import java.util.Currency

import com.lshoo.bank.exceptions.NoExchangeRateRegistered
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
    * @param inAmount
    * @param inToCurrency
    * @return
    */
  def exchange(inAmount: Money, inToCurrency: Currency): Option[Money] = {

    if (inAmount.currency == inToCurrency)
      Some(inAmount)
    /*else if (theExchangeRateOption.isEmpty)
      throw new NoExchangeRateRegistered(
        "No exchange rate registered for exchange from " + inAmount.currency + " to " + inToCurrency
      )*/
    else exchangeRates.get((inAmount.currency, inToCurrency)).map { rates =>
      new Money(rates.head * inAmount.amount, inToCurrency)
    }
  }


}

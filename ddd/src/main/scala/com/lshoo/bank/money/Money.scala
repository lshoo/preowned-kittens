package com.lshoo.bank.money

import java.util.Currency

/**
  * Value object representing an amount of money in a specific currency .
  */
class Money(val amount: BigDecimal, val currency: Currency) extends AnyRef {

  /**
    * Adds supplied money to this instance, creating a new money holding the amount
    * The supplied money must be in the same currency as this instance.
    *
    * @param inMoneyToAdd Money to add to this instance.
    * @return Money holding sum of this instance and supplied money.
    */
  def add(inMoneyToAdd: Money): Money = {
    require(currency ==  inMoneyToAdd.currency, "must add the same currency money")

    val theSum = amount + inMoneyToAdd.amount
    new Money(theSum, currency)
  }

  /**
    * Subtracts supplied money from this instance, creating a new money
    * holding the result.
    * The supplied money must be in the same currency as this instance.
    *
    * @param inMoneyToSubtract Money to subtract from this instance.
    * @return Money holding the difference between this instance and
    * supplied money.
    */
  def subtract(inMoneyToSubtract: Money): Money = {
    require(currency ==  inMoneyToSubtract.currency, "must subtract the same currency money")

    val theDifference = amount - inMoneyToSubtract.amount
    new Money(theDifference, currency)
  }

  /**
    * Compares supplied object with object on which this method
    * is invoked upon.
    *
    * @param inObjectToCompare Object to compare with.
    * @return True if supplied object is an instance of this class and
    * represents the same amount of money. False otherwise.
    */
  override def equals(inObjectToCompare: Any): Boolean = {
    inObjectToCompare match {
      case theMoneyToCompare: Money =>
        (theMoneyToCompare.amount == amount) &&
          (theMoneyToCompare.currency == currency)

      case _ =>
        false
    }
  }

  override def hashCode = 41 * (41 + amount.hashCode()) + currency.hashCode()

  override def toString(): String = {
    "Money: " + currency.getDisplayName + " " + amount
  }
}

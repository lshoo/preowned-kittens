package com.lshoo.bank.account

import java.util.Currency

import com.lshoo.bank.money.Money

/**
  * Represents an account in a bank.
  */
class BankAccount(val currency: Currency) extends Cloneable {

  /* Constructor code: */
  require(currency != null)
  /* Fields: */
  var balance: Money = new Money(0.0, currency)
  var accountNumber: String = null

  /**
    * Withdraws supplied amount from the account.
    *
    * @param inAmount Amount to withdraw. Must be greater then, or equal to, zero.
    */
  def withdraw(inAmount: Money): Unit = {
    require(inAmount.amount >= 0.0, "must withdraw positive amounts")
    require(inAmount.currency == currency, "must withdraw same currency")
    assume(balance.amount - inAmount.amount >= 0.0, "overdrafts not allowed")
    balance = balance.subtract(inAmount)
  }

  /**
    * Deposite supplied amount to the account.
    *
     * @param inAmount Amount to deposit. Must be greater then, or equal to, zero.
    */
  def deposit(inAmount: Money): Unit = {
    require(inAmount.amount >= 0.0, "must deposit positive amounts")
    require(inAmount.currency == currency, "must deposit same currency")
    balance = balance.add(inAmount)
  }

  /*
   * Clones this bank account by performing a deep copy of it.
   */
  override def clone(): BankAccount = {
    val theClone = new BankAccount(currency)
    theClone.accountNumber = accountNumber
    theClone.balance = balance
    theClone
  }
}

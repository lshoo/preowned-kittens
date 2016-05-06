package com.lshoo.bank.account

/**
  * Represents an account in a bank.
  */
class BankAccount extends Cloneable {

  /* Fields: */
  var balance: BigDecimal = 0.0
  var accountNumber: String = null

  /**
    * Withdraws supplied amount from the account.
    *
    * @param inAmout Amount to withdraw. Must be greater then, or equal to, zero.
    */
  def withdraw(inAmout: BigDecimal): Unit = {
    require(inAmout >= 0.0, "must withdraw positive amounts")
    assume(balance - inAmout >= 0.0, "overdrafts not allowed")
    balance = balance - inAmout
  }

  /**
    * Deposite supplied amount to the account.
    *
     * @param inAmount Amount to deposit. Must be greater then, or equal to, zero.
    */
  def deposit(inAmount: BigDecimal): Unit = {
    require(inAmount >= 0.0, "must deposit positive amounts")
    balance = balance + inAmount
  }

  /*
   * Clones this bank account by performing a deep copy of it.
   */
  override def clone(): BankAccount = {
    val theClone = new BankAccount()
    theClone.accountNumber = accountNumber
    theClone.balance = balance
    theClone
  }
}

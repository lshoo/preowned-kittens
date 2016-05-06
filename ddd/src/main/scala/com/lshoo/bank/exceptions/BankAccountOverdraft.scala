package com.lshoo.bank.exceptions

/**
  * Exception that indicates than an attempt to overdraft a bank account was made.
  */
class BankAccountOverdraft(msg: String, cause: Throwable) extends Exception(msg, cause) {
  def this() = this(null, null)
  def this(msg: String) = this(msg, null)
}
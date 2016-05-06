package com.lshoo.bank.exceptions

/**
  * Exception that indicates that an attempt was made to create a bank account with
  * an account number for which there already exist a bank account.
  */
class BankAccountAlreadyExists(msg: String, cause: Throwable) extends Exception(msg, cause) {
  def this() = this(null, null)
  def this(msg: String) = this(msg, null)
}

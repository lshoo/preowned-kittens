package com.lshoo.bank.exceptions

/**
  * Exception that indicates that an attempt was made to perform an operation on
  * a bank account that does not exist.
  */
class BankAccountNotFound(message: String, cause: Throwable) extends Exception(message, cause) {
  def this() = this(null, null)

  def this(msg: String) = this(msg, null)
}

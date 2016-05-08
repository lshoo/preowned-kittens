package com.lshoo.bank.exceptions

/**
  * Exception that indicates than an attempt was made to retrieve an exchange rate that
  * has not been registered with the system.
  */
class NoExchangeRateRegistered(message: String, cause: Throwable) extends Exception(message, cause) {
  def this() = this(null, null)

  def this(message: String) = this(message, null)
}

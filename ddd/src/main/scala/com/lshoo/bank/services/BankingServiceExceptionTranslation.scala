package com.lshoo.bank.services

import java.util.Currency

import com.lshoo.bank.account.BankAccount
import com.lshoo.bank.exceptions.{BankAccountAlreadyExists, BankAccountNotFound, BankAccountOverdraft, NoExchangeRateRegistered}
import com.lshoo.bank.money.Money

/**
  * Implements exception translation for the banking service.
  * Note that all the methods in this trait needs to abstract, since they overrides methods in
  * the BankingServiceMixinInterface trait that are not implemented in any super type of this trait.
  * Methods in the banking service exposed ot clients of the service wraps all unexpected exceptions
  * in Error exceptions, while utility methods only used internally by the service let
  * such exceptions propagate out of the method unwrapped.
  *
  * @see BankingServiceMixinInterface, BankingService
  */
trait BankingServiceExceptionTranslation extends BankingServiceMixinInterface {

  abstract override def registerBankAccount(inNewBankAccount: BankAccount): Unit = {
    try {
      super.registerBankAccount(inNewBankAccount)
    } catch {
      /* Bank account  with supplied account number already exists. */
      case _: AssertionError =>
        throw new BankAccountAlreadyExists(
          "Failed to register new bank account. An account with number" + inNewBankAccount.accountNumber +
          " has already been registered."
        )

      /* Propagate exception indicating bad parameter(s). */
      case theException: IllegalArgumentException =>
        throw theException

        /* Wrap unexpected exceptions. */
      case theException: Throwable =>
        throw new Error("Failed to register new bank account.", theException)
    }
  }

  /**
    * Performs exception translation in connection to querying for the balance of a bank account
    *
    * @param inBankAccountNumber Account number of bank account for which to inquire for balance.
    * @return Balance of the supplied bank account
    */
  abstract override def balance(inBankAccountNumber: String): Money = {
    try {
      /*
      * Note that the return value of the call to the balance
      * method becomes the result of the try-block, which in
      * turn becomes the result of the entire method.
      */
      super.balance(inBankAccountNumber)
    } catch {
      /* Propagate exception indicating bad parameter(s). */
      case theException: IllegalArgumentException =>
        throw theException

      /* Propagate exception indicating bank account not found. */
      case theException: BankAccountNotFound =>
        throw theException

      /* Wrap unexpected exceptions. */
      case theException: Throwable =>
        throw new Error("Failed to register new bank account.", theException)
    }
  }

  /**
    * Performs exception translation in connection to depositing money to a bank account
    *
    * @param inBankAccountNumber Account number of bank account to which to deposit money.
    * @param inAmount Amount of money to deposit to the account.
    */
  abstract override def deposit(inBankAccountNumber: String, inAmount: Money): Unit = {
    try {
      super.deposit(inBankAccountNumber, inAmount)
    } catch {
      /* Propagate exception indicating bad parameter(s). */
      case theException: IllegalArgumentException =>
        throw theException

      /* Propagate exception indicating bank account not found. */
      case theException: BankAccountNotFound =>
        throw theException

      /* Propagate exception indicating no exchange rate registered. */
      case theException: NoExchangeRateRegistered =>
        throw theException

      /* Wrap unexpected exceptions. */
      case theException: Throwable =>
        throw new Error("Failed to register new bank account.", theException)
    }
  }

  /**
    * Performs exception translation in connection to withdraw money from a bank account
    *
    * @param inBankAccountNumber Account number of bank account to which to withdraw money.
    * @param inAmount Amount of money to withdraw from the account.
    */
  abstract override def withdraw(inBankAccountNumber: String, inAmount: Money): Unit = {
    try {
      super.withdraw(inBankAccountNumber, inAmount)
    } catch {
      /* Attempted to overdraft bank account. */
      case _: AssertionError =>
        throw new BankAccountOverdraft(
          "Bank account: " + inBankAccountNumber + ", amount: " + inAmount
        )
      /* Propagate exception indicating bad parameter(s). */
      case theException: IllegalArgumentException =>
        throw theException

      /* Propagate exception indicating bank account not found. */
      case theException: BankAccountNotFound =>
        throw theException

      /* Propagate exception indicating no exchange rate registered. */
      case theException: NoExchangeRateRegistered =>
        throw theException

      /* Wrap unexpected exceptions. */
      case theException: Throwable =>
        throw new Error("Failed to register new bank account.", theException)
    }
  }

  /**
    * Performs exception translation in connection to retrieval of existing bank accounts.
    * Also ensures that a bank account was obtained.
    *
    * @param inBankAccountNumber Account number of bank account to retrieve.
    * @return Option holding bank account with supplied account number.
    */
  abstract override protected def retrieveBankAccount(inBankAccountNumber: String): Option[BankAccount] = {
    try {
      val theBankAccountOption = super.retrieveBankAccount(inBankAccountNumber)
      /* Throw an exception if no bank account found. */
      if (theBankAccountOption.isEmpty) {
        throw new BankAccountNotFound("Unable to find bank account " + inBankAccountNumber)
      }
      theBankAccountOption
    } catch {
      /* Propagate unexpected exceptions. */
      case theException: Throwable =>
        throw theException
    }
  }

  abstract override protected def exchangeMoney(inAmount: Money, inToCurrency: Currency): Option[Money] = {
    try {
      val theExchangedMoneyOption = super.exchangeMoney(inAmount, inToCurrency)
      if (theExchangedMoneyOption.isEmpty)
        throw new NoExchangeRateRegistered(
          "No exchange rate registered for exchange from " + inAmount.currency + " to " + inToCurrency
        )
      theExchangedMoneyOption
    } catch {
      /* Propagate unexpected exceptions. */
      case theException: Throwable =>
        throw theException
    }
  }

  abstract override def transfer(inFromBankAccountNumber: String, inToBankAccountNumber: String, inAmount: Money): Unit = {
    if (inFromBankAccountNumber == inToBankAccountNumber)
      throw new IllegalArgumentException("Cannot transfer money to the same account")

    try {
      super.transfer(inFromBankAccountNumber, inToBankAccountNumber, inAmount)
    } catch {
      /* Attempted to overdraft bank account. */
      case _: AssertionError =>
        throw new BankAccountOverdraft(
          "Bank account: " + inFromBankAccountNumber + ", amount: " + inAmount
        )
      /* Propagate exception indicating bad parameter(s). */
      case theException: IllegalArgumentException =>
        throw theException

      /* Propagate exception indicating bank account not found. */
      case theException: BankAccountNotFound =>
        throw theException

      /* Propagate exception indicating no exchange rate registered. */
      case theException: NoExchangeRateRegistered =>
        throw theException

      /* Wrap unexpected exceptions. */
      case theException: Throwable =>
        throw new Error("Failed to register new bank account.", theException)
    }
  }
}

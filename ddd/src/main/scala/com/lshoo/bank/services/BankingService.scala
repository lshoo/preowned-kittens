package com.lshoo.bank.services

import java.util.Currency

import com.lshoo.bank.account.BankAccount
import com.lshoo.bank.exceptions._
import com.lshoo.bank.money.Money
import com.lshoo.bank.repositories.BankAccountRepository

/**
  * Provides services related to bank account banking.
  */
class BankingService(repository: BankAccountRepository) extends BankingServiceMixinInterface {

  private val ACCOUNT_NUMBER_FORMAT_REGEXP = """[0-9]{3}\.[0-9]{3}""".r

  /**
    * Fields:
    */
  var exchangeRateService: ExchangeRateService = null

  /**
    * Registers the supplied bank account with the service.
    * The account number in the bank account must not have been previously used to register a bank account
    *
    * @param inBankAccount Bank account to register with the service.
    * @throws BankAccountAlreadyExists if a bank account with the same account number already exists.
    * @throws IllegalArgumentException if the supplied bank account's account number is not in a valid format.
    */
  def registerBankAccount(inBankAccount: BankAccount): Unit = {
    validateBankAccountNumberFormat(inBankAccount.accountNumber)

    /*
     * This is a command-type method, so we do not return a result.
     * The method has side-effects in that a bank account is created.
     */
    /* Attempt to create the new bank account in the repository. */
    /*try {
      repository.create(inBankAccount)
    } catch {
      case _: AssertionError =>
        throw new BankAccountAlreadyExists(
          "Failed to register new bank account. An account with number " + inBankAccount +
          " has already been registered."
        )

      case theException: IllegalArgumentException =>
        throw theException

      case theException: Throwable =>
        throw new Error("Failed to register new bank account.",
          theException)
    }*/
    repository.create(inBankAccount)
  }

  /**
    * Inquiries the balance of the bank account with the supplied account number.
    *
    * @param inBankAccountNumber Account number of bank account for which to inquire for balance.
    * @return Balance of the bank account.
    * @throws IllegalArgumentException if the supplied account number is not in a valid format
    * @throws BankAccountNotFound if there is no corresponding bank account for the supplied
    *                             bank account number.
    */
  def balance(inBankAccountNumber: String): Money = {
    validateBankAccountNumberFormat(inBankAccountNumber)
    /*
     * This is a query-type method, so it does not have any side-effects, it is idempotent.
     */
    //val theBankAccountOption = repository.findBankAccountWithAccountNumber(inBankAccountNumber)

    /*
     * Make sure that a bank account was found, else throw exception
     */
    //checkBankAccountFound(theBankAccountOption,
    //  s"Bank account with account number ${inBankAccountNumber} not found when performing balance query.")

    /*
     * Arriving here, we know that we has a bank account and can thus obtain its balance
     */
    val theBankAccountOption = retrieveBankAccount(inBankAccountNumber)
    val theBankAccount = theBankAccountOption.get

    theBankAccount.balance
  }

  /**
    * Deposits the supplied amount of money to bank account with the supplied account number.
    *
    * @param inBankAccountNumber Account number of bank account to which to deposit money.
    * @param inAmount Amount of money to deposit to the account.
    * @throws IllegalArgumentException If the supplied account number is not in a valid format.
    * @throws BankAccountNotFound If there is no corresponding bank account for
    *                             the supplied bank account number
    */
  def deposit(inBankAccountNumber: String, inAmount: Money): Unit = {
    /*
     * The method has side-effects in that the balance of a bank account is updated.
     */
    val theBankAccountOption = retrieveBankAccount(inBankAccountNumber)
    val theBankAccount = theBankAccountOption.get
    //checkBankAccountFound(theBankAccountOption,
    //  "Bank account with account number " + inBankAccountNumber +
    //    " not found when performing deposit.")

    /*
    * Exchange the currency to deposit to the currency of
    * the bank account. The exchange rate service will return
    * the supplied amount if it already is of the desired currency,
    * so it is safe to always perform the exchange operation.
    */

    val theExchangedAmountToDepositOption = exchangeRateService.exchange(inAmount, theBankAccount.currency)
    val theExchangedAmountToDeposit = theExchangedAmountToDepositOption.get
    //hasCurrencyExchangeSucceeded(theExchangeAmountToDepositOption, inAmount.currency, theBankAccount.currency)

    /*
    * Arriving here, we know that we have a bank account,
    * money to deposit in the bank account's currency and can
    * now perform the deposit and update the bank account.
    */
    theBankAccount.deposit(theExchangedAmountToDeposit)

    //repository.update(theBankAccount)
    updateBankAccount(theBankAccount)
  }

  /**
    * Withdraws the supplied amount of money from bank account with the supplied account number.
    *
    * @param inBankAccountNumber Account number of bank account from which to withdraw money.
    * @param inAmount Amount of money to withdraw from the account.
    * @throws IllegalArgumentException If the supplied account number is not in a valid format.
    * @throws BankAccountNotFound      If there is no corresponding bank account for
    *                                  the supplied bank account number
    * @throws BankAccountOverdraft If an attempt was made to overdraft the bank account
    */
  def withdraw(inBankAccountNumber: String, inAmount: Money): Unit = {
    val theBankAccountOption =retrieveBankAccount(inBankAccountNumber)
    val theBankAccount = theBankAccountOption.get
    /* Make sure that a bank account was found, else throw exception. */
    //checkBankAccountFound(theBankAccountOption,
    //  "Bank account with account number " + inBankAccountNumber +
    //   " not found when performing withdrawal.")

    /*
    * Exchange the currency to withdraw to the currency of
    * the bank account. The exchange rate service will do nothing if
    * the supplied amount is of the desired currency, so it is
    * safe to always perform the exchange operation.
    */

    val theExchangedAmountToWithdrawOption = exchangeMoney(inAmount, theBankAccount.currency)
    val theExchangedAmountToWithdraw = theExchangedAmountToWithdrawOption.get
    //hasCurrencyExchangeSucceeded(theExchangedAmountToWithdrawOption, inAmount.currency, theBankAccount.currency)

    /*
    * Arriving here, we know that we have a bank account,
    * money to withdraw in the bank account's currency and can
    * now perform the withdrawal and update the bank account.
    */
    /*try {
      theBankAccount.withdraw(theExchangedAmountToWithdrawOption.get)
    } catch {
      case _ : AssertionError =>
        throw new BankAccountOverdraft(
          "Bank account: " + inBankAccountNumber +
            ", amount: " + inAmount)
      case theException : IllegalArgumentException =>
        /* Just propagate the exception. */
        throw theException
      case theException : Throwable =>
        throw new Error("Failed to register new bank account.",
          theException)
    }

    repository.update(theBankAccount)*/
    theBankAccount.withdraw(theExchangedAmountToWithdraw)
    updateBankAccount(theBankAccount)

  }
  /**
    * Checks the supplied money option (being the result of a money
    * exchange). If it does not contain money, this is taken as an
    * indication that a money exchange has failed due to a missing
    * exchange rate and an exception is thrown.
    */
  /*private def hasCurrencyExchangeSucceeded(
                                            inExchangedMoneyOption : Option[Money],
                                            inFromCurrency : Currency, inToCurrency : Currency) : Unit = {
    inExchangedMoneyOption match {
      case None =>
        val theErrorMsg = "failed currency exchange from " +
          inFromCurrency.getDisplayName() + " to " +
          inToCurrency.getDisplayName()
        throw new NoExchangeRateRegistered(theErrorMsg)
      case _ =>
      /* Exchange succeeded, just continue. */
    }
  }*/

  /**
  * Check the supplied Option object and if it does not contain a bank account,
  * throw an exception with supplied message
  */
  /*private def checkBankAccountFound(inBankAccount: Option[BankAccount], inExceptionMessage: String): Unit = {
    inBankAccount match {
      case None =>
        throw new BankAccountNotFound(inExceptionMessage)

      case _ =>

    }
  }*/

  /**
  * Validates the format of the account number of the supplied bank account,
  * If it is not appropriate format, throw an exception.
  */
  override protected def validateBankAccountNumberFormat(inBankAccountNumber: String): Unit = {
    /*
    * Make sure that the account number is the proper format.
    * If the format is invalid, throws an exception.
    */
    inBankAccountNumber match {
      case ACCOUNT_NUMBER_FORMAT_REGEXP() =>
        /* Good account number, do nothing. */
      case _ =>
        /* Bad account number, throw exception. */
        throw new IllegalArgumentException(
          "Failed to register new bank account. Illegal account number format: " + inBankAccountNumber
        )
    }
  }

  /**
    * Retrieves bank account with supplied account number from bank account repository.
    * This method isolates access to the bank account repository in order to enable us to add
    * error handling, exception translation, logging etc. of access to a repository.
    * Note that we assume only a scenario in which access to be repository is successful.
    * @param inBankAccountNumber Account number of bank account to retrieve
    * @return Option holding bank account with supplied account number, or None if no bank account was found.
    */
  protected def retrieveBankAccount(inBankAccountNumber: String): Option[BankAccount] = {
    repository.findBankAccountWithAccountNumber(inBankAccountNumber)
  }

  /**
    * Updates supplied bank account in the bank account repository.
    * This method isolates access to the bank account repository in order to enable us to add
    * error handling, exception translation, logging etc. of access to a repository.
    * Note that we assume only a scenario in which a bank account is found.
    * @param inBankAccount Bank account to update.
    */
  protected def updateBankAccount(inBankAccount: BankAccount): Unit = {
    repository.update(inBankAccount)
  }

  /**
    * Exchange the supplied amount of money to the supplied currency.
    * This method isolates access to the bank account repository in order to enable us to add
    * error handling, exception translation, logging etc. of access to a repository.
    * Note that we assume only a scenario in which the exchange is successful.
    * @param inAmount Money to exchange
    * @param inToCurrency Currency to exchange money to.
    * @return Option holding exchanged money, or None if no exchange rate registered for the exchange.
    */
  protected def exchangeMoney(inAmount: Money, inToCurrency: Currency): Option[Money] = {
    exchangeRateService.exchange(inAmount, inToCurrency)
  }

  def transfer(inFromBankAccountNumber: String, inToBankAccountNumber: String, inAmount: Money): Unit = {

    val theFromBankAccount = repository.findBankAccountWithAccountNumber(inFromBankAccountNumber).get
    val theToBankAccount = repository.findBankAccountWithAccountNumber(inToBankAccountNumber).get
    theFromBankAccount.withdraw(inAmount)
    theToBankAccount.deposit(inAmount)
    repository.update(theFromBankAccount)
    repository.update(theToBankAccount)
  }
}

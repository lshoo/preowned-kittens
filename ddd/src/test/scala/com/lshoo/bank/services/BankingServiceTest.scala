package com.lshoo.bank.services

import java.util.Currency

import com.lshoo.bank.account.BankAccount
import com.lshoo.bank.exceptions._
import com.lshoo.bank.money.Money
import com.lshoo.bank.repositories.BankAccountRepository
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import BankingTestConstants._

/**
  * I come up with the following list:
  * • It should be possible to create a new bank account with an account number that has not
*previously been used.
 **
 *• It should not be possible to create a bank account with an account number that previously
*has been used.
 **
 *• It should not be possible to create a bank account with an account number that is of illegal
*format.
*For simplicity, we assume that all strings containing just digits and the “.” character are
*valid account numbers.
 **
 *• It should be possible to perform a balance inquiry on an existing bank account.
 **
 *• It should not be possible to perform a balance inquiry using an account number for which
*there is no bank account.
 **
 *• When money is deposited to a bank account, the account balance should increase
*accordingly.
 **
 *• It should not be possible to deposit money using an account number for which there is no
*bank account.
 **
 *• When money is withdrawn from a bank account, the account balance should decrease
*accordingly.
 **
 *• It should not be possible to withdraw money using an account number for which there is no
*bank account.
 **
 *• It should not be possible to overdraft a bank account.
 **
 *Again, note the tests that will cause an error condition in the system. These are included to define
*the behaviour of the system under such circumstances.
  */
/**
  * Tests the <code>BankingService</code> class.
  */
class BankingServiceTest extends FunSuite with BeforeAndAfterEach {


  /* Fields: */
  protected var bankingService: BankingService = null
  protected var newBankAccount: BankAccount = null
  protected var otherBandAccount: BankAccount = null

  override def beforeEach(): Unit = {

    /* Clear the repository, as to leave no bank accounts from earlier tests. */
    val bankingRepository = new BankAccountRepository()

    val exchangeRateService = createExchangeRateService()

    bankingService = new BankingService(bankingRepository) with BankingServiceExceptionTranslation
    bankingService.exchangeRateService = exchangeRateService

    /* Create a new bank account that has not been registered. */
    newBankAccount = new BankAccount(CURRENCY_TWD)
    newBankAccount.accountNumber = BANK_ACCOUNT_NUMBER
    otherBandAccount = new BankAccount(CURRENCY_TWD)
    otherBandAccount.accountNumber = BANK_ACCOUNT_NUMBER_2
  }

  private def createExchangeRateService(): ExchangeRateService = {
    /**
      * Create the exchange rate service and register some exchange rates for known currencies.
      */
    val theExchangeRateService = new ExchangeRateService()
    theExchangeRateService.registerExchangeRate(CURRENCY_TWD, CURRENCY_SEK, EXCHANGE_RATE_TWD_SEK)
    theExchangeRateService.registerExchangeRate(CURRENCY_SEK, CURRENCY_TWD, EXCHANGE_RATE_SEK_TWD)
    theExchangeRateService
  }

  test("It should be possible to create a new bank account with " +
    "an account number that has not previously been used."
  ) {
    bankingService.registerBankAccount(newBankAccount)
  }

  test("It should not be possible to create a new bank account with " +
    "an account number that previously has been used.") {
    bankingService.registerBankAccount(newBankAccount)
    intercept[BankAccountAlreadyExists] {
      bankingService.registerBankAccount(newBankAccount)
    }
  }

  test("It should not be possible to create a new bank account with an " +
    "account number that is of illegal format") {
    val theBankAccountWithBadAccountNumber = new BankAccount(CURRENCY_TWD)
    theBankAccountWithBadAccountNumber.accountNumber = BANK_ACCOUNT_NUMBER_BAD_FORMAT

    intercept[IllegalArgumentException] {
      bankingService.registerBankAccount(theBankAccountWithBadAccountNumber)
    }
  }

  test("It should be possible to perform a balance inquiry on an existing bank account") {
    bankingService.registerBankAccount(newBankAccount)
    val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)

    assert(theBalance == MONEY_0_TWD)
  }

  test("It should not be possible to perform a balance inquiry using an account number " +
    "for which there is no bank account") {
    intercept[BankAccountNotFound] {
      val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)
    }
  }

  test("When money is deposited to a bank account, the account balance should increase accordingly") {
    bankingService.registerBankAccount(newBankAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, MONEY_100_3_TWD)
    val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)

    assert(theBalance == MONEY_100_3_TWD)
  }

  test("It should not be possible to deposit money using an account number " +
    "for which there is no bank account") {
    intercept[BankAccountNotFound] {
      bankingService.deposit(BANK_ACCOUNT_NUMBER, MONEY_50_1_TWD)
    }
  }

  test("When money is withdraw from a bank account, the account balance should decrease accordingly") {
    bankingService.registerBankAccount(newBankAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, MONEY_100_3_TWD)
    bankingService.withdraw(BANK_ACCOUNT_NUMBER, MONEY_50_1_TWD)
    val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)

    assert(theBalance == MONEY_50_2_TWD)
  }

  test("It should not be possible to withdraw money using an account number " +
    " for which there is no bank account") {
    intercept[BankAccountNotFound] {
      bankingService.withdraw(BANK_ACCOUNT_NUMBER, MONEY_100_3_TWD)
    }
  }

  test("It should not be possible to overdraft a bank account") {
    bankingService.registerBankAccount(newBankAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, MONEY_100_3_TWD)

    intercept[BankAccountOverdraft] {
      bankingService.withdraw(BANK_ACCOUNT_NUMBER, MONEY_200_TWD)
    }

    val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)
    assert(theBalance == MONEY_100_3_TWD)
  }

  test("When money in a recognized currency that is not the bank " +
    "account's currency is deposited to a bank account, the " +
    "account balance should increase by the corresponding amount " +
    "in the bank account's currency calculated using the appropriate " +
    "buy exchange rate") {
    /*
    * A currency is considered foreign if it is not same as
    * the currency of the bank account to which the money in
    * that currency deposited or withdrawn.
    */
    bankingService.registerBankAccount(newBankAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, MONEY_10_SEK)
    val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)
    assert(theBalance == MONEY_40_TWD)
  }

  test("When money in a recognized currency that is not the bank " +
    "account's currency is withdrawn from a bank account, the " +
    "account balance should decrease by the corresponding amount " +
    "in the bank account's currency calculated using the appropriate " +
    "sell exchange rate") {
    /*
    * A currency is considered foreign if it is not same as
    * the currency of the bank account to which the money in
    * that currency deposited or withdrawn.
    */
    bankingService.registerBankAccount(newBankAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, MONEY_200_TWD)
    bankingService.withdraw(BANK_ACCOUNT_NUMBER, MONEY_10_SEK)
    val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)
    assert(theBalance == MONEY_160_TWD)
  }

  test("It should not be possible to withdraw money in a currency for " +
    "which no exchange rate has been registered") {
    /*
    * A currency is considered foreign if it is not same as
    * the currency of the bank account to which the money in
    * that currency deposited or withdrawn.
    */
    bankingService.registerBankAccount(newBankAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, MONEY_200_TWD)
    intercept[NoExchangeRateRegistered] {
      bankingService.withdraw(BANK_ACCOUNT_NUMBER,
        MONEY_10_USD_NOT_REGISTERED)
    }
    val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)
    assert(theBalance == MONEY_200_TWD)
  }

  test("It should be possible to transfer money from a bank account to another account") {
    bankingService.registerBankAccount(newBankAccount)
    bankingService.registerBankAccount(otherBandAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, MONEY_200_TWD)
    bankingService.transfer(BANK_ACCOUNT_NUMBER, BANK_ACCOUNT_NUMBER_2, MONEY_160_TWD)

    val fromBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)
    assert(fromBalance == MONEY_40_TWD)
    val toBalance = bankingService.balance(BANK_ACCOUNT_NUMBER_2)
    assert(toBalance == MONEY_160_TWD)
  }

  test("It should not possible to transfer money from a bank account to the same account") {
    bankingService.registerBankAccount(newBankAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, MONEY_200_TWD)
    intercept[IllegalArgumentException] {
      bankingService.transfer(BANK_ACCOUNT_NUMBER, BANK_ACCOUNT_NUMBER,
        MONEY_160_TWD)
    }

    val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)
    assert(theBalance == MONEY_200_TWD)
  }

  test("It should not possible to transfer more than the balance money in their account") {
    bankingService.registerBankAccount(newBankAccount)
    bankingService.registerBankAccount(otherBandAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, MONEY_160_TWD)
    intercept[BankAccountOverdraft] {
      bankingService.transfer(BANK_ACCOUNT_NUMBER, BANK_ACCOUNT_NUMBER_2,
        MONEY_200_TWD)
    }

    val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)
    assert(theBalance == MONEY_160_TWD)
  }
}

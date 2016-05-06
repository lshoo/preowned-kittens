package com.lshoo.bank.services

import com.lshoo.bank.account.BankAccount
import com.lshoo.bank.exceptions._
import com.lshoo.bank.repositories.BankAccountRepository
import org.scalatest.{BeforeAndAfterEach, FunSuite}

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
  /* Constant(s): */
  val BANK_ACCOUNT_NUMBER = "123.123"
  val BANK_ACCOUNT_NUMBER_BAD_FORMAT = "123-123"

  /* Fields: */
  protected var bankingService: BankingService = null
  protected var newBankAccount: BankAccount = null

  override def beforeEach(): Unit = {
    bankingService = new BankingService()

    /* Clear the repository, as to leave no bank accounts from earlier tests. */
    BankAccountRepository.clear()

    /* Create a new bank account that has not been registered. */
    newBankAccount = new BankAccount
    newBankAccount.accountNumber = BANK_ACCOUNT_NUMBER
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
    val theBankAccountWithBadAccountNumber = new BankAccount()
    theBankAccountWithBadAccountNumber.accountNumber = BANK_ACCOUNT_NUMBER_BAD_FORMAT

    intercept[IllegalArgumentException] {
      bankingService.registerBankAccount(theBankAccountWithBadAccountNumber)
    }
  }

  test("It should be possible to perform a balance inquiry on an existing bank account") {
    bankingService.registerBankAccount(newBankAccount)
    val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)

    val theExpectedBalance: BigDecimal = 0.0
    assert(theBalance == theExpectedBalance)
  }

  test("It should not be possible to perform a balance inquiry using an account number " +
    "for which there is no bank account") {
    intercept[BankAccountNotFound] {
      val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)
    }
  }

  test("When money is deposited to a bank account, the account balance should increase accordingly") {
    bankingService.registerBankAccount(newBankAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, 100.3)
    val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)

    val theExpectedBalance = 100.3
    assert(theBalance == theExpectedBalance)
  }

  test("It should not be possible to deposit money using an account number " +
    "for which there is no bank account") {
    intercept[BankAccountNotFound] {
      bankingService.deposit(BANK_ACCOUNT_NUMBER, 1.0)
    }
  }

  test("When money is withdraw from a bank account, the account balance should decrease accordingly") {
    bankingService.registerBankAccount(newBankAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, 100.3)
    bankingService.withdraw(BANK_ACCOUNT_NUMBER, 50.1)
    val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)

    val theExpectedBalance: BigDecimal = 50.2
    assert(theBalance == theExpectedBalance)
  }

  test("It should not be possible to withdraw money using an account number " +
    " for which there is no bank account") {
    intercept[BankAccountNotFound] {
      bankingService.withdraw(BANK_ACCOUNT_NUMBER, 1.0)
    }
  }

  test("It should not be possible to overdraft a bank account") {
    bankingService.registerBankAccount(newBankAccount)
    bankingService.deposit(BANK_ACCOUNT_NUMBER, 100.5)

    intercept[BankAccountOverdraft] {
      bankingService.withdraw(BANK_ACCOUNT_NUMBER, 200.0)
    }

    val theBalance = bankingService.balance(BANK_ACCOUNT_NUMBER)
    val theExpectedBalance: BigDecimal = 100.5
    assert(theBalance == theExpectedBalance)
  }

}

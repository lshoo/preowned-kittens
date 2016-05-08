package com.lshoo.bank.repositories

import java.util.Currency

import com.lshoo.bank.account.BankAccount
import com.lshoo.bank.money.Money
import org.scalatest.{BeforeAndAfterEach, FunSuite}

/**
  * Test the bank account repository ...
  */
class BankAccountRepositoryTest extends FunSuite with BeforeAndAfterEach {

  /* Constants: */
  private val NEW_BANK_ACCOUNTNUMBER = "000.001"
  private val CURRENCY = Currency.getInstance("TWD")
  private val MONEY_100 = new Money(100, CURRENCY)

  /* Fields: */
  var repository: BankAccountRepository = null

  override def beforeEach: Unit = {
    repository = new BankAccountRepository()
  }

  test("It should be possible to create a new bank account using an account number that is " +
    "not assigned to an existing bank account") {
    val theBankAccount = new BankAccount(CURRENCY)
    theBankAccount.accountNumber = NEW_BANK_ACCOUNTNUMBER
    repository.create(theBankAccount)
  }

  test("It should not be possible be create a bank account using an account number for which " +
    " a bank account has already been created") {
    val theFirstBankAccount = new BankAccount(CURRENCY)
    theFirstBankAccount.accountNumber = NEW_BANK_ACCOUNTNUMBER

    val theSecondBankAccount = new BankAccount(CURRENCY)
    theSecondBankAccount.accountNumber = NEW_BANK_ACCOUNTNUMBER

    /* Create first bank account - should succeed. */
    repository.create(theFirstBankAccount)

    /*
     * Create second bank account with the same account number as previous one just created. should fail.
     */
    intercept[AssertionError] {
      repository.create(theSecondBankAccount)
    }
  }

  test("It should be possible to retrieve a bank account that has been created earlier using its account number") {
    val theBankAccount = new BankAccount(CURRENCY)
    theBankAccount.accountNumber = NEW_BANK_ACCOUNTNUMBER

    repository.create(theBankAccount)

    val theBankAccountOption = repository.findBankAccountWithAccountNumber(NEW_BANK_ACCOUNTNUMBER)
    assert(theBankAccountOption.isDefined)
    assert(NEW_BANK_ACCOUNTNUMBER.equals(theBankAccountOption.get.accountNumber))

    /* The find method should return a new instance of BankAccount */
    assert(theBankAccountOption.get ne theBankAccount)
  }

  test("It should not be possible to retrieve a bank account using an" +
    " account number for which no bank account has been created") {
    val theReadBankAccountOption =
      repository.findBankAccountWithAccountNumber(
        NEW_BANK_ACCOUNTNUMBER)
    assert(theReadBankAccountOption.isEmpty)
  }

  test("It should be possible to update a bank account that has been created earlier.") {
    val theBankAccount = new BankAccount(CURRENCY)
    theBankAccount.accountNumber = NEW_BANK_ACCOUNTNUMBER

    repository.create(theBankAccount)

    /* Set a new balance and update the account */
    theBankAccount.balance = MONEY_100
    repository.update(theBankAccount)

    /* Read the bank account and verify the balance */
    val theReadBankAccountOption = repository.findBankAccountWithAccountNumber(NEW_BANK_ACCOUNTNUMBER)

    assert(theReadBankAccountOption.isDefined)
    assert(NEW_BANK_ACCOUNTNUMBER.equals(theReadBankAccountOption.get.accountNumber))
    assert(theReadBankAccountOption.get.balance == MONEY_100)
  }

  test("It should not be possible to update a bank account that has not" +
    " been created earlier") {
    val theBankAccount = new BankAccount(CURRENCY)
    theBankAccount.accountNumber = NEW_BANK_ACCOUNTNUMBER
    intercept[AssertionError] {
      repository.update(theBankAccount)
    }
  }
}

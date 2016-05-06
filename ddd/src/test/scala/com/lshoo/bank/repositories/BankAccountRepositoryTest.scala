package com.lshoo.bank.repositories

import com.lshoo.bank.account.BankAccount
import org.scalatest.{BeforeAndAfterEach, FunSuite}

/**
  * Test the bank account repository ...
  */
class BankAccountRepositoryTest extends FunSuite with BeforeAndAfterEach {

  /* Constants: */
  val NEW_BANK_ACCOUNTNUMBER = "0000-0001"

  /* Fields: */

  override def beforeEach: Unit = {
    BankAccountRepository.clear
  }

  test("It should be possible to create a new bank account using an account number that is " +
    "not assigned to an existing bank account") {
    val theBankAccount = new BankAccount()
    theBankAccount.accountNumber = NEW_BANK_ACCOUNTNUMBER
    BankAccountRepository.create(theBankAccount)
  }

  test("It should not be possible be create a bank account using an account number for which " +
    " a bank account has already been created") {
    val theFirstBankAccount = new BankAccount()
    theFirstBankAccount.accountNumber = NEW_BANK_ACCOUNTNUMBER

    val theSecondBankAccount = new BankAccount()
    theSecondBankAccount.accountNumber = NEW_BANK_ACCOUNTNUMBER

    /* Create first bank account - should succeed. */
    BankAccountRepository.create(theFirstBankAccount)

    /*
     * Create second bank account with the same account number as previous one just created. should fail.
     */
    intercept[AssertionError] {
      BankAccountRepository.create(theSecondBankAccount)
    }
  }

  test("It should be possible to retrieve a bank account that has been created earlier using its account number") {
    val theBankAccount = new BankAccount()
    theBankAccount.accountNumber = NEW_BANK_ACCOUNTNUMBER

    BankAccountRepository.create(theBankAccount)

    val theBankAccountOption = BankAccountRepository.findBankAccountWithAccountNumber(NEW_BANK_ACCOUNTNUMBER)
    assert(theBankAccountOption.isDefined)
    assert(NEW_BANK_ACCOUNTNUMBER.equals(theBankAccountOption.get.accountNumber))

    /* The find method should return a new instance of BankAccount */
    assert(theBankAccountOption.get ne theBankAccount)
  }

  test("It should not be possible to retrieve a bank account using an" +
    " account number for which no bank account has been created") {
    val theReadBankAccountOption =
      BankAccountRepository.findBankAccountWithAccountNumber(
        NEW_BANK_ACCOUNTNUMBER)
    assert(theReadBankAccountOption.isEmpty)
  }

  test("It should be possible to update a bank account that has been created earlier.") {
    val theBankAccount = new BankAccount()
    theBankAccount.accountNumber = NEW_BANK_ACCOUNTNUMBER

    BankAccountRepository.create(theBankAccount)

    /* Set a new balance and update the account */
    theBankAccount.balance = 100.0
    BankAccountRepository.update(theBankAccount)

    /* Read the bank account and verify the balance */
    val theReadBankAccountOption = BankAccountRepository.findBankAccountWithAccountNumber(NEW_BANK_ACCOUNTNUMBER)

    assert(theReadBankAccountOption.isDefined)
    assert(NEW_BANK_ACCOUNTNUMBER.equals(theReadBankAccountOption.get.accountNumber))
    assert(theReadBankAccountOption.get.balance == 100.0)
  }

  test("It should not be possible to update a bank account that has not" +
    " been created earlier") {
    val theBankAccount = new BankAccount()
    theBankAccount.accountNumber = NEW_BANK_ACCOUNTNUMBER
    intercept[AssertionError] {
      BankAccountRepository.update(theBankAccount)
    }
  }
}

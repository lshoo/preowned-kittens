package com.lshoo.bank.repositories

import java.util.Date

import com.lshoo.bank.money.Money
import com.lshoo.bank.services.BankingTestConstants._
import com.lshoo.bank.transactions.{DepositTransactionHistoryEntry, RegistrationTransactionHistoryEntry, TransactionHistoryEntry}
import org.scalatest.{BeforeAndAfterEach, FunSuite}

/**
  * Test the transaction history repository.
  */
class TransactionHistoryRepositoryTest extends FunSuite with BeforeAndAfterEach {
  /* Constants: */
  private val BANK_ACCOUNT_1 = "123.123"

  /* Fields: */
  var transactionHistoryRepository: TransactionHistoryRepository = null

  override def beforeEach(): Unit = {
    transactionHistoryRepository = new TransactionHistoryRepository()
  }

  test("It should not be possible to insert a transaction history entry without a bank account number") {
    val theNewEntry = new RegistrationTransactionHistoryEntry(new Date, null)

    intercept[IllegalArgumentException] {
      transactionHistoryRepository.create(theNewEntry)
    }

    val theRetrievedEntries: Seq[TransactionHistoryEntry] = transactionHistoryRepository.read(BANK_ACCOUNT_1)
    assert(theRetrievedEntries.size == 0)
  }

  test("It should not be possible to retrieve a transaction history data without supplying a bank account number") {
    intercept[IllegalArgumentException] {
      transactionHistoryRepository.read(null)
    }
  }

  test("It should be possible to create a transaction history entry for a bank account in the repository") {
    createRegistrationEntry()
  }

  test("It should be possible to retrieve an empty list of transaction history entries for a bank account" +
    " for which no entries have been created") {
    val theRetrievedEntries: Seq[TransactionHistoryEntry] = transactionHistoryRepository.read(BANK_ACCOUNT_1)
    assert(theRetrievedEntries.size == 0)
  }

  test("It should be possible to retrieve a list of transaction history entries for bank account " +
    " for which entries have been created") {
    /* Bank account registration */
    val theRegistrationHistory = createRegistrationEntry()

    /* Delay to ensure entries get different timestamp. */
    Thread.sleep(100)

    /* Deposit of TWD 100. */
    val theDepositMoney = new Money(100.0, CURRENCY_TWD)
    val theDepositHistory = new DepositTransactionHistoryEntry(new Date(), BANK_ACCOUNT_1, theDepositMoney)
    transactionHistoryRepository.create(theDepositHistory)

    val theRetrievedEntries: Seq[TransactionHistoryEntry] = transactionHistoryRepository.read(BANK_ACCOUNT_1)
    println(theRetrievedEntries)
    assert(theRetrievedEntries.size == 2)
    assert(theRetrievedEntries.head == theDepositHistory)
    assert(theRetrievedEntries.last == theRegistrationHistory)
  }

  test("It should not be possible to create a null transaction history list entry") {
    intercept[IllegalArgumentException] {
      transactionHistoryRepository.create(null)
    }

    val theRetrievedEntries: Seq[TransactionHistoryEntry] = transactionHistoryRepository.read(BANK_ACCOUNT_1)
    assert(theRetrievedEntries.size == 0)
  }

  /**
    * Creates a registration entry for a bank account in the repository under test.
    */
  private def createRegistrationEntry(): TransactionHistoryEntry = {
    val theNewEntry = new RegistrationTransactionHistoryEntry(new Date(), BANK_ACCOUNT_1)
    transactionHistoryRepository.create(theNewEntry)
    theNewEntry
  }
}

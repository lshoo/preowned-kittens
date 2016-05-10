package com.lshoo.bank.repositories

import com.lshoo.bank.transactions.TransactionHistoryEntry

import scala.collection.mutable

/**
  * Implements a repository for transaction history entries.
  * The entity identifier for a list of transaction history entries is a bank account number.
  * Transaction history entries are immutable.
  * Clients receive an immutable list of the actual entries in the repository
  */
class TransactionHistoryRepository {

  /* Fields: */
  private val transactionHistoryEntries: mutable.Map[String, Seq[TransactionHistoryEntry]] =
    mutable.Map()

  /**
    * Creates the supplied transaction history entry in the repository
    *
    * @param inEntry Transaction history entry which to create in the repository.
    *                Must not be null and must contain a bank account number
    */
  def create(inEntry: TransactionHistoryEntry): Unit = {
    require(inEntry != null, "Cannot insert null entries")
    require(inEntry.bankAccountNumber != null,
      "transaction history entry must contain a bank account number")

    val theBankAccountNumber = inEntry.bankAccountNumber
    val theExistingEntries = findOrCreateEntriesForAccount(theBankAccountNumber)
    transactionHistoryEntries(theBankAccountNumber) = inEntry +: theExistingEntries
  }

  def read(inBankAccountNumber: String): Seq[TransactionHistoryEntry] = {
    require(inBankAccountNumber != null, "must be supplied bank account number")
    findOrCreateEntriesForAccount(inBankAccountNumber)
  }

  private def findOrCreateEntriesForAccount(inBankAccountNumber: String): Seq[TransactionHistoryEntry] = {
    transactionHistoryEntries.getOrElseUpdate(inBankAccountNumber, Seq())
  }
}

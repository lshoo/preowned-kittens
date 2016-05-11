package com.lshoo.bank.services

import com.lshoo.bank.repositories.TransactionHistoryRepository
import com.lshoo.bank.transactions.TransactionHistoryEntry

/**
  * Allows for storage and retrieve of transaction history entries for bank accounts.
  */
class TransactionHistoryService(transactionHistoryRepository: TransactionHistoryRepository) {

  /**
    * Retrieves the transaction history for the bank account with
    * the supplied account number.
    *
    * @param inBankAccountNumber Account number of bank account which
    * transaction history to retrieve.
    * @return Chronologically ordered list containing the bank account's
    * transaction history.
    */
  def retrieveTransactionHistory(inBankAccountNumber: String): Seq[TransactionHistoryEntry] = {
    transactionHistoryRepository.read(inBankAccountNumber)
  }

  /**
    * Adds the supplied transaction history entry to the transaction
    * history entries of the bank account which account number is
    * specified in the entry.
    *
    * @param inTransactionHistoryEntry Transaction history entry to be
    * added.
    */
  def addTransactionHistoryEntry(inTransactionHistoryEntry: TransactionHistoryEntry): Unit = {
    transactionHistoryRepository.create(inTransactionHistoryEntry)
  }
}

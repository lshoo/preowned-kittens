package com.lshoo.bank.transactions

import java.util.Date

import com.lshoo.bank.money.Money

/**
  * Base class for the different kinds of entries that can occur in the transaction history of a bank account.
  * Transaction history entries are immutable Value Object.
  */

abstract class TransactionHistoryEntry(
                                      val timestamp: Date,
                                      val bankAccountNumber: String
                                      )

/**
  * Entry in transaction history of a bank account that describes a withdrawal in bank account's currency,
  * that is non-foreign currency, from the bank account.
  * @param timestamp
  * @param bankAccountNumber
  * @param amount
  */
class WithdrawTransactionHistoryEntry(
                                     override val timestamp: Date,
                                     override val bankAccountNumber: String,
                                     val amount: Money
                                     ) extends TransactionHistoryEntry(timestamp, bankAccountNumber)

/**
  * Entry in transaction history of a bank account that describes a withdrawal of foreign
  * currency from a bank account
  */
class ForeignCurrencyWithdrawalTransactionEntry(
                                               override val timestamp: Date,
                                               override val bankAccountNumber: String,
                                               val foreignCurrencyAmount: Money,
                                               override val amount: Money,
                                               val exchangeRate: BigDecimal
                                               ) extends WithdrawTransactionHistoryEntry(timestamp, bankAccountNumber, amount)

/**
  * Entry in transaction history of a bank account that describes the registration of the bank account.
  */
class RegistrationTransactionHistoryEntry(
                                  override val timestamp: Date,
                                  override val bankAccountNumber: String
                                  ) extends TransactionHistoryEntry(timestamp, bankAccountNumber)


class BalanceInquiryTransactionHistoryEntry(
                                           override val timestamp: Date,
                                           override val bankAccountNumber: String
                                           ) extends TransactionHistoryEntry(timestamp, bankAccountNumber)

class DepositTransactionHistoryEntry(
                                    override val timestamp: Date,
                                    override val bankAccountNumber: String,
                                    val amount: Money
                                    ) extends TransactionHistoryEntry(timestamp, bankAccountNumber)

class ForeignCurrencyDepositTransactionHistoryEntry(
                                            override val timestamp: Date,
                                            override val bankAccountNumber: String,
                                            val foreignCurrencyAmount: Money,
                                            override val amount: Money,
                                            val exchangeRate: BigDecimal
                                            ) extends DepositTransactionHistoryEntry(timestamp, bankAccountNumber, amount)


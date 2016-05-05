package com.lshoo.bank.services

import com.lshoo.bank.account.BankAccount
import com.lshoo.bank.exceptions.{BankAccountAlreadyExists, BankAccountNotFound, BankAccountOverdraft}

/**
  * Provides services related to bank account banking.
  */
class BankingService {

  /**
    * Registers the supplied bank account with the service.
    * The account number in the bank account must not have been previously used to register a bank account
    *
    * @param inBankAccount Bank account to register with the service.
    * @throws BankAccountAlreadyExists if a bank account with the same account number already exists.
    * @throws IllegalArgumentException if the supplied bank account's account number is not in a valid format.
    */
  def registerBankAccount(inBankAccount: BankAccount): Unit = {
    /*
     * This is a command-type method, so we do not return a result.
     * The method has side-effects in that a bank account is created.
     */
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
  def balance(inBankAccountNumber: String): BigDecimal = {
    /*
     * This is a query-type method, so it does not have any side-effects, it is idempotent.
     */
    0.0
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
  def deposit(inBankAccountNumber: String, inAmount: BigDecimal): Unit = {
    /*
     * The method has side-effects in that the balance of a bank account is updated.
     */
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
  def withdraw(inBankAccountNumber: String, inAmount: BigDecimal): Unit = {

  }
}

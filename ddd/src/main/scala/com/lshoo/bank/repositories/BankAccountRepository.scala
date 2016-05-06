package com.lshoo.bank.repositories

import com.lshoo.bank.account.BankAccount

import scala.collection.mutable

/**
  * Implements a repository for bank account .
  * The entity identifier for a bank account is always the account number, which muse unique.
  * The repository retains copies of bank accounts so any bank account supplied by a client may
  * be freely modified without affecting the contents of the repository.
  */
object BankAccountRepository {

  private val accounts = mutable.Map.empty[String, BankAccount]

  def create(inNewBankAccount: BankAccount): Unit = {
    /* Throws IllegalArgumentException if no bank account supplied */
    require(inNewBankAccount != null, "a bank account must be supplied.")

    /*
     * Throws AssertionError if bank account with the same account number as the new bank account already exists.
     */
    assume(accounts.contains(inNewBankAccount.accountNumber) == false,
      s"a bank account with the account number ${inNewBankAccount.accountNumber} already exists.")

    val theNewBankAccount = inNewBankAccount.clone()
    accounts(theNewBankAccount.accountNumber) = theNewBankAccount
  }

  /**
    * Finds a bank account with the supplied account number.
    *
    * @param inBankAccountNumber Account number of bank account to find.
    * @return Bank account with supplied account number, or None if no such
    * account found.
    */
  def findBankAccountWithAccountNumber(inBankAccountNumber: String): Option[BankAccount] = {
    /*
     * Throws IllegalArgumentException if no bank account number supplied.
     */
    require(inBankAccountNumber != null, "a bank account number is required")

    var theBankAccountOption = accounts.get(inBankAccountNumber)
    if (theBankAccountOption.isDefined) {
      /*
       * Clone the bank accoun, so that an instance in the repository is never  returned to a client.
       */
      val theCloneBankAccount = theBankAccountOption.get.clone()
      theBankAccountOption = Option(theCloneBankAccount)
    }

    theBankAccountOption
  }

  /**
    * Updates a bank account that has previously been persisted.
    * The bank account that is to be updated must exist in the repository.
    * @param inBankAccount Bank account to update
    */
  def update(inBankAccount: BankAccount): Unit = {
    /* Throws IllegalArgumentException if no bank account supplied. */
    require(inBankAccount != null, "a bank account must be supplied")

    assume (accounts.contains(inBankAccount.accountNumber),
      s"cannot update bank account with account number ${inBankAccount.accountNumber} since it does not exists")

    val theCloneBankAccount = inBankAccount.clone()
    accounts(theCloneBankAccount.accountNumber) = theCloneBankAccount
  }

  def clear(): Unit = accounts.clear()
}

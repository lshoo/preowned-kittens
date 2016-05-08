package com.lshoo.bank.account

import java.util.Currency

import com.lshoo.bank.money.Money
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}

/**
  * Before writing any actual code, I personally like to stop for a moment and remind myself that when
  * implementing tests, I have found it beneficial to keep in mind that test implementation also is partly
  * interface design – the interface of the class(es) to be tested.
  * Some questions to keep in mind are:

  * • Is the class to be tested a value object (an object that contains data but has no identity,
  * representing things like a date or an amount of money)?
  * If so, then it should be immutable.

  * • Is the class to be tested an entity (an object that has an identity that is unique in the system)?
  * If this is the case then it may be mutable.

  * • Is the class to be tested an aggregate root?
  * An aggregate root is a façade to a set of objects in the system under development that are
  * treated as one unit – an aggregate. An aggregate should have a clear boundary.
  * A class representing a car is a good example. An instance of the car class may refer to four
  * instances of the class representing a wheel, one instance of a class representing the engine
  * etc.

  * • How would I like the interface to be as a user of the class?
  * Overlaps with the next point, but also covers issues like granularity of operations in the class
  * that is to be tested etc.

  * • Will the client code using the class be easy to read and understand?
  * The following two code fragments are examples of client-code using two different
  * implementations of a bank account class with the first example being, in my opinion, easier
  * to understand:
  * 1.testedBankAccount.deposit(100.0)
  * testedBankAccount.withdraw(90.0)
  * 2.testedBankAccount.put(100.0)
  * testedBankAccount.minus(90.0)
  * Names of classes and methods are to be chosen so that they match terms used in the
  * language used when communicating with stakeholders of the system under development.
  * This is what in domain-driven design literature commonly is referred to as the
  * ubiquitous language.
  * Given that this is an example in a book and that we do not actually have a customer with
  * which to discuss, I will nevertheless pretend that there is a ubiquitous language.

  * • Is a method of the class that is tested by the test a command or a query methods?
  * Query methods do not alter the state of an object or an underlying system, while command
  * methods do. Separate command and query methods so that each method is either a
  * command or a query method. If a method is to operate on some data to produce a result, a
  * better alternative is to make a copy of the supplied data which is then processed and returned
  * as a method result instead of altering the supplied data.

  * • How would I like a method to report errors?
  * For example, instead of throwing a NullPointerException, it would be much more
  * informative if a method threw an IllegalArgumentException with a message indicating the
  * violating parameter.
  */

/**
  * Test the <code>BankAccount</code> class.
  */
//@RunWith(classOf[JUnitRunner])
class BankAccountTest extends FunSuite with BeforeAndAfterEach {

  /**
    * Constants:
    */
  private val CURRENCY = Currency.getInstance("TWD")
  private val MONEY_100 = new Money(100, CURRENCY)
  private val MONEY_10 = new Money(10.0, CURRENCY)
  private val MONEY_0 = new Money(0, CURRENCY)

  /**
    * Fields:
    */
  var testedBankAccount: BankAccount = null

  override def beforeEach(): Unit = {
    testedBankAccount = new BankAccount(CURRENCY)
  }

  test("A new bank account should have a zero balance") {
    val theBalance = testedBankAccount.balance
    assert(theBalance == MONEY_0)
  }

  test("When money is deposited to a bank account, the balance should be increase accordingly") {
    testedBankAccount.deposit(MONEY_10)

    val theBalance = testedBankAccount.balance
    assert(theBalance == MONEY_10)
  }

  test("It should not be possible to deposit a negative amount of money" +
    " to a bank account") {
    val theDepositedAmount = new Money(-10.0, CURRENCY)
    intercept[IllegalArgumentException] {
      testedBankAccount.deposit(theDepositedAmount)

      /** Should not arrive here . */
      println("DON'T GO HERE!")
      assert(false)
    }

    val theBalance = testedBankAccount.balance
    assert(theBalance == MONEY_0)
  }

  test("When money is withdrawn from a bank account, the balance should decrease accordingly")  {
    val the90Money = new Money(90, CURRENCY)
    testedBankAccount.deposit(MONEY_100)
    testedBankAccount.withdraw(the90Money)

    val theBalance = testedBankAccount.balance
    assert(theBalance == MONEY_10)
  }

  test("It should not be possible to withdraw a negative amount of money from a bank account") {
    val theNegativeMoney = new Money(-10.0, CURRENCY)
    testedBankAccount.deposit(MONEY_100)

    intercept[IllegalArgumentException] {
      testedBankAccount.withdraw(theNegativeMoney)

      /* Should not arrive here . */
      assert(false)
    }

    val theBalance = testedBankAccount.balance
    assert(theBalance == MONEY_100)
  }

  test("It should not be possible to overdraft a bank account") {
    val the30Money = new Money(30.0, CURRENCY)
    testedBankAccount.deposit(MONEY_10)

    intercept[AssertionError] {
      testedBankAccount.withdraw(the30Money)

      /** Should not arrive here . */
      assert(false)
    }

    val theBalance = testedBankAccount.balance
    assert(theBalance == MONEY_10)
  }

  test("It should be possible to clone a bank account") {
    val theAccountNumber = "123.456"
    val theBalance = MONEY_10

    testedBankAccount.accountNumber = theAccountNumber
    testedBankAccount.balance = theBalance

    val theBankAccountClone: BankAccount = testedBankAccount.clone()

    assert(theBankAccountClone != null)
    assert(theBankAccountClone.accountNumber == theAccountNumber)
    assert(theBankAccountClone.balance == theBalance)
    assert(theBankAccountClone.currency == CURRENCY)
    assert(theBankAccountClone ne testedBankAccount)
  }

  test("A new bank account should have a currency") {
    val theTestedAccountCurrency = testedBankAccount.currency
    assert(theTestedAccountCurrency != null)
  }


}

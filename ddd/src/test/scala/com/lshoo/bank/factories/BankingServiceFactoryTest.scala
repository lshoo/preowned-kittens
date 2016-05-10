package com.lshoo.bank.factories

import com.lshoo.bank.account.BankAccount
import com.lshoo.bank.services.BankingServiceTest
import com.lshoo.bank.services.BankingTestConstants._

/**
  * Tests an instance of the banking service, as created by the <code>BankingServiceFactory</code> factory.
  */
class BankingServiceFactoryTest extends BankingServiceTest {

  override def beforeEach(): Unit = {
    bankingService = BankingServiceFactory.createInstance()

    /**
      * Register some known exchange rates with the exchange rate service of the banking service.
      */
    val theExchangeRateService = bankingService.exchangeRateService
    theExchangeRateService.registerExchangeRate(
      CURRENCY_TWD, CURRENCY_SEK, EXCHANGE_RATE_TWD_SEK
    )
    theExchangeRateService.registerExchangeRate(
      CURRENCY_SEK, CURRENCY_TWD, EXCHANGE_RATE_SEK_TWD
    )

    newBankAccount = new BankAccount(CURRENCY_TWD)
    newBankAccount.accountNumber = BANK_ACCOUNT_NUMBER
  }
}

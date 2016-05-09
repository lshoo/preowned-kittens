package com.lshoo.bank.factories

import com.lshoo.bank.repositories.BankAccountRepository
import com.lshoo.bank.services.{BankingService, BankingServiceExceptionTranslation, ExchangeRateService}

/**
  * Factory that created and configures instance of banking service.
  */
object BankingServiceFactory {

  def createInstance(): BankingService = {
    val repository = new BankAccountRepository()

    /**
      * Create a new instance of the banking service.
      * Notice how the exception translation trait is mixed in at creation time and
      * the dependency to the exception translation trait is hidden from client of this factory.
      */
    val theNewBankingService = new BankingService(repository) with BankingServiceExceptionTranslation

    /**
      * Create an exchange rate service and inject it into the new banking service.
      * Note that clients of this factory are not  required to have a dependency on the exchange rate
      * service.
      * This dependency is encapsulated in the banking service.
      */
    val theExchangeRateService = new ExchangeRateService()
    theNewBankingService.exchangeRateService = theExchangeRateService

    theNewBankingService
  }
}

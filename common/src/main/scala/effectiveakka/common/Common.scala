package effectiveakka.common

import akka.actor.Actor

class Common {

  type Balance = (Long, BigDecimal)
  type Balances = List[Balance]

  case class GetCustomerAccountBalance(id: Long)
  case class AccountBalances (
                               checking: Option[Balances],
                               savings: Option[Balances],
                               moneyMarket: Option[Balances]
                             )

  case class CheckingAccountBalances(
                                      balances: Option[Balances]
                                    )

  case class SavingsAccountBalances(
                                   balances: Option[Balances]
                                   )

  case class MonkeyMarketAccountBalances(
                                     balances: Option[Balances]
                                   )

  trait CheckingAccountProxy extends Actor
  trait SavingsAccountProxy extends Actor
  trait MoneyMarketAccountProxy extends Actor
}


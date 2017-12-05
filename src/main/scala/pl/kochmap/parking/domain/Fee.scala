package pl.kochmap.parking.domain

import pl.kochmap.parking.domain.Currency.Currency

class Fee(val baseAmount: Double, val currencySnapshot: CurrencySnapshot) {
  val amountInCurrency: Double = currencySnapshot.currency.formater(
    baseAmount * currencySnapshot.exchangeRate)
}

object Currency extends Enumeration {
  protected case class Val(formater: Double => Double) extends super.Val
  implicit def valueToCurrencyVal(x: Value): Val = x.asInstanceOf[Val]
  type Currency = Value
  val PLN = Val(d => Math.round(d * 100.0d) / 100.0d)
}

case class CurrencySnapshot(exchangeRate: Double, currency: Currency)

object CurrencySnapshot {
  val constant1To1ExchangeRatePlnCurrencySnapshot = CurrencySnapshot(1.0d, Currency.PLN)
}

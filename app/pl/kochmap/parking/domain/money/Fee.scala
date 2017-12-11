package pl.kochmap.parking.domain.money

import pl.kochmap.parking.domain.money.Currency.Currency
import pl.kochmap.parking.domain.money.FeeTariff.FeeTariff
import pl.kochmap.parking.repository.FeeRow

case class Fee(id: Option[Long],
               baseAmount: Double,
               currencySnapshot: CurrencySnapshot,
               feeTariff: FeeTariff,
               parkingTicketId: Option[Long]) {

  val amountInCurrency: Double = currencySnapshot.currency.formater(
    baseAmount * currencySnapshot.exchangeRate)

  def this(feeRow: FeeRow) = {
    this(feeRow.id,
         feeRow.baseAmount,
         CurrencySnapshot(feeRow.exchangeRate, feeRow.currency),
         feeRow.feeTariff,
         feeRow.parkingTicketId)
  }

  def toFeeRow: FeeRow =
    FeeRow(id,
           baseAmount,
           currencySnapshot.exchangeRate,
           currencySnapshot.currency,
           amountInCurrency,
           feeTariff,
           parkingTicketId)
}

object Currency extends Enumeration {

  protected case class Val(formater: Double => Double) extends super.Val

  implicit def valueToCurrencyVal(x: Value): Val = x.asInstanceOf[Val]

  type Currency = Value
  val PLN = Val(d => Math.ceil(d * 100.0d) / 100.0d)
}

case class CurrencySnapshot(exchangeRate: Double, currency: Currency) {
  def convert(baseAmount: Double): Double = exchangeRate * baseAmount
}

object CurrencySnapshot {
  def apply(currency: Currency): CurrencySnapshot = currency match {
    case Currency.PLN => CurrencySnapshot(1.0d, Currency.PLN)
  }
}

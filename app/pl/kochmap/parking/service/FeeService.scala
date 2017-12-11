package pl.kochmap.parking.service

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import pl.kochmap.parking.domain.money.Currency.Currency
import pl.kochmap.parking.domain.money.{Currency, CurrencySnapshot}
import pl.kochmap.parking.repository.FeeRepository
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FeeService @Inject()(
    feeRepository: FeeRepository,
    val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends TransactionalService {

  def earningsDuring(date: LocalDate): Future[(Double, Currency)] = {
    val currency = Currency.PLN
    for {
      fees <- feeRepository.feesDuring(date)
      baseAmount = fees.foldLeft(0.0d)(_ + _.baseAmount)
    } yield (CurrencySnapshot(currency).convert(baseAmount), currency)
  }
}

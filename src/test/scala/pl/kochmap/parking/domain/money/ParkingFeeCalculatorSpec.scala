package pl.kochmap.parking.domain.money

import java.time.{Duration, Instant}

import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}
import pl.kochmap.parking.domain.money.FeeTariff.FeeTariff
import pl.kochmap.parking.domain.money.ParkingFeeCalculatorSpec.ParkingFeeCalculatorBehaviour
import pl.kochmap.parking.domain.{ParkSpace, StoppedParkingTicket, Vehicle}

class ParkingFeeCalculatorSpec
    extends FlatSpec
    with GivenWhenThen
    with Matchers
    with ParkingFeeCalculatorBehaviour {

  "ParkingFeeCalculator" should behave like parkingFeeCalculator(
    1 hour,
    FeeTariff.REGULAR_TARIFF,
    1.0d)

  it should behave like parkingFeeCalculator(
    2 hour,
    FeeTariff.REGULAR_TARIFF,
    3.0d)

  it should behave like parkingFeeCalculator(
    3 hour,
    FeeTariff.REGULAR_TARIFF,
    7.0d)

  it should behave like parkingFeeCalculator(
    11 hour,
    FeeTariff.REGULAR_TARIFF,
    2047.0d)

  it should behave like parkingFeeCalculator(
    1 hour,
    FeeTariff.VIP_TARIFF,
    0.0d)

  it should behave like parkingFeeCalculator(
    2 hour,
    FeeTariff.VIP_TARIFF,
    2.0d)

  it should behave like parkingFeeCalculator(
    3 hour,
    FeeTariff.VIP_TARIFF,
    5.0d)

  it should behave like parkingFeeCalculator(
    11 hour,
    FeeTariff.VIP_TARIFF,
    226.66d)

  implicit class IntToDurationConversion(time: Int) {
    def hour: Duration = hours

    def hours: Duration = Duration.ofHours(time)
  }

}

object ParkingFeeCalculatorSpec {
  trait ParkingFeeCalculatorBehaviour {
    this: FlatSpec with GivenWhenThen with Matchers =>

    val parkSpace = new ParkSpace
    val vehicle = Vehicle(None)
    val currencySnapshot: CurrencySnapshot =
      CurrencySnapshot.constant1To1ExchangeRatePlnCurrencySnapshot

    def parkingFeeCalculator(duration: Duration,
                             tariff: FeeTariff,
                             feeExpectedInPln: Double) = {
      it should s"calculate fee $feeExpectedInPln pln for ${duration.toHours} hours on ${tariff.toString}" in {
        Given(
          s"${duration.toHours} hours parking ticket, constant 1 to 1 pln exchange rate and regular fee tariff")
        val ticket =
          createParkingTicketFor(duration, tariff)

        When("fee is calculated")
        val fee =
          ParkingFeeCalculator.calculateFee(ticket, currencySnapshot, tariff)

        Then(s"fee should be $feeExpectedInPln pln")
        fee.currencySnapshot.currency should be(Currency.PLN)
        // Without epsilon because value was rounded
        fee.amountInCurrency should be(feeExpectedInPln)
      }
    }

    def createParkingTicketFor(duration: Duration,
                               tariff: FeeTariff): StoppedParkingTicket = {
      val startTimestamp = Instant.now()
      val stopTimestamp = startTimestamp.plusSeconds(duration.getSeconds)
      StoppedParkingTicket(None,
                           parkSpace,
                           vehicle,
                           startTimestamp,
                           stopTimestamp)
    }
  }
}

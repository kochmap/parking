package pl.kochmap.parking.domain.money

import java.time.{Duration, Instant}

import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}
import pl.kochmap.parking.Util._
import pl.kochmap.parking.domain.money.FeeTariff.FeeTariff
import pl.kochmap.parking.domain.money.ParkingFeeCalculatorSpec.ParkingFeeCalculatorBehaviour
import pl.kochmap.parking.domain.{ParkingMeter, StoppedParkingTicket}

class ParkingFeeCalculatorSpec
    extends FlatSpec
    with GivenWhenThen
    with Matchers
    with ParkingFeeCalculatorBehaviour {

  "Parking Fee Calculator" should behave like parkingFeeCalculator(
    30 seconds,
    FeeTariff.REGULAR_TARIFF,
    1.0d)

  it should behave like parkingFeeCalculator(1 hour,
                                             FeeTariff.REGULAR_TARIFF,
                                             1.0d)

  it should behave like parkingFeeCalculator(2 hour,
                                             FeeTariff.REGULAR_TARIFF,
                                             3.0d)

  it should behave like parkingFeeCalculator(3 hours,
                                             FeeTariff.REGULAR_TARIFF,
                                             7.0d)

  it should behave like parkingFeeCalculator((3 hours).plusNanos(1),
                                             FeeTariff.REGULAR_TARIFF,
                                             15.0d)

  it should behave like parkingFeeCalculator(11 hours,
                                             FeeTariff.REGULAR_TARIFF,
                                             2047.0d)

  it should behave like parkingFeeCalculator(45 seconds,
                                             FeeTariff.VIP_TARIFF,
                                             0.0d)

  it should behave like parkingFeeCalculator(1 hour, FeeTariff.VIP_TARIFF, 0.0d)

  it should behave like parkingFeeCalculator(2 hours,
                                             FeeTariff.VIP_TARIFF,
                                             2.0d)

  it should behave like parkingFeeCalculator(3 hours,
                                             FeeTariff.VIP_TARIFF,
                                             5.0d)


  it should behave like parkingFeeCalculator((3 hours).plusNanos(1),
    FeeTariff.VIP_TARIFF,
    9.5d)

  it should behave like parkingFeeCalculator(11 hours,
                                             FeeTariff.VIP_TARIFF,
                                             226.66d)

}

object ParkingFeeCalculatorSpec {

  trait ParkingFeeCalculatorBehaviour {
    this: FlatSpec with GivenWhenThen with Matchers =>

    val epislon = 1e-3

    implicit val doubleEq: Equality[Double] =
      TolerantNumerics.tolerantDoubleEquality(epislon)

    val parkingMeter = new ParkingMeter(Some(1), "1", Nil)
    val currencySnapshot: CurrencySnapshot =
      CurrencySnapshot(Currency.PLN)

    def parkingFeeCalculator(duration: Duration,
                             tariff: FeeTariff,
                             feeExpected: Double) = {
      it should s"calculate fee $feeExpected for ${duration.toString.stripPrefix("PT")} hours on ${tariff.toString}" in {
        Given(s"${duration.toHours} hours parking ticket and $tariff")
        val ticket =
          createParkingTicketFor(duration, tariff)

        When("fee is calculated")
        val fee =
          ParkingFeeCalculator.calculateFee(ticket, currencySnapshot, tariff)

        Then(s"fee should be $feeExpected")
        assert(fee.baseAmount === feeExpected)
      }
    }

    def createParkingTicketFor(duration: Duration,
                               tariff: FeeTariff): StoppedParkingTicket = {
      val startTimestamp = Instant.now()
      val stopTimestamp = startTimestamp.plusNanos(duration.toNanos)
      StoppedParkingTicket(Some(1),
                           parkingMeter.id,
                           "abcde",
                           startTimestamp,
                           stopTimestamp)
    }
  }

}

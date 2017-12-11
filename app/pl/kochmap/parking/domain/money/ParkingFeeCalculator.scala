package pl.kochmap.parking.domain.money

import java.time.Duration

import pl.kochmap.parking.Util._
import pl.kochmap.parking.domain.StoppedParkingTicket
import pl.kochmap.parking.domain.money.FeeTariff.FeeTariff

object ParkingFeeCalculator {

  def calculateFee(stoppedParkingTicket: StoppedParkingTicket,
                   currencySnapshot: CurrencySnapshot,
                   feeTariff: FeeTariff): Fee = {
    new Fee(
      stoppedParkingTicket.feeOption.flatMap(_.id),
      calculateFee(feeTariff,
                   Duration.between(stoppedParkingTicket.startTimestamp,
                                    stoppedParkingTicket.stopTimestamp)),
      currencySnapshot,
      feeTariff,
      stoppedParkingTicket.id
    )
  }

  private def calculateFee(feeTariff: FeeTariff, duration: Duration) =
    feeTariff match {
      case FeeTariff.REGULAR_TARIFF => calculateFeeOnRegularTariff(duration)
      case FeeTariff.VIP_TARIFF     => calculateFeeOnVipTariff(duration)
    }

  private def calculateFeeOnRegularTariff(duration: Duration): Double = {
    lazy val streamOfFees: Stream[(Duration, Double)] =
      (Duration.ZERO, 1.0d) #:: (Duration.ofHours(1).plusNanos(1), 2.0d) #:: streamOfFees.tail
        .map {
          case (hours, feeForHour) => (hours.plusHours(1), feeForHour * 2.0d)
        }

    streamOfFees
      .takeWhile {
        case (hours, _) => hours.compareTo(duration) <= 0
      }
      .foldLeft(0.0d)(_ + _._2)
  }

  private def calculateFeeOnVipTariff(duration: Duration): Double = {
    lazy val streamOfFees: Stream[(Duration, Double)] =
      (Duration.ZERO, 0.0d) #:: (Duration.ofHours(1).plusNanos(1), 2.0d) #:: streamOfFees.tail
        .map {
          case (hours, feeForHour) => (hours.plusHours(1), feeForHour * 1.5d)
        }

    streamOfFees
      .takeWhile {
        case (hours, _) => hours.compareTo(duration) <= 0
      }
      .foldLeft(0.0d)(_ + _._2)
  }
}

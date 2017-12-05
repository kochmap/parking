package pl.kochmap.parking.domain

import java.time.Duration

import pl.kochmap.parking.domain.FeeTariff.FeeTariff

object ParkingFeeCalculator {

  private val tariffFeeCalculateStrategyMap
    : Map[FeeTariff, Duration => Double] = Map(
    FeeTariff.REGULAR_TARIFF -> calculateFeeOnRegularTariff,
    FeeTariff.VIP_TARIFF -> calculateFeeOnVipTariff
  )

  def calculateFee(stoppedParkingTicket: StoppedParkingTicket,
                   currencySnapshot: CurrencySnapshot,
                   feeTariff: FeeTariff): Fee = {
    new Fee(tariffFeeCalculateStrategyMap(feeTariff)(
              Duration.between(stoppedParkingTicket.startTimestamp,
                               stoppedParkingTicket.stopTimestamp)),
            currencySnapshot)
  }

  private def calculateFeeOnRegularTariff(duration: Duration): Double = {
    lazy val streamOfFees: Stream[(Duration, Double)] = (Duration.ofHours(1),
                                                         1.0d) #:: streamOfFees
      .map {
        case (hour, feeForHour) => (hour.plusHours(1), feeForHour * 2.0d)
      }

    streamOfFees
      .takeWhile {
        case (hour, _) => hour.compareTo(duration) <= 0
      }
      .foldLeft(0.0d)(_ + _._2)
  }

  private def calculateFeeOnVipTariff(duration: Duration): Double = {
    lazy val streamOfFees: Stream[(Duration, Double)] = (Duration.ofHours(1),
                                                         0.0d) #:: (Duration
                                                                      .ofHours(
                                                                        2),
                                                                    2.0d) #:: streamOfFees.tail
      .map {
        case (hour, feeForHour) => (hour.plusHours(1), feeForHour * 1.5d)
      }

    streamOfFees
      .takeWhile {
        case (hour, _) => hour.compareTo(duration) <= 0
      }
      .foldLeft(0.0d)(_ + _._2)
  }
}

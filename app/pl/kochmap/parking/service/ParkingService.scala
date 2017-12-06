package pl.kochmap.parking.service

import java.time.LocalDate
import javax.inject.Singleton

import pl.kochmap.parking.domain.money.Money

import scala.concurrent.Future

@Singleton
class ParkingService {
  def isVehicleStartedParkingMeter(carLicensePlateNumber: String): Future[Boolean] = ???

  def earningsFrom(localDate: LocalDate): Future[Money] = ???
}

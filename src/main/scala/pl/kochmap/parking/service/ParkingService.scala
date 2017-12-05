package pl.kochmap.parking.service

import java.time.LocalDate

import pl.kochmap.parking.domain.money.Money

import scala.concurrent.Future

class ParkingService {
  def isVehicleStartedParkingMeter(carLicensePlateNumber: String): Future[Boolean] = ???

  def earningsFrom(localDate: LocalDate): Future[Money] = ???
}

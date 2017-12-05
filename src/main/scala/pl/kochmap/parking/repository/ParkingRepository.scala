package pl.kochmap.parking.repository

import java.time.LocalDate

import pl.kochmap.parking.domain.money.Money
import slick.dbio.DBIO

class ParkingRepository {
  def isVehicleStartedParkingMeter(
      carLicensePlateNumber: String): DBIO[Boolean] = ???

  def earningsFrom(localDate: LocalDate): DBIO[Money] = ???

}

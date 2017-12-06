package pl.kochmap.parking.repository

import java.time.LocalDate
import javax.inject.Singleton

import pl.kochmap.parking.domain.money.Currency.Currency
import slick.dbio.DBIO

@Singleton
class ParkingRepository {
  def isVehicleStartedParkingMeter(
      carLicensePlateNumber: String): DBIO[Boolean] = ???

  def earningsFrom(localDate: LocalDate): DBIO[(Double, Currency)] = ???

}

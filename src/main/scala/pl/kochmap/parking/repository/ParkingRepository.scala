package pl.kochmap.parking.repository

import slick.dbio.DBIO

class ParkingRepository {
  def isVehicleStartedParkingMeter(
      carLicensePlateNumber: String): DBIO[Boolean] = ???

}

package pl.kochmap.parking.service

import javax.inject.{Inject, Singleton}

import pl.kochmap.parking.repository.ParkingTicketRepository
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

@Singleton
class VehicleService @Inject()(parkingTicketRepository: ParkingTicketRepository,
                               val dbConfigProvider: DatabaseConfigProvider)
    extends TransactionalService {

  import dbConfig._
  import profile.api._

  def hasVehicleHasActiveParkingMeter(
      vehicleLicensePlateId: String): Future[Boolean] = {
    parkingTicketRepository.hasVehicleHasActiveParkingMeter(
      vehicleLicensePlateId)
  }

}

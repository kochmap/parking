package pl.kochmap.parking.service

import javax.inject.Singleton

import pl.kochmap.parking.repository.ParkingTicketRepository
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext

@Singleton
class FeeService(
    parkingTicketRepository: ParkingTicketRepository,
    val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends TransactionalService {
  
}

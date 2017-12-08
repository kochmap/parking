package pl.kochmap.parking.service

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import controllers.dto.VehicleDto
import pl.kochmap.parking.domain._
import pl.kochmap.parking.domain.money.Currency.Currency
import pl.kochmap.parking.repository.{
  ParkingMeterRepository,
  ParkingTicketRepository
}
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ParkingService @Inject()(
    parkingMeterRepository: ParkingMeterRepository,
    parkingTicketRepository: ParkingTicketRepository,
    dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  def startParkingMeter(parkingMeterDomainId: String, dto: VehicleDto): Future[
    Option[Either[ParkingMeterStartFailureException, ActiveParkingTicket]]] = {
    findParkingMeterByDomainIdOrCreateNew(parkingMeterDomainId)
      .map {
        case Some(parkingMeter) =>
          Some(parkingMeter.startFor(dto.licensePlateNumber))
        case _ => None
      }
      .flatMap {
        case Some(Right(ticket)) =>
          val persistedTicket = parkingTicketRepository
            .insertParkingTicket(ticket)
            .flatMap(parkingTicketRepository.findById)
          persistedTicket.map {
            case Some(pt: ActiveParkingTicket) => Some(Right(pt))
            case None                          => None
          }
        case s @ Some(_) => DBIO.successful(s)
        case None        => DBIO.successful(None)
      }
  }

  def stopParkingMeter(parkingMeterId: String): Future[
    Option[Either[ParkingMeterStopFailureException, StoppedParkingTicket]]] = {
    findParkingMeterByDomainIdOrCreateNew(parkingMeterId)
      .map {
        case Some(parkingMeter) => Some(parkingMeter.stop)
        case None               => None
      }
      .flatMap {
        case Some(Right(stoppedTicket)) =>
          parkingTicketRepository
            .updateParkingTicket(stoppedTicket)
            .map(_ => Some(Right(stoppedTicket)))

        case Some(l @ Left(e)) => DBIO.successful(Some(l))
        case None              => DBIO.successful(None)
      }
  }

  private def findParkingMeterByDomainIdOrCreateNew(parkingMeterId: String) = {
    parkingMeterRepository
      .findByDomainId(parkingMeterId)
      .flatMap {
        case s @ Some(_) => DBIO.successful(s)
        case None =>
          parkingMeterRepository
            .insertParkingMeter(new ParkingMeter(None, parkingMeterId, Nil))
            .flatMap(id => parkingMeterRepository.findById(id))
      }
  }

  def hasVehicleHasActiveParkingMeter(
      vehicleLicensePlateId: String): Future[Boolean] = {
    parkingTicketRepository.hasVehicleHasActiveParkingMeter(
      vehicleLicensePlateId)
  }

  def earningsFrom(localDate: LocalDate): Future[(Double, Currency)] = ???

  private implicit def runTransactionallyOnDb[R](dbio: DBIO[R]): Future[R] =
    db.run(dbio.transactionally)
}

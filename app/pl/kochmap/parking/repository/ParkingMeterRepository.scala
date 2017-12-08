package pl.kochmap.parking.repository

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import pl.kochmap.parking.domain.ParkingMeter
import pl.kochmap.parking.domain.money.Currency.Currency

import scala.concurrent.ExecutionContext

@Singleton
class ParkingMeterRepository @Inject() (
    tables: Tables,
    parkingTicketRepository: ParkingTicketRepository)(implicit ec: ExecutionContext) {

  import tables._
  import dbConfig._
  import profile.api._

  def insertParkingMeter(parkingMeter: ParkingMeter): slick.dbio.DBIO[Long] = {
    val parkingMeterRow =
      ParkingMeterRow(parkingMeter.id, parkingMeter.domainId)
    val insertParkingMeterQuery = (parkingMeters returning parkingMeters.map(
      _.id)) += parkingMeterRow
    for {
      parkingMeterId <- insertParkingMeterQuery
      parkingTicketsWithCorrectParkingMeterId = parkingMeter.tickets.map(pt =>
        pt.toParkingTicketRow.copy(parkingMeterId = Some(parkingMeterId)))

      _ <- DBIO.sequence(parkingTicketsWithCorrectParkingMeterId.map(
        parkingTicketRepository.insertParkingTicketRow))

    } yield parkingMeterId
  }

  def findByDomainId(
      domainId: String): slick.dbio.DBIO[Option[ParkingMeter]] = {
    val findQuery = parkingMeters
      .filter(_.domainId === domainId)
      .result
      .headOption

    buildFromFindQuery(findQuery)
  }

  def findById(id: Long): slick.dbio.DBIO[Option[ParkingMeter]] = {
    val findQuery = parkingMeters
      .filter(_.id === id)
      .result
      .headOption

    buildFromFindQuery(findQuery)
  }

  def isVehicleStartedParkingMeter(
      carLicensePlateNumber: String): slick.dbio.DBIO[Boolean] = ???

  def earningsFrom(localDate: LocalDate): slick.dbio.DBIO[(Double, Currency)] =
    ???

  private def buildFromFindQuery(findQuery: DBIO[Option[ParkingMeterRow]]) = {
    for {
      parkingMeterRowOption <- findQuery
      parkingTickets <- parkingMeterRowOption
        .map(pmr => parkingTicketRepository.findByParkingMeterId(pmr.id.get))
        .getOrElse(DBIO.successful(Nil))
    } yield
      parkingMeterRowOption.map(pmr =>
        new ParkingMeter(pmr.id, pmr.domainId, parkingTickets))
  }
}

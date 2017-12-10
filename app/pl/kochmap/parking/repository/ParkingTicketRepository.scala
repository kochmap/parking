package pl.kochmap.parking.repository

import javax.inject.{Inject, Singleton}

import pl.kochmap.parking.domain.ParkingTicket

import scala.concurrent.ExecutionContext

@Singleton
class ParkingTicketRepository @Inject()(
    tables: Tables,
    feeRepository: FeeRepository)(implicit ec: ExecutionContext) {

  import tables._
  import dbConfig._
  import profile.api._

  def findById(id: Long): slick.dbio.DBIO[Option[ParkingTicket]] = {
    parkingTickets
      .filter(_.id === id)
      .result
      .headOption
      .flatMap {
        case Some(row) => row.toEntityQuery.map(Some(_))
        case None      => DBIO.successful(None)
      }
  }

  def findByParkingMeterId(
      parkingMeterId: Long): slick.dbio.DBIO[Seq[ParkingTicket]] = {
    parkingTickets
      .filter(_.parkingMeterId === parkingMeterId)
      .result
      .flatMap(rows => DBIO.sequence(rows.map(_.toEntityQuery)))
  }

  def insertParkingTicket(
      parkingTicket: ParkingTicket): slick.dbio.DBIO[Long] = {
    val parkingTicketRow = parkingTicket.toParkingTicketRow
    for {
      parkingTicketId <- insertParkingTicketRow(parkingTicketRow)
      _ <- parkingTicket.feeOption match {
        case Some(fee) =>
          feeRepository
            .insertOrUpdate(fee.copy(parkingTicketId = Some(parkingTicketId)))
            .map(_ => ())

        case None => DBIO.successful(())
      }
    } yield parkingTicketId
  }

  def insertParkingTicketRow(
      parkingTicketRow: ParkingTicketRow): slick.dbio.DBIO[Long] = {
    (parkingTickets returning parkingTickets.map(_.id)) += parkingTicketRow
  }

  def updateParkingTicket(
      parkingTicket: ParkingTicket): slick.dbio.DBIO[Unit] = {
    val parkingTicketRow = parkingTicket.toParkingTicketRow
    val parkingUpdateQuery = parkingTickets.filter(_.id === parkingTicketRow.id) update parkingTicketRow
    for {
      _ <- parkingUpdateQuery
      _ <- parkingTicket.feeOption match {
        case Some(fee) => feeRepository.insertOrUpdate(fee)
        case None      => DBIO.successful(())
      }
    } yield ()
  }

  def hasVehicleHasActiveParkingMeter(
      vehicleLicensePlateId: String): slick.dbio.DBIO[Boolean] = {
    parkingTickets
      .filter(_.vehicleLicensePlateNumber === vehicleLicensePlateId)
      .filterNot(_.stopTimestamp.isDefined)
      .exists
      .result
  }

  implicit private class ParkingTicketRowToQueryConverter(
      parkingTicketRow: ParkingTicketRow) {

    def toEntityQuery = {
      val feeOption = parkingTicketRow.id match {
        case Some(id) => feeRepository.findByTicketId(id)
        case None     => DBIO.successful(None)
      }
      feeOption.map(feeOption => ParkingTicket(parkingTicketRow, feeOption))
    }
  }

}

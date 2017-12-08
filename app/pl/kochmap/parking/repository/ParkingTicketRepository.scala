package pl.kochmap.parking.repository

import javax.inject.{Inject, Singleton}

import pl.kochmap.parking.domain.ParkingTicket

import scala.concurrent.ExecutionContext

@Singleton
class ParkingTicketRepository @Inject()(tables: Tables)(
    implicit ec: ExecutionContext) {

  import tables._
  import dbConfig._
  import profile.api._

  def findById(id: Long): slick.dbio.DBIO[Option[ParkingTicket]] = {
    parkingTickets
      .filter(_.id === id)
      .result
      .headOption
      .map(_.map(ParkingTicket(_)))
  }

  def findByParkingMeterId(
      parkingMeterId: Long): slick.dbio.DBIO[Seq[ParkingTicket]] = {
    parkingTickets
      .filter(_.parkingMeterId === parkingMeterId)
      .result
      .map(_.map(ParkingTicket(_)))
  }

  def insertParkingTicket(
      parkingTicket: ParkingTicket): slick.dbio.DBIO[Long] = {
    insertParkingTicketRow(parkingTicket.toParkingTicketRow)
  }

  def insertParkingTicketRow(
      parkingTicketRow: ParkingTicketRow): slick.dbio.DBIO[Long] = {
    (parkingTickets returning parkingTickets.map(_.id)) += parkingTicketRow
  }

  def updateParkingTicket(
      parkingTicket: ParkingTicket): slick.dbio.DBIO[Unit] = {
    val parkingTicketRow = parkingTicket.toParkingTicketRow
    (parkingTickets.filter(_.id === parkingTicketRow.id) update parkingTicketRow)
      .map(_ => ())
  }

}

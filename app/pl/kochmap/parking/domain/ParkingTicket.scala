package pl.kochmap.parking.domain

import java.time.Instant

import pl.kochmap.parking.repository.ParkingTicketRow

sealed trait ParkingTicket {
  def id: Option[Long]
  def parkMeterId: Option[Long]
  def vehicleLicensePlateNumber: String
  def startTimestamp: Instant
  def stopTimestampOption: Option[Instant]
  def toParkingTicketRow: ParkingTicketRow =
    ParkingTicketRow(id,
                     parkMeterId,
                     vehicleLicensePlateNumber,
                     startTimestamp,
                     stopTimestampOption)
}

case class ActiveParkingTicket(id: Option[Long],
                               parkMeterId: Option[Long],
                               vehicleLicensePlateNumber: String,
                               startTimestamp: Instant = Instant.now())
    extends ParkingTicket {

  def stopCountingFee: StoppedParkingTicket =
    StoppedParkingTicket(id,
                         parkMeterId,
                         vehicleLicensePlateNumber,
                         startTimestamp)

  override def stopTimestampOption: Option[Instant] = None

}

case class StoppedParkingTicket(id: Option[Long],
                                parkMeterId: Option[Long],
                                vehicleLicensePlateNumber: String,
                                startTimestamp: Instant,
                                stopTimestamp: Instant = Instant.now())
    extends ParkingTicket {
  override def toParkingTicketRow: ParkingTicketRow =
    ParkingTicketRow(id,
                     parkMeterId,
                     vehicleLicensePlateNumber,
                     startTimestamp,
                     Some(stopTimestamp))

  override def stopTimestampOption: Option[Instant] = Some(stopTimestamp)
}

object ParkingTicket {
  def apply(parkingTicketRow: ParkingTicketRow): ParkingTicket =
    parkingTicketRow.stopTimestampOption match {
      case Some(stopTimestamp) =>
        StoppedParkingTicket(parkingTicketRow.id,
                             parkingTicketRow.parkingMeterId,
                             parkingTicketRow.vehicleLicensePlateNumber,
                             parkingTicketRow.startTimestamp,
                             stopTimestamp)
      case None =>
        ActiveParkingTicket(parkingTicketRow.id,
                            parkingTicketRow.parkingMeterId,
                            parkingTicketRow.vehicleLicensePlateNumber,
                            parkingTicketRow.startTimestamp)
    }
}

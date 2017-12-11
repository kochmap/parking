package pl.kochmap.parking.domain

import java.time.Instant

import pl.kochmap.parking.domain.money.Fee
import pl.kochmap.parking.repository.{FeeRow, ParkingTicketRow}

sealed trait ParkingTicket {
  def id: Option[Long]
  def parkingMeterId: Option[Long]
  def vehicleLicensePlateNumber: String
  def startTimestamp: Instant
  def stopTimestampOption: Option[Instant]
  def feeOption: Option[Fee]
  def toParkingTicketRow: ParkingTicketRow =
    ParkingTicketRow(id,
                     parkingMeterId,
                     vehicleLicensePlateNumber,
                     startTimestamp,
                     stopTimestampOption)
}

case class ActiveParkingTicket(id: Option[Long],
                               parkingMeterId: Option[Long],
                               vehicleLicensePlateNumber: String,
                               startTimestamp: Instant = Instant.now())
    extends ParkingTicket {

  def stopCountingFee: StoppedParkingTicket =
    StoppedParkingTicket(id,
                         parkingMeterId,
                         vehicleLicensePlateNumber,
                         startTimestamp)

  override val stopTimestampOption: Option[Instant] = None
  override val feeOption: Option[Fee] = None

}

case class StoppedParkingTicket(id: Option[Long],
                                parkingMeterId: Option[Long],
                                vehicleLicensePlateNumber: String,
                                startTimestamp: Instant,
                                stopTimestamp: Instant = Instant.now(),
                                feeOption: Option[Fee] = None)
    extends ParkingTicket {
  override def toParkingTicketRow: ParkingTicketRow =
    ParkingTicketRow(id,
                     parkingMeterId,
                     vehicleLicensePlateNumber,
                     startTimestamp,
                     Some(stopTimestamp))

  override def stopTimestampOption: Option[Instant] = Some(stopTimestamp)
}

object ParkingTicket {
  def apply(parkingTicketRow: ParkingTicketRow,
            feeOption: Option[Fee] = None): ParkingTicket =
    parkingTicketRow.stopTimestampOption match {
      case Some(stopTimestamp) =>
        StoppedParkingTicket(
          parkingTicketRow.id,
          parkingTicketRow.parkingMeterId,
          parkingTicketRow.vehicleLicensePlateNumber,
          parkingTicketRow.startTimestamp,
          stopTimestamp,
          feeOption
        )
      case None =>
        assert(feeOption.isEmpty)
        ActiveParkingTicket(parkingTicketRow.id,
                            parkingTicketRow.parkingMeterId,
                            parkingTicketRow.vehicleLicensePlateNumber,
                            parkingTicketRow.startTimestamp)
    }
}

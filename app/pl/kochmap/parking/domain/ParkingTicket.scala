package pl.kochmap.parking.domain

import java.time.Instant

sealed trait ParkingTicket {
  def id: Option[Long]
  def parkMeterId: Option[Long]
  def vehicleLicensePlateNumber: String
  def startTimestamp: Instant
}

case class ActiveParkingTicket(id: Option[Long],
                               parkMeterId: Option[Long],
                               vehicleLicensePlateNumber: String,
                               startTimestamp: Instant = Instant.now())
    extends ParkingTicket {
  def stopCountingFee: StoppedParkingTicket =
    StoppedParkingTicket(id, parkMeterId, vehicleLicensePlateNumber, startTimestamp)
}

case class StoppedParkingTicket(id: Option[Long],
                                parkMeterId: Option[Long],
                                vehicleLicensePlateNumber: String,
                                startTimestamp: Instant,
                                stopTimestamp: Instant = Instant.now())
    extends ParkingTicket {}

package pl.kochmap.parking.domain

import java.time.Instant

sealed trait ParkingTicket {
  def id: Option[Long]
  def parkSpaceId: Option[Long]
  def vehicleId: Option[Long]
  def startTimestamp: Instant
}

case class ActiveParkingTicket(id: Option[Long],
                               parkSpaceId: Option[Long],
                               vehicleId: Option[Long],
                               startTimestamp: Instant = Instant.now())
    extends ParkingTicket {
  def stopCountingFee: StoppedParkingTicket =
    StoppedParkingTicket(id, parkSpaceId, vehicleId, startTimestamp)
}

case class StoppedParkingTicket(id: Option[Long],
                                parkSpaceId: Option[Long],
                                vehicleId: Option[Long],
                                startTimestamp: Instant,
                                stopTimestamp: Instant = Instant.now())
    extends ParkingTicket {}

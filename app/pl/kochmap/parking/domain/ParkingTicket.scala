package pl.kochmap.parking.domain

import java.time.Instant

sealed trait ParkingTicket {
  def id: Option[Long]
  def parkMeterId: Option[Long]
  def vehicleId: Option[Long]
  def startTimestamp: Instant
}

case class ActiveParkingTicket(id: Option[Long],
                               parkMeterId: Option[Long],
                               vehicleId: Option[Long],
                               startTimestamp: Instant = Instant.now())
    extends ParkingTicket {
  def stopCountingFee: StoppedParkingTicket =
    StoppedParkingTicket(id, parkMeterId, vehicleId, startTimestamp)
}

case class StoppedParkingTicket(id: Option[Long],
                                parkMeterId: Option[Long],
                                vehicleId: Option[Long],
                                startTimestamp: Instant,
                                stopTimestamp: Instant = Instant.now())
    extends ParkingTicket {}

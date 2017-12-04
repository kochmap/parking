package pl.kochmap.parking.domain

import java.time.Instant

sealed trait ParkingTicket {
  def id: Option[Long]
  def parkSpace: ParkSpace
  def vehicle: Vehicle
  def startTimestamp: Instant
}

case class ActiveParkingTicket(id: Option[Long],
                               parkSpace: ParkSpace,
                               vehicle: Vehicle,
                               startTimestamp: Instant = Instant.now())
    extends ParkingTicket {
  def stopCountingFee: ParkingTicketWithAChargedFee = ???
}

case class ParkingTicketWithAChargedFee(id: Option[Long],
                                        parkSpace: ParkSpace,
                                        vehicle: Vehicle,
                                        startTimestamp: Instant,
                                        stopTimestamp: Instant = Instant.now())
    extends ParkingTicket {
  val fee: Float = ???
}

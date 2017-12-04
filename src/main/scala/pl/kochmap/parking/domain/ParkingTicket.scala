package pl.kochmap.parking.domain

case class ParkingTicket(id: Option[Long], parkSpace: ParkSpace, vehicle: Vehicle)

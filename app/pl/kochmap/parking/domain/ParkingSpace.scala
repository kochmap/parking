package pl.kochmap.parking.domain

class ParkSpace(id: Option[Long], tickets: Seq[ParkingTicket]) {

  val parkingMeterRunning: Boolean = tickets.exists {
    case _: ActiveParkingTicket => true
    case _                      => false
  }

  def startParkingMeterFor(vehicle: Vehicle)
    : Either[ParkingMeterStartFailureException, ActiveParkingTicket] = {
    if (parkingMeterRunning) Left(new ParkingMeterAlreadyStartedException)
    else Right(ActiveParkingTicket(None, id, vehicle.id))
  }
}

sealed trait ParkingMeterStartFailureException extends Exception

class ParkingMeterAlreadyStartedException
    extends ParkingMeterStartFailureException

package pl.kochmap.parking.domain

class ParkingMeter(val id: Option[Long],
                   val name: String,
                   val tickets: Seq[ParkingTicket]) {

  def parkingMeterRunning: Boolean = tickets.exists {
    case _: ActiveParkingTicket => true
    case _                      => false
  }

  def startFor(vehicleLicensePlateNumber: String)
    : Either[ParkingMeterStartFailureException, ActiveParkingTicket] = {
    if (parkingMeterRunning) Left(new ParkingMeterAlreadyStartedException)
    else Right(ActiveParkingTicket(None, id, vehicleLicensePlateNumber))
  }
}

sealed trait ParkingMeterStartFailureException extends Exception

class ParkingMeterAlreadyStartedException
    extends ParkingMeterStartFailureException

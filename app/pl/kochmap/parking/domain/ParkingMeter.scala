package pl.kochmap.parking.domain

class ParkingMeter(val id: Option[Long],
                   val domainId: String,
                   val tickets: Seq[ParkingTicket]) {

  assert(tickets.count {
    case _: ActiveParkingTicket => true
    case _                      => false
  } <= 1)

  def parkingMeterRunning: Boolean = tickets.exists {
    case _: ActiveParkingTicket => true
    case _                      => false
  }

  def activeParkingTicketOption: Option[ActiveParkingTicket] =
    tickets
      .collectFirst {
        case ticket: ActiveParkingTicket => ticket
      }

  def startFor(vehicleLicensePlateNumber: String)
    : Either[ParkingMeterStartFailureException, ActiveParkingTicket] = {
    if (parkingMeterRunning) Left(new ParkingMeterAlreadyStartedException)
    else Right(ActiveParkingTicket(None, id, vehicleLicensePlateNumber))
  }

  def stop
    : Either[ParkingMeterAlreadyStoppedException, StoppedParkingTicket] = {
    if (!parkingMeterRunning) Left(new ParkingMeterAlreadyStoppedException)
    else
      Right(activeParkingTicketOption.get.stopCountingFee)
  }

}

sealed abstract class ParkingMeterStartFailureException(message: String)
    extends DomainException(message)

class ParkingMeterAlreadyStartedException
    extends ParkingMeterStartFailureException(
      "Can't start parking meter because it has been started before")

sealed abstract class ParkingMeterStopFailureException(message: String)
    extends DomainException(message)

class ParkingMeterAlreadyStoppedException
    extends ParkingMeterStopFailureException(
      "Can't stop parking meter because it has been stopped before")

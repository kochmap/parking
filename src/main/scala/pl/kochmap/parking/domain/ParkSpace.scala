package pl.kochmap.parking.domain

class ParkSpace {
  def startParkingMeterFor(vehicle: Vehicle): Either[ParkingMeterStartFailureException, ActiveParkingTicket] = ???
}

sealed trait ParkingMeterStartFailureException extends Exception

class ParkingMeterAlreadyStartedException extends ParkingMeterStartFailureException
package pl.kochmap.parking.domain

class ParkSpace {
  def startParkingMeterFor(vehicle: Vehicle): Either[ParkingMeterStartFailureException, ParkingTicket] = ???
}

sealed trait ParkingMeterStartFailureException extends Exception

class ParkingMeterAlreadyStartedException extends ParkingMeterStartFailureException
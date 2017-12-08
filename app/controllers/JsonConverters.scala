package controllers

import controllers.dto.VehicleDto
import pl.kochmap.parking.domain.{ActiveParkingTicket, StoppedParkingTicket}
import play.api.libs.json.{Json, OFormat}

object JsonConverters {

  implicit val vehicleDtoFormat: OFormat[VehicleDto] = Json.format[VehicleDto]
  implicit val activeParkingTicket: OFormat[ActiveParkingTicket] =
    Json.format[ActiveParkingTicket]

  implicit val stoppedParkingTicket: OFormat[StoppedParkingTicket] =
    Json.format[StoppedParkingTicket]

}

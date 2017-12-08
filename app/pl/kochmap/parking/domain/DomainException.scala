package pl.kochmap.parking.domain

import play.api.libs.json.{JsObject, Json}

abstract class DomainException(message: String) extends Exception(message) {
  def toJson = Json.obj(
    "message" -> message
  )
}

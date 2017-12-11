package controllers

import javax.inject.{Inject, Singleton}

import pl.kochmap.parking.service.VehicleService
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import scala.concurrent.ExecutionContext

@Singleton
class VehicleController @Inject()(
    val controllerComponents: ControllerComponents,
    vehicleService: VehicleService)(implicit ec: ExecutionContext)
    extends BaseController {

  def hasVehicleHasActiveParkingMeter(
      vehicleLicensePlateId: String): Action[AnyContent] = Action.async {
    vehicleService
      .hasVehicleHasActiveParkingMeter(vehicleLicensePlateId)
      .map(has => Ok(Json.toJson(has)))
  }
}

package controllers

import javax.inject.Inject

import controllers.dto.VehicleDto
import pl.kochmap.parking.service.ParkingService
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class ParkingController @Inject()(
    val controllerComponents: ControllerComponents,
    parkingService: ParkingService)(implicit ec: ExecutionContext)
    extends BaseController {

  import JsonConverters._

  def index = Action {
    Ok("Hello world")
  }

  def startParkingMeter(parkingMeterId: String): Action[AnyContent] =
    Action.async { implicit request =>
      val vehicleDtoTry = Try {
        request.body.asJson.map(_.as[VehicleDto]).get
      }

      Future
        .fromTry(vehicleDtoTry)
        .flatMap(parkingService.startParkingMeter(parkingMeterId, _))
        .flatMap {
          case Some(Right(ticket)) => Future.successful(Ok(Json.toJson(ticket)))
          case Some(Left(e))       => Future.successful(Forbidden(e.toJson))
          case None                => Future.successful(NotFound)
        }
    }

  def stopParkingMeter(parkingMeterId: String): Action[AnyContent] =
    Action.async {
      parkingService.stopParkingMeter(parkingMeterId).flatMap {
        case Some(Right(ticket)) => Future.successful(Ok(Json.toJson(ticket)))
        case Some(Left(e))       => Future.successful(Forbidden(e.toJson))
        case None                => Future.successful(NotFound)
      }
    }

  def hasVehicleHasActiveParkingMeter(
      vehicleLicensePlateId: String): Action[AnyContent] = Action.async {
    parkingService.hasVehicleHasActiveParkingMeter(vehicleLicensePlateId).map(has => Ok(Json.toJson(has)))
  }
}

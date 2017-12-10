package controllers

import javax.inject.Inject

import controllers.dto.VehicleDto
import pl.kochmap.parking.service.ParkingMeterService
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class ParkingMeterController @Inject()(
    val controllerComponents: ControllerComponents,
    parkingMeterService: ParkingMeterService)(implicit ec: ExecutionContext)
    extends BaseController {

  import JsonConverters._

  def index = Action {
    Ok("Hello world")
  }

  def startParkingMeter(parkingMeterId: String): Action[AnyContent] =
    Action.async { implicit request =>
      val vehicleDtoParseFromJsonTry = Try {
        request.body.asJson.map(_.as[VehicleDto]).get
      }

      Future
        .fromTry(vehicleDtoParseFromJsonTry)
        .flatMap(parkingMeterService.startParkingMeter(parkingMeterId, _))
        .flatMap {
          case Some(Right(ticket)) => Future.successful(Ok(Json.toJson(ticket)))
          case Some(Left(e))       => Future.successful(Forbidden(Json.toJson(e)))
          case None                => Future.successful(NotFound)
        }
    }

  def stopParkingMeter(parkingMeterId: String): Action[AnyContent] =
    Action.async {
      parkingMeterService.stopParkingMeter(parkingMeterId).flatMap {
        case Some(Right(ticket)) => Future.successful(Ok(Json.toJson(ticket)))
        case Some(Left(e))       => Future.successful(Forbidden(Json.toJson(e)))
        case None                => Future.successful(NotFound)
      }
    }

}

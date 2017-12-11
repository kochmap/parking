package controllers

import javax.inject.{Inject, Singleton}

import controllers.util.JsonConverters._
import controllers.dto.TicketFeeChargeDto
import pl.kochmap.parking.service.ParkingTicketService
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class ParkingTicketController @Inject()(
    val controllerComponents: ControllerComponents,
    parkingTicketService: ParkingTicketService)(
    implicit executionContext: ExecutionContext)
    extends BaseController {

  def chargeTicketFee(ticketId: String): Action[AnyContent] = Action.async {
    implicit request =>
      val ticketChargeFeeDtoParseFromJsonTry = Try {
        request.body.asJson.map(_.as[TicketFeeChargeDto]).get
      }

      Future
        .fromTry(ticketChargeFeeDtoParseFromJsonTry)
        .flatMap(parkingTicketService.chargeTicketFee(ticketId.toLong, _))
        .map {
          case Some(Right(fee)) => Ok(Json.toJson(fee))
          case Some(Left(e))    => Forbidden(Json.toJson(e))
          case None             => NotFound
        }
  }

  def getTicketFee(ticketId: String): Action[AnyContent] = Action.async {
    parkingTicketService.getTicketFee(ticketId.toLong).map {
      case Some(fee) => Ok(Json.toJson(fee))
      case None      => NotFound
    }
  }

}

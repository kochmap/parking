package controllers

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import pl.kochmap.parking.service.FeeService
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class FeeController @Inject()(
    val controllerComponents: ControllerComponents,
    feeService: FeeService)(implicit executionContext: ExecutionContext)
    extends BaseController {
  def earningsDuring(date: String): Action[AnyContent] = Action.async {
    val dateParsingTry = Try {
      LocalDate.parse(date)
    }
    Future
      .fromTry(dateParsingTry)
      .flatMap(feeService.earningsDuring)
      .map(earnings => Ok(Json.toJson(earnings)))
  }
}

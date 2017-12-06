package controllers

import javax.inject.Inject

import play.api.mvc.{BaseController, ControllerComponents}

import scala.concurrent.ExecutionContext

class ParkingController @Inject()(
    val controllerComponents: ControllerComponents)(
    implicit ec: ExecutionContext)
    extends BaseController {

  def index = Action {
    Ok("Hello world")
  }

}

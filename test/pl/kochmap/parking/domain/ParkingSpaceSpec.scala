package pl.kochmap.parking.domain

import org.scalatest.{FlatSpec, GivenWhenThen}

class ParkingSpaceSpec extends FlatSpec with GivenWhenThen {

  "Parking space with stopped parking meter" should "start parking meter" in {
    Given("a parking space with not started parking meter and a car")
    val parkSpace = new ParkSpace()
    val vehicle = Vehicle(None)

    When("start parking meter is invoked")
    val resultEither = parkSpace.startParkingMeterFor(vehicle)

    Then("result should be a parking ticket")
    resultEither match {
      case Right(_: ActiveParkingTicket) => succeed
      case _ => fail("it wasn't a parking ticket")
    }
  }

  "Parking space with already started parking meter" should "not start parking meter" in {
    Given("a parking space with already started parking meter and a car")
    val parkSpace = new ParkSpace()
    val vehicle = Vehicle(None)

    When("start parking meter is invoked twice")
    parkSpace.startParkingMeterFor(vehicle)
    val resultEither = parkSpace.startParkingMeterFor(vehicle)

    Then("result should be a exception that parking meter was started")
    resultEither match {
      case Left(_: ParkingMeterAlreadyStartedException) => succeed
      case Right(_: ParkingTicket) => fail("excepting exception not ticket")
      case Left(sth) => fail(s"wasn't except that ${sth.toString}")
    }
  }
}

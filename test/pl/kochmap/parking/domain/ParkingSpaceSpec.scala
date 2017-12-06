package pl.kochmap.parking.domain

import org.scalatest.{FlatSpec, GivenWhenThen}

class ParkingSpaceSpec extends FlatSpec with GivenWhenThen {

  "Stopped parking meter" should "start" in {
    Given("a stopped parking meter")
    val parkingMeter = new ParkingMeter(Some(1), "abcd", Nil)

    When("start parking meter for is invoked")
    val resultEither = parkingMeter.startFor("abcde")

    Then("result should be a parking ticket")
    resultEither match {
      case Right(_: ActiveParkingTicket) => succeed
      case _                             => fail("it wasn't a parking ticket")
    }
  }

  "Already started parking meter" should "not start" in {
    Given("an already started parking meter(has active ticket)")
    val parkingMeter =
      new ParkingMeter(Some(1),
                       "abcd",
                       List(ActiveParkingTicket(None, Some(1), "abcde")))

    When("start parking meter is invoked")
    val resultEither = parkingMeter.startFor("abcde")

    Then("result should be a exception that parking meter was started")
    resultEither match {
      case Left(_: ParkingMeterAlreadyStartedException) => succeed
      case Right(_: ParkingTicket)                      => fail("excepting exception not ticket")
      case Left(sth)                                    => fail(s"wasn't except that ${sth.toString}")
    }
  }
}

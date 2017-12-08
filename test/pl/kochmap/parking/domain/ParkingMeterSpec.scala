package pl.kochmap.parking.domain

import org.scalatest.{FlatSpec, GivenWhenThen}

class ParkingMeterSpec extends FlatSpec with GivenWhenThen {

  "Stopped parking meter" should "start" in {
    Given("a stopped parking meter")
    val parkingMeter = new ParkingMeter(Some(1), "abcd", Nil)

    When("start for is invoked")
    val resultEither = parkingMeter.startFor("abcde")

    Then("result should be an active parking ticket")
    resultEither match {
      case Right(_: ActiveParkingTicket) => succeed
      case _                             => fail("it wasn't a parking ticket")
    }
  }

  it should "not stop" in {
    Given("a stopped parking meter")
    val parkingMeter =
      new ParkingMeter(Some(1),
                       "abcd", Nil)

    When("stop is invoked")
    val resultEither = parkingMeter.stop

    Then("result should be a exception that parking meter was stopped")
    resultEither match {
      case Left(_: ParkingMeterAlreadyStoppedException) => succeed
      case Right(_: ParkingTicket)                        => fail("excepting exception not ticket")
      case Left(sth)                                      => fail(s"wasn't except that ${sth.toString}")
    }
  }

  "Started parking meter" should "not start" in {
    Given("a started parking meter(has active ticket)")
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

  it should "stop" in {
    Given("a started parking meter(has active ticket)")
    val parkingMeter =
      new ParkingMeter(Some(1),
                       "abcd",
                       List(ActiveParkingTicket(Some(1), Some(1), "abcde")))

    When("stop parking meter is invoked")
    val resultEither = parkingMeter.stop

    Then("result should be a stopped parking ticket")
    resultEither match {
      case Right(_: StoppedParkingTicket) => succeed
      case _                              => fail("it wasn't a parking ticket")
    }
  }
}

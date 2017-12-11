package pl.kochmap.parking.domain

import java.time.Instant

import org.scalatest.{FlatSpec, GivenWhenThen}

import scala.util.Try

class ParkingMeterSpec extends FlatSpec with GivenWhenThen {

  "Stopped parking meter" should "start" in {
    Given("a stopped parking meter and vehicle license id")
    val parkingMeter = new ParkingMeter(Some(1), "abcd", Nil)
    val vehicleLicenseId = "abcde"

    When("start for is invoked")
    val resultEither = parkingMeter.startFor(vehicleLicenseId)

    Then("result should be an active parking ticket")
    resultEither match {
      case Right(_: ActiveParkingTicket) => succeed
      case _                             => fail("it wasn't a parking ticket")
    }
  }

  it should "not stop" in {
    Given("a stopped parking meter")
    val parkingMeter =
      new ParkingMeter(Some(1), "abcd", Nil)

    When("stop is invoked")
    val resultEither = parkingMeter.stop

    Then("result should be a exception that parking meter was stopped")
    resultEither match {
      case Left(_: ParkingMeterAlreadyStoppedException) => succeed
      case Right(_: ParkingTicket)                      => fail("excepting exception not ticket")
      case Left(sth)                                    => fail(s"wasn't except that ${sth.toString}")
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

  "Parking meter" should "have max one active ticket" in {
    Given("an active parking ticket and stopped ticket")
    val activeParkingTicket = ActiveParkingTicket(Some(1l), Some(1l), "abcde")
    val stoppedParkingTicket =
      StoppedParkingTicket(Some(2l), Some(1l), "abcde", Instant.now())

    When("constructing parking meters")
    val parkingMeterWithoutTicketsConstructionTry = Try {
      new ParkingMeter(activeParkingTicket.parkingMeterId, "abcd", Nil)
    }

    val parkingMeterWithOneActiveParkingTicketConstructionTry = Try {
      new ParkingMeter(activeParkingTicket.parkingMeterId,
                       "abcd",
                       List(activeParkingTicket, stoppedParkingTicket))
    }
    val parkingMeterWithTwoActiveParkingTicketConstructionTry = Try {
      new ParkingMeter(activeParkingTicket.parkingMeterId,
                       "abcd",
                       List(activeParkingTicket,
                            activeParkingTicket.copy(id = Some(3l)),
                            stoppedParkingTicket))
    }

    Then("constructed should be parking meters with one or less active tickets")
    assert(
      parkingMeterWithoutTicketsConstructionTry.isSuccess &&
        parkingMeterWithOneActiveParkingTicketConstructionTry.isSuccess &&
        parkingMeterWithTwoActiveParkingTicketConstructionTry.isFailure)
  }
}

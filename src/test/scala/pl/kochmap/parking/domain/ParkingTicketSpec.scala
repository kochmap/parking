package pl.kochmap.parking.domain

import org.scalatest.{FlatSpec, GivenWhenThen}

class ParkingTicketSpec extends FlatSpec with GivenWhenThen {

  val parkSpace = new ParkSpace()
  val vehicle = Vehicle(None)

  "Active parking ticket" should "stop counting fee" in {
    Given("an active parking ticket")
    val activeParkingTicket = ActiveParkingTicket(None, parkSpace, vehicle)

    When("stop counting fee is invoked")
    val parkingTicketWithAChargedFee = activeParkingTicket.stopCountingFee

    Then("result is stopped parking ticket")
    parkingTicketWithAChargedFee match {
      case _: StoppedParkingTicket => succeed
      case _ => fail("It is something else")
    }
  }

}

package pl.kochmap.parking.domain

import org.scalatest.{FlatSpec, GivenWhenThen}

class ParkingTicketSpec extends FlatSpec with GivenWhenThen {

  val parkingMeter = new ParkingMeter(Some(1), "abcd", Nil)

  "Active parking ticket" should "stop counting fee" in {
    Given("an active parking ticket")
    val activeParkingTicket =
      ActiveParkingTicket(None, parkingMeter.id, "abcde")

    When("stop counting fee is invoked")
    val parkingTicketWithAChargedFee = activeParkingTicket.stopCountingFee

    Then("result is stopped parking ticket")
    parkingTicketWithAChargedFee match {
      case _: StoppedParkingTicket => succeed
      case _                       => fail("It is something else")
    }
  }

}

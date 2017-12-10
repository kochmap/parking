package pl.kochmap.parking.domain.integration.service

import java.time.Instant

import controllers.dto.TicketFeeChargeDto
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.Matchers
import pl.kochmap.parking.domain.{
  ActiveParkingTicket,
  ParkingMeter,
  StoppedParkingTicket
}
import pl.kochmap.parking.domain.integration.AbstractIntegrationSpec
import pl.kochmap.parking.domain.money._
import pl.kochmap.parking.service.exception.CanNotChargeTicketFeeBecauseTicketHasNotBeenStoppedYetException

class ParkingTicketServiceSpec extends AbstractIntegrationSpec with Matchers {

  import dbConfig._

  val epislon = 1e-5

  implicit val doubleEq: Equality[Double] =
    TolerantNumerics.tolerantDoubleEquality(epislon)

  "Parking ticket service" should "charge fee for stopped ticket" in {
    Given(
      "a parking ticket service, stopped parking ticket in db and ticket fee charge dto")
    val now = Instant.now()
    val ticketFuture = for {
      parkingMeterId <- db.run(
        parkingMeterRepository.insertParkingMeter(
          new ParkingMeter(None, "1234", Nil)))
      ticket = StoppedParkingTicket(None,
                                    Some(parkingMeterId),
                                    "abcde",
                                    now.minusSeconds(3600),
                                    now)
      ticketId <- db.run(parkingTicketRepository.insertParkingTicket(ticket))

    } yield ticket.copy(id = Some(ticketId))
    val ticketFeeChargeDto =
      TicketFeeChargeDto(FeeTariff.REGULAR_TARIFF, Currency.PLN)

    When("charge fee for ticket is invoked")
    val resultEitherFuture = for {
      ticket <- ticketFuture
      resultEither <- parkingTicketService.chargeFeeForTicket(
        ticket.id.get,
        ticketFeeChargeDto)

    } yield resultEither

    Then("result should be fee")
    resultEitherFuture.map {
      case Some(Right(_: Fee)) => succeed
      case _                   => fail()
    }
  }

  it should "not charge fee for ticket because it hasn't been stopped yet" in {
    Given(
      "a parking ticket service, active parking ticket in db and ticket fee charge dto")
    val now = Instant.now()
    val ticketFuture = for {
      parkingMeterId <- db.run(
        parkingMeterRepository.insertParkingMeter(
          new ParkingMeter(None, "1234", Nil)))

      ticket = ActiveParkingTicket(None, Some(parkingMeterId), "abcde", now)
      ticketId <- db.run(parkingTicketRepository.insertParkingTicket(ticket))

    } yield ticket.copy(id = Some(ticketId))
    val ticketFeeChargeDto =
      TicketFeeChargeDto(FeeTariff.REGULAR_TARIFF, Currency.PLN)

    When("charge fee for ticket is invoked")
    val resultEitherFuture = for {
      ticket <- ticketFuture
      resultEither <- parkingTicketService.chargeFeeForTicket(
        ticket.id.get,
        ticketFeeChargeDto)
    } yield resultEither

    Then("result should be fee")
    resultEitherFuture.map {
      case Some(
          Left(
            _: CanNotChargeTicketFeeBecauseTicketHasNotBeenStoppedYetException)) =>
        succeed

      case _ => fail()
    }
  }

  it should "not charge fee for ticket because it doesn't exists" in {
    Given("a parking ticket service and ticket charge dto")
    val ticketFeeChargeDto =
      TicketFeeChargeDto(FeeTariff.REGULAR_TARIFF, Currency.PLN)

    When("charge fee for ticket is invoked")
    val resultEitherFuture =
      parkingTicketService.chargeFeeForTicket(1, ticketFeeChargeDto)

    Then("result should be none")
    resultEitherFuture.map {
      case None => succeed
      case _    => fail()
    }
  }

  it should "charge fee and then charge fee and last charge fee should be in db" in {

    Given(
      "a parking ticket service, stopped parking ticket in db and ticket fees charge dto")
    val now = Instant.now()
    val ticketFuture = for {
      parkingMeterId <- db.run(
        parkingMeterRepository.insertParkingMeter(
          new ParkingMeter(None, "1234", Nil)))
      ticket = StoppedParkingTicket(None,
                                    Some(parkingMeterId),
                                    "abcde",
                                    now.minusSeconds(3600),
                                    now)
      ticketId <- db.run(parkingTicketRepository.insertParkingTicket(ticket))

    } yield ticket.copy(id = Some(ticketId))
    val firstTicketFeeChargeDto =
      TicketFeeChargeDto(FeeTariff.REGULAR_TARIFF, Currency.PLN)

    val secondTicketFeeChargeDto =
      TicketFeeChargeDto(FeeTariff.VIP_TARIFF, Currency.PLN)

    When("charge fee for ticket is invoked")
    val twoResultTuppleEitherFuture = for {
      ticket <- ticketFuture
      ticketId = ticket.id.get
      firstResultEither <- parkingTicketService.chargeFeeForTicket(
        ticketId,
        firstTicketFeeChargeDto)

      secondResultEither <- parkingTicketService.chargeFeeForTicket(
        ticketId,
        secondTicketFeeChargeDto)

      feeFetchedFromDb <- parkingTicketService.getTicketFee(ticketId)

    } yield (firstResultEither, secondResultEither, feeFetchedFromDb)

    Then("first fee should have same id as second fee and be equal to fee fetched from db")
    twoResultTuppleEitherFuture.map {
      case (Some(Right(firstFee: Fee)),
            Some(Right(secondFee: Fee)),
            Some(feeFromDb: Fee)) =>
        firstFee.id should be(secondFee.id)
        secondFee should be(feeFromDb)

      case _ => fail()
    }
  }
}

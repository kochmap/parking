package pl.kochmap.parking.domain.integration.service

import java.time._

import controllers.dto.TicketFeeChargeDto
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.Matchers
import pl.kochmap.parking.domain.integration.AbstractIntegrationSpec
import pl.kochmap.parking.domain.money.{Currency, Fee, FeeTariff}
import pl.kochmap.parking.domain.{ParkingMeter, StoppedParkingTicket}
import pl.kochmap.parking.service.exception.CanNotChargeTicketFeeBecauseTicketHasNotBeenStoppedYetException
import slick.dbio

import scala.collection.immutable
import scala.concurrent.Future

class FeeServiceSpec extends AbstractIntegrationSpec with Matchers {

  import dbConfig._
  import pl.kochmap.parking.Util._

  val epislon = 1e-5

  implicit val doubleEq: Equality[Double] =
    TolerantNumerics.tolerantDoubleEquality(epislon)

  "Fee service" should "return earnings during given day" in {
    Given(
      "a fee service, parking meter, parking tickets in given day and other days, fees charged from this tickets")
    val givenDay = LocalDate.parse("2017-05-07")
    val timestampInGivenDay =
      givenDay.atStartOfDay(ZoneId.systemDefault()).toInstant

    val stoppedParkingTicketExample =
      StoppedParkingTicket(None,
                           None,
                           "abcde",
                           timestampInGivenDay,
                           timestampInGivenDay)

    val givenDayStoppedTickets =
      (1 to 5).map(_ => stoppedParkingTicketExample.copy())

    val anotherDaysStoppedTickets = (1 to 10).map(
      _ =>
        stoppedParkingTicketExample.copy(
          startTimestamp = timestampInGivenDay.minus(2 days),
          stopTimestamp = timestampInGivenDay.minus(2 days))) ++ (1 to 10).map(
      _ =>
        stoppedParkingTicketExample.copy(
          startTimestamp = timestampInGivenDay.minus(5 days),
          stopTimestamp = timestampInGivenDay.minus(5 days)))

    val parkingMeter =
      new ParkingMeter(None, "abcde", Nil)

    val feeChargeDto =
      TicketFeeChargeDto(FeeTariff.REGULAR_TARIFF, Currency.PLN)

    val givenDayEarningsFuture = for {
      parkingMeterId <- db.run(
        parkingMeterRepository.insertParkingMeter(parkingMeter))

      givenDayStoppedTicketsIds <- db.run(
        insertTicketQuery(parkingMeterId, givenDayStoppedTickets))

      anotherDayStoppedTicketIds <- db.run(
        insertTicketQuery(parkingMeterId, anotherDaysStoppedTickets))

      givenDayFeesOptionEither <- Future.sequence(
        givenDayStoppedTicketsIds.map(id =>
          parkingTicketService.chargeFeeForTicket(id, feeChargeDto)))

      _ <- Future.sequence(anotherDayStoppedTicketIds.map(id =>
        parkingTicketService.chargeFeeForTicket(id, feeChargeDto)))

      givenDayEarnings = earningsFromFees(givenDayFeesOptionEither)

    } yield (givenDayEarnings, feeChargeDto.currency)

    When("earning during is invoked")
    val resultFuture = for {
      _ <- givenDayEarningsFuture
      result <- feeService.earningsDuring(givenDay)
    } yield result

    Then("result should be given day earnings")
    for {
      expectedEarnings <- givenDayEarningsFuture
      actualEarnings <- resultFuture
    } yield {
      assert(actualEarnings._1 === expectedEarnings._1)
      actualEarnings._2 should be(expectedEarnings._2)
    }
  }

  "Fee service" should "return 0.0d as earnings during day if there aren't any fees in specific day" in {
    Given("a fee service and given day")
    val givenDay = LocalDate.parse("2017-05-07")

    When("earning during is invoked")
    val resultFuture = feeService.earningsDuring(givenDay)

    Then("result should be 0.0d")
    resultFuture.map {
      case (amount, _) => assert(amount === 0.0d)
      case _           => fail()
    }
  }

  private def insertTicketQuery(
      parkingMeterId: Long,
      stoppedTickets: immutable.IndexedSeq[StoppedParkingTicket]) = {
    dbio.DBIO.sequence(
      stoppedTickets
        .map(_.copy(parkingMeterId = Some(parkingMeterId)))
        .map(parkingTicketRepository.insertParkingTicket))
  }

  private def earningsFromFees(
      givenDayFeesOptionEther: Seq[Option[
        Either[CanNotChargeTicketFeeBecauseTicketHasNotBeenStoppedYetException,
               Fee]]]) = {
    givenDayFeesOptionEther
      .map(_.get)
      .map {
        case Right(fee) => fee.amountInCurrency
        case _          => 0
      }
      .foldLeft(0.0d)(_ + _)
  }
}

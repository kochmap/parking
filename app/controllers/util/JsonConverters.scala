package controllers.util

import controllers.dto.{TicketFeeChargeDto, VehicleDto}
import pl.kochmap.parking.domain.money.Currency.Currency
import pl.kochmap.parking.domain.money.FeeTariff.FeeTariff
import pl.kochmap.parking.domain.money.{Currency, Fee, FeeTariff}
import pl.kochmap.parking.domain.{ActiveParkingTicket, DomainException, StoppedParkingTicket}
import play.api.libs.json._

object JsonConverters {

  implicit val vehicleDtoFormat: OFormat[VehicleDto] = Json.format[VehicleDto]

  implicit val feeWrites: Writes[Fee] = (o: Fee) =>
    Json.obj(
      "amount" -> o.amountInCurrency,
      "currency" -> o.currencySnapshot.currency.toString
  )

  implicit val activeParkingTicketWrites: Writes[ActiveParkingTicket] =
    Json.writes[ActiveParkingTicket]

  implicit val stoppedParkingTicketWrites: Writes[StoppedParkingTicket] =
    Json.writes[StoppedParkingTicket]

  implicit val domainExceptionWrites: Writes[DomainException] =
    (o: DomainException) =>
      Json.obj(
        "message" -> o.message
    )

  implicit val feeTariffReads: Reads[FeeTariff] = Reads.enumNameReads(FeeTariff)

  implicit val currencyReads: Reads[Currency] = Reads.enumNameReads(Currency)

  implicit val ticketFeeChargeDtoReads: Reads[TicketFeeChargeDto] =
    Json.reads[TicketFeeChargeDto]

}

package controllers.dto

import pl.kochmap.parking.domain.money.Currency.Currency
import pl.kochmap.parking.domain.money.FeeTariff.FeeTariff

case class TicketFeeChargeDto(tariff: FeeTariff, currency: Currency)

package pl.kochmap.parking.domain.money

object FeeTariff extends Enumeration {
  type FeeTariff = Value
  val REGULAR_TARIFF, VIP_TARIFF = Value
}

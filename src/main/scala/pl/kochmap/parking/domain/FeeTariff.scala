package pl.kochmap.parking.domain

object FeeTariff extends Enumeration {
  type FeeTariff = Value
  val REGULAR_TARIFF, VIP_TARIFF = Value
}

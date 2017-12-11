package pl.kochmap.parking.domain.money

import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, GivenWhenThen}

class FeeSpec extends FlatSpec with GivenWhenThen {
  val epislon = 1e-4

  implicit val doubleEq: Equality[Double] =
    TolerantNumerics.tolerantDoubleEquality(epislon)

  "Fee" should "have been be computed from 0.003 to 0.01 using ceil" in {
    Given("fee in PLN on regular tariff where basic amount is 0.003")
    val fee = new Fee(None,
                      0.005,
                      CurrencySnapshot(Currency.PLN),
                      FeeTariff.REGULAR_TARIFF,
                      None)

    When("amount in currency is taken")
    val amountInCurrency = fee.amountInCurrency

    Then("result should be 0.01")
    assert(amountInCurrency === 0.01)
  }
}

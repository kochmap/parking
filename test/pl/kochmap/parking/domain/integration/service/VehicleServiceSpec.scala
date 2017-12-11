package pl.kochmap.parking.domain.integration.service

import controllers.dto.VehicleDto
import pl.kochmap.parking.domain.ParkingMeter
import pl.kochmap.parking.domain.integration.AbstractIntegrationSpec

class VehicleServiceSpec extends AbstractIntegrationSpec {

  import dbConfig._

  private val parkingMeter = new ParkingMeter(None, "1", Nil)

  private val vehicleDto = VehicleDto("abcde")

  "Vehicle service" should "tell that parking meter is started for vehicle when it has been started before" in {
    Given(
      "a vehicle service, a vehicle and parking meter which is already started and saved in db")

    val ticketEither = for {
      _ <- db.run(parkingMeterRepository.insertParkingMeter(parkingMeter))
      ticket <- parkingMeterService.startParkingMeter(parkingMeter.domainId,
                                                      vehicleDto)
    } yield ticket

    When("has vehicle has active parking meter is invoked")
    val result = for {
      _ <- ticketEither
      res <- vehicleService.hasVehicleHasActiveParkingMeter(
        vehicleDto.licensePlateNumber)
    } yield res

    Then("result should be true")
    result.map {
      case true  => succeed
      case false => fail()
    }
  }

  it should "tell that parking meter isn't started for vehicle" in {
    Given(
      "a vehicle service, a vehicle and parking meter which is saved in db and isn't started")

    val parkingMeterIdFuture = for {
      parkingMeterId <- db.run(
        parkingMeterRepository.insertParkingMeter(parkingMeter))
    } yield parkingMeterId

    When("has vehicle has active parking meter is invoked")
    val result = for {
      _ <- parkingMeterIdFuture
      res <- vehicleService.hasVehicleHasActiveParkingMeter(
        vehicleDto.licensePlateNumber)
    } yield res

    Then("result should be false")
    result.map {
      case false => succeed
      case true  => fail()
    }
  }
}

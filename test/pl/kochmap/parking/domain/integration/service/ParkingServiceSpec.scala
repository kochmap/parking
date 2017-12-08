package pl.kochmap.parking.domain.integration.service

import controllers.dto.VehicleDto
import pl.kochmap.parking.domain.integration.AbstractIntegrationSpec
import pl.kochmap.parking.domain._

class ParkingServiceSpec extends AbstractIntegrationSpec {

  import dbConfig._

  private val parkingMeter = new ParkingMeter(None, "1", Nil)

  private val vehicleDto = VehicleDto("abcde")

  "Parking service" should "start existing parking meter" in {

    Given("a parking service, a vehicle and one parking meter in db")
    val parkingMeterIdFuture =
      db.run(parkingMeterRepository.insertParkingMeter(parkingMeter))

    When("start parking meter is invoked")
    val ticketEither = for {
      _ <- parkingMeterIdFuture
      result <- parkingService.startParkingMeter(parkingMeter.domainId,
                                                 vehicleDto)
    } yield result

    Then("result should be active ticket")
    for {
      res <- ticketEither
    } yield
      res match {
        case Some(Right(_: ActiveParkingTicket)) => succeed
        case _                                   => fail()
      }
  }

  it should "start non-existing parking meter" in {
    Given("a parking service, a vehicle")

    When("start parking meter is invoked")
    val ticketEither = for {
      result <- parkingService.startParkingMeter("1", vehicleDto)
    } yield result

    Then("result should be active ticket")
    for {
      res <- ticketEither
    } yield
      res match {
        case Some(Right(_: ActiveParkingTicket)) => succeed
        case _                                   => fail()
      }
  }

  it should "not start already started parking meter" in {
    Given("a parking service, a vehicle and one meter in db")
    val parkingMeterIdFuture =
      db.run(parkingMeterRepository.insertParkingMeter(parkingMeter))

    When("start parking meter is invoked twice")

    val ticketEither = for {
      _ <- parkingMeterIdFuture
      _ <- parkingService.startParkingMeter(parkingMeter.domainId, vehicleDto)
      result <- parkingService.startParkingMeter(parkingMeter.domainId,
                                                 vehicleDto)
    } yield result

    Then("result should be a exception that parking meter was started")

    for {
      res <- ticketEither
    } yield
      res match {
        case Some(Left(_: ParkingMeterAlreadyStartedException)) => succeed
        case _                                                  => fail()
      }
  }

  it should "stop started parking meter" in {

    Given("a parking service, a vehicle and one parking meter in db")
    val parkingMeterIdFuture =
      db.run(parkingMeterRepository.insertParkingMeter(parkingMeter))

    When("start parking meter is invoked and after that stop too")
    val ticketEither = for {
      _ <- parkingMeterIdFuture
      _ <- parkingService.startParkingMeter(parkingMeter.domainId, vehicleDto)
      result <- parkingService.stopParkingMeter(parkingMeter.domainId)
    } yield result

    Then("result should be stopped parking ticket")
    for {
      res <- ticketEither
    } yield
      res match {
        case Some(Right(_: StoppedParkingTicket)) => succeed
        case _                                    => fail()
      }
  }

  it should "not stop stopped(new) parking meter" in {
    Given("a parking service")

    When("stop parking meter is invoked")
    val ticketEither = for {
      result <- parkingService.stopParkingMeter("1")
    } yield result

    Then("result should be a exception that parking meter was already stopped")
    for {
      res <- ticketEither
    } yield
      res match {
        case Some(Left(_: ParkingMeterAlreadyStoppedException)) => succeed
        case _                                                  => fail()
      }
  }

  it should "not stop already stopped parking meter" in {
    Given("a parking service, a vehicle and one parking meter in db")
    val parkingMeterIdFuture =
      db.run(parkingMeterRepository.insertParkingMeter(parkingMeter))

    When("start parking, stop parking and stop parking is invoked")
    val ticketEither = for {
      _ <- parkingMeterIdFuture
      _ <- parkingService.startParkingMeter(parkingMeter.domainId, vehicleDto)
      _ <- parkingService.stopParkingMeter(parkingMeter.domainId)
      result <- parkingService.stopParkingMeter(parkingMeter.domainId)

    } yield result

    Then("result should be a exception that parking meter was already stopped")
    for {
      res <- ticketEither
    } yield
      res match {
        case Some(Left(_: ParkingMeterAlreadyStoppedException)) => succeed
        case _                                                  => fail()
      }
  }

  it should "tell that parking meter is started for vehicle when it before occurred" in {
    Given(
      "a parking service, a vehicle and parking meter which is started in db")

    val ticketEither = for {
      _ <- db.run(parkingMeterRepository.insertParkingMeter(parkingMeter))
      ticket <- parkingService.startParkingMeter(parkingMeter.domainId,
                                                 vehicleDto)
    } yield ticket

    When("has vehicle has active parking meter is invoked")
    val result = for {
      _ <- ticketEither
      res <- parkingService.hasVehicleHasActiveParkingMeter(
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
      "a parking service, a vehicle and parking meter which isn't started in db")

    val parkingMeterIdFuture = for {
      parkingMeterId <- db.run(parkingMeterRepository.insertParkingMeter(parkingMeter))
    } yield parkingMeterId

    When("has vehicle has active parking meter is invoked")
    val result = for {
      _ <- parkingMeterIdFuture
      res <- parkingService.hasVehicleHasActiveParkingMeter(
        vehicleDto.licensePlateNumber)
    } yield res

    Then("result should be false")
    result.map {
      case false  => succeed
      case true => fail()
    }
  }

}

package pl.kochmap.parking.domain.integration.service

import controllers.dto.VehicleDto
import pl.kochmap.parking.domain.integration.AbstractIntegrationSpec
import pl.kochmap.parking.domain._

class ParkingMeterServiceSpec extends AbstractIntegrationSpec {

  import dbConfig._

  private val parkingMeter = new ParkingMeter(None, "1", Nil)

  private val vehicleDto = VehicleDto("abcde")

  "Parking meter service" should "start existing parking meter" in {

    Given("a parking meter service, a vehicle and one parking meter in db")
    val parkingMeterIdFuture =
      db.run(parkingMeterRepository.insertParkingMeter(parkingMeter))

    When("start parking meter is invoked")
    val ticketEither = for {
      _ <- parkingMeterIdFuture
      result <- parkingMeterService.startParkingMeter(parkingMeter.domainId,
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
    Given("a parking meter service, a vehicle")

    When("start parking meter is invoked")
    val ticketEither = for {
      result <- parkingMeterService.startParkingMeter("1", vehicleDto)
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
    Given("a parking meter service, a vehicle and one meter in db")
    val parkingMeterIdFuture =
      db.run(parkingMeterRepository.insertParkingMeter(parkingMeter))

    When("start parking meter is invoked twice")

    val ticketEither = for {
      _ <- parkingMeterIdFuture
      _ <- parkingMeterService.startParkingMeter(parkingMeter.domainId, vehicleDto)
      result <- parkingMeterService.startParkingMeter(parkingMeter.domainId,
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

    Given("a parking meter service, a vehicle and one parking meter in db")
    val parkingMeterIdFuture =
      db.run(parkingMeterRepository.insertParkingMeter(parkingMeter))

    When("start parking meter is invoked and after that stop too")
    val ticketEither = for {
      _ <- parkingMeterIdFuture
      _ <- parkingMeterService.startParkingMeter(parkingMeter.domainId, vehicleDto)
      result <- parkingMeterService.stopParkingMeter(parkingMeter.domainId)
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
    Given("a parking meter service")

    When("stop parking meter is invoked")
    val ticketEither = for {
      result <- parkingMeterService.stopParkingMeter("1")
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
    Given("a parking meter service, a vehicle and one parking meter in db")
    val parkingMeterIdFuture =
      db.run(parkingMeterRepository.insertParkingMeter(parkingMeter))

    When("start parking, stop parking and stop parking is invoked")
    val ticketEither = for {
      _ <- parkingMeterIdFuture
      _ <- parkingMeterService.startParkingMeter(parkingMeter.domainId, vehicleDto)
      _ <- parkingMeterService.stopParkingMeter(parkingMeter.domainId)
      result <- parkingMeterService.stopParkingMeter(parkingMeter.domainId)

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

}

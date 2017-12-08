package pl.kochmap.parking.domain.integration

import org.scalatest._
import pl.kochmap.parking.repository.{ParkingMeterRepository, Tables}
import pl.kochmap.parking.service.ParkingService
import play.api.{Application, Logger}
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait AbstractIntegrationSpec
    extends AsyncFlatSpec
    with GivenWhenThen
    with BeforeAndAfterEach {

  val app: Application = AbstractIntegrationSpec.app

  val injector: Injector = app.injector

  val tables: Tables = injector.instanceOf[Tables]

  val parkingService: ParkingService = injector.instanceOf[ParkingService]

  val dbConfigProvider: DatabaseConfigProvider =
    injector.instanceOf[DatabaseConfigProvider]

  val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  val parkingMeterRepository: ParkingMeterRepository =
    injector.instanceOf[ParkingMeterRepository]

  override protected def beforeEach(): Unit = {
    val dbConf = dbConfig
    import dbConf._
    import profile.api._
    db.run(sql"DROP ALL OBJECTS".asUpdate)
    Await.result(tables.createSchema(), Duration.Inf)
  }
}

object AbstractIntegrationSpec {
  private val app: Application = GuiceApplicationBuilder()
    .configure(
      Map(
        "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
        "slick.dbs.default.db.driver" -> "org.h2.Driver",
        "slick.dbs.default.db.url" -> "jdbc:h2:mem:play;DB_CLOSE_DELAY=-1",
        "logger.file" -> "conf/logback.test.xml"
      ))
    .build()

}

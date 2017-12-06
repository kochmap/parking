package pl.kochmap.parking.repository

import java.sql.Timestamp
import java.time.Instant
import javax.inject.{Inject, Singleton}

import pl.kochmap.parking.domain.money.Currency
import pl.kochmap.parking.domain.money.Currency.Currency
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, ProvenShape}

import scala.concurrent.ExecutionContext

@Singleton
class Tables @Inject()(val dbConfigProvider: DatabaseConfigProvider)(
    implicit executionContext: ExecutionContext) {

  val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  implicit val instantColumnType = MappedColumnType.base[Instant, Timestamp](
    { instant =>
      if (instant == null) null else new Timestamp(instant.toEpochMilli)
    }, { timestamp =>
      if (timestamp == null) null else Instant.ofEpochMilli(timestamp.getTime)
    }
  )

  implicit val currencyColumnType = MappedColumnType.base[Currency, String](
    c => c.toString,
    s => Currency.withName(s)
  )

  class Vehicles(tag: Tag) extends Table[VehicleRow](tag, "vehicles") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def licensePlateNumber: Rep[String] = column[String]("license_plate_number")

    def * : ProvenShape[VehicleRow] =
      (id.?, licensePlateNumber) <> (VehicleRow.tupled, VehicleRow.unapply)
  }

  lazy val vehicles = TableQuery[Vehicles]

  class ParkingMeters(tag: Tag)
      extends Table[ParkingMeterRow](tag, "parking_meters") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name: Rep[String] = column[String]("name")

    def * : ProvenShape[ParkingMeterRow] =
      (id.?, name) <> (ParkingMeterRow.tupled, ParkingMeterRow.unapply)

  }

  lazy val parkingMeters = TableQuery[ParkingMeters]

  class ParkingTickets(tag: Tag)
      extends Table[ParkingTicketRow](tag, "parking_tickets") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def parkingMeterId: Rep[Long] = column[Long]("parking_meter_id")

    def vehicleId: Rep[Long] = column[Long]("vehicle_id")

    def startTimestamp: Rep[Instant] = column[Instant]("start_timestamp")

    def stopTimestamp: Rep[Instant] = column[Instant]("stop_timestamp")

    def * : ProvenShape[ParkingTicketRow] =
      (id.?, parkingMeterId.?, vehicleId.?, startTimestamp, stopTimestamp.?) <> (ParkingTicketRow.tupled, ParkingTicketRow.unapply)

    def parkingSpace: ForeignKeyQuery[ParkingMeters, ParkingMeterRow] =
      foreignKey("fk_parking_tickets_parking_meter_id",
                 parkingMeterId,
                 parkingMeters)(_.id)

    def vehicle: ForeignKeyQuery[Vehicles, VehicleRow] =
      foreignKey("fk_parking_tickets_vehicle_id", vehicleId, vehicles)(_.id)
  }

  lazy val parkingTickets = TableQuery[ParkingTickets]

  class Fees(tag: Tag) extends Table[FeeRow](tag, "fees") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def baseAmount: Rep[Double] = column[Double]("base_amount")

    def exchangeRate: Rep[Double] = column[Double]("exchange_rate")

    def currency: Rep[Currency] = column[Currency]("currency")

    def amountInCurrency: Rep[Double] = column[Double]("amount_in_currency")

    def parkingTicketId: Rep[Long] = column[Long]("parking_ticket_id")

    def * : ProvenShape[FeeRow] =
      (id.?,
       baseAmount,
       exchangeRate,
       currency,
       amountInCurrency,
       parkingTicketId.?) <> (FeeRow.tupled, FeeRow.unapply)

    def parkingTicket: ForeignKeyQuery[ParkingTickets, ParkingTicketRow] =
      foreignKey("fk_fees_parking_ticket_id_id",
                 parkingTicketId,
                 parkingTickets)(_.id)
  }

  lazy val fees = TableQuery[Fees]

  private def createSchema() = {
    Logger.info("Creating schema for db")
    val schema = vehicles.schema ++ parkingMeters.schema ++ parkingTickets.schema ++ fees.schema
    db.run(schema.create)
  }

  createSchema()

}

case class VehicleRow(id: Option[Long], licensePlateNumber: String)

case class ParkingMeterRow(id: Option[Long], name: String)

case class ParkingTicketRow(id: Option[Long],
                            parkingMeterId: Option[Long],
                            vehicleId: Option[Long],
                            startTimestamp: Instant,
                            stopTimestampOption: Option[Instant])

case class FeeRow(id: Option[Long],
                  baseAmount: Double,
                  exchangeRate: Double,
                  currency: Currency,
                  amountInCurrency: Double,
                  parkingTicketId: Option[Long])

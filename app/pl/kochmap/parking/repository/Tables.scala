package pl.kochmap.parking.repository

import java.sql.Timestamp
import java.time.Instant
import javax.inject.{Inject, Singleton}

import pl.kochmap.parking.domain.money.{Currency, FeeTariff}
import pl.kochmap.parking.domain.money.Currency.Currency
import pl.kochmap.parking.domain.money.FeeTariff.FeeTariff
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, ProvenShape}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

@Singleton
class Tables @Inject()(dbConfigProvider: DatabaseConfigProvider)(
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

  implicit val feeTariffColumnType = MappedColumnType.base[FeeTariff, String](
    ft => ft.toString,
    s => FeeTariff.withName(s)
  )

  class ParkingMeters(tag: Tag)
      extends Table[ParkingMeterRow](tag, "parking_meters") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def domainId: Rep[String] = column[String]("domain_id", O.Unique)

    def * : ProvenShape[ParkingMeterRow] =
      (id.?, domainId) <> (ParkingMeterRow.tupled, ParkingMeterRow.unapply)

  }

  lazy val parkingMeters = TableQuery[ParkingMeters]

  class ParkingTickets(tag: Tag)
      extends Table[ParkingTicketRow](tag, "parking_tickets") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def parkingMeterId: Rep[Long] = column[Long]("parking_meter_id")

    def vehicleLicensePlateNumber: Rep[String] =
      column[String]("vehicle_license_plate_number")

    def startTimestamp: Rep[Instant] = column[Instant]("start_timestamp")

    def stopTimestamp: Rep[Option[Instant]] =
      column[Option[Instant]]("stop_timestamp")

    def * : ProvenShape[ParkingTicketRow] =
      (id.?,
       parkingMeterId.?,
       vehicleLicensePlateNumber,
       startTimestamp,
       stopTimestamp) <> (ParkingTicketRow.tupled, ParkingTicketRow.unapply)

    def parkingMeter: ForeignKeyQuery[ParkingMeters, ParkingMeterRow] =
      foreignKey("fk_parking_tickets_parking_meter_id",
                 parkingMeterId,
                 parkingMeters)(_.id)
  }

  lazy val parkingTickets = TableQuery[ParkingTickets]

  class Fees(tag: Tag) extends Table[FeeRow](tag, "fees") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def baseAmount: Rep[Double] = column[Double]("base_amount")

    def exchangeRate: Rep[Double] = column[Double]("exchange_rate")

    def currency: Rep[Currency] = column[Currency]("currency")

    def amountInCurrency: Rep[Double] = column[Double]("amount_in_currency")

    def feeTariff: Rep[FeeTariff] = column[FeeTariff]("fee_tariff")

    def parkingTicketId: Rep[Long] = column[Long]("parking_ticket_id")

    def * : ProvenShape[FeeRow] =
      (id.?,
       baseAmount,
       exchangeRate,
       currency,
       amountInCurrency,
       feeTariff,
       parkingTicketId.?) <> (FeeRow.tupled, FeeRow.unapply)

    def parkingTicket: ForeignKeyQuery[ParkingTickets, ParkingTicketRow] =
      foreignKey("fk_fees_parking_ticket_id_id",
                 parkingTicketId,
                 parkingTickets)(_.id)
  }

  lazy val fees = TableQuery[Fees]

  def createSchema() = {
    Logger.info("Creating schema for db")
    val schema = parkingMeters.schema ++ parkingTickets.schema ++ fees.schema
    Await.ready(db.run(schema.create), Duration.Inf)
  }

  createSchema()

}
case class ParkingMeterRow(id: Option[Long], domainId: String)

case class ParkingTicketRow(id: Option[Long],
                            parkingMeterId: Option[Long],
                            vehicleLicensePlateNumber: String,
                            startTimestamp: Instant,
                            stopTimestampOption: Option[Instant])

case class FeeRow(id: Option[Long],
                  baseAmount: Double,
                  exchangeRate: Double,
                  currency: Currency,
                  amountInCurrency: Double,
                  feeTariff: FeeTariff,
                  parkingTicketId: Option[Long])

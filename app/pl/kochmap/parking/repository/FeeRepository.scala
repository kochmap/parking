package pl.kochmap.parking.repository

import java.time.{LocalDate, ZoneId, ZonedDateTime}
import javax.inject.{Inject, Singleton}

import pl.kochmap.parking.domain.money.Fee

import scala.concurrent.ExecutionContext

@Singleton
class FeeRepository @Inject()(tables: Tables)(
    implicit executionContext: ExecutionContext) {

  import tables._
  import dbConfig._
  import profile.api._

  def insertOrUpdate(fee: Fee): slick.dbio.DBIO[Long] = {
    (fees returning fees.map(_.id) insertOrUpdate fee.toFeeRow)
      .map(_.getOrElse(fee.id.get))
  }

  def findById(id: Long): slick.dbio.DBIO[Option[Fee]] = {
    val findQuery = fees.filter(_.id === id)
    buildHeadOptionFromFeeQuery(findQuery)
  }

  def findByTicketId(ticketId: Long): slick.dbio.DBIO[Option[Fee]] = {
    val findQuery = fees
      .filter(_.parkingTicketId === ticketId)
    buildHeadOptionFromFeeQuery(findQuery)
  }

  def feesDuring(localDate: LocalDate): slick.dbio.DBIO[Seq[Fee]] = {
    (fees join parkingTickets on (_.parkingTicketId === _.id))
      .filter {
        case (_, parkingTicket) =>
          parkingTicket.stopTimestamp >= localDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant && parkingTicket.stopTimestamp < localDate
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant
      }
      .map(_._1)
      .result
      .map(_.map(new Fee(_)))
  }

  private def buildHeadOptionFromFeeQuery(
      findQuery: Query[tables.Fees, FeeRow, Seq]) = {
    findQuery.result.headOption
      .map(_.map(new Fee(_)))
  }
}

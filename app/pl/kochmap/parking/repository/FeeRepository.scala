package pl.kochmap.parking.repository

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
    buildFeeFromFindQuery(findQuery)
  }

  def findByTicketId(ticketId: Long): slick.dbio.DBIO[Option[Fee]] = {
    val findQuery = fees
      .filter(_.parkingTicketId === ticketId)
    buildFeeFromFindQuery(findQuery)
  }

  private def buildFeeFromFindQuery(
      findQuery: Query[tables.Fees, FeeRow, Seq]) = {
    findQuery.result.headOption
      .map(_.map(new Fee(_)))
  }
}

package pl.kochmap.parking.service

import javax.inject.{Inject, Singleton}

import controllers.dto.TicketFeeChargeDto
import pl.kochmap.parking.domain.{ActiveParkingTicket, StoppedParkingTicket}
import pl.kochmap.parking.domain.money.{
  CurrencySnapshot,
  Fee,
  ParkingFeeCalculator
}
import pl.kochmap.parking.repository.{FeeRepository, ParkingTicketRepository}
import pl.kochmap.parking.service.exception.CanNotChargeTicketFeeBecauseTicketHasNotBeenStoppedYetException
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ParkingTicketService @Inject()(
    val dbConfigProvider: DatabaseConfigProvider,
    parkingTicketRepository: ParkingTicketRepository,
    feeRepository: FeeRepository)(implicit executionContext: ExecutionContext)
    extends TransactionalService {

  import dbConfig._
  import profile.api._

  def chargeFeeForTicket(
      ticketId: Long,
      ticketFeeChargeDtoDto: TicketFeeChargeDto): Future[Option[
    Either[CanNotChargeTicketFeeBecauseTicketHasNotBeenStoppedYetException,
           Fee]]] = {
    parkingTicketRepository
      .findById(ticketId)
      .map {
        case Some(stoppedParkingTicket: StoppedParkingTicket) =>
          Some(
            Right(
              ParkingFeeCalculator.calculateFee(
                stoppedParkingTicket,
                CurrencySnapshot(ticketFeeChargeDtoDto.currency),
                ticketFeeChargeDtoDto.tariff)))
        case Some(_) =>
          Some(Left(
            new CanNotChargeTicketFeeBecauseTicketHasNotBeenStoppedYetException))
        case _ => None
      }
      .flatMap {
        case Some(Right(fee)) =>
          for {
            id <- feeRepository.insertOrUpdate(fee)
            fetchedFromDb <- feeRepository.findById(id)
          } yield fetchedFromDb.map(Right(_))
        case s @ Some(Left(_)) => DBIO.successful(s)
        case _                 => DBIO.successful(None)
      }
  }

  def getTicketFee(ticketId: Long): Future[Option[Fee]] = {
    feeRepository.findByTicketId(ticketId)
  }
}

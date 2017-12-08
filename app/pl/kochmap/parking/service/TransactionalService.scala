package pl.kochmap.parking.service

import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

trait TransactionalService {

  val dbConfigProvider: DatabaseConfigProvider
  val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  implicit def runTransactionallyOnDb[R](dbio: DBIO[R]): Future[R] =
    db.run(dbio.transactionally)
}

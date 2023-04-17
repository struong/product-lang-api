package com.struong.pure

import cats.effect._
import cats.implicits._
import com.comcast.ip4s.{Host, Port}
import config.{ApiConf, ServiceConf}
import db.{DatabaseMigrator, DoobieRepository, FlywayDatabaseMigrator}
import doobie._
import eu.timepit.refined.auto._
import eu.timepit.refined.pureconfig._
import http.Routes.{ProductRoutes, ProductsRoutes}
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import pureconfig._
import pureconfig.generic.auto._

object PureMain extends IOApp.Simple {
  object MkHttpServer {
    def run[F[_]: Async](cfg: ApiConf, httpApp: HttpApp[F]): F[Nothing] = {
      for {
        _ <-
          EmberServerBuilder
            .default[F]
            .withPort(
              Port
                .fromInt(cfg.port.value)
                .getOrElse(throw new RuntimeException("No port found"))
            )
            .withHost(
              Host
                .fromString(cfg.host.value)
                .getOrElse(throw new RuntimeException("No host found"))
            )
            .withHttpApp(httpApp)
            .build
      } yield ()
    }.useForever
  }

  override def run: IO[Unit] = {
    val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator

    val serviceConf = ConfigSource.default.loadOrThrow[ServiceConf]
    val databaseConf = serviceConf.database
    val apiConf = serviceConf.api

    for {
      migrate <-
        migrator.migrate(
          databaseConf.url,
          databaseConf.user,
          databaseConf.pass
        )
      tx = Transactor.fromDriverManager[IO](
        databaseConf.driver,
        databaseConf.url,
        databaseConf.user,
        databaseConf.pass
      )
      repo = new DoobieRepository(tx)
      productsRoutes = new ProductsRoutes(repo)
      productRoutes = new ProductRoutes(repo)
      routes = productRoutes.routes <+> productsRoutes.routes
      server <- MkHttpServer.run[IO](apiConf, routes.orNotFound)
    } yield server
  }
}

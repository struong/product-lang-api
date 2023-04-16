import cats.effect._
import cats.implicits._
import com.comcast.ip4s.{Host, Port}
import com.typesafe.config._
import config.ServiceConf
import db.{DatabaseMigrator, DoobieRepository, FlywayDatabaseMigrator}
import doobie._
import eu.timepit.refined.auto._
import eu.timepit.refined.pureconfig._
import http.Routes.{ProductRoutes, ProductsRoutes}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router
import pureconfig._
import pureconfig.generic.auto._
import com.typesafe.config._

object PureMain extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator

    val serviceConf = ConfigSource.default.loadOrThrow[ServiceConf]
    val databaseConf = serviceConf.database
    val apiConf = serviceConf.api

    for {
      migrate <- migrator.migrate(
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
      server <- EmberServerBuilder
        .default[IO]
        .withPort(
          Port
            .fromInt(apiConf.port.value)
            .getOrElse(throw new RuntimeException("No port found"))
        )
        .withHost(
          Host
            .fromString(apiConf.host.value)
            .getOrElse(throw new RuntimeException("No host found"))
        )
        .withHttpApp(routes.orNotFound)
        .build
        .use(_ => IO.never)
        .as(ExitCode.Success)
    } yield server

  }
}

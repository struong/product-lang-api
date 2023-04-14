import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import db.Repository
import http.Routes
import org.flywaydb.core.Flyway
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object ImpureMain {
  def main(args: Array[String]): Unit = {
    def getConfig(path: String)(implicit system: ActorSystem[_]) =
      system.settings.config.getString(path)

    def startHttpServer(
        routes: Route
    )(implicit system: ActorSystem[_]): Unit = {
      import system.executionContext

      val host = getConfig("api.host")
      val port = getConfig("api.port").toInt

      val futureBinding =
        Http().newServerAt(host, port).bind(routes)

      futureBinding.onComplete {
        case Success(binding) =>
          val address = binding.localAddress
          system.log.info(
            "Server online at http://{}:{}/",
            address.getHostString,
            address.getPort
          )
        case Failure(ex) =>
          system.log.error(
            "Failed to bind HTTP endpoint, terminating system",
            ex
          )
          system.terminate()
      }
    }

    def createRepository()(implicit system: ActorSystem[_]): Repository = {
      val url = "jdbc:postgresql://" + getConfig(
        "database.db.properties.serverName"
      ) + ":" + getConfig(
        "database.db.properties.portNumber"
      ) + "/" + getConfig(
        "database.db.properties.databaseName"
      )

      // Run flyway migration
      val user = getConfig("database.db.properties.user")
      val pass = getConfig("database.db.properties.password")
      val flyway = Flyway.configure().dataSource(url, user, pass).load()
      flyway.migrate()

      val dbConfig: DatabaseConfig[JdbcProfile] =
        DatabaseConfig.forConfig("database", system.settings.config)

      new Repository(dbConfig)
    }

    val rootBehavior: Behavior[Nothing] = Behaviors.setup[Nothing] { context =>
      implicit val ec: ExecutionContextExecutor = context.executionContext
      implicit val system: ActorSystem[Nothing] = context.system

      val repo = createRepository()

      val routes = new Routes(repo)
      startHttpServer(routes.allRoutes)

      Behaviors.empty
    }

    ActorSystem[Nothing](rootBehavior, "ImpureHttpServer")
  }
}

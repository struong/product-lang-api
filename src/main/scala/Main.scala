import akka.actor.ActorSystem
import db.Repository
import org.flywaydb.core.Flyway
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

object Main extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = system.dispatcher

  def getConfig(path: String) = system.settings.config.getString(path)

  val url = "jdbc:postgresql://" + getConfig(
    "database.db.properties.serverName"
  ) + ":" + getConfig(
    "database.db.properties.portNumber"
  ) + "/" + getConfig(
    "database.db.properties.databaseName"
  )
  val user = getConfig("database.db.properties.user")
  val pass = getConfig("database.db.properties.password")
  val flyway = Flyway.configure().dataSource(url, user, pass).load()
  flyway.migrate()

  protected val dbConfig: DatabaseConfig[JdbcProfile] =
    DatabaseConfig.forConfig("database", system.settings.config)
  protected val repo = new Repository(dbConfig)
}

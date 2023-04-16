package com.struong.impure

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorSystem
import com.typesafe.config.ConfigFactory
import org.flywaydb.core.Flyway
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpecLike
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import java.net.ServerSocket

trait BaseSpec
    extends AsyncWordSpecLike
    with Matchers
    with ScalaCheckPropertyChecks
    with BeforeAndAfterAll
    with BeforeAndAfterEach {

  val testKit: ActorTestKit = ActorTestKit(
    "it-test",
    ConfigFactory
      .parseString(s"api.port=${BaseSpec.findAvailablePort()}")
      .withFallback(ConfigFactory.load())
  )

  implicit val system: ActorSystem[Nothing] = testKit.system

  private val url = "jdbc:postgresql://" +
    system.settings.config.getString("database.db.properties.serverName") +
    ":" + system.settings.config
    .getString("database.db.properties.portNumber") +
    "/" + system.settings.config
    .getString("database.db.properties.databaseName")
  private val user = system.settings.config.getString("database.db.properties.user")
  private val pass =
    system.settings.config.getString("database.db.properties.password")

  protected val flyway: Flyway =
    Flyway.configure().dataSource(url, user, pass).load()

  override protected def afterAll(): Unit =
    testKit.shutdownTestKit()

  override protected def beforeAll(): Unit = {
    val _ = flyway.migrate()
  }

}

object BaseSpec {

  def findAvailablePort(): Int = {
    val serverSocket = new ServerSocket(0)
    val freePort = serverSocket.getLocalPort
    serverSocket.setReuseAddress(true) // Allow instant rebinding of the socket.
    serverSocket.close()
    freePort
  }
}

import sbt._

object Dependencies {
  val akkaVersion = "2.8.0"
  val akkaHttpVersion = "10.5.0"
  val akka: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  )

  val doobieVersion = "1.0.0-RC1"
  val doobie: Seq[ModuleID] = Seq(
    // Start with this one
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-h2", // H2 driver 1.4.200 + type mappings.
    "org.tpolecat" %% "doobie-hikari", // HikariCP transactor.
    "org.tpolecat" %% "doobie-postgres" // Postgres driver 42.3.1 + type mappings.
  ).map(_ % doobieVersion)

  val circeVersion = "0.14.1"
  val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-refined"
  ).map(_ % circeVersion)

  val refinedVersion = "0.10.3"
  val refined: Seq[ModuleID] = Seq(
    "eu.timepit" %% "refined",
    "eu.timepit" %% "refined-cats"
  ).map(_ % refinedVersion)

  val slickVersion = "3.4.1"
  val slick: Seq[ModuleID] =
    Seq("com.typesafe.slick" %% "slick").map(_ % slickVersion)

  val compile: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core" % "2.9.0",
    "com.github.pureconfig" %% "pureconfig" % "0.17.2",
    "org.flywaydb" % "flyway-core" % "9.16.0",
    "org.postgresql" % "postgresql" % "42.5.4"
  ) ++ akka ++ doobie ++ circe ++ refined ++ slick

  val test: Seq[ModuleID] = Seq(
    "eu.timepit" %% "refined-scalacheck" % "0.10.2" % Test,
    "org.mockito" %% "mockito-scala-scalatest" % "1.17.12" % Test,
    "com.github.tomakehurst" % "wiremock-jre8" % "2.35.0" % "test, it",
    // generation of arbitrary case classes
    "com.chuusai" %% "shapeless" % "2.3.10" % Test,
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.16" % "1.3.1" % Test,
    "org.tpolecat" %% "doobie-scalatest" % doobieVersion % Test // ScalaTest support for typechecking statements.
  )
}

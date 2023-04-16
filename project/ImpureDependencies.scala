import sbt._

object ImpureDependencies {
  val akkaVersion = "2.8.0"
  val akkaHttpVersion = "10.5.0"
  val akka: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  )

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
    Seq(
      "com.typesafe.slick" %% "slick",
      "com.typesafe.slick" %% "slick-hikaricp"
    ).map(_ % slickVersion)

  val akkaHttpJsonVersion = "1.29.1"
  val akkaHttpJson: Seq[ModuleID] = Seq(
    "de.heikoseeberger" %% "akka-http-circe"
  ).map(_ % akkaHttpJsonVersion)

  val compile: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core" % "2.9.0",
    "org.flywaydb" % "flyway-core" % "9.16.0",
    "org.postgresql" % "postgresql" % "42.5.4",
    "ch.qos.logback" % "logback-classic" % "1.4.6"
  ) ++ akka ++ circe ++ refined ++ slick ++ akkaHttpJson

  val test: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
    "eu.timepit" %% "refined-scalacheck" % "0.10.2",
    "org.scalatest" %% "scalatest" % "3.2.15",
    "org.scalatestplus" %% "scalacheck-1-17" % "3.2.15.0"
  ).map(_ % "test, it")
}

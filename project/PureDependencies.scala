import sbt._

object PureDependencies {
  val http4sVersion = "1.0.0-M39"
  val http4s: Seq[ModuleID] = Seq(
    "org.http4s" %% "http4s-ember-client",
    "org.http4s" %% "http4s-ember-server",
    "org.http4s" %% "http4s-circe",
    "org.http4s" %% "http4s-dsl"
  ).map(_ % http4sVersion)

  val doobieVersion = "1.0.0-RC1"
  val doobie: Seq[ModuleID] = Seq(
    // Start with this one
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-hikari",
    "org.tpolecat" %% "doobie-postgres",
    "org.tpolecat" %% "doobie-refined"
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
    "eu.timepit" %% "refined-cats",
    "eu.timepit" %% "refined-pureconfig"
  ).map(_ % refinedVersion)

  val compile: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core" % "2.9.0",
    "com.github.pureconfig" %% "pureconfig" % "0.17.2",
    "org.flywaydb" % "flyway-core" % "9.16.0",
    "org.postgresql" % "postgresql" % "42.5.4",
    "ch.qos.logback" % "logback-classic" % "1.4.6"
  ) ++ doobie ++ circe ++ refined ++ http4s

  val test: Seq[ModuleID] = Seq(
    "eu.timepit" %% "refined-scalacheck" % "0.10.2" % Test,
    "org.mockito" %% "mockito-scala-scalatest" % "1.17.12" % Test,
    // generation of arbitrary case classes
    "com.chuusai" %% "shapeless" % "2.3.10" % Test,
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.16" % "1.3.1" % Test,
    "org.tpolecat" %% "doobie-scalatest" % doobieVersion % Test // ScalaTest support for typechecking statements.
  )
}

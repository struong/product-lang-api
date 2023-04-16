ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val impure = (project in file("impure"))
  .settings(
    // Run in a separate JVM, to make sure sbt waits until all threads have
    // finished before returning.
    fork := true,
    name := "impure-product-lang-api",
    scalacOptions ++= Seq("-Ymacro-annotations"),
    libraryDependencies ++= ImpureDependencies.compile ++ ImpureDependencies.test
  )

lazy val pure = (project in file("pure"))
  .settings(
    name := "pure-product-lang-api",
    scalacOptions ++= Seq("-Ymacro-annotations"),
    libraryDependencies ++= PureDependencies.compile ++ PureDependencies.test
  )

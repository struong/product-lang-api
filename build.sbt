ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

// Run in a separate JVM, to make sure sbt waits until all threads have
// finished before returning.
// If you want to keep the application running while executing other
// sbt tasks, consider https://github.com/spray/sbt-revolver/
fork := true

lazy val impure = (project in file("impure"))
  .settings(
    name := "impure-product-lang-api",
    scalacOptions ++= Seq("-Ymacro-annotations"),
    libraryDependencies ++= Dependencies.compile ++ Dependencies.test
  )

lazy val pure = (project in file("pure"))
  .settings(
    name := "pure-product-lang-api",
    scalacOptions ++= Seq("-Ymacro-annotations"),
    libraryDependencies ++= Dependencies.compile ++ Dependencies.test
  )

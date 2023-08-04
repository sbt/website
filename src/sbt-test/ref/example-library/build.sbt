ThisBuild / scalaVersion := "2.13.6"
ThisBuild / organization := "com.example"

lazy val hello = project
  .in(file("."))
  .settings(
    name := "Hello",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client4" %% "core" % "4.0.0-M2",
      "com.lihaoyi" %% "ujson" % "3.1.2",
      "org.scalatest" %% "scalatest" % "3.2.16" % Test
    )
  )

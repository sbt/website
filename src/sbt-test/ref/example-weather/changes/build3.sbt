ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "2.13.11"
ThisBuild / organization := "com.example"

val scalaTest = "org.scalatest" %% "scalatest" % "3.2.16"
val sttp = "com.softwaremill.sttp.client4" %% "core" % "4.0.0-M2"
val ujson = "com.lihaoyi" %% "ujson" % "3.1.2"

lazy val hello = project
  .in(file("."))
  .aggregate(helloCore)
  .dependsOn(helloCore)
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "Hello",
    libraryDependencies += scalaTest % Test
  )

lazy val helloCore = project
  .in(file("core"))
  .settings(
    name := "Hello Core",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      sttp,
      ujson
    )
  )

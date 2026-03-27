ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / homepage := Some(url("https://github.com/sbt/sbt-obfuscate"))

lazy val root = rootProject
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-obfuscate",
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "3" => "2.0.0-RC10" // set minimum sbt version
      }
    }
  )

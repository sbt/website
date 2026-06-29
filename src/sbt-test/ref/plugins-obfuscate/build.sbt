version := "0.1.0-SNAPSHOT"
organization := "com.example"
homepage := Some(url("https://github.com/sbt/sbt-obfuscate"))

lazy val root = rootProject
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-obfuscate",
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match
        case "3" => "2.0.1" // set minimum sbt version
    }
  )

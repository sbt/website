ThisBuild / scalaVersion     := "2.13.4"
ThisBuild / version          := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .configs(Fuzz)
  .enablePlugins(FuzzPlugin, ScalafmtCliPlugin)
  .settings(
    name := "use",
  )

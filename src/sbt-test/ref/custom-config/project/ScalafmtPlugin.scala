package com.example

import sbt._
import Keys._

object ScalafmtCliPlugin extends AutoPlugin {
  object autoImport {
    lazy val ScalafmtSandbox = config("scalafmt").hide
    lazy val scalafmt = inputKey[Unit]("")
  }
  import autoImport._
  override lazy val projectSettings = Seq(
    ivyConfigurations += ScalafmtSandbox,
    libraryDependencies += "org.scalameta" %% "scalafmt-cli" % "2.7.5" % ScalafmtSandbox,
    scalafmt := (ScalafmtSandbox / run).evaluated
  ) ++ inConfig(ScalafmtSandbox)(
    Seq(
      run := Defaults.runTask(managedClasspath, run / mainClass, run / runner)
        .evaluated,
      managedClasspath := Classpaths.managedJars(
        ScalafmtSandbox,
        classpathTypes.value,
        update.value,
      )
    ) ++
      inTask(run)(
        Seq(
          mainClass := Some("org.scalafmt.cli.Cli"),
          fork := true, // to avoid exit
        ) ++ Defaults.runnerSettings
      )
  )
}

package com.example.sbtfuzz

import sbt._

object FuzzPlugin extends AutoPlugin {
  object autoImport {
    lazy val Fuzz = config("fuzz")
  }
  import autoImport._
  override lazy val projectSettings =
    inConfig(Fuzz)(Defaults.configSettings)
}

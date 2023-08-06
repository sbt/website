ThisBuild / scalaVersion := "2.13.11"
ThisBuild / organization := "com.example"

lazy val hello = project
  .in(file("."))
  .settings(
    name := "Hello",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % Test
  )

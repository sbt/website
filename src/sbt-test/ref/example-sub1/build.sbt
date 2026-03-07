scalaVersion := "3.3.3"
organization := "com.example"

lazy val hello = rootProject
  .settings(
    name := "Hello",
    libraryDependencies ++= Seq(
      "org.scala-lang" %% "toolkit" % "0.1.7",
      "org.scala-lang" %% "toolkit-test" % "0.1.7" % Test,
    ),
  )

lazy val helloCore = (project in file("core"))
  .settings(
    name := "Hello Core",
  )

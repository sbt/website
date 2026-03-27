import CommandExample.*

scalaVersion := "3.8.2"
LocalRootProject / commands ++= Seq(hello, helloAll, failIfTrue, changeColor, printState)

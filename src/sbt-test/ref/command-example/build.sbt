import CommandExample.*

scalaVersion := "3.8.4"
LocalRootProject / commands ++= Seq(hello, helloAll, failIfTrue, changeColor, printState)

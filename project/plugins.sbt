addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.3.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.2")
libraryDependencies += "org.foundweekends" %% "pamflet-library" % "0.7.2"
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.2.0")
libraryDependencies += { "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value }

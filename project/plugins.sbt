addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.0-17f2019e17bf3fd6a89ec922020d81717272d956")

libraryDependencies += "net.databinder" %% "pamflet-library" % "0.5.1-SNAPSHOT"

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.2")

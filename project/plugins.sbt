addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.8.0")

//libraryDependencies += "net.databinder" %% "pamflet-library" % "0.5.1-SNAPSHOT"

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.2")

lazy val pamfletUri = uri("git://github.com/eed3si9n/pamflet#topic/combinedmd")

lazy val websitePlugins = (
  project in file(".")
  dependsOn(ProjectRef(pamfletUri, "pamflet"))
)

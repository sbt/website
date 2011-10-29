organization := "com.github.sbt"

name := "sbt-site"

version := "0.1.0-SNAPSHOT"

seq(ghpages.settings:_*)

ghpages.gitRemoteRepo := "git@github.com:sbt/sbt.github.com.git"

ghpages.genSite <<= (baseDirectory, ghpages.siteDirectory) map { (bd, sd) =>
  IO.copyDirectory(bd / "static", sd)
}

organization := "me.lessis"

name := "sbt-root"

version := "0.1.0-SNAPSHOT"

seq(ghpages.settings:_*)

ghpages.gitRemoteRepo := "git@github.com:softprops/www-sbt.git"

ghpages.genSite <<= (baseDirectory, ghpages.siteDirectory) map { (bd, sd) =>
  IO.copyDirectory(bd / "static", sd)
}

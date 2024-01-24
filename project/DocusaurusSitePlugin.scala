import sbt.*
import Keys.*
import com.typesafe.sbt.site.SitePlugin.autoImport.siteSubdirName
import com.typesafe.sbt.site.SitePlugin
import com.typesafe.sbt.site.util.SiteHelpers
import scala.annotation.nowarn
import scala.sys.process.Process
import scala.sys.process.ProcessBuilder

object DocusaurusSitePlugin extends AutoPlugin {
  override def requires = SitePlugin
  override def trigger = noTrigger
  override def projectSettings = docusaurusSettings(Compile)

  object autoImport {
    val Docusaurus = config("docusaurus")
    val docusaurusBuild = taskKey[File]("")
    val docusaurusDirectory = settingKey[File]("Directory where docs are located")
  }
  import autoImport.*

  lazy val yarnBin =
    if (scala.util.Properties.isWin) "yarn.cmd"
    else "yarn"

  @nowarn
  def docusaurusSettings(config: Configuration): Seq[Setting[_]] =
    inConfig(if (config == Compile) Docusaurus else config)(
      List(
        siteSubdirName := "",
        docusaurusDirectory := baseDirectory.value,
        // borrowed from https://github.com/scalameta/mdoc/blob/main/mdoc-sbt/src/main/scala/mdoc/DocusaurusPlugin.scala
        config / docusaurusBuild := {
          import scala.sys.process.*
          val dir = docusaurusDirectory.value
          Process(List(yarnBin, "install"), cwd = dir).!
          Process(List(yarnBin, "run", "build"), cwd = dir).!
          val out = dir / "build"
          out
        },
        config / cleanFiles ++= {
          val dir = docusaurusDirectory.value
          val buildDir = dir / "build"
          val nodeModules = dir / "node_modules"
          Seq(buildDir, nodeModules).filter(_.exists())
        },
      )
    ) ++
      SiteHelpers.watchSettings(ThisScope.in(config, docusaurusBuild.key)) ++
      SiteHelpers.addMappingsToSiteDir(
        (config / docusaurusBuild)
          .map(SiteHelpers.selectSubpaths(_, AllPassFilter)),
        (if (config == Compile) Docusaurus else config) / siteSubdirName
      )
}

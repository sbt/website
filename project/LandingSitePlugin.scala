import sbt.*
import Keys.*
import com.typesafe.sbt.site.SitePlugin.autoImport.siteSubdirName
import com.typesafe.sbt.site.SitePlugin
import com.typesafe.sbt.site.util.SiteHelpers
import scala.annotation.nowarn
import scala.sys.process.Process

object LandingSitePlugin extends AutoPlugin {
  override def requires = SitePlugin
  override def trigger = noTrigger
  override def projectSettings = landingSettings(Compile)

  object autoImport {
    val Landing = config("landing")
    val landingBuild = taskKey[File]("")
    val landingDirectory = settingKey[File]("Directory where the landing page is located")
  }
  import autoImport.*

  @nowarn
  def landingSettings(config: Configuration): Seq[Setting[_]] =
    inConfig(if (config == Compile) Landing else config)(
      List(
        siteSubdirName := "",
        landingDirectory := (LocalRootProject / baseDirectory).value / "landing",
        config / landingBuild := {
          import scala.sys.process.*
          val dir = landingDirectory.value
          Process(List("npm", "ci"), cwd = dir).!
          Process(List("npm", "run", "build"), cwd = dir).!
          dir / "_site"
        },
        config / cleanFiles ++= {
          val dir = landingDirectory.value
          val siteDir = dir / "_site"
          val nodeModules = dir / "node_modules"
          Seq(siteDir, nodeModules).filter(_.exists())
        },
      )
    ) ++
      SiteHelpers.watchSettings(ThisScope.in(config, landingBuild.key)) ++
      SiteHelpers.addMappingsToSiteDir(
        (config / landingBuild)
          .map(SiteHelpers.selectSubpaths(_, AllPassFilter)),
        (if (config == Compile) Landing else config) / siteSubdirName
      )
}

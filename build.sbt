import com.typesafe.sbt.site.util.SiteHelpers
import Docs._

lazy val tutorialSubDirName = settingKey[String]("subdir name for old tutorial")
lazy val fileEncoding = settingKey[String]("check the file encoding")
lazy val landingSubDirName = settingKey[String]("subdir name for landing")

ThisBuild / organization := "org.scala-sbt"
ThisBuild / scalafmtOnCompile := true

lazy val root = (project in file("."))
  .enablePlugins(
    (if (!is1xBranch && !isBetaBranch) Seq(LandingSitePlugin) else Seq()) ++
      Seq(MdBookSitePlugin, ScriptedPlugin, SitePreviewPlugin): _*
  )
  .settings(
    name := "website",
    scalaVersion := "3.8.2",
    siteEmail := "eed3si9n" + "@gmail.com",
    // Reference
    MdBook / siteSubdirName := s"""$targetSbtBinaryVersion/docs/""",
    tutorialSubDirName := s"""$targetSbtBinaryVersion/tutorial""",
    landingSubDirName := "",
    // Redirects
    redirectSettings,
    // SiteHelpers.addMappingsToSiteDir(Redirect / mappings, Pamflet / siteSubdirName),
    SiteHelpers.addMappingsToSiteDir(RedirectLanding / mappings, landingSubDirName),
    // redirectTutorialSettings,
    // SiteHelpers.addMappingsToSiteDir(RedirectTutorial / mappings, tutorialSubDirName),
    // GitHub Pages. See project/Docs.scala
    customGhPagesSettings,
    fileEncoding := {
      sys.props("file.encoding") match {
        case "UTF-8" => "UTF-8"
        case x       => sys.error(s"Unexpected encoding $x")
      }
    },
    pluginCrossBuild / sbtVersion := referenceSbtVersion,
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    isGenerateSiteMap := true
  )

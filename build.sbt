import com.typesafe.sbt.site.util.SiteHelpers
import Docs._

lazy val tutorialSubDirName = settingKey[String]("subdir name for old tutorial")
lazy val fileEncoding = settingKey[String]("check the file encoding")
lazy val landingSubDirName = settingKey[String]("subdir name for landing")

ThisBuild / organization := "org.scala-sbt"
ThisBuild / scalafmtOnCompile := true

lazy val root = (project in file("."))
  .enablePlugins(
    (if (!isBetaBranch) Seq(ParadoxSitePlugin) else Seq()) ++
      Seq(LowTechSnippetPamfletPlugin, ScriptedPlugin): _*
  )
  .settings(
    name := "website",
    siteEmail := "eed3si9n" + "@gmail.com",
    // Landing
    Compile / paradox / sourceDirectory := baseDirectory.value / "src" / "landing",
    Compile / paradoxTheme / sourceDirectory := (Compile / paradox / sourceDirectory).value / "_template",
    paradoxProperties ++= Map(
      "sbtVersion" -> Docs.targetSbtFullVersion,
      "windowsBuild" -> Docs.sbtWindowsBuild,
      "sbtVersionForScalaDoc" -> Docs.sbtVersionForScalaDoc,
      "scala3ExampleVersion" -> Docs.scala3ExampleVersion,
    ),
    Compile / paradoxRoots := List(
      "404.html",
      "community.html",
      "cookie.html",
      "documentation.html",
      "download.html",
      "index.html",
      "learn.html",
      "thank-you.html",
    ),
    // Reference
    Pamflet / sourceDirectory := baseDirectory.value / "src" / "reference",
    Pamflet / siteSubdirName := s"""$targetSbtBinaryVersion/docs""",
    tutorialSubDirName := s"""$targetSbtBinaryVersion/tutorial""",
    landingSubDirName := "",
    // Redirects
    redirectSettings,
    SiteHelpers.addMappingsToSiteDir(Redirect / mappings, Pamflet / siteSubdirName),
    SiteHelpers.addMappingsToSiteDir(RedirectLanding / mappings, landingSubDirName),
    redirectTutorialSettings,
    SiteHelpers.addMappingsToSiteDir(RedirectTutorial / mappings, tutorialSubDirName),
    // GitHub Pages. See project/Docs.scala
    customGhPagesSettings,
    Pamflet / mappings := {
      val xs = (Pamflet / mappings).value
      Pdf.cleanupCombinedPages(xs) ++ xs
    },
    if (scala.sys.BooleanProp.keyExists("sbt.website.generate_pdf"))
      Def settings (
        // NOTE - PDF settings must be done externally like this because pdf generation generically looks
        // through `mappings in Config` for Combined+Pages.md to generate PDF from, and therefore we
        // can't create a circular dependency by adding it back into the original mappings.
        Pdf.settings,
        Pdf.settingsFor(Pamflet, "sbt-reference"),
        SiteHelpers.addMappingsToSiteDir(
          Pamflet / Pdf.generatePdf / mappings,
          Pamflet / siteSubdirName,
        )
      )
    else if (scala.sys.BooleanProp.keyExists("sbt.website.detect_pdf"))
      Def.settings(
        // assume PDF files were created in another Docker container
        Pamflet / Pdf.detectPdf := ((Pamflet / target).value ** "*.pdf").get,
        Pamflet / Pdf.detectPdf / mappings := {
          (Pamflet / Pdf.detectPdf).value pair Path.relativeTo((Pamflet / target).value)
        },
        SiteHelpers.addMappingsToSiteDir(
          Pamflet / Pdf.detectPdf / mappings,
          Pamflet / siteSubdirName,
        )
      )
    else Nil,
    fileEncoding := {
      sys.props("file.encoding") match {
        case "UTF-8" => "UTF-8"
        case x       => sys.error(s"Unexpected encoding $x")
      }
    },
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    isGenerateSiteMap := true
  )

//

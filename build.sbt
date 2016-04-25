import com.typesafe.sbt.site.util.SiteHelpers
import Docs._

lazy val tutorialSubDirName = settingKey[String]("subdir name for old tutorial")
lazy val fileEncoding = settingKey[String]("check the file encoding")

lazy val root = (project in file(".")).
  enablePlugins(NanocPlugin, PamfletPlugin).
  settings(
    organization := "org.scala-sbt",
    name := "website",
    // Reference
    sourceDirectory in Pamflet := baseDirectory.value / "src" / "reference",
    siteSubdirName in Pamflet := s"""$targetSbtBinaryVersion/docs""",
    tutorialSubDirName := s"""$targetSbtBinaryVersion/tutorial""",
    // Redirects
    redirectSettings,
    SiteHelpers.addMappingsToSiteDir(mappings in Redirect, siteSubdirName in Pamflet),
    redirectTutorialSettings,
    SiteHelpers.addMappingsToSiteDir(mappings in RedirectTutorial, tutorialSubDirName),
    // Github Pages. See project/Docs.scala
    customGhPagesSettings,
    // NOTE - PDF settings must be done externally like this because pdf generation generically looks
    // through `mappings in Config` for Combined+Pages.md to generate PDF from, and therefore we
    // can't create a circular dpeendnecy by adding it back into the original mappings.
    Pdf.settings,
    Pdf.settingsFor(Pamflet, "sbt-reference"),
    SiteHelpers.addMappingsToSiteDir(mappings in Pdf.generatePdf in Pamflet, siteSubdirName in Pamflet),
    fileEncoding := {
      sys.props("file.encoding") match {
        case "UTF-8" => "UTF-8"
        case x       => sys.error(s"Unexpected encoding $x")
      }
    }
  )

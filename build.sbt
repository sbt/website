import com.typesafe.sbt.site.pamflet.PamfletPlugin
import com.typesafe.sbt.site.util.SiteHelpers
import Docs._

enablePlugins(NanocPlugin)

lazy val root = (project in file("."))
  .settings(
    organization := "org.scala-sbt",
    name := "website"
  )
  // Getting Started guide
  .settings(PamfletPlugin.pamfletSettings(Tutorial))
  .settings(siteSubdirName in Tutorial := s"$targetSbtBinaryVersion/tutorial")
  // Reference
  .settings(PamfletPlugin.pamfletSettings(Ref))
  .settings(siteSubdirName in Ref := s"$targetSbtBinaryVersion/docs")
  // Redirects
  .settings(redirectSettings)
  .settings(SiteHelpers.addMappingsToSiteDir(mappings in Redirect, siteSubdirName in Ref))
  // Github Pages. See project/Docs.scala
  .settings(customGhPagesSettings)
  // NOTE - PDF settings must be done externally like this because pdf generation generically looks
  // through `mappings in Config` for Combined+Pages.md to generate PDF from, and therefore we
  // can't create a circular dpeendnecy by adding it back into the original mappings.
  .settings(Pdf.settings ++
    Pdf.settingsFor(Tutorial, "sbt-tutorial") ++
    SiteHelpers.addMappingsToSiteDir(mappings in Pdf.generatePdf in Tutorial, siteSubdirName in Tutorial) ++
    Pdf.settingsFor(Ref, "sbt-reference") ++
    SiteHelpers.addMappingsToSiteDir(mappings in Pdf.generatePdf in Ref, siteSubdirName in Ref))

import com.typesafe.sbt.site.PamfletSupport
import Docs._

lazy val root = (project in file("."))
  .settings(
    organization := "org.scala-sbt",
    name := "website"
  )
  // landing page
  .settings(site.settings ++ site.nanocSupport(): _*)
  // Reference
  .settings(PamfletSupport.settings(Ref) ++
    site.addMappingsToSiteDir(mappings in Ref, s"""$targetSbtBinaryVersion/docs"""): _*)
  // Redirects
  .settings(redirectSettings ++
    site.addMappingsToSiteDir(mappings in Redirect, s"""$targetSbtBinaryVersion/docs"""): _*)
  .settings(redirectTutorialSettings ++
    site.addMappingsToSiteDir(mappings in RedirectTutorial, s"""$targetSbtBinaryVersion/tutorial"""): _*)
  // Github Pages. See project/Docs.scala
  .settings(customGhPagesSettings: _*)
  // NOTE - PDF settings must be done externally like this because pdf generation generically looks
  // through `mappings in Config` for Combined+Pages.md to generate PDF from, and therefore we
  // can't create a circular dpeendnecy by adding it back into the original mappings.
  .settings(Pdf.settings ++
    // Pdf.settingsFor(Tutorial, "sbt-tutorial") ++
    // site.addMappingsToSiteDir(mappings in Pdf.generatePdf in Tutorial, s"""$targetSbtBinaryVersion/tutorial""") ++
    Pdf.settingsFor(Ref, "sbt-reference") ++
    site.addMappingsToSiteDir(mappings in Pdf.generatePdf in Ref, s"""$targetSbtBinaryVersion/docs"""): _*)

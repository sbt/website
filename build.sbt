import com.typesafe.sbt.site.PamfletSupport
import Docs._

site.settings

site.nanocSupport()

PamfletSupport.settings(Tutorial)

site.addMappingsToSiteDir(mappings in Tutorial, s"""$targetSbtBinaryVersion/tutorial""")

PamfletSupport.settings(Ref)

site.addMappingsToSiteDir(mappings in Ref, s"""$targetSbtBinaryVersion/docs""")

redirectSettings

site.addMappingsToSiteDir(mappings in Redirect, s"""$targetSbtBinaryVersion/docs""")

customGhPagesSettings // see project/Docs.scala

Pdf.settings

// NOTE - PDF settings must be done externally like this because pdf generation generically looks
// through `mappings in Config` for Combined+Pages.md to generate PDF from, and therefore we
// can't create a circular dpeendnecy by adding it back into the original mappings.

Pdf.settingsFor(Tutorial, "sbt-tutorial")

site.addMappingsToSiteDir(mappings in Pdf.generatePdf in Tutorial, s"""$targetSbtBinaryVersion/tutorial""")

Pdf.settingsFor(Ref, "sbt-reference")

site.addMappingsToSiteDir(mappings in Pdf.generatePdf in Ref, s"""$targetSbtBinaryVersion/docs""")

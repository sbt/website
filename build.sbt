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

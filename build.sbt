import com.typesafe.sbt.site.PamfletSupport
import Docs._

lazy val Tutorial = config("tutorial")
lazy val Ref = config("reference")

site.settings

site.nanocSupport()

PamfletSupport.settings(Tutorial)

site.addMappingsToSiteDir(mappings in Tutorial, s"""$targetSbtBinaryVersion/tutorial""")

PamfletSupport.settings(Ref)

site.addMappingsToSiteDir(mappings in Ref, s"""$targetSbtBinaryVersion/docs""")

customGhPagesSettings // see project/Docs.scala

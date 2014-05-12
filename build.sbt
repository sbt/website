import com.typesafe.sbt.site.PamfletSupport

lazy val Tutorial = config("tutorial")

lazy val Ref = config("reference")

site.settings

site.nanocSupport()

PamfletSupport.settings(Tutorial)

site.addMappingsToSiteDir(mappings in Tutorial, "0.13/tutorial")

PamfletSupport.settings(Ref)

site.addMappingsToSiteDir(mappings in Ref, "0.13/reference")

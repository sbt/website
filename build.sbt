import com.typesafe.sbt.site.PamfletSupport

lazy val Tutorial = config("tutorial")

site.settings

site.nanocSupport()

PamfletSupport.settings(Tutorial)

site.addMappingsToSiteDir(mappings in Tutorial, "0.13/tutorial")

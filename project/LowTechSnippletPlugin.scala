import sbt._
import Keys._

object LowTechSnippetPamfletPlugin extends AutoPlugin {
  import com.typesafe.sbt.site.pamflet._
  import PamfletPlugin.autoImport._
  import pamflet._

  override def requires = PamfletPlugin
  override def trigger = noTrigger
  override def projectSettings = pamfletSettings

  def pamfletSettings: Seq[Setting[_]] =
    Seq(
      mappings in Pamflet := generate(
        (sourceDirectory in Pamflet).value,
        (target.value / "lowtech_generated"),
        (target in Pamflet).value,
        (includeFilter in Pamflet).value,
        (pamfletFencePlugins in Pamflet).value
      )
    )

  def generate(input: File,
               generated: File,
               output: File,
               includeFilter: FileFilter,
               fencePlugins: Seq[FencePlugin]): Seq[(File, String)] = {
    // this is the added step
    Snippet.processDirectory(input, generated)
    val storage = FileStorage(generated, fencePlugins.toList)
    Produce(storage.globalized, output)
    output ** includeFilter --- output pair Path.relativeTo(output)
  }
}

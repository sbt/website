package sbtobfuscate

import sbt.*
import sbt.Keys.*
import sbt.CacheImplicits.given
import xsbti.HashedVirtualFileRef

object ObfuscatePlugin extends AutoPlugin:
  // by defining autoImport, the settings are automatically
  // imported into user's `*.sbt`
  object autoImport:
    val obfuscate = taskKey[Seq[HashedVirtualFileRef]]("Obfuscates files.")

    // configuration points, like built-in `version` and `libraryDependencies`
    val obfuscateLiterals = settingKey[Boolean]("Obfuscate literals.")
  end autoImport

  import autoImport.*

  // This plugin is automatically enabled for projects which are JvmPlugin.
  override def trigger = allRequirements

  // default values for the settings
  override lazy val globalSettings: Seq[Def.Setting[?]] = Seq(
    obfuscateLiterals := false,
  )

  // default implementations for the tasks
  val baseObfuscateSettings: Seq[Def.Setting[?]] = Seq(
    obfuscate := {
      Obfuscate(sourcesVF.value, (obfuscate / obfuscateLiterals).value)
    },
  )

  // a group of settings that are automatically added to projects.
  override lazy val projectSettings: Seq[Def.Setting[?]] =
    inConfig(Compile)(baseObfuscateSettings) ++
    inConfig(Test)(baseObfuscateSettings)
end ObfuscatePlugin

object Obfuscate:
  def apply(
    sources: Seq[HashedVirtualFileRef],
    obfuscateLiterals: Boolean
  ): Seq[HashedVirtualFileRef] =
    // TODO obfuscate stuff!
    sources
end Obfuscate

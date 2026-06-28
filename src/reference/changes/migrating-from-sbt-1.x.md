# Migrating from sbt 1.x

## Changing `build.sbt` DSL to Scala 3.x

As a reminder, users can build either Scala 2.x or Scala 3.x programs using either sbt 1.x or sbt 2.x. However, the Scala that underlies the `build.sbt` DSL is determined by the sbt version. In sbt 2.0, we are migrating to Scala 3.8.x.

This means that if you implement custom tasks or sbt plugins for sbt 2.x, it must be done using Scala 3.x. Consult [Scala 3.x incompatibility table][scala-incompatibility-table] and [Scala 2 with -Xsource:3][tooling-scala2-xsource3] for details about Scala 3.x.

```scala
// This works on Scala 2.12.20 under -Xsource:3
import sbt.{ given, * }
```

### Import given

One of the differences between Scala 2.x and 3.x is the way typeclass instances are imported into scope. In Scala 2.x `import FooCodec._` was used whereas Scala 3 uses `import FooCodec.given`. Writing:

```scala
// The following works for both sbt 1.x and 2.x
import sbt.librarymanagement.LibraryManagementCodec.{ given, * }
```

### Avoid postfix

It wasn't uncommon for sbt 0.13 and 1.x examples to use postfix notations, especially with `ModuleID`:

```scala
// BAD
libraryDependencies +=
  "com.github.sbt" % "junit-interface" % "0.13.2" withSources() withJavadoc()
```

The above will fail to load on sbt 2.x:

```scala
-- Error: /private/tmp/foo/build.sbt:9:61 --------------------------------------
9 |  "com.github.sbt" % "junit-interface" % "0.13.2" withSources() withJavadoc()
  |                                                             ^^
  |can't supply unit value with infix notation because nullary method withSources
   in class ModuleIDExtra: (): sbt.librarymanagement.ModuleID takes no arguments;
   use dotted invocation instead: (...).withSources()
```

To fix this, use the normal (dotted) function call notation:

```scala
// GOOD
libraryDependencies +=
  ("com.github.sbt" % "junit-interface" % "0.13.2").withSources().withJavadoc()
```

## Bare settings changes

```scala
version := "0.1.0"
scalaVersion := "{{scala3_example_version}}"
```

_Bare settings_, like the example above, are settings written directly in `build.sbt` without `settings(...)`.

```admonish warning
In sbt 1.x bare settings were project settings that applied only to the root subproject. In sbt 2.x, the bare settings in `build.sbt` are common settings that are injected to **all subprojects**.
```

```scala
name := "root"         // all subprojects will be named root!
publish / skip := true // all subprojects will be skipped!
```

To apply some settings to the root subproject only, either define it using multi-project build, or scope the setting under `LocalRootProject`:

```scala
LocalRootProject / name := "root"
LocalRootProject / publish / skip := true
```

### Migrating ThisBuild

In sbt 2.x, bare settings settings should no longer be scoped to `ThisBuild`. One benefit of the new _common settings_ over `ThisBuild` is that it would act in a more predictable delegation. These settings are inserted between plugins settings and those defined in `settings(...)`, meaning they can be used to define settings like `Compile / scalacOptions`, which was not possible with `ThisBuild`.

## Changes to `exportJars`

`exportJars` defaults to `true`, was `false`. This might break `getResource("/")` and `resource.toURI`. Set `exportJars := false` if this logic is broken in your build, producing `NullPointerException`s and `FileSystemNotFoundException`s. Set `exportJars := false` in your build if you want to keep the old behavior. The change was introduced by [sbt/sbt#7464](https://github.com/sbt/sbt/pull/7464), see also [blog](https://eed3si9n.com/sbt-remote-cache/).

## Migrating to cached tasks

In sbt 2.x, all tasks are cached by default. To participate in caching, the task result type must provide a given for `sjsonnew.JsonFormat`. Any task whose result type lacks `JsonFormat` (e.g. complex objects like `ParadoxProcessor`, `ClassLoader`, `Seq[PathMapping]`, or function types) will fail at build load time in sbt 2.

If you don't want to define the given, the easiest way to migrate is to wrap the tasks with `Def.uncached(...)` so sbt 2 skips caching and always re-executes them:

```scala
myTask := Def.uncached {
  // task body returning a non-serializable type
}
```

When considering caching for a task, watch out for side-effecting tasks. When sbt 2 restores a task result from its disk cache, it returns the cached value without re-executing the task body. Any side effect (e.g. writing files, syncing mappings) is silently skipped. If a task is meant to produce a side effect every time it runs, wrap it in `Def.uncached(...)` so sbt 2 always re-executes it.

The [sbt2-compat](https://github.com/sbt/sbt2-compat) plugin provides `Def.uncached` as a compatibility shim on sbt 1.x (where it is a no-op). See [Cached task](../reference/cached-task.md) reference for details, including build-wide and per-task opt-out options.

## Migration away from IntegrationTest

To migrate away from the `IntegrationTest` configuration, create a separate subproject and implement it as normal test.

## Migrating to slash syntax

sbt 1.x supported both the sbt 0.13 style syntax and the slash syntax. sbt 2.x removes the support for the sbt 0.13 syntax, so use the slash syntax for both sbt shell and in `build.sbt`:

```scala
<project-id> / Config / intask / key
```

For example, `test:compile` will no longer work on the shell. Use `Test/compile` instead. See [syntactic Scalafix rule for unified slash syntax][syntactic-scalafix-rule-for-unified-slash-syntax] for semi-automated migration of `build.sbt` files.

```bash
scalafix --rules=https://gist.githubusercontent.com/eed3si9n/57e83f5330592d968ce49f0d5030d4d5/raw/7f576f16a90e432baa49911c9a66204c354947bb/Sbt0_13BuildSyntax.scala *.sbt project/*.scala
```

## Cross building sbt plugins

In sbt 2.x, if you cross build an sbt plugin with Scala 3.x and 2.12.x, it will automatically cross build against sbt 1.x and sbt 2.x:

```scala
// using sbt 2.x
lazy val plugin = (projectMatrix in file("plugin"))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-vimquit",
  )
  .jvmPlatform(scalaVersions = Seq("3.8.4", "2.12.20"))
```

If you use `projectMatrix`, make sure to move the plugin to a subdirectory like `plugin/`. Otherwise, the synthetic root project will also pick up the `src/`.

### Cross building sbt plugin with sbt 1.x

Use sbt 1.10.2 or later, if you want to cross build using sbt 1.x.

```scala
// using sbt 1.x
lazy val scala212 = "2.12.20"
lazy val scala3 = "3.8.4"
ThisBuild / crossScalaVersions := Seq(scala212, scala3)

lazy val plugin = (project in file("plugin"))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-vimquit",
    (pluginCrossBuild / sbtVersion) := {
      scalaBinaryVersion.value match {
        case "2.12" => "1.5.8"
        case _      => "{{sbt_version}}"
      }
    },
  )
```

## Changes to `%%`

In sbt 2.x, `ModuleID`'s `%%` operator has become platform-aware. For JVM subprojects, `%%` works as before, encoding Scala suffix (for example `_3`) on Maven repositories.

### Migrating `%%%` operator

When Scala.JS or Scala Native becomes available on sbt 2.x, `%%` will encode both the Scala version (such as `_3`) and the platform suffix (`_sjs1` etc). As a result, `%%%` can be replaced with `%%`:

```scala
libraryDependencies += "org.scala-js" %% "scalajs-dom" % "2.8.0"
```

Use `.platform(Platform.jvm)` in case where JVM libraries are needed.

## Changes to `target`

In sbt 2.x, the `target` directory is unified to be a single `target/` directory in the working directory, and each subproject creates a subdirectory encoding platform, Scala version, and the subproject id. To absorb this change in scripted tests, `exists`, `absent`, and `delete` now supports glob expression `**`, as well as `||`.

```bash
# before
$ absent target/out/jvm/scala-3.3.1/clean-managed/src_managed/foo.txt
$ exists target/out/jvm/scala-3.3.1/clean-managed/src_managed/bar.txt

# after
$ absent target/**/src_managed/foo.txt
$ exists target/**/src_managed/bar.txt

# either is ok
$ exists target/**/proj/src_managed/bar.txt || proj/target/**/src_managed/bar.txt
```

In sbt 1.x, `target.value` resolves to the project root `target/` directory. In sbt 2.x, it resolves to `target/out/jvm/scala-<ver>/<project-name>` instead. Plugins should be aware of this change during migration.

## Migrating CI pipelines

There are some behavior changes that may affect CI pipelines.

### Running a sequence of commands

A sequence of commands must now be supplied as a quoted string separated by
semicolons:

```bash
sbt "clean ; compile ; test"
```

Previously, you could write `sbt clean compile test`. That now produces the
error "Expected whitespace character".

### sbt server with multiple steps

If a CI pipeline contains multiple steps that run `sbt`, later steps reuse the
sbt server started by the first invocation instead of starting a new `sbt`
process each time. As a result, these later steps continue to use the environment
variables passed to the first session.

If your pipeline relies on passing different environment variables (such as `JAVA_OPTS`)
to each session, you must either provide all variables at the job level so they are
identical for all `sbt` invocations, or shut down `sbt` after each step or between steps.

```bash
sbt "clean ; compile ; test ; shutdown"
```

Note that this applies whether you run `sbt` directly or through a third-party action
such as `sbt-dependency-submission` that invokes `sbt` internally.

### Test artifacts

Output artifacts such as test results are now stored in subdirectories beneath
`target/out` so you may need to update the paths used for test publishing and
artifact archival:

```yaml
path: target/out/**/test-reports/*.xml
```

### Change of global base directory

In sbt 2.x, the global base directory follows directory standard. The default value on Windows is `%LOCALAPPDATA%/sbt/2`. Otherwise, it is `$XDG_CONFIG_HOME/sbt/2` or `$HOME/.config/sbt/2`. See [sbt reference](../reference/sbt.md#global-base-directory) for details.

## The PluginCompat technique

To use the same `*.scala` source but target both sbt 1.x and 2.x, we can create a shim, for example an object named `PluginCompat` in both `src/main/scala-2.12/` and `src/main/scala-3/`. APIs commonly encountered during migrations are abstracted into the [sbt2-compat](https://github.com/sbt/sbt2-compat) plugin that can be used to avoid creating the shims manually. To use it in your sbt plugin, you can add it to your sbt plugin's `build.sbt`:

```scala
addSbtPlugin("com.github.sbt" % "sbt2-compat" % "<version>")
```

And import and use the conversion methods in your shared source:

```scala
import sbtcompat.PluginCompat._

// Use the conversion methods here
```

You can read more about `sbt2-compat`, the `PluginCompat` pattern and how to use them the following blog article: [Migrating sbt plugins to sbt 2 with sbt2-compat plugin](https://www.scala-lang.org/blog/2026/03/02/sbt2-compat.html).

### Migrating Classpath type

sbt 2.x changed the `Classpath` type to be an alias of `Seq[Attributed[xsbti.HashedVirtualFileRef]]` instead of `Seq[Attributed[File]]`. Any plugin that needs `File` or `Path` for I/O (classpath URLs, mappings, sync, validation) must convert these references. With `sbt2-compat` added and imported as above, use `toNioPaths` and `toFiles`, for example:

```scala
import sbtcompat.PluginCompat._

myTask := {
  implicit val conv: FileConverter = fileConverter.value
  val paths = toNioPaths((Compile / dependencyClasspath).value)
  val files = toFiles((Compile / dependencyClasspath).value)
  // ...
}
```

`sbt2-compat` also provides `toNioPath`, `toFile`, `toFileRefsMapping`, `Def.uncached` and other convenience methods to be used in the shared sources. See the [sbt2-compat README](https://github.com/sbt/sbt2-compat) for the up-to-date documentation on the API provided by the plugin.

### Defining your own PluginCompat shims

For the APIs broken between sbt 1.x and 2.x that are not covered by `sbt2-compat`, you can define your own `PluginCompat` shims by creating separate source files under `src/main/scala-2.12/PluginCompat.scala` and `src/main/scala-3/PluginCompat.scala`. For example:

```scala
// src/main/scala-2.12/PluginCompat.scala

package sbtfoo

import sbt._
import Keys._

object PluginCompat {
  def someSharedMethod(): Unit = ...
}
```

```scala
// src/main/scala-3/PluginCompat.scala

package sbtfoo

import sbt._
import Keys._

object PluginCompat {
  def someSharedMethod(): Unit = ...
}
```

Then use your own `PluginCompat` shims in your sbt plugin:

```scala
import sbtfoo.PluginCompat._

myTask := {
  someSharedMethod()
}
```

This pattern is compatible with `sbt2-compat` and can be used alongside it to absorb the differences between sbt 1.x and 2.x.

[scala-incompatibility-table]: https://docs.scala-lang.org/scala3/guides/migration/incompatibility-table.html
[syntactic-scalafix-rule-for-unified-slash-syntax]: https://eed3si9n.com/syntactic-scalafix-rule-for-unified-slash-syntax/
[tooling-scala2-xsource3]: https://docs.scala-lang.org/scala3/guides/migration/tooling-scala2-xsource3.html

---
out: Plugins-Best-Practices.html
---

  [Using-Plugins]: Using-Plugins.html
  [Pluings]: Plugins.html
  [Tasks]: Tasks.html
  [Commands]: Commands.html
  [Plugins]: Plugins.html
  [ScopeFilter]: Tasks.html#ScopeFilter
  [inspectr]: https://github.com/eed3si9n/sbt-inspectr/blob/aa88bfac609e4668d0ad8ac220e4ef5fb1c3b9f5/src/main/scala/sbtinspectr/InspectrCommand.scala
  [Community-Plugins]: Community-Plugins.html

Plugins Best Practices
----------------------

*This page is intended primarily for sbt plugin authors.*
This page assumes you've read [using plugins][Using-Plugins] and [Plugins][Plugins].

A plugin developer should strive for consistency and ease of use.
Specifically:

-   Plugins should play well with other plugins. Avoiding namespace
    clashes (in both sbt and Scala) is paramount.
-   Plugins should follow consistent conventions. The experiences of an
    sbt *user* should be consistent, no matter what plugins are pulled
    in.

Here are some current plugin best practices.

> **Note:** Best practices are evolving, so check back frequently.

### Get your plugins known

Make sure people can find your plugin. Here are some of the recommended steps:

1. Mention [@scala_sbt](https://twitter.com/scala_sbt) in your announcement, and we will RT it.
2. Send a pull req to [sbt/website](https://github.com/sbt/website) and add your plugin on [the plugins list][Community-Plugins].

### Don't use default package

Users who have their build files in some package will not be able to use
your plugin if it's defined in default (no-name) package.

### Follow the naming conventions

Use the `sbt-\$projectname` scheme to name your library and artifact.
A plugin ecosystem with a consistent naming convention makes it easier for users to tell whether a
project or dependency is an SBT plugin.

If the project's name is `foobar` the following holds:

 - BAD: `foobar`
 - BAD: `foobar-sbt`
 - BAD: `sbt-foobar-plugin`
 - GOOD: `sbt-foobar`

If your plugin provides an obvious "main" task, consider naming it `foobar` or `foobar...` to make
it more intuitive to explore the capabilities of your plugin within the sbt shell and tab-completion.

### Use settings and tasks. Avoid commands.

Your plugin should fit in naturally with the rest of the sbt ecosystem.
The first thing you can do is to avoid defining [commands][Commands],
and use settings and [tasks][Tasks] and task-scoping instead (see below for more on task-scoping).
Most of the interesting things in sbt like
`compile`, `test` and `publish` are provided using tasks.
Tasks can take advantage of duplication reduction and parallel execution by the task engine.
With features like [ScopeFilter][ScopeFilter], many of the features that previously required
commands are now possible using tasks. 

Settings can be composed from other settings and tasks.
Tasks can be composed from other tasks and input tasks.
Commands, on the other hand, cannot be composed from any of the above.
In general, use the minimal thing that you need.
One legitimate use of commands may be using plugin to access the build definition itself not the code.
sbt-inspectr was implemented using [a command][inspectr] before it became `inspect tree`.

### Use `sbt.AutoPlugin`

sbt is in the process of migrating to `sbt.AutoPlugin` from `sbt.Plugin`.
The new mechanism features a set of user-level
controls and dependency declarations that cleans up a lot of
long-standing issues with plugins.

### Reuse existing keys

sbt has a number of [predefined keys](../api/sbt/Keys\$.html).
Where possible, reuse them in your plugin. For instance, don't define:

```scala
val sourceFiles = settingKey[Seq[File]]("Some source files")
```

Instead, simply reuse sbt's existing `sources` key.

### Avoid namespace clashes

Sometimes, you need a new key, because there is no existing sbt key. In
this case, use a plugin-specific prefix.

```scala
package sbtobfuscate

import sbt._, Keys._

object ObfuscatePlugin extends sbt.AutoPlugin {
  object autoImport {
    lazy val obfuscateStylesheet = settingKey[File]("obfuscate stylesheet")
  } 
}
```

In this approach, every `lazy val` starts with `obfuscate`. A user of the
plugin would refer to the settings like this:

```scala
obfuscateStylesheet := file("something.txt")
```

### Provide core feature in a plain old Scala object

The core feature of sbt's `package` task, for example, is implemented in [sbt.Package](../api/sbt/Package\$.html),
which can be called via its `apply` method.
This allows greater reuse of the feature from other plugins such as sbt-assembly,
which in return implements `sbtassembly.Assembly` object to implement its core feature.

Follow their lead, and provide core feature in a plain old Scala object.

### Configuration advices

If your plugin introduces either a new set of source code or
its own library dependencies, only then you want your own configuration.

#### You probably won't need your own configuration

Configurations should *not* be used to namespace keys for a plugin. 
If you're merely adding tasks and settings, don't define your own
configuration. Instead, reuse an existing one *or* scope by the main
task (see below).

```scala
package sbtwhatever

import sbt._, Keys._

object WhateverPlugin extends sbt.AutoPlugin {
  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements

  object autoImport {
    // BAD sample
    lazy val Whatever = config("whatever") extend(Compile)
    lazy val specificKey = settingKey[String]("A plugin specific key")
  }
  import autoImport._
  override lazy val projectSettings = Seq(
    specificKey in Whatever := "another opinion" // DON'T DO THIS
  )
}
```

#### When to define your own configuration

If your plugin introduces either a new set of source code or
its own library dependencies, only then you want your own configuration.
For instance, suppose you've built a plugin that performs fuzz testing
that requires its own fuzzing library and fuzzing source code.
`scalaSource` key can be reused similar to `Compile` and `Test` configuration,
but `scalaSource` scoped to `Fuzz` configuration (denoted as `scalaSource in Fuzz`)
can point to `src/fuzz/scala` so it is distinct from other Scala source directories.
Thus, these three definitions use
the same *key*, but they represent distinct *values*. So, in a user's
`build.sbt`, we might see:

```scala
scalaSource in Fuzz := baseDirectory.value / "source" / "fuzz" / "scala"

scalaSource in Compile := baseDirectory.value / "source" / "main" / "scala"
```

In the fuzzing plugin, this is achieved with an `inConfig` definition:

```scala
package sbtfuzz

import sbt._, Keys._

object FuzzPlugin extends sbt.AutoPlugin {
  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements

  object autoImport {
    lazy val Fuzz = config("fuzz") extend(Compile)
  }
  import autoImport._
  
  lazy val baseFuzzSettings: Seq[Def.Setting[_]] = Seq(
    test := {
      println("fuzz test")
    }
  )
  override lazy val projectSettings = inConfig(Fuzz)(baseFuzzSettings)
}
```

When defining a new type of configuration, e.g.

```scala
lazy val Fuzz = config("fuzz") extend(Compile)
```

should be used to create a configuration.
Configurations actually tie into dependency resolution (with Ivy) and
can alter generated pom files.

#### Playing nice with configurations

Whether you ship with a configuration or not, a plugin should strive to
support multiple configurations, including those created by the build
user. Some tasks that are tied to a particular configuration can be
re-used in other configurations. While you may not see the need
immediately in your plugin, some project may and will ask you for the
flexibility.

#### Provide raw settings and configured settings

Split your settings by the configuration axis like so:

```scala
package sbtobfuscate

import sbt._, Keys._

object ObfuscatePlugin extends sbt.AutoPlugin {
  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements

  object autoImport {
    lazy val obfuscate = taskKey[Seq[File]]("obfuscate the source")
    lazy val obfuscateStylesheet = settingKey[File]("obfuscate stylesheet")
  }
  import autoImport._
  lazy val baseObfuscateSettings: Seq[Def.Setting[_]] = Seq(
    obfuscate := Obfuscate((sources in obfuscate).value),
    sources in obfuscate := sources.value
  )
  override lazy val projectSettings = inConfig(Compile)(baseObfuscateSettings)
}

// core feature implemented here
object Obfuscate {
  def apply(sources: Seq[File]): Seq[File] = {
    sources
  }
}
```

The `baseObfuscateSettings` value provides base configuration for the
plugin's tasks. This can be re-used in other configurations if projects
require it. The `obfuscateSettings` value provides the default `Compile`
scoped settings for projects to use directly. This gives the greatest
flexibility in using features provided by a plugin. Here's how the raw
settings may be reused:

```scala
import sbtobfuscate.ObfuscatePlugin

lazy val app = (project in file("app"))
  .settings(inConfig(Test)(ObfuscatePlugin.baseObfuscateSettings))
```

#### Using a "main" task scope for settings

Sometimes you want to define some settings for a particular "main" task
in your plugin. In this instance, you can scope your settings using the
task itself. See the `baseObfuscateSettings`:

```scala
  lazy val baseObfuscateSettings: Seq[Def.Setting[_]] = Seq(
    obfuscate := Obfuscate((sources in obfuscate).value),
    sources in obfuscate := sources.value
  )
```

In the above example, `sources in obfuscate` is scoped under the main
task, `obfuscate`.

### Mucking with `globalSettings`

There may be times when you need to muck with `globalSettings`. The
general rule is *be careful what you touch*.

When overriding global settings, care should be taken to ensure previous
settings from other plugins are not ignored. e.g. when creating a new
`onLoad` handler, ensure that the previous `onLoad` handler is not
removed.

```scala
package sbtsomething

import sbt._, Keys._

object MyPlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements

  override val globalSettings: Seq[Def.Setting[_]] = Seq(
    onLoad in Global := (onLoad in Global).value andThen { state =>
      ... return new state ...
    }
  )
}
```

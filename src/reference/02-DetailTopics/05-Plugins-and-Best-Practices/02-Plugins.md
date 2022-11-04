---
out: Plugins.html
---

  [Using-Plugins]: Using-Plugins.html
  [Full-Def]: Full-Def.html
  [Best-Practices]: Best-Practices.html
  [Plugins-Best-Practices]: Plugins-Best-Practices.html
  [Use-Settings-And-Tasks]: Plugins-Best-Practices.html#Use+settings+and+tasks.+Avoid+commands.
  [Commands]: Commands.html
  [Cross-Build-Plugins]: Cross-Build-Plugins.html

Plugins
-------

There's a [getting started page][Using-Plugins] focused on using existing plugins,
which you may want to read first.

A plugin is a way to use external code in a build definition.
A plugin can be a library used to implement a task (you might use
[Knockoff](https://github.com/tristanjuricek/knockoff/) to write a
markdown processing task). A plugin can define a sequence of sbt settings
that are automatically added to all projects or that are explicitly
declared for selected projects. For example, a plugin might add a
`proguard` task and associated (overridable) settings. Finally, a plugin
can define new commands (via the `commands` setting).

sbt 0.13.5 introduces auto plugins, with improved dependency management
among the plugins and explicitly scoped auto importing.
Going forward, our recommendation is to migrate to the auto plugins.
The [Plugins Best Practices][Plugins-Best-Practices] page describes
the currently evolving guidelines to writing sbt plugins. See also the general
[best practices][Best-Practices].

### Using an auto plugin

A common situation is when using a binary plugin published to a repository.
You can create `project/plugins.sbt` with all of the desired sbt plugins, any general dependencies, and any necessary repositories:

```scala
addSbtPlugin("org.example" % "plugin" % "1.0")
addSbtPlugin("org.example" % "another-plugin" % "2.0")

// plain library (not an sbt plugin) for use in the build definition
libraryDependencies += "org.example" % "utilities" % "1.3"

resolvers += "Example Plugin Repository" at "https://example.org/repo/"
```

Many of the auto plugins automatically add settings into projects,
however, some may require explicit enablement. Here's an example:

```scala
lazy val util = (project in file("util"))
  .enablePlugins(FooPlugin, BarPlugin)
  .disablePlugins(plugins.IvyPlugin)
  .settings(
    name := "hello-util"
  )
```

See [using plugins][Using-Plugins] in the Getting Started guide for more details on using plugins.

### By Description

A plugin definition is a project under `project/` folder. This
project's classpath is the classpath used for build definitions in
`project/` and any `.sbt` files in the project's base
directory. It is also used for the `eval` and `set` commands.

Specifically,

1.  Managed dependencies declared by the `project/` project are
    retrieved and are available on the build definition classpath, just
    like for a normal project.
2.  Unmanaged dependencies in `project/lib/` are available to the build
    definition, just like for a normal project.
3.  Sources in the `project/` project are the build definition files and
    are compiled using the classpath built from the managed and
    unmanaged dependencies.
4.  Project dependencies can be declared in `project/plugins.sbt`
    (similarly to `build.sbt` file in a normal project) and will be available to the build
    definitions.

The build definition classpath is searched for `sbt/sbt.autoplugins`
descriptor files containing the names of
`sbt.AutoPlugin` implementations.

The `reload plugins` command changes the current build to
the (root) project's `project/` build definition. This allows manipulating
the build definition project like a normal project. `reload return` changes back
to the original build. Any session settings for the plugin definition
project that have not been saved are dropped.

An auto plugin is a module that defines settings to automatically inject into
projects. In addition an auto plugin provides the following feature:

- Automatically import selective names to `.sbt` files and the `eval` and `set` commands.
- Specify plugin dependencies to other auto plugins.
- Automatically activate itself when all dependencies are present.
- Specify `projectSettings`, `buildSettings`, and `globalSettings` as appropriate.

### Plugin dependencies

When a traditional plugin wanted to reuse some functionality from an existing plugin, it would pull in the plugin as a library dependency, and then it would either:

1. add the setting sequence from the dependency as part of its own setting sequence, or
2. tell the build users to include them in the right order.

This becomes complicated as the number of plugins increase within an application, and becomes more error prone. The main goal of auto plugin is to alleviate this setting dependency problem. An auto plugin can depend on other auto plugins and ensure these dependency settings are loaded first.

Suppose we have the `SbtLessPlugin` and the `SbtCoffeeScriptPlugin`, which in turn depends on the `SbtJsTaskPlugin`, `SbtWebPlugin`, and `JvmPlugin`. Instead of manually activating all of these plugins, a project can just activate the `SbtLessPlugin` and `SbtCoffeeScriptPlugin` like this:

```scala
lazy val root = (project in file("."))
  .enablePlugins(SbtLessPlugin, SbtCoffeeScriptPlugin)
```

This will pull in the right setting sequence from the plugins in the right order.  The key notion here is you declare the plugins you want, and sbt can fill in the gap.

A plugin implementation is not required to produce an auto plugin, however.
It is a convenience for plugin consumers and because of the automatic nature, it is not always appropriate.

#### Global plugins

The `$global_plugins_base$` directory is treated as a global plugin
definition project. It is a normal sbt project whose classpath is
available to all sbt project definitions for that user as described
above for per-project plugins.

### Creating an auto plugin

A minimal sbt plugin is a Scala library that is built against the version of
Scala that sbt runs (currently, $scala_version$) or a Java library.
Nothing special needs to be done for this type of library.
A more typical plugin will provide sbt tasks, commands, or settings.
This kind of plugin may provide these settings
automatically or make them available for the user to explicitly
integrate.

To make an auto plugin, create a project and enable `SbtPlugin`.

```scala
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / homepage := Some(url("https://github.com/sbt/sbt-hello"))

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-hello",
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.12" => "1.2.8" // set minimum sbt version
      }
    }
  )
```

Some details to note:

- sbt plugins must be compiled with Scala 2.12.x that sbt itself is compiled in. By NOT specifying `scalaVersion`, sbt will default to the Scala version suited for a plugin.
- By default sbt plugin is compiled with whichever the sbt version you are using. Because sbt does NOT keep forward compatibility, that would typically require all of your plugin users to upgrade to the latest too. `pluginCrossBuild / sbtVersion` is an optional setting to compile your plugin against an _older_ version of sbt, which allows the plugin users to choose from a range of sbt versions.

Then, write the plugin code and publish your project to a repository.
The plugin can be used as described in the previous section.

First, in an appropriate namespace, define your auto plugin object
by extending `sbt.AutoPlugin`.

#### projectSettings and buildSettings

With auto plugins, all provided settings (e.g. `assemblySettings`) are provided by the plugin directly via the `projectSettings` method. Here’s an example plugin that adds a task named hello to sbt projects:

@@snip [HelloPlugin]($root$/src/sbt-test/ref/plugins-hello/project/HelloPlugin.scala) {}

If the plugin needs to append settings at the build-level (that is, in `ThisBuild`) there's a `buildSettings` method. The settings returned here are guaranteed to be added to a given build scope only once
regardless of how many projects for that build activate this AutoPlugin.

```scala
override def buildSettings: Seq[Setting[_]] = Nil
```

The `globalSettings` is appended once to the global settings (`in Global`).
These allow a plugin to automatically provide new functionality or new defaults. 
One main use of this feature is to globally add commands, such as for IDE plugins.

```scala
override def globalSettings: Seq[Setting[_]] = Nil
```

Use `globalSettings` to define the default value of a setting.

#### Implementing plugin dependencies

Next step is to define the plugin dependencies.

```scala
package sbtless

import sbt._
import Keys._
object SbtLessPlugin extends AutoPlugin {
  override def requires = SbtJsTaskPlugin
  override lazy val projectSettings = ...
}
```

The `requires` method returns a value of type `Plugins`, which is a DSL for constructing the dependency list. The requires method typically contains one of the following values:

- `empty` (No plugins)
- other auto plugins
- `&&` operator (for defining multiple dependencies)

#### Root plugins and triggered plugins

Some plugins should always be explicitly enabled on projects. we call
these root plugins, i.e. plugins that are "root" nodes in the plugin
dependency graph. An auto plugin is by default a root plugin.

Auto plugins also provide a way for plugins to automatically attach themselves to
projects if their dependencies are met. We call these triggered plugins,
and they are created by overriding the `trigger` method.

For example, we might want to create a triggered plugin that can append commands automatically to the build. To do this, set the `requires` method to return `empty`, and override the `trigger` method with `allRequirements`.

```scala
package sbthello

import sbt._
import Keys._

object HelloPlugin2 extends AutoPlugin {
  override def trigger = allRequirements
  override lazy val buildSettings = Seq(commands += helloCommand)
  lazy val helloCommand =
    Command.command("hello") { (state: State) =>
      println("Hi!")
      state
    }
}
```

The build user still needs to include this plugin in `project/plugins.sbt`, but it is no longer needed to be included in `build.sbt`. This becomes more interesting when you do specify a plugin with requirements. Let's modify the `SbtLessPlugin` so that it depends on another plugin:

```scala
package sbtless
import sbt._
import Keys._
object SbtLessPlugin extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = SbtJsTaskPlugin
  override lazy val projectSettings = ...
}
```

As it turns out, `PlayScala` plugin (in case you didn't know, the Play framework is an sbt plugin) lists `SbtJsTaskPlugin` as one of its required plugins. So, if we define a `build.sbt` with:

```scala
lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
```

then the setting sequence from `SbtLessPlugin` will be automatically appended somewhere after the settings from `PlayScala`.

This allows plugins to silently, and correctly, extend existing plugins with more features.  It also can help remove the burden of ordering from the user, allowing the plugin authors greater freedom and power when providing feature for their users.

#### Controlling the import with autoImport

When an auto plugin provides a stable field such as `val` or `object`
named `autoImport`, the contents of the field are wildcard imported
in `set`, `eval`, and `.sbt` files. In the next example, we'll replace
our hello command with a task to get the value of `greeting` easily.
In practice, it's recommended [to prefer settings or tasks to commands][Use-Settings-And-Tasks].

```scala
package sbthello

import sbt._
import Keys._

object HelloPlugin3 extends AutoPlugin {
  object autoImport {
    val greeting = settingKey[String]("greeting")
    val hello = taskKey[Unit]("say hello")
  }
  import autoImport._
  override def trigger = allRequirements
  override lazy val buildSettings = Seq(
    greeting := "Hi!",
    hello := helloTask.value)
  lazy val helloTask =
    Def.task {
      println(greeting.value)
    }
}
```

Typically, `autoImport` is used to provide new keys - `SettingKey`s, `TaskKey`s,
or `InputKey`s - or core methods without requiring an import or qualification.

#### Example Plugin

An example of a typical plugin:

`build.sbt`:

@@snip [build.sbt]($root$/src/sbt-test/ref/plugins-obfuscate/build.sbt) {}

`ObfuscatePlugin.scala`:

```scala
package sbtobfuscate

import sbt._
import sbt.Keys._

object ObfuscatePlugin extends AutoPlugin {
  // by defining autoImport, the settings are automatically imported into user's `*.sbt`
  object autoImport {
    // configuration points, like the built-in `version`, `libraryDependencies`, or `compile`
    val obfuscate = taskKey[Seq[File]]("Obfuscates files.")
    val obfuscateLiterals = settingKey[Boolean]("Obfuscate literals.")

    // default values for the tasks and settings
    lazy val baseObfuscateSettings: Seq[Def.Setting[_]] = Seq(
      obfuscate := {
        Obfuscate(sources.value, (obfuscate / obfuscateLiterals).value)
      },
      obfuscate / obfuscateLiterals := false
    )
  }

  import autoImport._
  override def requires = sbt.plugins.JvmPlugin

  // This plugin is automatically enabled for projects which are JvmPlugin.
  override def trigger = allRequirements

  // a group of settings that are automatically added to projects.
  override val projectSettings =
    inConfig(Compile)(baseObfuscateSettings) ++
    inConfig(Test)(baseObfuscateSettings)
}

object Obfuscate {
  def apply(sources: Seq[File], obfuscateLiterals: Boolean): Seq[File] = {
    // TODO obfuscate stuff!
    sources
  }
}
```

#### Usage example

A build definition that uses the plugin might look like. `obfuscate.sbt`:

```scala
obfuscate / obfuscateLiterals := true
```

#### Global plugins example

The simplest global plugin definition is declaring a library or plugin
in `$global_plugin_sbt_file$`:

```scala
libraryDependencies += "org.example" %% "example-plugin" % "0.1"
```

This plugin will be available for every sbt project for the current
user.

In addition:

- Jars may be placed directly in `$global_plugins_base$lib/`
   and will be available to every build definition for the current user.
- Dependencies on plugins built from source may be declared in
   `$global_plugins_base$project/Build.scala` as described at
   [.scala build definition][Full-Def].
- A Plugin may be directly defined in Scala
   source files in `$global_plugins_base$`, such as
   `$global_plugins_base$MyPlugin.scala`.
   `$global_plugins_base$/build.sbt`
   should contain `sbtPlugin := true`. This can be used for quicker
   turnaround when developing a plugin initially:
   
   1.  Edit the global plugin code
   2.  `reload` the project you want to use the modified plugin in
   3.  sbt will rebuild the plugin and use it for the project.
       Additionally, the plugin will be available in other projects on
       the machine without recompiling again. This approach skips the
       overhead of `publishLocal` and `clean`ing the plugins directory of the
       project using the plugin.

These are all consequences of `$global_plugins_base$` being a standard
project whose classpath is added to every sbt project's build
definition.

### Using a library in a build definition example

As an example, we'll add the Grizzled Scala library as a plugin.
Although this does not provide sbt-specific functionality, it
demonstrates how to declare plugins.

#### 1a) Manually managed

1.  Download the jar manually from
    [https://oss.sonatype.org/content/repositories/releases/org/clapper/grizzled-scala_2.8.1/1.0.4/grizzled-scala_2.8.1-1.0.4.jar](https://oss.sonatype.org/content/repositories/releases/org/clapper/grizzled-scala_2.8.1/1.0.4/grizzled-scala_2.8.1-1.0.4.jar)
2.  Put it in `project/lib/`

#### 1b) Automatically managed: direct editing approach

Edit `project/plugins.sbt` to contain:

```scala
libraryDependencies += "org.clapper" %% "grizzled-scala" % "1.0.4"
```

If sbt is running, do `reload`.

#### 1c) Automatically managed: command-line approach

We can change to the plugins project in `project/` using
`reload plugins`.

```
\$ sbt
> reload plugins
[info] Set current project to default (in build file:/Users/sbt/demo2/project/)
>
```

Then, we can add dependencies like usual and save them to
`project/plugins.sbt`. It is useful, but not required, to run `update`
to verify that the dependencies are correct.

```
> set libraryDependencies += "org.clapper" %% "grizzled-scala" % "1.0.4"
...
> update
...
> session save
...
```

To switch back to the main project use `reload return`:

```
> reload return
[info] Set current project to root (in build file:/Users/sbt/demo2/)
```

#### 1d) Project dependency

This variant shows how to use sbt's external project support to declare
a source dependency on a plugin. This means that the plugin will be
built from source and used on the classpath.

Edit `project/plugins.sbt`

```scala
lazy val root = (project in file(".")).dependsOn(assemblyPlugin)

lazy val assemblyPlugin = RootProject(uri("git://github.com/sbt/sbt-assembly"))
```

If sbt is running, run `reload`.

Note that this approach can be useful when developing a plugin. A
project that uses the plugin will rebuild the plugin on `reload`. This
saves the intermediate steps of `publishLocal` and `update`. It can also
be used to work with the development version of a plugin from its
repository.

It is however recommended to explicitly specify the commit or tag by appending
it to the repository as a fragment:

```scala
lazy val assemblyPlugin = uri("git://github.com/sbt/sbt-assembly#0.9.1")
```

One caveat to using this method is that the local sbt will try to run
the remote plugin's build. It is quite possible that the plugin's own
build uses a different sbt version, as many plugins cross-publish for
several sbt versions. As such, it is recommended to stick with binary
artifacts when possible.

#### 2) Use the library

Grizzled Scala is ready to be used in build definitions. This includes
the `eval` and `set` commands and `.sbt` and `project/*.scala` files.

```
> eval grizzled.sys.os
```

In a `build.sbt` file:

```scala
import grizzled.sys._
import OperatingSystem._

libraryDependencies ++=
    if(os == Windows)
        Seq("org.example" % "windows-only" % "1.0")
    else
        Seq.empty
```

### Best Practices

If you're a plugin writer, please consult the [Plugins Best Practices][Plugins-Best-Practices]
page; it contains a set of guidelines to help you ensure that your
plugin is consistent and plays well with other plugins.

For cross building sbt plugins see also [Cross building plugins][Cross-Build-Plugins].

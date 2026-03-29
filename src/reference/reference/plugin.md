
  [Using-Plugins]: Using-Plugins.html
  [plugin-basics]: ../guide/plugin-basics.md
  [Full-Def]: Full-Def.html
  [Best-Practices]: Best-Practices.html
  [Plugins-Best-Practices]: Plugins-Best-Practices.html
  [Use-Settings-And-Tasks]: Plugins-Best-Practices.html#Use+settings+and+tasks.+Avoid+commands.
  [Commands]: Commands.html
  [Cross-Build-Plugins]: Cross-Build-Plugins.html

Plugin
======

~~~admonish note
This page covers plugin details for intermediate users.
See [Plugin basics][plugin-basics] for the general introduction of the plugin system.
~~~

## Description

A _plugin_ is a way to extend the build definition, often to add capabilities beyond the built-in settings and tasks. Some of the example usages includes building Docker containers, supporting to Scala.JS, and generate GitHub Actions YAML.

A plugin defines a sequence of sbt settings
that are automatically added to all projects or that are explicitly
declared for selected projects. A plugin
can also define new commands. The plugins in sbt 1.x and 2.x extend the `sbt.AutoPlugin` trait,
which automates the setting orders between dependent plugins.

## Creating a plugin

Conceptually we can think of the sbt plugins to be _any other libraries on the metabuild_, which is currently on Scala {{scala3_metabuild_version}}. However, in practice modern sbt plugins are built around auto plugin (`sbt.AutoPlugin`), which provides tasks and settings in the correct order. See the Plugin dependencies section for more details.

### build.sbt setup

To make an auto plugin, create a project and enable `SbtPlugin`.

~~~admonish example title="build.sbt"
```scala
version := "0.1.0-SNAPSHOT"
organization := "com.example"
homepage := Some(url("https://github.com/sbt/sbt-hello"))

lazy val root = rootProject
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-hello",
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match
        case "3"    => "2.0.0-RC10" // set minimum sbt version
    }
  )
```
~~~

~~~admonish note
- sbt 2.x plugins must be compiled with Scala 3.x. By not specifying `scalaVersion`, sbt will default to the Scala version suited for a plugin.
- `pluginCrossBuild / sbtVersion` is a setting to compile your plugin against an _older_ version of sbt, which allows the plugin users to choose from a range of sbt versions.
~~~

### projectSettings

In an appropriate namespace, define your auto plugin object
by extending `sbt.AutoPlugin`. With auto plugins, settings are provided by the plugin directly via the `projectSettings` method. Here's an example plugin that adds a task named `hello` to the subprojects:

~~~admonish example title="src/main/sbthello/HelloPlugin.scala"
```scala
{{#include ../../sbt-test/ref/plugins-hello/project/HelloPlugin.scala}}
```
~~~

~~~admonish note title="def vs lazy val"
In Scala, a `def` method can be overridden using a `lazy val`, using a mechanism called _uniform access principle_.
We recommending using `lazy val` for `projectSettings` since the setting definitions tend to be immutable.
~~~

<!--
If the plugin needs to append settings at the build-level (that is, in `ThisBuild`) there's a `buildSettings` method.

The settings returned here are guaranteed to be added to a given build scope only once
regardless of how many projects for that build activate this AutoPlugin.

```scala
override def buildSettings: Seq[Setting[?]] = Nil
```
-->

### globalSettings

The `globalSettings` is appended once to the global settings (for example, `Global / helloGreeting`).
These allow a plugin to provide new functionality or new defaults.
One main use of this feature is to globally add commands, such as for IDE plugins.

```scala
override lazy val globalSettings: Seq[Setting[?]] = Nil
```

Where possible, your plugin should define the default value using `globalSettings`. Providing the value in the widest scope and using the value in narrowest scope give the user the maximum flexibility to change the settings.

### Implementing plugin dependencies

Next step is to define the plugin dependencies.

```scala
package sbtless

import sbt.*
import Keys.*
object SbtLessPlugin extends AutoPlugin:
  override def requires = SbtJsTaskPlugin
  override lazy val projectSettings = ...
end SbtLessPlugin
```

The `requires` method returns a value of type `Plugins`, which is a DSL for constructing the dependency list. The requires method typically contains one of the following values:

- `empty` (No plugins)
- Other auto plugins
- `&&` operator (for defining multiple dependencies)

### Root plugins and triggered plugins

Some plugins should always be explicitly enabled on subprojects. We call
these root plugins, i.e. plugins that are _root_ nodes in the plugin
dependency graph.

Auto plugins also provide a way for plugins to automatically attach themselves to
projects if their dependencies are met. We call these triggered plugins,
and they are created by overriding the `trigger` method.

For example, we might want to create a triggered plugin that can append commands automatically to the build. To do this, set the `requires` method to return `empty`, and override the `trigger` method with `allRequirements`.

```scala
package sbthello

import sbt.*
import Keys.*

object HelloPlugin2 extends AutoPlugin:
  override def trigger = allRequirements

  ....
end HelloPlugin2
```

The build user still needs to include this plugin in `project/plugins.sbt`, but it is no longer needed to be included in `build.sbt`. This becomes more interesting when you do specify a plugin with requirements. Let's modify the `SbtLessPlugin` so that it depends on another plugin:

```scala
package sbtless

import sbt.*
import Keys.*

object SbtLessPlugin extends AutoPlugin:
  override def trigger = allRequirements
  override def requires = SbtJsTaskPlugin
  override lazy val projectSettings = ...
end SbtLessPlugin
```

As it turns out, `PlayScala` plugin (in case you didn't know, the Play framework is an sbt plugin) lists `SbtJsTaskPlugin` as one of its required plugins. So, if we define a `build.sbt` with:

```scala
lazy val root = rootProject
  .enablePlugins(PlayScala)
```

then the setting sequence from `SbtLessPlugin` will be automatically appended somewhere after the settings from `PlayScala`.

This allows plugins to silently, and correctly, extend existing plugins with more features.  It also can help remove the burden of ordering from the user, allowing the plugin authors greater freedom and power when providing feature for their users.

### Controlling the import with autoImport

When an auto plugin provides a `val`, `lazy val`, or `object`
named `autoImport`, the contents of the field are wildcard imported
in `set`, `eval`, and `*.sbt` files.

```scala
package sbthello

import sbt.*
import Keys.*

object HelloPlugin3 extends AutoPlugin:
  object autoImport:
    val greeting = settingKey[String]("greeting")
    val hello = taskKey[Unit]("say hello")
  end autoImport

  import autoImport.*
  override def trigger = allRequirements
  ....
end HelloPlugin3
```

Typically, `autoImport` is used to provide new keys - `SettingKey`s, `TaskKey`s,
or `InputKey`s - or core methods without requiring an import or qualification.

### Example Plugin

An example of a typical plugin:

~~~admonish example title="Plugin build.sbt"
```scala
{{#include ../../sbt-test/ref/plugins-obfuscate/build.sbt}}
```
~~~

~~~admonish example title="src/main/scala/sbtobfuscate/ObfuscatePlugin.scala"
```scala
{{#include ../../sbt-test/ref/plugins-obfuscate/src/main/scala/sbtobfuscate/ObfuscatePlugin.scala}}
```
~~~

#### Usage example

A build definition that uses the plugin might look like `build.sbt`:

```scala
obfuscateLiterals := true
```

## Using an auto plugin

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

## Metabuild

A metabuild is a project under `project/` folder. This
project's classpath is the classpath used for build definitions in
`project/` and any `.sbt` files in the project's base
directory. It is also used for the `eval` and `set` commands.

Specifically,

1.  Managed dependencies declared by the metabuild are
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
the (root) project's metabuild definition. This allows manipulating
the metabuild project like a normal subproject. `reload return` changes back
to the original build. Any session settings for the metabuild project
that have not been saved are dropped.

An auto plugin is a module that defines settings to automatically inject into
subprojects. Additionally, an auto plugin provides the following feature:

- Automatically import selective names to `.sbt` files and the `eval` and `set` commands.
- Specify plugin dependencies to other auto plugins.
- Automatically activate itself when all dependencies are present.
- Specify `projectSettings`, `buildSettings`, and `globalSettings` as appropriate.

## Plugin dependencies

Prior to the introduction of the `AutoPlugin` system in sbt 0.13.5, if a plugin depended on the settings and tasks from another plugin, the build users had to include the plugins in the right order. This became complicated and error-prone as the number of plugins increased within an application.

An auto plugin can depend on other auto plugins and ensure these dependency settings are loaded first.

Suppose we have the `SbtLessPlugin` and the `SbtCoffeeScriptPlugin`, which in turn depends on the `SbtJsTaskPlugin`, `SbtWebPlugin`, and `JvmPlugin`. Instead of manually activating all of these plugins, a project can just activate the `SbtLessPlugin` and `SbtCoffeeScriptPlugin` like this:

```scala
lazy val root = (project in file("."))
  .enablePlugins(SbtLessPlugin, SbtCoffeeScriptPlugin)
```

This will pull in the right setting sequence from the plugins in the right order.

## Maven publishing convention

sbt 2.x plugins are published to Maven layout repositories with `_sbt2_3` suffix in its artifact name. For example, sbt-ci-release 1.11.2 is published to the Central Repo as <https://repo1.maven.org/maven2/com/github/sbt/sbt-ci-release_sbt2_3/1.11.2/>. This suffix is appended automatically by sbt, so the `name` should just be called `sbt-ci-release` etc in `build.sbt`.

<!--
### Best Practices

If you're a plugin writer, please consult the [Plugins Best Practices][Plugins-Best-Practices]
page; it contains a set of guidelines to help you ensure that your
plugin is consistent and plays well with other plugins.

For cross building sbt plugins see also [Cross building plugins][Cross-Build-Plugins].
-->

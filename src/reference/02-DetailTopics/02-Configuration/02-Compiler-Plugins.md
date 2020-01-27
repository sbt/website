---
out: Compiler-Plugins.html
---

Compiler Plugin Support
-----------------------

There is some special support for using compiler plugins. You can set
`autoCompilerPlugins` to `true` to enable this functionality.

```scala
autoCompilerPlugins := true
```

To use a compiler plugin, you either put it in your unmanaged library
directory (`lib/` by default) or add it as managed dependency in the
`plugin` configuration. `addCompilerPlugin` is a convenience method for
specifying `plugin` as the configuration for a dependency:

```scala
addCompilerPlugin("org.scala-tools.sxr" %% "sxr" % "0.3.0")
```

The `compile` and `testCompile` actions will use any compiler plugins
found in the `lib` directory or in the `plugin` configuration. You are
responsible for configuring the plugins as necessary. For example, Scala
X-Ray requires the extra option:

```scala
// declare the main Scala source directory as the base directory
scalacOptions :=
    scalacOptions.value :+ ("-Psxr:base-directory:" + (Compile / scalaSource).value.getAbsolutePath)
```

You can still specify compiler plugins manually. For example:

```scala
scalacOptions += "-Xplugin:<path-to-sxr>/sxr-0.3.0.jar"
```

### Continuations Plugin Example

Support for continuations in Scala 2.12 is implemented as a compiler
plugin. You can use the compiler plugin support for this, as shown here.

```scala
val continuationsVersion = "1.0.3"

autoCompilerPlugins := true

addCompilerPlugin("org.scala-lang.plugins" % "scala-continuations-plugin_2.12.2" % continuationsVersion)

libraryDependencies += "org.scala-lang.plugins" %% "scala-continuations-library" % continuationsVersion

scalacOptions += "-P:continuations:enable"
```

### Version-specific Compiler Plugin Example

Adding a version-specific compiler plugin can be done as follows:

```scala
val continuationsVersion = "1.0.3"

autoCompilerPlugins := true

libraryDependencies +=
    compilerPlugin("org.scala-lang.plugins" % ("scala-continuations-plugin_" + scalaVersion.value) % continuationsVersion)

libraryDependencies += "org.scala-lang.plugins" %% "scala-continuations-library" % continuationsVersion

scalacOptions += "-P:continuations:enable"
```

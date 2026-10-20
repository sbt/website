
  [Basic-Def]: build-definition-basics.md
  [Library-Dependencies]: library-dependency-basics.html
  [Multi-Project]: multi-project-basics.md
  [Inspecting-Settings]: ../docs/Inspecting-Settings.html
  [Scope-Delegation]: Scope-Delegation.html

Scope basics
============

This page describes scopes.

## The whole story about keys

[Previously][Basic-Def] we pretended that a key like `name` corresponded
to one entry in sbt's map of key-value pairs. This was a simplification.
In the real builds, each key can have an associated value in more than one
context, called a *scope.*

Some concrete examples:

- if you have multiple subprojects in your build definition, a key can
  have a different value in each project.
- the `compile` key may have a different value for your main sources and
  your test sources, if you want to compile them differently.
- the `scalacOptions` key (which contains Scala compiler options) may have different values when compiling class files
  (`compile`) or running the REPL (`console`).

## Unified slash syntax

A full scope in sbt is described using the _unified slash syntax_:

```scala
projA / Compile / console / scalacOptions
```

These represent the three level of scoping:

- The subproject scoping
- The dependency configuration scoping
- The task scoping

~~~admonish note
Introduced in sbt 1.1, the unified slash syntax can be used in both
the sbt shell and in `build.sbt`.
~~~

### Scoping by the subproject

If you [put multiple subprojects in a single build][Multi-Project], each
subproject needs its own settings.
From the sbt shell, you can compile a specific subproject as follows:

```bash
sbt> projA/compile
```

### Scoping by the dependency configuration

A dependency configuration (or _configuration_ for short) defines
a graph of library dependencies, potentially with its own
classpath, sources, generated packages, etc.

Some configurations you'll see in sbt:

- `Compile` which defines the main code (`src/main/scala`).
- `Test` which defines how to compile tests (`src/test/scala`).
- `Runtime` which defines the classpath for the `run` task.

From the sbt shell, you can compile the test code as follows, which would also compile the main code:

```bash
sbt> Test/compile
```

In `build.sbt`, you can specify the tests to be forked as follows:

```scala
Test / fork := true
```

### Scoping by task

Settings can affect how a task works. For example, the `compile` task
is affected by the `scalacOptions` setting.

To support this, a task key (such as `compile`) can be a scope for
another key (such as `scalacOptions`).
Various tasks that invoke the Scala compiler (`compile`, `console`,
`doc`) can share keys related to compilation such as `scalacOptions`. Those keys can have distinct values.

```scala
scalaVersion := "{{scala3_example_version}}"
Compile / scalacOptions ++= List("-deprecation", "-Werror", "-Wunused:imports")
Compile / console / scalacOptions --= List("-Werror", "-Wunused:imports")
```

### Inspecting scopes

In sbt shell, you can use the `inspect` command to understand
keys and their scopes. Try `inspect Test / fullClasspath`:

```bash
sbt:Hello> inspect Test / fullClasspath
[info] Task: Seq[class sbt.internal.util.Attributed]
[info] Description:
[info] 	The exported classpath, consisting of build products and unmanaged and
        managed, internal and external dependencies.
[info] Provided by:
[info] 	Test / fullClasspath
[info] Defined at:
[info] 	Defaults.scala:2823
[info] Dependencies:
[info] 	Test / fullClasspath / streams
[info] 	Test / exportedProducts
[info] 	Test / dependencyClasspath
[info] Reverse dependencies:
[info] 	Test / fgRun
[info] 	Test / run / bspBuildTargetJvmEnvironmentItem
[info] 	Test / console
[info] 	Test / fgRunMain
[info] 	Test / testLoader
[info] 	Test / bspBuildTargetJvmEnvironmentItem
....
```

For more details, see [sbt inspect](../reference/sbt-inspect.md) reference guide.

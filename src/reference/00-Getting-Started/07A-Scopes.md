---
out: Scopes.html
---

  [Basic-Def]: Basic-Def.html
  [Task-Graph]: Task-Graph.html
  [Library-Dependencies]: Library-Dependencies.html
  [Multi-Project]: Multi-Project.html
  [Inspecting-Settings]: ../docs/Inspecting-Settings.html
  [Scope-Delegation]: Scope-Delegation.html

Scopes
------

This page describes scopes. It assumes you've read and understood the
previous pages, [build definition][Basic-Def] and [task graph][Task-Graph].

### The whole story about keys

[Previously][Basic-Def] we pretended that a key like `name` corresponded
to one entry in sbt's map of key-value pairs. This was a simplification.

In truth, each key can have an associated value in more than one
context, called a *scope.*

Some concrete examples:

- if you have multiple projects (also called subprojects) in your build definition, a key can
  have a different value in each project.
- the `compile` key may have a different value for your main sources and
  your test sources, if you want to compile them differently.
- the `packageOptions` key (which contains options for creating jar
  packages) may have different values when packaging class files
  (`packageBin`) or packaging source code (`packageSrc`).

*There is no single value for a given key `name`*, because the value may
differ according to scope.

However, there is a single value for a given *scoped* key.

If you think about sbt processing a list of settings to generate a
key-value map describing the project, as
[discussed earlier][Basic-Def], the keys in that key-value map are
*scoped* keys. Each setting defined in the build definition (for example
in `build.sbt`) applies to a scoped key as well.

Often the scope is implied or has a default, but if the defaults are
wrong, you'll need to mention the desired scope in `build.sbt`.

### Scope axes

A *scope axis* is a type constructor similar to `Option[A]`,
that is used to form a component in a scope.

There are three scope axes:

- The subproject axis
- The dependency configuration axis
- The task axis

If you're not familiar with the notion of *axis*, we can think of the RGB color cube
as an example:

![color cube](files/rgb_color_solid_cube.png)

In the RGB color model, all colors are represented by a point in the cube whose axes
correspond to red, green, and blue components encoded by a number.
Similarly, a full scope in sbt is formed by a **tuple** of a subproject,
a configuration, and a task value:

```scala
projA / Compile / console / scalacOptions
```

This is the slash syntax, introduced in sbt 1.1, for:

```scala
scalacOptions in (
  Select(projA: Reference),
  Select(Compile: ConfigKey),
  Select(console.key)
)
```

#### Scoping by the subproject axis

If you [put multiple projects in a single build][Multi-Project], each
project needs its own settings. That is, keys can be scoped according to
the project.

The project axis can also be set to `ThisBuild`, which means the "entire build",
so a setting applies to the entire build rather than a single project.
Build-level settings are often used as a fallback when a project doesn't define a
project-specific setting. We will discuss more on build-level settings later in this page.

#### Scoping by the configuration axis

A *dependency configuration* (or "configuration" for short) defines
a graph of library dependencies, potentially with its own
classpath, sources, generated packages, etc. The dependency configuration concept
comes from Ivy, which sbt used to use for
managed dependencies [Library Dependencies][Library-Dependencies], and from
[MavenScopes](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Scope).

Some configurations you'll see in sbt:

- `Compile` which defines the main build (`src/main/scala`).
- `Test` which defines how to build tests (`src/test/scala`).
- `Runtime` which defines the classpath for the `run` task.

By default, all the keys associated with compiling, packaging, and
running are scoped to a configuration and therefore may work differently
in each configuration. The most obvious examples are the task keys
`compile`, `package`, and `run`; but all the keys which *affect* those keys
(such as `sourceDirectories` or `scalacOptions` or `fullClasspath`) are also
scoped to the configuration.

Another thing to note about a configuration is that it can extend other configurations.
The following figure shows the extension relationship among the most common configurations.

![dependency configurations](files/sbt-configurations.png)

`Test` and `IntegrationTest` extends `Runtime`; `Runtime` extends `Compile`;
`CompileInternal` extends `Compile`, `Optional`, and `Provided`.

#### Scoping by Task axis

Settings can affect how a task works. For example, the `packageSrc` task
is affected by the `packageOptions` setting.

To support this, a task key (such as `packageSrc`) can be a scope for
another key (such as `packageOptions`).

The various tasks that build a package (`packageSrc`, `packageBin`,
`packageDoc`) can share keys related to packaging, such as `artifactName`
and `packageOptions`. Those keys can have distinct values for each
packaging task.

#### Zero scope component

Each scope axis can be filled in with an instance of the axis type (analogous to `Some(_)`),
or the axis can be filled in with the special value `Zero`.
So we can think of `Zero` as `None`.

`Zero` is a universal fallback for all scope axes,
but its direct use should be reserved to sbt and plugin authors in most cases.

`Global` is a scope that sets `Zero` to all axes: `Zero / Zero / Zero`. In other words, `Global / someKey` is a shorthand for `Zero / Zero / Zero / someKey`.

### Referring to scopes in a build definition

If you create a setting in `build.sbt` with a bare key, it will be scoped
to (current subproject / configuration `Zero` / task `Zero`):

```scala
lazy val root = (project in file("."))
  .settings(
    name := "hello"
  )
```

Run sbt and `inspect name` to see that it's provided by
`ProjectRef(uri("file:/private/tmp/hello/"), "root") / name`, that is, the
project is `ProjectRef(uri("file:/Users/xxx/hello/"), "root")`, and
neither configuration nor task scope are shown (which means `Zero`).

A bare key on the right hand side is also scoped to
(current subproject / configuration `Zero` / task `Zero`):

@@snip [build.sbt]($root$/src/sbt-test/ref/scopes/build.sbt) { #unscoped }

The types of any of the scope axes have been method enriched to have a `/` operator.
The argument to `/` can be a key or another scope axis. So for
example, though there's no good reason to do this, you could have an instance of the
`name` key scoped to the `Compile` configuration:

@@snip [build.sbt]($root$/src/sbt-test/ref/scopes/build.sbt) { #confScoped }

or you could set the name scoped to the `packageBin` task (pointless! just
an example):

@@snip [build.sbt]($root$/src/sbt-test/ref/scopes/build.sbt) { #taskScoped }

or you could set the `name` with multiple scope axes, for example in the
`packageBin` task in the `Compile` configuration:

@@snip [build.sbt]($root$/src/sbt-test/ref/scopes/build.sbt) { #confAndTaskScoped }

or you could use `Global`:

@@snip [build.sbt]($root$/src/sbt-test/ref/scopes/build.sbt) { #global }

(`Global / concurrentRestrictions` implicitly converts to
`Zero / Zero / Zero / concurrentRestrictions`, setting
all axes to `Zero` scope component; the task and configuration are already
`Zero` by default, so here the effect is to make the project `Zero`,
that is, define `Zero / Zero / Zero / concurrentRestrictions` rather than
`ProjectRef(uri("file:/tmp/hello/"), "root") / Zero / Zero / concurrentRestrictions`)

### Referring to scoped keys from the sbt shell

On the command line and in the sbt shell, sbt displays (and parses)
scoped keys like this:

```
ref / Config / intask / key
```

- `ref` identifies the subproject axis. It could be `<project-id>`, `ProjectRef(uri("file:..."), "id")`, or `ThisBuild` that denotes the "entire build" scope.
- `Config` identifies the configuration axis using the capitalized Scala identifier.
- `intask` identifies the task axis.
- `key` identifies the key being scoped.

`Zero` can appear for each axis.

If you omit part of the scoped key, it will be inferred as follows:

- the current project will be used if you omit the project.
- a key-dependent configuration will be auto-detected if you omit the
  configuration or task.

For more details, see [Interacting with the Configuration System][Inspecting-Settings].

### Examples of scoped key notation in the sbt shell

- `fullClasspath` specifies just a key, so the default scopes are used:
  current project, a key-dependent configuration, and `Zero` task
  scope.
- `Test / fullClasspath` specifies the configuration, so this is
  `fullClasspath` in the `Test` configuration, with defaults for the other
  two scope axes.
- `root / fullClasspath` specifies the project `root`, where the project is
  identified with the project id.
- `root / Zero / fullClasspath` specified the project `root`, and
  specifies `Zero` for the configuration, rather than the default configuration.
- `doc / fullClasspath` specifies the `fullClasspath` key scoped to the `doc`
  task, with the defaults for the project and configuration axes.
- `ProjectRef(uri("file:/tmp/hello/"), "root") / Test / fullClasspath`
  specifies a project `ProjectRef(uri("file:/tmp/hello/"), "root")`.
  Also specifies configuration Test, leaves the default task axis.
- `ThisBuild / version` sets the subproject axis to "entire build" where
  the build is `ThisBuild`, with the default configuration.
- `Zero / fullClasspath` sets the subproject axis to `Zero`,
  with the default configuration.
- `root / Compile / doc / fullClasspath` sets all three scope axes.

### Inspecting scopes

In sbt shell, you can use the `inspect` command to understand
keys and their scopes. Try `inspect Test/fullClasspath`:

```
\$ sbt
sbt:Hello> inspect Test / fullClasspath
[info] Task: scala.collection.Seq[sbt.internal.util.Attributed[java.io.File]]
[info] Description:
[info]  The exported classpath, consisting of build products and unmanaged and managed, internal and external dependencies.
[info] Provided by:
[info]  ProjectRef(uri("file:/tmp/hello/"), "root") / Test / fullClasspath
[info] Defined at:
[info]  (sbt.Classpaths.classpaths) Defaults.scala:1639
[info] Dependencies:
[info]  Test / dependencyClasspath
[info]  Test / exportedProducts
[info]  Test / fullClasspath / streams
[info] Reverse dependencies:
[info]  Test / testLoader
[info] Delegates:
[info]  Test / fullClasspath
[info]  Runtime / fullClasspath
[info]  Compile / fullClasspath
[info]  fullClasspath
[info]  ThisBuild / Test / fullClasspath
[info]  ThisBuild / Runtime / fullClasspath
[info]  ThisBuild / Compile / fullClasspath
[info]  ThisBuild / fullClasspath
[info]  Zero / Test / fullClasspath
[info]  Zero / Runtime / fullClasspath
[info]  Zero / Compile / fullClasspath
[info]  Global / fullClasspath
[info] Related:
[info]  Compile / fullClasspath
[info]  Runtime / fullClasspath
```

On the first line, you can see this is a task (as opposed to a setting,
as explained in [.sbt build definition][Basic-Def]). The value
resulting from the task will have type
`scala.collection.Seq[sbt.Attributed[java.io.File]]`.

"Provided by" points you to the scoped key that defines the value, in
this case
`ProjectRef(uri("file:/tmp/hello/"), "root") / Test / fullClasspath` (which
is the `fullClasspath` key scoped to the `Test` configuration and the
`ProjectRef(uri("file:/tmp/hello/"), "root")` project).

"Dependencies" was discussed in detail in the [previous page][Task-Graph].

We'll discuss "Delegates" later.

Try `inspect fullClasspath` (as opposed to the above example,
inspect `Test / fullClasspath`) to get a sense of the difference. Because
the configuration is omitted, it is autodetected as `Compile`.
`inspect Compile / fullClasspath` should therefore look the same as
`inspect fullClasspath`.

Try `inspect ThisBuild / Zero / fullClasspath` for another contrast. `fullClasspath` is not
defined in the `Zero` configuration scope by default.

Again, for more details, see [Interacting with the Configuration System][Inspecting-Settings].

### When to specify a scope

You need to specify the scope if the key in question is normally scoped.
For example, the `compile` task, by default, is scoped to `Compile` and `Test`
configurations, and does not exist outside of those scopes.

To change the value associated with the `compile` key, you need to write
`Compile / compile` or `Test / compile`. Using plain `compile` would define
a new compile task scoped to the current project, rather than overriding
the standard compile tasks which are scoped to a configuration.

If you get an error like *"Reference to undefined setting"*, often
you've failed to specify a scope, or you've specified the wrong scope.
The key you're using may be defined in some other scope. sbt will try to
suggest what you meant as part of the error message; look for "Did you
mean Compile / compile?"

One way to think of it is that a name is only *part* of a key. In
reality, all keys consist of both a name, and a scope (where the scope
has three axes). The entire expression
`Compile / packageBin / packageOptions` is a key name, in other words.
Simply `packageOptions` is also a key name, but a different one (for keys
with no slashes, a scope is implicitly assumed: current project, `Zero`
config, `Zero` task).

### Build-level settings

An advanced technique for factoring out common settings
across subprojects is to define the settings scoped to `ThisBuild`.

If a key that is scoped to a particular subproject is not found,
sbt will look for it in `ThisBuild` as a fallback.
Using the mechanism, we can define a build-level default setting for
frequently used keys such as `version`, `scalaVersion`, and `organization`.

```scala
ThisBuild / organization := "com.example",
ThisBuild / scalaVersion := "$example_scala_version$",
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    publish / skip := true
  )

lazy val core = (project in file("core"))
  .settings(
    // other settings
  )

lazy val util = (project in file("util"))
  .settings(
    // other settings
  )
```

For convenience, there is `inThisBuild(...)` function that will
scope both the key and the body of the setting expression to `ThisBuild`.
Putting setting expressions in there would be equivalent to prepending `ThisBuild /` where possible.

Due to the nature of [scope delegation][Scope-Delegation] that we will cover later,
build-level settings should be set only to a pure value or settings from either `Global` or `ThisBuild` scoping.

### Scope delegation

A scoped key may be undefined, if it has no value associated with it in
its scope.

For each scope axis, sbt has a fallback search path made up of other scope values.
Typically, if a key has no associated value in a more-specific scope,
sbt will try to get a value from a more general scope, such as the `ThisBuild` scope.

This feature allows you to set a value once in a more general scope,
allowing multiple more-specific scopes to inherit the value.
We will discuss [scope delegation][Scope-Delegation] in detail later.

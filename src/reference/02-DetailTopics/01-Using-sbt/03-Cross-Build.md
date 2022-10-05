---
out: Cross-Build.html
---

  [Command-Line-Reference]: Command-Line-Reference.html
  [Publishing]: Publishing.html
  [Cross-Build-Plugins]: Cross-Build-Plugins.html
  [playframework4520]: https://github.com/playframework/playframework/pull/4520

Cross-building
--------------

### Introduction

Different versions of Scala can be binary incompatible, despite
maintaining source compatibility. This page describes how to use `sbt`
to build and publish your project against multiple versions of Scala and
how to use libraries that have done the same.

For cross building sbt plugins see also [Cross building plugins][Cross-Build-Plugins].

### Publishing conventions

The underlying mechanism used to indicate which version of Scala a
library was compiled against is to append `_<scala-binary-version>` to the
library's name. For example, the artifact name `dispatch-core_2.12` is used
when compiled against Scala 2.12.0, 2.12.1 or any 2.12.x version. This fairly simple approach
allows interoperability with users of Maven, Ant and other build tools.

For pre-prelease versions of Scala such as 2.13.0-RC1 and for versions prior to 2.10.x,
full version is used as the suffix.

The rest of this page describes how sbt handles this for you as part
of cross-building.

### Using cross-built libraries

To use a library built against multiple versions of Scala, double the
first `%` in an inline dependency to be `%%`. This tells `sbt` that it
should append the current version of Scala being used to build the
library to the dependency's name. For example:

```scala
libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.13.3"
```
A nearly equivalent, manual alternative for a fixed version of Scala is:

```scala
libraryDependencies += "net.databinder.dispatch" % "dispatch-core_2.12" % "0.13.3"
```

### Cross building a project using sbt-projectmatrix

No plugin is required to enable cross-building in sbt, although consider using 
[sbt-projectmatrix](https://github.com/sbt/sbt-projectmatrix) that is capable of 
cross building across Scala versions and different platforms in parallel.

### Cross building a project statefully

Define the versions of Scala to build against in the
`crossScalaVersions` setting. Versions of Scala 2.10.2 or later are
allowed. For example, in a `.sbt` build definition:

```scala
lazy val scala212 = "$example_scala_version$"
lazy val scala211 = "$example_scala211$"
lazy val supportedScalaVersions = List(scala212, scala211)

ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := scala212

lazy val root = (project in file("."))
  .aggregate(util, core)
  .settings(
    // crossScalaVersions must be set to Nil on the aggregating project
    crossScalaVersions := Nil,
    publish / skip := true
  )

lazy val core = (project in file("core"))
  .settings(
    crossScalaVersions := supportedScalaVersions,
    // other settings
  )

lazy val util = (project in file("util"))
  .settings(
    crossScalaVersions := supportedScalaVersions,
    // other settings
  )
```

**Note**: `crossScalaVersions` must be set to `Nil` on the root project to avoid double publishing.

To build against all versions listed in `crossScalaVersions`, prefix
the action to run with `+`. For example:

```
> + test
```

A typical way to use this feature is to do development on a single Scala
version (no `+` prefix) and then cross-build (using `+`) occasionally
and when releasing.

### Change settings depending on the Scala version

Here's how we can change some settings depending on the Scala version.
`CrossVersion.partialVersion(scalaVersion.value)` returns `Option[(Int, Int)]` containing
the first two segments of the Scala version.

This can be useful for instance if you include a dependency that requires the macro paradise
compiler plugin for Scala 2.12 and the `-Ymacro-annotations` compiler option for Scala 2.13.

```scala
lazy val core = (project in file("core"))
  .settings(
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n <= 12 =>
          List(compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full))
        case _                       => Nil
      }
    },
    Compile / scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n <= 12 => Nil
        case _                       => List("-Ymacro-annotations")
      }
    },
  )
```

<a name="crossPaths"></a>
#### Scala-version specific source directory

In addition to `src/main/scala/` directory, `src/main/scala-<scala binary version>/`
directory is included as a source directory.
For, example if the current subproject's `scalaVersion` is 2.12.10, then
`src/main/scala-2.12` is included as a Scala-version specific source.

By setting `crossPaths` to `false`, you can opt out of both Scala-version source directory
and the `_<scala-binary-version>` publishing convention. This might be useful for non-Scala projects.

Similarly, the build products such as `*.class` files are written into
`crossTarget` directory, which by default is `target/scala-<scala binary version>`.

#### Cross building with a Java project

A special care must be taken when cross building involves pure Java project.
Let's say in the following example, `network` is a Java project, and `core` is
a Scala project that depends on `network`.

```scala
lazy val scala212 = "$example_scala_version$"
lazy val scala211 = "$example_scala211$"
lazy val supportedScalaVersions = List(scala212, scala211)

ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := scala212

lazy val root = (project in file("."))
  .aggregate(network, core)
  .settings(
    // crossScalaVersions must be set to Nil on the aggregating project
    crossScalaVersions := Nil,
    publish / skip := false
  )

// example Java project
lazy val network = (project in file("network"))
  .settings(
    // set to exactly one Scala version
    crossScalaVersions := List(scala212),
    crossPaths := false,
    autoScalaLibrary := false,
    // other settings
  )

lazy val core = (project in file("core"))
  .dependsOn(network)
  .settings(
    crossScalaVersions := supportedScalaVersions,
    // other settings
  )
```

1. `crossScalaVersions` must be set to `Nil` on the aggregating projects such as the root.
2. Java subprojects should set `crossPaths` to false, which turns off the `_<scala-binary-version>` publishing convention and the Scala-version specific source directory.
3. Java subprojects should have exactly one Scala version in `crossScalaVersions` to avoid double publishing, typically `scala212`.
4. Scala subprojects can have multiple Scala versions in `crossScalaVersions`, but must avoid aggregating Java subprojects.

#### Switching Scala version

You can use `++ <version> [command]` to temporarily switch the Scala version currently
being used to build the subprojects given that `<version>` is listed in their `crossScalaVersions`.

For example:

```
> ++ $example_scala_version$
[info] Setting version to $example_scala_version$
> ++ $example_scala211$
[info] Setting version to $example_scala211$
> compile
```

`<version>` should be either a version for Scala published to a repository or
the path to a Scala home directory, as in `++ /path/to/scala/home`.
See [Command Line Reference][Command-Line-Reference] for details.

When a `[command]` is passed in to `++`, it will execute the command
on the subprojects that supports the given `<version>`.

For example:

```
> ++ $example_scala211$ -v test
[info] Setting Scala version to $example_scala211$ on 1 projects.
[info] Switching Scala version on:
[info]     core ($example_scala_version$, $example_scala211$)
[info] Excluding projects:
[info]   * root ()
[info]     network ($example_scala_version$)
[info] Reapplying settings...
[info] Set current project to core (in build file:/Users/xxx/hello/)
```

Sometimes you might want to force the Scala version switch regardless of the `crossScalaVersions` values.
You can use `++ <version>!` with exclamation mark for that.

For example:

```
> ++ 2.13.0-M5! -v
[info] Forcing Scala version to 2.13.0-M5 on all projects.
[info] Switching Scala version on:
[info]   * root ()
[info]     core ($example_scala_version$, $example_scala211$)
[info]     network ($example_scala_version$)
```

#### Cross publishing

The ultimate purpose of `+` is to cross-publish your
project. That is, by doing:

```
> + publishSigned
```

you make your project available to users for different versions of
Scala. See [Publishing][Publishing] for more details on publishing your project.

In order to make this process as quick as possible, different output and
managed dependency directories are used for different versions of Scala.
For example, when building against Scala 2.12.7,

-   `./target/` becomes `./target/scala_2.12/`
-   `./lib_managed/` becomes `./lib_managed/scala_2.12/`

Packaged jars, wars, and other artifacts have `_<scala-version>`
appended to the normal artifact ID as mentioned in the Publishing
Conventions section above.

This means that the outputs of each build against each version of Scala
are independent of the others. sbt will resolve your dependencies for
each version separately. This way, for example, you get the version of
Dispatch compiled against 2.11 for your 2.11.x build, the version
compiled against 2.12 for your 2.12.x builds, and so on.

#### Overriding the publishing convention

`crossVersion` setting can override the publishing convention:

- `CrossVersion.disabled` (no suffix)
- `CrossVersion.binary` (`_<scala-binary-version>`)
- `CrossVersion.full` (`_<scala-version>`)

The default is either `CrossVersion.binary` or `CrossVersion.disabled`
depending on the value of `crossPaths`.

Because (unlike Scala library) Scala compiler is not forward compatible among
the patch releases, compiler plugins should use `CrossVersion.full`.

#### Scala 3 specific cross-versions 

In a Scala 3 project you can use Scala 2.13 libraries:

```scala
("a" % "b" % "1.0") cross CrossVersion.for3Use2_13
```

This is equivalent to using `%%` except it resolves the `_2.13` variant 
of the library  when `scalaVersion` is 3.x.y.

Conversely we have `CrossVersion.for2_13Use3` to use the `_3` variant of the
library when `scalaVersion` is 2.13.x:

```scala
("a" % "b" % "1.0") cross CrossVersion.for2_13Use3
```

**Warning for library authors:** It is generally not safe to publish
a Scala 3 library that depends on a Scala 2.13 library or vice-versa.
The reason is to prevent your end users from having two versions `x_2.13`
and `x_3` of the same x library in their classpath.

#### More about using cross-built libraries

You can have fine-grained control over the behavior for different Scala versions
by using the `cross` method on `ModuleID` These are equivalent:

```scala
"a" % "b" % "1.0"
("a" % "b" % "1.0").cross(CrossVersion.disabled)
```

These are equivalent:

```scala
"a" %% "b" % "1.0"
("a" % "b" % "1.0").cross(CrossVersion.binary)
```

This overrides the defaults to always use the full Scala version instead
of the binary Scala version:

```scala
("a" % "b" % "1.0").cross(CrossVersion.full)
```

`CrossVersion.patch` sits between `CrossVersion.binary` and `CrossVersion.full`
in that it strips off any trailing `-bin-...` suffix which is used to
distinguish variant but binary compatible Scala toolchain builds.

```scala
("a" % "b" % "1.0").cross(CrossVersion.patch)
```

`CrossVersion.constant` fixes a constant value:

```scala
("a" % "b" % "1.0") cross CrossVersion.constant("2.9.1")
```

It is equivalent to:

```scala
"a" % "b_2.9.1" % "1.0"
```

A constant cross version is mainly used when cross-building and a dependency
isn't available for all Scala versions or it uses a different convention
than the default.

```scala
("a" % "b" % "1.0") cross CrossVersion.constant {
  scalaVersion.value match {
    case "2.9.1" => "2.9.0"
    case x => x
  }
}
```

#### Note about sbt-release

sbt-release implemented cross building support by copy-pasting sbt 0.13's `+` implementation,
so at least as of sbt-release 1.0.10, it does not work correctly with sbt 1.x's cross building,
which was prototyped originally as sbt-doge.

To cross publish using sbt-release with sbt 1.x, use the following workaround:

```scala
ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := scala212

import ReleaseTransformations._
lazy val root = (project in file("."))
  .aggregate(util, core)
  .settings(
    // crossScalaVersions must be set to Nil on the aggregating project
    crossScalaVersions := Nil,
    publish / skip := true,

    // don't use sbt-release's cross facility
    releaseCrossBuild := false,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      releaseStepCommandAndRemaining("+test"),
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommandAndRemaining("+publishSigned"),
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  )
```

This will then use the real cross (`+`) implementation for testing and publishing.
Credit for this technique goes to James Roper at [playframework#4520][playframework4520] and later inventing `releaseStepCommandAndRemaining`.

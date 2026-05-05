  [virtual-axis]: ../recipes/virtual-axis.md

Cross building setup
====================

This page covers cross building setup. See [Cross building](../concepts/cross-building.html) for general explanation.

Using cross-built libraries 
---------------------------

To use a library built against multiple versions of Scala, double the first `%` in a ModuleID to be `%%`. This tells sbt that it should append the current version of Scala being used to build the library to the dependency’s name. For example:

```scala
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.4"
```

A nearly equivalent, manual alternative for a fixed version of Scala is:

```scala
libraryDependencies += "org.typelevel" % "cats-effect_3" % "3.5.4"
```

### Scala 3 specific cross-versions 

If you are developing an application in Scala 3, you can use Scala 2.13 libraries:

```scala
("a" % "b" % "1.0").cross(CrossVersion.for3Use2_13)
```

This is equivalent to using `%%` except it resolves the `_2.13` variant of the library  when `scalaVersion` is 3.x.y.

Conversely we have `CrossVersion.for2_13Use3` to use the `_3` variant of the library when `scalaVersion` is 2.13.x:

```scala
("a" % "b" % "1.0").cross(CrossVersion.for2_13Use3)
```

```admonish warning
**Warning for library authors:** It is generally not safe to publish a Scala 3 library that depends on a Scala 2.13 library or vice-versa. Doing so could introduce two versions of the same library like `scala-xml_2.13` and `scala-xml_3` on the end users' classpath.
```

### More about using cross-built libraries

You can have fine-grained control over the behavior for different Scala versions by using the `cross` method on `ModuleID` These are equivalent:

```scala
"a" % "b" % "1.0"
("a" % "b" % "1.0").cross(CrossVersion.disabled)
```

These are equivalent:

```scala
"a" %% "b" % "1.0"
("a" % "b" % "1.0").cross(CrossVersion.binary)
```

This overrides the defaults to always use the full Scala version instead of the binary Scala version:

```scala
("a" % "b" % "1.0").cross(CrossVersion.full)
```

`CrossVersion.patch` sits between `CrossVersion.binary` and `CrossVersion.full` in that it strips off any trailing `-bin-...` suffix which is used to distinguish variant but binary compatible Scala toolchain builds.

```scala
("a" % "b" % "1.0").cross(CrossVersion.patch)
```

`CrossVersion.constant` fixes a constant value:

```scala
("a" % "b" % "1.0").cross(CrossVersion.constant("2.9.1"))
```

It is equivalent to:

```scala
"a" % "b_2.9.1" % "1.0"
```

Project matrix
--------------

sbt 2.x introduces project matrix, which enables cross building to happen in parallel by representing cross build using subprojects.

~~~admonish example title="build.sbt"
```scala
lazy val scala3 = "{{scala3_example_version}}"
lazy val scala2_13 = "{{scala2_13_example_version}}"

organization := "com.example"
scalaVersion := scala3
version      := "0.1.0-SNAPSHOT"

lazy val core = (projectMatrix in file("core"))
  .settings(
    name := "core",
  )
  .jvmPlatform(scalaVersions = Seq(scala3, scala2_13))
  .nativePlatform(scalaVersions = Seq(scala3, scala2_13))
  // .jsPlatform(scalaVersions = Seq(scala3))

// optional
lazy val core3 = core.jvm(scala3)
lazy val coreNative3 = core.native(scala3)
```
~~~

~~~admonish example title="project/plugins.sbt"
```scala
addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.5.11")
```
~~~

### Generated subprojects

At the loading time, project matrices expand into subproject by combining the platform and Scala versions:

```bash
sbt:cross-root> projects
[info]     core
[info]     core2_13
[info]     coreNative
[info]     coreNative2_13
[info]   * cross-root
```

By convention, the JVM Scala 3 variant of the matrix is given the name without any suffix, for example `core`.

```bash
sbt:cross-root> core/run
[info] running (fork) example.main
Hello
[success] ok
```

To reference the subprojects inside `build.sbt`, you can call `jvm(...)`, `js(...)`, or `native(...)`:

```scala
// For Scala
lazy val core3 = core.jvm(scalaVersion = scala3)

// For Java
lazy val intf0 = intf.jvm(autoScalaLibrary = false)
```

### Virtual axis

Each combination in a matrix is called a `ProjectRow`; and a ProjectRow is represented as a sequence of `VirtualAxis`.

```scala
final class ProjectRow(
    val autoScalaLibrary: Boolean,
    val axisValues: Seq[VirtualAxis],
    val process: Project => Project
)

object VirtualAxis:

  /**
   * WeakAxis allows a row to depend on another row with Zero value.
   * For example, Scala version can be Zero for Java project, and it's ok.
   */
  abstract class WeakAxis extends VirtualAxis

  /** StrongAxis requires a row to depend on another row with the same selected value. */
  abstract class StrongAxis extends VirtualAxis
end VirtualAxis
```

VirtualAxis splits into `WeakAxis` and `StrongAxis`. Scala version is an example of a weak axis where a row with a Scala version can depend on another Java row without a Scala version. Meanwhile, the platform is a strong axis where a row can depend on another only if the platform matches exactly.

For example, we can define SparkAxis as follows:

~~~admonish example title="project/Axis.scala"
```scala
import sbt.*

case class SparkAxis(idSuffix: String, directorySuffix: String)
  extends VirtualAxis.WeakAxis
```
~~~

See [Cross building on a virtual axis][virtual-axis] recipe for the details on how to use `SparkAxis`.

Publishing convention
---------------------

We use the Scala ABI (application binary interface) version as suffix to denote which version of Scala was used to compile a library. For example, the artifact name `cats-effect_2.13` means Scala 2.13.x was used. `cats-effect_3` means Scala 3.x was used. This fairly simple approach allows interoperability with users of Maven, Ant and other build tools. For pre-prelease versions of Scala, such as 2.13.0-RC1, full version will be considered the ABI version.

`crossVersion` setting can be used to override the publishing convention:

- `CrossVersion.disabled` (no suffix)
- `CrossVersion.binary` (`_<scala-abi-version>`)
- `CrossVersion.full` (`_<scala-version>`)

The default is either `CrossVersion.binary` or `CrossVersion.disabled` depending on the value of `crossPaths`. Because (unlike Scala library) Scala compiler is not forward compatible among the patch releases, compiler plugins should use `CrossVersion.full`.

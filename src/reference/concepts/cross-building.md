Cross building
==============

_Cross building_ refers to the idea of building multiple targets from the same set of source file. This includes Scala cross building, targeting multiple versions of Scala releases; platform cross building, targeting JVM, Scala.JS, and Scala Native; and custom virtual axes like Spark versions.

Using the cross-built libraries
-------------------------------

To use a library built against multiple versions of Scala, double the first `%` in a ModuleID to be `%%`. This tells sbt that it should append the Scala ABI (application binary interface) suffix to the dependency's name. For example:

```scala
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.4"
```

When the current Scala version is Scala 3.x, the above is equivalent to the following:

```scala
libraryDependencies += "org.typelevel" % "cats-effect_3" % "3.5.4"
```

See [cross building setup](../reference/cross-building-setup.html) for more details on the setup.

Historical context
------------------

In the earlier years of Scala (pre-Scala 2.9), the Scala library did not maintain binary compatibility even at the patch level, so each time a new Scala version was released, the libraries had to be re-released against the new version of Scala. This meant that a library user needed to pick a specific version that was compatible with the Scala version they were using.

Even after Scala 2.9.x, the Scala library did not maintain the binary compatibility at minor version level, so the libraries compiled against Scala 2.10.x was not compatible with 2.11.x.

To workaround this problem, sbt developed cross building mechanism such that:

- Same set of source files can be compiled against multiple versions of Scala
- Define a convention to append ABI version (e.g. `_2.12`) to the Maven artifact
- Later this mechanism was extended to support Scala.JS and other platforms

Project matrix
--------------

sbt 2.x introduces project matrix, which enables cross building to happen in parallel.

```scala
organization := "com.example"
scalaVersion := "{{scala3_example_version}}"
version      := "0.1.0-SNAPSHOT"

lazy val core = (projectMatrix in file("core"))
  .settings(
    name := "core"
  )
  .jvmPlatform(scalaVersions = Seq("{{scala3_example_version}}", "{{scala2_13_example_version}}"))
```

See [cross building setup](../reference/cross-building-setup.html) for more details on the setup.

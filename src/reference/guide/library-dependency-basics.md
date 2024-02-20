Library dependency basics
=========================

This page explains the basics of library dependency management using sbt.

sbt uses [Coursier](https://get-coursier.io/) to implement managed
dependencies, so if you're familiar with package managers like Coursier,
npm, PIP, etc you won't have much trouble.

The `libraryDependencies` key
-----------------------------

Declaring a dependency looks like this, where `groupId`, `artifactId`, and
`revision` are strings:

```scala
libraryDependencies += groupID % artifactID % revision
```

or like this, where `configuration` can be a string or a `Configuration` value (such as `Test`):

```scala
libraryDependencies += groupID % artifactID % revision % configuration
```

When you run:

```
> compile
```

sbt will automatically resolve the dependencies and download the JAR files.

### Getting the right Scala version with `%%`

If you use `organization %% moduleName % version` rather than
`organization % moduleName % version` (the difference is the double `%%` after
the `organization`), sbt will add your project's binary Scala version to the artifact
name. This is just a shortcut. You could write this without the `%%`:

```scala
libraryDependencies += "org.scala-lang" % "toolkit_3" % "0.2.0"
```

Assuming the `scalaVersion` for your build is 3.x, the following is
identical (note the double `%%` after `"toolkit"`):

```scala
libraryDependencies += "org.scala-lang" %% "toolkit" % "0.2.0"
```

The idea is that many dependencies are compiled for multiple Scala
versions, and you'd like to get the one that matches your project
to ensure binary compatibility.

Tracking dependencies in one place
----------------------------------

`.scala` files under `project` becomes part of the build definition,
which we can use to track dependencies in one place by
creating a file named `project/Dependencies.scala`.


```scala
// place this file at project/Dependencies.scala

import sbt.*

object Dependencies:
  // versions
  lazy val toolkitV = "0.2.0"

  // libraries
  val toolkit = "org.scala-lang" %% "toolkit" % toolkitV
  val toolkitTest = "org.scala-lang" %% "toolkit-test" % toolkitV
end Dependencies
```

The `Dependencies` object will be available in `build.sbt`.
To make it easier to use the `val`s defined in it, import `Dependencies.*` in your build.sbt file.

```scala
import Dependencies.*

scalaVersion := "{{scala3_example_version}}"
name := "something"
libraryDependencies += toolkit
libraryDependencies += toolkitTest % Test
```

Viewing library dependencies
----------------------------

Type in `Compile/dependencyTree` in the sbt shell to show the library dependency tree, including the transitive dependencies:

```
> Compile/dependencyTree
```

This should display something like the following:

```
sbt:bar> Compile/dependencyTree
[info] default:bar_3:0.1.0-SNAPSHOT
[info]   +-org.scala-lang:scala3-library_3:3.3.1 [S]
[info]   +-org.scala-lang:toolkit_3:0.2.0
[info]     +-com.lihaoyi:os-lib_3:0.9.1
[info]     | +-com.lihaoyi:geny_3:1.0.0
[info]     | | +-org.scala-lang:scala3-library_3:3.1.3 (evicted by: 3.3.1)
[info]     | | +-org.scala-lang:scala3-library_3:3.3.1 [S]
....
```

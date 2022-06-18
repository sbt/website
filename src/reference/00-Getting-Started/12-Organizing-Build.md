---
out: Organizing-Build.html
---

  [Basic-Def]: Basic-Def.html
  [Task-Graph]: Task-Graph.html
  [Using-Plugins]: Using-Plugins.html
  [Library-Dependencies]: Library-Dependencies.html
  [Multi-Project]: Multi-Project.html
  [Plugins]: ../docs/Plugins.html

Organizing the build
--------------------

This page discusses the organization of the build structure.

Please read the earlier pages in the Getting Started Guide first, in
particular you need to understand
[build.sbt][Basic-Def],
[task graph][Task-Graph],
[Library dependencies][Library-Dependencies],
and [Multi-project builds][Multi-Project]
before reading this page.

### sbt is recursive

`build.sbt` conceals how sbt really works. sbt builds are
defined with Scala code. That code, itself, has to be built. What better
way than with sbt?

The `project` directory *is another build inside your build*, which
knows how to build your build. To distinguish the builds,
we sometimes use the term **proper build** to refer to your build,
and **meta-build** to refer to the build in `project`.
The projects inside the metabuild can do anything
any other project can do. *Your build definition is an sbt project.*

And the turtles go all the way down. If you like, you can tweak the
build definition of the build definition project, by creating a
`project/project/` directory.

Here's an illustration.

```
hello/                     # your build's root project's base directory

    Hello.scala            # a source file in your build's root project
                           #   (could be in src/main/scala too)

    build.sbt              # build.sbt is part of the source code for
                           #   meta-build's root project inside project/;
                           #   the build definition for your build

    project/               # base directory of meta-build's root project

        Dependencies.scala # a source file in the meta-build's root project,
                           #   that is, a source file in the build definition
                           #   the build definition for your build

        assembly.sbt       # this is part of the source code for
                           #   meta-meta-build's root project in project/project;
                           #   build definition's build definition

        project/           # base directory of meta-meta-build's root project;
                           #   the build definition project for the build definition

            MetaDeps.scala # source file in the root project of
                           #   meta-meta-build in project/project/
```

*Don't worry!* Most of the time you are not going to need all that. But
understanding the principle can be helpful.

By the way: any time files ending in `.scala` or `.sbt` are used, naming
them `build.sbt` and `Dependencies.scala` are conventions only. This also means
that multiple files are allowed.

### Tracking dependencies in one place

One way of using the fact that `.scala` files under `project` becomes
part of the build definition is to create `project/Dependencies.scala`
to track dependencies in one place.

```scala
import sbt._

object Dependencies {
  // Versions
  lazy val akkaVersion = "$example_akka_version$"

  // Libraries
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % akkaVersion
  val specs2core = "org.specs2" %% "specs2-core" % "$example_specs2_version$"

  // Projects
  val backendDeps =
    Seq(akkaActor, specs2core % Test)
}
```

The `Dependencies` object will be available in `build.sbt`.
To make it easier to use the `val`s defined in it, import `Dependencies._` in your build.sbt file.

```scala
import Dependencies._

ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "$example_scala_version$"

lazy val backend = (project in file("backend"))
  .settings(
    name := "backend",
    libraryDependencies ++= backendDeps
  )
```

This technique is useful when you have a multi-project build that's getting
large, and you want to ensure that subprojects have consistent dependencies.

### When to use `.scala` files

In `.scala` files, you can write any Scala code, including top-level
classes and objects.

The recommended approach is to define most settings in
a multi-project `build.sbt` file,
and using `project/*.scala` files for task implementations or to share values,
such as keys. The use of `.scala` files also depends on how comfortable
you or your team are with Scala.

### Defining auto plugins

For more advanced users, another way of organizing your build is to
define one-off [auto plugins][Plugins] in `project/*.scala`.
By defining triggered plugins, auto plugins can be used as a convenient
way to inject custom tasks and commands across all subprojects.


  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Directories]: Directories.html
  [Organizing-Build]: Organizing-Build.html

Multi project basics
====================

While a simple program can start out as a single-project build,
it's more common for a build to split into smaller, multiple subprojects.

Each subproject in a build has its own source directories, generates
its own JAR file when you run `packageBin`, and in general works like any
other project.

A project is defined by declaring a lazy val of type
[Project](../../api/sbt/Project.html). For example, :

```scala
scalaVersion := "{{scala3_example_version}}"

lazy val core = (project in file("core"))
  .settings(
    name := "core",
  )

lazy val util = (project in file("util"))
  .dependsOn(core)
  .settings(
    name := "util",
  )
```

The name of the val is used as the subproject's ID, which
is used to refer to the subproject at the sbt shell.

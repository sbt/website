
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

A **subproject** is defined by declaring a `lazy val` of type
[Project](../../api/sbt/Project.html). For example, :

```scala
scalaVersion := "{{scala3_example_version}}"
LocalRootProject / publish / skip := true

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

The name of the `val` is used as the subproject's ID, which
is used to refer to the subproject in the sbt shell.

sbt will always define a root project, so in the above build definition,
we will have total of three subprojects.

Subproject dependency
---------------------

A subproject may depend on the code from another subproject.
This is done by declaring `dependsOn(...)`. For example, if `util` needed `util` on its classpath, you would define `util` as:

```scala
lazy val util = (project in file("util"))
  .dependsOn(core)
```

Root project
------------

The subproject at the root of the build is called a **root project**,
and often plays a special role in the build.
If a subproject is not defined at the root directory of the build,
sbt automatically creates a default one that aggregates all other subprojects in the build.

Task aggregation
----------------

Task aggregation means that running a task on the aggregate subproject will also run on the aggregated subprojects.

```scala
scalaVersion := "{{scala3_example_version}}"

lazy val root = rootProject
  .autoAggregate
  .settings(
    publish / skip := true
  )

lazy val util = (project in file("util"))

lazy val core = (project in file("core"))
```

In the above example, the root subproject aggregates `util` and `core`. When you type `compile` in the sbt shell, all tree subprojects are compiled in parallel.

Common settings
---------------

In sbt 2.x, bare settings that are written directly in `build.sbt` without `settings(...)` are **common settings** that are injected to all subprojects.

```scala
scalaVersion := "{{scala3_example_version}}"

lazy val core = (project in file("core"))

lazy val app = (project in file("app"))
  .dependsOn(core)
```

In the above, the `scalaVersion` setting is applied to the default root subproject, `core`, and `util`.

One exception to this rule is settings that are already scoped to a subproject.

```scala
scalaVersion := "{{scala3_example_version}}"

lazy val core = (project in file("core"))

lazy val app = (project in file("app"))
  .dependsOn(core)

// This is applied only to app
app / name := "app1"
```

We can take advantage of this exception to add some settings that only apply to the default root project as follows:

```scala
scalaVersion := "{{scala3_example_version}}"

lazy val core = (project in file("core"))

lazy val app = (project in file("app"))
  .dependsOn(core)

// These are applied only to root
LocalRootProject / name := "root"
LocalRootProject / publish / skip := true
```

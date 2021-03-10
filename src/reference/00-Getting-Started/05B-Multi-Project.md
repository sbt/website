---
out: Multi-Project.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Directories]: Directories.html
  [Organizing-Build]: Organizing-Build.html

Multi-project builds
--------------------

This page introduces multiple subprojects in a single build.

Please read the earlier pages in the Getting Started Guide first, in
particular you need to understand [build.sbt][Basic-Def] before reading
this page.

### Multiple subprojects

It can be useful to keep multiple related subprojects in a single build,
especially if they depend on one another and you tend to modify them
together.

Each subproject in a build has its own source directories, generates
its own jar file when you run package, and in general works like any
other project.

A project is defined by declaring a lazy val of type
[Project](../api/sbt/Project.html). For example, :

```scala
lazy val util = (project in file("util"))

lazy val core = (project in file("core"))
```

The name of the val is used as the subproject's ID, which
is used to refer to the subproject at the sbt shell.

Optionally the base directory may be omitted if it's the same as the name of the val.

```scala
lazy val util = project

lazy val core = project
```

<a name="ThisBuild"></a>
#### Build-wide settings

To factor out common settings across multiple subprojects,
define the settings scoped to `ThisBuild`.
`ThisBuild` acts as a special subproject name that you can use to define default
value for the build.
When you define one or more subprojects, and when the subproject does not define
`scalaVersion` key, it will look for `ThisBuild / scalaVersion`.

The limitation is that the right-hand side needs to be a pure value
or settings scoped to `Global` or `ThisBuild`,
and there are no default settings scoped to subprojects. (See [Scopes][Scopes])

```scala
ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "$example_scala_version$"

lazy val core = (project in file("core"))
  .settings(
    // other settings
  )

lazy val util = (project in file("util"))
  .settings(
    // other settings
  )
```

Now we can bump up `version` in one place, and it will be reflected
across subprojects when you reload the build.

#### Common settings

Another way to factor out common settings across multiple projects is to
create a sequence named `commonSettings` and call `settings` method
on each project.

```scala
lazy val commonSettings = Seq(
  target := { baseDirectory.value / "target2" }
)

lazy val core = (project in file("core"))
  .settings(
    commonSettings,
    // other settings
  )

lazy val util = (project in file("util"))
  .settings(
    commonSettings,
    // other settings
  )
```

### Dependencies

Projects in the build can be completely independent of one another, but
usually they will be related to one another by some kind of dependency.
There are two types of dependencies: aggregate and classpath.

#### Aggregation

Aggregation means that running a task on the aggregate project will also
run it on the aggregated projects. For example,

```scala
lazy val root = (project in file("."))
  .aggregate(util, core)

lazy val util = (project in file("util"))

lazy val core = (project in file("core"))
```

In the above example, the root project aggregates `util` and `core`. Start
up sbt with two subprojects as in the example, and try compile. You
should see that all three projects are compiled.

*In the project doing the aggregating*, the root project in this case,
you can control aggregation per-task. For example, to avoid aggregating
the `update` task:

```scala
lazy val root = (project in file("."))
  .aggregate(util, core)
  .settings(
    update / aggregate := false
  )

[...]
```

`update / aggregate` is the aggregate key scoped to the `update` task. (See
[scopes][Scopes].)

Note: aggregation will run the aggregated tasks in parallel and with no
defined ordering between them.

#### Classpath dependencies

A project may depend on code in another project. This is done by adding
a `dependsOn` method call. For example, if core needed util on its
classpath, you would define core as:

```scala
lazy val core = project.dependsOn(util)
```

Now code in `core` can use classes from `util`. This also creates an
ordering between the projects when compiling them; `util` must be updated
and compiled before core can be compiled.

To depend on multiple projects, use multiple arguments to `dependsOn`,
like `dependsOn(bar, baz)`.

##### Per-configuration classpath dependencies

`core dependsOn(util)` means that the `compile` configuration in `core` depends
on the `compile` configuration in `util`. You could write this explicitly as
`dependsOn(util % "compile->compile")`.

The `->` in `"compile->compile"` means "depends on" so `"test->compile"`
means the `test` configuration in `core` would depend on the `compile`
configuration in `util`.

Omitting the `->config` part implies `->compile`, so
`dependsOn(util % "test")` means that the `test` configuration in `core` depends
on the `Compile` configuration in `util`.

A useful declaration is `"test->test"` which means `test` depends on `test`.
This allows you to put utility code for testing in `util/src/test/scala`
and then use that code in `core/src/test/scala`, for example.

You can have multiple configurations for a dependency, separated by
semicolons. For example,
`dependsOn(util % "test->test;compile->compile")`.

### Inter-project dependencies

On extremely large projects with many files and many subprojects, sbt
can perform less optimally at continuously watching files that have
changed and use a lot of disk and system I/O.

sbt has `trackInternalDependencies` and `exportToInternal`
settings. These can be used to control whether to trigger compilation
of a dependent subprojects when you call `compile`. Both keys will
take one of three values: `TrackLevel.NoTracking`,
`TrackLevel.TrackIfMissing`, and `TrackLevel.TrackAlways`. By default
they are both set to `TrackLevel.TrackAlways`.

When `trackInternalDependencies` is set to
`TrackLevel.TrackIfMissing`, sbt will no longer try to compile
internal (inter-project) dependencies automatically, unless there are
no `*.class` files (or JAR file when `exportJars` is `true`) in the
output directory.

When the setting is set to `TrackLevel.NoTracking`, the compilation of
internal dependencies will be skipped. Note that the classpath will
still be appended, and dependency graph will still show them as
dependencies. The motivation is to save the I/O overhead of checking
for the changes on a build with many subprojects during
development. Here's how to set all subprojects to `TrackIfMissing`.

```scala
ThisBuild / trackInternalDependencies := TrackLevel.TrackIfMissing
ThisBuild / exportJars := true

lazy val root = (project in file("."))
  .aggregate(....)
```

The `exportToInternal` setting allows the dependee subprojects to opt
out of the internal tracking, which might be useful if you want to
track most subprojects except for a few. The intersection of the
`trackInternalDependencies` and `exportToInternal` settings will be
used to determine the actual track level. Here's an example to opt-out
one project:

```scala
lazy val dontTrackMe = (project in file("dontTrackMe"))
  .settings(
    exportToInternal := TrackLevel.NoTracking
  )
```

### Default root project

If a project is not defined for the root directory in the build, sbt
creates a default one that aggregates all other projects in the build.

Because project `hello-foo` is defined with `base = file("foo")`, it will be
contained in the subdirectory foo. Its sources could be directly under
`foo`, like `foo/Foo.scala`, or in `foo/src/main/scala`. The usual sbt
[directory structure][Directories] applies underneath `foo` with the
exception of build definition files.

### Navigating projects interactively

At the sbt interactive prompt, type `projects` to list your projects and
`project <projectname>` to select a current project. When you run a task
like `compile`, it runs on the current project. So you don't necessarily
have to compile the root project, you could compile only a subproject.

You can run a task in another project by explicitly specifying the
project ID, such as `subProjectID/compile`.

### Common code

The definitions in `.sbt` files are not visible in other `.sbt` files. In
order to share code between `.sbt` files, define one or more Scala files
in the `project/` directory of the build root.

See [organizing the build][Organizing-Build] for details.

### Appendix: Subproject build definition files

Any `.sbt` files in `foo`, say `foo/build.sbt`, will be merged with the build
definition for the entire build, but scoped to the `hello-foo` project.

If your whole project is in hello, try defining a different version
(`version := "0.6"`) in `hello/build.sbt`, `hello/foo/build.sbt`, and
`hello/bar/build.sbt`. Now `show version` at the sbt interactive prompt. You
should get something like this (with whatever versions you defined):

```
> show version
[info] hello-foo/*:version
[info]  0.7
[info] hello-bar/*:version
[info]  0.9
[info] hello/*:version
[info]  0.5
```

`hello-foo/*:version` was defined in `hello/foo/build.sbt`,
`hello-bar/*:version` was defined in `hello/bar/build.sbt`, and
`hello/*:version` was defined in `hello/build.sbt`. Remember the
[syntax for scoped keys][Scopes]. Each `version` key is scoped to a
project, based on the location of the `build.sbt`. But all three `build.sbt`
are part of the same build definition.

Style choices:

- Each subproject's settings can go into `*.sbt` files in the base directory of that project,
  while the root `build.sbt` declares only minimum project declarations in the form of `lazy val foo = (project in file("foo"))` without the settings.
- We recommend putting all project declarations and settings in the root `build.sbt` file
  in order to keep all build definition under a single file. However, it's up to you.

**Note**: You cannot have a project subdirectory or `project/*.scala` files in the
sub-projects. `foo/project/Build.scala` would be ignored.

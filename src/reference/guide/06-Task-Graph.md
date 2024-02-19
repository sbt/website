---
out: Task-Graph.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Make]: https://en.wikipedia.org/wiki/Make_(software)
  [Ant]: https://ant.apache.org/
  [Rake]: https://ruby.github.io/rake/

Task graph
----------

Continuing from [build definition][Basic-Def],
this page explains `build.sbt` definition in more detail.

Rather than thinking of `settings` as key-value pairs,
a better analogy would be to think of it as a _directed acyclic graph_ (DAG)
of tasks where the edges denote **happens-before**. Let's call this the _task graph_.

### Terminology

Let's review the key terms before we dive in.

- Setting/Task expression: entry inside `.settings(...)`.
- Key: Left hand side of a setting expression. It could be a `SettingKey[A]`, a `TaskKey[A]`, or an `InputKey[A]`.
- Setting: Defined by a setting expression with `SettingKey[A]`. The value is calculated once during load.
- Task: Defined by a task expression with `TaskKey[A]`. The value is calculated each time it is invoked.

### Declaring dependency to other tasks

In `build.sbt` DSL, we use `.value` method to express the dependency to
another task or setting. The value method is special and may only be
called in the argument to `:=` (or, `+=` or `++=`, which we'll see later).

As a first example, consider defining the `scalacOptions` that depends on
`update` and `clean` tasks. Here are the definitions of these keys (from [Keys](../api/sbt/Keys\$.html)).

**Note**: The values calculated below are nonsensical for `scalaOptions`,
and it's just for demonstration purpose only:

```scala
val scalacOptions = taskKey[Seq[String]]("Options for the Scala compiler.")
val update = taskKey[UpdateReport]("Resolves and optionally retrieves dependencies, producing a report.")
val clean = taskKey[Unit]("Deletes files produced by the build, such as generated sources, compiled classes, and task caches.")
```

Here's how we can rewire `scalacOptions`:

```scala
scalacOptions := {
  val ur = update.value  // update task happens-before scalacOptions
  val x = clean.value    // clean task happens-before scalacOptions
  // ---- scalacOptions begins here ----
  ur.allConfigurations.take(3)
}
```

`update.value` and `clean.value` declare task dependencies,
whereas `ur.allConfigurations.take(3)` is the body of the task.

`.value` is not a normal Scala method call. `build.sbt` DSL
uses a macro to lift these outside of the task body.
**Both `update` and `clean` tasks are completed
by the time task engine evaluates the opening `{` of `scalacOptions`
regardless of which line it appears in the body.**

See the following example:

```scala
ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "$example_scala_version$"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    scalacOptions := {
      val out = streams.value // streams task happens-before scalacOptions
      val log = out.log
      log.info("123")
      val ur = update.value   // update task happens-before scalacOptions
      log.info("456")
      ur.allConfigurations.take(3)
    }
  )
```

Next, from sbt shell type `scalacOptions`:

```
> scalacOptions
[info] Updating {file:/xxx/}root...
[info] Resolving jline#jline;2.14.1 ...
[info] Done updating.
[info] 123
[info] 456
[success] Total time: 0 s, completed Jan 2, 2017 10:38:24 PM
```

Even though `val ur = ...` appears in between `log.info("123")` and
`log.info("456")` the evaluation of `update` task happens before
either of them.

Here's another example:

```scala
ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "$example_scala_version$"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    scalacOptions := {
      val ur = update.value  // update task happens-before scalacOptions
      if (false) {
        val x = clean.value  // clean task happens-before scalacOptions
      }
      ur.allConfigurations.take(3)
    }
  )
```

Next, from sbt shell type `run` then `scalacOptions`:

```
> run
[info] Updating {file:/xxx/}root...
[info] Resolving jline#jline;2.14.1 ...
[info] Done updating.
[info] Compiling 1 Scala source to /Users/eugene/work/quick-test/task-graph/target/scala-2.12/classes...
[info] Running example.Hello
hello
[success] Total time: 0 s, completed Jan 2, 2017 10:45:19 PM
> scalacOptions
[info] Updating {file:/xxx/}root...
[info] Resolving jline#jline;2.14.1 ...
[info] Done updating.
[success] Total time: 0 s, completed Jan 2, 2017 10:45:23 PM
```

Now if you check for `target/scala-2.12/classes/`,
it won't exist because `clean` task has run even though it is inside
the `if (false)`.

Another important thing to note is that there's no guarantee
about the ordering of `update` and `clean` tasks.
They might run `update` then `clean`, `clean` then `update`,
or both in parallel.

### Inlining .value calls

As explained above, `.value` is a special method that is used to express
the dependency to other tasks and settings.
Until you're familiar with build.sbt, we recommend you
put all `.value` calls at the top of the task body.

However, as you get more comfortable, you might wish to inline the `.value` calls
because it could make the task/setting more concise, and you don't have to
come up with variable names.

We've inlined a few examples:

```scala
scalacOptions := {
  val x = clean.value
  update.value.allConfigurations.take(3)
}
```

Note whether `.value` calls are inlined, or placed anywhere in the task body,
they are still evaluated before entering the task body.

#### Inspecting the task

In the above example, `scalacOptions` has a *dependency* on
`update` and `clean` tasks.
If you place the above in `build.sbt` and
run the sbt interactive console, then type `inspect scalacOptions`, you should see
(in part):

```
> inspect scalacOptions
[info] Task: scala.collection.Seq[java.lang.String]
[info] Description:
[info]  Options for the Scala compiler.
....
[info] Dependencies:
[info]  *:clean
[info]  *:update
....
```

This is how sbt knows which tasks depend on which other tasks.

For example, if you `inspect tree compile` you'll see it depends on another key
`incCompileSetup`, which it in turn depends on
other keys like `dependencyClasspath`. Keep following the dependency chains and magic happens.

```
> inspect tree compile
[info] compile:compile = Task[sbt.inc.Analysis]
[info]   +-compile:incCompileSetup = Task[sbt.Compiler\$IncSetup]
[info]   | +-*/*:skip = Task[Boolean]
[info]   | +-compile:compileAnalysisFilename = Task[java.lang.String]
[info]   | | +-*/*:crossPaths = true
[info]   | | +-{.}/*:scalaBinaryVersion = 2.12
[info]   | |
[info]   | +-*/*:compilerCache = Task[xsbti.compile.GlobalsCache]
[info]   | +-*/*:definesClass = Task[scala.Function1[java.io.File, scala.Function1[java.lang.String, Boolean]]]
[info]   | +-compile:dependencyClasspath = Task[scala.collection.Seq[sbt.Attributed[java.io.File]]]
[info]   | | +-compile:dependencyClasspath::streams = Task[sbt.std.TaskStreams[sbt.Init\$ScopedKey[_ <: Any]]]
[info]   | | | +-*/*:streamsManager = Task[sbt.std.Streams[sbt.Init\$ScopedKey[_ <: Any]]]
[info]   | | |
[info]   | | +-compile:externalDependencyClasspath = Task[scala.collection.Seq[sbt.Attributed[java.io.File]]]
[info]   | | | +-compile:externalDependencyClasspath::streams = Task[sbt.std.TaskStreams[sbt.Init\$ScopedKey[_ <: Any]]]
[info]   | | | | +-*/*:streamsManager = Task[sbt.std.Streams[sbt.Init\$ScopedKey[_ <: Any]]]
[info]   | | | |
[info]   | | | +-compile:managedClasspath = Task[scala.collection.Seq[sbt.Attributed[java.io.File]]]
[info]   | | | | +-compile:classpathConfiguration = Task[sbt.Configuration]
[info]   | | | | | +-compile:configuration = compile
[info]   | | | | | +-*/*:internalConfigurationMap = <function1>
[info]   | | | | | +-*:update = Task[sbt.UpdateReport]
[info]   | | | | |
....
```

When you type `compile` sbt automatically performs an `update`, for example. It
Just Works because the values required as inputs to the `compile`
computation require sbt to do the `update` computation first.

In this way, all build dependencies in sbt are *automatic* rather than
explicitly declared. If you use a key's value in another computation,
then the computation depends on that key.

#### Defining a task that depends on other settings

`scalacOptions` is a task key.
Let's say it's been set to some values already, but you want to
filter out `"-Xfatal-warnings"` and `"-deprecation"` for non-2.12.

```scala
lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    organization := "com.example",
    scalaVersion := "$example_scala_version$",
    version := "0.1.0-SNAPSHOT",
    scalacOptions := List("-encoding", "utf8", "-Xfatal-warnings", "-deprecation", "-unchecked"),
    scalacOptions := {
      val old = scalacOptions.value
      scalaBinaryVersion.value match {
        case "2.12" => old
        case _      => old filterNot (Set("-Xfatal-warnings", "-deprecation").apply)
      }
    }
  )
```

Here's how it should look on the sbt shell:

```
> show scalacOptions
[info] * -encoding
[info] * utf8
[info] * -Xfatal-warnings
[info] * -deprecation
[info] * -unchecked
[success] Total time: 0 s, completed Jan 2, 2017 11:44:44 PM
> ++2.11.8!
[info] Forcing Scala version to 2.11.8 on all projects.
[info] Reapplying settings...
[info] Set current project to Hello (in build file:/xxx/)
> show scalacOptions
[info] * -encoding
[info] * utf8
[info] * -unchecked
[success] Total time: 0 s, completed Jan 2, 2017 11:44:51 PM
```

Next, take these two keys (from [Keys](../api/sbt/Keys\$.html)):

```scala
val scalacOptions = taskKey[Seq[String]]("Options for the Scala compiler.")
val checksums = settingKey[Seq[String]]("The list of checksums to generate and to verify for dependencies.")
```

**Note**: `scalacOptions` and `checksums` have nothing to do with each other.
They are just two keys with the same value type, where one is a task.

It is possible to compile a `build.sbt` that aliases `scalacOptions` to
`checksums`, but not the other way. For example, this is allowed:

```scala
// The scalacOptions task may be defined in terms of the checksums setting
scalacOptions := checksums.value
```

There is no way to go the *other* direction. That is, a setting key
can't depend on a task key. That's because a setting key is only
computed once on project load, so the task would not be re-run every
time, and tasks expect to re-run every time.

```scala
// Bad example: The checksums setting cannot be defined in terms of the scalacOptions task!
checksums := scalacOptions.value
```

#### Defining a setting that depends on other settings

In terms of the execution timing, we can think of the settings
as a special tasks that evaluate during loading time.

Consider defining the project organization to be the same as the project name.

```scala
// name our organization after our project (both are SettingKey[String])
organization := name.value
```

Here's a realistic example.
This rewires `Compile / scalaSource` key to a different directory
only when `scalaBinaryVersion` is `"2.11"`.

```scala
Compile / scalaSource := {
  val old = (Compile / scalaSource).value
  scalaBinaryVersion.value match {
    case "2.11" => baseDirectory.value / "src-2.11" / "main" / "scala"
    case _      => old
  }
}
```

### What's the point of the build.sbt DSL?

We use the `build.sbt` domain-specific language(DSL) to construct a DAG of settings and tasks.
The setting expressions encode settings, tasks and the dependencies among them.

This structure is common to [Make][Make] (1976), [Ant][Ant] (2000), and [Rake][Rake] (2003).

#### Intro to Make

The basic Makefile syntax looks like the following:

```
target: dependencies
[tab] system command1
[tab] system command2
```

Given a target (the default target is named `all`),

1. Make checks if the target's dependencies have been built, and builds any of the dependencies that hasn't been built yet.
2. Make runs the system commands in order.

Let's take a look at a `Makefile`:

```
CC=g++
CFLAGS=-Wall

all: hello

hello: main.o hello.o
    \$(CC) main.o hello.o -o hello

%.o: %.cpp
    \$(CC) \$(CFLAGS) -c \$< -o \$@
```

Running `make`, it will by default pick the target named `all`.
The target lists `hello` as its dependency, which hasn't been built yet, so Make will build `hello`.

Next, Make checks if the `hello` target's dependencies have been built yet.
`hello` lists two targets: `main.o` and `hello.o`.
Once those targets are created using the last pattern matching rule,
only then the system command is executed to link `main.o` and `hello.o` to `hello`.

If you're just running `make`, you can focus on what you want as the target,
and the exact timing and commands necessary to build the intermediate products are figured out by Make.
We can think of this as dependency-oriented programming, or flow-based programming.
Make is actually considered a hybrid system because while the DSL describes the task dependencies, the actions are delegated to system commands.

#### Rake

This hybridity is continued for Make successors such as Ant, Rake, and sbt.
Take a look at the basic syntax for Rakefile:

```ruby
task name: [:prereq1, :prereq2] do |t|
  # actions (may reference prereq as t.name etc)
end
```

The breakthrough made with Rake was that it used a programming language to
describe the actions instead of the system commands.

#### Benefits of hybrid flow-based programming

There are several motivation to organizing the build this way.

First is de-duplication. With flow-based programming, a task is executed only once even when it is depended by multiple tasks.
For example, even when multiple tasks along the task graph depend on `Compile / compile`,
the compilation will be executed exactly once.

Second is parallel processing. Using the task graph, the task engine can
schedule mutually non-dependent tasks in parallel.

Third is the separation of concern and the flexibility.
The task graph lets the build user wire the tasks together in different ways,
while sbt and plugins can provide various features such as compilation and
library dependency management as functions that can be reused.

### Summary

The core data structure of the build definition is a DAG of tasks,
where the edges denote happens-before relationships.
`build.sbt` is a DSL designed to express dependency-oriented programming,
or flow-based programming, similar to `Makefile` and `Rakefile`.

The key motivation for the flow-based programming is de-duplication,
parallel processing, and customizability.

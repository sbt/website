---
out: Task-Graph.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Make]: https://en.wikipedia.org/wiki/Make_(software)
  [Ant]: http://ant.apache.org/
  [Rake]: https://ruby.github.io/rake/

Task graph
----------

This page explains `build.sbt` definition in more detail.
It assumes you've read [.sbt build definition][Basic-Def] and
[scopes][Scopes].

### What's the point of the build.sbt DSL?

As we saw before, a [build definition][Basic-Def] consists of subprojects
with a map called `settings` describing the subproject.
There's more to the story.

What the `Setting` sequence encodes is tasks and the dependencies among them,
similar to [Make][Make] (1976), [Ant][Ant] (2000), and [Rake][Rake] (2003).

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

This hybridness is continues for Make successors such as Ant, Rake, and sbt.
Take a look at the basic syntax for Rakefile:

```ruby
task name: [:prereq1, :prereq2] do |t|
  # actions (may reference prereq as t.name etc)
end
```

The breakthough made with Rake was that it used a programming language to
describe the actions instead of the system commands.

#### Benefits of hybrid flow-based programming

Rather than thinking `settings` as a key-value map,
a better analogy would be to think of it as a DAG (directed acyclic graph)
of tasks where the edges denote *happens-before*. Let's call this the _task graph_.
There are several motivation to organizing the build this way.

First is de-duplication. With flow-based programming, a task is executed only once even when it is depended by multiple tasks.
For example, even when multiple tasks along the task graph depend on `compile in Compile`,
the compilation will be executed exactly once.

Second is parallel processing. Using the task graph, the task engine can
schedule mutually non-dependent tasks in parallel.

Third is the separation of concern and the flexibility.
The task graph lets the build user wire the tasks together in different ways,
while sbt and plugins can provide various features such as compilation and
library dependency management as functions that can be reused.

### Declaring dependency to other tasks

In `build.sbt` DSL, we use `.value` method to express the dependency to
another task or setting. The value method is special and may only be
called in the argument to `:=` (or, `+=` or `++=`, which we'll see later).

As a first example, consider defining the `scalacOption` that depends on
`update` and `clean` tasks. Here are the definitions of these keys (from [Keys](../sxr/sbt/Keys.scala.html)):

```scala
val scalacOptions = TaskKey[Seq[String]]("scalac-options", "Options for the Scala compiler.")
val update = TaskKey[UpdateReport]("update", "Resolves and optionally retrieves dependencies, producing a report.")
val clean = TaskKey[Unit]("clean", "Deletes files produced by the build, such as generated sources, compiled classes, and task caches.")
```

Here's how we can define `scalacOptions`:

```scala
scalacOptions := {
  val ur = update.value
  val x = clean.value
  ur.allConfigurations.take(3)
}
```

**Note**: The values calculated above are nonsensical for `scalaOptions`,
and it's just for demonstration purpose only.

The lines where `update.value` and `clean.value` are called
corresponds to the declaration of task dependencies,
whereas `ur.allConfigurations.take(3)` is the body of the task.

Recall from the previous section that the edges in the task graph
denote happens-before relationship like the dependencies in Makefile.
**This means that both `update` and `clean` tasks are completed
by the time task engine evaluates the opening `{` of `scalacOptions`.**

Another important thing to note is that there's no guarantee
about the ordering of `update` and `clean` tasks.
They might run `update` then `clean`, `clean` then `update`,
or both in parallel.

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

#### Declaring dependency to other settings

Next, take these two keys (from [Keys](../sxr/sbt/Keys.scala.html)):

```scala
val scalacOptions = taskKey[Seq[String]]("Options for the Scala compiler.")
val checksums = settingKey[Seq[String]]("The list of checksums to generate and to verify for dependencies.")
```

**Note**: `scalacOptions` and `checksums` have nothing to do with each other, they
are just two keys with the same value type, where one is a task.

It is possible to compile a `build.sbt` that aliases `scalacOptions` to
`checksums`, but not the other way. For example, this is allowed:

```scala
// The scalacOptions task may be defined in terms of the checksums setting
scalacOptions := {
  val cs = checksums.value
  cs
}
```

There is no way to go the *other* direction. That is, a setting key
can't depend on a task key. That's because a setting key is only
computed once on project load, so the task would not be re-run every
time, and tasks expect to re-run every time.

```scala
// The checksums setting may not be defined in terms of the scalacOptions task
checksums := {
  val so = scalacOptions.value
  so
}
```

#### Defining a setting that depends on other settings

In terms of the execution timing, we can think of the settings
as a special tasks that evaluate during the loading time.

Consider defining the project organization to be the same as the project name.

```scala
// name our organization after our project (both are SettingKey[String])
organization := {
  val n = name.value
  n
}
```

Or, set the name to the name of the project's directory:

```scala
// name is a Key[String], baseDirectory is a Key[File]
// name the project after the directory it's inside
name := {
  val base = baseDirectory.value
  base.getName
}
```

This transforms the value of `baseDirectory` using the standard `getName`
method of `java.io.File`.

Using multiple inputs is similar. For example,

```scala
name := {
  val old = name.value
  val o = organization.value
  val v = version.value
  s"project \$old from \$o version \$v"
}
```

This sets the name in terms of its previous value as well as the
organization and version settings.

### Summary

The core data structure of the build definition is a DAG of tasks,
where the edges denote happens-before relationships.
`build.sbt` is a DSL designed to express dependency-oriented programming,
or flow-based programming, similar to `Makefile` and `Rakefile`.

The key motivation for the flow-based programming is de-duplication,
parallel processing, and customizability.

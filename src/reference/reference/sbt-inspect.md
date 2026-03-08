
  [Basic-Def]: Basic-Def.html

sbt inspect
===========

Synopsis
--------

`sbt` `inspect` \[_subproject_ / \] \[ _config_ / \] _task_<br>
`sbt` `inspect actual` \[_subproject_ / \] \[ _config_ / \] _task_<br>
`sbt` `inspect tree` \[_subproject_ / \] \[ _config_ / \] _task_

Description
-----------

The `inspect` command provides a means to inspect the task and setting graph.
For instace, it can be used to determine which setting should be
modified to affect another task.

### Value, Description, and Provided By

The first piece of information provided by `inspect` is the type of a
task or the value and type of a setting.

For example,

```bash
$ sbt inspect libraryDependencies
[info] Setting: interface scala.collection.immutable.Seq =
  List(org.scala-lang:scala3-library:3.7.2,
       org.typelevel:toolkit:0.1.29,
       org.typelevel:toolkit-test:0.1.29:test)
[info] Description:
[info]  Declares managed dependencies.
[info] Provided by:
[info]  ProjectRef(uri("file:/tmp/aaa/"), "aaa") / libraryDependencies
....
```

The following section of output
is labeled "Provided by". This shows the actual scope where the setting
is defined.

This shows that `libraryDependencies` has been defined on the current
project (`ProjectRef(uri("file:/tmp/aaa/"), "aaa")`).

### Related Settings

The _Related_ section of `inspect` output lists all of the definitions
of a key. For example,

```bash
> inspect compile
...
[info] Related:
[info]  Test / compile
```

This shows that in addition to the requested `Compile / compile` task,
there is also a `Test / compile` task.

### Setting dependencies

Forward dependencies show the other settings (or tasks) used to define a
setting (or task). Reverse dependencies go the other direction, showing
what uses a given setting. `inspect` provides this information based on
either the requested dependencies or the actual dependencies. Requested
dependencies are those that a setting directly specifies. Actual
settings are what those dependencies get resolved to. This distinction
is explained in more detail in the following sections.

#### Requested Dependencies

As an example, we'll look at `console`:

```bash
$ sbt inspect console
...
[info] Dependencies:
[info]  Compile / console / initialCommands
[info]  Compile / console / compilers
[info]  Compile / state
[info]  Compile / console / cleanupCommands
[info]  Compile / console / taskTemporaryDirectory
[info]  Compile / console / scalaInstance
[info]  Compile / console / scalacOptions
[info]  Compile / console / fullClasspath
[info]  Compile / fileConverter
[info]  Compile / console / streams

...
```

This shows the inputs to the `console` task. We can see that it gets its
classpath and options from `Compile / console / fullClasspath` and
`Compile / console / scalacOptions`. The information provided by the `inspect`
command can thus assist in finding the right setting to change. The
convention for keys, like `console` and `fullClasspath`, is that the
Scala identifier is camel case, while the String representation is
lowercase and separated by dashes. The Scala identifier for a
configuration is uppercase to distinguish it from tasks like `compile`
and `test`. For example, we can infer from the previous example how to
add code to be run when the Scala interpreter starts up:

```scala
> set Compile / console / initialCommands := "import mypackage._"
> console
...
import mypackage._
...
```

`inspect` showed that `console` used the setting
`Compile / console / initialCommands`. Translating the `initialCommands`
string to the Scala identifier gives us `initialCommands`. `compile`
indicates that this is for the main sources. `console /` indicates that
the setting is specific to `console`. Because of this, we can set the
initial commands on the `console` task without affecting the
`consoleQuick` task, for example.

#### Actual Dependencies

`inspect actual <scoped-key>` shows the actual dependency used. This is
useful because delegation means that the dependency can come from a
scope other than the requested one. Using `inspect actual`, we see
exactly which scope is providing a value for a setting. Combining
`inspect actual` with plain `inspect`, we can see the range of scopes
that will affect a setting. Returning to the example in Requested
Dependencies,

```bash
$ sbt inspect actual console
...
[info] Dependencies:
[info]  Compile / console / streams
[info]  Global / taskTemporaryDirectory
[info]  scalaInstance
[info]  Compile / scalacOptions
[info]  Global / initialCommands
[info]  Global / cleanupCommands
[info]  Compile / fullClasspath
[info]  console / compilers
...
```

For `initialCommands`, we see that it comes from the global scope
(`Global`). Combining this with the relevant output from
`inspect console`:

```
Compile / console / initialCommands
```

we know that we can set `initialCommands` as generally as the global
scope, as specific as the current project's `console` task scope, or
anything in between. This means that we can, for example, set
`initialCommands` for the whole project and will affect `console`:

```scala
> set initialCommands := "import mypackage._"
...
```

The reason we might want to set it here this is that other console tasks
will use this value now. We can see which ones use our new setting by
looking at the reverse dependencies output of `inspect actual`:

```bash
$ sbt inspect actual Global/initialCommands
...
[info] Reverse dependencies:
[info]  Compile / console
[info]  consoleProject
[info]  Test / console
[info]  Test / consoleQuick
[info]  Compile / consoleQuick
...
```

We now know that by setting `initialCommands` on the whole project, we
affect all console tasks in all configurations in that project. If we
didn't want the initial commands to apply for `consoleProject`, which
doesn't have our project's classpath available, we could use the more
specific task axis:

```scala
> set console / initialCommands := "import mypackage._"
> set consoleQuick / initialCommands := "import mypackage._"`
```

or configuration axis:

```scala
> set Compile/　initialCommands := "import mypackage._"
> set Test / initialCommands := "import mypackage._"
```

The next part describes the Delegates section, which shows the chain of
delegation for scopes.

### Delegates

A setting has a key and a scope. A request for a key in a scope A may be
delegated to another scope if A doesn't define a value for the key. The
delegation chain is well-defined and is displayed in the Delegates
section of the `inspect` command. The Delegates section shows the order
in which scopes are searched when a value is not defined for the
requested key.

As an example, consider the initial commands for `console` again:

```bash
$ sbt inspect console/initialCommands
...
[info] Delegates:
[info]  console / initialCommands
[info]  initialCommands
[info]  ThisBuild / console / initialCommands
[info]  ThisBuild / initialCommands
[info]  Zero / console / initialCommands
[info]  Global / initialCommands
...
```

This means that if there is no value specifically for
`console/initialCommands`, the scopes listed under Delegates will be
searched in order until a defined value is found.

### Inspect tree

In addition to displaying immediate forward and reverse dependencies as
described in the previous section, the `inspect tree` command can display the
full dependency tree for a task or setting. For example,

```bash
$ sbt inspect tree console
[info] Compile / console = Task[void]
[info]   +-Global / cleanupCommands =
[info]   +-console / compilers = Task[class xsbti.compile.Compilers]
[info]   +-Compile / fullClasspath = Task[Seq[class sbt.internal.util.Attributed]]
[info]   +-Global / initialCommands =
[info]   +-scalaInstance = Task[class sbt.internal.inc.ScalaInstance]
[info]   +-Compile / scalacOptions = Task[Seq[class java.lang.String]]
[info]   +-Compile / console / streams = Task[interface sbt.std.TaskStreams]
[info]   | +-Global / streamsManager = Task[interface sbt.std.Streams]
[info]   |
[info]   +-Global / taskTemporaryDirectory = target/....
[info]   +-Global / fileConverter = sbt.internal.inc.MappedFileConverter@10095d95
[info]   +-Global / state = Task[class sbt.State]
[info]
[success] elapsed: 0 s
```

For each task, `inspect tree` show the type of the value generated by
the task. For a setting, the `toString` of the setting is displayed.

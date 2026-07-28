  [Basic-Def]: Basic-Def.html
  [cached-task]: cached-task.md
  [input-task]: input-task.md
  [command]: command.md
  [Parsing-Input]: Parsing-Input.html
  [tab-completion-parser]: tab-completion-parser.md

Task
====

Description
-----------

A task is a unit of work in an sbt build.
Tasks `compile` your code, run `test`s incrementally, `package` JAR files,
or do anything else a build needs to do.

An interesting aspect of tasks is that they enable parallel programming,
similar to `Future[A]` and `IO[A]`.
While `Future` and `IO` are often composed via `for` comprehension,
sbt provides _structured concurrency_ of tasks via the direct-style `build.sbt` DSL.

```scala
lazy val intTaskA = taskKey[Int]("")
lazy val intTaskB = taskKey[Int]("")
lazy val intTaskC = taskKey[Int]("")

intTaskA := 1
intTaskB := 2

// intTaskA and intTaskB will run in parallel
intTaskC := {
  val a = intTaskA.value
  val b = intTaskB.value
  a + b
}
```

### Def.Initialized[Task[A]]

Under the hood, all tasks including task keys are typed as `Def.Initialized[Task[A]]`.

1. `Def.Initialized[A]`. This is the same type as a setting, and
   used to construct the setting/task graph at load time.
   It is so named because it represents an initialized value of `A`.
2. `sbt.Task[A]`. This a datatype that represents repeatable asynchronous
   computation.
   This is the part that gets executed by sbt's task engine.

By nesting these together, a task can be read as a node in a setting/task graph
that produces an asynchronous computation of `A`.

### Settings vs tasks

Both settings and tasks produce values, but there is a major
difference between them:

-  Settings are evaluated at build load time. Tasks are executed on
   demand, often in response to the user invoking them from the shell.

### Features

There are several features of the task system:

1.  By integrating with the settings system, tasks can be added,
    removed, and modified as easily as settings.
2.  Tasks produce values. Other tasks can access a task's value by
    calling `value` on it within a task definition.
3.  Task values are cached automatically based on its inputs.
    See [cached task][cached-task] for details.
4.  [Input tasks][input-task] use
    [parser combinators][tab-completion-parser] to define the syntax for their
    arguments. This allows flexible syntax and tab-completions in the
    same way as [commands][command].
5.  Dynamically changing the structure of the task graph is possible.
    Tasks can be injected into the execution graph based on the result
    of another task.
6.  There are ways to handle task failure, similar to
    `try/catch/finally`.
7.  Each task has access to its own Logger that by default persists the
    logging for that task at a more verbose level than is initially
    printed to the screen.

These features are discussed in detail in the following sections.

Defining a task
---------------

### Hello world example

~~~admonish example title='build.sbt'
```scala
lazy val hello = taskKey[Unit]("prints 'hello world'")

hello := println("hello world!")
```
~~~

Run `sbt hello` from command line to invoke the task.
Run `sbt tasks` to see this task listed.

### Define the key

To declare a new task, define a lazy val of type `TaskKey`:

```scala
lazy val sampleTask = taskKey[Int]("A sample task.")
```

The name of the `val` is used when referring to the task in `build.sbt`
and at the command line. The string passed to the `taskKey` method is a
description of the task. The type parameter passed to `taskKey` (here,
`Int`) is the type of value produced by the task.

We'll define a couple of other keys for the examples:

~~~admonish example title='build.sbt'
```scala
lazy val intTask = taskKey[Int]("An int task")
lazy val stringTask = taskKey[String]("A string task")
```
~~~

The examples themselves are valid entries in a `build.sbt`.

### Implement the task

There are three main parts to implementing a task once its key is
defined:

1.  Determine the settings and other tasks needed by the task. They are
    the task's inputs.
2.  Define the code that implements the task in terms of these inputs.
3.  Determine the scope the task will go in.

These parts are then combined just like the parts of a setting are
combined.

#### Defining a basic task

A task is defined using `:=`

~~~admonish example title='build.sbt'
```scala
lazy val intTask = taskKey[Int]("An int task")
lazy val stringTask = taskKey[String]("A string task")

intTask := 1 + 2

stringTask := Def.uncached {
  sys.props("user.name")
}
```
~~~

#### Tasks with inputs

Tasks with other tasks or settings as inputs are also defined using
`:=`. The values of the inputs are referenced by the `value` method.
This method is a special syntax and can only be called when defining a
task, such as in the argument to `:=`. The following defines a task that
adds one to the value produced by `intTask` and returns the result.

```scala
sampleTask := intTask.value + 1
```

Multiple settings are handled similarly:

```scala
stringTask := {
  val x = sampleTask.value
  val i = intTask.value
  s"sample: $x, int: $i"
}
```

#### Task scope

As with settings, tasks can be defined in a specific scope. For example,
there are separate `compile` tasks for the `Compile` and `Test` scopes.

In the following example, `Test/sampleTask` uses the result of
`Compile/intTask`:

```scala
Test / sampleTask := (Compile / intTask).value * 3
```

~~~admonish note title='On operator precedence'
As a reminder, infix method precedence is determined by the name of the method.

1.  Assignment methods have the lowest precedence. These are methods
    with names ending in `=`, except for `!=`, `<=`, `>=`, and names that
    start with `=`.
2.  Methods starting with a letter have the next highest precedence.
3.  Methods with names that start with a symbol and aren't included in
    1 have the highest precedence. (This category is divided further
    according to the specific character it starts with. See the Scala
    specification for details.)

Therefore, the previous example is equivalent to the following:

```scala
(Test / sampleTask).:=((Compile / intTask).value * 3)
```
~~~

### Separating implementations

The implementation of a task can be separated from the binding. For
example, a basic separate definition looks like:

```scala
// Define a new, standalone task implemention
lazy val intTaskImpl: Initialize[Task[Int]] =
   Def.cachedTask { sampleTask.value - 3 }

// Bind the implementation to a specific key
intTask := intTaskImpl.value
```

Note that whenever `.value` is used, it must be within a task
definition, such as within `Def.task` above or as an argument to `:=`.

### Modifying an existing task

In the general case, modify a task by declaring the previous task as an
input.

```scala
// initial definition
intTask := 3

// overriding definition that references the previous definition
intTask := intTask.value + 1
```

Completely override a task by not declaring the previous task as an
input. Each of the definitions in the following example completely
overrides the previous one. That is, when `intTask` is run, it will only
print `#3`.

```scala
intTask := {
  println("#1")
  3
}

intTask := {
  println("#2")
  5
}

intTask :=  {
  println("#3")
  sampleTask.value - 3
}
```

Error handling
--------------

### Errors in a task

To express an error state in a task, throw an exception.
For example:

~~~admonish example title='build.sbt'
```scala
lazy val intTask = taskKey[Int]("An int task")

intTask := sys.error("failed")
```
~~~

sbt's task engine will catch and handling the error without crashing sbt:

```bash
sbt:aaa> intTask
[error] stack trace is suppressed; run last intTask for the full output
[error] (intTask) boom
[error] elapsed time: 0 s, cache 0%, 1 error
```

The stacktrace can be shown using `last intTask`.

### `result`

The `result` method creates a new task that returns the full `Result[A1]`
value for the original task. `Result` has
the same structure as `Either[Incomplete, A1]` for a task result of type
`A1`. That is, it has two subtypes:

- `Result.Inc`, which wraps `Incomplete` in case of failure
- `Result.Value`, which wraps a task's result in case of success.

Thus, the task created by `result` executes whether or not the original
task succeeds or fails.

~~~admonish example title='build.sbt'
```scala
lazy val intTask = taskKey[Int]("An int task")

intTask := sys.error("boom")

intTask := Def.uncached {
  intTask.result.value match
    case Result.Inc(inc: Incomplete) =>
      println("Ignoring failure: " + inc)
      3
    case Result.Value(v) =>
      println("Using successful result: " + v)
      v
}
```
~~~

This rewires the original `intTask` definition so that if the original
task fails, the exception is printed and the constant `3` is returned.
If it succeeds, the value is printed and returned.

### `failure`

The `failure` method creates a new task that returns the `Incomplete`
value when the original task fails to complete normally. If the original
task succeeds, the new task fails.

~~~admonish example title='build.sbt'
```scala
lazy val intTask = taskKey[Int]("An int task")

intTask := sys.error("boom")

intTask := Def.uncached {
  println("Ignoring failure: " + intTask.failure.value)
  3
}
```
~~~

This rewires the `intTask` so that the original exception is printed
and the constant `3` is returned.

Advanced task operations
------------------------

### Streams: Per-task logging

Per-task loggers are part of a more general system for task-specific
data called Streams.

To use Streams, get the value of the `streams` task.
A logger can be obtained by the `log` method:

```scala
foo := {
  val s = streams.value
  s.log.debug("Saying hi...")
  s.log.info("Hello!")
}
```

You can scope logging settings by the specific task's scope:

```scala
foo / logLevel := Level.Debug
foo / traceLevel := 5
```

To obtain the last logging output from a task, use the `last` command:

```
$ last foo
[debug] Saying hi...
[info] Hello!
```

The verbosity with which logging is persisted is controlled using the
`persistLogLevel` and `persistTraceLevel` settings. The `last` command
displays what was logged according to these levels. The levels do not
affect already logged information.

### Conditional task

When a task consists of an `if`-expression at the top-level, a conditional task is automatically created:

```scala
bar := {
  if number.value < 0 then negAction.value
  else if number.value == 0 then zeroAction.value
  else posAction.value
}
```

Unlike the regular (Applicative) task composition, conditional tasks delays the evaluation of then-clause and else-clause as naturally expected of an `if`-expression. This is already possible with `Def.taskDyn { ... }`, but unlike dynamic tasks, conditional task works with `inspect` command.

<a name="dynamic"></a>
### Dynamic Computations with `Def.taskDyn`

It can be useful to use the result of a task to determine the next tasks
to evaluate. This is done using `Def.taskDyn`.

The result of `taskDyn`
is called a dynamic task because it introduces dependencies at runtime.
The `taskDyn` method supports the same syntax as `Def.task` and `:=`
except that you return a task instead of a plain value.

For example,

~~~admonish example title='build.sbt'
```scala
lazy val stringTask = taskKey[String]("A string task")
lazy val intTask = taskKey[Int]("An int task")
lazy val foo = taskKey[Int]("foo")

val dynamic = Def.taskDyn {
  // decide what to evaluate based on the value of `stringTask`
  if stringTask.value == "dev" then
    // create the dev-mode task: this is only evaluated if the
    //   value of stringTask is "dev"
    Def.task {
      3
    }
  else
    // create the production task: only evaluated if the value
    //    of the stringTask is not "dev"
    Def.task {
      intTask.value + 5
    }
}

stringTask := "test"
intTask := 1

foo := Def.uncached {
  val num = dynamic.value
  println(s"number selected was $num")
  num
}
```
~~~

The only static dependency of `foo` is `stringTask`. The dependency
on `intTask` is only introduced in non-dev mode.

~~~admonish warning
A dynamic task cannot refer to itself or a circular dependency will
result. In the example above, there would be a circular dependency if
the code passed to taskDyn referenced `foo`.
~~~

### Using `Def.sequential`

`Def.sequential` is a helper function to define a semi-sequential task.
This is similar to the dynamic task, but easier to define.
To demonstrate the sequential task, let's create a custom task called `compilecheck` that runs `Compile / compile` and then `Compile / scalastyle` task.

```scala
lazy val compilecheck = taskKey[Unit]("compile and then scalastyle")

lazy val root = (project in file("."))
  .settings(
    Compile / compilecheck := Def.sequential(
      Compile / compile,
      (Compile / scalastyle).toTask("")
    ).value
  )
```

For a completely sequential execution, use [commands][command] instead.

<a name="multiple-scopes"></a>
### Getting values from multiple scopes

The general form of an expression that gets values from multiple scopes
is:

```scala
<setting-or-task>.all(<scope-filter>).value
```

~~~admonish warning
Make sure to assign the `ScopeFilter` as a `val`! This is an
implementation detail requirement of the `.all` macro.
~~~

The `all` method is implicitly added to tasks and settings. It accepts a
`ScopeFilter` that will select the `Scopes`. The result has type
`Seq[A]`, where `A` is the key's underlying type.

A common scenario is getting the sources for all subprojects for
processing all at once, such as passing them to scaladoc. The task that
we want to obtain values for is `sources` and we want to get the values
in all non-root projects and in the `Compile` configuration. This looks
like:

```scala
lazy val core = project

lazy val util = project

val filter = ScopeFilter(inProjects(core, util), inConfigurations(Compile))

lazy val root = rootProject
  .settings(
    sources := {
      // each sources definition is of type Seq[File],
      //   giving us a Seq[Seq[File]] that we then flatten to Seq[File]
      val allSources: Seq[Seq[File]] = sources.all(filter).value
      allSources.flatten
    }
  )
```

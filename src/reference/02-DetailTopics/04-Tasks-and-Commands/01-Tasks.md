---
out: Tasks.html
---

  [Basic-Def]: Basic-Def.html
  [Full-Def]: Full-Def.html
  [Input-Tasks]: Input-Tasks.html
  [Commands]: Commands.html
  [Parsing-Input]: Parsing-Input.html

Tasks
-----

Tasks and settings are introduced in the
[getting started guide][Basic-Def], which you may wish
to read first. This page has additional details and background and is
intended more as a reference.

### Introduction

Both settings and tasks produce values, but there are two major
differences between them:

1.  Settings are evaluated at project load time. Tasks are executed on
    demand, often in response to a command from the user.
2.  At the beginning of project loading, settings and their dependencies
    are fixed. Tasks can introduce new tasks during execution, however.

### Features

There are several features of the task system:

1.  By integrating with the settings system, tasks can be added,
    removed, and modified as easily and flexibly as settings.
2.  [Input Tasks][Input-Tasks] use
    [parser combinators][Parsing-Input] to define the syntax for their
    arguments. This allows flexible syntax and tab-completions in the
    same way as [Commands][Commands].
3.  Tasks produce values. Other tasks can access a task's value by
    calling `value` on it within a task definition.
4.  Dynamically changing the structure of the task graph is possible.
    Tasks can be injected into the execution graph based on the result
    of another task.
5.  There are ways to handle task failure, similar to
    `try/catch/finally`.
6.  Each task has access to its own Logger that by default persists the
    logging for that task at a more verbose level than is initially
    printed to the screen.

These features are discussed in detail in the following sections.

### Defining a Task

#### Hello World example (sbt)

`build.sbt`:

```scala
lazy val hello = taskKey[Unit]("Prints 'Hello World'")

hello := println("hello world!")
```

Run "sbt hello" from command line to invoke the task. Run "sbt tasks" to
see this task listed.

#### Define the key

To declare a new task, define a lazy val of type `TaskKey`:

```scala
lazy val sampleTask = taskKey[Int]("A sample task.")
```

The name of the `val` is used when referring to the task in Scala code
and at the command line. The string passed to the `taskKey` method is a
description of the task. The type parameter passed to `taskKey` (here,
`Int`) is the type of value produced by the task.

We'll define a couple of other keys for the examples:

```scala
lazy val intTask = taskKey[Int]("An int task")
lazy val stringTask = taskKey[String]("A string task")
```

The examples themselves are valid entries in a `build.sbt` or can be
provided as part of a sequence to `Project.settings` (see
[.scala build definition][Full-Def]).

#### Implement the task

There are three main parts to implementing a task once its key is
defined:

1.  Determine the settings and other tasks needed by the task. They are
    the task's inputs.
2.  Define the code that implements the task in terms of these inputs.
3.  Determine the scope the task will go in.

These parts are then combined just like the parts of a setting are
combined.

##### Defining a basic task

A task is defined using `:=`

```scala
intTask := 1 + 2

stringTask := System.getProperty("user.name")

sampleTask := {
   val sum = 1 + 2
   println("sum: " + sum)
   sum
}
```

As mentioned in the introduction, a task is evaluated on demand. Each
time `sampleTask` is invoked, for example, it will print the sum. If the
username changes between runs, `stringTask` will take different values
in those separate runs. (Within a run, each task is evaluated at most
once.) In contrast, settings are evaluated once on project load and are
fixed until the next reload.

##### Tasks with inputs

Tasks with other tasks or settings as inputs are also defined using
`:=`. The values of the inputs are referenced by the `value` method.
This method is special syntax and can only be called when defining a
task, such as in the argument to `:=`. The following defines a task that
adds one to the value produced by `intTask` and returns the result.

```scala
sampleTask := intTask.value + 1
```

Multiple settings are handled similarly:

```scala
stringTask := "Sample: " + sampleTask.value + ", int: " + intTask.value
```

##### Task Scope

As with settings, tasks can be defined in a specific scope. For example,
there are separate `compile` tasks for the `compile` and `test` scopes.
The scope of a task is defined the same as for a setting. In the
following example, `Test/sampleTask` uses the result of
`Compile/intTask`.

```scala
Test / sampleTask := (Compile / intTask).value * 3
```

##### On precedence

As a reminder, infix method precedence is by the name of the method and
postfix methods have lower precedence than infix methods.

1.  Assignment methods have the lowest precedence. These are methods
    with names ending in `=`, except for `!=`, `<=`, `>=`, and names that
    start with `=`.
2.  Methods starting with a letter have the next highest precedence.
3.  Methods with names that start with a symbol and aren't included in
    1. have the highest precedence. (This category is divided further
    according to the specific character it starts with. See the Scala
    specification for details.)

Therefore, the previous example is equivalent to the following:


```scala
(Test / sampleTask).:=( (Compile / intTask).value * 3 )
```

Additionally, the braces in the following are necessary:

```scala
helloTask := { "echo Hello" ! }
```

Without them, Scala interprets the line as
`( helloTask.:=("echo Hello") ).!` instead of the desired
`helloTask.:=( "echo Hello".! )`.

#### Separating implementations

The implementation of a task can be separated from the binding. For
example, a basic separate definition looks like:

```scala
// Define a new, standalone task implemention
lazy val intTaskImpl: Initialize[Task[Int]] =
   Def.task { sampleTask.value - 3 }

// Bind the implementation to a specific key
intTask := intTaskImpl.value
```

Note that whenever `.value` is used, it must be within a task
definition, such as within `Def.task` above or as an argument to `:=`.

#### Modifying an Existing Task

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

<a name="multiple-scopes"></a>

### Getting values from multiple scopes

#### Introduction

The general form of an expression that gets values from multiple scopes
is:

```scala
<setting-or-task>.all(<scope-filter>).value
```

**NOTE!** Make sure to assign the `ScopeFilter` as a `val`!  This is an
implementation detail requirement of the `.all` macro.

The `all` method is implicitly added to tasks and settings. It accepts a
`ScopeFilter` that will select the `Scopes`. The result has type
`Seq[T]`, where `T` is the key's underlying type.

#### Example

A common scenario is getting the sources for all subprojects for
processing all at once, such as passing them to scaladoc. The task that
we want to obtain values for is `sources` and we want to get the values
in all non-root projects and in the `Compile` configuration. This looks
like:

```scala
lazy val core = project

lazy val util = project

val filter = ScopeFilter( inProjects(core, util), inConfigurations(Compile) )

lazy val root = project.settings(
   sources := {
      // each sources definition is of type Seq[File],
      //   giving us a Seq[Seq[File]] that we then flatten to Seq[File]
      val allSources: Seq[Seq[File]] = sources.all(filter).value
      allSources.flatten
   }
)
```

The next section describes various ways to construct a ScopeFilter.

#### ScopeFilter

A basic `ScopeFilter` is constructed by the `ScopeFilter.apply` method.
This method makes a `ScopeFilter` from filters on the parts of a
`Scope`: a `ProjectFilter`, `ConfigurationFilter`, and `TaskFilter`. The
simplest case is explicitly specifying the values for the parts:

```scala
val filter: ScopeFilter =
   ScopeFilter(
      inProjects( core, util ),
      inConfigurations( Compile, Test )
   )
```

##### Unspecified filters

If the task filter is not specified, as in the example above, the
default is to select scopes without a specific task (global). Similarly,
an unspecified configuration filter will select scopes in the global
configuration. The project filter should usually be explicit, but if
left unspecified, the current project context will be used.

##### More on filter construction

The example showed the basic methods `inProjects` and
`inConfigurations`. This section describes all methods for constructing
a `ProjectFilter`, `ConfigurationFilter`, or `TaskFilter`. These methods
can be organized into four groups:

-   Explicit member list (`inProjects`, `inConfigurations`, `inTasks`)
-   Global value (`inGlobalProject`, `inGlobalConfiguration`,
    `inGlobalTask`)
-   Default filter (`inAnyProject`, `inAnyConfiguration`, `inAnyTask`)
-   Project relationships (`inAggregates`, `inDependencies`)

See the [API documentation](../api/sbt/ScopeFilter\$\$Make.html) for
details.

##### Combining ScopeFilters

`ScopeFilters` may be combined with the `&&`, `||`, `--`, and `-`
methods:

- `a && b` Selects scopes that match both a and b
- `a || b` Selects scopes that match either a or b
- `a -- b` Selects scopes that match a but not b
- `-b` Selects scopes that do not match b

For example, the following selects the scope for the `Compile` and
`Test` configurations of the `core` project and the global configuration
of the `util` project:

```scala
val filter: ScopeFilter =
   ScopeFilter( inProjects(core), inConfigurations(Compile, Test)) ||
   ScopeFilter( inProjects(util), inGlobalConfiguration )
```

#### More operations

The `all` method applies to both settings (values of type
`Initialize[T]`) and tasks (values of type `Initialize[Task[T]]`). It
returns a setting or task that provides a `Seq[T]`, as shown in this
table:

<table class="table table-striped">
  <tr>
    <th>Target</th>
    <th>Result</th>
  </tr>

  <tr>
    <td><tt>Initialize[T] </tt></td>
    <td><tt>Initialize[Seq[T]]</tt></td>
  </tr>

  <tr>
    <td><tt>Initialize[Task[T]]</tt></td>
    <td><tt>Initialize[Task[Seq[T]]]</tt></td>
  </tr>
</table>

This means that the `all` method can be combined with methods that
construct tasks and settings.

##### Missing values

Some scopes might not define a setting or task. The `?` and `??` methods
can help in this case. They are both defined on settings and tasks and
indicate what to do when a key is undefined.

<table class="table table-striped">
  <tr>
    <td><tt>?</tt></td>
    <td><tt>On a setting or task with underlying type T, this accepts no
    arguments and returns a setting or task (respectively) of type
    Option[T]. The result is None if the setting/task is undefined and
    Some[T] with the value if it is.</tt></td>
  </tr>

  <tr>
    <td><tt>??</tt></td>
    <td><tt>On a setting or task with underlying type T, this accepts an
    argument of type T and uses this argument if the setting/task is
    undefined.</tt></td>
  </tr>
</table>

The following contrived example sets the maximum errors to be the
maximum of all aggregates of the current project.

```scala
// select the transitive aggregates for this project, but not the project itself
val filter: ScopeFilter =
   ScopeFilter( inAggregates(ThisProject, includeRoot=false) )

maxErrors := {
   // get the configured maximum errors in each selected scope,
   // using 0 if not defined in a scope
   val allVersions: Seq[Int] =
      (maxErrors ?? 0).all(filter).value
   allVersions.max
}
```

##### Multiple values from multiple scopes

The target of `all` is any task or setting, including anonymous ones.
This means it is possible to get multiple values at once without
defining a new task or setting in each scope. A common use case is to
pair each value obtained with the project, configuration, or full scope
it came from.

- `resolvedScoped`: Provides the full enclosing ScopedKey (which is a Scope +
    `AttributeKey[_]`)
- `thisProject`: Provides the Project associated with this scope (undefined at the
    global and build levels)
-  `thisProjectRef`: Provides the ProjectRef for the context (undefined at the global and
    build levels)
- `configuration`: Provides the Configuration for the context (undefined for the global
    configuration)

For example, the following defines a task that prints non-Compile
configurations that define sbt plugins. This might be used to identify
an incorrectly configured build (or not, since this is a fairly
contrived example):

```scala
// Select all configurations in the current project except for Compile
lazy val filter: ScopeFilter = ScopeFilter(
   inProjects(ThisProject),
   inAnyConfiguration -- inConfigurations(Compile)
)

// Define a task that provides the name of the current configuration
//   and the set of sbt plugins defined in the configuration
lazy val pluginsWithConfig: Initialize[Task[ (String, Set[String]) ]] =
   Def.task {
      ( configuration.value.name, definedSbtPlugins.value )
   }

checkPluginsTask := {
   val oddPlugins: Seq[(String, Set[String])] =
      pluginsWithConfig.all(filter).value
   // Print each configuration that defines sbt plugins
   for( (config, plugins) <- oddPlugins if plugins.nonEmpty )
      println(s"\$config defines sbt plugins: \${plugins.mkString(", ")}")
}
```

### Advanced Task Operations

The examples in this section use the task keys defined in the previous
section.

#### Streams: Per-task logging

Per-task loggers are part of a more general system for task-specific
data called Streams. This allows controlling the verbosity of stack
traces and logging individually for tasks as well as recalling the last
logging for a task. Tasks also have access to their own persisted binary
or text data.

To use Streams, get the value of the `streams` task. This is a special
task that provides an instance of
[TaskStreams](../api/sbt/std/TaskStreams.html) for the defining
task. This type provides access to named binary and text streams, named
loggers, and a default logger. The default
[Logger](../api/sbt/util/Logger.html), which is the most commonly used
aspect, is obtained by the `log` method:

```scala
myTask := {
  val s: TaskStreams = streams.value
  s.log.debug("Saying hi...")
  s.log.info("Hello!")
}
```

You can scope logging settings by the specific task's scope:

```scala
myTask / logLevel := Level.Debug

myTask / traceLevel := 5
```

To obtain the last logging output from a task, use the `last` command:

```
\$ last myTask
[debug] Saying hi...
[info] Hello!
```

The verbosity with which logging is persisted is controlled using the
`persistLogLevel` and `persistTraceLevel` settings. The `last` command
displays what was logged according to these levels. The levels do not
affect already logged information.

### Conditional task

(Requires sbt 1.4.0+)

When `Def.task { ... }` consists of an `if`-expression at the top-level, a conditional task (or Selective task) is automatically created:

```scala
bar := {
  if (number.value < 0) negAction.value
  else if (number.value == 0) zeroAction.value
  else posAction.value
}
```

Unlike the regular (Applicative) task composition, conditional tasks delays the evaluation of then-clause and else-clause as naturally expected of an `if`-expression. This is already possible with `Def.taskDyn { ... }`, but unlike dynamic tasks, conditional task works with `inspect` command.

### Dynamic Computations with `Def.taskDyn`

It can be useful to use the result of a task to determine the next tasks
to evaluate. This is done using `Def.taskDyn`. The result of `taskDyn`
is called a dynamic task because it introduces dependencies at runtime.
The `taskDyn` method supports the same syntax as `Def.task` and `:=`
except that you return a task instead of a plain value.

For example,

```scala
val dynamic = Def.taskDyn {
  // decide what to evaluate based on the value of `stringTask`
  if(stringTask.value == "dev")
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

myTask := {
  val num = dynamic.value
  println(s"Number selected was \$num")
}
```

The only static dependency of `myTask` is `stringTask`. The dependency
on `intTask` is only introduced in non-dev mode.

> **Note**: A dynamic task cannot refer to itself or a circular dependency will
> result. In the example above, there would be a circular dependency if
> the code passed to taskDyn referenced myTask.


### Using Def.sequential

sbt 0.13.8 added `Def.sequential` function to run tasks under semi-sequential semantics.
This is similar to the dynamic task, but easier to define.
To demonstrate the sequential task, let's create a custom task called `compilecheck` that runs `Compile / compile` and then `Compile / scalastyle` task added by [scalastyle-sbt-plugin](http://www.scalastyle.org/sbt.html).

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

To call this task type in `compilecheck` from the shell. If the compilation fails, `compilecheck` would stop the execution.

```
root> compilecheck
[info] Compiling 1 Scala source to /Users/x/proj/target/scala-2.10/classes...
[error] /Users/x/proj/src/main/scala/Foo.scala:3: Unmatched closing brace '}' ignored here
[error] }
[error] ^
[error] one error found
[error] (compile:compileIncremental) Compilation failed
```

### Handling Failure

This section discusses the `failure`, `result`, and `andFinally`
methods, which are used to handle failure of other tasks.

#### `failure`

The `failure` method creates a new task that returns the `Incomplete`
value when the original task fails to complete normally. If the original
task succeeds, the new task fails.
[Incomplete](../api/sbt/Incomplete.html) is an exception with
information about any tasks that caused the failure and any underlying
exceptions thrown during task execution.

For example:

```scala
intTask := sys.error("Failed.")

intTask := {
   println("Ignoring failure: " + intTask.failure.value)
   3
}
```

This overrides the `intTask` so that the original exception is printed
and the constant `3` is returned.

`failure` does not prevent other tasks that depend on the target from
failing. Consider the following example:

```scala
intTask := if(shouldSucceed) 5 else sys.error("Failed.")

// Return 3 if intTask fails. If intTask succeeds, this task will fail.
aTask := intTask.failure.value - 2

// A new task that increments the result of intTask.
bTask := intTask.value + 1

cTask := aTask.value + bTask.value
```

The following table lists the results of each task depending on the
initially invoked task:

<table class="table table-striped">
  <thead>
    <tr>
      <th>invoked task</th>
      <th>intTask result</th>
      <th>aTask result</th>
      <th>bTask result</th>
      <th>cTask result</th>
      <th>overall result</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>intTask</td>
      <td>failure</td>
      <td>not run</td>
      <td>not run</td>
      <td>not run</td>
      <td>failure</td>
    </tr>
    <tr>
      <td>aTask</td>
      <td>failure</td>
      <td>success</td>
      <td>not run</td>
      <td>not run</td>
      <td>success</td>
    </tr>
    <tr>
      <td>bTask</td>
      <td>failure</td>
      <td>not run</td>
      <td>failure</td>
      <td>not run</td>
      <td>failure</td>
    </tr>
    <tr>
      <td>cTask</td>
      <td>failure</td>
      <td>success</td>
      <td>failure</td>
      <td>failure</td>
      <td>failure</td>
    </tr>
    <tr>
      <td>intTask</td>
      <td>success</td>
      <td>not run</td>
      <td>not run</td>
      <td>not run</td>
      <td>success</td>
    </tr>
    <tr>
      <td>aTask</td>
      <td>success</td>
      <td>failure</td>
      <td>not run</td>
      <td>not run</td>
      <td>failure</td>
    </tr>
    <tr>
      <td>bTask</td>
      <td>success</td>
      <td>not run</td>
      <td>success</td>
      <td>not run</td>
      <td>success</td>
    </tr>
    <tr>
      <td>cTask</td>
      <td>success</td>
      <td>failure</td>
      <td>success</td>
      <td>failure</td>
      <td>failure</td>
    </tr>
  </tbody>
</table>

The overall result is always the same as the root task (the directly
invoked task). A `failure` turns a success into a failure, and a failure
into an `Incomplete`. A normal task definition fails when any of its
inputs fail and computes its value otherwise.

#### `result`

The `result` method creates a new task that returns the full `Result[T]`
value for the original task. [Result](../api/sbt/Result.html) has
the same structure as `Either[Incomplete, T]` for a task result of type
`T`. That is, it has two subtypes:

-   `Inc`, which wraps `Incomplete` in case of failure
-   `Value`, which wraps a task's result in case of success.

Thus, the task created by `result` executes whether or not the original
task succeeds or fails.

For example:

```scala
intTask := sys.error("Failed.")

intTask := {
   intTask.result.value match {
      case Inc(inc: Incomplete) =>
         println("Ignoring failure: " + inc)
         3
      case Value(v) =>
         println("Using successful result: " + v)
         v
   }
}
```

This overrides the original `intTask` definition so that if the original
task fails, the exception is printed and the constant `3` is returned.
If it succeeds, the value is printed and returned.

##### andFinally

The `andFinally` method defines a new task that runs the original task
and evaluates a side effect regardless of whether the original task
succeeded. The result of the task is the result of the original task.
For example:

```scala
intTask := sys.error("I didn't succeed.")

lazy val intTaskImpl = intTask andFinally { println("andFinally") }

intTask := intTaskImpl.value
```

This modifies the original `intTask` to always print "andFinally" even
if the task fails.

Note that `andFinally` constructs a new task. This means that the new
task has to be invoked in order for the extra block to run. This is
important when calling andFinally on another task instead of overriding
a task like in the previous example. For example, consider this code:

```scala
intTask := sys.error("I didn't succeed.")

lazy val intTaskImpl = intTask andFinally { println("andFinally") }

otherIntTask := intTaskImpl.value
```

If `intTask` is run directly, `otherIntTask` is never involved in
execution. This case is similar to the following plain Scala code:

```scala
def intTask(): Int =
  sys.error("I didn't succeed.")

def otherIntTask(): Int =
  try { intTask() }
  finally { println("finally") }

intTask()
```

It is obvious here that calling intTask() will never result in "finally"
being printed.

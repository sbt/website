---
out: Build-State.html
---

  [Commands]: Commands.html

State and actions
-----------------

[State](../api/sbt/State\$.html) is the entry point to all
available information in sbt. The key methods are:

-   `definedCommands: Seq[Command]` returns all registered Command
    definitions
-   `remainingCommands: List[Exec]` returns the remaining commands to
    be run
-   `attributes: AttributeMap` contains generic data.

The action part of a command performs work and transforms `State`. The
following sections discuss `State => State` transformations. As
mentioned previously, a command will typically handle a parsed value as
well: `(State, T) => State`.

### Command-related data

A Command can modify the currently registered commands or the commands
to be executed. This is done in the action part by transforming the
(immutable) State provided to the command. A function that registers
additional power commands might look like:

```scala
val powerCommands: Seq[Command] = ...

val addPower: State => State =
  (state: State) =>
    state.copy(definedCommands =
      (state.definedCommands ++ powerCommands).distinct
    )
```

This takes the current commands, appends new commands, and drops
duplicates. Alternatively, State has a convenience method for doing the
above:

```scala
val addPower2 = (state: State) => state ++ powerCommands
```

Some examples of functions that modify the remaining commands to
execute:

```scala
val appendCommand: State => State =
  (state: State) =>
    state.copy(remainingCommands = state.remainingCommands :+ "cleanup")

val insertCommand: State => State =
  (state: State) =>
    state.copy(remainingCommands = "next-command" +: state.remainingCommands)
```

The first adds a command that will run after all currently specified
commands run. The second inserts a command that will run next. The
remaining commands will run after the inserted command completes.

To indicate that a command has failed and execution should not continue,
return `state.fail`.

```scala
(state: State) => {
  val success: Boolean = ...
  if(success) state else state.fail
}
```

### Project-related data

Project-related information is stored in `attributes`. Typically,
commands won't access this directly but will instead use a convenience
method to extract the most useful information:

```scala
val state: State
val extracted: Extracted = Project.extract(state)
import extracted._
```

[Extracted](../api/sbt/Extracted.html) provides:

-   Access to the current build and project (`currentRef`)
-   Access to initialized project setting data (`structure.data`)
-   Access to session `Setting`s and the original, permanent settings
    from .sbt and .scala files (session.append and session.original,
    respectively)
-   Access to the current [Eval](../api/sbt/compiler/Eval.html)
    instance for evaluating Scala expressions in the build context.

### Project data

All project data is stored in `structure.data`, which is of type
`sbt.Settings[Scope]`. Typically, one gets information of type `T` in
the following way:

```scala
val key: SettingKey[T]
val scope: Scope
val value: Option[T] = key in scope get structure.data
```

Here, a `SettingKey[T]` is typically obtained from
[Keys](../api/sbt/Keys\$.html) and is the same type that is used to
define settings in `.sbt` files, for example.
[Scope](../api/sbt/Scope.html) selects the scope the key is
obtained for. There are convenience overloads of `in` that can be used
to specify only the required scope axes. See
[Structure.scala](https://github.com/sbt/sbt/blob/develop/main-settings/src/main/scala/sbt/Structure.scala) for where `in`
and other parts of the settings interface are defined. Some examples:

```scala
import Keys._
val extracted: Extracted
import extracted._

// get name of current project
val nameOpt: Option[String] = (currentRef / name).get(structure.data)

// get the package options for the `Test/packageSrc` task or Nil if none are defined
val pkgOpts: Seq[PackageOption] = (currentRef / Test / packageSrc / packageOptions).get(structure.data).getOrElse(Nil)
```

[BuildStructure](../api/sbt/internal/BuildStructure.html) contains
information about build and project relationships. Key members are:

```scala
units: Map[URI, LoadedBuildUnit]
root: URI
```

A `URI` identifies a build and `root` identifies the initial build
loaded. [LoadedBuildUnit](../api/sbt/internal/LoadedBuildUnit.html)
provides information about a single build. The key members of
`LoadedBuildUnit` are:

```scala
// Defines the base directory for the build
localBase: File

// maps the project ID to the Project definition
defined: Map[String, ResolvedProject]
```

[ResolvedProject](../api/sbt/ResolvedProject.html) has the same
information as the `Project` used in a `project/Build.scala` except that
[ProjectReferences](../api/sbt/ProjectReference.html) are resolved
to `ProjectRef`s.

### Classpaths

Classpaths in sbt are of type `Seq[Attributed[File]]`. This allows
tagging arbitrary information to classpath entries. sbt currently uses
this to associate an `Analysis` with an entry. This is how it manages
the information needed for multi-project incremental recompilation. It
also associates the ModuleID and Artifact with managed entries (those
obtained by dependency management). When you only want the underlying
`Seq[File]`, use `files`:

```scala
val attributedClasspath: Seq[Attribute[File]] = ...
val classpath: Seq[File] = attributedClasspath.files
```

### Running tasks

It can be useful to run a specific project task from a
[command][Commands] (*not from another task*) and get its result. For
example, an IDE-related command might want to get the classpath from a
project or a task might analyze the results of a compilation. The
relevant method is `Project.runTask`, which has the following
signature:

```scala
def runTask[T](taskKey: ScopedKey[Task[T]], state: State,
  checkCycles: Boolean = false): Option[(State, Result[T])]
```

For example,

```scala
val eval: State => State = (state: State) => {

    // This selects the main 'compile' task for the current project.
    //   The value produced by 'compile' is of type inc.Analysis,
    //   which contains information about the compiled code.
    val taskKey = Compile / Keys.compile

    // Evaluate the task
    // None if the key is not defined
    // Some(Inc) if the task does not complete successfully (Inc for incomplete)
    // Some(Value(v)) with the resulting value
    val result: Option[(State, Result[inc.Analysis])] = Project.runTask(taskKey, state)
    // handle the result
    result match
    {
        case None => // Key wasn't defined.
        case Some((newState, Inc(inc))) => // error detail, inc is of type Incomplete, use Incomplete.show(inc.tpe) to get an error message
        case Some((newState, Value(v))) => // do something with v: inc.Analysis
    }
}
```

For getting the test classpath of a specific project, use this key:

```scala
val projectRef: ProjectRef = ...
val taskKey: Task[Seq[Attributed[File]]] =
  (projectRef / Test / Keys.fullClasspath)
```

### Using State in a task

To access the current State from a task, use the `state` task as an
input. For example,

```scala
myTask := ... state.value ...
```

### Updating State in a task

It is also possible to update the sbt state in a task. To do this, the
task must return type `StateTransform`. The state will be transformed upon
completion of task evaluation. The `StateTransform` is constructed with
a function from `State => State` that accepts the previous value of the `State`
and generates a new state. For example:

```scala
import complete.DefaultParsers._
val counter = AttributeKey[Int]("counter")
val setCounter = inputKey[StateTransform]("Set the value of the counter attribute")
setCounter := {
  val count = (Space ~> IntBasic).parsed
  StateTransform(_.put(counter, count))
}
```

creates the input task `setCounter` that sets the counter attribute to some
value.

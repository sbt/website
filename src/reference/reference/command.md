
  [tab-completion-parser]: ./tab-completion-parser.md
  [Build-State]: Build-State.html
  [command-basics]: ../concepts/command-basics.md

Command
=======

This page covers commands in detail. See [Command basics][command-basics] for a general explanation.

## Description

A _command_ is a system-level building block of sbt, often used to capture user interactions. At the command level, there is little support for subprojects and parallel processing since those are implemented in the `act` command.

Plugin authors should try to solve their problem using settings, tasks, and input tasks first. Several notable exceptions are:

- Extending the user experience of sbt itself
- Providing sequential processing, for example for `release` command

### Introduction

There are three main aspects to commands:

1.  The syntax used by the user to invoke the command, including:
    -   Tab completion for the syntax
    -   The parser to turn input into an appropriate data structure
2.  The action to perform using the parsed data structure. This action
    transforms the build `State`.
3.  Help provided to the user

In sbt, the syntax part, including tab completion, is specified with
parser combinators. If you are familiar with the parser combinators in
Scala's standard library, these are very similar.
See the
[Tab-completion parser][tab-completion-parser] page for how to
use the parser combinators.

State provides access to the build state,
such as all registered commands, the remaining commands to execute,
and all project-related information.

<!--
See [States and Actions][Build-State] for details on
State.
-->

Finally, basic help information may be provided that is used by the
`help` command to display command help.

## Defining a Command

A command combines a function `State => Parser[T]` with an action
`(State, T) => State`. The reason for `State => Parser[T]` and not
simply `Parser[T]` is that often the current `State` is used to build
the parser. For example, the currently loaded projects (provided by
`State`) determine valid completions for the `project` command. Examples
for the general and specific cases are shown in the following sections.

See [Command.scala](https://github.com/sbt/sbt/blob/develop/main-command/src/main/scala/sbt/Command.scala) for the source
API details for constructing commands.

### General commands

General command construction looks like:

```scala
val action: (State, A) => State = ...
val parser: State => Parser[A] = ...
val command: Command = Command("name")(parser)(action)
```

### No-argument commands

There is a convenience method for constructing commands that do not
accept any arguments.

```scala
val action: State => State = ...
val command: Command = Command.command("name")(action)
```

### Single-argument command

There is a convenience method for constructing commands that accept a
single argument with arbitrary content.

```scala
// accepts the state and the single argument
val action: (State, String) => State = ...
val command: Command = Command.single("name")(action)
```

### Multi-argument command

There is a convenience method for constructing commands that accept
multiple arguments separated by spaces.

```scala
val action: (State, Seq[String]) => State = ...

// <arg> is the suggestion printed for tab completion on an argument
val command: Command = Command.args("name", "<arg>")(action)
```

## Full Example

The following example is a sample build that adds
commands to a project. To try it out:

1.  Create `build.sbt` and `project/CommandExample.scala`.
2.  Run sbt on the project.
3.  Try out the `hello`, `helloAll`, `failIfTrue`, `color`, and
    printState commands.
4.  Use tab-completion and the code below as guidance.

Here's `build.sbt`:

~~~admonish example title="build.sbt"

```scala
{{#include ../../sbt-test/ref/command-example/build.sbt}}
```
~~~

Here's `project/CommandExample.scala`:

~~~admonish example title="project/CommandExample.scala"

```scala
{{#include ../../sbt-test/ref/command-example/project/CommandExample.scala}}
```
~~~

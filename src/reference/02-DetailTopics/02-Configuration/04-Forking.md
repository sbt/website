---
out: Forking.html
---

  [Running-Project-Code]: Running-Project-Code.html
  [Testing]: Testing.html

Forking
-------

By default, the `run` task runs in the same JVM as sbt. Forking is
required under [certain circumstances][Running-Project-Code], however.
Or, you might want to fork Java processes when implementing new tasks.

By default, a forked process uses the same Java and Scala versions being
used for the build and the working directory and JVM options of the
current process. This page discusses how to enable and configure forking
for both `run` and `test` tasks. Each kind of task may be configured
separately by scoping the relevant keys as explained below.

### Enable forking

The `fork` setting controls whether forking is enabled (true) or not
(false). It can be set in the `run` scope to only fork `run` commands or
in the `test` scope to only fork `test` commands.

To fork all test tasks (`test`, `testOnly`, and `testQuick`) and run
tasks (`run`, `runMain`, `Test / run`, and `Test / runMain`),

```scala
fork := true
```

To only fork `Compile / run` and `Compile / runMain`:

```scala
Compile / run / fork := true
```

To only fork `Test / run` and `Test / runMain`:

```scala
Test / run / fork := true
```

_Note:_ `run` and `runMain` share the same configuration and cannot be configured separately.

To enable forking all `test` tasks only, set `fork` to `true` in the
`Test` scope:

```scala
Test / fork := true
```

See [Testing][Testing] for more control over how tests are assigned to JVMs and
what options to pass to each group.

### Change working directory

To change the working directory when forked, set `Compile / run / baseDirectory`
or `Test / baseDirectory`:

```scala
// sets the working directory for all `run`-like tasks
run / baseDirectory := file("/path/to/working/directory/")

// sets the working directory for `run` and `runMain` only
Compile / run / baseDirectory := file("/path/to/working/directory/")

// sets the working directory for `Test / run` and `Test / runMain` only
Test / run / baseDirectory := file("/path/to/working/directory/")

// sets the working directory for `test`, `testQuick`, and `testOnly`
Test / baseDirectory := file("/path/to/working/directory/")
```

### Forked JVM options

To specify options to be provided to the forked JVM, set `javaOptions`:

```scala
run / javaOptions += "-Xmx8G"
```

or specify the configuration to affect only the main or test `run`
tasks:

```scala
Test / run / javaOptions += "-Xmx8G"
```

or only affect the `test` tasks:

```scala
Test / javaOptions += "-Xmx8G"
```

### Java Home

Select the Java installation to use by setting the `javaHome` directory:

```scala
javaHome := Some(file("/path/to/jre/"))
```

Note that if this is set globally, it also sets the Java installation
used to compile Java sources. You can restrict it to running only by
setting it in the `run` scope:

```scala
run / javaHome := Some(file("/path/to/jre/"))
```

As with the other settings, you can specify the configuration to affect
only the main or test `run` tasks or just the `test` tasks.

### Configuring output

By default, forked output is sent to the Logger, with standard output
logged at the `Info` level and standard error at the `Error` level. This
can be configured with the `outputStrategy` setting, which is of type
[OutputStrategy](../api/sbt/OutputStrategy.html).

```scala
// send output to the build's standard output and error
outputStrategy := Some(StdoutOutput)

// send output to the provided OutputStream `someStream`
outputStrategy := Some(CustomOutput(someStream: OutputStream))

// send output to the provided Logger `log` (unbuffered)
outputStrategy := Some(LoggedOutput(log: Logger))

// send output to the provided Logger `log` after the process terminates
outputStrategy := Some(BufferedOutput(log: Logger))
```

As with other settings, this can be configured individually for main or
test `run` tasks or for `test` tasks.

### Configuring Input

By default, the standard input of the sbt process is not forwarded to
the forked process. To enable this, configure the `connectInput`
setting:

```scala
run / connectInput := true
```

### Direct Usage

To fork a new Java process, use the
[Fork API](../api/sbt/Fork\$.html). The values of interest are
`Fork.java`, `Fork.javac`, `Fork.scala`, and `Fork.scalac`. These are of
type [Fork](../api/sbt/Fork.html) and provide `apply` and `fork`
methods. For example, to fork a new Java process, :

```scala
val options = ForkOptions(...)
val arguments: Seq[String] = ...
val mainClass: String = ...
val exitCode: Int = Fork.java(options, mainClass +: arguments)
```

[ForkOptions](../api/sbt/ForkOptions.html) defines the Java
installation to use, the working directory, environment variables, and
more. For example, :

```scala
val cwd: File = ...
val javaDir: File = ...
val options = ForkOptions(
   envVars = Map("KEY" -> "value"),
   workingDirectory = Some(cwd),
   javaHome = Some(javaDir)
)
```

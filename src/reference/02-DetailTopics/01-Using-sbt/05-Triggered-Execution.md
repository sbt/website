---
out: Triggered-Execution.html
---

[Full-Def]: Full-Def.html

Triggered Execution
-------------------

sbt provides the ability to monitor the input files for a particular task
and repeat the task when changes to those files occur.

Some example usages are described below:

### Compile

A common use-case is continuous compilation. The following commands will make
sbt watch for source changes in the Test and Compile (default) configurations
respectively and re-run the compile command.

```
> ~ Test / compile

> ~ compile
```
Note that because `Test / compile` depends on `Compile / compile`,
source changes in the main source directory will trigger recompilation
of the test sources.

### Testing

Triggered execution is often used when developing in a test driven development
(TDD) style. The following command will monitor changes to both the main and
test source sources for the build and re-run only the tests that reference
classes that have been re-compiled since the last test run.

```
> ~ testQuick
```

It is also possible to re-run only a particular test if its dependencies
have changed.

```
> ~ testQuick foo.BarTest
```

It is possible to always re-run a test when source changes are
detected regardless of whether the test depends on any of the updated
source files.

```
> ~ testOnly foo.BarTest
```

To run all of the tests in the project when any sources change, use

```
> ~test
```

### Running Multiple Commands

sbt supports watching multiple, semicolon separated, commands. For example, the
following command will monitor for source file changes and run `clean` and
`test`:

```
> ~ clean; test
```

### Build sources

If the build is configured to automatically reload when build source changes
are made by setting `Global / onChangedBuildSource := ReloadOnSourceChanges`,
then sbt will monitor the build sources (i.e. `*.sbt` and `*.{java,scala}`
files in the `project` directory). When build source changes are detected,
the build will be reloaded and sbt will re-enter triggered execution mode
when the reload completes.

The following snippet can be added as a [global setting](../api/sbt/Global-Settings.html) to `~/.sbt/1.0/config.sbt` to enable `ReloadOnSourceChanges` for all sbt 1.3+ builds without breaking earlier versions:

```
Def.settings {
  try {
    val value = Class.forName("sbt.nio.Keys\$ReloadOnSourceChanges\$").getDeclaredField("MODULE\$").get(null)
    val clazz = Class.forName("sbt.nio.Keys\$WatchBuildSourceOption")
    val manifest = new scala.reflect.Manifest[AnyRef]{ def runtimeClass = clazz }
    Seq(
      Global / SettingKey[AnyRef]("onChangedBuildSource")(manifest, sbt.util.NoJsonWriter()) := value
    )
  } catch {
    case e: Throwable =>
      Nil
  }
}
```

### Clearing the screen

sbt can clear the console screen before it evaluates the task or after it
triggers an event. To configure sbt to clear the screen after an event is
triggered add

```
ThisBuild / watchTriggeredMessage := Watch.clearScreenOnTrigger
```
to the build settings. To clear the screen before running the task, add

```
ThisBuild  / watchBeforeCommand := Watch.clearScreen
```
to the build settings.

### Configuration

The behavior of triggered execution can be configured via a number of settings.

- `watchTriggers: Seq[Glob]` adds search queries for files that should task
trigger evaluation but that the task does not directly depend on. For
example, if the project build.sbt file contains `foo / watchTriggers +=
baseDirectory.value.toGlob / "*.txt"`, then any modifications to files
ending with the `txt` extension will cause the `foo` command to trigger
when in triggered execution mode.

- `watchTriggeredMessage: (Int, Path, Seq[String]) => Option[String]`
sets the message that is displayed when a file modification triggers a
new build. Its input parameters are the current watch iteration count,
the file that triggered the build and the command(s) that are going to
be run. By default, it prints a message indicating what file triggered
the build and what commands its going to run. No message is printed when
the function returns `None`. To clear the screen before printing the
message, just add `Watch.clearScreen()` inside of the task definition.
This will ensure that the screen is cleared and that the message, if
any is defined, will be printed after the screen clearing.

- `watchInputOptions: Seq[Watch.InputOption]` allows the build to
override the default watch options. For example, to add the ability to
reload the build by typing the 'l' key, add
`ThisBuild / watchInputOptions += Watch.InputOption('l', "reload",
Watch.Reload)` to the `build.sbt` file. When using the default
`watchStartMessage`, this will also add the option to the list displayed
by the '?' option.

- `watchBeforeCommand: () => Unit` provides a callback to run before
evaluating the task.  It can be used to clear the console screen by
adding `ThisBuild / watchBeforeCommand := Watch.clearScreen` to the
project build.sbt file. By default it is no-op.

- `watchLogLevel` sets the logging level of the file monitoring system.
This can be useful if the triggered execution is not being evaluated
when source files or modified or if is unexpectedly triggering due to
modifications to files that should not be monitored.

- `watchInputParser: Parser[Watch.Action]` changes how the monitor
handles input events. For example, setting `watchInputParser := 'l' ^^^
Watch.Reload | '\r' ^^^ new Watch.Run("")` will make it so that
typing the 'l' key will reload the build and typing a newline will
return to the shell. By default this is automatically derived from the
`watchInputOptions`.

- `watchStartMessage: (Int, ProjectRef, Seq[String]) => Option[String]`
sets the banner that is printed while the watch process is waiting for
file or input events. The inputs are the iteration count, the current
project and the commands to run. The default message includes
instructions for terminating the watch or displaying all available
options. This banner is only displayed if `watchOnIteration` logs the
result of `watchStartMessage`.

- `watchOnIteration: (Int, ProjectRef, Seq[String]) => Watch.Action` a
function that is evaluated before waiting for source or input events. It
can be used to terminate the watch early if, for example, a certain
number of iterations have been reached. By default, it just logs the
result of `watchStartMessage`.

- `watchForceTriggerOnAnyChange: Boolean` configures whether or not the
contents of a source file must change in order to trigger a build. The default
value is false.

- `watchPersistFileStamps: Boolean` toggles whether or not sbt will
persist the file hashes computed for source files across multiple task
evaluation runs. This can improve performance for projects with many
source files. Because the file hashes are cached, it is possible for the
evaluated task to read an invalid hash if many source files are being
concurrently modified. The default value is false.

- `watchAntiEntropy: FiniteDuration` controls the time that must elapse
before a build is re-triggered by the same file that previously
triggered the build. This is intended to prevent spurious builds that
can occur when a file is modified in short bursts. The default value is 500ms.

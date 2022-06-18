---
out: Howto-Logging.html
---

Configure and use logging
-------------------------

<a name="last"></a>

### View the logging output of the previously executed command

When a command is run, more detailed logging output is sent to a file
than to the screen (by default). This output can be recalled for the
command just executed by running `last`.

For example, the output of `run` when the sources are up to date is:

```
> run
[info] Running A
Hi!
[success] Total time: 0 s, completed Feb 25, 2012 1:00:00 PM
```

The details of this execution can be recalled by running `last`:

```
> last
[debug] Running task... Cancelable: false, max worker threads: 4, check cycles: false
[debug]
[debug] Initial source changes:
[debug]     removed:Set()
[debug]     added: Set()
[debug]     modified: Set()
[debug] Removed products: Set()
[debug] Modified external sources: Set()
[debug] Modified binary dependencies: Set()
[debug] Initial directly invalidated sources: Set()
[debug]
[debug] Sources indirectly invalidated by:
[debug]     product: Set()
[debug]     binary dep: Set()
[debug]     external source: Set()
[debug] Initially invalidated: Set()
[debug] Copy resource mappings:
[debug]
[info] Running A
[debug] Starting sandboxed run...
[debug] Waiting for threads to exit or System.exit to be called.
[debug]   Classpath:
[debug]     /tmp/e/target/scala-2.9.2/classes
[debug]     /tmp/e/.sbt/0.12.0/boot/scala-2.9.2/lib/scala-library.jar
[debug] Waiting for thread runMain to exit
[debug]     Thread runMain exited.
[debug] Interrupting remaining threads (should be all daemons).
[debug] Sandboxed run complete..
[debug] Exited with code 0
[success] Total time: 0 s, completed Jan 1, 2012 1:00:00 PM
```

Configuration of the logging level for the console and for the backing
file are described in following sections.

<a name="tasklast"></a>

### View the previous logging output of a specific task

When a task is run, more detailed logging output is sent to a file than
to the screen (by default). This output can be recalled for a specific
task by running `last <task>`. For example, the first time `compile` is
run, output might look like:

```
> compile
[info] Updating {file:/.../demo/}example...
[info] Resolving org.scala-lang#scala-library;2.9.2 ...
[info] Done updating.
[info] Compiling 1 Scala source to .../demo/target/scala-2.9.2/classes...
[success] Total time: 0 s, completed Jun 1, 2012 1:11:11 PM
```

The output indicates that both dependency resolution and compilation
were performed. The detailed output of each of these may be recalled
individually. For example,

```
> last compile
[debug]
[debug] Initial source changes:
[debug]     removed:Set()
[debug]     added: Set(/home/mark/tmp/a/b/A.scala)
[debug]     modified: Set()
...
```

and:

```
> last update
[info] Updating {file:/.../demo/}example...
[debug] post 1.3 ivy file: using exact as default matcher
[debug] :: resolving dependencies :: example#example_2.9.2;0.1-SNAPSHOT
[debug]     confs: [compile, runtime, test, provided, optional, compile-internal, runtime-internal, test-internal, plugin, sources, docs, pom]
[debug]     validate = true
[debug]     refresh = false
[debug] resolving dependencies for configuration 'compile'
...
```

<a name="printwarnings"></a>

### Show warnings from the previous compilation

The Scala compiler does not print the full details of warnings by
default. Compiling code that uses the deprecated `error` method from
Predef might generate the following output:

```
> compile
[info] Compiling 1 Scala source to <...>/classes...
[warn] there were 1 deprecation warnings; re-run with -deprecation for details
[warn] one warning found
```

The details aren't provided, so it is necessary to add `-deprecation` to
the options passed to the compiler (`scalacOptions`) and recompile. An
alternative when using Scala 2.10 and later is to run `printWarnings`.
This task will display all warnings from the previous compilation. For
example,

```
> printWarnings
[warn] A.scala:2: method error in object Predef is deprecated: Use sys.error(message) instead
[warn]  def x = error("Failed.")
[warn]          ^
```

<a name="level"></a>

### Change the logging level globally

The quickest way to change logging levels is by using the `error`,
`warn`, `info`, or `debug` commands. These set the default logging level
for commands and tasks. For example,

```
> warn
```

will by default show only warnings and errors. To set the logging level
before any commands are executed on startup, use `--` before the logging
level. For example,

```
\$ sbt --warn
> compile
[warn] there were 2 feature warning(s); re-run with -feature for details
[warn] one warning found
[success] Total time: 4 s, completed ...
>
```

The logging level can be overridden at a finer granularity, which is
described next.

<a name="tasklevel"></a>

### Change the logging level for a specific task, configuration, or project

The amount of logging is controlled by the `logLevel` setting, which
takes values from the `Level` enumeration. Valid values are `Error`,
`Warn`, `Info`, and `Debug` in order of increasing verbosity. The
logging level may be configured globally, as described in the previous
section, or it may be applied to a specific project, configuration, or
task. For example, to change the logging level for compilation to only
show warnings and errors:

```scala
> set Compile / compile / logLevel := Level.Warn
```

To enable debug logging for all tasks in the current project,

```scala
> set logLevel := Level.Warn
```

A common scenario is that after running a task, you notice that you need
more information than was shown by default. A `logLevel` based solution
typically requires changing the logging level and running a task again.
However, there are two cases where this is unnecessary. First, warnings
from a previous compilation may be displayed using `printWarnings` for
the main sources or `Test/printWarnings` for test sources. Second,
output from the previous execution is available either for a single task
or for in its entirety. See the section on
[printWarnings](#printwarnings) and the sections on
[previous output](#last).

<a name="trace"></a>

### Configure printing of stack traces

By default, sbt hides the stack trace of most exceptions thrown during
execution. It prints a message that indicates how to display the
exception. However, you may want to show more of stack traces by
default.

The setting to configure is `traceLevel`, which is a setting with an Int
value. When `traceLevel` is set to a negative value, no stack traces are
shown. When it is zero, the stack trace is displayed up to the first sbt
stack frame. When positive, the stack trace is shown up to that many
stack frames.

For example, the following configures sbt to show stack traces up to the
first sbt frame:

```scala
> set every traceLevel := 0
```

The `every` part means to override the setting in all scopes. To change
the trace printing behavior for a single project, configuration, or
task, scope `traceLevel` appropriately:

```scala
> set Test / traceLevel := 5
> set update / traceLevel := 0
> set ThisProject / traceLevel := -1
```

<a name="nobuffer"></a>

### Print the output of tests immediately instead of buffering

By default, sbt buffers the logging output of a test until the whole
class finishes. This is so that output does not get mixed up when
executing in parallel. To disable buffering, set the `logBuffered`
setting to false:

```scala
logBuffered := false
```

<a name="custom"></a>

### Add a custom logger


The setting `extraLoggers` can be used to add custom loggers. Internally, sbt
makes use of the [log4j2 library](https://logging.apache.org/log4j/2.x/), so a custom
logger should implement [`org.apache.logging.log4j.core.Appender`](https://logging.apache.org/log4j/2.x/log4j-core/apidocs/org/apache/logging/log4j/core/Appender.html),
usually by extending [`AbstractAppender`](https://logging.apache.org/log4j/2.x/log4j-core/apidocs/org/apache/logging/log4j/core/appender/AbstractAppender.html).

`extraLoggers` is a function
`ScopedKey[_] => Seq[Appender]`. This means that it can provide
different logging based on the task that requests the logger.

```scala
extraLoggers := {
  val currentFunction = extraLoggers.value
    (key: ScopedKey[_]) => {
        myCustomLogger(key) +: currentFunction(key)
    }
}
```

Here, we take the current function `currentFunction` for the setting and
provide a new function. The new function prepends our custom logger to
the ones provided by the old function.

An `Appender` in log4j2 appends a [`LogEvent`](https://logging.apache.org/log4j/2.x/log4j-core/apidocs/org/apache/logging/log4j/core/LogEvent.html),
whose core internally is a [`Message`](https://logging.apache.org/log4j/2.x/log4j-api/apidocs/org/apache/logging/log4j/message/Message.html). There can
be many types of Message, but sbt generates events containing instances of [`ObjectMessage`](https://logging.apache.org/log4j/2.x/log4j-api/apidocs/org/apache/logging/log4j/message/ObjectMessage.html),
containing a payload that can be retrieved by calling [`getParameter()`](https://logging.apache.org/log4j/2.x/log4j-api/apidocs/org/apache/logging/log4j/message/ObjectMessage.html#getParameter--).

The payload emitted by sbt logging is an instance of [`StringEvent`](https://github.com/sbt/util/blob/develop/internal/util-logging/src/main/contraband-scala/sbt/internal/util/StringEvent.scala),
which contains `String` fields including `message` and `level`.

Putting all that together, here's a (completely useless!) example of an extra logger that logs messages from tasks in reverse to the console:

```scala
extraLoggers := {
  import org.apache.logging.log4j.core.LogEvent;
  import org.apache.logging.log4j.core.appender.AbstractAppender
  import org.apache.logging.log4j.message.{Message,ObjectMessage}

  import sbt.internal.util.StringEvent

  def loggerNameForKey( key : sbt.Def.ScopedKey[_] ) = s"""reverse.\${key.scope.task.toOption.getOrElse("<unknown>")}"""

  class ReverseConsoleAppender( key : ScopedKey[_] ) extends AbstractAppender (
    loggerNameForKey( key ), // name : String
    null,                    // filter : org.apache.logging.log4j.core.Filter
    null,                    // layout : org.apache.logging.log4j.core.Layout[ _ <: Serializable]
    false                    // ignoreExceptions : Boolean
  ) {

    this.start() // the log4j2 Appender must be started, or it will fail with an Exception

    override def append( event : LogEvent ) : Unit = {
      val output = {
        def forUnexpected( message : Message ) = s"[\${this.getName()}] Unexpected: \${message.getFormattedMessage()}"
        event.getMessage() match {
	   case om : ObjectMessage => { // what we expect
	     om.getParameter() match {
	       case se : StringEvent => s"[\${this.getName()} - \${se.level}] \${se.message.reverse}"
	       case other            => forUnexpected( om )
	     }
	   }
	   case unexpected : Message => forUnexpected( unexpected )
	}
      }
      System.out.synchronized { // sbt adopts a convention of acquiring System.out's monitor printing to the console
         println( output )
      }
    }
  }

  val currentFunction = extraLoggers.value
  (key: ScopedKey[_]) => {
     new ReverseConsoleAppender(key) +: currentFunction(key)
  }
}
```

Now, if we execute a task that logs messages, we should see our logger invoked:

```
sbt:sbt-logging-example> update
[info] Updating ...
[reverse.update - info] ... gnitadpU
[info] Done updating.
[reverse.update - info] .gnitadpu enoD
[success] Total time: 0 s, completed Oct 16, 2019 5:22:22 AM
```

<a name="log"></a>

### Log messages in a task

The special task `streams` provides per-task logging and I/O via a
[Streams](../api/sbt/std/Streams.html) instance. To log, a task uses
the `log` member from the `streams` task. Calling `log` provides
a [Logger](../api/sbt/util/Logger.html):

```scala
import sbt.Keys.streams

myTask := {
  val log = streams.value.log
  log.warn("A warning.")
}
```

### Log messages in a setting

Since settings cannot reference tasks, the special task `streams`
cannot be used to provide logging during setting initialization.
The recommended way is to use `sLog`. Calling `sLog.value` provides
a [Logger](../api/sbt/util/Logger.html).

```scala
mySetting := {
  val log = sLog.value
  log.warn("A warning.")
}
```

---
out: Running.html
---

  [Hello]: Hello.html
  [Setup]: Setup.html
  [Triggered-Execution]: ../docs/Triggered-Execution.html
  [Command-Line-Reference]: ../docs/Command-Line-Reference.html

Running
-------

This page describes how to use sbt once you have set up your project. It
assumes you've [installed sbt][Setup] and created a
[Hello, World][Hello] or other project.

### sbt shell

Run sbt in your project directory with no arguments:

```
\$ sbt
```

Running sbt with no command line arguments starts sbt shell.
sbt shell has a command prompt (with tab completion and
history!).

For example, you could type `compile` at the sbt shell:

```
> compile
```

To `compile` again, press up arrow and then enter.

To run your program, type `run`.

To leave sbt shell, type `exit` or use Ctrl+D (Unix) or Ctrl+Z
(Windows).

### Batch mode

You can also run sbt in batch mode, specifying a space-separated list of
sbt commands as arguments. For sbt commands that take arguments, pass
the command and arguments as one argument to sbt by enclosing them in
quotes. For example,

```
\$ sbt clean compile "testOnly TestA TestB"
```

In this example, `testOnly` has arguments, `TestA` and `TestB`. The commands
will be run in sequence (`clean`, `compile`, then `testOnly`).

**Note**: Running in batch mode requires JVM spinup and JIT each time,
so **your build will run much slower**.
For day-to-day coding, we recommend using the sbt shell
or Continuous build and test feature described below.

### Continuous build and test

To speed up your edit-compile-test cycle, you can ask sbt to
automatically recompile or run tests whenever you save a source file.

Make a command run when one or more source files change by prefixing the
command with `~`. For example, in sbt shell try:

```
> ~testQuick
```

Press enter to stop watching for changes.

You can use the `~` prefix with either sbt shell or batch mode.

See [Triggered Execution][Triggered-Execution] for more details.

### Common commands

Here are some of the most common sbt commands. For a more complete list,
see [Command Line Reference][Command-Line-Reference].

<table>
  <tr>
    <td><tt>clean</tt></td>
    <td>Deletes all generated files (in the <tt>target</tt> directory).</td>
  </tr>
  <tr>
    <td><tt>compile</tt></td>
    <td>Compiles the main sources (in <tt>src/main/scala</tt> and
   <tt>src/main/java</tt> directories).</td>
  </tr>
  <tr>
    <td><tt>test</tt></td>
    <td>Compiles and runs all tests.</td>
  </tr>
  <tr>
    <td><tt>console</tt></td>
    <td>Starts the Scala interpreter with a classpath including the
   compiled sources and all dependencies. To return to sbt, type <tt>:quit</tt>,
   Ctrl+D (Unix), or Ctrl+Z (Windows).</td>
  </tr>
  <tr>
    <td><nobr><tt>run &lt;argument&gt;*</tt></nobr></td>
    <td>Runs the main class for the project in the same
   virtual machine as sbt.</td>
  </tr>
  <tr>
    <td><tt>package</tt></td>
    <td>Creates a jar file containing the files in
    <tt>src/main/resources</tt> and the classes compiled from <tt>src/main/scala</tt> and
    <tt>src/main/java</tt>.</td>
  </tr>
  <tr>
    <td><tt>help &lt;command&gt;</tt></td>
    <td>Displays detailed help for the specified command.
    If no command is provided, displays brief descriptions of all
    commands.</td>
  </tr>
  <tr>
    <td><tt>reload</tt></td>
    <td>Reloads the build definition (<tt>build.sbt</tt>, <tt>project/*.scala</tt>,
    <tt>project/*.sbt</tt> files). Needed if you change the build definition.</td>
  </tr>
</table>

### Tab completion

sbt shell has tab completion, including at an empty prompt. A
special sbt convention is that pressing tab once may show only a subset
of most likely completions, while pressing it more times shows more
verbose choices.

### History Commands

sbt shell remembers history, even if you exit sbt and restart it.
The simplest way to access history is with the up arrow key. The
following commands are also supported:

<table>
  <tr>
    <td><tt>!</tt></td>
    <td>Show history command help.</td>
  </tr>
  <tr>
    <td><tt>!!</tt></td>
    <td>Execute the previous command again.</td>
  </tr>
  <tr>
    <td><tt>!:</tt></td>
    <td>Show all previous commands.</td>
  </tr>
  <tr>
    <td><tt>!:n</tt></td>
    <td>Show the last <tt>n</tt> commands.</td>
  </tr>
  <tr>
    <td><tt>!n</tt></td>
    <td>Execute the command with index <tt>n</tt>, as shown by the <tt>!:</tt> command.</td>
  </tr>
  <tr>
    <td><tt>!-n</tt></td>
    <td>Execute the nth command before this one.</td>
  </tr>
  <tr>
    <td><tt>!string</tt></td>
    <td>Execute the most recent command starting with 'string.'</td>
  </tr>
  <tr>
    <td><tt>!?string</tt></td>
    <td>Execute the most recent command containing 'string.'</td>
  </tr>
</table>

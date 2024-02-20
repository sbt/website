sbt components
==============

sbt runner
----------

An sbt build is executed using the `sbt` runner, also called "sbt-the-shell-script" to distinguish from other components. It's important to note is that sbt runner is designed to run **any version** of sbt.

### Specifying sbt version with project/build.properties

The sbt runner executes a subcomponent called sbt launcher, which reads `project/build.properties` to determine the sbt version for the build, and downloads the artifacts if they haven't been cached:

```
sbt.version={{sbt_version}}
```

This means that:

- Anyone who checkouts your build would get the same sbt version, regardless of *sbt runner* they may have installed on their machines.
- The change of sbt version can be tracked in a version control system, like git.

### sbtn (`sbt --client`)

sbtn (native thin client) is a subcomponent of the sbt runner, called when you pass `--client` flag to the sbt runner, and is used to send commands to the sbt server. It is called sbtn because it is compiled to native code using GraalVM native-image. The protocol between sbtn and sbt server is stable enough that it should work between **most recent versions** of sbt.

sbt server
----------

The sbt server is the actual build tool whose version is specified using `project/build.properties`. The sbt server acts as a cashier to take commands from sbtn and editors.

### Coursier

The sbt server runs [Couriser][coursier] as a subcomponent to resolve Scala library, Scala compiler, and any other library dependencies your build needs.

### Zinc

Zinc is the incremental compiler for Scala, developed and maintained by sbt project.
An often overlooked aspect of Zinc is that Zinc provides a stable API to invoke **any modern versions** of Scala compiler. Combined with the fact that Coursier can resolve any Scala version, with sbt we can invoke any modern versions of Scala just by writing a single line `build.sbt`:

```scala
scalaVersion := "{{scala3_example_version}}"
```

### BSP server

The sbt server supports [Build Server Protocol (BSP)](https://build-server-protocol.github.io/) to list build targets, build them, etc.
This allows IDEs like IntelliJ and Metals to communicate with a running sbt server programmatically.

Connecting to sbt server
------------------------

Let's look at three ways of connecting to the sbt server.

### sbt shell using sbtn

Run `sbt --client` in the working directory of your build:

```bash
sbt --client
```

This should display something like the following:

```bash
$ sbt --client
[info] server was not detected. starting an instance
[info] welcome to sbt 2.0.0-alpha7 (Azul Systems, Inc. Java 1.8.0_352)
[info] loading project definition from /private/tmp/bar/project
[info] loading settings for project bar from build.sbt ...
[info] set current project to bar (in build file:/private/tmp/bar/)
[info] sbt server started at local:///Users/eed3si9n/.sbt/2.0.0-alpha7/server/d0ac1409c0117a949d47/sock
[info] started sbt server
[info] terminate the server with `shutdown`
[info] disconnect from the server with `exit`
sbt:bar>
```

Running sbt with no command line arguments starts sbt shell. sbt shell has a command prompt (with tab completion and history!).

For example, you could type `compile` at the sbt shell:

```bash
sbt:bar> compile
```

To `compile` again, press up arrow and then enter.

To leave sbt shell, type `exit` or use `Ctrl-D` (Unix) or `Ctrl-Z` (Windows).

### Batch mode using sbtn

You can also run sbt in batch mode:

```bash
sbt --client compile
sbt --client testOnly TestA
```

```bash
$ sbt --client compile
> compile
```

### Shutting down sbt server

Run the following to shutdown all sbt servers on your machine:

```bash
sbt shutdownall
```

Or the following to shutdown just the current one:

```bash
sbt --client shutdown
```

  [coursier]: https://get-coursier.io/

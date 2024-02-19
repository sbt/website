  [sbt-components]: ./sbt-components.md

Working with an existing build
==============================

This page describes how to use sbt once you have set up your project.
This page assumes you've read [sbt components][sbt-components].

If you pull a repository that uses sbt, it's fairly easy to get started.
First, get the package from GitHub, or some other repository.

```bash
$ git clone https://github.com/scalanlp/breeze.git
$ cd breeze
```

### sbt shell with sbtn

As mentioned in [sbt components][sbt-components], start an sbt shell:

```
$ sbt --client
```

This should display something like the following:

```bash
$ sbt --client
[info] entering *experimental* thin client - BEEP WHIRR
[info] server was not detected. starting an instance
[info] welcome to sbt 1.5.5 (Azul Systems, Inc. Java 1.8.0_352)
[info] loading global plugins from /Users/eed3si9n/.sbt/1.0/plugins
[info] loading settings for project breeze-build from plugins.sbt ...
[info] loading project definition from /private/tmp/breeze/project
Downloading https://repo1.maven.org/maven2/org/scalanlp/sbt-breeze-expand-codegen_2.12_1.0/0.2.1/sbt-breeze-expand-codegen-0.2.1.pom
....
[info] sbt server started at local:///Users/eed3si9n/.sbt/1.0/server/dd982e07e85c7de1b618/sock
[info] terminate the server with `shutdown`
[info] disconnect from the server with `exit`
sbt:breeze-parent>
```

projects command
----------------

Let's explore the build by listing out the subprojects with `projects` command:

```
sbt:breeze-parent> projects
[info] In file:/private/tmp/breeze/
[info]     benchmark
[info]     macros
[info]     math
[info]     natives
[info]   * root
[info]     viz
```

This shows that this build has 6 subprojects, including the current subproject called `root`.

tasks command
-------------

Similarly, we can list the tasks availble to this build using `tasks` command:

```
sbt:breeze-parent> tasks

This is a list of tasks defined for the current project.
It does not list the scopes the tasks are defined in; use the 'inspect' command for that.
Tasks produce values.  Use the 'show' command to run the task and print the resulting value.

  bgRun            Start an application's default main class as a background job
  bgRunMain        Start a provided main class as a background job
  clean            Deletes files produced by the build, such as generated sources, compiled classes, and task caches.
  compile          Compiles sources.
  console          Starts the Scala interpreter with the project classes on the classpath.
  consoleProject   Starts the Scala interpreter with the sbt and the build definition on the classpath and useful imports.
  consoleQuick     Starts the Scala interpreter with the project dependencies on the classpath.
  copyResources    Copies resources to the output directory.
  doc              Generates API documentation.
  package          Produces the main artifact, such as a binary jar.  This is typically an alias for the task that actually does the packaging.
  packageBin       Produces a main artifact, such as a binary jar.
  packageDoc       Produces a documentation artifact, such as a jar containing API documentation.
  packageSrc       Produces a source artifact, such as a jar containing sources and resources.
  publish          Publishes artifacts to a repository.
  publishLocal     Publishes artifacts to the local Ivy repository.
  publishM2        Publishes artifacts to the local Maven repository.
  run              Runs a main class, passing along arguments provided on the command line.
  runMain          Runs the main class selected by the first argument, passing the remaining arguments to the main method.
  test             Executes all tests.
  testOnly         Executes the tests provided as arguments or all tests if no arguments are provided.
  testQuick        Executes the tests that either failed before, were not run or whose transitive dependencies changed, among those provided as arguments.
  update           Resolves and optionally retrieves dependencies, producing a report.

More tasks may be viewed by increasing verbosity.  See 'help tasks'
```

### compile

The `compile` tasks compiles the sources, after resolving and downloading the library dependendies.

```
> compile
```

This should display something like the following:

```
sbt:breeze-parent> compile
[info] compiling 341 Scala sources and 1 Java source to /private/tmp/breeze/math/target/scala-3.1.3/classes ...
  | => math / Compile / compileIncremental 51s
```

### run

The `run` task runs the main class for the subproject.
In the sbt shell, type `math/run`:

```
> math/run
```

`math/run` means `run` task, scoped to `math` subproject.
This should display something like the following:

```
sbt:breeze-parent> math/run
[info] Scala version: 3.1.3 true
....

Multiple main classes detected. Select one to run:
 [1] breeze.optimize.linear.NNLS
 [2] breeze.optimize.proximal.NonlinearMinimizer
 [3] breeze.optimize.proximal.QuadraticMinimizer
 [4] breeze.util.UpdateSerializedObjects

Enter number:
```

Enter `1` at the prompt.

### testQuick

The `testQuick` task tests either the tests that failed before, were not run, or whose transitive dependencies changed.

```
> math/testQuick
```

This should display something like the following:

```
sbt:breeze-parent> math/testQuick
[info] FeatureVectorTest:
[info] - axpy fv dv (1 second, 106 milliseconds)
[info] - axpy fv vb (9 milliseconds)
[info] - DM mult (19 milliseconds)
[info] - CSC mult (32 milliseconds)
[info] - DM trans mult (4 milliseconds)
....
[info] Run completed in 58 seconds, 183 milliseconds.
[info] Total number of tests run: 1285
[info] Suites: completed 168, aborted 0
[info] Tests: succeeded 1285, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[success] Total time: 130 s (02:10), completed Feb 19, 2024
```

Watch (tilde) command
---------------------

To speed up your edit-compile-test cycle, you can ask sbt to
automatically recompile or run tests whenever you save a source file.

Make a command run when one or more source files change by prefixing the
command with `~`. For example, in sbt shell try:

```
> ~testQuick
```

Press enter to stop watching for changes.
You can use the `~` prefix with either sbt shell or batch mode.

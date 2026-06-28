
  [Console-Project]: Console-Project.html
  [Basic-Def]: Basic-Def.html
  [Full-Def]: Full-Def.html
  [Library-Dependencies]: Library-Dependencies.html
  [Multi-Project]: Multi-Project.html
  [Running]: ../guide/basic-tasks.md
  [Inspecting-Settings]: Inspecting-Settings.html
  [Triggered-Execution]: Triggered-Execution.html
  [Commands]: Commands.html
  [Running-Project-Code]: Running-Project-Code.html
  [Testing]: Testing.html

sbt
===

See [Basic Tasks][Running] in the Getting Started Guide for an intro to the basics.

Synopsis
--------

`sbt`<br>
`sbt` *command* *args*<br>
`sbt --server`<br>
`sbt --script-version`

Description
-----------
sbt is a simple build tool created originally for Scala and Java. It lets us declare subprojects and their various dependencies and custom tasks to ensure that we'll always get a fast, repeatable build.

### sbt runner and sbt server

- sbt runner is a system shell script named `sbt`, or `sbt.bat` on Windows. That is capable of running *any version of sbt*. This is sometimes called "sbt-the-shell-script".
  - When sbt 2.x is detected, sbt runner executes in a client mode, typically using sbtn, a GraalVM native implementation of the thin client program.
  - sbt runner also executes sbt launcher, a launcher that is capable of running *any verions of sbt*.
  - When you install sbt from a installer, what you're installing is the sbt runner.
- sbt server is sbt's main artifact, and the actual build tool.
  - The sbt version is determined by `project/build.properties` in each working directory.
  - sbt server accepts commands from sbtn, network API, or via its own sbt shell.

```
sbt.version={{sbt_version}}
```

This mechanism allows builds to be configured to a specific version of sbt, and everyone working on the project would use the same build semantics, regardless of the sbt runner installed on their machine.

This also means that some features are implemented at sbt runner or sbtn level, while other features are implemented at sbt server level.

sbt commands
-------------------

```admonish note title="Note on commands"
In sbt, there are _tasks_ that operate at the subproject level (like `compile`), and _commands_ in the narrow sense (like `set`), which is capable of manipulating the build definition itself.

Given that settings and tasks are lifted into the `act` command, we can consider _all things that can be typed into the sbt shell_ as commands in the wide sense.
See the [Command](../concepts/command.md) concept page for details.
```

<!--
-   Some tasks produce useful values. The `toString` representation of
    these values can be shown using `show <task>` to run the task
    instead of just `<task>`.
-   In a multi-project build, execution dependencies and the aggregate
    setting control which tasks from which projects are executed. See
    [multi-project builds][Multi-Project].
-->

### Subproject-level tasks

-   `clean` Deletes all generated files (the `target` directory).
-   [`publish`](./sbt-publish.md) Publishes artifacts (such as JARs) to the repository
    defined by the `publishTo` setting.
-   [`publishLocal`](./sbt-publish.md) Publishes artifacts (such as JARs) to the local Ivy
    repository as described in Publishing.
-   [`update`](./sbt-update.md) Resolves and retrieves external dependencies.

### Configuration-level tasks

Configuration-level tasks are tasks associated with a configuration. For
example, `compile`, which is equivalent to `Compile/compile`, compiles
the main source code (the `Compile` configuration). `Test/compile`
compiles the test source code (the `Test` configuration). Most tasks
for the `Compile` configuration have an equivalent in the `Test`
configuration that can be run using a `Test/` prefix.

-   [`compile`](./sbt-compile.md) Compiles the main sources (in the `src/main/scala`
    directory). `Test/compile` compiles test sources (in the
    src/test/scala/ directory).
-   `console` Starts the Scala interpreter with a classpath including
    the compiled sources, all JARs in the lib directory, and managed
    libraries. To return to sbt, type :quit, Ctrl+D (Unix), or Ctrl+Z
    (Windows). Similarly, Test/console starts the interpreter with the
    test classes and classpath.
-   `doc` Generates API documentation for Scala source files in
    `src/main/scala` using scaladoc. `Test/doc` generates API documentation
    for source files in `src/test/scala`.
-   `package` Creates a JAR file containing the files in
    `src/main/resources` and the classes compiled from `src/main/scala`.
    `Test/package` creates a JAR containing the files in
    `src/test/resources` and the class compiled from `src/test/scala`.
-   `packageDoc` Creates a JAR file containing API documentation
    generated from Scala source files in src/main/scala. Test/packageDoc
    creates a JAR containing API documentation for test sources files in
    src/test/scala.
-   `packageSrc`: Creates a JAR file containing all main source files
    and resources. The packaged paths are relative to src/main/scala and
    src/main/resources. Similarly, Test/packageSrc operates on test
    source files and resources.
-   [`run <argument>*`](./sbt-run.md) Runs the main class for the project in the same
    virtual machine as sbt. The main class is passed the arguments
    provided.
-   `runMain <main-class> <argument>*` Runs the specified main class for
    the project in the same virtual machine as sbt. The main class is
    passed the arguments provided.
    <!-- See
    [Running Project Code][Running-Project-Code] for
    details on the use of System.exit and multithreading (including
    GUIs) in code run by this action. `Test/runMain` runs the specified
    main class in the test code. -->
-   [`test <test>*`](./sbt-test.md) Runs the tests specified as arguments (or all
    tests if no arguments are given) that:
      1.  have not been run yet OR
      2.  failed the last time they were run OR
      3.  had any transitive dependencies recompiled since the last successful run<br>
    `*` is interpreted as a wildcard in the
        test name.
-   [`testFull`](./sbt-test.md) Runs all tests detected during test compilation.

<!--
-   [`testOnly <test>*`](./sbt-test.md) Runs the tests provided as arguments.

-   `consoleQuick` Starts the Scala interpreter with the project's
    compile-time dependencies on the classpath. Test/consoleQuick uses
    the test dependencies. This task differs from console in that it
    does not force compilation of the current project's sources.
-   `consoleProject` Enters an interactive session with sbt and the
    build definition on the classpath. The build definition and related
    values are bound to variables and common packages and values are
    imported. See the [consoleProject documentation][Console-Project]
    for more information.
-->

### General commands

-   `shutdown` Shuts down the sbt server to end the current sbt session.
-   `exit` or `quit` End the current interactive session or build.
    Additionally, Ctrl+D (Unix) or Ctrl+Z (Windows) will exit the
    interactive prompt.
-   `help <command>` Displays detailed help for the specified command.
    If the command does not exist, help lists detailed help for commands
    whose name or description match the argument, which is interpreted
    as a regular expression. If no command is provided, displays brief
    descriptions of the main commands. Related commands are tasks and
    settings.
-   `projects [add|remove <URI>]` List all available projects if no
    arguments provided or adds/removes the build at the provided URI.
    <!-- (See [multi-project builds][Multi-Project] for details on multi-project
    builds.) -->
<!--
-   `project <project-id>` Change the current project to the project
    with ID `<project-id>`. Further operations will be done in the
    context of the given project.
    (See [multi-project builds][Multi-Project] for
    details on multiple project builds.)
-->
-   [Watch command](./watch.md) `~ <command>` Executes the project specified action or method
    whenever source files change.
-   `< filename` Executes the commands in the given file. Each command
    should be on its own line. Empty lines and lines beginning with '#'
    are ignored
-   `A ; B` Execute A and if it succeeds, run B. Note that the leading
    semicolon is required.
-   `eval <Scala-expression>` Evaluates the given Scala expression and
    returns the result and inferred type. This can be used to set system
    properties, as a calculator, to fork processes, etc ... For example:

        > eval System.setProperty("demo", "true")
        > eval 1+1
        > eval "ls -l" !

<!--
-   `+ <command>` Executes the project specified action or method for
    all versions of Scala defined in the crossScalaVersions setting.
-   `++ <version|home-directory> <command>` Temporarily changes the
    version of Scala building the project and executes the provided
    command. `<command>` is optional. The specified version of Scala is
    used until the project is reloaded, settings are modified (such as
    by the set or session commands), or ++ is run again. `<version>`
    does not need to be listed in the build definition, but it must be
    available in a repository. Alternatively, specify the path to a
    Scala installation.
-->

### Commands for managing the build definition

-   `reload [plugins|return]` If no argument is specified, reloads the
    build, recompiling any build or plugin definitions as necessary.
    reload plugins changes the current project to the build definition
    project (in `project/`). This can be useful to directly manipulate the
    build definition. For example, running clean on the build definition
    project will force snapshots to be updated and the build definition
    to be recompiled. reload return changes back to the main project.
-   `set <setting-expression>` Evaluates and applies the given setting
    definition. The setting applies until sbt is restarted, the build is
    reloaded, or the setting is overridden by another set command or
    removed by the session command.
    <!-- See [.sbt build definition][Basic-Def] and
    [Inspecting Settings][Inspecting-Settings] for details. -->
-   `session <command>` Manages session settings defined by the `set`
    command. It can persist settings configured at the prompt.
    <!-- See [Inspecting Settings][Inspecting-Settings] for details. -->
-   `inspect <setting-key>` Displays information about settings, such as
    the value, description, defining scope, dependencies, delegation
    chain, and related settings.
    <!-- See [Inspecting Settings][Inspecting-Settings] for details. -->

sbt runner and launcher
-----------------------

When launching the `sbt` runner from the system shell, various system properties
or JVM extra options can be specified to influence its behaviour.

### sbt JVM options and system properties

If the `JAVA_OPTS` and/or `SBT_OPTS` environment variables are defined when 
`sbt` starts, their content is passed as command line arguments to the JVM 
running sbt server. 

If a file named `.jvmopts` exists in the  current directory, its content
is appended to `JAVA_OPTS` at sbt startup. Similarly, if `.sbtopts` 
and/or `/etc/sbt/sbtopts` exist, their content is appended to `SBT_OPTS`.
The default value of `JAVA_OPTS` is `-Dfile.encoding=UTF8`.

You can also specify JVM system properties and command line options 
directly as `sbt` arguments: any `-Dkey=val` argument will be passed 
as-is to the JVM, and any `-J-Xfoo` will be passed as `-Xfoo`.

See also `sbt --help` for more details. 


### sbt JVM heap, permgen, and stack sizes

If you find yourself running out of permgen space or your workstation is
low on memory, adjust the JVM configuration as you would for any java
application. 

For example a common set of memory-related options is:

```bash
export SBT_OPTS="-Xmx2048M -Xss2M"
sbt
```

Or if you prefer to specify them just for this session:

```bash
sbt -J-Xmx2048M -J-Xss2M
```

### Boot directory

`sbt` runner is just a bootstrap, the actual sbt server,
Scala compiler and standard library are by default downloaded to
the shared directory  `$HOME/.sbt/boot/`.

To change the location of this directory, set the `sbt.boot.directory`
system property. A relative path will be resolved
against the current working directory, which can be useful if you want
to avoid sharing the boot directory between projects. For example, the
following uses the pre-0.11 style of putting the boot directory in
`project/boot/`:

```bash
sbt -Dsbt.boot.directory=project/boot/
```

### Global base directory

Global base directory contains machine-wide settings and plugins.
This is determined by:

1. `-Dsbt.boot.directory` system property
2. `$SBT_CONFIG_HOME` environment variable.
3. `%LOCALAPPDATA%/sbt` on Windows
4. `$XDG_CONFIG_HOME/sbt` on non-Windows
5. `$HOME/.config/sbt`

Then a subdirectory named `2`, for example, `$HOME/.config/sbt/2`.

### Terminal encoding

The character encoding used by your terminal may differ from Java's
default encoding for your platform. In this case, you will need to specify
the `file.encoding=<encoding>` system property, which might look like:

```bash
export JAVA_OPTS="-Dfile.encoding=Cp1252"
sbt
```

### HTTP/HTTPS/FTP Proxy

On Unix, sbt will pick up any HTTP, HTTPS, or FTP proxy settings from
the standard `http_proxy`, `https_proxy`, and `ftp_proxy` environment
variables. If you are behind a proxy requiring authentication, you
need to pass some supplementary flags at sbt startup. See 
[JVM networking system properties](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/doc-files/net-properties.html) for more details. 

For example:

```bash
sbt -Dhttp.proxyUser=username -Dhttp.proxyPassword=mypassword
```

On Windows, your script should set properties for proxy host, port, and
if applicable, username and password. For example, for HTTP:

```bash
sbt -Dhttp.proxyHost=myproxy -Dhttp.proxyPort=8080 -Dhttp.proxyUser=username -Dhttp.proxyPassword=mypassword
```

Replace `http` with `https` or `ftp` in the above command line to
configure HTTPS or FTP.

### Other system properties

The following system properties can also be passed to `sbt` runner:

#### `-Dsbt.banner=true`

Show a welcome banner advertising new features.

#### `-Dsbt.ci=true`

Default `false` (unless then env var `BUILD_NUMBER` is set). For continuous integration environments. Suppress supershell and color.

#### `-Dsbt.client=true`

Run the sbt client.

#### `-Dsbt.color=auto`

- To turn on color, use `always` or `true`.
- To turn off color, use `never` or `false`.
- To use color if the output is a terminal (not a pipe) that supports color, use `auto`.

#### `-Dsbt.coursier.home=$HOME/.cache/coursier/v1`

Location of the Coursier artifact cache, where the default is defined by [Coursier cache resolution logic](https://get-coursier.io/docs/cache.html#default-location). You can verify the value with the command `csrCacheDirectory`.

#### `-Dsbt.genbuildprops=true`

Generate `build.properties` if missing. If unset, this defers to `sbt.skip.version.write`.

#### `-Dsbt.override.build.repos=true`

If true, repositories configured in a build definition are ignored and the repositories configured for the launcher are used instead.

<!-- See <tt>sbt.repository.config</tt> and the <a href="Launcher-Configuration.html">sbt launcher</a> documentation. -->

#### `-Dsbt.repository.config=$HOME/.sbt/repositories`

A file containing the repositories to use for the launcher. The format is the same as a `[repositories]` section for a sbt launcher configuration file. This setting is typically used in conjunction with setting `sbt.override.build.repos` to `true`.

<!-- (see <tt>sbt.override.build.repos</tt> and the <a href="Launcher-Configuration.html">sbt launcher</a> documentation).</td> -->


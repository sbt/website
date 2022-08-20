---
out: Command-Line-Reference.html
---

  [Console-Project]: Console-Project.html
  [Basic-Def]: Basic-Def.html
  [Full-Def]: Full-Def.html
  [Library-Dependencies]: Library-Dependencies.html
  [Multi-Project]: Multi-Project.html
  [Running]: Running.html
  [Inspecting-Settings]: Inspecting-Settings.html
  [Triggered-Execution]: Triggered-Execution.html
  [Commands]: Commands.html
  [Running-Project-Code]: Running-Project-Code.html
  [Testing]: Testing.html

Command Line Reference
----------------------

This page is a relatively complete list of command line options,
commands, and tasks you can use from the sbt interactive prompt or in
batch mode. See [Running][Running] in the Getting
Started Guide for an intro to the basics, while this page has a lot more
detail.

### Notes on the command line

-   There is a technical distinction in sbt between *tasks*, which are
    "inside" the build definition, and *commands*, which manipulate the
    build definition itself. If you're interested in creating a command,
    see [Commands][Commands]. This specific sbt meaning of "command"
    means there's no good general term for "thing you can type at the
    sbt prompt", which may be a setting, task, or command.
-   Some tasks produce useful values. The `toString` representation of
    these values can be shown using `show <task>` to run the task
    instead of just `<task>`.
-   In a multi-project build, execution dependencies and the aggregate
    setting control which tasks from which projects are executed. See
    [multi-project builds][Multi-Project].

### Project-level tasks

-   `clean` Deletes all generated files (the `target` directory).
-   `publishLocal` Publishes artifacts (such as jars) to the local Ivy
    repository as described in Publishing.
-   `publish` Publishes artifacts (such as jars) to the repository
    defined by the publishTo setting, described in Publishing.
-   `update` Resolves and retrieves external dependencies as described
    in [library dependencies][Library-Dependencies].

### Configuration-level tasks

Configuration-level tasks are tasks associated with a configuration. For
example, `compile`, which is equivalent to `Compile/compile`, compiles
the main source code (the `compile` configuration). `Test/compile`
compiles the test source code (test `test` configuration). Most tasks
for the `compile` configuration have an equivalent in the `test`
configuration that can be run using a `Test/` prefix.

-   `compile` Compiles the main sources (in the `src/main/scala`
    directory). `Test/compile` compiles test sources (in the
    src/test/scala/ directory).
-   `console` Starts the Scala interpreter with a classpath including
    the compiled sources, all jars in the lib directory, and managed
    libraries. To return to sbt, type :quit, Ctrl+D (Unix), or Ctrl+Z
    (Windows). Similarly, Test/console starts the interpreter with the
    test classes and classpath.
-   `consoleQuick` Starts the Scala interpreter with the project's
    compile-time dependencies on the classpath. Test/consoleQuick uses
    the test dependencies. This task differs from console in that it
    does not force compilation of the current project's sources.
-   `consoleProject` Enters an interactive session with sbt and the
    build definition on the classpath. The build definition and related
    values are bound to variables and common packages and values are
    imported. See the [consoleProject documentation][Console-Project]
    for more information.
-   `doc` Generates API documentation for Scala source files in
    `src/main/scala` using scaladoc. `Test/doc` generates API documentation
    for source files in `src/test/scala`.
-   `package` Creates a jar file containing the files in
    `src/main/resources` and the classes compiled from `src/main/scala`.
    `Test/package` creates a jar containing the files in
    `src/test/resources` and the class compiled from `src/test/scala`.
-   `packageDoc` Creates a jar file containing API documentation
    generated from Scala source files in src/main/scala. Test/packageDoc
    creates a jar containing API documentation for test sources files in
    src/test/scala.
-   `packageSrc`: Creates a jar file containing all main source files
    and resources. The packaged paths are relative to src/main/scala and
    src/main/resources. Similarly, Test/packageSrc operates on test
    source files and resources.
-   `run <argument>*` Runs the main class for the project in the same
    virtual machine as sbt. The main class is passed the arguments
    provided. Please see
    [Running Project Code][Running-Project-Code] for details on the use of
    System.exit and multithreading (including GUIs) in code run by this
    action. `Test/run` runs a main class in the test code.
-   `runMain <main-class> <argument>*` Runs the specified main class for
    the project in the same virtual machine as sbt. The main class is
    passed the arguments provided. Please see
    [Running Project Code][Running-Project-Code] for
    details on the use of System.exit and multithreading (including
    GUIs) in code run by this action. `Test/runMain` runs the specified
    main class in the test code.
-   `test` Runs all tests detected during test compilation. See [Testing][Testing]
    for details.
-   `testOnly <test>*` Runs the tests provided as arguments. `*` (will
    be) interpreted as a wildcard in the test name. See [Testing][Testing] for
    details.
-   `testQuick <test>*` Runs the tests specified as arguments (or all
    tests if no arguments are given) that:
    1.  have not been run yet OR
    2.  failed the last time they were run OR
    3.  had any transitive dependencies recompiled since the last
        successful run `*` (will be) interpreted as a wildcard in the
        test name. See [Testing][Testing] for details.

### General commands

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
    (See [multi-project builds][Multi-Project] for details on multi-project
    builds.)
-   `project <project-id>` Change the current project to the project
    with ID `<project-id>`. Further operations will be done in the
    context of the given project. (See [multi-project builds][Multi-Project] for
    details on multiple project builds.)
-   `~ <command>` Executes the project specified action or method
    whenever source files change. See
    [Triggered Execution][Triggered-Execution] for details.
-   `< filename` Executes the commands in the given file. Each command
    should be on its own line. Empty lines and lines beginning with '#'
    are ignored
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
-   `; A ; B` Execute A and if it succeeds, run B. Note that the leading
    semicolon is required.
-   `eval <Scala-expression>` Evaluates the given Scala expression and
    returns the result and inferred type. This can be used to set system
    properties, as a calculator, to fork processes, etc ... For example:

        > eval System.setProperty("demo", "true")
        > eval 1+1
        > eval "ls -l" !

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
    removed by the session command. See
    [.sbt build definition][Basic-Def] and
    [Inspecting Settings][Inspecting-Settings] for details.
-   `session <command>` Manages session settings defined by the `set`
    command. It can persist settings configured at the prompt. See
    [Inspecting Settings][Inspecting-Settings] for details.
-   `inspect <setting-key>` Displays information about settings, such as
    the value, description, defining scope, dependencies, delegation
    chain, and related settings. See
    [Inspecting Settings][Inspecting-Settings] for details.


Sbt runner arguments
-----------------

When launching the `sbt` runner from the OS shell, various system properties
or JVM extra options can be specified to influence its behaviour.

#### sbt JVM options and system properties

If the `JAVA_OPTS` and/or `SBT_OPTS` environment variables are defined when 
`sbt` starts, their content is passed as command line arguments to the JVM 
running sbt. 

If a file named `.jvmopt` exists in the  current directory, its content 
is appended to `JAVA_OPTS` at sbt startup. Similarly, if `.sbtopts` 
and/or `/etc/sbt/sbtopts` exit, their content is appended to `SBT_OPTS`.
The default value of `JAVA_OPTS` is `-Dfile.encoding=UTF8`.

You can also specify JVM system properties and command line options 
directly as `sbt` arguments: any `-Dkey=val` argument will be passed 
as-is to the JVM, and any `-J-Xfoo` will be passed as `-Xfoo`.

See also `sbt --help` for more details. 


#### sbt JVM heap, permgen, and stack sizes

If you find yourself running out of permgen space or your workstation is
low on memory, adjust the JVM configuration as you would for any java
application. 

For example a common set of memory-related options is:

```
export SBT_OPTS="-Xmx2048M -Xss2M"
sbt
```

Or if you prefer to specify them just for this session:

```
sbt -J-Xmx2048M -J-Xss2M
```

#### Boot directory

`sbt` is just a bootstrap, the actual meat of sbt, the Scala compiler 
and standard library are by default downloaded to the shared directory 
`\$HOME/.sbt/boot/`.

To change the location of this directory, set the `sbt.boot.directory`
system property. A relative path will be resolved
against the current working directory, which can be useful if you want
to avoid sharing the boot directory between projects. For example, the
following uses the pre-0.11 style of putting the boot directory in
`project/boot/`:

```
sbt -Dsbt.boot.directory=project/boot/
```

#### Terminal encoding

The character encoding used by your terminal may differ from Java's
default encoding for your platform. In this case, you will need to specify
the `file.encoding=<encoding>` system property, which might look like:

```
export JAVA_OPTS="-Dfile.encoding=Cp1252"
sbt
```

#### HTTP/HTTPS/FTP Proxy

On Unix, sbt will pick up any HTTP, HTTPS, or FTP proxy settings from
the standard `http_proxy`, `https_proxy`, and `ftp_proxy` environment
variables. If you are behind a proxy requiring authentication, you
need to pass some supplementary flags at sbt startup. See 
[JVM networking system properties](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/doc-files/net-properties.html) for more details. 

For example:

```
sbt -Dhttp.proxyUser=username -Dhttp.proxyPassword=mypassword
```

On Windows, your script should set properties for proxy host, port, and
if applicable, username and password. For example, for HTTP:

```
sbt -Dhttp.proxyHost=myproxy -Dhttp.proxyPort=8080 -Dhttp.proxyUser=username -Dhttp.proxyPassword=mypassword
```

Replace `http` with `https` or `ftp` in the above command line to
configure HTTPS or FTP.

#### Other system properties

The following system properties can also be passed to `sbt`:

<table class="table table-striped">
  <tr>
    <th>Property</th>
    <th>Values</th>
    <th>Default</th>
    <th>Meaning</th>
  </tr>

  <tr>
    <td><tt>sbt.banner</tt></td>
    <td>Boolean</td>
    <td><tt>true</tt> (in 1.3+)</td>
    <td>Show a welcome banner advertising new features.</td>
  </tr>

  <tr>
    <td><tt>sbt.boot.directory</tt></td>
    <td>Directory</td>
    <td><tt>~/.sbt/boot</tt></td>
    <td>Path to shared boot directory.  You should define <tt>sbt.boot.directory</tt> explicitly if you are using sbt in a CI environment and want to cache sbt boot classes between runs.</td>
  </tr>

  <tr>
    <td><tt>sbt.boot.properties</tt></td>
    <td>File</td>
    <td>Roughly <tt>sbt.boot.properties</tt> internal to the sbt launcher and specific to the sbt version.</td>
    <td>The path to find the sbt <a href="Launcher-Configuration.html">boot
        properties</a> file. This can be a
        relative path, relative to the sbt base directory, the users
        home directory or the location of the sbt jar file, or it can
        be an absolute path or an absolute file URI.</td>
  </tr>

  <tr>
    <td><tt>sbt.ci</tt></td>
    <td>Boolean</td>
    <td><tt>false</tt> (unless then env var <tt>BUILD_NUMBER</tt> is set)</td>
    <td>For continuous integration environments. Suppress supershell and color.</td>
  </tr>

  <tr>
    <td><tt>sbt.client</tt></td>
    <td>Boolean</td>
    <td><tt>false</tt></td>
    <td></td>
  </tr>

  <tr>
    <td><tt>sbt.color</tt></td>
    <td>String</td>
    <td><tt>auto</tt></td>
    <td>Supercedes <tt>sbt.log.format</tt>.
    To turn on color, use <tt>always</tt> or <tt>true</tt>.
    To turn off color, use <tt>never</tt> or <tt>false>.
    To use color if the output is a terminal (not a pipe) that supports color, use <tt>auto</tt>.</td>
  </tr>

  <tr>
    <td><tt>sbt.coursier</tt></td>
    <td>Boolean</td>
    <td><tt>true</tt> (in 1.3+)</td>
    <td>Use coursier to retrieve packages. (See also <tt>sbt.ivy</tt>.)</td>
  </tr>

  <tr>
    <td><tt>sbt.coursier.home</tt></td>
    <td>Directory</td>
    <!-- https://github.com/sbt/sbt/blob/v1.3.1/main/src/main/scala/sbt/coursierint/LMCoursier.scala#L40-L44 -->
    <td><tt>CoursierDependencyResolution.defaultCacheLocation</tt> (in 1.3+)</td>
    <td>Location of coursier artifact cache, where the default is defined by <a href="https://get-coursier.io/docs/cache.html#default-location">Coursier cache resolution logic</a>. You can verify the value with the command `csrCacheDirectory`.  You should define <tt>sbt.coursier.home</tt> explicitly if you are using coursier in a CI environment and want to cache artifacts between runs.</td>
  </tr>

  <tr>
    <td><tt>sbt.extraClasspath</tt></td>
    <td>Classpath Entries</td>
    <td><tt></tt></td>
    <td>(jar files or directories) that are added to sbt's classpath.
        Note that the entries are delimited by comma, e.g.:
        <tt>entry1, entry2,..</tt>. See also <tt>resource</tt> in the
        <a href="Launcher-Configuration.html">sbt launcher</a> documentation.
        </td>
  </tr>

  <tr>
    <td><tt>sbt.genbuildprops</tt></td>
    <td>Boolean</td>
    <td><tt>true</tt></td>
    <td>Generate <tt>build.properties</tt> if missing.
        If unset, this defers to <tt>sbt.skip.version.write</tt>.</td>
  </tr>

  <tr>
    <td><tt>sbt.global.base</tt></td>
    <td>Directory</td>
    <td><tt>$global_base$</tt></td>
    <td>The directory containing global settings and plugins.</td>
  </tr>

  <tr>
    <td><tt>xsbt.inc.debug</tt></td>
    <td>Boolean</td>
    <td><tt>false</tt></td>
    <td>Extra debugging for the incremental debugger.</td>
  </tr>

  <tr>
    <td><tt>sbt.ivy</tt></td>
    <td>Boolean</td>
    <td><tt>true</tt> (in <1.3)</td>
    <td>Use ivy to retrieve packages.</td>
  </tr>

  <tr>
    <td><tt>sbt.ivy.home</tt></td>
    <td>Directory</td>
    <td><tt>~/.ivy2</tt></td>
    <td>The directory containing the local Ivy repository and artifact cache.  You should define <tt>sbt.ivy.home</tt> explicitly if you are using sbt in a CI environment and want to cache ivy artifacts between runs.</td>
  </tr>

  <tr>
    <td><tt>sbt.log.noformat</tt></td>
    <td>Boolean</td>
    <td><tt>false</tt></td>
    <td>If true, disable ANSI color
        codes. Useful on build servers
        or terminals that do not support
        color.</td>
  </tr>

  <tr>
    <td><tt>sbt.main.class</tt></td>
    <td>String</td>
    <td><tt>sbt.xMain</tt></td>
    <td>The sbt class to use (<a
        href="Scripts.html#Manual+Setup">alternatives</a> include
        <tt>sbt.ConsoleMain</tt> and <tt>sbt.ScriptMain</tt>).</td>
  </tr>

  <tr>
    <td><tt>sbt.offline</tt></td>
    <td>Boolean</td>
    <td><tt>false</tt></td>
    <td>Avoid retrieving classes from repositories.</td>
  </tr>

  <tr>
    <td><tt>sbt.override.build.repos</tt></td>
    <td>Boolean</td>
    <td><tt>false</tt></td>
    <td>If true, repositories configured in a build definition
        are ignored and the repositories configured for the launcher are
        used instead. See <tt>sbt.repository.config</tt> and the
        <a href="Launcher-Configuration.html">sbt launcher</a> documentation.</td>
  </tr>

  <tr>
    <td><tt>sbt.progress</tt></td>
    <td>String</td>
    <td><tt>?</tt></td>
    <td>Use <tt>always</tt> to show progress ("supershell").</td>
  </tr>

  <tr>
    <td><tt>sbt.repository.config</tt></td>
    <td>File</td>
    <td><tt>~/.sbt/repositories</tt></td>
    <td>A file containing the repositories to use for the
        launcher. The format is the same as a
        <tt>[repositories]</tt> section for a
        <a href="Launcher-Configuration.html">sbt launcher</a> configuration file.
        This setting is typically used in conjunction with setting
        <tt>sbt.override.build.repos</tt> to
        <tt>true</tt> (see <tt>sbt.override.build.repos</tt> and the
        <a href="Launcher-Configuration.html">sbt launcher</a> documentation).</td>
  </tr>

  <tr>
    <td><tt>sbt.resident.limit</tt></td>
    <td>Number</td>
    <td><tt>0</tt></td>
    <td>The number of scala compilers to keep around.
        This experimental feature was intended to improve compilation time.
        It does not appear to have succeeded and this flag will probably be removed.</td>
  </tr>

  <tr>
    <td><tt>sbt.skip.version.write</tt></td>
    <td>Boolean</td>
    <td><tt>false</tt></td>
    <td>Generate <tt>build.properties</tt> if missing. See <tt>sbt.genbuildprops</tt>.</td>
  </tr>

  <tr>
    <td><tt>sbt.supershell</tt></td>
    <td>Boolean</td>
    <td><tt>true</tt> if color is enabled</td>
    <td>Use supershell (show progress at bottom of shell).</td>
  </tr>

  <tr>
    <td><tt>sbt.supershell.sleep</tt></td>
    <td>Number</td>
    <td><tt>100</tt></td>
    <td>Time to wait between updating the supershell progress area.</td>
  </tr>

  <tr>
    <td><tt>sbt.task.timings</tt></td>
    <td>Boolean</td>
    <td><tt>false</tt></td>
    <td>Measure the time elapsed for running tasks.</td>
  </tr>

  <tr>
    <td><tt>sbt.task.timings.omit.paths</tt></td>
    <td>Boolean</td>
    <td><tt>false</tt></td>
    <td>Omit paths when reporting timings.</td>
  </tr>

  <tr>
    <td><tt>sbt.task.timings.on.shutdown</tt></td>
    <td>Boolean</td>
    <td><tt>false</tt></td>
    <td>Report timings at JVM shutdown (instead of at task completion).</td>
  </tr>

  <tr>
    <td><tt>sbt.task.timings.threshold</tt></td>
    <td>String</td>
    <td><tt>0</tt></td>
    <td>Omit items from timing report if they are below this threshold.</td>
  </tr>

  <tr>
    <td><tt>sbt.traces</tt></td>
    <td>Boolean</td>
    <td><tt>false</tt></td>
    <td>Generate Chrome Trace Event Format log for tasks.</td>
  </tr>

  <tr>
    <td><tt>sbt.turbo</tt></td>
    <td>Boolean</td>
    <td><tt>false</tt></td>
    <td>Use additional layered class loaders.</td>
  </tr>

  <tr>
    <td><tt>sbt.version</tt></td>
    <td>Version</td>
    <td><tt>$app_version$</tt></td>
    <td>sbt version to use, usually taken from <tt>project/build.properties</tt>.</td>
  </tr>

  <tr>
    <td><tt>sbt.watch.mode</tt></td>
    <td>String</td>
    <td><tt>auto</tt></td>
    <td>If <tt>polling</tt>, check file system periodically for updates.</td>
  </tr>
</table>

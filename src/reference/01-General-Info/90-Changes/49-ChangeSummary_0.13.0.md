---
out: ChangeSummary_0.13.0.html
---

  [multiple-scopes]: Tasks.html#multiple-scopes
  [conflict-management]: Library-Management.html#conflict-management
  [Setup]: Setup.html
  [Input-Tasks]: Input-Tasks.html

sbt 0.13.0 - 0.13.2
-------------------

### sbt 0.13.2

- Adding new name-hashing feature to incremental compiler. Alters how scala dependencies are tracked, reducing number of recompiles necessary.
- Added the ability to launch servers via the sbt-launcher.
- Added `.previous` feature on tasks which can load the previous value.
- Added `all` command which can run more than tasks in parallel.
- Exposed the 'overwrite' flags from ivy. Added warning if overwriting a release version.
- Improve the error message when credentials are not found in Ivy.
- Improve task macros to handle more scala constructs.
- Fix `last` and `export` tasks to read from the correct stream.
- Fix issue where ivy's `.+` dependency ranges were not correctly translated to maven.
- Override security manager to ignore file permissions (performance issue)
- 2.11 compatibility fixes
- Launcher can now handle ivy's `.+` revisions.
- `SessionSettings` now correctly overwrite existing settings.
- Adding a simple `Logic` system for inclusionary/dependency logic of plugins.
- Improve build hooks for `LoggerReporter` and `TaskProgress`.
- Serialize incremental compiler analysis into text-file format.
- Issue a warning when generating Paths and separate already exists in the path.
- Migrate to Ivy 2.3.0-final.
- Docs: Use bintray as default repository host
- Docs: improved docs on test groups.
- Docs: updated documentation on the Launcher.
- Docs: started architecture document.

### sbt 0.13.1

- The Scala version for sbt and sbt plugins is now 2.10.3. This is a compatible version bump.
- New method `toTask` on `Initialize[InputTask[T]]` to apply the full input and get a plain task out.
- Improved performance of inspect tree
- Work around various issues with Maven local repositories, including resolving -SNAPSHOTs from them. (#321)
- Better representation of no cross-version suffix in suffix conflict error message: now shows `<none>` instead of just `_`
- `TrapExit` support for multiple, concurrent managed applications. Now enabled by default for all `run`-like tasks. (#831)
- Add minimal support for class file formats 51.0, 52.0 in incremental compiler. (#842)
- Allow main class to be non-public. (#883)
- Convert `-classpath` to `CLASSPATH` when forking on Windows and length exceeds a heuristic maximum. (#755)
- `scalacOptions` for `.scala` build definitions are now also used for `.sbt` files
- `error`, `warn`, `info`, `debug` commands to set log level and `--error`, ... to set the level before the project is loaded. (#806)
- `sLog` settings that provides a `Logger` for use by settings. (#806)
- Early commands: any command prefixed with `--` gets moved before other commands on startup and doesn't force sbt into batch mode.
- Deprecate internal `-`, `--`, and `---` commands in favor of `onFailure`, `sbtClearOnFailure`, and `resumeFromFailure`.
- `makePom` no longer generates `<type>` elements for standard classifiers. (#728)
- Fix many instances of the Turkish i bug.
- Read https+ftp proxy environment variables into system properties where Java will use them. (#886)
- The `Process` methods that are redirection-like no longer discard the exit code of the input. This addresses an inconsistency with `Fork`, where using the `CustomOutput OutputStrategy` makes the exit code always zero.
- Recover from failed `reload` command in the scripted sbt handler.
- Parse external `pom.xml` with `CustomPomParser` to handle multiple definitions. (#758)
- Improve key collision error message (#877)
- Display the source position of an undefined setting.
- Respect the `-nowarn` option when compiling Scala sources.
- Improve forked test debugging by listing tests run by sbt in debug output. (#868)
- Fix scaladoc cache to track changes to `-doc-root-content` (#837)
- Incremental compiler: Internal refactoring in preparation for name-hashing (#936)
- Incremental compiler: improved cache loading/saving speed by internal file names (#931)
- Docs: many contributed miscellaneous fixes and additions
- Docs: link to page source now at the bottom of the page
- Docs: sitemap now automatically generated
- Docs: custom `:key:` role enables links from a key name in the docs to the val in `sxr/sbt/Keys.scala`
- Docs: restore sxr support and fix links to sxr'd sources. (#863)

### sbt 0.13.0

#### Features, fixes, changes with compatibility implications

-   Moved to Scala 2.10 for sbt and build definitions.
-   Support for plugin configuration in `project/plugins/` has been
    removed. It was deprecated since 0.11.2.
-   Dropped support for tab completing the right side of a setting for
    the `set` command. The new task macros make this tab completion
    obsolete.
-   The convention for keys is now camelCase only. Details below.
-   Fixed the default classifier for tests to be `tests` for proper
    Maven compatibility.
-   The global settings and plugins directories are now versioned.
    Global settings go in `~/.sbt/0.13/` and global plugins in
    `~/.sbt/0.13/plugins/` by default. Explicit overrides, such as via
    the `sbt.global.base` system property, are still respected. (gh-735)
-   sbt no longer canonicalizes files passed to scalac. (gh-723)
-   sbt now enforces that each project must have a unique `target`
    directory.
-   sbt no longer overrides the Scala version in dependencies. This
    allows independent configurations to depend on different Scala
    versions and treats Scala dependencies other than scala-library as
    normal dependencies. However, it can result in resolved versions
    other than `scalaVersion` for those other Scala libraries.
-   JLine is now configured differently for Cygwin. See
    [Installing sbt][Setup].
-   Jline and Ansi codes work better on Windows now. CI servers might
    have to explicitly disable Ansi codes via `-Dsbt.log.format=false`.
-   JLine now tries to respect `~/.inputrc`.
-   Forked tests and runs now use the project's base directory as the
    current working directory.
-   `compileInputs` is now defined in `(Compile,compile)` instead of
    just `Compile`
-   The result of running tests is now
    [Tests.Output](../api/sbt/Tests\$\$Output.html).

#### Features

-   Use the repositories in boot.properties as the default project
    resolvers. Add `bootOnly` to a repository in boot.properties to
    specify that it should not be used by projects by default. (Josh S.,
    gh-608)
-   Support vals and defs in .sbt files. Details below.
-   Support defining Projects in .sbt files: vals of type Project are
    added to the Build. Details below.
-   New syntax for settings, tasks, and input tasks. Details below.
-   Automatically link to external API scaladocs of dependencies by
    setting `autoAPIMappings := true`. This requires at least Scala
    2.10.1 and for dependencies to define `apiURL` for their scaladoc
    location. Mappings may be manually added to the `apiMappings` task
    as well.
-   Support setting Scala home directory temporary using the switch
    command: `++ scala-version=/path/to/scala/home`. The scala-version
    part is optional, but is used as the version for any managed
    dependencies.
-   Add `publishM2` task for publishing to `~/.m2/repository`. (gh-485)
-   Use a default root project aggregating all projects if no root is
    defined. (gh-697)
-   New API for getting tasks and settings from multiple projects and
    configurations. See the new section
    [getting values from multiple scopes][multiple-scopes].
-   Enhanced test interface for better support of test framework
    features. (Details pending.)
-   `export` command

    > -   For tasks, prints the contents of the 'export' stream. By
    >     convention, this should be the equivalent command line(s)
    >     representation. compile, doc, and console show the approximate
    >     command lines for their execution. Classpath tasks print the
    >     classpath string suitable for passing as an option.
    > -   For settings, directly prints the value of a setting instead
    >     of going through the logger

#### Fixes

-   sbt no longer tries to warn on dependency conflicts. Configure a
    [conflict manager][conflict-management] instead. (gh-709)
-   Run test Cleanup and Setup when forking. The test ClassLoader is not
    available because it is in another jvm.

#### Improvements

-   Run the API extraction phase after the compiler's `pickler` phase
    instead of `typer` to allow compiler plugins after `typer`. (Adriaan
    M., gh-609)
-   Record defining source position of settings. `inspect` shows the
    definition location of all settings contributing to a defined value.
-   Allow the root project to be specified explicitly in
    `Build.rootProject`.
-   Tasks that need a directory for storing cache information can now
    use the `cacheDirectory` method on `streams`. This supersedes the
    `cacheDirectory` setting.
-   The environment variables used when forking `run` and `test` may be
    set via `envVars`, which is a `Task[Map[String,String]]`. (gh-665)
-   Restore class files after an unsuccessful compilation. This is
    useful when an error occurs in a later incremental step that
    requires a fix in the originally changed files.
-   Better auto-generated IDs for default projects. (gh-554)
-   Fork run directly with 'java' to avoid additional class loader from
    'scala' command. (gh-702)
-   Make autoCompilerPlugins support compiler plugins defined in a
    internal dependency (only if `exportJars := true` due to scalac
    limitations)
-   Track ancestors of non-private templates and use this information to
    require fewer, smaller intermediate incremental compilation steps.
-   `autoCompilerPlugins` now supports compiler plugins defined in a
    internal dependency. The plugin project must define
    `exportJars := true`. Depend on the plugin with
    `...dependsOn(... % Configurations.CompilerPlugin)`.
-   Add utilities for debugging API representation extracted by the
    incremental compiler. (Grzegorz K., gh-677, gh-793)
-   `consoleProject` unifies the syntax for getting the value of a
    setting and executing a task. See
    [Console Project](Console-Project.html).

#### Other

-   The source layout for the sbt project itself follows the package
    name to accommodate to Eclipse users. (Grzegorz K., gh-613)

### Details of major changes

#### camelCase Key names

The convention for key names is now camelCase only instead of camelCase
for Scala identifiers and hyphenated, lower-case on the command line.
camelCase is accepted for existing hyphenated key names and the
hyphenated form will still be accepted on the command line for those
existing tasks and settings declared with hyphenated names. Only
camelCase will be shown for tab completion, however.

#### New key definition methods

There are new methods that help avoid duplicating key names by declaring
keys as:

```scala
val myTask = taskKey[Int]("A (required) description of myTask.")
```

The name will be picked up from the val identifier by the implementation
of the taskKey macro so there is no reflection needed or runtime
overhead. Note that a description is mandatory and the method `taskKey`
begins with a lowercase `t`. Similar methods exist for keys for settings
and input tasks: `settingKey` and `inputKey`.

#### New task/setting syntax

First, the old syntax is still supported with the intention of allowing
conversion to the new syntax at your leisure. There may be some
incompatibilities and some may be unavoidable, but please report any
issues you have with an existing build.

The new syntax is implemented by making `:=`, `+=`, and `++=` macros and
making these the only required assignment methods. To refer to the value
of other settings or tasks, use the `value` method on settings and
tasks. This method is a stub that is removed at compile time by the
macro, which will translate the implementation of the task/setting to
the old syntax.

For example, the following declares a dependency on `scala-reflect`
using the value of the `scalaVersion` setting:

```scala
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
```

The `value` method is only allowed within a call to `:=`, `+=`, or
`++=`. To construct a setting or task outside of these methods, use
`Def.task` or `Def.setting`. For example,

```scala
val reflectDep = Def.setting { "org.scala-lang" % "scala-reflect" % scalaVersion.value }

libraryDependencies += reflectDep.value   
```

A similar method `parsed` is defined on `Parser[T]`,
`Initialize[Parser[T]]` (a setting that provides a parser), and
`Initialize[State => Parser[T]]` (a setting that uses the current
`State` to provide a `Parser[T]`. This method can be used when defining
an input task to get the result of user input.

```scala
myInputTask := {
     // Define the parser, which is the standard space-delimited arguments parser.
   val args = Def.spaceDelimited("<args>").parsed
     // Demonstrates using a setting value and a task result:
   println("Project name: " + name.value)
   println("Classpath: " + (fullClasspath in Compile).value.map(_.file))
   println("Arguments:")
   for(arg <- args) println("  " + arg)
}
```

For details, see [Input Tasks][Input-Tasks].

To expect a task to fail and get the failing exception, use the
`failure` method instead of `value`. This provides an `Incomplete`
value, which wraps the exception. To get the result of a task whether or
not it succeeds, use `result`, which provides a `Result[T]`.

Dynamic settings and tasks (`flatMap`) have been cleaned up. Use the
`Def.taskDyn` and `Def.settingDyn` methods to define them (better name
suggestions welcome). These methods expect the result to be a task and
setting, respectively.

#### .sbt format enhancements

vals and defs are now allowed in .sbt files. They must follow the same
rules as settings concerning blank lines, although multiple definitions
may be grouped together. For example,

```scala
val n = "widgets"
val o = "org.example"

name := n

organization := o
```

All definitions are compiled before settings, but it will probably be
best practice to put definitions together. Currently, the visibility of
definitions is restricted to the .sbt file it is defined in. They are
not visible in `consoleProject` or the `set` command at this time,
either. Use Scala files in `project/` for visibility in all .sbt files.

vals of type `Project` are added to the `Build` so that multi-project
builds can be defined entirely in .sbt files now. For example,

```scala
lazy val a = Project("a", file("a")).dependsOn(b)

lazy val b = Project("b", file("sub")).settings(
   version := "1.0"
)
```

Currently, it only makes sense to defines these in the root project's
.sbt files.

A shorthand for defining Projects is provided by a new macro called
`project`. This requires the constructed Project to be directly assigned
to a `val`. The name of this val is used for the project ID and base
directory. The base directory can be changed with the `in` method. The
previous example can also be written as:

```scala
lazy val a = project.dependsOn(b)

lazy val b = project in file("sub") settings(
  version := "1.0"
)
```

This macro is also available for use in Scala files.

#### Control over automatically added settings

sbt loads settings from a few places in addition to the settings
explicitly defined by the `Project.settings` field. These include
plugins, global settings, and .sbt files. The new `Project.autoSettings`
method configures these sources: whether to include them for the project
and in what order.

`Project.autoSettings` accepts a sequence of values of type
`AddSettings`. Instances of `AddSettings` are constructed from methods
in the `AddSettings` companion object. The configurable settings are
per-user settings (from `~/.sbt`, for example), settings from .sbt files,
and plugin settings (project-level only). The order in which these
instances are provided to `autoSettings` determines the order in which
they are appended to the settings explicitly provided in
`Project.settings`.

For .sbt files, `AddSettings.defaultSbtFiles` adds the settings from all
.sbt files in the project's base directory as usual. The alternative
method `AddSettings.sbtFiles` accepts a sequence of `Files` that will be
loaded according to the standard .sbt format. Relative files are
resolved against the project's base directory.

Plugin settings may be included on a per-Plugin basis by using the
`AddSettings.plugins` method and passing a `Plugin => Boolean`. The
settings controlled here are only the automatic per-project settings.
Per-build and global settings will always be included. Settings that
plugins require to be manually added still need to be added manually.

For example,

```scala
import AddSettings._

lazy val root = Project("root", file(".")) autoSettings(
   userSettings, allPlugins, sbtFiles(file("explicit/a.txt"))
)

lazy val sub = Project("sub", file("Sub")) autoSettings(
   defaultSbtFiles, plugins(includePlugin)
)

def includePlugin(p: Plugin): Boolean =
   p.getClass.getName.startsWith("org.example.")
```

#### Resolving Scala dependencies

Scala dependencies (like scala-library and scala-compiler) are now
resolved via the normal `update` task. This means:

> 1.  Scala jars won't be copied to the boot directory, except for those
>     needed to run sbt.
> 2.  Scala SNAPSHOTs behave like normal SNAPSHOTs. In particular,
>     running update will properly re-resolve the dynamic revision.
> 3.  Scala jars are resolved using the same repositories and
>     configuration as other dependencies.
> 4.  Scala dependencies are not resolved via update when scalaHome is
>     set, but are instead obtained from the configured directory.
> 5.  The Scala version for sbt will still be resolved via the
>     repositories configured for the launcher.

sbt still needs access to the compiler and its dependencies in order to
run `compile`, `console`, and other Scala-based tasks. So, the Scala
compiler jar and dependencies (like scala-reflect.jar and
scala-library.jar) are defined and resolved in the `scala-tool`
configuration (unless `scalaHome` is defined). By default, this
configuration and the dependencies in it are automatically added by sbt.
This occurs even when dependencies are configured in a `pom.xml` or
`ivy.xml` and so it means that the version of Scala defined for your
project must be resolvable by the resolvers configured for your project.

If you need to manually configure where sbt gets the Scala compiler and
library used for compilation, the REPL, and other Scala tasks, do one of
the following:

> 1.  Set scalaHome to use the existing Scala jars in a specific
>     directory. If autoScalaLibrary is true, the library jar found here
>     will be added to the (unmanaged) classpath.
> 2.  Set managedScalaInstance := false and explicitly define
>     scalaInstance, which is of type ScalaInstance. This defines the
>     compiler, library, and other jars comprising Scala. If
>     autoScalaLibrary is true, the library jar from the defined
>     ScalaInstance will be added to the (unmanaged) classpath.

The [Configuring Scala](Configuring-Scala.html) page provides full details.

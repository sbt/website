---
out: ChangeSummary_0.12.0.html
---

  [Cross-Build]: Cross-Build.html
  [Parallel-Execution]: Parallel-Execution.html
  [Sbt-Launcher]: Sbt-Launcher.html

## sbt 0.12.4

-   Work around URI problems with encoding and resolving. (gh-725)
-   Allow -cp argument to `apply` command to be quoted. (gh-724)
-   Make `sbtBinaryVersion` use the new approach for 0.13 and later to
    support cross-building plugins.
-   Pull `sbtDependency` version from `sbtVersion` to facilitate
    cross-building plugins.
-   Proper support for stashing on-failure handlers. (gh-732)
-   Include files with zip extension in unmanaged jars. (gh-750)
-   Only add automatically detected plugins to options once. (gh-757)
-   Properly handle failure in a multi-command that includes `reload`.
    (gh-732)
-   Fix unsynchronized caching of Scala class loaders that could result
    in Scala classes being loaded in multiple class loaders.
-   Incremental compiler: remove resident compiler code (wasn't used and
    was a compatibility liability)
-   Incremental compiler: properly track `abstract override` modifier.
    (gh-726)
-   Incremental compiler: do not normalize types in the API extraction
    phase. (gh-736)
-   Ivy cache: account for `localOnly` when cache subclass overrides
    `isChanging`
-   Ivy cache: fix corruption when developing sbt or sbt plugins.
    (gh-768)
-   Ivy cache: invalidate when artifact download fails to avoid locking
    into bad resolver. (gh-760)
-   Ivy cache: use publication date from metadata instead of original
    file's last modified time when deleting out of date artifacts.
    (gh-764)

## sbt 0.12.3

-   Allow `cleanKeepFiles` to contain directories
-   Disable Ivy debug-level logging for performance. (gh-635)
-   Invalidate artifacts not recorded in the original metadata when a
    module marked as changing changes. (gh-637, gh-641)
-   Ivy Artifact needs wildcard configuration added if no explicit ones
    are defined. (gh-439)
-   Right precedence of sbt.boot.properties lookup, handle qualifier
    correctly. (gh-651)
-   Mark the tests failed exception as having already provided feedback.
-   Handle exceptions not caught by the test framework when forking.
    (gh-653)
-   Support `reload plugins` after ignoring a failure to load a project.
-   Workaround for os deadlock detection at the process level. (gh-650)
-   Fix for dependency on class file corresponding to a package.
    (Grzegorz K., gh-620)
-   Fix incremental compilation problem with package objects inheriting
    from invalidated sources in a subpackage.
-   Use Ivy's default name for the resolution report so that links to
    other configurations work.
-   Include jars from java.ext.dirs in incremental classpath. (gh-678)
-   Multi-line prompt text offset issue (Jibbers42, gh-625)
-   Added `xml:space="preserve"` attribute to extraDependencyAttributes
    XML Block for publishing poms for plugins dependent on other plugins
    (Brendan M., gh-645)
-   Tag the actual test task and not a later task. (gh-692)
-   Make exclude-classifiers per-user instead of per-build. (gh-634)
-   Load global plugins in their own class loader and replace the base
    loader with that. (gh-272)
-   Demote the default conflict warnings to the debug level. These will
    be removed completely in 0.13. (gh-709)
-   Fix Ivy cache issues when multiple resolvers are involved. (gh-704)

## sbt 0.12.2

-   Support -Yrangepos. (Lex S., gh-607)
-   Only make one call to test frameworks per test name. (gh-520)
-   Add `-cp` option to the `apply` method to make adding commands from
    an external program easier.
-   Stable representation of refinement typerefs. This fixes unnecessary
    recompilations in some cases. (Adriaan M., gh-610)
-   Disable aggregation for `run-main`. (gh-606)
-   Concurrent restrictions: Untagged should be set based on the task's
    tags, not the tags of all tasks.
-   When preserving the last modified time of files, convert negative
    values to 0
-   Use `java.lang.Throwable.setStackTrace` when sending exceptions back
    from forked tests. (Eugene V., gh-543)
-   Don't merge dependencies with mismatched transitive/force/changing
    values. (gh-582)
-   Filter out null parent files when deleting empty directories.
    (Eugene V., gh-589)
-   Work around File constructor not accepting URIs for UNC paths.
    (gh-564)
-   Split ForkTests react() out to workaround SI-6526 (avoids a
    stackoverflow in some forked test situations)
-   Maven-style ivy repo support in the launcher config (Eric B.,
    gh-585)
-   Compare external binaries with canonical files (nau, gh-584)
-   Call System.exit after the main thread is finished. (Eugene V.,
    gh-565)
-   Abort running tests on the first failure to communicate results back
    to the main process. (Eugene V., gh-557)
-   Don't let the right side of the alias command fail the parse.
    (gh-572)
-   API extraction: handle any type that is annotated, not just the
    spec'd simple type. (gh-559)
-   Don't try to look up the class file for a package. (gh-620)

## sbt 0.12.1

### Dependency management fixes:

-   Merge multiple dependency definitions for the same ID. Workaround
    for gh-468, gh-285, gh-419, gh-480.
-   Don't write section of pom if scope is 'compile'.
-   Ability to properly match on artifact type. Fixes gh-507 (Thomas).
-   Force `update` to run on changes to last modified time of artifacts
    or cached descriptor (part of fix for gh-532). It may also fix
    issues when working with multiple local projects via 'publish-local'
    and binary dependencies.
-   Per-project resolution cache that deletes cached files before
    update. Notes:

> -   The resolution cache differs from the repository cache and does
>     not contain dependency metadata or artifacts.
> -   The resolution cache contains the generated ivy files, properties,
>     and resolve reports for the project.
> -   There will no longer be individual files directly in
>     `~/.ivy2/cache/`
> -   Resolve reports are now in target/resolution-cache/reports/,
>     viewable with a browser.
> -   Cache location includes extra attributes so that cross builds of a
>     plugin do not overwrite each other. Fixes gh-532.

### Three stage incremental compilation:

-   As before, the first step recompiles sources that were edited (or
    otherwise directly invalidated).
-   The second step recompiles sources from the first step whose API has
    changed, their direct dependencies, and sources forming a cycle with
    these sources.
-   The third step recompiles transitive dependencies of sources from
    the second step whose API changed.
-   Code relying mainly on composition should see decreased compilation
    times with this approach.
-   Code with deep inheritance hierarchies and large cycles between
    sources may take longer to compile.
-   `last compile` will show cycles that were processed in step 2.
    Reducing large cycles of sources shown here may decrease compile
    times.

### Miscellaneous fixes and improvements:

-   Various test forking fixes. Fixes gh-512, gh-515.
-   Proper isolation of build definition classes. Fixes gh-536, gh-511.
-   `orbit` packaging should be handled like a standard jar. Fixes
    gh-499.
-   In `IO.copyFile`, limit maximum size transferred via NIO. Fixes
    gh-491.
-   Add OSX JNI library extension in `includeFilter` by default. Fixes
    gh-500. (Indrajit)
-   Translate `show x y` into `;show x ;show y` . Fixes gh-495.
-   Clean up temporary directory on exit. Fixes gh-502.
-   `set` prints the scopes+keys it defines and affects.
-   Tab completion for `set` (experimental).
-   Report file name when an error occurs while opening a corrupt zip
    file in incremental compilation code. (James)
-   Defer opening logging output files until an actual write. Helps
    reduce number of open file descriptors.
-   Back all console loggers by a common console interface that merges
    (overwrites) consecutive Resolving xxxx ... lines when ansi codes
    are enabled (as first done by Play).

### Forward-compatible-only change (not present in 0.12.0):

-   `sourcesInBase` setting controls whether sources in base directory
    are included. Fixes gh-494.

## sbt 0.12.0

#### Features, fixes, changes with compatibility implications

-   The cross versioning convention has changed for Scala versions 2.10
    and later as well as for sbt plugins.
-   When invoked directly, 'update' will always perform an update
    (gh-335)
-   The sbt plugins repository is added by default for plugins and
    plugin definitions. gh-380
-   Plugin configuration directory precedence has changed (see details
    section below)
-   Source dependencies have been fixed, but the fix required changes
    (see details section below)
-   Aggregation has changed to be more flexible (see details section
    below)
-   Task axis syntax has changed from key(for task) to task::key (see
    details section below)
-   The organization for sbt has to changed to `org.scala-sbt` (was:
    org.scala-tools.sbt). This affects users of the scripted plugin in
    particular.
-   `artifactName` type has changed to
    `(ScalaVersion, Artifact, ModuleID) => String`
-   `javacOptions` is now a task
-   `session save` overwrites settings in `build.sbt` (when
    appropriate). gh-369
-   scala-library.jar is now required to be on the classpath in order to
    compile Scala code. See the scala-library.jar section at the bottom
    of the page for details.

#### Features

-   Support for forking tests (gh-415)
-   `test-quick` (see details section below)
-   Support globally overriding repositories (gh-472)
-   Added `print-warnings` task that will print unchecked and
    deprecation warnings from the previous compilation without needing
    to recompile (Scala 2.10+ only)
-   Support for loading an ivy settings file from a URL.
-   `projects add/remove <URI>` for temporarily working with other
    builds
-   Enhanced control over parallel execution (see details section below)
-   `inspect tree <key>` for calling `inspect` command recursively
    (gh-274)

#### Fixes

-   Delete a symlink and not its contents when recursively deleting a
    directory.
-   Fix detection of ancestors for java sources
-   Fix the resolvers used for `update-sbt-classifiers` (gh-304)
-   Fix auto-imports of plugins (gh-412)
-   Argument quoting (see details section below)
-   Properly reset JLine after being stopped by Ctrl+z (unix only).
    gh-394

#### Improvements

-   The launcher can launch all released sbt versions back to 0.7.0.
-   A more refined hint to run 'last' is given when a stack trace is
    suppressed.
-   Use java 7 Redirect.INHERIT to inherit input stream of subprocess
    (gh-462,gh-327). This should fix issues when forking interactive
    programs. (@vigdorchik)
-   Mirror ivy 'force' attribute (gh-361)
-   Various improvements to `help` and `tasks` commands as well as new
    settings command (gh-315)
-   Bump jsch version to 0.1.46. (gh-403)
-   Improved help commands: `help`, `tasks`, `settings`.
-   Bump to JLine 1.0 (see details section below)
-   Global repository setting (see details section below)
-   Other fixes/improvements: gh-368, gh-377, gh-378, gh-386, gh-387,
    gh-388, gh-389

#### Experimental or In-progress

-   API for embedding incremental compilation. This interface is subject
    to change, but already being used in [a branch of the
    scala-maven-plugin](https://github.com/davidB/scala-maven-plugin/tree/feature/sbt-inc).
-   Experimental support for keeping the Scala compiler resident. Enable
    by passing -Dsbt.resident.limit=n to sbt, where n is an integer
    indicating the maximum number of compilers to keep around.
-   The [Howto pages](Howto.html) on the
    [new site](https://www.scala-sbt.org) are at least readable now. There
    is more content to write and more formatting improvements are
    needed, so [pull requests are
    welcome](https://github.com/sbt/sbt.github.com).

### Details of major changes from 0.11.2 to 0.12.0

#### Plugin configuration directory

In 0.11.0, plugin configuration moved from `project/plugins/` to just
`project/`, with `project/plugins/` being deprecated. Only 0.11.2 had a
deprecation message, but in all of 0.11.x, the presence of the old style
`project/plugins/` directory took precedence over the new style. In
0.12.0, the new style takes precedence. Support for the old style won't
be removed until 0.13.0.

1.  Ideally, a project should ensure there is never a conflict. Both
    styles are still supported; only the behavior when there is a
    conflict has changed.
2.  In practice, switching from an older branch of a project to a new
    branch would often leave an empty project/plugins/ directory that
    would cause the old style to be used, despite there being no
    configuration there.
3.  Therefore, the intention is that this change is strictly an
    improvement for projects transitioning to the new style and isn't
    noticed by other projects.

#### Parsing task axis

There is an important change related to parsing the task axis for
settings and tasks that fixes gh-202

1.  The syntax before 0.12 has been `{build}project/config:key(for task)`
2.  The proposed (and implemented) change for 0.12 is
    `{build}project/config:task::key`
3.  By moving the task axis before the key, it allows for easier
    discovery (via tab completion) of keys in plugins.
4.  It is not planned to support the old syntax.

#### Aggregation

Aggregation has been made more flexible. This is along the direction
that has been previously discussed on the mailing list.

1.  Before 0.12, a setting was parsed according to the current project
    and only the exact setting parsed was aggregated.
2.  Also, tab completion did not account for aggregation.
3.  This meant that if the setting/task didn't exist on the current
    project, parsing failed even if an aggregated project contained the
    setting/task.
4.  Additionally, if compile:package existed for the current project,
    `*:package` existed for an aggregated project, and the user requested
    'package' to run (without specifying the configuration), `*:package`
    wouldn't be run on the aggregated project (because it isn't the same
    as the `compile:package` key that existed on the current project).
5.  In 0.12, both of these situations result in the aggregated settings
    being selected. For example,
    1.  Consider a project `root` that aggregates a subproject `sub`.
    2.  `root` defines `*:package`.
    3.  `sub` defines `compile:package` and `compile:compile`.
    4.  Running `root/package` will run `root/*:package` and
        `sub/compile:package`
    5.  Running `root/compile` will run `sub/compile:compile`
6.  This change was made possible in part by the change to task axis
    parsing.

#### Parallel Execution

Fine control over parallel execution is supported as described here:
[Parallel Execution][Parallel-Execution].

1.  The default behavior should be the same as before, including the
    parallelExecution settings.
2.  The new capabilities of the system should otherwise be considered
    experimental.
3.  Therefore, `parallelExecution` won't be deprecated at this time.

#### Source dependencies

A fix for issue gh-329 is included in 0.12.0. This fix ensures that only
one version of a plugin is loaded across all projects. There are two
parts to this.

1.  The version of a plugin is fixed by the first build to load it. In
    particular, the plugin version used in the root build (the one in
    which sbt is started in) always overrides the version used in
    dependencies.
2.  Plugins from all builds are loaded in the same class loader.

Additionally, Sanjin's patches to add support for hg and svn URIs are
included.

1.  sbt uses Subversion to retrieve URIs beginning with `svn` or
    svn+ssh. An optional fragment identifies a specific revision to
    checkout.
2.  Because a URI for Mercurial doesn't have a Mercurial-specific
    scheme, sbt requires the URI to be prefixed with hg: to identify it
    as a Mercurial repository.
3.  Also, URIs that end with `.git` are now handled properly.

#### Cross building

The cross version suffix is shortened to only include the major and
minor version for Scala versions starting with the 2.10 series and for
sbt versions starting with the 0.12 series. For example, `sbinary_2.10`
for a normal library or `sbt-plugin_2.10_0.12` for an sbt plugin. This
requires forward and backward binary compatibility across incremental
releases for both Scala and sbt.

1.  This change has been a long time coming, but it requires everyone
    publishing an open source project to switch to 0.12 to publish for
    2.10 or adjust the cross versioned prefix in their builds
    appropriately.
2.  Obviously, using 0.12 to publish a library for 2.10 requires 0.12.0
    to be released before projects publish for 2.10.
3.  There is now the concept of a binary version. This is a subset of
    the full version string that represents binary compatibility. That
    is, equal binary versions implies binary compatibility. All Scala
    versions prior to 2.10 use the full version for the binary version
    to reflect previous sbt behavior. For 2.10 and later, the binary
    version is `<major>.<minor>`.
4.  The cross version behavior for published artifacts is configured by
    the crossVersion setting. It can be configured for dependencies by
    using the cross method on ModuleID or by the traditional %%
    dependency construction variant. By default, a dependency has cross
    versioning disabled when constructed with a single % and uses the
    binary Scala version when constructed with %%.
5.  The artifactName function now accepts a type ScalaVersion as its
    first argument instead of a String. The full type is now
    (ScalaVersion, ModuleID, Artifact) => String. ScalaVersion contains
    both the full Scala version (such as 2.10.0) as well as the binary
    Scala version (such as 2.10).
6.  The flexible version mapping added by Indrajit has been merged into
    the cross method and the %% variants accepting more than one
    argument have been deprecated. See [Cross Build][Cross-Build] for
    details.

#### Global repository setting

Define the repositories to use by putting a standalone `[repositories]`
section (see the [sbt Launcher][Sbt-Launcher] page) in
`~/.sbt/repositories` and pass `-Dsbt.override.build.repos=true` to sbt.
Only the repositories in that file will be used by the launcher for
retrieving sbt and Scala and by sbt when retrieving project
dependencies. (@jsuereth)

#### test-quick

`test-quick` (gh-393) runs the tests specified as arguments (or all
tests if no arguments are given) that:

1.  have not been run yet OR
2.  failed the last time they were run OR
3.  had any transitive dependencies recompiled since the last successful
    run

#### Argument quoting

Argument quoting (gh-396) from the intereactive mode works like Scala
string literals.

1.  `> command "arg with spaces,\\n escapes interpreted"`
2.  `> command """arg with spaces,\\n escapes not interpreted"""`
3.  For the first variant, note that paths on Windows use backslashes
    and need to be escaped (\\\\). Alternatively, use the second
    variant, which does not interpret escapes.
4.  For using either variant in batch mode, note that a shell will
    generally require the double quotes themselves to be escaped.

### scala-library.jar

sbt versions prior to 0.12.0 provided the location of scala-library.jar
to scalac even if scala-library.jar wasn't on the classpath. This
allowed compiling Scala code without scala-library as a dependency, for
example, but this was a misfeature. Instead, the Scala library should be
declared as `provided`:

```scala
// Don't automatically add the scala-library dependency
// in the 'compile' configuration
autoScalaLibrary := false

libraryDependencies += "org.scala-lang" % "scala-library" % "2.9.2" % "provided"
```

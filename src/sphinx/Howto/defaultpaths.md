Customizing paths
=================

This page describes how to modify the default source, resource, and
library directories and what files get included from them.

The directory that contains the main Scala sources is by default
`src/main/scala`. For test Scala sources, it is `src/test/scala`. To
change this, modify `scalaSource` in the `Compile` (for main sources) or
`Test` (for test sources). For example, :

    scalaSource in Compile := baseDirectory.value / "src"

    scalaSource in Test := baseDirectory.value / "test-src"

> **note**
>
> The Scala source directory can be the same as the Java source
> directory.

The directory that contains the main Java sources is by default
`src/main/java`. For test Java sources, it is `src/test/java`. To change
this, modify `javaSource` in the `Compile` (for main sources) or `Test`
(for test sources).

For example, :

    javaSource in Compile := baseDirectory.value / "src"

    javaSource in Test := baseDirectory.value / "test-src"

> **note**
>
> The Scala source directory can be the same as the Java source
> directory.

The directory that contains the main resources is by default
`src/main/resources`. For test resources, it is `src/test/resources`. To
change this, modify `resourceDirectory` in either the `Compile` or
`Test` configuration.

For example, :

    resourceDirectory in Compile := baseDirectory.value / "resources"

    resourceDirectory in Test := baseDirectory.value / "test-resources"

The directory that contains the unmanaged libraries is by default
`lib/`. To change this, modify `unmanagedBase`. This setting can be
changed at the project level or in the `Compile`, `Runtime`, or `Test`
configurations.

When defined without a configuration, the directory is the default
directory for all configurations. For example, the following declares
`jars/` as containing libraries: :

    unmanagedBase := baseDirectory.value / "jars"

When set for `Compile`, `Runtime`, or `Test`, `unmanagedBase` is the
directory containing libraries for that configuration, overriding the
default. For example, the following declares `lib/main/` to contain jars
only for `Compile` and not for running or testing: :

    unmanagedBase in Compile := baseDirectory.value / "lib" / "main"

By default, sbt includes `.scala` files from the project's base
directory as main source files. To disable this, configure
`sourcesInBase`: :

    sourcesInBase := false

sbt collects `sources` from `unmanagedSourceDirectories`, which by
default consists of `scalaSource` and `javaSource`. Add a directory to
`unmanagedSourceDirectories` in the appropriate configuration to add a
source directory. For example, to add `extra-src` to be an additional
directory containing main sources, :

    unmanagedSourceDirectories in Compile += baseDirectory.value / "extra-src"

> **note**
>
> This directory should only contain unmanaged sources, which are
> sources that are manually created and managed. See
> /Howto/generatefiles for working with automatically generated sources.

sbt collects `resources` from `unmanagedResourceDirectories`, which by
default consists of `resourceDirectory`. Add a directory to
`unmanagedResourceDirectories` in the appropriate configuration to add
another resource directory. For example, to add `extra-resources` to be
an additional directory containing main resources, :

    unmanagedResourceDirectories in Compile += baseDirectory.value / "extra-resources"

> **note**
>
> This directory should only contain unmanaged resources, which are
> resources that are manually created and managed. See
> /Howto/generatefiles for working with automatically generated
> resources.

When sbt traverses `unmanagedSourceDirectories` for sources, it only
includes directories and files that match `includeFilter` and do not
match `excludeFilter`. `includeFilter` and `excludeFilter` have type
`java.io.FileFilter` and sbt
`provides some useful combinators <file-filter>` for constructing a
`FileFilter`. For example, in addition to the default hidden files
exclusion, the following also ignores files containing `impl` in their
name, :

    excludeFilter in unmanagedSources := HiddenFileFilter || "*impl*"

To have different filters for main and test libraries, configure
`Compile` and `Test` separately: :

    includeFilter in (Compile, unmanagedSources) := "*.scala" || "*.java"

    includeFilter in (Test, unmanagedSources) := HiddenFileFilter || "*impl*"

> **note**
>
> By default, sbt includes .scala and .java sources, excluding hidden
> files.

When sbt traverses `unmanagedResourceDirectories` for resources, it only
includes directories and files that match `includeFilter` and do not
match `excludeFilter`. `includeFilter` and `excludeFilter` have type
`java.io.FileFilter` and sbt
`provides some useful combinators <file-filter>` for constructing a
`FileFilter`. For example, in addition to the default hidden files
exclusion, the following also ignores files containing `impl` in their
name, :

    excludeFilter in unmanagedSources := HiddenFileFilter || "*impl*"

To have different filters for main and test libraries, configure
`Compile` and `Test` separately: :

    includeFilter in (Compile, unmanagedSources) := "*.txt"

    includeFilter in (Test, unmanagedSources) := "*.html"

> **note**
>
> By default, sbt includes all files that are not hidden.

When sbt traverses `unmanagedBase` for resources, it only includes
directories and files that match `includeFilter` and do not match
`excludeFilter`. `includeFilter` and `excludeFilter` have type
`java.io.FileFilter` and sbt
`provides some useful combinators <file-filter>` for constructing a
`FileFilter`. For example, in addition to the default hidden files
exclusion, the following also ignores zips, :

    excludeFilter in unmanagedJars := HiddenFileFilter || "*.zip"

To have different filters for main and test libraries, configure
`Compile` and `Test` separately: :

    includeFilter in (Compile, unmanagedJars) := "*.jar"

    includeFilter in (Test, unmanagedJars) := "*.jar" || "*.zip"

> **note**
>
> By default, sbt includes jars, zips, and native dynamic libraries,
> excluding hidden files.

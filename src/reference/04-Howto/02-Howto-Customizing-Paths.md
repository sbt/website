---
out: Howto-Customizing-Paths.html
---

  [file-filter]: Paths.html#file-filter
  [Howto-Generating-Files]: Howto-Generating-Files.html

Customizing paths
-----------------

This page describes how to modify the default source, resource, and
library directories and what files get included from them.

<a name="scala-source-directory"></a>

### Change the default Scala source directory

The directory that contains the main Scala sources is by default
`src/main/scala`. For test Scala sources, it is `src/test/scala`. To
change this, modify `scalaSource` in the `Compile` (for main sources) or
`Test` (for test sources). For example,

```scala
Compile / scalaSource := baseDirectory.value / "src"

Test / scalaSource := baseDirectory.value / "test-src"
```

> **Note**: The Scala source directory can be the same as the Java source
> directory.

<a name="java-source-directory"></a>

### Change the default Java source directory

The directory that contains the main Java sources is by default
`src/main/java`. For test Java sources, it is `src/test/java`. To change
this, modify `javaSource` in the `Compile` (for main sources) or `Test`
(for test sources).

For example,

```scala
Compile / javaSource := baseDirectory.value / "src"

Test / javaSource := baseDirectory.value / "test-src"
```

> **Note**: The Scala source directory can be the same as the Java source
> directory.

<a name="resource-directory"></a>

### Change the default resource directory

The directory that contains the main resources is by default
`src/main/resources`. For test resources, it is `src/test/resources`. To
change this, modify `resourceDirectory` in either the `Compile` or
`Test` configuration.

For example,

```scala
Compile / resourceDirectory := baseDirectory.value / "resources"

Test / resourceDirectory := baseDirectory.value / "test-resources"
```

<a name="unmanaged-base-directory"></a>

### Change the default (unmanaged) library directory

The directory that contains the unmanaged libraries is by default
`lib/`. To change this, modify `unmanagedBase`. This setting can be
changed at the project level or in the `Compile`, `Runtime`, or `Test`
configurations.

When defined without a configuration, the directory is the default
directory for all configurations. For example, the following declares
`jars/` as containing libraries:

```scala
unmanagedBase := baseDirectory.value / "jars"
```

When set for `Compile`, `Runtime`, or `Test`, `unmanagedBase` is the
directory containing libraries for that configuration, overriding the
default. For example, the following declares `lib/main/` to contain jars
only for `Compile` and not for running or testing:

```scala
Compile / unmanagedBase := baseDirectory.value / "lib" / "main"
```

<a name="disable-base-sources"></a>

### Disable using the project's base directory as a source directory

By default, sbt includes `.scala` files from the project's base
directory as main source files. To disable this, configure
`sourcesInBase`:

```scala
sourcesInBase := false
```

<a name="add-source-directory"></a>

### Add an additional source directory

sbt collects `sources` from `unmanagedSourceDirectories`, which by
default consists of `scalaSource` and `javaSource`. Add a directory to
`unmanagedSourceDirectories` in the appropriate configuration to add a
source directory. For example, to add `extra-src` to be an additional
directory containing main sources,

```scala
Compile / unmanagedSourceDirectories += baseDirectory.value / "extra-src"
```

> **Note**: This directory should only contain unmanaged sources, which are
> sources that are manually created and managed. See
> [Generating Files][Howto-Generating-Files] for working with automatically generated sources.

<a name="add-resource-directory"></a>

### Add an additional resource directory

sbt collects `resources` from `unmanagedResourceDirectories`, which by
default consists of `resourceDirectory`. Add a directory to
`unmanagedResourceDirectories` in the appropriate configuration to add
another resource directory. For example, to add `extra-resources` to be
an additional directory containing main resources,

```scala
Compile / unmanagedResourceDirectories += baseDirectory.value / "extra-resources"
```

> **Note**: This directory should only contain unmanaged resources, which are
> resources that are manually created and managed. See
> [Generating Files][Howto-Generating-Files] for working with automatically generated
> resources.

<a name="source-include-filter"></a>

### Include/exclude files in the source directory

When sbt traverses `unmanagedSourceDirectories` for sources, it only
includes directories and files that match `includeFilter` and do not
match `excludeFilter`. `includeFilter` and `excludeFilter` have type
`java.io.FileFilter` and sbt
[provides some useful combinators][file-filter] for constructing a
`FileFilter`. For example, in addition to the default hidden files
exclusion, the following also ignores files containing `impl` in their
name,

```scala
unmanagedSources / excludeFilter := HiddenFileFilter || "*impl*"
```

To have different filters for main and test libraries, configure
`Compile` and `Test` separately:

```scala
Compile / unmanagedSources / includeFilter := "*.scala" || "*.java"
Test / unmanagedSources / includeFilter := HiddenFileFilter || "*impl*"
```

> **Note**: By default, sbt includes `.scala` and `.java` sources, excluding hidden
> files.

<a name="resource-include-filter"></a>

### Include/exclude files in the resource directory

When sbt traverses `unmanagedResourceDirectories` for resources, it only
includes directories and files that match `includeFilter` and do not
match `excludeFilter`. `includeFilter` and `excludeFilter` have type
`java.io.FileFilter` and sbt
[provides some useful combinators][file-filter] for constructing a
`FileFilter`. For example, in addition to the default hidden files
exclusion, the following also ignores files containing `impl` in their
name,

```scala
unmanagedResources / excludeFilter := HiddenFileFilter || "*impl*"
```

To have different filters for main and test libraries, configure
`Compile` and `Test` separately:

```scala
Compile / unmanagedResources / includeFilter := "*.txt"
Test / unmanagedResources / includeFilter := "*.html"
```

> **Note**: By default, sbt includes all files that are not hidden.

<a name="lib-include-filter"></a>

### Include only certain (unmanaged) libraries

When sbt traverses `unmanagedBase` for resources, it only includes
directories and files that match `includeFilter` and do not match
`excludeFilter`. `includeFilter` and `excludeFilter` have type
`java.io.FileFilter` and sbt
[provides some useful combinators][file-filter] for constructing a
`FileFilter`. For example, in addition to the default hidden files
exclusion, the following also ignores zips,

```scala
unmanagedJars / excludeFilter := HiddenFileFilter || "*.zip"
```

To have different filters for main and test libraries, configure
`Compile` and `Test` separately:

```scala
Compile / unmanagedJars / includeFilter := "*.jar"
Test / unmanagedJars / includeFilter := "*.jar" || "*.zip"
```

> **Note**: By default, sbt includes jars, zips, and native dynamic libraries,
> excluding hidden files.

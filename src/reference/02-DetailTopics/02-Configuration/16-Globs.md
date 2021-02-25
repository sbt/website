---
out: Globs.html
---
  [Paths]: Paths.html#path-finder

Globs
-----

sbt 1.3.0 introduces the `Glob` type which can be used to specify a file system
query. The design is inspired by shell
[globs](https://en.wikipedia.org/wiki/Glob_%28programming%29). `Glob` has
only one public method, `matches(java.nio.file.Path)`, that can be used to
check if a path matches the glob pattern.

### Constructing Globs

Globs can be constructed explicitly or using a dsl that uses the `/` operator to
extend queries. In all of the examples provided, we use `java.nio.file.Path`,
but `java.io.File` may also be used.

The simplest Glob represents a single path. Explicitly create a single path glob
with:

```scala
val glob = Glob(Paths.get("foo/bar"))
println(glob.matches(Paths.get("foo"))) // prints false
println(glob.matches(Paths.get("foo/bar"))) // prints true
println(glob.matches(Paths.get("foo/bar/baz"))) // prints false
```
It can also be created using the glob dsl with:

```scala
val glob = Paths.get("foo/bar").toGlob
```

There are two special glob objects:
1) `AnyPath` (aliased by `*`) matches any path with just one name component
2) `RecursiveGlob` (aliased by `**`) matches all paths

Using `AnyPath`, we can explicitly construct a glob that matches all children of
a directory:

```scala
val path = Paths.get("/foo/bar")
val children = Glob(path, AnyPath)
println(children.matches(path)) // prints false
println(children.matches(path.resolve("baz")) // prints true
println(children.matches(path.resolve("baz").resolve("buzz") // prints false
```

Using the dsl, the above becomes:

```scala
val children    = Paths.get("/foo/bar").toGlob / AnyPath
val dslChildren = Paths.get("/foo/bar").toGlob / *
// these two definitions have identical results
```

Recursive globs are similar:

```scala
val path = Paths.get("/foo/bar")
val allDescendants = Glob(path, RescursiveGlob)
println(allDescendants.matches(path)) // prints false
println(allDescendants.matches(path.resolve("baz")) // prints true
println(allDescendants.matches(path.resolve("baz").resolve("buzz") // prints true
```

or

```scala
val allDescendants = Paths.get("/foo/bar").toGlob / **
```

### Path names

Globs may also be constructed using path names. The following three globs are
equivalent:

```scala
val pathGlob = Paths.get("foo").resolve("bar")
val glob = Glob("foo/bar")
val altGlob = Glob("foo") / "bar"
```

When parsing glob paths, any `/` characters are automatically converted to `\\`
on windows.

### Filters

Globs can apply name filters at each path level. For example,

```scala
val scalaSources = Paths.get("/foo/bar").toGlob / ** / "src" / "*.scala"
```
specifies all of the descendants of `/foo/bar` that have the `scala` file
extension whose parent directory is named `src`.

More advanced queries are also possible:

```scala
val scalaAndJavaSources =
  Paths.get("/foo/bar").toGlob / ** / "src" / "*.{scala,java}"
```

### Depth

The `AnyPath` special glob can be used to control the depth of the query. For
example, the glob

```scala
  val twoDeep = Glob("/foo/bar") / * / * / *
```

matches any path that is a descendant of `/foo/bar` that has exactly two
parents, e.g. `/foo/bar/a/b/c.txt` would be accepted but not `/foo/bar/a/b` or
`/foo/bar/a/b/c/d.txt`.

### Regular expressions

The `Glob` apis use glob syntax (see
[PathMatcher](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String))
for details). [Regular
expressions](https://www.scala-lang.org/api/2.12.8/scala/util/matching/Regex.html)
can be used instead:

```scala
val digitGlob = Glob("/foo/bar") / ".*-\\d{2,3}[.]txt".r
digitGlob.matches(Paths.get("/foo/bar").resolve("foo-1.txt")) // false
digitGlob.matches(Paths.get("/foo/bar").resolve("foo-23.txt")) // true
digitGlob.matches(Paths.get("/foo/bar").resolve("foo-123.txt")) // true
```

It is possible to specify multiple path components in the regex:

```scala
val multiRegex = Glob("/foo/bar") / "baz-\\d/.*/foo.txt"
multiRegex.matches(Paths.get("/foo/bar/baz-1/buzz/foo.txt")) // true
multiRegex.matches(Paths.get("/foo/bar/baz-12/buzz/foo.txt")) // false
```

Recursive globs cannot be expressed using regex syntax because `**` is not valid
in a regex and paths are matched component wise (so `"foo/.*/foo.txt"` is actually
split into three regular expressions `{"foo", ".*", "foo.txt"}` for matching
purposes. To make the `multiRegex` from above recursive, one could write:

```
val multiRegex = Glob("/foo/bar") / "baz-\\d/".r / ** / "foo.txt"
multiRegex.matches(Paths.get("/foo/bar/baz-1/buzz/foo.txt")) // true
multiRegex.matches(Paths.get("/foo/bar/baz-1/fizz/buzz/foo.txt")) // true
```

In regex syntax, `\` is an escape character and cannot be used as a path
separator. If the regex covers multiple path components, `/` must be used as the
path separator, even on Windows:

```
val multiRegex = Glob("/foo/bar") / "baz-\\d/foo\\.txt".r
val validRegex = Glob("/foo/bar") / "baz/Foo[.].txt".r
// throws java.util.regex.PatternSyntaxException because \\F is not a valid
// regex construct
val invalidRegex = Glob("/foo/bar") / "baz\\Foo[.].txt".r
```

<a name="file-tree-view"></a>
### Querying the file system with FileTreeView

Querying the file system for the files that match one or more `Glob` patterns is
done via the `sbt.nio.file.FileTreeView` trait. It provides two methods

1. `def list(glob: Glob): Seq[(Path, FileAttributes)]`
2. `def list(globs: Seq[Glob]): Seq[(Path, FileAttributes)]`

that can be used to retrieve all of the paths matching the provided patterns.

```scala
val scalaSources: Glob = ** / "*.scala"
val regularSources: Glob = "/foo/src/main/scala" / scalaSources
val scala212Sources: Glob = "/foo/src/main/scala-2.12"
val sources: Seq[Path] = FileTreeView.default.list(regularSources).map(_._1)
val allSources: Seq[Path] =
  FileTreeView.default.list(Seq(regularSources, scala212Sources)).map(_._1)
```

In the variant that takes `Seq[Glob]` as input, sbt will aggregate all of the
globs in such a way that it will only ever list any directory on the file system
once. It should return all of the files whose path name matches _any_ of the
provided `Glob` patterns in the input `Seq[Glob]`.

#### File attributes

The `FileTreeView` trait is parameterized by a type, `T`, that is always
`(java.nio.file.Path, sbt.nio.file.FileAttributes)` in sbt. The `FileAttributes`
trait provides access to the following properties:

1. `isDirectory` -- returns true if the `Path` represents a directory.
2. `isRegularFile` -- returns true if the `Path` represents a regular file. This
should usually be the inverse of `isDirectory`.
3. `isSymbolicLink` -- returns true if the `Path` is a symbolic link. The
default `FileTreeView` implementation always follows symbolic links. If the
symbolic link targets a regular file, both `isSymbolicLink` and `isRegularFile`
will be true. Similarly, if the link targets a directory, both `isSymbolicLink`
and `isDirectory` will be true. If the link is broken, `isSymbolicLink` will be
true but both `isDirectory` and `isRegularFile` will be false.

The reason that the `FileTreeView` always provides the attributes is because
checking the type of a file requires a system call, which can be slow. All of
the major desktop operating systems provide apis for listing a directory where
both the file names and file node types are returned. This allows sbt to provide
this information without making an extra system call. We can use this to
efficiently filter paths:

```scala
// No additional io is performed in the call to attributes.isRegularFile
val scalaSourcePaths =
  FileTreeView.default.list(Glob("/foo/src/main/scala/**/*.scala")).collect {
    case (path, attributes) if attributes.isRegularFile => path
  }
```

<a name="path-filters"></a>
#### Filtering

In addition to the `list` methods described above, there two additional
overloads that take an `sbt.nio.file.PathFilter` argument:

1. `def list(glob: Glob, filter: PathFilter): Seq[(Path, FileAttributes)]`
2. `def list(globs: Seq[Glob], filter: PathFilter): Seq[(Path, FileAttributes)]`

The `PathFilter` has a single abstract method:

```scala
def accept(path: Path, attributes: FileAttributes): Boolean
```
It can be used to further filter the query specified by the glob patterns:

```scala
val regularFileFilter: PathFilter = (_, a) => a.isRegularFile
val scalaSourceFiles =
  FileTreeView.list(Glob("/foo/bar/src/main/scala/**/*.scala"), regularFileFilter)
```

A `Glob` may be used as a `PathFilter`:

```scala
val filter: PathFilter = ** / "*include*"
val scalaSourceFiles =
  FileTreeView.default.list(Glob("/foo/bar/src/main/scala/**/*.scala"), filter)
```
Instances of `PathFilter` can be negated with the `!` unary operator:

```scala
val hiddenFileFilter: PathFilter = (p, _) => Try(Files.isHidden(p)).getOrElse(false)
val notHiddenFileFilter: PathFilter = !hiddenFileFilter
```
They can be combined with the `&&` operator:

```scala
val regularFileFilter: PathFilter = (_, a) => a.isRegularFile
val notHiddenFileFilter: PathFilter = (p, _) => Try(Files.isHidden(p)).getOrElse(false)
val andFilter = regularFileFilter && notHiddenFileFilter
val scalaSources =
  FileTreeView.default.list(Glob("/foo/bar/src/main/scala/**/*.scala"), andFilter)
```

They can be combined with the `||` operator:

```scala
val scalaSources: PathFilter = ** / "*.scala"
val javaSources: PathFilter = ** / "*.java"
val jvmSourceFilter = scalaSources || javaSources
val jvmSourceFiles =
  FileTreeView.default.list(Glob("/foo/bar/src/**"), jvmSourceFilter)
```

There is also an implicit conversion from `String` to `PathFilter` that converts
the `String` to a `Glob` and converts the `Glob` to a `PathFilter`:

```scala
val regularFileFilter: PathFilter = (p, a) => a.isRegularFile
val regularScalaFiles: PathFilter = regularFileFilter && "**/*.scala"
```

In addition to the ad-hoc filters, there are some commonly used filters that are
available in the default sbt scope:

1. `sbt.io.HiddenFileFilter` -- accepts any file that is hidden according to
`Files.isHidden`. On posix systems, this will just check if the name starts with
`.` while on Windows, it will need to perform io to extract the `dos:hidden`
attribute.
2. `sbt.io.RegularFileFilter` -- equivalent to `(_, a: FileAttributes) =>
a.isRegularFile`
3. `sbt.io.DirectoryFilter` -- equivalent to `(_, a: FileAttributes) =>
a.isDirectory`

There is also a converter from `sbt.io.FileFilter` to `sbt.nio.file.PathFilter`
that can be invoked by calling `toNio` on the `sbt.io.FileFilter` instance:

```scala
val excludeFilter: sbt.io.FileFilter = HiddenFileFilter || DirectoryFilter
val excludePathFilter: sbt.nio.file.PathFilter = excludeFilter.toNio
```

The `HiddenFileFilter`, `RegularFileFilter` and `DirectoryFilter` inherit both
`sbt.io.FileFilter` and `sbt.nio.file.PathFilter`. They typically can be treated
like a `PathFilter`:

```scala
val regularScalaFiles: PathFilter = RegularFileFilter && (** / "*.scala")
```

This will not work when the implicit conversion from `String` to `PathFinder` is
required.

```scala
 val regularScalaFiles = RegularFileFilter && "**/*.scala"
// won't compile because it gets interpreted as
// (RegularFileFilter: sbt.io.FileFilter).&&(("**/*.scala"): sbt.io.NameFilter)
```

In these situations, use `toNio`:

```scala
 val regularScalaFiles = RegularFileFilter.toNio && "**/*.scala"
```

It is important to note that semantics of `Glob` are different from
`NameFilter`. When using the `sbt.io.FileFilter`, in order to filter files
ending with the `.scala` extension, one would write:

```scala
val scalaFilter: NameFilter = "*.scala"
```

An equivalent `PathFilter` is written

```scala
val scalaFilter: PathFilter = "**/*.scala"
```
The glob represented `"*.scala"` matches a path with a single component ending
in scala. In general, when converting `sbt.io.NameFilter` to
`sbt.nio.file.PathFilter`, it will be necessary to add a `"**/"` prefix.

#### Streaming

In addition to `FileTreeView.list`, there is also `FileTreeView.iterator`. The
latter may be used to reduce memory pressure:

```
// Prints all of the files on the root file system
FileTreeView.iterator(Glob("/**")).foreach { case (p, _) => println(p) }
```

In the context of sbt, the type parameter, `T`, is always `(java.nio.file.Path,
sbt.nio.file.FileAttributes)`. An implementation of `FileTreeView` is provided in sbt with the `fileTreeView`
key:

```
fileTreeView.value.list(baseDirectory.value / ** / "*.txt")
```

#### Implementation

The `FileTreeView[+T]` trait has a single abstract method:

```
def list(path: Path): Seq[T]
```

sbt only provides implementations of `FileTreeView[(Path, FileAttributes)]`. In
this context, the `list` method should return the `(Path, FileAttributes)` pairs
for all of the direct children of the input `path`.

There are two implementations of `FileTreeView[(Path, FileAttribute)]`
provided by sbt:
1. `FileTreeView.native` -- this uses a native jni library to efficiently
extract the file names and attributes from the file system without performing
additional io. Native implementations are available for 64 bit FreeBSD, Linux,
Mac OS and Windows. If no native implementation is available, it falls back to a
`java.nio.file` based implementation.
2. `FileTreeView.nio` -- uses apis in `java.nio.file` to implement
`FileTreeView`

The `FileTreeView.default` method returns `FileTreeView.native`.

The `list` and `iterator` methods that take `Glob` or `Seq[Glob]` as arguments
are provided as extension methods to `FileTreeView[(Path, FileAttributes)]`.
Since any implementation of `FileTreeView[(Path, FileAttributes)]` automatically
receives these extensions, it is easy to write an alternative implementation
that will still correctly work with `Glob` and `Seq[Glob]`:

```scala
val listedDirectories = mutable.Set.empty[Path]
val trackingView: FileTreeView[(Path, FileAttributes)] = path => {
  val results = FileTreeView.default.list(path)
  listedDirectories += path
  results
}
val scalaSources =
  trackingView.list(Glob("/foo/bar/src/main/scala/**/*.scala")).map(_._1)
println(listedDirectories) // prints all of the directories traversed by list
```

<a name="glob-vs-pathfinder"></a>
### Globs vs. PathFinder

sbt has long had the [PathFinder][Paths] api which provides a dsl for collecting
files. While there is overlap, Globs are a less powerful abstraction than
PathFinder. This makes them more suitable for optimization. Globs describe the
what, but not the how, of a query. PathFinders combine the what and the how,
which makes them more difficult to optimize. For example, the following sbt snippet:

```
val paths = fileTreeView.value.list(
    baseDirectory.value / ** / "*.scala",
    baseDirectory.value / ** / "*.java").map(_._1)
```

will only traverse the file system once to collect all of the scala and java
sources in the project. By contrast,

```
val paths =
    (baseDirectory.value ** "*.scala" +++
     baseDirectory.value ** "*.java").allPaths
```

will make two passes and will thus take about twice as long to run when compared
to the Glob version.

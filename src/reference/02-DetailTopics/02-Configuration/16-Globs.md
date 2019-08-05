---
out: Globs.html
---
  [Paths]: Paths.html#path-finder

### Globs

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

```
val glob = Glob(Paths.get("foo/bar"))
println(glob.matches(Paths.get("foo"))) // prints false
println(glob.matches(Paths.get("foo/bar"))) // prints true
println(glob.matches(Paths.get("foo/bar/baz"))) // prints false
```
It can also be created using the glob dsl with:

```
val glob = Paths.get("foo/bar").toGlob
```

There are two special glob objects:
1) `AnyPath` (aliased by `*`) matches any path with just one name component
2) `RecursiveGlob` (aliased by `**`) matches all paths

Using `AnyPath`, we can explicitly construct a glob that matches all children of
a directory:

```
val path = Paths.get("/foo/bar")
val children = Glob(path, AnyPath)
println(children.matches(path)) // prints false
println(children.matches(path.resolve("baz")) // prints true
println(children.matches(path.resolve("baz").resolve("buzz") // prints false
```

Using the dsl, the above becomes:

```
val children    = Paths.get("/foo/bar").toGlob / AnyPath
val dslChildren = Paths.get("/foo/bar").toGlob / *
// these two definitions have identical results
```

Recursive globs are similar:

```
val path = Paths.get("/foo/bar")
val allDescendants = Glob(path, RescursiveGlob)
println(allDescendants.matches(path)) // prints false
println(allDescendants.matches(path.resolve("baz")) // prints true
println(allDescendants.matches(path.resolve("baz").resolve("buzz") // prints true
```

or

```
val allDescendants = Paths.get("/foo/bar").toGlob / **
```

### Path names

Globs may also be constructed using path names. The following three globs are
equivalent:

```
val pathGlob = Paths.get("foo").resolve("bar")
val glob = Glob("foo/bar")
val altGlob = Glob("foo") / "bar"
```

When parsing glob paths, any `/` characters are automatically converted to `\\`
on windows.

### Filters

Globs can apply name filters at each path level. For example,

```
val scalaSources = Paths.get("/foo/bar").toGlob / ** / "src" / "*.scala"
```
specifies all of the descendants of `/foo/bar` that have the `scala` file
extension whose parent directory is named `src`.

More advanced queries are also possible:

```
val scalaAndJavaSources =
  Paths.get("/foo/bar").toGlob / ** / "src" / "*.{scala,java}"
```

### Depth

The `AnyPath` special glob can be used to control the depth of the query. For
example, the glob

```
  val twoDeep = Glob("/foo/bar") / * / * / *
```

matches any path that is a descendant of `/foo/bar` that has exactly two
parents, e.g. `/foo/bar/a/b/c.txt` would be accepted but not `/foo/bar/a/b` or
`/foo/bar/a/b/c/d.txt`.



### Regular expressions

The `Glob` apis use glob syntax (see
[PathMatcher](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher-java.lang.String-)
for details). [Regular
expressions](https://www.scala-lang.org/api/2.12.8/scala/util/matching/Regex.html)
can be used instead:

```
val digitGlob = Glob("/foo/bar") / ".*-\\d{2,3}[.]txt".r
digitGlob.matches(Paths.get("/foo/bar").resolve("foo-1.txt")) // false
digitGlob.matches(Paths.get("/foo/bar").resolve("foo-23.txt")) // true
digitGlob.matches(Paths.get("/foo/bar").resolve("foo-123.txt")) // true
```

It is possible to specify multiple path components in the regex:

```
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
    (baseDirectory.value ** "*.scala" ++
     baseDirectory.value ** "*.java").allPaths
```

will make two passes and will thus take about twice as long to run when compared
to the Glob version.

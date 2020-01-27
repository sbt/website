---
out: Howto-Generating-Files.html
---

  [track-file-inputs-and-outputs]: Howto-Track-File-Inputs-and-Outputs.html
  [modify-package-contents]: Howto-Package.html#modify-package-contents

Generating files
----------------

sbt provides standard hooks for adding source and resource generation
tasks.

<a name="sources"></a>

### Generate sources

A source generation task should generate sources in a subdirectory of
`sourceManaged` and return a sequence of files generated. The signature
of a source generation function (that becomes a basis for a task) is
usually as follows:

```scala
def makeSomeSources(base: File): Seq[File]
```

The key to add the task to is called `sourceGenerators`. Because we want
to add the task, and not the value after its execution, we use
`taskValue` instead of the usual `value`. `sourceGenerators` should be
scoped according to whether the generated files are main (`Compile`) or
test (`Test`) sources. This basic structure looks like:

```scala
Compile / sourceGenerators += <task of type Seq[File]>.taskValue
```

For example, assuming a method
`def makeSomeSources(base: File): Seq[File]`,

```scala
Compile / sourceGenerators += Def.task {
  makeSomeSources((Compile / sourceManaged).value / "demo")
}.taskValue
```

As a specific example, the following source generator generates
`Test.scala` application object that once executed, prints `"Hi"` to the
console:

```scala
Compile / sourceGenerators += Def.task {
  val file = (Compile / sourceManaged).value / "demo" / "Test.scala"
  IO.write(file, """object Test extends App { println("Hi") }""")
  Seq(file)
}.taskValue
```

Executing `run` will print `"Hi"`.

```
> run
[info] Running Test
Hi
```

Change `Compile` to `Test` to make it a test source.

**NOTE:** For the efficiency of the build, `sourceGenerators` should avoid
regenerating source files upon each call. Instead, the outputs should be cached
based on the input values either using the [File tracking
system][track-file-inputs-and-outputs] or by manually
tracking the input values using `sbt.Tracked.{ inputChanged, outputChanged }`
etc.

By default, generated sources are not included in the packaged source
artifact. To do so, add them as you would other mappings. See
[Adding files to a package][modify-package-contents]. A source
generator can return both Java and Scala sources mixed together in the
same sequence. They will be distinguished by their extension later.

<a name="resources"></a>

### Generate resources

A resource generation task should generate resources in a subdirectory
of `resourceManaged` and return a sequence of files generated. Like a
source generation function, the signature of a resource generation
function (that becomes a basis for a task) is usually as follows:

```scala
def makeSomeResources(base: File): Seq[File]
```

The key to add the task to is called `resourceGenerators`. Because we
want to add the task, and not the value after its execution, we use
`taskValue` instead of the usual `value`. It should be scoped according
to whether the generated files are main (`Compile`) or test (`Test`)
resources. This basic structure looks like:

```scala
Compile / resourceGenerators += <task of type Seq[File]>.taskValue
```

For example, assuming a method
`def makeSomeResources(base: File): Seq[File]`,

```scala
Compile / resourceGenerators += Def.task {
  makeSomeResources((Compile / resourceManaged).value / "demo")
}.taskValue
```

Executing `run` (or `package`, not `compile`) will add a file `demo` to
`resourceManaged`, which is `target/scala-*/resource_managed"`. By default,
generated resources are not included in the packaged source artifact. To do so,
add them as you would other mappings.
See [Adding files to a package][modify-package-contents].

As a specific example, the following generates a properties file
`myapp.properties` containing the application name and version:

```scala
Compile / resourceGenerators += Def.task {
  val file = (Compile / resourceManaged).value / "demo" / "myapp.properties"
  val contents = "name=%s\\nversion=%s".format(name.value,version.value)
  IO.write(file, contents)
  Seq(file)
}.taskValue
```

Change `Compile` to `Test` to make it a test resource.

**NOTE:** For the efficiency of the build, `resourceGenerators` should avoid regenerating resource files upon each call,
and cache based on the input values using `sbt.Tracked.{ inputChanged, outputChanged }` etc instead.

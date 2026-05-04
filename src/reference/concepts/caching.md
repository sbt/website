Caching
=======

sbt 2.0 introduces hybrid local/remote cache system, which can cache the task results to local disk and Bazel-compatible remote cache. Throughout sbt releases it has implemented various caches, like `update` cache, incremental compilation, but sbt 2.x's cache is a significant step change for a few reasons:

1. **Automatic**. sbt 2.x cache automates the caching by embedding itself into the task macro unlike sbt 1.x wherein the plugin author called the cache functions manually in the task implementation.
2. **Machine-wide**. sbt 2.x disk cache is shared among all builds on a machine.
3. **Remote-ready**. In sbt 2.x, the cache storage is configured separately such that all cacheable tasks are automatically remote-cache-ready.

The overall objective of caching is to flatten the build and test time growth as the code size increases compared to the status quo.
For this reason, speedup ratio would depend on the code size etc, but aiming for 5x to 20x is achievable for builds that currently takes 10+ minutes to test.

Basics of caching
-----------------

The basic idea is treat as if the build process is a pure function that takes input `(A1, A2, A3, ...)` and return some outputs `(R1, List(O1, O2, O3, ...))`. For example, we can take a list of source files, Scala version, and produce a `*.jar` file at the end. If the assumption holds, then for the same inputs, we can memorize the ouput JAR for everyone. We are interested in this technique because using the memorized output JAR would be faster than performing the actual task like Scala compilation etc.

### Hermetic build

As a mental model of the _build as a pure function_, build engineers sometimes use the term _hermetic build_, which is a build that takes place in a shipping container in a dessert with no clocks or the Internet. If we can produce a JAR file from that state, then the JAR file should be safe to be shared by any machine. Why did I mention the clock? It's because a JAR file could capture the timestamp, and thus produce slightly different JARs each time. To avoid this, hermetic build tools overwrite the timestamp to a fixed date 2010-01-01 regardless of when the build took place.

A build that ends up capturing ephemeral inputs, are said to _break the hermeticity_ or _non-hermetic_. Another common way the hermeticity is broken is capturing absolute paths as either input or output. Sometimes the path gets embedded into the JAR via a macro, you might not know until you inspect the bytecode.

Automatic caching
-----------------

Here's a demonstration of the automatic caching:

```scala
val someKey = taskKey[String]("something")

someKey := name.value + version.value + "!"
```

In sbt 2.x, the task result will be cached based on the values of two settings `name` and `version`.
The first time we run the task, it will be executed onsite, but it will use the disk cache from the second time onwards:

```
sbt:demo> show someKey
[info] demo0.1.0-SNAPSHOT!
[success] elapsed time: 0 s, cache 0%, 1 onsite task
sbt:demo> show someKey
[info] demo0.1.0-SNAPSHOT!
[success] elapsed time: 0 s, cache 100%, 1 disk cache hit
```

### Caching is just as hard as serialization

To participate in the automatic caching,
the input keys (e.g. `name` and `version`) must provide a given　for
`sjsonnew.HashWriter` typeclass and return type must provide a given for
`sjsonnew.JsonFormat`.
[Contraband](https://www.scala-sbt.org/contraband/) can be used to generate sjson-new codecs.

### Underbaking

It's useful to have words to describe the failure states of cached tasks.

- **Overbaking**: When a cached task is invalidated more than necessary because of excess input `(A1, A2, A3, ...)`, leading to unnecessary work, we can say that the task is _overbaked_.
- **Underbaking**: When a cached task fails to invalidate because of insufficient inputs, leading to invalid result, we can say that the task is _underbaked_. In the context of incremental compilation, we call it _under-compilation_.

For example, we can say that mixing `@transient` keys into a cached task could potentially lead to underbaking.

Caching files
-------------

Caching files (e.g. `java.io.File`) requires its own consideration, not because it's technically difficult, but mostly because of the ambiguity and assumptions when files are involved. When we say a "file" it could actually mean:

1. Relative path from a well-known location
2. Materialized actual file
3. A unique proof of a file, or a content hash

Technically speaking, a `File` just means the file path, so we can deserialize just the filename such as `target/a/b.jar`. This will fail the downstream tasks if they assumed that `target/a/b.jar` would exist in the file system. For clarity, and also for avoiding to capture absolute paths, sbt 2.x provides three separate types for the three cases.

- `xsbti.VirtualFileRef` is used to mean just the relative path, which is equivalent to passing a string
- `xsbti.VirtualFile` represents a materialized file with contents, which could be a virtual file or a file in your disk

However, for the purpose of hermetic build, neither is great to represent a list of files. Having just the filename alone doesn't guarantee that the file will be the same, and carrying the entire content of the files is too inefficient in a JSON etc.

This is where the mysterious third option, a unique proof of file comes in handy. In addition to the relative path, `HashedVirtualFileRef` tracks the SHA-256 content hash and the file size. This can easily be serialized to JSON yet we can reference the exact file.

### The effect of file

There are many tasks that generate file that do not use `VirtualFile` as the return type. For example, `compile` returns `Analysis` instead, and `*.class` file generation happens as a _side effect_ in sbt 1.x.

To participate in caching, we need to declare these effects as something we care about.

```scala
someKey := {
  val conv = fileConverter.value
  val out: java.nio.file.Path = createFile(...)
  val vf: xsbti.VirtualFile = conv.toVirtualFile(out)
  Def.declareOutput(vf)
  vf: xsbti.HashedVirtualFileRef
}
```

Remote caching
--------------

You can optionally extend the build to use remote cache in addition to the local disk cache. Remote caching could improve build performance by allowing multiple machines to share build artifacts and outputs.

Imagine you have a dozen people in your project or a company. Each morning, you will `git pull` the changes the dozen people made, and you need to build their code. If you have a successful project, the code size will only get bigger over time, and the % of the time you spend building someone else's in your day increases. This becomes the limiting factor of your team size and code size. Remote caching reverses this tide by CI systems hydrate the cache and you can download the artifacts and task outputs.

sbt 2.x implements Bazel-compatible gRPC interface, which works with number of backend both open source and commercial. See [Remote cache setup](../reference/remote-cache-setup.md) for more details.

Reference
---------

See also [Cached task](../reference/cached-task.md) reference guide.

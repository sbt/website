Cached task
===========

This page covers the cached task details. See [Caching](../concepts/caching.md) for a general explanation.

Automatic caching
-----------------

```scala
val someKey = taskKey[String]("something")

someKey := name.value + version.value + "!"
```

In sbt 2.x, the task result will be automatically cached based on the two settings `name` and `version`. The first time we run the task it will be executed onsite, but the second time onward, it will use the disk cache:

```
sbt:demo> show someKey
[info] demo0.1.0-SNAPSHOT!
[success] elapsed time: 0 s, cache 0%, 1 onsite task
sbt:demo> show someKey
[info] demo0.1.0-SNAPSHOT!
[success] elapsed time: 0 s, cache 100%, 1 disk cache hit
```

### Caching is serialization-hard

To participate in the automatic caching, the input keys (e.g. `name` and `version`) must provide a given instance for `sjsonnew.HashWriter` typeclass and return type must provide a given instance for `sjsonnew.JsonFormat`.

```bash
[error] -- Error: /Users/xxx/caching/project/FooPlugin.scala:17:4 -
[error]  17 |    foo := {
[error]     |    ^
[error]     |given evidence sjsonnew.JsonFormat[sbt.HashedVirtualFileRef] is not found; opt out of caching by annotating the key with @transient, or as foo := Def.uncached(...), or provide a given value
[error]     |
[error]  18 |      val b = baseDirectory.value
```

Codecs for some of the built-in types like `HashedVirtualFileRef` can be made available using `CacheImplicits.given`:

```scala
import CacheImplicits.given

....

  override lazy val projectSettings: Seq[Setting[?]] = Seq(
    foo := {
      val b = baseDirectory.value
      val conv = fileConverter.value
      conv.toVirtualFile((b / "build.sbt").toPath)
    },
    bar := foo.value,
  )
```

[Contraband](https://www.scala-sbt.org/contraband/) can be used to generate sjson-new codecs.

Effect tracking
---------------

### The effect of file creation

To cache the effect of file creation, not just returning the name of the file, we need to track the effect of file creation using `Def.declareOutput(vf)`.

```scala
someKey := {
  val conv = fileConverter.value
  val out: java.nio.file.Path = createFile(...)
  val vf: xsbti.VirtualFile = conv.toVirtualFile(out)
  Def.declareOutput(vf)
  vf: xsbti.HashedVirtualFileRef
}
```

### The effect of system properties

Capturing a system property or an environment variable in a cached task can break the cache stability.
Consider the following cached task:

~~~admonish example title='build.sbt bad example'
```scala
lazy val someInt = taskKey[Int]("")

// BAD
someInt := {
  sys.props("release").toInt + 1
}
```
~~~

Suppose we run it first with `-Drelease=0`:

```bash
$ sbt --server -Drelease=0
....
sbt:caching> show someInt
[info] 1
[success] elapsed time: 0 s, cache 0%, 1 onsite task
```

However, even if we run with `-Drelease=1` the value does not change:

```bash
$ sbt --server -Drelease=1
....
sbt:caching> show someInt
[info] 1
[success] elapsed time: 0 s, cache 100%, 1 disk cache hit
```

This is because `someInt` only looks at the shape of its task definition and the input settings and tasks as cache key.
To correctly cache `someInt`, wrap the system property as a setting as follows:

~~~admonish example title='build.sbt'
```scala
lazy val someInt = taskKey[Int]("")
lazy val releaseVer = settingKey[Int]("")

releaseVer := sys.props("release").toInt
someInt := releaseVer.value + 1
```
~~~

This will reevaluate the system property each time the build loads:

```bash
$ sbt --server -Drelease=1
sbt:caching> show someInt
[info] 2
[success] elapsed time: 0 s, cache 0%, 1 onsite tas
```

Opting out from caching
-----------------------

### Per-task-key opt-out

Next, if you want to opt some task keys from caching, you can set the cache level as follows:

```scala
@transient
val someKey = taskKey[String]("something")
```

or

```scala
@cacheLevel(include = Array.empty)
val someKey = taskKey[String]("something")
```

### Per-task opt-out

To opt out of the cache individually, use `Def.uncached(...)` as follows:

```scala
val someKey = taskKey[String]("something")

someKey := Def.uncached {
  name.value + somethingUncachable.value + "!"
}
```

### Build-wide opt-out

To opt out of by-default custom task caching, add the following to `project/plugins.sbt`:

```scala
Compile / scalacOptions += "-Xmacro-settings:sbt:no-default-task-cache"
```

```admonish note
This applies only to the custom tasks introduced in the build. Any cached tasks provided by sbt or plugins will remain cached.
```

Remote caching
--------------

sbt 2.x implements Bazel-compatible gRPC interface, which works with number of backend both open source and commercial. See [Remote cache setup](./remote-cache-setup.md) for more details.


  [Caching Files]: ../concepts/caching.md#caching-files

sbt 2.0 changes (draft)
=======================

```admonish warning
This is a draft documentation of sbt 2.x that is yet to be released.
While the general concept translates to sbt 1.x,
details of both 2.x and this doc are subject to change.
```

Changes with compatibility implications
---------------------------------------

See also [Migrating from sbt 1.x](./migrating-from-sbt-1.x.md).

- sbt 2.x uses Scala 3.x (currently **{{scala3_metabuild_version}}**) for build definitions and plugins (Both sbt 1.x and 2.x are capable of building Scala 2.x and 3.x) by [@eed3si9n][@eed3si9n], [@adpi2][@adpi2], and others.
- Bare settings are added to all subprojects, as opposed to just the root subproject, and thus replacing the role that `ThisBuild` has played.
- `test` task is changed to be incremental test that can cache test results. Use `testFull` for full test by [@eed3si9n][@eed3si9n] in [#7686][7686]
- Default settings and tasks keys typed to `URL` `apiMappings`, `apiURL`, `homepage`, `organizationHomepage`, `releaseNotesURL` were changed to `URI` in [#7927](https://github.com/sbt/sbt/pull/7927).
- `licenses` key is changed from `Seq[(String, URL)]` to `Seq[License]` in [#7927](https://github.com/sbt/sbt/pull/7927).
- sbt 2.x plugins are published with `_sbt2_3` suffix by [@eed3si9n][@eed3si9n] in [#7671][7671]
- sbt 2.x adds `platform` setting so `ModuleID`'s `%%` operator can cross build on JVM as well as JS and Native, as opposed to `%%%` operator that was created in a plugin to workaround this issue, by [@eed3si9n][@eed3si9n] in [#6746][6746]
- Dropped `useCoursier` setting so Coursier cannot be opted out, by [@eed3si9n][@eed3si9n] in [#7712][7712]
- `Key.Classpath` is changed to be an alias of the `Seq[Attributed[xsbti.HashedVirtualFileRef]]` type, instead of `Seq[Attributed[File]]`. Similarly, some task keys that used to return `File` have changed to return `HashedVirtualFileRef` instead. See [Caching Files].
- In sbt 2.x `target` defaults to `target/out/jvm/scala-{{scala3_metabuild_version}}/<subproject>/`, as opposed to `<subproject>/target/`.

### Dropped dreprecations

- sbt 0.13 style shell syntax by [@eed3si9n][@eed3si9n] in [#7700][7700]

Features
--------

- Project matrix, which was available via plugin in sbt 1.x, is in-sourced.
- sbt 2.x extends the unified slash syntax to support query of subprojects. Details below.
- Local/remote cache system. Details below

### Common settings

In sbt 2.x, the bare settings in `build.sbt` are interpreted to be common settings, and are injected to all subprojects. This means we can now set `scalaVersion` without using `ThisBuild` scoping:

```scala
scalaVersion := "{{scala3_example_version}}"
```

This also fixes the so-called dynamic dispatch problem:

```scala
lazy val hi = taskKey[String]("")
hi := name.value + "!"
```

In sbt 1.x `hi` task will capture the name of the root project, but in sbt 2.x it will return the `name` of each subproject with `!`:

```scala
$ export SBT_NATIVE_CLIENT=true
$ sbt show hi
[info] entering *experimental* thin client - BEEP WHIRR
[info] terminate the server with `shutdown`
> show hi
[info] foo / hi
[info]  foo!
[info] hi
[info]  root!
```

Contributed by [@eed3si9n][@eed3si9n] in [#6746][6746]

### sbt query

To filter down the subprojects, sbt 2.x introduces sbt query.

```bash
$ export SBT_NATIVE_CLIENT=true
$ sbt foo.../test
```

The above runs all subprojects that begins with `foo`.

```bash
$ sbt ...@scalaBinaryVersion=3/test
```

The above runs all subprojects whose `scalaBinaryVersion` is `3`. Contributed by [@eed3si9n][@eed3si9n] in [#7699][7699]

### Local/remote cache system

sbt 2.x implements cached task, which can automatically cache the task results to local disk and Bazel-compatible remote cache.

```scala
lazy val task1 = taskKey[String]("doc for task1")

task1 := (Def.cachedTask {
  name.value + version.value + "!"
}).value
```

This tracks the inputs into the `task1` and creates a machine-wide disk cache, which can also be configured to also use a remote cache. Since it's common for sbt tasks to also produce files on the side, we also provide a mechanism to cache file contents:

```scala
lazy val task1 = taskKey[String]("doc for task1")

task1 := (Def.cachedTask {
  val converter = fileConverter.value
  ....
  val output = converter.toVirtualFile(somefile)
  Def.declareOutput(output)
  name.value + version.value + "!"
}).value
```

Contributed by [@eed3si9n][@eed3si9n] in [#7464][7464] / [#7525][7525]

Previously on sbt
-----------------

See also:

- [sbt 1.0 changes](https://www.scala-sbt.org/1.x/docs/sbt-1.0-Release-Notes.html)

  [6746]: https://github.com/sbt/sbt/pull/6746
  [7464]: https://github.com/sbt/sbt/pull/7464
  [7525]: https://github.com/sbt/sbt/pull/7525
  [7671]: https://github.com/sbt/sbt/pull/7671
  [7686]: https://github.com/sbt/sbt/pull/7686
  [7699]: https://github.com/sbt/sbt/pull/7699
  [7700]: https://github.com/sbt/sbt/pull/7700
  [7712]: https://github.com/sbt/sbt/pull/7712
  [@eed3si9n]: https://github.com/eed3si9n
  [@adpi2]: https://github.com/adpi2

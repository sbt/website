
  [Caching Files]: ../concepts/caching.md#caching-files

sbt 2.0 changes
===============

Changes with compatibility implications
---------------------------------------

See also [Migrating from sbt 1.x](./migrating-from-sbt-1.x.md).

- **Scala 3 in metabuild**. sbt 2.x build.sbt DSL, used for build definitions and plugins, is based on Scala 3.x (currently **3.8.4**)  (Both sbt 1.x and 2.x are capable of building Scala 2.x and 3.x) by [@eed3si9n][@eed3si9n], [@adpi2][@adpi2], and others.
- **Common settings**. Bare settings are added to all subprojects, as opposed to just the root subproject, and thus replacing the role that `ThisBuild` has played.
- **Incremental test**. `test` task is changed to be incremental test that can cache test results. Use `testFull` for full test by [@eed3si9n][@eed3si9n] in [#7686][7686]
- **Cached task**. All tasks are cached by default. Details in [Caching](../concepts/caching.md).
- **Depedency tree**. `dependencyTree` tasks are unified to one input task by [@eed3si9n][@eed3si9n] in [#8199](https://github.com/sbt/sbt/pull/8199)
- `test` task type is changed from `Unit` to `TestResult` by [@eed3si9n][@eed3si9n] in [#8181][8181]
- Default settings and tasks keys typed to `URL` (i.e. `apiMappings`, `apiURL`, `homepage`, `organizationHomepage`, `releaseNotesURL`) were changed to `URI` in [#7927](https://github.com/sbt/sbt/pull/7927).
- `licenses` key is changed from `Seq[(String, URL)]` to `Seq[License]` in [#7927](https://github.com/sbt/sbt/pull/7927).
- sbt 2.x plugins are published with `_sbt2_3` suffix by [@eed3si9n][@eed3si9n] in [#7671][7671]
- sbt 2.x adds `platform` setting so `ModuleID`'s `%%` operator can cross build on JVM as well as JS and Native, as opposed to `%%%` operator that was created in a plugin to workaround this issue, by [@eed3si9n][@eed3si9n] in [#6746][6746]
- Dropped `useCoursier` setting so Coursier cannot be opted out, by [@eed3si9n][@eed3si9n] in [#7712][7712]
- `Key.Classpath` is changed to be an alias of the `Seq[Attributed[xsbti.HashedVirtualFileRef]]` type, instead of `Seq[Attributed[File]]`. Similarly, some task keys that used to return `File` have changed to return `HashedVirtualFileRef` instead. See [Caching Files].
- In sbt 2.x `target` defaults to `target/out/jvm/scala-3.8.4/<subproject>/`, as opposed to `<subproject>/target/`.
- sbt 2.x auto reloads by default on `build.sbt` changes, by [@eed3si9n][@eed3si9n] in [#8211][8211]
- sbt 2.x disables the delegation of scoped tasks in the sbt shell by [@eed3si9n][@eed3si9n] in [#8539][8539]
- sbt 2.x enforces eviction error in `Test` configuration by [@calm329][@calm329] and [@zainab-ali][@zainab-ali] in [#8451](https://github.com/sbt/sbt/pull/8451) + [#9102](https://github.com/sbt/sbt/pull/9102)

### Dropped dreprecations

- Removed `IntegrationTest` configuration in [#8184][8184]
- Removed sbt 0.13 style shell syntax in [#7700][7700]

Features
--------

- **Project matrix**. Project matrix, which was available via plugin in sbt 1.x, is in-sourced to provide parallel cross build support.
- **sbt query**. sbt 2.x extends the unified slash syntax to support query of subprojects. Details below.
- **Local/remote cache system**. Details below
- **Client-side run**. Details below.
- **Client-side console**. Details below.
- **rootProject and autoAggregate**. Details below
- **Maven BOM (Bill of Materials) usage**. Details below

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
$ sbt foo.../test
```

The above runs all subprojects that begins with `foo`.

```bash
$ sbt ...@scalaBinaryVersion=3/test
```

The above runs all subprojects whose `scalaBinaryVersion` is `3`. Contributed by [@eed3si9n][@eed3si9n] in [#7699][7699]

### Incremental test

In sbt 2.x, `test` task became an input task that accept arguments that can filter the test suites to run:

```bash
> test ...ExampleTest
```

In addition, `test` is incremental and cached. This means, the test will not run unless it previously failed or something changed since the last run.

See [test](../reference/sbt-test.md) for details.

### Local/remote cache system

sbt 2.x implements cached task by default, which can automatically cache the task results to local disk and Bazel-compatible remote cache.

```scala
lazy val task1 = taskKey[String]("doc for task1")

task1 := name.value + version.value + "!"
```

This tracks the inputs into the `task1` and creates a machine-wide disk cache, which can also be configured to also use a remote cache. Since it's common for sbt tasks to also produce files on the side, we also provide a mechanism to cache file contents:

```scala
lazy val task1 = taskKey[String]("doc for task1")

task1 := {
  val converter = fileConverter.value
  ....
  val output = converter.toVirtualFile(somefile)
  Def.declareOutput(output)
  name.value + version.value + "!"
}
```

See [Caching](../concepts/caching.md) for details. Contributed by [@eed3si9n][@eed3si9n] in [#7464][7464] / [#7525][7525].

### Client-side run

In sbt 2.0, sbt server sends the `run` task back to sbtn, which will fork a fresh JVM. All you have to do is:

```bash
sbt run
```

This avoids blocking the sbt server, and you can have multiple runs. Contributed by [@eed3si9n][@eed3si9n] in [#8060](https://github.com/sbt/sbt/pull/8060). See also [run](../reference/sbt-run.md) documentation.

### Client-side console

Similar to the client-side run, sbt server sends `console` (Scala REPL) back to the sbtn, which forks a fresh JVM to run the REPL. All you have to do is:

```bash
sbt console
```

This avoids blocking the sbt server. This was contributed by [@eed3si9n][@eed3si9n] and [@calm329][@calm329] in [#8018](https://github.com/sbt/sbt/pull/8018), [#8604](https://github.com/sbt/sbt/pull/8604), [#8677](https://github.com/sbt/sbt/pull/8677), [#8705](https://github.com/sbt/sbt/pull/8705), [#8722](https://github.com/sbt/sbt/pull/8722).

### rootProject and autoAggregate

sbt 2.0 adds `rootProject` macro:

```scala
lazy val root = rootProject
```

This is a shortcut for `(project in file("."))`, which tends to be a boilerplate in `build.sbt`.

```scala
lazy val root = rootProject
  .autoAggregate
```

sbt 2.0 also adds `autoAggregate` method, which at the loading time expands to local subprojects.

### Maven BOM (Bill of Materials) usage

sbt 2.0 adds Maven BOM (Bill of Materials) usage support. Subprojects can depend on published BOM artifacts using `.pomOnly()`:

```scala
libraryDependencies += ("com.fasterxml.jackson" % "jackson-bom" % "2.21.0").pomOnly()
```

These bill of materials are forwarded to Coursier via via Resolve.addBom(), which should introduce version constraints for specific libraries (such as Jackson). You can use `"*"` to declare versionless dependency:

```scala
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "*"
```

This will let Coursier automatically fill in the version based on the bill of material constraints (in this case `"2.21.0"`). Contributed by [@bitloi][@bitloi] in [#8675](https://github.com/sbt/sbt/pull/8675).

### Performance improvements

Adrien Piquerez contributed a series of changes to improve performance while he was at Scala Center.

* perf: Reduces number of long-living instances to speed up startup by 20% relative to 2.0.0-M2 by [@adpi2][@adpi2] in [#7866](https://github.com/sbt/sbt/pull/7866)
* perf: Reduces creation of `Setting` and `Initialize`  by [@adpi2][@adpi2] in [#7880](https://github.com/sbt/sbt/pull/7880)
* perf: Refactors `Settings` and optimize indexing of aggregate keys by [@adpi2][@adpi2] in [#7879](https://github.com/sbt/sbt/pull/7879)
* perf: Removes instances of `Info` and `BasicAttributeMap` by [@adpi2][@adpi2] in [#7882](https://github.com/sbt/sbt/pull/7882)

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
  [8181]: https://github.com/sbt/sbt/pull/8181
  [8184]: https://github.com/sbt/sbt/pull/8184
  [8211]: https://github.com/sbt/sbt/pull/8211
  [8290]: https://github.com/sbt/sbt/pull/8290
  [8539]: https://github.com/sbt/sbt/pull/8539
  [@eed3si9n]: https://github.com/eed3si9n
  [@adpi2]: https://github.com/adpi2
  [@bitloi]: https://github.com/bitloi
  [@calm329]: https://github.com/calm329
  [@zainab-ali]: https://github.com/zainab-ali

sbt query
=========

sbt 2.x extends the slash syntax to enable aggregation of subprojects:

_act_ `::=` \[ _query_ `/` \] \[ _config_ `/` \] \[ _in-task_ `/` \] ( _taskKey_ | _settingKey_ )

In other words, sbt query is a new way of writing the subproject-axis.

### Subproject reference

A subproject reference works as a query to select the subproject:

~~~admonish example title='build.sbt example 1'
```scala
scalaVersion := "{{scala3_example_version}}"

lazy val foo = project
```
~~~

Given the above build, we can run tests on `foo` subproject as follows, which is the same syntax as it was in sbt 1.x:

```
foo/test
```

### `...` wildcard

`...` wildcard matches to any characters, and can be combined with other letters and numbers to filter down the root aggregate list. For example, we can run tests on all subproject that starts with `foo` as follows:

```
foo.../test
```

```admonish note title='Note: * vs ...'
sbt query intentionally uses `...` (dot dot dot) instead of more intuitive `*` (asterisk) because `*` is often used in a shell as a wildcard to match existing files or directories. This would require quoting, and forgetting to quote `*/test` would match to something like `src/test`.
```

### `@scalaBinaryVersion` parameter

`@scalaBinaryVersion` parameter matches to the subproject's `scalaBinaryVersion` setting.

~~~admonish example
```scala
val toolkitV = "0.5.0"
val toolkit = "org.scala-lang" %% "toolkit" % toolkitV

lazy val foo = projectMatrix
  .settings(
    libraryDependencies += toolkit,
  )
  .jvmPlatform(scalaVersions = Seq("{{scala3_example_version}}", "{{scala2_13_example_version}}"))

lazy val bar = projectMatrix
  .settings(
    libraryDependencies += toolkit,
  )
  .jvmPlatform(scalaVersions = Seq("{{scala3_example_version}}", "{{scala2_13_example_version}}"))
```
~~~

For example, we can run tests on all 3.x subprojects as follows:

```
...@scalaBinaryVersion=3/test
```

This can be used from a terminal as follows:

```bash
$ sbt ...@scalaBinaryVersion=3/test
[info] entering *experimental* thin client - BEEP WHIRR
[info] terminate the server with `shutdown`
> ...@scalaBinaryVersion=3/test
[info] Passed: Total 0, Failed 0, Errors 0, Passed 0
[info] No tests to run for Test / testQuick
[info] compiling 1 Scala source to /tmp/foo/target/out/jvm/scala-3.6.4/foo/test-backend ...
[info] Passed: Total 0, Failed 0, Errors 0, Passed 0
[info] No tests to run for bar / Test / testQuick
example.ExampleSuite:
  + Scala version 0.003s
[info] Passed: Total 1, Failed 0, Errors 0, Passed 1
```

This lets us filter down the aggregated subprojects, which could be a lot using `projectMatrix`.

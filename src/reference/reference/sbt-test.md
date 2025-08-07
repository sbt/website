sbt test
========

Synopsis
--------

`sbt` \[_query_ / \] `test` \[_testname1_ _testname2_\] \[ -- _options_ \]

Description
-----------

The `test` task provides a means for compiling and running the tests.

By default, the `test` task in sbt 2.x:

1. **Subproject parallelism**. Performs compilation of the relevant subprojects in parallel, specified by the [query](../concepts/sbt-query.md).
2. **Test suite parallelism**. Maps discovered test suites, to tasks and executes them in parallel.
3. **Incremental test**. Runs only the tests that either failed in the previous run, never run, or if sbt detects changes in the test or its dependencies.
4. **Cached**. The test result is cached machine-wide, and optionally remote cached.

The standard source locations for testing are:

-   Scala sources in `src/test/scala/`
-   Java sources in `src/test/java/`
-   Resources for the test classpath in `src/test/resources/`

The resources may be accessed from tests by using the `getResource`
methods of `java.lang.Class` or `java.lang.ClassLoader`.

### Test interfaces

sbt defines the common interface for JVM-based test frameworks, allowing automatic test suite discovery and parallel execution. By default sbt integrates with [MUnit](https://scalameta.org/munit/), [ScalaTest](https://www.scalatest.org/), [Hedgehog](https://hedgehogqa.github.io/scala-hedgehog/), [ScalaCheck](https://scalacheck.org/), [Specs2](https://etorreborre.github.io/specs2/), [Weaver](https://typelevel.org/weaver-test/), [ZIO Test](https://zio.dev/reference/test/), and [JUnit 4](https://github.com/sbt/junit-interface); this means you only need to add the test framework to the classpath to work with sbt. For example, MUnit may be used by declaring it as a `libraryDependency`:

```scala
lazy val munit = "org.scalameta" %% "munit" % "{{example_munit_version}}"

libraryDependencies += munit % Test
```

In the above, `Test` denotes the `Test` configuration, and means that MUnit will
only be on the test classpath and it isn't needed by the main sources.

#### JUnit

Support for JUnit 5 is provided by
[sbt-jupiter-interface](https://github.com/sbt/sbt-jupiter-interface). To add
JUnit Jupiter support into your project, add the jupiter-interface dependency in
your project's main build.sbt file.

```scala
libraryDependencies += "com.github.sbt.junit" % "jupiter-interface" % "0.15.1" % Test
```

and the sbt-jupiter-interface plugin to your `project/plugins.sbt`:

```scala
addSbtPlugin("com.github.sbt.junit" % "sbt-jupiter-interface" % "0.15.1")
```

Support for JUnit 4 is provided by
[junit-interface](https://github.com/sbt/junit-interface).
Add the junit-interface dependency in your project's main build.sbt file.

```scala
libraryDependencies += "com.github.sbt" % "junit-interface" % "0.13.3" % Test
```

### Test filtering

In sbt 2.x, the `test` task accepts a whitespace separated list of test names to
run. For example:

```bash
> test example.ExampleSuite example.ExampleSuite2
```

Here's an example output:

```bash
> test example.ExampleSuite example.ExampleSuite2
[info] compiling 1 Scala source to /tmp/foo/target/out/jvm/scala-3.7.2/foo/backend ...
[info] compiling 2 Scala sources to /tmp/foo/target/out/jvm/scala-3.7.2/foo/test-backend ...
example.ExampleSuite:
  + addition 0.003s
example.ExampleSuite2:
  + subtraction 0.003s
[info] Passed: Total 2, Failed 0, Errors 0, Passed 2
[success] elapsed time: 3 s, cache 49%, 25 disk cache hits, 26 onsite tasks
```

It supports wildcards as well:

```
> test *Example*
```

### Incremental testing

In addition to the explicit filter, the `test` task runs only the tests that satisfy
one of the following conditions are run:

-   The tests that failed in the previous run
-   The tests that were not run before
-   The tests that have one or more transitive dependencies, maybe in a
    different project, recompiled.

### Full testing

To run, uncached full tests, like sbt 1.x, use the `testFull` task.

<!--
### Tab completion

Tab completion is provided for test names based on the results of the
last `Test/compile`. This means that a new sources aren't available for
tab completion until they are compiled and deleted sources won't be
removed from tab completion until a recompile. A new test source can
still be manually written out and run using `test`.
-->

### Other tasks

Tasks that are available for main sources are generally available for
test sources, but are prefixed with `Test /` on the command line and are
referenced in Scala code with `Test /` as well. These tasks include:

-   `Test / compile`
-   `Test / console`
-   `Test / consoleQuick`
-   `Test / run`
-   `Test / runMain`

See [sbt run](./sbt-run.md) for details on these tasks.

### Output

By default, logging is buffered for each test source file until all
tests for that file complete. This can be disabled by setting
`logBuffered`:

```scala
Test / logBuffered := false
```

#### Test Reports

By default, sbt will generate JUnit XML test reports for all tests in
the build, located in the `target/test-reports` directory for a project.
This can be disabled by disabling the `JUnitXmlReportPlugin`

```scala
val myProject = (project in file(".")).disablePlugins(plugins.JUnitXmlReportPlugin)
```

### Options

#### Test framework arguments

Arguments to the test framework may be provided on the command line to
the `test` tasks following a `--` separator. For example:

```
> test org.example.MyTest -- -verbosity 1
```

To specify test framework arguments as part of the build, add options
constructed by `Tests.Argument`:

```scala
Test / testOptions += Tests.Argument("-verbosity", "1")
```

To specify them for a specific test framework only:

```scala
Test / testOptions += Tests.Argument(TestFrameworks.ScalaCheck, "-verbosity", "1")
```

#### Setup and Cleanup

Specify setup and cleanup actions using `Tests.Setup` and
`Tests.Cleanup`. These accept either a function of type `() => Unit` or
a function of type `ClassLoader => Unit`. The variant that accepts a
ClassLoader is passed the class loader that is (or was) used for running
the tests. It provides access to the test classes as well as the test
framework classes.

```admonish note
When forking, the `ClassLoader` containing the test classes cannot be
provided because it is in another JVM. Only use the `() => Unit`
variants in this case.
```

Examples:

```scala
Test / testOptions += Tests.Setup( () => println("Setup") )
Test / testOptions += Tests.Cleanup( () => println("Cleanup") )
Test / testOptions += Tests.Setup( loader => ... )
Test / testOptions += Tests.Cleanup( loader => ... )
```

#### Disable parallel execution of test suites

By default, sbt runs all tasks in parallel and within the same JVM as sbt itself.
Because each test suite is mapped to a task, tests are also run in parallel by default.
To make tests within a given project execute serially:

```scala
Test / parallelExecution := false
```

Note that tests from different projects may
still execute concurrently.

#### Filter classes

If you want to only run test classes whose name ends with "Test", use
`Tests.Filter`:

```scala
Test / testOptions := Seq(Tests.Filter(s => s.endsWith("Test")))
```

#### Forking tests

The setting:

```scala
Test / fork := true
```

specifies that all tests will be executed in a single external JVM. <!-- See
[Forking][Forking] for configuring standard options for forking.
By default,
tests executed in a forked JVM are executed *sequentially*.
-->

More control over how tests are assigned to JVMs and what options to pass to those is
available with `testGrouping` key.

<!--
For example in build.sbt:

```scala
import Tests._

{
  def groupByFirst(tests: Seq[TestDefinition]) =
    tests groupBy (_.name(0)) map {
      case (letter, tests) =>
        val options = ForkOptions().withRunJVMOptions(Vector("-Dfirst.letter"+letter))
        new Group(letter.toString, tests, SubProcess(options))
    } toSeq

    Test / testGrouping := groupByFirst( (Test / definedTests).value )
}
```


The tests in a single group are run sequentially.
-->

Control the number of
forked JVMs allowed to run at the same time by setting the limit on
`Tags.ForkedTestGroup` tag, which is 1 by default. `Setup` and `Cleanup`
actions cannot be provided with the actual test class loader when a
group is forked.


<!--
In addition, forked tests can optionally be run in parallel within the
forked JVM(s), using the following setting:

```scala
Test / testForkedParallel := true
```

<a name="additional-test-configurations"></a>

### Additional test configurations

You can add an additional test configuration to have a separate set of
test sources and associated compilation, packaging, and testing tasks
and settings. The steps are:

-   Define the configuration
-   Add the tasks and settings
-   Declare library dependencies
-   Create sources
-   Run tasks

The following two examples demonstrate this. The first example shows how
to enable integration tests. The second shows how to define a customized
test configuration. This allows you to define multiple types of tests
per project.

#### Custom test configuration

The previous example may be generalized to a custom test configuration.

```scala
lazy val scalatest = "org.scalatest" %% "scalatest" % "$example_scalatest_version$"
lazy val FunTest = config("fun") extend(Test)

ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "$example_scala_version$"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .configs(FunTest)
  .settings(
    inConfig(FunTest)(Defaults.testSettings),
    libraryDependencies += scalatest % FunTest
    // other settings here
  )
```

Instead of using the built-in configuration, we defined a new one:

```scala
lazy val FunTest = config("fun") extend(Test)
```

The `extend(Test)` part means to delegate to `Test` for undefined
`FunTest` settings. The line that adds the tasks and settings for the
new test configuration is:

```scala
settings(inConfig(FunTest)(Defaults.testSettings))
```

This says to add test and settings tasks in the `FunTest` configuration.
We could have done it this way for integration tests as well. In fact,
`Defaults.itSettings` is a convenience definition:
`val itSettings = inConfig(IntegrationTest)(Defaults.testSettings)`.

The comments in the integration test section hold, except with
`IntegrationTest` replaced with `FunTest` and `"it"` replaced with
`"fun"`. For example, test options can be configured specifically for
`FunTest`:

```scala
FunTest / testOptions += ...
```

Test tasks are run by prefixing them with `fun:`

```
> FunTest / test
```

#### Additional test configurations with shared sources

An alternative to adding separate sets of test sources (and
compilations) is to share sources. In this approach, the sources are
compiled together using the same classpath and are packaged together.
However, different tests are run depending on the configuration.

```scala
lazy val scalatest = "org.scalatest" %% "scalatest" % "$example_scalatest_version$"
lazy val FunTest = config("fun") extend(Test)

ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "$example_scala_version$"
ThisBuild / version      := "0.1.0-SNAPSHOT"

def itFilter(name: String): Boolean = name endsWith "ITest"
def unitFilter(name: String): Boolean = (name endsWith "Test") && !itFilter(name)

lazy val root = (project in file("."))
  .configs(FunTest)
  .settings(
    inConfig(FunTest)(Defaults.testTasks),
    libraryDependencies += scalatest % FunTest,
    Test / testOptions := Seq(Tests.Filter(unitFilter)),
    FunTest / testOptions := Seq(Tests.Filter(itFilter))
    // other settings here
  )
```

The key differences are:

-   We are now only adding the test tasks
    (inConfig(FunTest)(Defaults.testTasks)) and not compilation and
    packaging tasks and settings.
-   We filter the tests to be run for each configuration.

To run standard unit tests, run `test` (or equivalently, `Test / test`):

```
> test
```

To run tests for the added configuration (here, `"FunTest"`), prefix it with
the configuration name as before:

```
> FunTest / test
> FunTest / testOnly org.example.AFunTest
```

##### Application to parallel execution

One use for this shared-source approach is to separate tests that can
run in parallel from those that must execute serially. Apply the
procedure described in this section for an additional configuration.
Let's call the configuration `serial`:

```scala
lazy val Serial = config("serial") extend(Test)
```

Then, we can disable parallel execution in just that configuration
using:

```scala
Serial / parallelExecution := false
```

The tests to run in parallel would be run with `test` and the ones to
run in serial would be run with `Serial/test`.

### Extensions

This page describes adding support for additional testing libraries and
defining additional test reporters. You do this by implementing `sbt`
interfaces (described below). If you are the author of the testing
framework, you can depend on the test interface as a provided
dependency. Alternatively, anyone can provide support for a test
framework by implementing the interfaces in a separate project and
packaging the project as an sbt [Plugin][Plugins].

#### Custom Test Framework

The main Scala testing libraries have built-in support for sbt. To add
support for a different framework, implement the
[uniform test interface](https://github.com/sbt/test-interface).

#### Custom Test Reporters

Test frameworks report status and results to test reporters. You can
create a new test reporter by implementing either
[TestReportListener](../api/sbt/TestReportListener.html) or
[TestsListener](../api/sbt/TestsListener.html).

#### Using Extensions

To use your extensions in a project definition:

Modify the `testFrameworks` setting to reference your test framework:

```scala
testFrameworks += new TestFramework("custom.framework.ClassName")
```

Specify the test reporters you want to use by overriding the
`testListeners` setting in your project definition.

```scala
testListeners += customTestListener
```

where `customTestListener` is of type `sbt.TestReportListener`.
-->

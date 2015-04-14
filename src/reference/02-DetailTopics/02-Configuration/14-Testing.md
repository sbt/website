---
out: Testing.html
---

  [Running]: ../tutorial/Running.html
  [Plugins]: Plugins.html
  [Library-Dependencies]: ../tutorial/Library-Dependencies.html
  [ivy-configurations]: Library-Management.html#ivy-configurations
  [Forking]: Forking.html

Testing
-------

### Basics

The standard source locations for testing are:

-   Scala sources in `src/test/scala/`
-   Java sources in `src/test/java/`
-   Resources for the test classpath in `src/test/resources/`

The resources may be accessed from tests by using the `getResource`
methods of `java.lang.Class` or `java.lang.ClassLoader`.

The main Scala testing frameworks (
[specs2](http://specs2.org/),
[ScalaCheck](http://scalacheck.org/), and
[ScalaTest](http://scalatest.org/)) provide an implementation of the
common test interface and only need to be added to the classpath to work
with sbt. For example, ScalaCheck may be used by declaring it as a
[managed dependency][Library-Dependencies]:

```scala
lazy val scalacheck = "org.scalacheck" %% "scalacheck" % "$example_scalacheck_version$"
libraryDependencies += scalacheck % Test
```

`Test` is the [configuration][ivy-configurations] and means that ScalaCheck will
only be on the test classpath and it isn't needed by the main sources.
This is generally good practice for libraries because your users don't
typically need your test dependencies to use your library.

With the library dependency defined, you can then add test sources in
the locations listed above and compile and run tests. The tasks for
running tests are `test` and `testOnly`. The `test` task accepts no
command line arguments and runs all tests:

```
> test
```

#### testOnly

The `testOnly` task accepts a whitespace separated list of test names to
run. For example:

```
> testOnly org.example.MyTest1 org.example.MyTest2
```

It supports wildcards as well:

```
> testOnly org.example.*Slow org.example.MyTest1
```

And you can add exclusions as well by prefixing with a `-`:

```
> testOnly org.example.*Slow -*ReallySlow*
```

#### testQuick

The `testQuick` task, like `testOnly`, allows to filter the tests to run
to specific tests or wildcards using the same syntax to indicate the
filters. In addition to the explicit filter, only the tests that satisfy
one of the following conditions are run:

-   The tests that failed in the previous run
-   The tests that were not run before
-   The tests that have one or more transitive dependencies, maybe in a
    different project, recompiled.
-   The tests that are not specifically excluded with `-`.

##### Tab completion

Tab completion is provided for test names based on the results of the
last `test:compile`. This means that a new sources aren't available for
tab completion until they are compiled and deleted sources won't be
removed from tab completion until a recompile. A new test source can
still be manually written out and run using `testOnly`.

#### Other tasks

Tasks that are available for main sources are generally available for
test sources, but are prefixed with `test:` on the command line and are
referenced in Scala code with `in Test`. These tasks include:

-   `test:compile`
-   `test:console`
-   `test:consoleQuick`
-   `test:run`
-   `test:runMain`

See [Running][Running] for details on these tasks.

### Output

By default, logging is buffered for each test source file until all
tests for that file complete. This can be disabled by setting
`logBuffered`:

```scala
logBuffered in Test := false
```

#### Test Reports

By default, sbt will generate JUnit XML test reports for all tests in
the build, located in the `target/test-reports` directory for a project.
This can be disabled by disabling the `JUnitXmlReportPlugin`

```scala
val myProject = (project in file(".")).disablePlugins(plugins.JUnitXmlReportPlugin)  
```

### Options

#### Test Framework Arguments

Arguments to the test framework may be provided on the command line to
the `testOnly` tasks following a `--` separator. For example:

```
> testOnly org.example.MyTest -- -verbosity 1
```

To specify test framework arguments as part of the build, add options
constructed by `Tests.Argument`:

```scala
testOptions in Test += Tests.Argument("-verbosity", "1")
```

To specify them for a specific test framework only:

```scala
testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck, "-verbosity", "1")
```

#### Setup and Cleanup

Specify setup and cleanup actions using `Tests.Setup` and
`Tests.Cleanup`. These accept either a function of type `() => Unit` or
a function of type `ClassLoader => Unit`. The variant that accepts a
ClassLoader is passed the class loader that is (or was) used for running
the tests. It provides access to the test classes as well as the test
framework classes.

> **Note**: When forking, the ClassLoader containing the test classes cannot be
> provided because it is in another JVM. Only use the () => Unit
> variants in this case.

Examples:

```scala
testOptions in Test += Tests.Setup( () => println("Setup") )

testOptions in Test += Tests.Cleanup( () => println("Cleanup") )

testOptions in Test += Tests.Setup( loader => ... )

testOptions in Test += Tests.Cleanup( loader => ... )
```

#### Disable Parallel Execution of Tests

By default, sbt runs all tasks in parallel and within the same JVM as sbt itself. 
Because each test is mapped to a task, tests are also run in parallel by default. 
To make tests within a given project execute serially: :

```scala
parallelExecution in Test := false
```

`Test` can be replaced with `IntegrationTest` to only execute
integration tests serially. Note that tests from different projects may
still execute concurrently.

#### Filter classes

If you want to only run test classes whose name ends with "Test", use
`Tests.Filter`:

```scala
testOptions in Test := Seq(Tests.Filter(s => s.endsWith("Test")))
```

#### Forking tests

The setting:

```scala
fork in Test := true
```

specifies that all tests will be executed in a single external JVM. See
[Forking][Forking] for configuring standard options for forking. By default,
tests executed in a forked JVM are executed *sequentially*.   More control
over how tests are assigned to JVMs and what options to pass to those is
available with `testGrouping` key. For example in build.sbt:

```scala
import Tests._

{
  def groupByFirst(tests: Seq[TestDefinition]) =
    tests groupBy (_.name(0)) map {
      case (letter, tests) => new Group(letter.toString, tests, SubProcess(Seq("-Dfirst.letter"+letter)))
    } toSeq

    testGrouping in Test <<= groupByFirst( (definedTests in Test).value )
}
```

The tests in a single group are run sequentially. Control the number of
forked JVMs allowed to run at the same time by setting the limit on
`Tags.ForkedTestGroup` tag, which is 1 by default. `Setup` and `Cleanup`
actions cannot be provided with the actual test class loader when a
group is forked.

In addition, forked tests can optionally be run in parallel.  This feature
is still considered experimental, and may be enabled with the following setting :

```scala
testForkedParallel in Test := true
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

#### Integration Tests

The following full build configuration demonstrates integration tests.

```scala
lazy val commonSettings = Seq(
  scalaVersion := "$example_scala_version$",
  organization := "com.example"
)
lazy val specs2core = "org.specs2" %% "specs2-core" % "$example_specs2_version$"

lazy val root = (project in file(".")).
  configs(IntegrationTest).
  settings(commonSettings: _*).
  settings(Defaults.itSettings: _*).
  settings(
    libraryDependencies += specs2core % "it,test"
    // other settings here
  )
```

-   `configs(IntegrationTest)` adds the predefined integration test
    configuration. This configuration is referred to by the name it.
-   `settings(Defaults.itSettings : _*)` adds compilation, packaging,
    and testing actions and settings in the IntegrationTest
    configuration.
-   `settings(libraryDependencies += specs2core % "it,test")` adds specs2 to both the
    standard test configuration and the integration test configuration
    it. To define a dependency only for integration tests, use "it" as
    the configuration instead of "it,test".

The standard source hierarchy is used:

-   `src/it/scala` for Scala sources
-   `src/it/java` for Java sources
-   `src/it/resources` for resources that should go on the integration
    test classpath

The standard testing tasks are available, but must be prefixed with
`it:`. For example,

```
> it:testOnly org.example.AnIntegrationTest
```

Similarly the standard settings may be configured for the
`IntegrationTest` configuration. If not specified directly, most
`IntegrationTest` settings delegate to `Test` settings by default. For
example, if test options are specified as:

```scala
testOptions in Test += ...
```

then these will be picked up by the `Test` configuration and in turn by
the `IntegrationTest` configuration. Options can be added specifically
for integration tests by putting them in the `IntegrationTest`
configuration:

```scala
testOptions in IntegrationTest += ...
```

Or, use `:=` to overwrite any existing options, declaring these to be
the definitive integration test options:

```scala
testOptions in IntegrationTest := Seq(...)
```

#### Custom test configuration

The previous example may be generalized to a custom test configuration.

```scala
lazy val commonSettings = Seq(
  scalaVersion := "$example_scala_version$",
  organization := "com.example"
)
lazy val specs2core = "org.specs2" %% "specs2-core" % "$example_specs2_version$"
lazy val FunTest = config("fun") extend(Test)

lazy val root = (project in file(".")).
  configs(FunTest).
  settings(commonSettings: _*).
  settings(inConfig(FunTest)(Defaults.testSettings): _*).
  settings(
    libraryDependencies += specs2core % FunTest
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
settings(inConfig(FunTest)(Defaults.testSettings): _*)
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
testOptions in FunTest += ...
```

Test tasks are run by prefixing them with `fun:`

```
> fun:test
```

#### Additional test configurations with shared sources

An alternative to adding separate sets of test sources (and
compilations) is to share sources. In this approach, the sources are
compiled together using the same classpath and are packaged together.
However, different tests are run depending on the configuration.

```scala
lazy val commonSettings = Seq(
  scalaVersion := "$example_scala_version$",
  organization := "com.example"
)
lazy val specs2core = "org.specs2" %% "specs2-core" % "$example_specs2_version$"
lazy val FunTest = config("fun") extend(Test)

def itFilter(name: String): Boolean = name endsWith "ITest"
def unitFilter(name: String): Boolean = (name endsWith "Test") && !itFilter(name)

lazy val root = (project in file(".")).
  configs(FunTest).
  settings(commonSettings: _*).
  settings(inConfig(FunTest)(Defaults.testTasks): _*).
  settings(
    libraryDependencies += specs2core % FunTest,
    testOptions in Test := Seq(Tests.Filter(unitFilter)),
    testOptions in FunTest := Seq(Tests.Filter(itFilter))
    // other settings here
  )
```

The key differences are:

-   We are now only adding the test tasks
    (inConfig(FunTest)(Defaults.testTasks)) and not compilation and
    packaging tasks and settings.
-   We filter the tests to be run for each configuration.

To run standard unit tests, run `test` (or equivalently, `test:test`):

```
> test
```

To run tests for the added configuration (here, `"fun"`), prefix it with
the configuration name as before:

```
> fun:test
> fun:testOnly org.example.AFunTest
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
parallelExecution in Serial := false
```

The tests to run in parallel would be run with `test` and the ones to
run in serial would be run with `serial:test`.

### JUnit

Support for JUnit is provided by
[junit-interface](https://github.com/szeiger/junit-interface). To add
JUnit support into your project, add the junit-interface dependency in
your project's main build.sbt file.

```scala
libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % Test
```

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

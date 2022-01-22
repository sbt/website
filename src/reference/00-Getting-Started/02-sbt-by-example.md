---
out: sbt-by-example.html
---

  [Basic-Def]: Basic-Def.html
  [Setup]: Setup.html
  [Running]: Running.html
  [Essential-sbt]: https://www.scalawilliam.com/essential-sbt/

sbt by example
--------------

This page assumes you've [installed sbt 1][Setup].

Let's start with examples rather than explaining how sbt works or why.

### Create a minimum sbt build

```
\$ mkdir foo-build
\$ cd foo-build
\$ touch build.sbt
```

### Start sbt shell

```
\$ sbt
[info] Updated file /tmp/foo-build/project/build.properties: set sbt.version to 1.1.4
[info] Loading project definition from /tmp/foo-build/project
[info] Loading settings from build.sbt ...
[info] Set current project to foo-build (in build file:/tmp/foo-build/)
[info] sbt server started at local:///Users/eed3si9n/.sbt/1.0/server/abc4fb6c89985a00fd95/sock
sbt:foo-build>
```

### Exit sbt shell

To leave sbt shell, type `exit` or use Ctrl+D (Unix) or Ctrl+Z (Windows).

```
sbt:foo-build> exit
```

### Compile a project

As a convention, we will use the `sbt:...>` or `>` prompt to mean that we're in the sbt interactive shell.

```
\$ sbt
sbt:foo-build> compile
```

### Recompile on code change

Prefixing the `compile` command (or any other command) with `~` causes the command to be automatically
re-executed whenever one of the source files within the project is modified. For example:

```
sbt:foo-build> ~compile
[success] Total time: 0 s, completed May 6, 2018 3:52:08 PM
1. Waiting for source changes... (press enter to interrupt)
```

### Create a source file

Leave the previous command running. From a different shell or in your file manager create in the foo-build
directory the following nested directories: `src/main/scala/example`. Then, create `Hello.scala`
in the `example` directory using your favorite editor as follows:

```scala
package example

object Hello {
  def main(args: Array[String]): Unit = {
    println("Hello")
  }
}
```

This new file should be picked up by the running command:

```
[info] Compiling 1 Scala source to /tmp/foo-build/target/scala-2.12/classes ...
[info] Done compiling.
[success] Total time: 2 s, completed May 6, 2018 3:53:42 PM
2. Waiting for source changes... (press enter to interrupt)
```

Press `Enter` to exit `~compile`.

### Run a previous command

From sbt shell, press up-arrow twice to find the `compile` command that you
executed at the beginning.

```
sbt:foo-build> compile
```

### Getting help

Use the `help` command to get basic help about the available commands.

```
sbt:foo-build> help

  about      Displays basic information about sbt and the build.
  tasks      Lists the tasks defined for the current project.
  settings   Lists the settings defined for the current project.
  reload     (Re)loads the current project or changes to plugins project or returns from it.
  new        Creates a new sbt build.
  projects   Lists the names of available projects or temporarily adds/removes extra builds to the session.
  project    Displays the current project or changes to the provided `project`.

....
```

Display the description of a specific task:

```
sbt:foo-build> help run
Runs a main class, passing along arguments provided on the command line.
```

### Run your app

```
sbt:foo-build> run
[info] Packaging /tmp/foo-build/target/scala-2.12/foo-build_2.12-0.1.0-SNAPSHOT.jar ...
[info] Done packaging.
[info] Running example.Hello
Hello
[success] Total time: 1 s, completed May 6, 2018 4:10:44 PM
```

### Set ThisBuild / scalaVersion from sbt shell

```
sbt:foo-build> set ThisBuild / scalaVersion := "$example_scala213$"
[info] Defining ThisBuild / scalaVersion
```

Check the `scalaVersion` setting:

```
sbt:foo-build> scalaVersion
[info] $example_scala213$
```

### Save the session to build.sbt

We can save the ad-hoc settings using `session save`.

```
sbt:foo-build> session save
[info] Reapplying settings...
```

`build.sbt` file should now contain:

```scala
ThisBuild / scalaVersion := "$example_scala213$"

```

### Name your project

Using an editor, change `build.sbt` as follows:

@@snip [name]($root$/src/sbt-test/ref/example-name/build.sbt) {}

### Reload the build

Use the `reload` command to reload the build. The command causes the
`build.sbt` file to be re-read, and its settings applied.

```
sbt:foo-build> reload
[info] Loading project definition from /tmp/foo-build/project
[info] Loading settings from build.sbt ...
[info] Set current project to Hello (in build file:/tmp/foo-build/)
sbt:Hello>
```

Note that the prompt has now changed to `sbt:Hello>`.

### Add ScalaTest to libraryDependencies

Using an editor, change `build.sbt` as follows:

@@snip [scalatest]($root$/src/sbt-test/ref/example-scalatest/build.sbt) {}

Use the `reload` command to reflect the change in `build.sbt`.

```
sbt:Hello> reload
```

### Run tests

```
sbt:Hello> test
```

### Run incremental tests continuously

```
sbt:Hello> ~testQuick
```

### Write a test

Leaving the previous command running, create a file named `src/test/scala/HelloSpec.scala`
using an editor:

@@snip [scalatest]($root$/src/sbt-test/ref/example-scalatest/src/test/scala/HelloSpec.scala) {}

`~testQuick` should pick up the change:

```
2. Waiting for source changes... (press enter to interrupt)
[info] Compiling 1 Scala source to /tmp/foo-build/target/scala-2.12/test-classes ...
[info] Done compiling.
[info] HelloSpec:
[info] - Hello should start with H *** FAILED ***
[info]   assert("hello".startsWith("H"))
[info]          |       |          |
[info]          "hello" false      "H" (HelloSpec.scala:5)
[info] Run completed in 135 milliseconds.
[info] Total number of tests run: 1
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 0, failed 1, canceled 0, ignored 0, pending 0
[info] *** 1 TEST FAILED ***
[error] Failed tests:
[error]   HelloSpec
[error] (Test / testQuick) sbt.TestsFailedException: Tests unsuccessful
```

### Make the test pass

Using an editor, change `src/test/scala/HelloSpec.scala` to:

@@snip [scalatest]($root$/src/sbt-test/ref/example-scalatest/changes/HelloSpec.scala) {}

Confirm that the test passes, then press `Enter` to exit the continuous test.

### Add a library dependency

Using an editor, change `build.sbt` as follows:

@@snip [example-library]($root$/src/sbt-test/ref/example-library/build.sbt) {}

Use the `reload` command to reflect the change in `build.sbt`.

### Use Scala REPL

We can find out the current weather in New York.

```scala
sbt:Hello> console
[info] Starting scala interpreter...
Welcome to Scala 2.12.7 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_171).
Type in expressions for evaluation. Or try :help.

scala> :paste
// Entering paste mode (ctrl-D to finish)

import scala.concurrent._, duration._
import gigahorse._, support.okhttp.Gigahorse
import play.api.libs.json._

Gigahorse.withHttp(Gigahorse.config) { http =>
  val baseUrl = "https://www.metaweather.com/api/location"
  val rLoc = Gigahorse.url(baseUrl + "/search/").get.
    addQueryString("query" -> "New York")
  val fLoc = http.run(rLoc, Gigahorse.asString)
  val loc = Await.result(fLoc, 10.seconds)
  val woeid = (Json.parse(loc) \\ 0 \\ "woeid").get
  val rWeather = Gigahorse.url(baseUrl + s"/\$woeid/").get
  val fWeather = http.run(rWeather, Gigahorse.asString)
  val weather = Await.result(fWeather, 10.seconds)
  ({Json.parse(_: String)} andThen Json.prettyPrint)(weather)
}

// press Ctrl+D

// Exiting paste mode, now interpreting.

import scala.concurrent._
import duration._
import gigahorse._
import support.okhttp.Gigahorse
import play.api.libs.json._
res0: String =
{
  "consolidated_weather" : [ {
    "id" : 6446939314847744,
    "weather_state_name" : "Light Rain",
    "weather_state_abbr" : "lr",
    "wind_direction_compass" : "WNW",
    "created" : "2019-02-21T04:39:47.747805Z",
    "applicable_date" : "2019-02-21",
    "min_temp" : 0.48000000000000004,
    "max_temp" : 7.84,
    "the_temp" : 2.1700000000000004,
    "wind_speed" : 5.996333145703094,
    "wind_direction" : 293.12257757287307,
    "air_pressure" : 1033.115,
    "humidity" : 77,
    "visibility" : 14.890539250775472,
    "predictability" : 75
  }, {
    "id" : 5806299509948416,
    "weather_state_name" : "Heavy Cloud",
...

scala> :q // to quit
```

### Make a subproject

Change `build.sbt` as follows:

@@snip [example-sub1]($root$/src/sbt-test/ref/example-sub1/build.sbt) {}

Use the `reload` command to reflect the change in `build.sbt`.

### List all subprojects

```
sbt:Hello> projects
[info] In file:/tmp/foo-build/
[info]   * hello
[info]     helloCore
```

### Compile the subproject

```
sbt:Hello> helloCore/compile
```

### Add ScalaTest to the subproject

Change `build.sbt` as follows:

@@snip [example-sub2]($root$/src/sbt-test/ref/example-sub2/build.sbt) {}

### Broadcast commands

Set aggregate so that the command sent to `hello` is broadcast to `helloCore` too:

@@snip [example-sub3]($root$/src/sbt-test/ref/example-sub3/build.sbt) {}

After `reload`, `~testQuick` now runs on both subprojects:

```scala
sbt:Hello> ~testQuick
```

Press `Enter` to exit the continuous test.

### Make hello depend on helloCore

Use `.dependsOn(...)` to add a dependency on other subprojects. Also let's move the Gigahorse dependency to `helloCore`.

@@snip [example-sub4]($root$/src/sbt-test/ref/example-sub4/build.sbt) {}

### Parse JSON using Play JSON

Let's add Play JSON to `helloCore`.

@@snip [example-weather-build]($root$/src/sbt-test/ref/example-weather/build.sbt) {}

After `reload`, add `core/src/main/scala/example/core/Weather.scala`:

```scala
package example.core

import gigahorse._, support.okhttp.Gigahorse
import scala.concurrent._, duration._
import play.api.libs.json._

object Weather {
  lazy val http = Gigahorse.http(Gigahorse.config)

  def weather: Future[String] = {
    val baseUrl = "https://www.metaweather.com/api/location"
    val locUrl = baseUrl + "/search/"
    val weatherUrl = baseUrl + "/%s/"
    val rLoc = Gigahorse.url(locUrl).get.
      addQueryString("query" -> "New York")
    import ExecutionContext.Implicits.global
    for {
      loc <- http.run(rLoc, parse)
      woeid = (loc \\ 0  \\ "woeid").get
      rWeather = Gigahorse.url(weatherUrl format woeid).get
      weather <- http.run(rWeather, parse)
    } yield (weather \\\\ "weather_state_name")(0).as[String].toLowerCase
  }

  private def parse = Gigahorse.asString andThen Json.parse
}
```

Next, change `src/main/scala/example/Hello.scala` as follows:

```scala
package example

import scala.concurrent._, duration._
import core.Weather

object Hello {
  def main(args: Array[String]): Unit = {
    val w = Await.result(Weather.weather, 10.seconds)
    println(s"Hello! The weather in New York is \$w.")
    Weather.http.close()
  }
}
```

Let's run the app to see if it worked:

```
sbt:Hello> run
[info] Compiling 1 Scala source to /tmp/foo-build/core/target/scala-2.12/classes ...
[info] Done compiling.
[info] Compiling 1 Scala source to /tmp/foo-build/target/scala-2.12/classes ...
[info] Packaging /tmp/foo-build/core/target/scala-2.12/hello-core_2.12-0.1.0-SNAPSHOT.jar ...
[info] Done packaging.
[info] Done compiling.
[info] Packaging /tmp/foo-build/target/scala-2.12/hello_2.12-0.1.0-SNAPSHOT.jar ...
[info] Done packaging.
[info] Running example.Hello
Hello! The weather in New York is mostly cloudy.
```

### Add sbt-native-packager plugin

Using an editor, create `project/plugins.sbt`:

@@snip [example-weather-plugins]($root$/src/sbt-test/ref/example-weather/changes/plugins.sbt) {}

Next change `build.sbt` as follows to add `JavaAppPackaging`:

@@snip [example-weather-build2]($root$/src/sbt-test/ref/example-weather/changes/build.sbt) {}

### Reload and create a .zip distribution

```
sbt:Hello> reload
...
sbt:Hello> dist
[info] Wrote /tmp/foo-build/target/scala-2.12/hello_2.12-0.1.0-SNAPSHOT.pom
[info] Wrote /tmp/foo-build/core/target/scala-2.12/hello-core_2.12-0.1.0-SNAPSHOT.pom
[info] Your package is ready in /tmp/foo-build/target/universal/hello-0.1.0-SNAPSHOT.zip
```

Here's how you can run the packaged app:

```
\$ /tmp/someother
\$ cd /tmp/someother
\$ unzip -o -d /tmp/someother /tmp/foo-build/target/universal/hello-0.1.0-SNAPSHOT.zip
\$ ./hello-0.1.0-SNAPSHOT/bin/hello
Hello! The weather in New York is mostly cloudy.
```

### Dockerize your app

```
sbt:Hello> Docker/publishLocal
....
[info] Successfully built b6ce1b6ab2c0
[info] Successfully tagged hello:0.1.0-SNAPSHOT
[info] Built image hello:0.1.0-SNAPSHOT
```

Here's how to run the Dockerized app:

```
\$ docker run hello:0.1.0-SNAPSHOT
Hello! The weather in New York is mostly cloudy
```

### Set the version

Change `build.sbt` as follows:

@@snip [example-weather-build3]($root$/src/sbt-test/ref/example-weather/changes/build3.sbt) {}

### Switch scalaVersion temporarily

```
sbt:Hello> ++2.12.14!
[info] Forcing Scala version to 2.12.14 on all projects.
[info] Reapplying settings...
[info] Set current project to Hello (in build file:/tmp/foo-build/)
```

Check the `scalaVersion` setting:

```
sbt:Hello> scalaVersion
[info] helloCore / scalaVersion
[info]  2.12.14
[info] scalaVersion
[info]  2.12.14
```

This setting will go away after `reload`.

### Inspect the dist task

To find out more about `dist`, try `help` and `inspect`.

```scala
sbt:Hello> help dist
Creates the distribution packages.
sbt:Hello> inspect dist
```

To call inspect recursively on the dependency tasks use `inspect tree`.

```scala
sbt:Hello> inspect tree dist
[info] dist = Task[java.io.File]
[info]   +-Universal / dist = Task[java.io.File]
....
```

### Batch mode

You can also run sbt in batch mode, passing sbt commands directly from the terminal.

```
\$ sbt clean "testOnly HelloSpec"
```

**Note**: Running in batch mode requires JVM spinup and JIT each time,
so **your build will run much slower**.
For day-to-day coding, we recommend using the sbt shell
or a continuous test like `~testQuick`.

### sbt new command

You can use the sbt `new` command to quickly setup a simple "Hello world" build.

```
\$ sbt new scala/scala-seed.g8
....
A minimal Scala project.

name [My Something Project]: hello

Template applied in ./hello
```

When prompted for the project name, type `hello`.

This will create a new project under a directory named `hello`.

### Credits

This page is based on the [Essential sbt][essential-sbt] tutorial written by William "Scala William" Narmontas.

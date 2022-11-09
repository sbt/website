---
out: Basic-Def-Examples.html
---

  [Basic-Def]: Basic-Def.html

.sbt build examples
-------------------

**Note**: As of sbt 0.13.7 blank lines are no longer used to delimit `build.sbt` files. The following example requires sbt 0.13.7+.

Listed here are some examples of settings (each setting is independent).
See [.sbt build definition][Basic-Def] for details.

```scala
import scala.concurrent.duration._

// factor out common settings
ThisBuild / organization := "org.myproject"
ThisBuild / scalaVersion := "$example_scala_version$"
// set the Scala version used for the project
ThisBuild / version      := "0.1.0-SNAPSHOT"

// set the prompt (for this build) to include the project id.
ThisBuild / shellPrompt := { state => Project.extract(state).currentRef.project + "> " }

// define ModuleID for library dependencies
lazy val scalacheck = "org.scalacheck" %% "scalacheck" % "$example_scalacheck_version$"

// define ModuleID using string interpolator
lazy val osmlibVersion = "2.5.2-RC1"
lazy val osmlib = ("net.sf.travelingsales" % "osmlib" % osmlibVersion from
  s"""http://downloads.sourceforge.net/project/travelingsales/libosm/\$osmlibVersion/libosm-\$osmlibVersion.jar""")

lazy val root = (project in file("."))
  .settings(
    // set the name of the project
    name := "My Project",

    // set the main Scala source directory to be <base>/src
    Compile / scalaSource := baseDirectory.value / "src",

    // set the Scala test source directory to be <base>/test
    Test / scalaSource := baseDirectory.value / "test",

    // add a test dependency on ScalaCheck
    libraryDependencies += scalacheck % Test,

    // add compile dependency on osmlib
    libraryDependencies += osmlib,

    // reduce the maximum number of errors shown by the Scala compiler
    maxErrors := 20,

    // increase the time between polling for file changes when using continuous execution
    pollInterval := 1.second,

    // append several options to the list of options passed to the Java compiler
    javacOptions ++= Seq("-source", "1.5", "-target", "1.5"),

    // append -deprecation to the options passed to the Scala compiler
    scalacOptions += "-deprecation",

    // define the statements initially evaluated when entering 'console', 'consoleQuick', or 'consoleProject'
    initialCommands := """
      |import System.{currentTimeMillis => now}
      |def time[T](f: => T): T = {
      |  val start = now
      |  try { f } finally { println("Elapsed: " + (now - start)/1000.0 + " s") }
      |}""".stripMargin,

    // set the initial commands when entering 'console' or 'consoleQuick', but not 'consoleProject'
    console / initialCommands := "import myproject._",

    // set the main class for packaging the main jar
    // 'run' will still auto-detect and prompt
    // change Compile to Test to set it for the test jar
    Compile / packageBin / mainClass := Some("myproject.MyMain"),

    // set the main class for the main 'run' task
    // change Compile to Test to set it for 'Test/run'
    Compile / run / mainClass := Some("myproject.MyMain"),

    // add <base>/input to the files that '~' triggers on
    watchSources += baseDirectory.value / "input",

    // add a maven-style repository
    resolvers += "name" at "url",

    // add a sequence of maven-style repositories
    resolvers ++= Seq("name" at "url"),

    // define the repository to publish to
    publishTo := Some("name" at "url"),

    // set Ivy logging to be at the highest level
    ivyLoggingLevel := UpdateLogging.Full,

    // disable updating dynamic revisions (including -SNAPSHOT versions)
    offline := true,

    // set the prompt (for the current project) to include the username
    shellPrompt := { state => System.getProperty("user.name") + "> " },

    // disable printing timing information, but still print [success]
    showTiming := false,

    // disable printing a message indicating the success or failure of running a task
    showSuccess := false,

    // change the format used for printing task completion time
    timingFormat := {
        import java.text.DateFormat
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
    },

    // disable using the Scala version in output paths and artifacts
    crossPaths := false,

    // fork a new JVM for 'run' and 'Test/run'
    fork := true,

    // fork a new JVM for 'Test/run', but not 'run'
    Test / fork := true,

    // add a JVM option to use when forking a JVM for 'run'
    javaOptions += "-Xmx2G",

    // only use a single thread for building
    parallelExecution := false,

    // Execute tests in the current project serially
    //   Tests from other projects may still run concurrently.
    Test / parallelExecution := false,

    // set the location of the JDK to use for compiling Java code.
    // if 'fork' is true, this is used for 'run' as well
    javaHome := Some(file("/usr/lib/jvm/sun-jdk-1.6")),

    // Use Scala from a directory on the filesystem instead of retrieving from a repository
    scalaHome := Some(file("/home/user/scala/trunk/")),

    // don't aggregate clean (See FullConfiguration for aggregation details)
    clean / aggregate := false,

    // only show warnings and errors on the screen for compilations.
    //  this applies to both Test/compile and compile and is Info by default
    compile / logLevel := Level.Warn,

    // only show warnings and errors on the screen for all tasks (the default is Info)
    //  individual tasks can then be more verbose using the previous setting
    logLevel := Level.Warn,

    // only store messages at info and above (the default is Debug)
    //   this is the logging level for replaying logging with 'last'
    persistLogLevel := Level.Debug,

    // only show 10 lines of stack traces
    traceLevel := 10,

    // only show stack traces up to the first sbt stack frame
    traceLevel := 0,

    // add SWT to the unmanaged classpath
    Compile / unmanagedJars += Attributed.blank(file("/usr/share/java/swt.jar")),

    // publish test jar, sources, and docs
    Test / publishArtifact := true,

    // disable publishing of main docs
    Compile / packageDoc / publishArtifact := false,

    // change the classifier for the docs artifact
    packageDoc / artifactClassifier := Some("doc"),

    // Copy all managed dependencies to <build-root>/lib_managed/
    //   This is essentially a project-local cache.  There is only one
    //   lib_managed/ in the build root (not per-project).
    retrieveManaged := true,

    /* Specify a file containing credentials for publishing. The format is:
    realm=Sonatype Nexus Repository Manager
    host=nexus.scala-tools.org
    user=admin
    password=admin123
    */
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),

    // Directly specify credentials for publishing.
    credentials += Credentials("Sonatype Nexus Repository Manager", "nexus.scala-tools.org", "admin", "admin123"),

    // Exclude transitive dependencies, e.g., include log4j without including logging via jdmk, jmx, or jms.
    libraryDependencies +=
      "log4j" % "log4j" % "1.2.15" excludeAll(
        ExclusionRule(organization = "com.sun.jdmk"),
        ExclusionRule(organization = "com.sun.jmx"),
        ExclusionRule(organization = "javax.jms")
      )
  )
```

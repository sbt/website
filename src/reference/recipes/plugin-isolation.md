Isolate plugin classpath
========================

```admonish note
The recipe section of the documentation focuses on the objectives
with minimal explanations about the basics.
```

Objective
---------

I want to isolate plugin classpath, for example so I can use a Scala 2.12 app.

Steps
-----

1. Start with a command-line application published to Maven Central, for example Coursier CLI, or your own application.
2. Define a synthetic subproject that wraps the command-line app:
   ```scala
   package example

   import sbt.{ *, given }
   import Keys.*

   object BootstrapPlugin extends AutoPlugin:
     override lazy val requires = sbt.plugins.JvmPlugin

     lazy val bootstrapCs = project
       .settings(
         scalaVersion := "2.12.21",
         libraryDependencies += "io.get-coursier" %% "coursier-cli" % "2.1.14",
         Compile / run / mainClass := Some("coursier.cli.Coursier"),
         // applicable to sbt 2.x only
         clientSide := false,
       )

     override lazy val extraProjects = Vector(bootstrapCs)

     ....
   end BootstrapPlugin
   ```
3. Define a setting that represents the command-line argument and a dynamic task that calls `run` task on the synthetic subproject.

~~~admonish example title="project/BootstrapPlugin.scala"
```scala
package example

import sbt.{ *, given }
import Keys.*

object BootstrapPlugin extends AutoPlugin:
  override lazy val requires = sbt.plugins.JvmPlugin

  object autoImport:
    val packageBootstrap = taskKey[HashedVirtualFileRef]("packageBootstrap")
    val packageBootstrapArgs = settingKey[Seq[String]]("packageBootstrapArgs")
    val packageBootstrapOutput = settingKey[File]("packageBootstrapOutput")
  end autoImport
  import autoImport.*

  lazy val bootstrapCs = project
    .settings(
      scalaVersion := "2.12.21",
      libraryDependencies += "io.get-coursier" %% "coursier-cli" % "2.1.14",
      Compile / run / mainClass := Some("coursier.cli.Coursier"),
      // applicable to sbt 2.x only
      clientSide := false,
    )

  override lazy val extraProjects = Vector(bootstrapCs)

  override lazy val projectSettings = Vector(
    packageBootstrapOutput := target.value / "bootstrap" / s"${moduleName.value}.jar",
    packageBootstrapArgs := {
      val coord =
        s"${organization.value}:${name.value}_${scalaBinaryVersion.value}:${version.value}"
      val sv = scalaVersion.value
      Vector("bootstrap", "--verbose", "--bat=true",
        "--scala-version", sv,
        "-f", coord,
        "-o", packageBootstrapOutput.value.toString)
    },
    packageBootstrap := Def.uncached {
      // to process args before toTask, we need to use dynamic tasks
      (Def.taskDyn {
        // phase 1
        val c = fileConverter.value
        val args = packageBootstrapArgs.value
        val out = packageBootstrapOutput.value
        // phase 2
        val outVf: HashedVirtualFileRef = c.toVirtualFile(out.toPath())
        IO.createDirectory(out.getParentFile())
        // phase 3
        (bootstrapCs / Compile / run)
          .toTask(args.mkString(" ", " ", ""))
          .map(_ => outVf)
      }).value
    },
  )
end BootstrapPlugin
```
~~~

~~~admonish note
If the command-line app calls `sys.exit(1)`, you have to fork the `run`:

```scala
// default for sbtn
clientSide := true,
// for sbt --server
Compile / run / fork := true,
```
~~~

Test
----

```bash
$ sbt app/publishLocal
$ sbt app/packageBootstrap
[info] running coursier.cli.Coursier bootstrap --verbose --bat=true 
  --scala-version 3.8.4 -f com.example:hello_3:0.1.0-SNAPSHOT
  -o /.../isolation/target/out/jvm/scala-3.8.4/hello/bootstrap/hello.jar
  Dependencies:
com.example:hello_3:0.1.0-SNAPSHOT:
Wrote target/out/jvm/scala-3.8.4/hello/bootstrap/hello.jar
Wrote target/out/jvm/scala-3.8.4/hello/bootstrap/hello.jar.bat
[success] elapsed time: 3 s, cache 100%, 17 disk cache hits
$ target/out/jvm/scala-3.8.4/hello/bootstrap/hello.jar world
hi world
```

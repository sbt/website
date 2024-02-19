Creating a new build
====================

To start a new build with `sbt`, use `sbt new`.

```bash
$ mkdir /tmp/foo
$ cd /tmp/foo
$ sbt new

Welcome to sbt new!
Here are some templates to get started:
 a) scala/toolkit.local               - Scala Toolkit (beta) by Scala Center and VirtusLab
 b) typelevel/toolkit.local           - Toolkit to start building Typelevel apps
 c) sbt/cross-platform.local          - A cross-JVM/JS/Native project
 d) scala/scala3.g8                   - Scala 3 seed template
 e) scala/scala-seed.g8               - Scala 2 seed template
 f) playframework/play-scala-seed.g8  - A Play project in Scala
 g) playframework/play-java-seed.g8   - A Play project in Java
 i) softwaremill/tapir.g8             - A tapir project using Netty
 m) scala-js/vite.g8                  - A Scala.JS + Vite project
 n) holdenk/sparkProjectTemplate.g8   - A Scala Spark project
 o) spotify/scio.g8                   - A Scio project
 p) disneystreaming/smithy4s.g8       - A Smithy4s project
 q) quit
Select a template:
```

If you select "a", you will be prompted by more questions:

```bash
Select a template: a
Scala version (default: 3.3.0):
Scala Toolkit version (default: 0.2.0):
```

Hit return key to select the default values.

```
[info] Updated file /private/tmp/bar/project/build.properties: set sbt.version to 1.9.8
[info] welcome to sbt 1.9.8 (Azul Systems, Inc. Java 1.8.0_352)
....
[info] set current project to bar (in build file:/private/tmp/foo/)
[info] sbt server started at local:///Users/eed3si9n/.sbt/1.0/server/d0ac1409c0117a949d47/sock
[info] started sbt server
sbt:bar> exit
[info] shutting down sbt server
```

Here are the files that are created by this template:

```bash
.
├── build.sbt
├── project
│   └── build.properties
├── src
│   ├── main
│   │   └── scala
│   │       └── example
│   │           └── Main.scala
│   └── test
│       └── scala
│           └── example
│               └── ExampleSuite.scala
└── target
```

Let's take a look at the `build.sbt` file:

```scala
val toolkitV = "0.2.0"
val toolkit = "org.scala-lang" %% "toolkit" % toolkitV
val toolkitTest = "org.scala-lang" %% "toolkit-test" % toolkitV

scalaVersion := "3.3.0"
libraryDependencies += toolkit
libraryDependencies += (toolkitTest % Test)
```

This is called a **build definition**, and it contains the information sbt needs to compile your project. This is written in `.sbt` format, a subset of Scala language.

Here's what's in `src/main/scala/example/Main.scala`:

```scala
package example

@main def main(args: String*): Unit =
  println(s"Hello ${args.mkString}")
```

This is a Hello world template. We can run it from the sbt shell by starting `sbt --client` and typing `run <your_name>` inside the shell:

```
$ sbt --client
[info] entering *experimental* thin client - BEEP WHIRR
[info] server was not detected. starting an instance
....
info] terminate the server with `shutdown`
[info] disconnect from the server with `exit`
sbt:bar> run Raj
[info] running example.main Raj
Hello Raj
[success] Total time: 0 s, completed Feb 18, 2024 2:38:10 PM
```

### Giter8 templates

In addition to a few `.local` templates, `sbt new` integrates with [Giter8](https://www.foundweekends.org/giter8/),
and open templating system that uses GitHub to host templates. For example, `scala/scala3.g8` is maintained by the Scala team to create a new Scala 3 build:

```
$ /tmp
$ sbt new scala/scala3.g8
```

[Giter8 wiki](https://github.com/foundweekends/giter8/wiki/giter8-templates) lists over 100 templates that can jump start your new build.

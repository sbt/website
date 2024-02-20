Why sbt exists
==============

Preliminaries
-------------

In Scala, a library or a program is compiled using the Scala compiler, `scalac`, as documented in the [Scala 3 Book](https://docs.scala-lang.org/scala3/book/taste-hello-world.html):

```scala
@main def hello() = println("Hello, World!")
```

```bash
$ scalac hello.scala
$ scala hello
Hello, World!
```

This process gets tedious and slow if we were to invoke `scalac` directly since we'd have to pass all the Scala source file names.

Furthermore, most non-trivial programs will likely have library dependencies, and will therefore also depend transitively on their dependencies.
This is doubly complicated for Scala ecosystem because we have Scala 2.12, 2.13 ecosystem, Scala 3.x ecosystem, JVM, JS, and Native platforms.

Rather than working with JAR files and `scalac`, we can avoid manual toil by introducing a higher-level subproject abstraction and by using a build tool.

sbt
---

*sbt* is a simple build tool created for Scala and Java.
It lets us declare subprojects and their various dependencies and custom tasks to ensure that we'll always get a fast, repeatable build.

To accomplish this goal, sbt does several things:

- The version of sbt itself is tracked in `project/build.properties`.
- Defines a domain-specific language (DSL) called **build.sbt DSL** that can declare the Scala version and other subproject information in `build.sbt`.
- Uses Coursier to fetch subprojects dependencies and their dependencies.
- Invokes Zinc to incrementally compile Scala and Java sources.
- Automatically runs tasks in parallel whenever possible.
- Defines conventions on how packages are published to Maven repositories to interoperate with the wider JVM ecosystem.

To a large extent, sbt standardizes the commands needed to build a given program or library.

Why build.sbt DSL?
------------------

build.sbt DSL makes sbt a unique build tool,
as opposed to other tools that use configuration file formats like YAML, TOML, and XML.
Originally developed beween 2010 and 2013, `build.sbt` can start almost like a YAML file, declaring just `scalaVersion` and `libraryDependencies`,
but it can supports more features to keep the build definition organized as the build grows larger:

- To avoid repeating the same information, like the version number for a library, `build.sbt` can declare variables using `val`.
- Uses Scala language constructs like `if` to define settings and tasks, when needed.
- Statically typed settings and tasks, to catch typos and type errors before the build starts. The type also helps passing data from one task from another.
- Provides **structured concurrency** via `Initialized[Task[A]]`. The DSL uses *direct style* `.value` syntax to concisely define task graphs.
- Enpowers the community to extend sbt with plugins that provide custom tasks or language extensions like Scala.JS.

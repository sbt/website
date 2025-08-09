sbt compile
===========

Synopsis
--------

`sbt` \[_query_ / \] `compile`<br>
`sbt` \[_query_ / \] `Test` / `compile`<br>

Description
-----------

The `compile` task compiles the selected subprojects and their subproject dependencies. Since sbt 2.x, the compiled artifacts are cached automatically.

Compiling Scala code with the raw Scala compiler has been slow, so significant poriton of sbt's development efforts deal with various strategies for speeding up compilation.

### Reduce the overhead of restarting the compiler

sbt server stays up in the background, allowing Scala compilation to run the same Java virtual machine (JVM). Keeping the JVM warm makes compilation significantly faster because it takes a long time to classload the compiler and for the Just-in-Time compiler to optimize it.

### Incremental compilation

When a source file `A.scala` is modified, sbt goes to great effort
to minimize the other source files recompiled due to `A.scala`'s change. This process of tracking dependencies between the language constructs and recompiling only the required sources is called _incremental compilation_.

### (Remote) caching

In sbt 2.x, compiled artifacts are not only cached across the sessions and builds, but can optionally be cached across different machines using Bazel-compatible remote cache. See [Caching](../concepts/caching.md) for details.

### Test / compile

Scoping the `compile` task with a configuration, like `Test / compile` will compile the test sources and their source dependencies.

### Compilation settings

#### scalaVersion

The version of Scala used for compilation.

```scala
scalaVersion := "{{scala3_example_version}}"
```

#### scalacOptions

Options for the Scala compiler.

```scala
Compile / scalacOptions += "-Werror"
```

#### javacOptions

Options for the Java compiler.

```scala
Compile / javacOptions ++= List("-Xlint", "-Xlint:-serial")
```

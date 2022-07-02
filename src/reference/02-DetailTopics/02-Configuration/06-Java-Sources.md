---
out: Java-Sources.html
---

Java Sources
------------

sbt has support for compiling Java sources with the limitation that
dependency tracking is limited to the dependencies present in compiled
class files.

### Usage

-   `compile` will compile the sources under `src/main/java` by default.
-   `testCompile` will compile the sources under `src/test/java` by
    default.

Pass options to the Java compiler by setting `javacOptions`:

```scala
javacOptions += "-g:none"
```

As with options for the Scala compiler, the arguments are not parsed by
sbt. Multi-element options, such as `-source 1.5`, are specified like:

```scala
javacOptions ++= Seq("-source", "1.5")
```

You can specify the order in which Scala and Java sources are built with
the `compileOrder` setting. Possible values are from the `CompileOrder`
enumeration: `Mixed`, `JavaThenScala`, and `ScalaThenJava`. If you have
circular dependencies between Scala and Java sources, you need the
default, `Mixed`, which passes both Java and Scala sources to `scalac`
and then compiles the Java sources with `javac`. If you do not have
circular dependencies, you can use one of the other two options to speed
up your build by not passing the Java sources to `scalac`. For example,
if your Scala sources depend on your Java sources, but your Java sources
do not depend on your Scala sources, you can do:

```scala
compileOrder := CompileOrder.JavaThenScala
```

To specify different orders for main and test sources, scope the setting
by configuration:

```scala
// Java then Scala for main sources
Compile / compileOrder := CompileOrder.JavaThenScala

// allow circular dependencies for test sources
Test / compileOrder := CompileOrder.Mixed
```

Note that in an incremental compilation setting, it is not practical to
ensure complete isolation between Java sources and Scala sources because
they share the same output directory. So, previously compiled classes
not involved in the current recompilation may be picked up. A clean
compile will always provide full checking, however.

### Known issues in mixed mode compilation

The Scala compiler does not identify compile-time constant variables
(Java specification [4.12.4](https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.12.4))
in Java source code if their definition is not a literal.
This issue has several symptoms, described in the Scala ticket [SI-5333](https://github.com/scala/bug/issues/5333):

1. The selection of a (non-literal) constant variable is rejected when used as an argument
to a Java annotation (a compile-time constant expression is required).
2. The selection of a constant variable is not replaced by its value, but compiled
as an actual field load (the
[Scala specification 4.1](https://www.scala-lang.org/files/archive/spec/2.13/04-basic-declarations-and-definitions.html#value-declarations-and-definitions)
defines that constant expressions should be replaced by their values).

Since Scala 2.11.4, a similar issue arises when using a Java-defined annotation in
a Scala class. The Scala compiler does not recognize `@Retention` annotations when
parsing the annotation `@interface` from source and therefore emits the annotation
with visibility `RUNTIME` ([SI-8928](https://github.com/scala/bug/issues/8928)).

### Ignoring the Scala source directories

By default, sbt includes `src/main/scala` and `src/main/java` in its
list of unmanaged source directories. For Java-only projects, the
unnecessary Scala directories can be ignored by modifying
`unmanagedSourceDirectories`:

```scala
// Include only src/main/java in the compile configuration
Compile / unmanagedSourceDirectories := (Compile / javaSource).value :: Nil

// Include only src/test/java in the test configuration
Test / unmanagedSourceDirectories := (Test / javaSource).value :: Nil
```

However, there should not be any harm in leaving the Scala directories
if they are empty.

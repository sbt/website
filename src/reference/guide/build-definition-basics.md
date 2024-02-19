Build definition basics
=======================

This page discusses the `build.sbt` build definition.

### What is a build definition?

A *build definition* is defined in `build.sbt`,
and it consists of a set of projects (of type [`Project`](../api/sbt/Project.html)).
Because the term *project* can be ambiguous,
we often call it a *subproject* in this guide.

For instance, in `build.sbt` you define
the subproject located in the current directory like this:

```scala
{{#include ../../../src/sbt-test/ref/bare/build.sbt}}
```

or more explicitly:

```scala
{{#include ../../../src/sbt-test/ref/basic/build.sbt}}
```

Each subproject is configured by key-value pairs.
For example, one key is `name` and it maps to a string value, the name of
your subproject.
The key-value pairs are listed under the `.settings(...)` method.

build.sbt DSL
-------------

`build.sbt` defines subprojects using a DSL called build.sbt DSL, which is based on Scala.
Initially you can use build.sbt DSL, like a YAML file, declaring just `scalaVersion` and `libraryDependencies`,
but it can supports more features to keep the build definition organized as the build grows larger.

### Typed setting expression

Let's take a closer look at the `build.sbt` DSL:

```scala
organization  :=         "com.example"
^^^^^^^^^^^^  ^^^^^^^^   ^^^^^^^^^^^^^
key           operator   (setting/task) body
```

Each entry is called a *setting expression*.
Some among them are also called task expressions.
We will see more on the difference later in this page.

A setting expression consists of three parts:

1. Left-hand side is a *key*.
2. *Operator*, which in this case is `:=`
3. Right-hand side is called the *body*, or the *setting/task body*.

On the left-hand side, `name`, `version`, and `scalaVersion` are *keys*.
A key is an instance of
[`SettingKey[A]`](../../api/sbt/SettingKey.html),
[`TaskKey[A]`](../../api/sbt/TaskKey.html), or
[`InputKey[A]`](../../api/sbt/InputKey.html) where `A` is the
expected value type.

Because key `name` is typed to `SettingKey[String]`,
the `:=` operator on `name` is also typed specifically to `String`.
If you use the wrong value type, the build definition will not compile:

```scala
name := 42 // will not compile
```

### `val`s and `lazy val`s

To avoid repeating the same information, like the version number for a library,
`build.sbt` may be interspersed with `val`s, `lazy val`s, and `def`s.

```scala
val toolkitV = "0.2.0"
val toolkit = "org.scala-lang" %% "toolkit" % toolkitV
val toolkitTest = "org.scala-lang" %% "toolkit-test" % toolkitV

scalaVersion := "{{scala3_example_version}}"
libraryDependencies += toolkit
libraryDependencies += (toolkitTest % Test)
```

In the above, `val` defines a variable, which are initialized from the top to bottom.
This means that `toolkitV` must be defined before it is referenced.

Here's a bad example:

```scala
// bad example
val toolkit = "org.scala-lang" %% "toolkit" % toolkitV // uninitialized reference!
val toolkitTest = "org.scala-lang" %% "toolkit-test" % toolkitV // uninitialized reference!
val toolkitV = "0.2.0"
```

sbt will fail to load with `java.lang.ExceptionInInitializerError` cased by a `NullPointerException` if your build.sbt contains an uninitialized forward reference.
One way to let the compiler fix this is to define the variables as `lazy`:

```scala
lazy val toolkit = "org.scala-lang" %% "toolkit" % toolkitV
lazy val toolkitTest = "org.scala-lang" %% "toolkit-test" % toolkitV
lazy val toolkitV = "0.2.0"
```

Some frown upon gratuitous `lazy val`s, but Scala 3 lazy vals are efficient,
and we think it makes the build definition more robust for copy-pasting.

```admonish note
Top-level objects and classes are not allowed in `build.sbt`.
Those should go in the `project/` directory as Scala source files.
```

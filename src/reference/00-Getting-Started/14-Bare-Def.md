---
out: Bare-Def.html
---

  [More-About-Settings]: More-About-Settings.html
  [Full-Def]: Full-Def.html
  [Basic-Def]: Basic-Def.html

Appendix: Bare .sbt build definition
------------------------------------

This page describes an old style of `.sbt` build definition.
The current recommendation is to use [Multi-project .sbt build definition][Basic-Def].

### What is a bare .sbt build definition

Unlike [Multi-project .sbt build definition][Basic-Def] and [.scala build definition][Full-Def]
that explicitly define a [Project](../api/sbt/Project.html) definition,
bare build definition implicitly defines one based on the location of the `.sbt` file.

Instead of defining `Project`s, bare `.sbt` build definition consists of
a list of `Setting[_]` expressions.

```scala
name := "hello"

version := "1.0"

scalaVersion := "$example_scala_version$"
```

### (Pre 0.13.7) Settings must be separated by blank lines

**Note**: This blank line delimitation will no longer be needed after 0.13.7.

You can't write a bare build.sbt like this:

```scala
// will NOT compile, no blank lines
name := "hello"
version := "1.0"
scalaVersion := "2.10.3"
```

sbt needs some kind of delimiter to tell where one expression stops and
the next begins.

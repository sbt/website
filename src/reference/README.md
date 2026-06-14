The Book of sbt (Draft)
=======================

**Languages**:
- [English](https://www.scala-sbt.org/2.x/docs/en/)
- [中文 (简体)](https://www.scala-sbt.org/2.x/docs/zh-cn/)
- [日本語](https://www.scala-sbt.org/2.x/docs/ja/)

![sbt logo](files/sbt-logo.svg)

sbt is a simple build tool for Scala and Java.
sbt downloads your library dependencies via Coursier,
incrementally compiles and tests your projects,
integrates with IDEs like IntelliJ and VS Code,
makes JAR packages, and publishes them to [the Central Repo](https://central.sonatype.com/),
JVM community's package registry.

```scala
scalaVersion := "{{scala3_example_version}}"
```

You just need one line of `build.sbt` to get started with Scala.

Links
-----
- The source for this documentation is hosted at [sbt/website](https://github.com/sbt/website/)
- The [documentation for sbt 1.x](https://www.scala-sbt.org/1.x/docs/) is available

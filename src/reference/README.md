The Book of sbt (Draft)
=======================

```admonish warning
This is a draft documentation of sbt 2.x that is yet to be released.
While the general concept translates to sbt 1.x,
details of both 2.x and this doc are subject to change.
```

![sbt logo](files/sbt-logo.svg)

sbt is a simple build tool for Scala and Java.
sbt downloads your library dependencies via Coursier,
incrementally compiles and tests your projects,
integrates with IDEs like IntelliJ and VS Code,
makes JAR packages, and publishes them to [Maven Central](https://central.sonatype.com/),
JVM community's package registry.

```scala
scalaVersion := "{{scala3_example_version}}"
```

You just need one line of `build.sbt` to get started with Scala.

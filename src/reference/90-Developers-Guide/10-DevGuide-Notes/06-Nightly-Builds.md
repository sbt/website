---
out: Nightly-Builds.html
---

  [Manual-Installation]: Manual-Installation.html
  [Setup]: Setup.html

Nightly Builds
--------------

The latest development versions of $app_version$ are available as nightly
builds on sbt-maven-snapshots (<https://repo.scala-sbt.org/scalasbt/maven-snapshots>) repo.

Note that currently following the URL would lead you to Bintray,
but [/org/scala-sbt/sbt/](https://repo.scala-sbt.org/scalasbt/maven-snapshots/org/scala-sbt/sbt/) would actually point to a Jenkins server.

To use a nightly build:

1. Find out a version from [/org/scala-sbt/sbt/](https://repo.scala-sbt.org/scalasbt/maven-snapshots/org/scala-sbt/sbt/).
2. Put the version, for example `sbt.version=1.3.0-bin-20190813T192012` in `project/build.properties`.

sbt launcher will resolve the sbt core artifacts based on the specification.

Unless you're debugging the `sbt` script or the launcher JAR, you should be able to use any recent stable version of sbt installation as the launcher following the [Setup][Setup] instructions first.

If you're overriding the repositories via `~/.sbt/repositories`, make sure that there's a following entry:

```
[repositories]
  ...
  sbt-maven-snapshots: https://repo.scala-sbt.org/scalasbt/maven-snapshots/, bootOnly
```

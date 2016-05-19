---
out: Community-Plugins.html
---

  [Bintray-For-Plugins]: Bintray-For-Plugins.html

Community Plugins
-----------------

### sbt Organization

The [sbt organization](https://github.com/sbt) is available for use by
any sbt plugin. Developers who contribute their plugins into the
community organization will still retain control over their repository
and its access. The goal of the sbt organization is to organize sbt
software into one central location.

A side benefit to using the sbt organization for projects is that you
can use gh-pages to host websites under the http://scala-sbt.org domain.

### Community Ivy Repository

[Lightbend](https://www.lightbend.com) has provided a freely available
[Ivy Repository](https://repo.scala-sbt.org/scalasbt) for sbt projects
to use. This Ivy repository is mirrored from the freely available
[Bintray service](https://bintray.com).
If you'd like to submit your plugin, please follow these instructions:
[Bintray For Plugins][Bintray-For-Plugins].

### Plugins available for sbt 1.0.0-M4

Please feel free to
[submit a pull request](https://github.com/sbt/website/pulls) that adds
your plugin to the list.

- bintray-sbt: <https://github.com/softprops/bintray-sbt>

Put this into `project/bintray.sbt`:

```scala
lazy val bintraySbt = RootProject(uri("git://github.com/eed3si9n/bintray-sbt#topic/sbt1.0.0-M4"))
lazy val root = (project in file(".")).
  dependsOn(bintraySbt)
```

- [Scripted plugin](Testing-sbt-plugins.html)
- sbt-assembly 0.14.3: <https://github.com/sbt/sbt-assembly>
- sbt-doge 0.1.5: <https://github.com/sbt/sbt-doge>
- sbt-buildinfo 0.6.1: <https://github.com/sbt/sbt-buildinfo>

---
out: Community-Plugins.html
---

  [Bintray-For-Plugins]: Bintray-For-Plugins.html
  [Cross-Build-Plugins]: Cross-Build-Plugins.html

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

### Cross building plugins from sbt 0.13

See [Cross Build Plugins][Cross-Build-Plugins].

### Plugins available for sbt 1.0.0-M5

Please feel free to
[submit a pull request](https://github.com/sbt/website/pulls) that adds
your plugin to the list.

- sbt-bintray 0.4.0: <https://github.com/sbt/sbt-bintray>
- [Scripted plugin](Testing-sbt-plugins.html)
- sbt-assembly 0.14.4: <https://github.com/sbt/sbt-assembly>
- sbt-buildinfo 0.7.0: <https://github.com/sbt/sbt-buildinfo>
- sbt-contraband 0.3.0-M4: <http://www.scala-sbt.org/contraband/>
- Scalafmt 0.7.0-RC1 <http://scalameta.org/scalafmt/>
- neo-sbt-scalafmt 0.3 <https://github.com/lucidsoftware/neo-sbt-scalafmt>
- Coursier 1.0.0-RC2 <https://github.com/coursier/coursier>
- sbt-pgp 1.1.0-M1: http://www.scala-sbt.org/sbt-pgp/

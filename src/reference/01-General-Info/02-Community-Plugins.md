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

### Plugins available for sbt 1.0 (including RC-x)

[[Edit]](https://github.com/sbt/website/edit/1.x/src/reference/01-General-Info/02-Community-Plugins.md) this page to
submit a pull request that adds
your plugin to the list.

#### Code formatter plugins

- neo-sbt-scalafmt (Scalafmt):
    <https://github.com/lucidsoftware/neo-sbt-scalafmt>

#### Documentation plugins

- tut (Scala literate programming):
    <https://github.com/tpolecat/tut> <!-- 398 stars -->
- sbt-unidoc (Create a unified API document across subprojects):
    <https://github.com/sbt/sbt-unidoc> <!-- 83 stars -->
- sbt-class-diagram (Create a class diagram):
    <https://github.com/xuwei-k/sbt-class-diagram> <!-- 50 stars -->

#### One jar plugins

- sbt-assembly (make fat JARs):
    <https://github.com/sbt/sbt-assembly> <!-- 1092 stars -->

#### Release plugins

- [sbt-native-packager](http://sbt-native-packager.readthedocs.io/en/stable/):
    <https://github.com/sbt/sbt-native-packager> <!-- 869 stars -->
- sbt-docker (Build and push Docker images):
    <https://github.com/marcuslonnberg/sbt-docker> <!-- 468 stars -->
- sbt-release (customizable release process):
    <https://github.com/sbt/sbt-release> <!-- 393 stars -->
- sbt-pack (generates packages with dependent jars and launch scripts):
    <https://github.com/xerial/sbt-pack> <!-- 292 stars -->
- sbt-bintray (Publish to Bintray):
    <https://github.com/sbt/sbt-bintray> <!-- 134 stars -->
- sbt-pgp (PGP signing plugin, can generate keys too):
    <https://github.com/sbt/sbt-pgp> <!-- 72 stars -->

#### Deployment integration plugins

...

#### Utility and system plugins

- sbt-git (executes git commands):
    <https://github.com/sbt/sbt-git> <!-- 223 stars -->
- sbt-conscript (app distribution using GitHub and Maven Central):
    <http://www.foundweekends.org/conscript/>
- MiMa (Migration Manager - the Scala binary compatibility validation tool):
    <https://github.com/typesafehub/migration-manager>
- sbt-groll (sbt plugin to navigate the Git history):
    <https://github.com/sbt/sbt-groll>
- sbt-jshell (execute Java REPL)
    <https://github.com/xuwei-k/sbt-jshell>
- sbt-tmpfs (automatically utilize tmpfs to speed up development and save disks): https://github.com/cuzfrog/sbt-tmpfs

#### Test plugins

-   [Scripted plugin](Testing-sbt-plugins.html)
-   sbt-jmh (OpenJDK JMH (Java Microbenchmark Harness) integration for Scala):
    <https://github.com/ktoso/sbt-jmh> <!-- 382 stars -->
-   sbt-scalaprops (plugin for scalaprops):
    <https://github.com/scalaprops/sbt-scalaprops>
-   tut (doc/tutorial generator for scala):
    <https://github.com/tpolecat/tut>

#### Library dependency plugins

- sbt-dependency-graph (Creates a graphml file of the dependency tree):
    <https://github.com/jrudolph/sbt-dependency-graph>
- sbt-updates (Display updated versions of your project dependencies):
    <https://github.com/rtimush/sbt-updates>:
- sbt-dependency-check (Check your project dependencies for publicly know vulnerabilities/CVE):
    <https://github.com/albuch/sbt-dependency-check>

#### Web and frontend development plugins

- [Play Framework](https://www.playframework.com/) <!-- 9597 stars -->
- [Scala.js](https://www.scala-js.org/) <!-- 3053 stars -->

#### Database plugins

...

#### Framework-specific plugins

- sbt-newrelic:
    <https://github.com/gilt/sbt-newrelic> <!-- 69 stars -->
- sbt-spark (Configure Spark applications):
    <https://github.com/alonsodomin/sbt-spark>

#### Code generator plugins

- sbt-buildinfo (Generate Scala source for any settings):
    <https://github.com/sbt/sbt-buildinfo> <!-- 307 stars -->
- sbt-protobuf (Google Protocol Buffers):
    <https://github.com/sbt/sbt-protobuf> <!-- 130 stars -->
- sbt-header (creating file headers, e.g. copyright headers):
    <https://github.com/sbt/sbt-header> <!-- 102 stars -->
- sbt-avro (Apache Avro):
    <https://github.com/cavorite/sbt-avro> <!-- 63 stars -->
- sbt-protoc (SBT plugin for generating code from Protocol Buffer using protoc):
    <https://github.com/thesamet/sbt-protoc> <!-- 29 stars -->
- sbt-sql (Generate model classes from SQL files):
    <https://github.com/xerial/sbt-sql> <!-- 15 stars -->
- sbt-contraband (Generate pseudo-case class from GraphQL schema):
    <http://www.scala-sbt.org/contraband/>

#### Static code analysis plugins

...


#### Code coverage plugins

...

#### Create new project plugins

- sbt-fresh (create an opinionated fresh sbt project):
    <https://github.com/sbt/sbt-fresh> <!-- 173 stars -->

#### In-house plugins

- sbt-houserules (Settings used by sbt modules):
  <https://github.com/sbt/sbt-houserules>

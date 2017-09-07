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

- sbt-site (Site generation for sbt):
    <https://github.com/sbt/sbt-site>
- tut (Scala literate programming):
    <https://github.com/tpolecat/tut> <!-- 398 stars -->
- sbt-unidoc (Create a unified API document across subprojects):
    <https://github.com/sbt/sbt-unidoc> <!-- 83 stars -->
- sbt-class-diagram (Create a class diagram):
    <https://github.com/xuwei-k/sbt-class-diagram> <!-- 50 stars -->
- sbt-api-mappings (Fill apiMappings for common Scala libraries):
    <https://github.com/ThoughtWorksInc/sbt-api-mappings> <!-- 45 stars -->

#### One jar plugins

- sbt-assembly (make fat JARs):
    <https://github.com/sbt/sbt-assembly> <!-- 1092 stars -->

#### Release plugins

- sbt-ghpages (publishes generated site and api):
    <https://github.com/sbt/sbt-ghpages>
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
- sbt-deploy (generates fat jars with launch scripts and configurations, ideal for distribution):
    <https://github.com/amanjpro/sbt-deploy-plugin>

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
- sbt-sh (execute shell commands)
    <https://github.com/melezov/sbt-sh>
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

- fm-sbt-s3-resolver (SBT Plugin that adds support for resolving and publishing using Amazon S3):
    <https://github.com/frugalmechanic/fm-sbt-s3-resolver>
- sbt-dependency-graph (Creates a graphml file of the dependency tree):
    <https://github.com/jrudolph/sbt-dependency-graph>
- sbt-updates (Display updated versions of your project dependencies):
    <https://github.com/rtimush/sbt-updates>:
- sbt-dependency-check (Check your project dependencies for publicly know vulnerabilities/CVE):
    <https://github.com/albuch/sbt-dependency-check>

#### Web and frontend development plugins

- Play Framework (Reactive web framework for Java and Scala):
    <https://www.playframework.com/> <!-- 9671 stars -->
- Scala.js (the Scala to JavaScript compiler):
    <https://www.scala-js.org/> <!-- 3085 stars -->
- sbt-web (Library for building sbt plugins for the web):
    <https://github.com/sbt/sbt-web> <!-- 314 stars -->
- sbt-less (sbt-web plugin for LESS compilation):
    <https://github.com/sbt/sbt-less> <!-- 34 stars -->
- sbt-js-engine (Library for plugging node base asset compilers into sbt-web):
    <https://github.com/sbt/sbt-js-engine> <!-- 33 stars -->
- sbt-typescript (sbt-web plugin for Typescript compilation):
    <https://github.com/joost-de-vries/sbt-typescript> <!-- 23 stars -->
- sbt-uglify (sbt-web plugin for uglify):
    <https://github.com/sbt/sbt-uglify> <!-- 21 stars -->
- sbt-digest (sbt-web plugin for digesting assets):
    <https://github.com/sbt/sbt-digest> <!-- 17 stars -->
- sbt-gzip (sbt-web plugin for gzipping assets):
    <https://github.com/sbt/sbt-gzip> <!-- 15 stars -->
- sbt-stylus (sbt-web plugin for Stylus compilation):
    <https://github.com/sbt/sbt-stylus> <!-- 2 stars -->

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

- sbt-taglist (Find and log TODO, FIXMEs in source files):
    <https://github.com/johanandren/sbt-taglist>

#### Code coverage plugins

...

#### Create new project plugins

- sbt-fresh (create an opinionated fresh sbt project):
    <https://github.com/sbt/sbt-fresh> <!-- 173 stars -->

#### In-house plugins

- sbt-houserules (Settings used by sbt modules):
  <https://github.com/sbt/sbt-houserules>

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

#### Plugins for IDEs

...

#### One jar plugins

- sbt-assembly (make fat JARs):
    <https://github.com/sbt/sbt-assembly>

#### Release plugins

- sbt-pgp (PGP signing plugin, can generate keys too):
    <https://github.com/sbt/sbt-pgp>
- sbt-bintray (Publish to Bintray):
    <https://github.com/sbt/sbt-bintray>
- sbt-release (customizable release process):
    <https://github.com/sbt/sbt-release>
- sbt-docker (Build and push Docker images):
    <https://github.com/marcuslonnberg/sbt-docker>

#### Deployment integration plugins

...

#### Utility and system plugins

- sbt-git (executes git commands):
    <https://github.com/sbt/sbt-git>
- sbt-conscript (app distribution using GitHub and Maven Central):
    <http://www.foundweekends.org/conscript/>
- MiMa (Migration Manager - the Scala binary compatibility validation tool):
    <https://github.com/typesafehub/migration-manager>
- sbt-jshell (execute Java REPL)
    <https://github.com/xuwei-k/sbt-jshell>

#### Test plugins

-   [Scripted plugin](Testing-sbt-plugins.html)
-   sbt-jmh (OpenJDK JMH (Java Microbenchmark Harness) integration for Scala):
    <https://github.com/ktoso/sbt-jmh>
-   sbt-scalaprops (plugin for scalaprops):
    <https://github.com/scalaprops/sbt-scalaprops>
-   tut (doc/tutorial generator for scala):
    <https://github.com/tpolecat/tut>

#### Library dependency plugins

- sbt-updates (Display updated versions of your project dependencies):
    <https://github.com/rtimush/sbt-updates>
- sbt-dependency-check (Check your project dependencies for publicly know vulnerabilities/CVE):
    <https://github.com/albuch/sbt-dependency-check>

#### Web and frontend development plugins

...


#### Database plugins

...


#### Code generator plugins

- sbt-buildinfo (Generate Scala source for any settings):
    <https://github.com/sbt/sbt-buildinfo>
- sbt-protobuf (Google Protocol Buffers):
    <https://github.com/sbt/sbt-protobuf>
- sbt-protoc (SBT plugin for generating code from Protocol Buffer using protoc)
    <https://github.com/thesamet/sbt-protoc>
- sbt-contraband (Generate pseudo-case class from GraphQL schema):
    <http://www.scala-sbt.org/contraband/>

#### Documentation plugins

- sbt-unidoc (Create a unified API document across subprojects):
    <https://github.com/sbt/sbt-unidoc>
- sbt-class-diagram (Create a class diagram):
    <https://github.com/xuwei-k/sbt-class-diagram>

#### Static code analysis plugins

...


#### Code coverage plugins

...

#### In-house plugins

- sbt-houserules (Settings used by sbt modules):
  <https://github.com/sbt/sbt-houserules>

### Plugins available for sbt 1.0.0-M6

Please submit a [pull request](https://github.com/sbt/website/pulls) that adds
your plugin to the list.

- sbt-sonatype 2.0: <https://github.com/xerial/sbt-sonatype>
- sbt-class-diagram 0.2.1 <https://github.com/xuwei-k/sbt-class-diagram>
- sbt-scalaprops 0.2.1 <https://github.com/scalaprops/sbt-scalaprops>
- sbt-bintray 0.5.0: <https://github.com/sbt/sbt-bintray>
- [Scripted plugin](Testing-sbt-plugins.html)
- sbt-assembly 0.14.5: <https://github.com/sbt/sbt-assembly>
- sbt-buildinfo 0.7.0: <https://github.com/sbt/sbt-buildinfo>
- sbt-contraband 0.3.0-M5: <http://www.scala-sbt.org/contraband/>
- sbt-docker 1.4.1: <https://github.com/marcuslonnberg/sbt-docker>
- sbt-pgp 1.1.0-M1: <http://www.scala-sbt.org/sbt-pgp/>
- sbt-git 0.9.3: <https://github.com/sbt/sbt-git>
- sbt-protobuf 0.6.1 <https://github.com/sbt/sbt-protobuf>
- sbt-conscript 0.5.2 <https://github.com/foundweekends/conscript>
- sbt-jmh 0.2.26 <https://github.com/ktoso/sbt-jmh>
- sbt-updates 0.3.1: <https://github.com/rtimush/sbt-updates>

### Plugins available for sbt 1.0.0-M5

- sbt-bintray 0.4.0: <https://github.com/sbt/sbt-bintray>
- [Scripted plugin](Testing-sbt-plugins.html)
- sbt-assembly 0.14.4: <https://github.com/sbt/sbt-assembly>
- sbt-buildinfo 0.7.0: <https://github.com/sbt/sbt-buildinfo>
- sbt-contraband 0.3.0-M4: <http://www.scala-sbt.org/contraband/>
- Scalafmt 0.7.0-RC1 <http://scalameta.org/scalafmt/>
- neo-sbt-scalafmt 0.3 <https://github.com/lucidsoftware/neo-sbt-scalafmt>
- Coursier 1.0.0-RC2 <https://github.com/coursier/coursier>
- sbt-pgp 1.1.0-M1: <http://www.scala-sbt.org/sbt-pgp/>
- sbt-sonatype 2.0.0-M1: <https://github.com/xerial/sbt-sonatype>
- sbt-updates 0.3.1: <https://github.com/rtimush/sbt-updates>

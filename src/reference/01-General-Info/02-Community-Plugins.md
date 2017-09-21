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

- [neo-sbt-scalafmt](https://github.com/lucidsoftware/neo-sbt-scalafmt)

#### Documentation plugins

- [sbt-site](https://github.com/sbt/sbt-site): site generator.
- [tut](https://github.com/tpolecat/tut): documentation and tutorial generator.
- [sbt-unidoc](https://github.com/sbt/sbt-unidoc): create unified API
  documentation across subprojects.
- [sbt-class-diagram](https://github.com/xuwei-k/sbt-class-diagram): generate
  class diagrams from Scala source code.
- [sbt-api-mappings](https://github.com/ThoughtWorksInc/sbt-api-mappings):
  generate Scaladoc `apiMappings` for common Scala libraries.

#### One jar plugins

- [sbt-assembly](https://github.com/sbt/sbt-assembly): create fat JARs.

#### Release plugins

- [sbt-ghpages](https://github.com/sbt/sbt-ghpages): publish generated
  sites to GitHub pages.
- [sbt-native-packager](https://github.com/sbt/sbt-native-packager)
  ([docs](http://sbt-native-packager.readthedocs.io/en/stable/)): build
  native packages (RPM, .deb etc) for your projects.
- [sbt-docker](https://github.com/marcuslonnberg/sbt-docker): create and
  push Docker images.
- [sbt-release](https://github.com/sbt/sbt-release): create a customizable
  release process.
- [sbt-pack](https://github.com/xerial/sbt-pack): create runnable distributions
  for your projects.
- [sbt-bintray](https://github.com/sbt/sbt-bintray): publish artefacts to
  Bintray.
- [sbt-pgp](https://github.com/sbt/sbt-pgp): sign artefacts using PGP/GPG and
  manage signing keys.
- [sbt-deploy](https://github.com/amanjpro/sbt-deploy-plugin): create
  deployable fat JARs.

#### Deployment integration plugins

...

#### Utility and system plugins

- [sbt-git](https://github.com/sbt/sbt-git): run git commands from sbt.
- [sbt-conscript](http://www.foundweekends.org/conscript/): distribute apps
  using GitHub and Maven Central.
- [MiMa](https://github.com/typesafehub/migration-manager): binary
  compatibility management for Scala libraries.
- [sbt-groll](https://github.com/sbt/sbt-groll): navigate git history inside
  sbt.
- [sbt-jshell](https://github.com/xuwei-k/sbt-jshell): Java REPL for sbt.
- [sbt-sh](https://github.com/melezov/sbt-sh): run shell commands from sbt.
- [sbt-tmpfs](https://github.com/cuzfrog/sbt-tmpfs): utilize tmpfs to speed
  up builds.

#### Test plugins

- [scripted](Testing-sbt-plugins.html): integration testing for sbt plugins.
- [sbt-jmh](https://github.com/ktoso/sbt-jmh): run Java Microbenchmark Harness
  (JMH) benchmarks from sbt.
- [sbt-scalaprops](https://github.com/scalaprops/sbt-scalaprops): scalaprops
  property-based testing integration.

#### Library dependency plugins

- [sbt-s3-resolver](https://github.com/ohnosequences/sbt-s3-resolver): resolve
  dependencies using Amazon S3.
- [fm-sbt-s3-resolver](https://github.com/frugalmechanic/fm-sbt-s3-resolver):
  resolve and publish artefacts using Amazon S3.
- [sbt-dependency-graph](https://github.com/jrudolph/sbt-dependency-graph):
  create dependency graphs using GraphML, graphviz or ASCII.
- [sbt-updates](https://github.com/rtimush/sbt-updates): list updated versions
  of dependencies.
- [sbt-dependency-check](https://github.com/albuch/sbt-dependency-check):
  check dependencies for known vulnerabilities/CVEs.

#### Web and frontend development plugins

- [Play Framework](https://www.playframework.com): reactive web framework for
  Scala and Java.
- [Scala.js](https://www.scala-js.org): Scala to JavaScript compiler.
- [sbt-web](https://github.com/sbt/sbt-web): library for building sbt plugins
  for the web.
- [sbt-less](https://github.com/sbt/sbt-less): Less CSS compilation support.
- [sbt-js-engine](https://github.com/sbt/sbt-js-engine): support for sbt
  plugins that use JavaScript.
- [sbt-typescript](https://github.com/joost-de-vries/sbt-typescript):
  TypeScript compilation support.
- [sbt-uglify](https://github.com/sbt/sbt-uglify): JavaScript minifier using
  UglifyJS.
- [sbt-digest](https://github.com/sbt/sbt-digest): generate checksums of
  assets.
- [sbt-gzip](https://github.com/sbt/sbt-gzip): gzip compressor for assets.
- [sbt-stylus](https://github.com/sbt/sbt-stylus): Stylus stylesheet compiler.

#### Database plugins

...

#### Framework-specific plugins

- [sbt-newrelic](https://github.com/gilt/sbt-newrelic): NewRelic support for
  artefacts built with sbt-native-packager.
- [sbt-spark](https://github.com/alonsodomin/sbt-spark): Spark application
  configurator.

#### Code generator plugins

- [sbt-buildinfo](https://github.com/sbt/sbt-buildinfo): generate Scala code
  from SBT setting keys.
- [sbt-protobuf](https://github.com/sbt/sbt-protobuf): protobuf code generator.
- [sbt-header](https://github.com/sbt/sbt-header): auto-generate source code
  file headers (such as copyright notices).
- [sbt-avro](https://github.com/cavorite/sbt-avro): Apache Avro schema
  and protocol generator.
- [sbt-protoc](https://github.com/thesamet/sbt-protoc): protobuf code generator
  using protoc.
- [sbt-sql](https://github.com/xerial/sbt-sql): generate model classes from
  SQL.
- [sbt-contraband](http://www.scala-sbt.org/contraband): generate pseudo-case
  classes from GraphQL schemas.

#### Static code analysis plugins

- [sbt-taglist](https://github.com/johanandren/sbt-taglist): find tags within
  source files (such as TODO and FIXME).

#### Code coverage plugins

...

#### Create new project plugins

- [sbt-fresh](https://github.com/sbt/sbt-fresh): create an opinionated fresh
  sbt project.

#### In-house plugins

- [sbt-houserules](https://github.com/sbt/sbt-houserules): houserules settings
  for sbt modules.

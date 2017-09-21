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

- [neo-sbt-scalafmt](https://github.com/lucidsoftware/neo-sbt-scalafmt): code
  formatting using Scalafmt.
- [sbt-scalariform](https://github.com/sbt/sbt-scalariform): code
  formatting using Scalariform.
- [sbt-java-formatter](https://github.com/typesafehub/sbt-java-formatter):
  code formatting for Java sources.

#### Documentation plugins

- [sbt-site](https://github.com/sbt/sbt-site): site generator.
- [tut](https://github.com/tpolecat/tut): documentation and tutorial generator.
- [sbt-unidoc](https://github.com/sbt/sbt-unidoc): create unified API
  documentation across subprojects.
- [sbt-class-diagram](https://github.com/xuwei-k/sbt-class-diagram): generate
  class diagrams from Scala source code.
- [sbt-api-mappings](https://github.com/ThoughtWorksInc/sbt-api-mappings):
  generate Scaladoc `apiMappings` for common Scala libraries.
- [sbt-microsites](https://github.com/47deg/sbt-microsites): generate
  and publish microsites using Jekyll.

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
- [sbt-sonatype](https://github.com/xerial/sbt-sonatype): publish artefacts to
  Maven Central.
- [sbt-aether-deploy](https://github.com/arktekk/sbt-aether-deploy): publish
  artefacts using Eclipse Aether.
- [sbt-rig](https://github.com/Verizon/sbt-rig): opinionated common release
  steps.
- [sbt-s3](https://github.com/sbt/sbt-s3): manage objects on Amazon S3.
- [sbt-osgi](https://github.com/sbt/sbt-osgi): create OSGi bundles.

#### Deployment integration plugins

- [sbt-heroku](https://github.com/heroku/sbt-heroku): deploy applications
  directly to Heroku.
- [sbt-docker-compose](https://github.com/Tapad/sbt-docker-compose):
  launch Docker images using docker compose.

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
- [sbt-revolver](https://github.com/spray/sbt-revolver): auto-restart forked
  JVMs on update.
- [sbt-errors-summary](https://github.com/Duhemm/sbt-errors-summary): show a
  summary of compilation errors.
- [sbt-dynver](https://github.com/dwijnand/sbt-dynver): set project version
  dynamically from git metadata.
- [sbt-prompt](https://github.com/agemooij/sbt-prompt): add promptlets and
  themes to your sbt prompt.
- [sbt-crossproject](https://github.com/scala-native/sbt-crossproject):
  cross-build Scala, Scala.js and Scala Native.
- [sbt-proguard](https://github.com/sbt/sbt-proguard): run ProGuard on
  compiled sources.
- [sbt-structure](https://github.com/JetBrains/sbt-structure): extract project
  structure in XML format.
- [sbt-jni](https://github.com/jodersky/sbt-jni): helpers for working with
  projects that use JNI.
- [sbt-jol](https://github.com/ktoso/sbt-jol): inspect OpenJDK Java Object
  Layout from sbt.
- [sbt-musical](https://github.com/tototoshi/sbt-musical): control iTunes
  from sbt (Mac only).
- [sbt-travisci](https://github.com/dwijnand/sbt-travisci): integration
  with Travis CI.
- [horder](https://github.com/romanowski/hoarder): cache compilation
  artefacts for future builds.
- [sbt-javaagent](https://github.com/sbt/sbt-javaagent): add Java agents to
  projects.

#### IDE integration plugins

- [sbteclipse](https://github.com/typesafehub/sbteclipse): Eclipse project
  definition generator.
- [sbt-sublime](https://github.com/orrsella/sbt-sublime): Sublime Text project
  generator.

#### Test plugins

- [scripted](Testing-sbt-plugins.html): integration testing for sbt plugins.
- [sbt-jmh](https://github.com/ktoso/sbt-jmh): run Java Microbenchmark Harness
  (JMH) benchmarks from sbt.
- [sbt-scalaprops](https://github.com/scalaprops/sbt-scalaprops): scalaprops
  property-based testing integration.
- [sbt-doctest](https://github.com/tkawachi/sbt-doctest): generate and run
  tests from Scaladoc comments.
- [gatling-sbt](https://github.com/gatling/gatling-sbt): performance and
  load-testing using Gatling.
- [sbt-multi-jvm](https://github.com/sbt/sbt-multi-jvm): run tests using
  multiple JVMs.
- [sbt-testng](https://github.com/sbt/sbt-testng): TestNG framework
  integration.
- [sbt-jcstress](https://github.com/ktoso/sbt-jcstress): Java Concurrency
  Stress Test (jcstress) integration.

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
- [coursier](https://github.com/coursier/coursier): pure Scala dependency
  fetcher.
- [sbt-s3-resolver](https://github.com/ohnosequences/sbt-s3-resolver): resolve
  dependencies using Amazon S3.
- [sbt-lock](https://github.com/tkawachi/sbt-lock): create a lock file
  containing explicit sbt dependencies.
- [sbt-license-report](https://github.com/sbt/sbt-license-report): generate
  reports of licenses used by dependencies.

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
- [xsbt-web-plugin](https://github.com/earldouglas/xsbt-web-plugin): build and
  deploy JEE web applications.
- [sbt-web-scalajs](https://github.com/vmunier/sbt-web-scalajs): use Scala.js
  with any web server.
- [sbt-scalatra](https://github.com/scalatra/sbt-scalatra): build and run
  Scalatra apps.

#### Database plugins

- [sbt-dynamodb](https://github.com/localytics/sbt-dynamodb): run a local
  Amazon DynamoDB test instance from sbt.

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
- [sbt-scalaxb](https://github.com/eed3si9n/scalaxb): generate model classes
  from XML schemas and WSDL.
- [sbt-boilerplate](https://github.com/sbt/sbt-boilerplate): TupleX and FunctionX
  boilerplate code generator.
- [sbt-aspectj](https://github.com/sbt/sbt-aspectj): AspectJ weaving for sbt.
- [sbt-antlr4](https://github.com/ihji/sbt-antlr4): run ANTLR v4 from sbt.
- [sbt-partial-unification](https://github.com/fiadliel/sbt-partial-unification):
  enable partial unification support in Scala (SI-2712).

#### Static code analysis plugins

- [sbt-taglist](https://github.com/johanandren/sbt-taglist): find tags within
  source files (such as TODO and FIXME).
- [wartremover](https://github.com/wartremover/wartremover): flexible Scala
  linting tool.
- [scalastyle-sbt-plugin](https://github.com/scalastyle/scalastyle-sbt-plugin):
  code style checking using Scalastyle.
- [sbt-scapegoat](https://github.com/sksamuel/sbt-scapegoat): static analysis
  using Scapegoat.
- [sbt-stats](https://github.com/orrsella/sbt-stats): generate source code
  statistics (lines of code etc).
- [sbt-jcheckstyle](https://github.com/xerial/sbt-jcheckstyle): Java code
  style checking using Checkstyle.

#### Code coverage plugins

- [sbt-scoverage](https://github.com/scoverage/sbt-scoverage): Scala code
  coverage using Scoverage.
- [sbt-jacoco](https://github.com/sbt/sbt-jacoco): Scala and Java code coverage
  using JaCoCo.

#### Create new project plugins

- [sbt-fresh](https://github.com/sbt/sbt-fresh): create an opinionated fresh
  sbt project.

#### In-house plugins

- [sbt-houserules](https://github.com/sbt/sbt-houserules): houserules settings
  for sbt modules.

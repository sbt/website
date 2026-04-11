
  [Bintray-For-Plugins]: Bintray-For-Plugins.html
  [Cross-Build-Plugins]: Cross-Build-Plugins.html

Community Plugins
=================

### The GitHub sbt Organization

The [sbt organization](https://github.com/sbt) is available for use by
any sbt plugin. Developers who contribute their plugins into the
community organization will still retain control over their repository
and its access. The goal of the sbt organization is to organize sbt
software into one central location.

A side benefit to using the sbt organization for projects is that you
can use gh-pages to host websites under the https://www.scala-sbt.org domain.

The [sbt autoplugin giter8 template](https://github.com/sbt/sbt-autoplugin.g8)
is a good place to start. This sets up a new sbt plugin project appropriately.
The generated `README` includes a summary of the steps for publishing a new
community plugin.

~~~admonish note title="Plugins available for sbt 2.x"
[[Edit]](https://github.com/sbt/website/edit/develop/src/reference/community-plugins.md) this page to
submit a pull request that adds
your plugin to the list.
~~~

### Code formatter plugins

- [sbt-scalafmt](https://scalameta.org/scalafmt/docs/installation.html#sbt): code formatting using
  Scalafmt. <!-- 829 stars -->
- [sbt-java-formatter](https://github.com/sbt/sbt-java-formatter):
  code formatting for Java sources. <!-- 8 stars -->

<!--
- [safety-plugin](https://github.com/leobenkel/safety_plugin): Enforce the use of style rules across your company
-->

### One jar plugins

- [sbt-assembly](https://github.com/sbt/sbt-assembly): create über JARs.
  <!-- 1136 stars -->

### Release plugins

- [sbt-native-packager](https://github.com/sbt/sbt-native-packager)
  ([docs](https://sbt-native-packager.readthedocs.io/en/stable/)): build
  native packages (RPM, .deb etc) for your projects. <!-- 1602 stars -->
- [sbt-release](https://github.com/sbt/sbt-release): create a customizable
  release process. <!-- 652 stars -->
- [sbt-ci-release](https://github.com/sbt/sbt-ci-release): automate Central Repo releases from GitHub Actions. <!-- 302 stars -->
- [sbt-native-image](https://github.com/scalameta/sbt-native-image): generate GraalVM native-image binaries. <!-- 257 stars -->
- [sbt-pgp](https://github.com/sbt/sbt-pgp): sign artifacts using PGP/GPG and
  manage signing keys. <!-- 149 stars -->

<div style="display: none;">

- [sbt-pack](https://github.com/xerial/sbt-pack): create runnable distributions
  for your projects. <!-- 302 stars -->
- [sbt-docker](https://github.com/marcuslonnberg/sbt-docker): create and
  push Docker images. <!-- 72 stars -->
- [sbt-aether-deploy](https://github.com/arktekk/sbt-aether-deploy): publish
  artefacts using Eclipse Aether. <!-- 59 stars -->
- [sbt-s3](https://github.com/sbt/sbt-s3): manage objects on Amazon S3.
  <!-- 36 stars -->
- [sbt-osgi](https://github.com/sbt/sbt-osgi): create OSGi bundles.
  <!-- 32 stars -->
- [sbt-github-release](https://github.com/ohnosequences/sbt-github-release): 
  publish Github releases. <!-- 22 stars -->
- [sbt-hadoop](https://github.com/Tapad/sbt-hadoop-oss): publish artifacts
  to the [Hadoop](https://hadoop.apache.org) Distributed File System (HDFS).
  <!-- 6 stars -->
- [sbt-publish-more](https://github.com/laughedelic/sbt-publish-more):
  publish artifacts to several repositories <!-- 1 star -->
- [sbt-deploy](https://github.com/amanjpro/sbt-deploy-plugin): create
  deployable fat JARs. <!-- 1 star -->
- [sbt-release-fossil](https://chiselapp.com/user/twenstar/repository/sbt-release-fossil):
  enhances [sbt-release](https://github.com/sbt/sbt-release) to support [Fossil](https://fossil-scm.org) repositories
- [sbt-autoversion](https://github.com/sbt/sbt-autoversion): automatically set your
  next version bump based on patterns of your commit message since last release. <!-- 2 stars -->
- [sbt-gcs](https://github.com/saint1991/sbt-gcs): manage objects on Google Cloud Storage. <!-- 1 star -->
- [sbt-sourcebundler](https://github.com/kotobotov/sbt-sourcebundler): merge all source code into one scala file. <!-- 1 star -->
- [sbt-kubeyml](https://github.com/vaslabs/sbt-kubeyml): Create a typesafe kubernetes Deployment based on your project settings
- [sbt-k8s](https://github.com/hnaderi/sbt-k8s): Create any manifest or use provided cookbooks using [scala-k8s](https://github.com/hnaderi/scala-k8s) library
- [sbt-release-notes](https://github.com/AmadeusITGroup/sbt-release-notes): provide a Release Step for [sbt-release](https://github.com/sbt/sbt-release) to automatically update the release notes file.

</div>

### IDE integration plugins

- [sbt-structure](https://github.com/JetBrains/sbt-structure): extract project
  structure in XML for IntelliJ Scala plugin. <!-- 74 stars -->
- [Metals](https://scalameta.org/metals/docs/build-tools/sbt/): Scala language server.

### Test plugins

- [sbt-jmh](https://github.com/ktoso/sbt-jmh): run Java Microbenchmark Harness
  (JMH) benchmarks from sbt. <!-- 790 stars -->
- [sbt-stryker4s](https://github.com/stryker-mutator/stryker4s): Test your tests with mutation testing. <!-- 202 stars -->
- [junit-interface](https://github.com/sbt/junit-interface): test interface for JUnit 4. <!-- 135 stars -->
- [sbt-doctest](https://github.com/tkawachi/sbt-doctest): generate and run
  tests from Scaladoc comments. <!-- 120 stars -->
- [snapshot4s](https://siriusxm.github.io/snapshot4s/): snapshot testing <!-- 66 stars -->
- [sbt-jupiter-interface](https://github.com/sbt/sbt-jupiter-interface): test interface for JUnit 5. <!-- 35 stars -->
- [test-times-reporter](https://github.com/xuwei-k/test-times-reporter): report slow tests. <!-- 3 stars -->

<div style="display: none;">

- [scripted](Testing-sbt-plugins.html): integration testing for sbt plugins.
- [gatling-sbt](https://github.com/gatling/gatling-sbt): performance and
  load-testing using Gatling. <!-- 79 stars -->
- [sbt-multi-jvm](https://github.com/sbt/sbt-multi-jvm): run tests using
  multiple JVMs. <!-- 36 stars -->
- [sbt-scalaprops](https://github.com/scalaprops/sbt-scalaprops): scalaprops
  property-based testing integration. <!-- 10 stars -->
- [sbt-testng](https://github.com/sbt/sbt-testng): TestNG framework
  integration. <!-- 8 stars -->
- [sbt-jcstress](https://github.com/ktoso/sbt-jcstress): Java Concurrency
  Stress Test (jcstress) integration. <!-- 8 stars -->
- [sbt-cached-ci](https://github.com/OlegYch/sbt-cached-ci): Incremental sbt builds for CI environments. <!-- 0 stars -->

</div>

### Library dependency plugins

- [sbt-license-report](https://github.com/sbt/sbt-license-report): generate
  reports of licenses used by dependencies. <!-- 88 stars -->
- [sbt-dependency-submission](https://github.com/scalacenter/sbt-dependency-submission): Dependency Submission API integration. <!-- 16 stars -->
- [sbt-conflict-classes](https://github.com/xuwei-k/sbt-conflict-classes): show conflict classes in the classpath. <!-- 16 stars -->
- [sbt-akka-version-check](https://github.com/johanandren/sbt-akka-version-check): detect Akka module mismatches and fail build. <!-- 10 stars -->
- [sbt-license-check](https://github.com/philippus/sbt-license-check): check and report on licenses used, fail build for disallowed licenses. <!-- 9 stars -->
- [sbt-pekko-version-check](https://github.com/philippus/sbt-pekko-version-check): check if the Apache Pekko modules match. <!-- 5 stars -->
- [sbt-jackson-version-check](https://github.com/philippus/sbt-jackson-version-check): check if the Jackson modules match. <!-- 3 stars -->
- [sbt-dependency-rules](https://github.com/evolution-gaming/sbt-dependency-rules-plugin): enforce user-defined rules on project dependencies. <!-- 3 stars -->

<div style="display: none;">

- [sbt-updates](https://github.com/rtimush/sbt-updates): list updated versions
  of dependencies. <!-- 361 stars -->
- [fm-sbt-s3-resolver](https://github.com/frugalmechanic/fm-sbt-s3-resolver):
  resolve and publish artefacts using Amazon S3. <!-- 79 stars -->
- [sbt-s3-resolver](https://github.com/ohnosequences/sbt-s3-resolver): resolve
  dependencies using Amazon S3. <!-- 73 stars -->
- [sbt-dependency-check](https://github.com/nMoncho/sbt-dependency-check):
  check dependencies for known vulnerabilities/CVEs. <!-- 25 stars -->
- [sbt-duplicates-finder](https://github.com/sbt/sbt-duplicates-finder): detect
  class and resources conflicting in your project's classpath. <!-- 13 stars -->
- [sbt-google-cloud-storage](https://github.com/lightbend/sbt-google-cloud-storage): resolver and publisher for Google Cloud Storage.
- [sbt-trace](https://github.com/delprks/sbt-trace): find traces of the client or library usage in other projects. <!-- 3 stars -->
- [safety-plugin](https://github.com/leobenkel/safety_plugin): Enforce the use of specified versions of dependencies across your company
- [sbt-dependency-lock](https://stringbean.github.io/sbt-dependency-lock):
generate dependency lockfiles and check for changes at build time.
- [sbt-unzip](https://github.com/djice/sbt-unzip-plugin): Extract zip dependencies where you want in your project.

</div>

### Code generator plugins

- [sbt-buildinfo](https://github.com/sbt/sbt-buildinfo): generate Scala code
  from sbt setting keys. <!-- 559 stars -->
- [smithy4s](https://disneystreaming.github.io/smithy4s/docs/overview/installation#sbt): Smithy for Scala <!-- 396 stars -->
- [sbt-scalaxb](https://github.com/eed3si9n/scalaxb): generate model classes
  from XML schemas and WSDL. <!-- 335 stars -->
- [avrohugger](https://github.com/julianpeeters/avrohugger): generate case classes from Avro schemas. <!-- 203 stars -->
- [sbt-github-actions](https://github.com/sbt/sbt-github-actions): generate GitHub Actions YAML <!-- 197 stars -->
- [sbt-header](https://github.com/sbt/sbt-header): auto-generate source code
  file headers (such as copyright notices). <!-- 187 stars -->
- [sbt-protobuf](https://github.com/sbt/sbt-protobuf): protobuf code generator.
  <!-- 173 stars -->
- [sbt-boilerplate](https://github.com/sbt/sbt-boilerplate): TupleX and FunctionX
  boilerplate code generator. <!-- 109 stars -->
- [sbt-contraband](https://github.com/sbt/contraband)
  ([docs](https://www.scala-sbt.org/contraband)): generate pseudo-case classes
  from GraphQL schemas. <!-- 67 stars -->
- [sbt-openapi-generator](https://github.com/OpenAPITools/sbt-openapi-generator): OpenAPI generator.
- [sbt-teavm](https://github.com/sbt-teavm/sbt-teavm): generate JavaScript and WebAssembly from Java bytecode <!-- 11 stars -->

<div style="display: none;">

- [sbt-avro](https://github.com/cavorite/sbt-avro): Apache Avro schema
  and protocol generator. <!-- 66 stars -->
- [sbt-aspectj](https://github.com/sbt/sbt-aspectj): AspectJ weaving for sbt.
  <!-- 62 stars -->
- [sbt-protoc](https://github.com/thesamet/sbt-protoc): protobuf code generator
  using protoc. <!-- 35 stars -->
- [sbt-antlr4](https://github.com/ihji/sbt-antlr4): run ANTLR v4 from sbt.
  <!-- 22 stars -->
- [sbt-sql](https://github.com/xerial/sbt-sql): generate model classes from
  SQL. <!-- 15 stars -->
- [sbt-i18n](https://github.com/ant8e/sbt-i18n):
  transform your i18n bundles into Scala code. <!-- 1 stars -->
- [sbt-lit](https://github.com/earldouglas/sbt-lit): build literate code with sbt.
- [sbt-embedded-files](https://github.com/yurique/embedded-files): 
  generate Scala objects containing the contents of glob-specified files as strings or byte-arrays.
</div>

<div style="display: none;">
### Verification plugins

- [sbt-stainless](https://github.com/NiceKingWei/sbt-stainless): verify Scala or Dotty code using stainless. <!-- 1 star -->
</div>

### Language support plugins

- [sbt-redacted](https://github.com/polentino/sbt-redacted): redacted compiler plugin. <!-- 2 stars -->

<div style="display: none;">

- [sbt-scala-ts](https://github.com/scala-ts/scala-ts/):
  generate TypeScript code according compiled Scala types (case class, trait, object, ...). <!-- 149 stars -->
- [sbt-frege](https://github.com/earldouglas/sbt-frege): build Frege
  code with sbt. <!-- 47 stars -->
- [sbt-cc](https://github.com/tnakamot/sbt-cc): compile C and C++ source files with sbt.
</div>


### Web and frontend development plugins

- [sbt-war](https://github.com/earldouglas/sbt-war): package and run WAR files <!-- 381 stars -->
- [sbt-web](https://github.com/sbt/sbt-web): library for building sbt plugins
  for the web. <!-- 314 stars -->
- [sbt-js-engine](https://github.com/sbt/sbt-js-engine): support for sbt
  plugins that use JavaScript. <!-- 40 stars -->
- [sbt-less](https://github.com/sbt/sbt-less): Less CSS compilation support.
  <!-- 34 stars -->
- [sbt-coffeescript](https://github.com/sbt/sbt-coffeescript): CoffeeScript support.
  <!-- 14 stars -->

<div style="display: none;">

  - [Play Framework](https://www.playframework.com): reactive web framework for
  Scala and Java. <!-- 9727 stars -->
- [Scala.js](https://www.scala-js.org): Scala to JavaScript compiler.
  <!-- 3113 stars -->
- [sbt-war](https://github.com/earldouglas/sbt-war): Servlet
  support. <!-- 379 stars -->
- [sbt-web-scalajs](https://github.com/vmunier/sbt-web-scalajs): use Scala.js
  with any web server. <!-- 148 stars -->
- [sbt-typescript](https://github.com/joost-de-vries/sbt-typescript):
  TypeScript compilation support. <!-- 25 stars -->
- [sbt-uglify](https://github.com/sbt/sbt-uglify): JavaScript minifier using
  UglifyJS. <!-- 22 stars -->
- [sbt-terser](https://github.com/andriimartynov/sbt-terser): JavaScript (ES6+) minifier
  using terser. <!-- 0 stars -->
- [sbt-digest](https://github.com/sbt/sbt-digest): generate checksums of
  assets. <!-- 18 stars -->
- [sbt-scalatra](https://github.com/scalatra/sbt-scalatra): build and run
  Scalatra apps. <!-- 17 stars -->
- [sbt-scala-js-map](https://github.com/ThoughtWorksInc/sbt-scala-js-map): Configure source mapping for Scala.js projects hosted on Github. <!-- 16 stars -->
- [sbt-gzip](https://github.com/sbt/sbt-gzip): gzip compressor for assets.
  <!-- 15 stars -->
- [sbt-stylus](https://github.com/sbt/sbt-stylus): Stylus stylesheet compiler.
  <!-- 2 stars -->
- [sbt-hepek](https://github.com/sake92/sbt-hepek): Render static websites directly from Scala code.
  <!-- 5 stars -->
- [sbt-puresass](https://chiselapp.com/user/twenstar/repository/sbt-puresass): [sbt-web](https://github.com/sbt/sbt-web) plugin for Sass styles compilation.
- [sbt-scala-ts](https://github.com/swachter/scala-ts); generates TypeScript declaration files from ScalaJS sources and outputs Node modules.

</div>

### Database plugins

- [flyway-sbt](https://github.com/sbt/flyway-sbt) Flyway database migration. <!-- 131 stars -->
- [sbt-dao-generator](https://github.com/sbt-dao-generator/sbt-dao-generator) generate code for O/R Mapper Free <!-- 14 stars -->
- [sbt-sliquibase](https://codeberg.org/PerformantData/sbt-sliquibase): generate
  code for Slick API types from a Liquibase changelog.

<div style="display: none;">

- [scalikejdbc-mapper-generator](https://github.com/scalikejdbc/scalikejdbc):
  Scala code generator from database schema. <!-- 802 stars -->
- [sbt-dynamodb](https://github.com/localytics/sbt-dynamodb): run a local
  Amazon DynamoDB test instance from sbt. <!-- 41 stars -->
- [sbt-migrations](https://github.com/LeonhardtDavid/migrations): database
  migrations manager.

</div>

### Static code analysis plugins

- [wartremover](https://github.com/wartremover/wartremover): flexible Scala
  linting tool. <!-- 728 stars -->
- [sbt-scalafix](https://scalacenter.github.io/scalafix/): refactoring and linting tool for Scala using Scalafix. <!-- 91 stars -->
- [sbt-warning-diff](https://github.com/xuwei-k/sbt-warning-diff): show added/removed warnings. <!-- 9 stars -->

<div style="display: none;">

- [scalastyle-sbt-plugin](https://github.com/scalastyle/scalastyle-sbt-plugin):
  code style checking using Scalastyle. <!-- 114 stars -->
- [sbt-scapegoat](https://github.com/sksamuel/sbt-scapegoat): static analysis
  using Scapegoat. <!-- 63 stars -->
- [sbt-stats](https://github.com/orrsella/sbt-stats): generate source code
  statistics (lines of code etc). <!-- 53 stars -->
- [sbt-explicit-dependencies](https://github.com/cb372/sbt-explicit-dependencies):
  check that you have declared all your library dependencies correctly <!-- 12 stars -->
- [sbt-taglist](https://github.com/johanandren/sbt-taglist): find tags within
  source files (such as TODO and FIXME). <!-- 11 stars -->
- [sbt-rewarn](https://github.com/rtimush/sbt-rewarn): always display compilation warnings,
  despite the incremental compilation. <!-- 11 stars -->
- [sbt-jcheckstyle](https://github.com/xerial/sbt-jcheckstyle): Java code
  style checking using Checkstyle. <!-- 6 stars -->
</div>

### Utility and system plugins

- [MiMa](https://github.com/lightbend/mima): binary
  compatibility management for Scala libraries. <!-- 471 stars -->
- [sbt-git](https://github.com/sbt/sbt-git): run git commands from sbt.
  <!-- 233 stars -->
- [sbt-dotenv](https://github.com/philippus/sbt-dotenv): load environment variables from .env into the JVM System Environment for local development. <!-- 189 stars -->
- [sbt-dynver](https://github.com/sbt/sbt-dynver): set project version
  dynamically from git metadata. <!-- 87 stars -->
- [sbt-javaagent](https://github.com/sbt/sbt-javaagent): add Java agents to
  projects. <!-- 58 stars -->
- [sbt-nocomma](https://github.com/sbt/sbt-nocomma): reduce commas. <!-- 13 stars -->
- [sbt-jshell](https://github.com/xuwei-k/sbt-jshell): Java REPL for sbt.
  <!-- 10 stars -->
- [sbt-config](https://github.com/matejcerny/sbt-config): configures subproject via HOCON.
- [sbt-vimquit](https://github.com/sbt/sbt-vimquit): adds `:q` command. <!-- 6 stars -->

<div style="display: none;">
- [sbt-revolver](https://github.com/spray/sbt-revolver): auto-restart forked
  JVMs on update. <!-- 563 stars -->
- [sbt-conscript](https://github.com/foundweekends/conscript)
  ([docs](https://www.foundweekends.org/conscript/)): distribute apps using
  GitHub and Maven Central. <!-- 467 stars -->
- [sbt-errors-summary](https://github.com/Duhemm/sbt-errors-summary): show a
  summary of compilation errors. <!-- 145 stars -->
- [sbt-groll](https://github.com/sbt/sbt-groll): navigate git history inside
  sbt. <!-- 100 stars -->
- [sbt-prompt](https://github.com/agemooij/sbt-prompt): add promptlets and
  themes to your sbt prompt. <!-- 75 stars -->
- [sbt-crossproject](https://github.com/portable-scala/sbt-crossproject):
  cross-build Scala, Scala.js and Scala Native. <!-- 66 stars -->
- [sbt-proguard](https://github.com/sbt/sbt-proguard): run ProGuard on
  compiled sources. <!-- 63 stars -->
- [sbt-jni](https://github.com/sbt/sbt-jni): helpers for working with
  projects that use JNI. <!-- 51 stars -->
- [sbt-jol](https://github.com/ktoso/sbt-jol): inspect OpenJDK Java Object
  Layout from sbt. <!-- 48 stars -->
- [sbt-musical](https://github.com/tototoshi/sbt-musical): control iTunes
  from sbt (Mac only). <!-- 47 stars -->
- [horder](https://github.com/romanowski/hoarder): cache compilation
  artefacts for future builds. <!-- 31 stars -->
- [sbt-check](https://github.com/jeffreyolchovy/sbt-check): compile up to,
  and including, the typer phase. <!-- 10 stars -->
- [sbt-mima-version-check](https://github.com/ChristopherDavenport/sbt-mima-version-check): Automate which Mima Versions to Check <!-- 6 stars -->
- [sbt-tmpfs](https://github.com/cuzfrog/sbt-tmpfs): utilize tmpfs to speed
  up builds. <!-- 4 stars -->
- [sbt-sh](https://github.com/melezov/sbt-sh): run shell commands from sbt.
  <!-- 2 stars -->
- [sbt-ammonite-classpath](https://github.com/ThoughtWorksInc/sbt-ammonite-classpath): export classpath for [Ammonite](https://ammonite.io/) and [Almond](https://almond.sh/).
  <!-- 2 stars -->
- [sbt-version-scheme-enforcer-plugin](https://github.com/isomarcte/sbt-version-scheme-enforcer): Derive Mima settings for your library from your declared `versionScheme`. This supports Early SemVer, Strict SemVer, and Package Versioning Policy (PVP).

</div>

### Documentation plugins

- [mdoc](https://scalameta.org/mdoc/docs/installation.html#sbt): typechecked markdown documentation for Scala  <!-- 402 stars -->
- [sbt-unidoc](https://github.com/sbt/sbt-unidoc): create unified API
  documentation across subprojects. <!-- 126 stars -->
- [sbt-class-diagram](https://github.com/xuwei-k/sbt-class-diagram): generate
  class diagrams from Scala source code. <!-- 100 stars -->
- [sbt-plantuml](https://github.com/cheleb/sbt-plantuml): generate PlantUML diagram.

<div style="display: none;">

- [tut](https://github.com/tpolecat/tut): documentation and tutorial generator.
  <!-- 409  stars -->
- [Laika](https://github.com/planet42/Laika): Transform Markdown or reStructuredText
  into HTML or PDF with Templating.
  <!-- 161 stars -->
- [sbt-site](https://github.com/sbt/sbt-site): site generator.
  <!-- 131 stars -->
- [sbt-microsites](https://github.com/47degrees/sbt-microsites): generate
  and publish microsites using Jekyll. <!-- 125 stars -->
- [sbt-ghpages](https://github.com/sbt/sbt-ghpages): publish generated
  sites to GitHub pages. <!-- 71 stars -->
- [sbt-api-mappings](https://github.com/ThoughtWorksInc/sbt-api-mappings):
  generate Scaladoc `apiMappings` for common Scala libraries. <!-- 49 stars -->
- [literator](https://github.com/laughedelic/literator):
  generate literate-style markdown docs from your sources. <!-- 33 stars -->
- [sbt-example](https://github.com/ThoughtWorksInc/sbt-example): generate ScalaTest test suites from examples in Scaladoc. <!-- 17 stars -->
- [sbt-delombok](https://github.com/ThoughtWorksInc/sbt-delombok): delombok Java sources files that contain Lombok annotations to make Javadoc contain Lombok-generated classes and methods. <!-- 2 stars -->
- [sbt-alldocs](https://github.com/glngn/sbt-alldocs): collect all the docs for a project and dependencies into a single folder.
- [sbt-apidoc](https://github.com/valydia/sbt-apidoc): A port of [apidocjs](https://apidocjs.com) to sbt, to document REST Api. <!-- 1 star -->
- [sbt-github-pages](https://github.com/Kevin-Lee/sbt-github-pages)
  ([docs](https://kevin-lee.github.io/sbt-github-pages)): publish a website to GitHub Pages with minimal effort - works well with GitHub Actions.
- [sbt-docusaur](https://github.com/Kevin-Lee/sbt-docusaur)
  ([docs](https://kevin-lee.github.io/sbt-docusaur)): build a website using Docusaurus and publish to GitHub Pages with minimal effort - works well with GitHub Actions.
- [sbt-hl-compiler](https://github.com/cchantep/sbt-hl-compiler/): compile the code snippets from documentation (to keep it consistent). <!-- 1 star -->
- [sbt-scaladoc-compiler](https://github.com/cchantep/sbt-scaladoc-compiler/): compile the code snippets included in Scaladoc comments. <!-- 2 stars -->

</div>

### Code coverage plugins

- [sbt-scoverage](https://github.com/scoverage/sbt-scoverage): Scala code
  coverage using Scoverage. <!-- 649 stars -->
- [sbt-jacoco](https://github.com/sbt/sbt-jacoco): Scala and Java code coverage
  using JaCoCo. <!-- 123 stars -->

<div style="display: none;">
### Create new project plugins

- [sbt-fresh](https://github.com/sbt/sbt-fresh): create an opinionated fresh
  sbt project. <!-- 177 stars -->

</div>
<div style="display: none;">

### Deployment integration plugins

- [sbt-riotctl](https://github.com/riot-framework/sbt-riotctl): deploy
  applications as systemd services directly to a Raspberry Pi, ensuring
  dependencies (e.g. wiringpi) are met.
- [sbt-kind](https://github.com/tirithel/sbt-kind): load built docker images into a [kind](https://kind.sigs.k8s.io/) cluster.

</div>
<div style="display: none;">

### Framework-specific plugins

- [sbt-newrelic](https://github.com/gilt/sbt-newrelic): NewRelic support for
  artefacts built with sbt-native-packager. <!-- 73 stars -->
- [sbt-spark](https://github.com/alonsodomin/sbt-spark): Spark application
  configurator. <!-- 7 stars -->
- [sbt-api-builder](https://github.com/sirocchj/sbt-api-builder): support for
  ApiBuilder from within sbt's shell. <!-- 1 star -->

</div>

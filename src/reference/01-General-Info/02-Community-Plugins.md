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

[Typesafe](https://www.typesafe.com) has provided a freely available
[Ivy Repository](https://repo.scala-sbt.org/scalasbt) for sbt projects
to use. This Ivy repository is mirrored from the freely available
[Bintray service](https://bintray.com).
If you'd like to submit your plugin, please follow these instructions:
[Bintray For Plugins][Bintray-For-Plugins].

### Available Plugins

Please feel free to
[submit a pull request](https://github.com/sbt/website/pulls) that adds
your plugin to the list.

#### Plugins for IDEs

-   IntelliJ IDEA
    -   sbt Plugin to generate IDEA project configuration: <https://github.com/mpeltonen/sbt-idea>
    -   IDEA Plugin to embed an sbt Console into the IDE: <https://github.com/orfjackal/idea-sbt-plugin>
-   Netbeans (no support to create a new sbt project yet)
    -   sbt-netbeans-plugin (older): <https://github.com/remeniuk/sbt-netbeans-plugin>
    -   sbt plugin to generate NetBeans configuration: <https://github.com/dcaoyuan/nbsbt>
    -   sbt plugin to add scala support to NetBeans: <https://github.com/dcaoyuan/nbscala>
-   Eclipse: <https://github.com/typesafehub/sbteclipse>
-   Sublime Text: <https://github.com/orrsella/sbt-sublime>
-   Ensime: <https://github.com/aemoncannon/ensime-sbt-cmd>
-   sbt-mode for Emacs: <https://github.com/hvesalai/sbt-mode>
-   sbt-ctags (manage library dependency sources for vim, emacs,
    sublime) <https://github.com/kalmanb/sbt-ctags>

### Test plugins

-   junit_xml_listener: <https://github.com/ijuma/junit_xml_listener>
-   sbt-growl-plugin: <https://github.com/softprops/sbt-growl-plugin>
-   sbt-teamcity-test-reporting-plugin:
    <https://github.com/guardian/sbt-teamcity-test-reporting-plugin>
-   xsbt-cucumber-plugin:
    <https://github.com/skipoleschris/xsbt-cucumber-plugin>
-   sbt-multi-jvm: <https://github.com/sbt/sbt-multi-jvm>
-   sbt-testng-interface: <https://github.com/sbt/sbt-testng-interface>
-   sbt-doctest: <https://github.com/tkawachi/sbt-doctest>
-   sbt-cassandra-plugin: <https://github.com/hochgi/sbt-cassandra-plugin>
-   sbt-tabular-test-reporter: <https://github.com/programmiersportgruppe/sbt-tabular-test-reporter>
-   sbt-notifications: <https://github.com/PavelPenkov/sbt-notifications> (sends notifications when test run is finished)
-   sbt-dynamodb: <https://github.com/localytics/sbt-dynamodb> (downloads and runs DynamoDB Local for testing)
-   sbt-sqs: <https://github.com/localytics/sbt-sqs> (downloads and runs ElasticMQ for testing)
-   sbt-s3: <https://github.com/localytics/sbt-s3> (downloads and runs S3Proxy for testing)

#### Code coverage plugins

-   sbt-scct: <https://github.com/sqality/sbt-scct>
-   sbt-scoverage: <https://github.com/scoverage/sbt-scoverage>
-   jacoco4sbt: <https://github.com/sbt/jacoco4sbt>
-   sbt-coveralls: <https://github.com/scoverage/sbt-coveralls>
-   sbt-clover: <https://github.com/shanbin/sbt-clover>

#### Static code analysis plugins

-   wartremover: <https://github.com/puffnfresh/wartremover> (WartRemover -
    Scala static analysis)
-   cpd4sbt: <https://github.com/sbt/cpd4sbt> (copy/paste detection,
    works for Scala, too)
-   findbugs4sbt: <https://github.com/sbt/findbugs4sbt> (FindBugs only
    supports Java projects atm)
-   scalastyle: <https://github.com/scalastyle/scalastyle-sbt-plugin>
    (Scalastyle - static code checker for Scala)
-   sbt-scapegoat: <https://github.com/sksamuel/sbt-scapegoat> (Scapegoat - Scala static code analysis)
-   sbt-stats: <https://github.com/orrsella/sbt-stats> (simple,
    extensible source code statistics)
-   sbt-checkstyle-plugin: <https://github.com/etsy/sbt-checkstyle-plugin>
    (Checkstyle - static analysis for Java code)
-   sbt-jcheckstyle: <https://github.com/xerial/sbt-jcheckstyle>
    (handy checkstyle runner for Java projects)

#### One jar plugins

-   sbt-assembly: <https://github.com/sbt/sbt-assembly>
-   xsbt-proguard-plugin:
    <https://github.com/adamw/xsbt-proguard-plugin>
-   sbt-deploy: <https://github.com/reaktor/sbt-deploy>
-   sbt-appbundle (os x standalone):
    <https://github.com/sbt/sbt-appbundle>
-   sbt-onejar (Packages your project using One-JAR™):
    <https://github.com/sbt/sbt-onejar>

#### Release plugins

-   sbt-native-packager: <https://github.com/sbt/sbt-native-packager>
-   sbt-ghpages (publishes generated site and api):
    <https://github.com/sbt/sbt-ghpages>
-   sbt-pgp (PGP signing plugin, can generate keys too):
    <https://github.com/sbt/sbt-pgp>
-   sbt-release (customizable release process):
    <https://github.com/sbt/sbt-release>
-   sbt-sonatype-plugin (releases to Sonatype Nexus repository)
    <https://github.com/xerial/sbt-sonatype>
-   sbt-aether-plugin (Published artifacts using Sonatype Aether):
    <https://github.com/arktekk/sbt-aether-deploy>
-   posterous-sbt: <https://github.com/n8han/posterous-sbt>
-   sbt-signer-plugin: <https://github.com/rossabaker/sbt-signer-plugin>
-   sbt-izpack (generates IzPack an installer):
    <http://software.clapper.org/sbt-izpack/>
-   sbt-unique-version (emulates unique snapshots):
    <https://github.com/sbt/sbt-unique-version>
-   sbt-install4j: <https://github.com/jpsacha/sbt-install4j>
-   sbt-pack (generates packages with dependent jars and launch
    scripts): <https://github.com/xerial/sbt-pack>
-   sbt-start-script: <https://github.com/sbt/sbt-start-script>
-   xitrum-package (collects dependency .jar files for standalone Scala
    programs): <https://github.com/ngocdaothanh/xitrum-package>

#### Deployment integration plugins

-   sbt-appengine: <https://github.com/sbt/sbt-appengine>
-   sbt-cloudbees-plugin:
    <https://github.com/timperrett/sbt-cloudbees-plugin>
-   sbt-jelastic-deploy:
    <https://github.com/casualjim/sbt-jelastic-deploy>
-   sbt-elasticbeanstalk (Deploy WAR files to AWS Elastic Beanstalk): <https://github.com/sqs/sbt-elasticbeanstalk>
-   sbt-cloudformation (AWS CloudFormation templates and stacks management): <https://github.com/tptodorov/sbt-cloudformation>
-   sbt-codedeploy: <https://github.com/gilt/sbt-codedeploy>
-   sbt-heroku: <https://github.com/heroku/sbt-heroku>

#### Monitoring integration plugins

-   sbt-newrelic: <https://github.com/gilt/sbt-newrelic>

#### Web and frontend development plugins

-   xsbt-web-plugin:
    <https://github.com/earldouglas/xsbt-web-plugin>
-   xsbt-webstart: <https://github.com/ritschwumm/xsbt-webstart>
-   sbt-gwt-plugin: <https://github.com/cdietze/sbt-gwt-plugin>
-   coffeescripted-sbt:
    <https://github.com/softprops/coffeescripted-sbt>
-   less-sbt (for less-1.3.0): <https://github.com/softprops/less-sbt>
-   sbt-less-plugin (it uses less-1.3.0):
    <https://github.com/btd/sbt-less-plugin>
-   sbt-emberjs: <https://github.com/stefri/sbt-emberjs>
-   sbt-closure: <https://github.com/eltimn/sbt-closure>
-   sbt-imagej: <https://github.com/jpsacha/sbt-imagej>
-   sbt-yui-compressor:
    <https://github.com/indrajitr/sbt-yui-compressor>
-   sbt-requirejs: <https://github.com/scalatra/sbt-requirejs>
-   sbt-vaadin-plugin:
    <https://github.com/henrikerola/sbt-vaadin-plugin>
-   sbt-purescript: <https://github.com/eamelink/sbt-purescript>
-   sbt-jasmine-plugin (Run javascript tests with jasmine within sbt):
    <https://github.com/joescii/sbt-jasmine-plugin>
-   sbt-javafx (Package JavaFX applications): <https://github.com/kavedaa/sbt-javafx>
-   sbt-phantomjs (Automated installer and configurator for PhantomJS): <https://github.com/saturday06/sbt-phantomjs>
-   sbt-play-scalajs: <https://github.com/vmunier/sbt-play-scalajs>
-   scalatra-sbt: <https://github.com/scalatra/scalatra-sbt>

#### Documentation plugins

-   tut (Scala literate programming): <https://github.com/tpolecat/tut>
-   sbt-site (Site generation for sbt):
    <https://github.com/sbt/sbt-site>
-   sbt-lwm (Convert lightweight markup files, e.g., Markdown and
    Textile, to HTML): <http://software.clapper.org/sbt-lwm/>
-   Laika (Template-based site generation, Markdown, reStructuredText,
    no external tools): <http://planet42.github.io/Laika/>
-   literator-plugin (Converts sources into markdown documents):
    <https://github.com/laughedelic/literator>
-   sbt-class-diagram (Create a class diagram)
    <https://github.com/xuwei-k/sbt-class-diagram>
-   sbt-api-mappings (Resolves external links in ScalaDoc for common Scala libraries)
    <https://github.com/ThoughtWorksInc/sbt-api-mappings>

#### Library dependency plugins

-   sbt-dependency-graph (Creates a graphml file of the dependency
    tree): <https://github.com/jrudolph/sbt-dependency-graph>
-   ls-sbt (An sbt interface for ls.implicit.ly):
    <https://github.com/softprops/ls>
-   sbt-dirty-money (Cleans Ivy2 cache):
    <https://github.com/sbt/sbt-dirty-money>
-   sbt-updates (Checks Maven repos for dependency updates):
    <https://github.com/rtimush/sbt-updates>
-   sbt-lock (Locks library versions for reproducible build):
    <https://github.com/tkawachi/sbt-lock>
-   sbt-versions (Checks for updated versions of your dependencies):
    <https://github.com/sksamuel/sbt-versions>
-   sbt-bobby (Prevents outdated dependencies from being used by your project):
    <https://github.com/hmrc/sbt-bobby>

#### Build interoperability plugins

-   ant4sbt: <https://github.com/sbt/ant4sbt>
-   sbt-pom-reader: <https://github.com/sbt/sbt-pom-reader>

#### Create new project plugins

-   np (Dead simple new project directory generation):
    <https://github.com/softprops/np>
-   npt (Creates new project skeletons based on templates):
    <https://github.com/reikje/npt>
-   sbt-fresh (create an opinionated fresh sbt project):
    <https://github.com/sbt/sbt-fresh>

#### Utility and system plugins

-   sbt-javaversioncheck (enforces build requirement for specific version level of Java): <https://github.com/sbt/sbt-javaversioncheck>
-   sbt-scalariform (adding support for source code formatting using
    Scalariform): <https://github.com/sbt/sbt-scalariform>
-   sbt-process-runner (Run your own applications from SBT console)
    <https://github.com/whysoserious/sbt-process-runner>
-   jot (Write down your ideas lest you forget them)
    <https://github.com/softprops/jot>
-   sbt-editsource (A poor man's *sed*(1), for sbt):
    <http://software.clapper.org/sbt-editsource/>
-   sbt-conflict-classes (Show conclict classes from classpath):
    <https://github.com/todesking/sbt-conflict-classes>
-   sbt-cross (An alternative to `crossScalaVersions`):
    <https://github.com/lucidsoftware/sbt-cross>
-   sbt-cross-building (Simplifies building your plugins for multiple
    versions of sbt): <https://github.com/jrudolph/sbt-cross-building>
-   sbt-doge (aggregates tasks across subprojects and their `crossScalaVersions`):
    <https://github.com/sbt/sbt-doge>
-   sbt-revolver (Triggered restart, hot reloading):
    <https://github.com/spray/sbt-revolver>
-   sbt-scalaedit (Open and upgrade ScalaEdit (text editor)):
    <https://github.com/kjellwinblad/sbt-scalaedit-plugin>
-   sbt-man (Looks up scaladoc): <https://github.com/sbt/sbt-man>
-   sbt-taglist (Looks for TODO-tags in the sources):
    <https://github.com/johanandren/sbt-taglist>
-   migration-manager:
    <https://github.com/typesafehub/migration-manager>
-   sbt-aspectj: <https://github.com/sbt/sbt-aspectj>
-   sbt-properties: <https://github.com/sbt/sbt-properties>
-   sbt-multi-publish (publish to more than one repository
    simultaneously):
    <https://github.com/davidharcombe/sbt-multi-publish>
-   sbt-about-plugins (shows some details about plugins loaded):
    <https://github.com/jozic/sbt-about-plugins>
-   sbt-one-log (make Log dependency easy):
    <https://github.com/zavakid/sbt-one-log>
-   sbt-git-stamp (include git metadata in MANIFEST.MF file in artifact):
    <https://bitbucket.org/pkaeding/sbt-git-stamp>
-   fm-sbt-s3-resolver (Resolve and Publish using Amazon S3):
    <https://github.com/frugalmechanic/fm-sbt-s3-resolver>
-   sbt-notebook (Adds scala-notebook capabilities to sbt projects):
    <https://github.com/alexarchambault/sbt-notebook>
-   sbt-sh (executes shell commands):
    <https://github.com/steppenwells/sbt-sh>
-   cronish-sbt (interval sbt / shell command execution):
    <https://github.com/philcali/cronish-sbt>
-   git (executes git commands): <https://github.com/sbt/sbt-git>
-   svn (execute svn commands): <https://github.com/xuwei-k/sbtsvn>
-   sbt-groll (sbt plugin to navigate the Git history):
    <https://github.com/sbt/sbt-groll>
-   sbt-twt (twitter processor for sbt):
    <https://github.com/sbt/sbt-twt>
-   sbt-compile-quick-plugin (compile and package a single file):
    <https://github.com/etsy/sbt-compile-quick-plugin>
-   sbt-meow (display ascii-fied random cat pictures):
    <https://github.com/thricejamie/sbt-meow>
-   sbt-build-files-watcher (show message on build files changed):
    <https://github.com/tototoshi/sbt-build-files-watcher>
-   sbt-backup (compress and scp a directory):
    <https://github.com/sensatus/sbt-backup>
-   sbt-project-graph (visualise inter-project dependencies):
    <https://github.com/dwijnand/sbt-project-graph>
-   solr-plugin (start solr search engine from sbt)
    <https://github.com/sgrouples/sbt-solr-plugin>
-   sbt-todolist (find TODOs in source files and print them to console):
    <https://github.com/fedragon/sbt-todolist>
-   sbt-ortho (simple spell and English style checker):
    <https://github.com/henrikengstrom/sbt-ortho>
-   sbt-write-output-to-file (redirect the output of `run` to a file):
    <https://github.com/cb372/sbt-write-output-to-file>

#### Database plugins

-   flyway-sbt (Flyway - The agile database migration framework):
    <http://flywaydb.org/getstarted/firststeps/sbt.html>
-   sbt-liquibase (Liquibase RDBMS database migrations):
    <https://github.com/bigtoast/sbt-liquibase>
-   sbt-dbdeploy (dbdeploy, a database change management tool):
    <https://github.com/mr-ken/sbt-dbdeploy>
-   sbt-pillar (tasks for managing Cassandra DB migrations using Pillar library):
    <https://github.com/henders/sbt-pillar-plugin>

#### Code generator plugins

-   sbt-planout4j (Compiling Planout4j yaml to Planout language):
    <https://github.com/reikje/sbt-planout4j>
-   sbt-buildinfo (Generate Scala source for any settings):
    <https://github.com/sbt/sbt-buildinfo>
-   pttrt (Pass any data from compile-time to run-time):
    <https://github.com/Atry/pttrt>
-   sbt-haxe (Compiling [Haxe](http://www.haxe.org/) to Java):
    <https://github.com/qifun/sbt-haxe>
-   sbt-scalabuff (Google Protocol Buffers with native scala suppport
    thru ScalaBuff): <https://github.com/sbt/sbt-scalabuff>
-   sbt-fmpp (FreeMarker Scala/Java Templating):
    <https://github.com/sbt/sbt-fmpp>
-   sbt-scalaxb (XSD and WSDL binding):
    <https://github.com/eed3si9n/scalaxb>
-   sbt-protobuf (Google Protocol Buffers):
    <https://github.com/sbt/sbt-protobuf>
-   sbt-cppp (Cross-Project Protobuf Plugin for Sbt):
    <https://github.com/Atry/sbt-cppp>
-   sbt-avro (Apache Avro): <https://github.com/cavorite/sbt-avro>
-   sbt-xjc (XSD binding, using
    [JAXB XJC](http://download.oracle.com/javase/6/docs/technotes/tools/share/xjc.html) ):
    <https://github.com/sbt/sbt-xjc>
-   xsbt-scalate-generate (Generate/Precompile Scalate Templates):
    <https://github.com/backchatio/xsbt-scalate-generate>
-   sbt-antlr (Generate Java source code based on ANTLR3 grammars):
    <https://github.com/stefri/sbt-antlr>
-   sbt-antlr4 (Antlr4 runner for generating Java source code):
    <https://github.com/ihji/sbt-antlr4>
-   xsbt-reflect (Generate Scala source code for project name and
    version): <https://github.com/ritschwumm/xsbt-reflect>
-   lifty (Brings scaffolding to sbt): <https://github.com/lifty/lifty>
-   sbt-thrift (Thrift Code Generation):
    <https://github.com/bigtoast/sbt-thrift>
-   xsbt-hginfo (Generate Scala source code for Mercurial repository
    information):
    <https://bitbucket.org/lukas_pustina/xsbt-hginfo>
-   sbt-scalashim (Generate Scala shim like `sys.error`):
    <https://github.com/sbt/sbt-scalashim>
-   sbtend (Generate Java source code from
    [xtend](https://www.eclipse.org/xtend/) ):
    <https://github.com/xuwei-k/sbtend>
-   sbt-boilerplate (generating scala.Tuple/Function related boilerplate
    code): <https://github.com/sbt/sbt-boilerplate>
-   sbt-fxml (Generates controller classes for JavaFX FXML files):
    <https://bitbucket.org/phdoerfler/sbt-fxml>
-   sbt-clojure (Compiling Clojure code):
    <https://github.com/Geal/sbt-clojure>
-   sbt-build-info-conf (Generates resources.conf file with build information):
    <https://github.com/Sensatus/sbt-build-info-conf>
-   sbt-frege (Build [Frege](https://github.com/frege/frege) code):
    <https://github.com/earldouglas/sbt-frege>
-   sbt-swagger-codegen (Models, Client and Server code generation integrated as an SBT plugin. Generate code from your Swagger(https://github.com/swagger-api) files):
    <https://github.com/unicredit/sbt-swagger-codegen>
-   sbt-heroku-deploy (Deploy Scala Web applications to Heroku):
    <https://github.com/earldouglas/sbt-heroku-deploy>
-   scavro (Code generation from [Avro](http://avro.apache.org/) schema):
    <https://github.com/oedura/scavro>
-   sbt-spi-plugin (Generates provider-configuration files in the resource directory META-INF/services for later use with ServiceLoader)
    <https://github.com/nyavro/spi-plugin>

#### Game development plugins

-   sbt-lwjgl-plugin (Light Weight Java Game Library):
    <https://github.com/philcali/sbt-lwjgl-plugin>
-   sbt-scage-plugin (Scala Game Engine):
    <https://github.com/mvallerie/sbt-scage-plugin>

#### Android plugins

-   android-plugin: <https://github.com/jberkel/android-plugin>
-   android-sdk-plugin: <https://github.com/pfn/android-sdk-plugin>

#### iOS plugins

-   sbt-robovm (Compiling Scala using RoboVM for iOS or native OSX): <https://github.com/roboscala/sbt-robovm>

#### OSGi plugin

-   sbtosgi: <https://github.com/sbt/sbt-osgi>

#### C++ interop plugins

- sbt-javacpp (JavaCPP is the missing bridge between Java and native C++; this lib helps you download platform-specific presets): <https://github.com/lloydmeta/sbt-javacpp>

#### Computer vision plugins

- sbt-opencv (Start an OpenCV via JavaCV project in 1 line): <https://github.com/lloydmeta/sbt-opencv>

#### Plugin bundles

-   tl-os-sbt-plugins (Version, Release, and Package Management, Play 2.0 and Git utilities) :
    <https://github.com/trafficland/tl-os-sbt-plugins>

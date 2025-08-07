  
  [Coursier]: https://get-coursier.io/
  [Setup]: Setup.html
  [Basic-Def]: Basic-Def.html
  [Full-Def]: Full-Def.html
  [Library-Dependencies]: ../guide/library-dependency-basics.md
  [Update-Report]: Update-Report.html
  [Paths]: Paths.html
  [Resolvers]: Resolvers.html
  [Publishing]: Publishing.html
  [Cross-Build]: ../concepts/cross-building.md
  [Cached-Resolution]: Cached-Resolution.html

sbt update
==========

See [library depdency basics][Library-Dependencies] in the Getting Started guide to learn about the basics.

Synopsis
--------

`sbt` \[_query_ / \] `update`

Description
-----------

sbt uses [Coursier][Coursier] to implement library management,
also known as a package manager in other ecosystems.
The general idea of library management is that you can specify external libraries you would
like to use in your subprojects, and the library management system would:
- Check if such versions exists in the listed repositories
- Look for the transitive dependencies (i.e. the libraries used by the libraries)
- Attempt to resolve version conflicts, if any
- Download the artifacts, such as JAR files, from the repositories

### Dependencies

Declaring a dependency looks like:

```scala
libraryDependencies += groupID %% artifactID % revision
```

or

```scala
libraryDependencies += groupID %% artifactID % revision % configuration
```

<!--
See [configurations](#ivy-configurations) for details on configuration
mappings.
-->

Also, several dependencies can be declared together:

```scala
libraryDependencies ++= Seq(
  groupID %% artifactID % revision,
  groupID %% otherID % otherRevision
)
```

If you are using a dependency that was built with sbt, double the first
`%` to be `%%`:

```scala
libraryDependencies += groupID %% artifactID % revision
```

This will use the right JAR for the dependency built with the version of
Scala that you are currently using. If you get an error while resolving
this kind of dependency, that dependency probably wasn't published for
the version of Scala you are using. See [Cross building][Cross-Build] for details.

<!--
Ivy can select the latest revision of a module according to constraints
you specify. Instead of a fixed revision like `"1.6.1"`, you specify
`"latest.integration"`, `"2.9.+"`, or `"[1.0,)"`. See the
[Ivy revisions](https://ant.apache.org/ivy/history/2.3.0/ivyfile/dependency.html#revision)
documentation for details.
-->

### `versionScheme` and eviction errors

sbt allows library authors to declare the version semantics using the `versionScheme` setting:

```scala
// Semantic Versioning applied to 0.x, as well as 1.x, 2.x, etc
versionScheme := Some(VersionScheme.EarlySemVer)
```

When Coursier finds multiple versions of a library, for example Cats Effect 2.x and Cats Effect 3.0.0-M4,
it often resolves the conflict by removing the older version from the graph. This process is colloquially called eviction,
like "Cats Effect 2.2.0 was evicted."

This would work if the new tenant is binary compatible with Cats Effect 2.2.0.
In this case, the library authors have declared that they are _not_ binary compatible, so the eviction was actually unsafe.
An unsafe eviction would cause runtime issues such as `ClassNotFoundException`. Instead Coursier should've failed to resolve.

```scala
lazy val use = project
  .settings(
    name := "use",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % "0.21.11",
      "org.typelevel" %% "cats-effect" % "3.0.0-M4",
    ),
  )
```

sbt performs this secondary compatibility check after Coursier returns a candidate:

```
[error] stack trace is suppressed; run last use / update for the full output
[error] (use / update) found version conflict(s) in library dependencies; some are suspected to be binary incompatible:
[error]
[error]   * org.typelevel:cats-effect_2.12:3.0.0-M4 (early-semver) is selected over {2.2.0, 2.0.0, 2.0.0, 2.2.0}
[error]       +- use:use_2.12:0.1.0-SNAPSHOT                        (depends on 3.0.0-M4)
[error]       +- org.http4s:http4s-core_2.12:0.21.11                (depends on 2.2.0)
[error]       +- io.chrisdavenport:vault_2.12:2.0.0                 (depends on 2.0.0)
[error]       +- io.chrisdavenport:unique_2.12:2.0.0                (depends on 2.0.0)
[error]       +- co.fs2:fs2-core_2.12:2.4.5                         (depends on 2.2.0)
[error]
[error]
[error] this can be overridden using libraryDependencySchemes or evictionErrorLevel
```

This mechanism is called the _eviction error_.

#### Opting out of the the eviction error

If the library authors have declared the compatibility breakage, but if you want to ignore
the strict check (often for `scala-xml`), you can write this in `project/plugins.sbt` and `build.sbt`:

```scala
libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
```

To ignore all eviction errors:

```scala
evictionErrorLevel := Level.Info
```

### Resolvers

sbt uses the standard Maven Central repository by default. Declare additional repositories with the form:

```scala
resolvers += name at location
```

For example:

```scala
libraryDependencies ++= Seq(
    "org.apache.derby" % "derby" % "10.4.1.3",
    "org.specs" % "specs" % "1.6.1"
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
```

sbt can search your local Maven repository if you add it as a
repository:

```scala
resolvers += Resolver.mavenLocal
```

<!--
See [Resolvers][Resolvers] for details on defining other types of repositories.
-->

#### Override default resolvers

`resolvers` configures additional, inline user resolvers. By default,
`sbt` combines these resolvers with default repositories (Maven Central
and the local Ivy repository) to form `externalResolvers`. To have more
control over repositories, set `externalResolvers` directly. To only
specify repositories in addition to the usual defaults, configure
`resolvers`.

For example, to use the Sonatype OSS Snapshots repository in addition to
the default repositories,

```scala
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
```

To use the local repository, but not the Maven Central repository:

```scala
externalResolvers := Resolver.combineDefaultResolvers(resolvers.value.toVector, mavenCentral = false)
```

#### Override all resolvers for all builds

The repositories used to retrieve sbt, Scala, plugins, and application
dependencies can be configured globally and declared to override the
resolvers configured in a build or plugin definition. There are two
parts:

1.  Define the repositories used by the launcher.
2.  Specify that these repositories should override those in build
    definitions.

The repositories used by the launcher can be overridden by defining
`~/.sbt/repositories`, which must contain a `[repositories]` section
with the same format as the `Launcher` configuration file. For example:

```
[repositories]
local
my-maven-repo: https://example.org/repo
my-ivy-repo: https://example.org/ivy-repo/, [organization]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext]
```

A different location for the repositories file may be specified by the
`sbt.repository.config` system property in the sbt startup script. The
final step is to set `sbt.override.build.repos` to true to use these
repositories for dependency resolution and retrieval.

### Exclude Transitive Dependencies

In certain cases a transitive dependency should be excluded from
all dependencies. This can be achieved by setting up `ExclusionRules`
in `excludeDependencies`. 

```scala
excludeDependencies ++= Seq(
  // commons-logging is replaced by jcl-over-slf4j
  ExclusionRule("commons-logging", "commons-logging")
)
```

To exclude certain transitive dependencies of a dependency, use the
`excludeAll` or `exclude` methods. The `exclude` method should be used
when a pom will be published for the project. It requires the
organization and module name to exclude. For example,

```scala
libraryDependencies += 
  ("log4j" % "log4j" % "1.2.15").exclude("javax.jms", "jms")
```

<!--
The `excludeAll` method is more flexible, but because it cannot be
represented in a pom.xml, it should only be used when a pom doesn't need
to be generated. For example,

```scala
libraryDependencies +=
  "log4j" % "log4j" % "1.2.15" excludeAll(
    ExclusionRule(organization = "com.sun.jdmk"),
    ExclusionRule(organization = "com.sun.jmx"),
    ExclusionRule(organization = "javax.jms")
  )
```

See [ModuleID](../api/sbt/librarymanagement/ModuleID.html) for API details.
-->

### Explicit URL

If your project requires a dependency that is not present in a
repository, a direct URL to its jar can be specified as follows:

```scala
libraryDependencies += "slinky" % "slinky" % "2.1" from "https://slinky2.googlecode.com/svn/artifacts/2.1/slinky.jar"
```

The URL is only used as a fallback if the dependency cannot be found
through the configured repositories. Also, the explicit URL is not
included in published metadata (that is, the pom or ivy.xml).

### Disable Transitivity

By default, these declarations fetch all project dependencies,
transitively. In some instances, you may find that the dependencies
listed for a project aren't necessary for it to build. Projects using
the Felix OSGI framework, for instance, only explicitly require its main
jar to compile and run. Avoid fetching artifact dependencies with either
`intransitive()` or `notTransitive()`, as in this example:

```scala
libraryDependencies += ("org.apache.felix" % "org.apache.felix.framework" % "1.8.0").intransitive()
```

### Classifiers

You can specify the classifier for a dependency using the `classifier`
method. For example, to get the jdk15 version of TestNG:

```scala
libraryDependencies += ("org.testng" % "testng" % "5.7").classifier("jdk15")
```

For multiple classifiers, use multiple `classifier` calls:

```scala
libraryDependencies += 
  "org.lwjgl.lwjgl" % "lwjgl-platform" % lwjglVersion classifier "natives-windows" classifier "natives-linux" classifier "natives-osx"
```

To obtain particular classifiers for all dependencies transitively, run
the `updateClassifiers` task. By default, this resolves all artifacts
with the `sources` or `javadoc` classifier. Select the classifiers to
obtain by configuring the `transitiveClassifiers` setting. For example,
to only retrieve sources:

```scala
transitiveClassifiers := Seq("sources")
```

### Download Sources

Downloading source and API documentation jars is usually handled by an
IDE plugin. These plugins use the `updateClassifiers` and
`updateSbtClassifiers` tasks, which produce an `Update-Report`
referencing these jars.

To have sbt download the dependency's sources without using an IDE
plugin, add `withSources()` to the dependency definition. For API jars,
add `withJavadoc()`. For example:

```scala
libraryDependencies += 
  ("org.apache.felix" % "org.apache.felix.framework" % "1.8.0").withSources().withJavadoc()
```

Note that this is not transitive. Use the `update*Classifiers` tasks
for that.

<!--
### Extra Attributes

[Extra attributes](https://ant.apache.org/ivy/history/2.3.0/concept.html#extra)
can be specified by passing key/value pairs to the `extra` method.

To select dependencies by extra attributes:

```scala
libraryDependencies += ("org" % "name" % "rev").extra("color" -> "blue")
```

To define extra attributes on the current project:

```scala
projectID := {
    val previous = projectID.value
    previous.extra("color" -> "blue", "component" -> "compiler-interface")
}
```
-->

<!--
#### Configurations

Ivy configurations are a useful feature for your build when you need
custom groups of dependencies, such as for a plugin. Ivy configurations
are essentially named sets of dependencies. You can read the
[Ivy documentation](https://ant.apache.org/ivy/history/2.3.0/tutorial/conf.html)
for details.

The built-in use of configurations in sbt is similar to scopes in Maven.
sbt adds dependencies to different classpaths by the configuration that
they are defined in. See the description of
[Maven Scopes](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Scope)
for details.

You put a dependency in a configuration by selecting one or more of its
configurations to map to one or more of your project's configurations.
The most common case is to have one of your configurations `A` use a
dependency's configuration `B`. The mapping for this looks like
`"A->B"`. To apply this mapping to a dependency, add it to the end of
your dependency definition:

```scala
libraryDependencies += "org.scalatest" %% "scalatest" % "$example_scalatest_version$" % "test->compile"
```

This says that your project's `"test"` configuration uses `ScalaTest`'s
`"compile"` configuration. See the
[Ivy documentation](https://ant.apache.org/ivy/history/2.3.0/tutorial/conf.html)
for more advanced mappings. Most projects published to Maven
repositories will use the `"compile"` configuration.

A useful application of configurations is to group dependencies that are
not used on normal classpaths. For example, your project might use a
`"js"` configuration to automatically download jQuery and then include
it in your jar by modifying `resources`. For example:

```scala
val JS = config("js") hide

ivyConfigurations += JS

libraryDependencies += "jquery" % "jquery" % "3.2.1" % "js->default" from "https://code.jquery.com/jquery-3.2.1.min.js"

Compile / resources ++= update.value.select(configurationFilter("js"))
```

The `config` method defines a new configuration with name `"js"` and
makes it private to the project so that it is not used for publishing.
See [Update Report][Update-Report] for more information on selecting
managed artifacts.

A configuration without a mapping (no `"->"`) is mapped to `"default"`
or `"compile"`. The `->` is only needed when mapping to a different
configuration than those. The ScalaTest dependency above can then be
shortened to:

```scala
libraryDependencies += "org.scalatest" %% "scalatest" % "$example_scalatest_version$" % "test"
```
-->

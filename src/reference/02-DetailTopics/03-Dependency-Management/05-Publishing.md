---
out: Publishing.html
---

  [Artifacts]: Artifacts.html
  [Resolvers]: Resolvers.html
  [Cross-Build]: Cross-Build.html

Publishing
----------

This page describes how to publish your project. Publishing consists of
uploading a descriptor, such as an Ivy file or Maven POM, and artifacts,
such as a jar or war, to a repository so that other projects can specify
your project as a dependency.

The `publish` action is used to publish your project to a remote
repository. To use publishing, you need to specify the repository to
publish to and the credentials to use. Once these are set up, you can
run `publish`.

The `publishLocal` action is used to publish your project to your Ivy local
file repository, which is usually located at `\$HOME/.ivy2/local/`. You can
then use this project from other projects on the same machine.

### Skip publishing

To avoid publishing a project, add the following setting to the subprojects that you want to skip:

```scala
publish / skip := true
```

Common use case is to prevent publishing of the root project.

### Define the repository

To specify the repository, assign a repository to `publishTo` and
optionally set the publishing style. For example, to upload to Nexus:

```scala
publishTo := Some("Sonatype Snapshots Nexus" at "https://oss.sonatype.org/content/repositories/snapshots")
```

To publish to a local maven repository:

```scala
publishTo := Some(MavenCache("local-maven", file("path/to/maven-repo/releases")))
```

To publish to a local Ivy repository:

```scala
publishTo := Some(Resolver.file("local-ivy", file("path/to/ivy-repo/releases")))
```

If you're using Maven repositories you will also have to select the
right repository depending on your artifacts: SNAPSHOT versions go to
the /snapshot repository while other versions go to the /releases
repository. Doing this selection can be done by using the value of the
`isSnapshot` SettingKey:

```scala
publishTo := {
  val nexus = "https://my.artifact.repo.net/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
```

### Publishing locally

The `publishLocal` task will publish to the "local" Ivy repository.
By default, this is at `\$HOME/.ivy2/local/`. Other builds on the
same machine can then list the project as a dependency. For example, if
the project you are publishing has configuration parameters like:

```scala
ThisBuild / organization := "org.me"
ThisBuild / version      := "0.1-SNAPSHOT"

name := "My Project"
```

Then another build on the same machine can depend on it:

```scala
libraryDependencies += "org.me" %% "my-project" % "0.1-SNAPSHOT"
```

The version number you select must end with `SNAPSHOT`, or you must
change the version number each time you publish to indicate that it's
a changing artifact.

**Note**: SNAPSHOT dependencies should be avoided beyond local testing since
it makes dependency resolution slower and the build non-repeatable.

Similar to `publishLocal`, `publishM2` task will publish the user's Maven local repository.
This is at the location specified by `\$HOME/.m2/settings.xml` or at
`\$HOME/.m2/repository/` by default.
Another build would require `Resolver.mavenLocal` to resolve out of it:

```scala
resolvers += Resolver.mavenLocal
```

See [Resolvers][Resolvers] for more details.

### Credentials

There are two ways to specify credentials for such a repository.

The first and better way is to load them from a file, for example:

```scala
credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
```

The credentials file is a properties file with keys `realm`, `host`,
`user`, and `password`. For example:

```
realm=Sonatype Nexus Repository Manager
host=my.artifact.repo.net
user=admin
password=admin123
```

The second way is to specify them inline:

```scala
credentials += Credentials("Sonatype Nexus Repository Manager", "my.artifact.repo.net", "admin", "admin123")
```

**NOTE**: Credentials matching is done using both: `realm` and `host` keys.
The `realm` key is the HTTP WWW-Authenticate header's realm directive, which is
part of the response of HTTP servers for [HTTP Basic Authentication](https://en.wikipedia.org/wiki/Basic_access_authentication#Server_side).
For a given repository, this can be found by reading all the headers received.
For example:

```bash
curl -D - my.artifact.repo.net
```

### Cross-publishing

To support multiple incompatible Scala versions, enable cross building
and do `+ publish` (see [Cross Build][Cross-Build]). See [Resolvers] for other
supported repository types.

### Overriding the publishing convention

By default sbt will publish your artifact with the binary version of Scala
you're using. For example if your project is using Scala 2.13.x your example
artifact would be published under `example_2.13`. This is often what you want,
but if you're publishing a pure Java artifact or a compiler plugin you'll want
to change the `CrossVersion`. See the [Cross Build][Cross-Build] page for more
details under the _Overriding the publishing convention_ section.

### Published artifacts

By default, the main binary jar, a sources jar, and a API documentation
jar are published. You can declare other types of artifacts to publish
and disable or modify the default artifacts. See the [Artifacts][Artifacts] page
for details.

### Modifying the generated POM

When `publishMavenStyle` is `true`, a POM is generated by the `makePom`
action and published to the repository instead of an Ivy file. This POM
file may be altered by changing a few settings. Set `pomExtra` to
provide XML (`scala.xml.NodeSeq`) to insert directly into the generated
pom. For example:

```scala
pomExtra := <something></something>
```

There is also a `pomPostProcess` setting that can be used to manipulate
the final XML before it is written. It's type is `Node => Node`.

```scala
pomPostProcess := { (node: Node) =>
  ...
}
```

`makePom` adds to the POM any Maven-style repositories you have
declared. You can filter these by modifying `pomRepositoryFilter`, which
by default excludes local repositories. To instead only include local
repositories:

```scala
pomIncludeRepository := { (repo: MavenRepository) =>
  repo.root.startsWith("file:")
}
```

### Version scheme

sbt 1.4.0 adds a new setting called `ThisBuild / versionScheme` to track version scheme of the build:

```
ThisBuild / versionScheme := Some("early-semver")
```

The supported values are `"early-semver"`, `"pvp"`, `"semver-spec"`, and `"strict"`. sbt will include this information into `pom.xml` and `ivy.xml` as a property.

<table>
<tr><th>versionScheme</th><th>description</th></tr>
<tr><td><nobr><code>Some("early-semver")</code></nobr></td><td>Early Semantic Versioning that would keep binary compatibility across patch updates within 0.Y.z (for instance 0.13.0 and 0.13.2). Once it goes 1.0.0, it follows the regular Semantic Versioning where 1.1.0 is bincompat with 1.0.0.</td></tr>
<tr><td><nobr><code>Some("semver-spec")</code></nobr></td><td><a href="https://semver.org/">Semantic Versioning</a> where all 0.y.z are treated as initial development (no bincompat guarantees)</td></tr>
<tr><td><code>Some("pvp")</code></td><td><a href="https://pvp.haskell.org/">Haskell Package Versioning Policy</a> where X.Y are treated as major version</td></tr>
<tr><td><code>Some("strict")</code></td><td>Requires exact match of version</td></tr>
</table>

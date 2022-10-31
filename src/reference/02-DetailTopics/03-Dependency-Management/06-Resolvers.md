---
out: Resolvers.html
---

Resolvers
---------

### Maven resolvers

Resolvers for Maven repositories are added as follows:

```scala
resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
```

This is the most common kind of user-defined resolvers. The rest of this
page describes how to define other types of repositories.

### Local Maven resolvers

Following adds a resolver to the Maven local repository:

```scala
resolvers += Resolver.mavenLocal
```

To add a resolver for a custom location:

```scala
resolvers += MavenCache("local-maven", file("path/to/maven-repo/releases"))
```

### Predefined resolvers

A few predefined repositories are available and are listed below

-   `Resolver.mavenLocal` This is the local Maven repository.
-   `DefaultMavenRepository` This is the main Maven repository at
    <https://repo1.maven.org/maven2/> and is included by default
-   `JavaNet2Repository` This is the java.net Maven2 Repository at
    <https://maven.java.net/content/repositories/public/>
-   `Resolver.sonatypeRepo("public")` (or "snapshots", "staging", "releases") This is Sonatype OSS Maven Repository at
    <https://oss.sonatype.org/content/repositories/public>
-   `Resolver.typesafeRepo("releases")` (or "snapshots") This is Typesafe Repository at
    <https://repo.typesafe.com/typesafe/releases>
-   `Resolver.typesafeIvyRepo("releases")` (or "snapshots") This is Typesafe Ivy Repository at
    <https://repo.typesafe.com/typesafe/ivy-releases>
-   `Resolver.sbtPluginRepo("releases")` (or "snapshots") This is sbt Community Repository at
    <https://repo.scala-sbt.org/scalasbt/sbt-plugin-releases>
-   `Resolver.bintrayRepo("owner", "repo")` This is the Bintray repository at
    <https://dl.bintray.com/[owner]/[repo]/>
-   `Resolver.jcenterRepo` This is the Bintray JCenter repository at
    <https://jcenter.bintray.com/>

For example, to use the `java.net` repository, use the following setting
in your build definition:

```scala
resolvers += JavaNet2Repository
```

Predefined repositories will go under Resolver going forward so they are
in one place:

```scala
Resolver.sonatypeOssRepos("releases")  // Or "snapshots"
```

### Custom resolvers

sbt provides an interface to the repository types available in Ivy:
file, URL, SSH, and SFTP. A key feature of repositories in Ivy is using
[patterns](https://ant.apache.org/ivy/history/latest-milestone/concept.html#patterns)
to configure repositories.

Construct a repository definition using the factory in `sbt.Resolver`
for the desired type. This factory creates a `Repository` object that
can be further configured. The following table contains links to the Ivy
documentation for the repository type and the API documentation for the
factory and repository class. The SSH and SFTP repositories are
configured identically except for the name of the factory. Use
`Resolver.ssh` for SSH and `Resolver.sftp` for SFTP.

<table class="table table-striped">
  <tr>
    <th>Type</th>
    <th>Factory</th>
    <th>Ivy Docs</th>
    <th>Factory API</th>
    <th>Repository Class API</th>
  </tr>

  <tr>
    <td>Filesystem</td>
    <td><tt>Resolver.file</tt></td>
    <td><a href="https://ant.apache.org/ivy/history/latest-milestone/resolver/filesystem.html">Ivy filesystem</a></td>
    <td><a href="../api/sbt/librarymanagement/Resolver\$.html#file">filesystem factory</a></td>
    <td><a href="../api/sbt/librarymanagement/FileRepository.html">FileRepository API</a></td>
  </tr>

  <tr>
    <td>SFTP</td>
    <td><tt>Resolver.sftp</tt></td>
    <td><a href="https://ant.apache.org/ivy/history/latest-milestone/resolver/sftp.html">Ivy sftp</a></td>
    <td><a href="../api/sbt/librarymanagement/Resolver\$.html#sftp">sftp factory</a></td>
    <td><a href="../api/sbt/librarymanagement/SftpRepository.html">SftpRepository API</a></td>
  </tr>

  <tr>
    <td>SSH</td>
    <td><tt>Resolver.ssh</tt></td>
    <td><a href="https://ant.apache.org/ivy/history/latest-milestone/resolver/ssh.html">Ivy ssh</a></td>
    <td><a href="../api/sbt/librarymanagement/Resolver\$.html#ssh">ssh factory</a></td>
    <td><a href="../api/sbt/librarymanagement/SshRepository.html">SshRepository API</a></td>
  </tr>

  <tr>
    <td>URL</td>
    <td><tt>Resolver.url</tt></td>
    <td><a href="https://ant.apache.org/ivy/history/latest-milestone/resolver/url.html">Ivy url</a></td>
    <td><a href="../api/sbt/librarymanagement/Resolver\$.html#url">url factory</a></td>
    <td><a href="../api/sbt/librarymanagement/URLRepository.html">URLRepository API</a></td>
  </tr>
</table>

#### Basic Examples

These are basic examples that use the default Maven-style repository
layout.

##### Filesystem

Define a filesystem repository in the `test` directory of the current
working directory and declare that publishing to this repository must be
atomic.

```scala
resolvers += Resolver.file("my-test-repo", file("test")) transactional()
```

##### URL

Define a URL repository at `"https://example.org/repo-releases/"`.

```scala
resolvers += Resolver.url("my-test-repo", url("https://example.org/repo-releases/"))
```

To specify an Ivy repository, use:

```scala
resolvers += Resolver.url("my-test-repo", url)(Resolver.ivyStylePatterns)
```

or customize the layout pattern described in the Custom Layout section
below.

##### SFTP and SSH Repositories

The following defines a repository that is served by SFTP from host
`"example.org"`:

```scala
resolvers += Resolver.sftp("my-sftp-repo", "example.org")
```

To explicitly specify the port:

```scala
resolvers += Resolver.sftp("my-sftp-repo", "example.org", 22)
```

To specify a base path:

```scala
resolvers += Resolver.sftp("my-sftp-repo", "example.org", "maven2/repo-releases/")
```

Authentication for the repositories returned by `sftp` and `ssh` can be
configured by the `as` methods.

To use password authentication:

```scala
resolvers += Resolver.ssh("my-ssh-repo", "example.org") as("user", "password")
```

or to be prompted for the password:

```scala
resolvers += Resolver.ssh("my-ssh-repo", "example.org") as("user")
```

To use key authentication:

```scala
resolvers += {
  val keyFile: File = ...
  Resolver.ssh("my-ssh-repo", "example.org") as("user", keyFile, "keyFilePassword")
}
```

or if no keyfile password is required or if you want to be prompted for
it:

```scala
resolvers += Resolver.ssh("my-ssh-repo", "example.org") as("user", keyFile)
```

To specify the permissions used when publishing to the server:

```scala
resolvers += Resolver.ssh("my-ssh-repo", "example.org") withPermissions("0644")
```

This is a chmod-like mode specification.

#### Custom Layout

These examples specify custom repository layouts using patterns. The
factory methods accept an `Patterns` instance that defines the patterns
to use. The patterns are first resolved against the base file or URL.
The default patterns give the default Maven-style layout. Provide a
different Patterns object to use a different layout. For example:

```scala
resolvers += Resolver.url("my-test-repo", url)( Patterns("[organisation]/[module]/[revision]/[artifact].[ext]") )
```

You can specify multiple patterns or patterns for the metadata and
artifacts separately. You can also specify whether the repository should
be Maven compatible (as defined by Ivy). See the
[patterns API](../api/sbt/librarymanagement/Patterns\$.html) for the methods to use.

For filesystem and URL repositories, you can specify absolute patterns
by omitting the base URL, passing an empty `Patterns` instance, and
using `ivys` and `artifacts`:

```scala
resolvers += Resolver.url("my-test-repo") artifacts
        "https://example.org/[organisation]/[module]/[revision]/[artifact].[ext]"
```

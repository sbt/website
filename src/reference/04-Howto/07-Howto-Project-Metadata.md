---
out: Howto-Project-Metadata.html
---

Project metadata
----------------

<a name="name"></a>

### Set the project name

A project should define `name` and `version`. These will be used in
various parts of the build, such as the names of generated artifacts.
Projects that are published to a repository should also override
`organization`.

```scala
name := "Your project name"
```

For published projects, this name is normalized to be suitable for use
as an artifact name and dependency ID. This normalized name is stored in
`normalizedName`.

<a name="version"></a>

### Set the project version

```scala
version := "1.0"
```

<a name="organization"></a>

### Set the project organization

```scala
organization := "org.example"
```

By convention, this is a reverse domain name that you own, typically one
specific to your project. It is used as a namespace for projects.

A full/formal name can be defined in the `organizationName` setting.
This is used in the generated pom.xml. If the organization has a web
site, it may be set in the `organizationHomepage` setting. For example:

```scala
organizationName := "Example, Inc."

organizationHomepage := Some(url("http://example.org"))
```

<a name="other"></a>

### Set the project's homepage and other metadata

```scala
homepage := Some(url("https://www.scala-sbt.org"))

startYear := Some(2008)

description := "A build tool for Scala."

licenses += "GPLv2" -> url("https://www.gnu.org/licenses/gpl-2.0.html")
```

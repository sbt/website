---
out: Using-Sonatype.html
---

  [sonatype-central-portal-register]: https://central.sonatype.org/register/central-portal/
  [sonatype-namespace]: https://central.sonatype.org/register/namespace/
  [sonatype-new-project]: https://issues.sonatype.org/secure/CreateIssue.jspa?issuetype=21&pid=10134
  [sonatype-requirements]: https://central.sonatype.org/publish/requirements/
  [sonatype-coordinates]: https://central.sonatype.org/publish/requirements/coordinates/
  [sonatype-nexus]: https://oss.sonatype.org/#welcome
  [sonatype-pgp]: https://central.sonatype.org/publish/requirements/gpg/
  [publish-portal-snapshots]: https://central.sonatype.org/publish/publish-portal-snapshots/
  [sbt-pgp]: https://github.com/sbt/sbt-pgp#sbt-pgp
  [sbt-sonatype]: https://github.com/xerial/sbt-sonatype
  [sbt-release]: https://github.com/sbt/sbt-release
  [gnupg]: https://www.gnupg.org/
  [sbt-ci-release]: https://github.com/sbt/sbt-ci-release
  [central-portal]: https://central.sonatype.com/
  [20250326_ossrh_sunset]: https://central.sonatype.org/news/20250326_ossrh_sunset/

Using Sonatype
--------------

Publishing to the Central Repository is easy!

#### Central Portal and Legacy OSSRH

The Central Repository (aka Maven Central) has long been the pillar of the JVM ecosystem,
including Scala. The mechanism to publish libraries to the Central has been hosted by Sonatype
as OSS Repository Hosting (OSSRH) via `HTTP PUT`; however, in March 2025 it was
[announced][20250326_ossrh_sunset] that the endpoint will be sunset on June 30th, 2025
in favor of the Central Portal at <https://central.sonatype.com/>.

<table style="border: 1px solid gray; width: 80%;">
<tr><th>&nbsp;</th><th>Central Portal</th> <th>Legacy OSSRH</th></tr>
<tr><td>sbt version</td> <td>Use sbt <b>1.11.0-RC2</b>+</td> <td>Any sbt 1.x version</td></tr>
<tr><td>Availability</td> <td>Available</td> <td>Sunset on 2025-06-30</td></tr>
</table>
<br>

Publishing to the Central Portal is built into sbt, for sbt 1.11.0-RC2 and above.
The rest of this page will document the publishing process for Central Portal,
but there are some notes at the end for the Legacy OSSRH publishing.

#### Central Portal registration

The reference process for configuring and publishing to the Central Repository is
described in Sonatype's [Publish guides][sonatype-central-portal-register].

The [Publish guides][sonatype-central-portal-register] walk you through the required
process of setting up the account with the Central Portal.
When creating a personal account, try to authenticate via GitHub,
which will automatically associate `io.github.<user_name>`.

Otherwise, follow the steps described in [register a namespace][sonatype-namespace] guide
to associate a domain name with your account.

**Note**: To convert an existing account to the Central Portal,
go to <https://central.sonatype.com/>, nagivate to **Sign In**, then use the existing
Sonatype OSSRH user name and password to try to log in. If it doesn't work, use **Forgot password**
link to reset the password instead of creating a fresh account.
This should let you log into the Central Portal while still keeping your namespaces still associated
with the Legacy OSSRH publishing until you migrate them.

### sbt setup

To address Sonatype's [requirements]
[sonatype-requirements] for publishing to the central repository and to simplify the publishing process, you can
use two community plugins. The [sbt-pgp plugin][sbt-pgp] can sign the files with GPG/PGP.
(Optionally [sbt-ci-release][sbt-ci-release] can automate the publishing process.)

#### step 1: PGP Signatures

Follow the Sonatype's [GPG guide][sonatype-pgp].

First, you should [install GnuPG](https://www.gnupg.org/download/), and verify the version:

```
\$ gpg --version
gpg (GnuPG/MacGPG2) 2.2.8
libgcrypt 1.8.3
Copyright (C) 2018 Free Software Foundation, Inc.
License GPLv3+: GNU GPL version 3 or later <https://gnu.org/licenses/gpl.html>
```

Next generate a key:

```
\$ gpg --gen-key
```

List the keys:

```
\$ gpg --list-keys

/home/foo/.gnupg/pubring.gpg
------------------------------

pub   rsa4096 2018-08-22 [SC]
      1234517530FB96F147C6A146A326F592D39AAAAA
uid           [ultimate] your name <you@example.com>
sub   rsa4096 2018-08-22 [E]
```

Distribute the key:

```
\$ gpg --keyserver keyserver.ubuntu.com --send-keys 1234517530FB96F147C6A146A326F592D39AAAAA
```

#### step 2: sbt-pgp

With the PGP key you want to use, you can sign the artifacts
you want to publish to the Sonatype repository with the [sbt-pgp plugin][sbt-pgp]. Follow
the instructions for the plugin and you'll have PGP signed artifacts in no
time.

In short, add the following line to your `project/plugins.sbt` file to
enable it for your build:

```
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")
```

> *Note:* The plugin is a solution to sign artifacts. It works with the GPG command line tool.

Make sure that the `gpg` command is in PATH available to the sbt.

#### step 3: Credentials

Generate a user token from the portal to be used for the credentials.
The token must be stored somewhere safe (*e.g. NOT in the repository*).
A common convention is a `$global_base$/credentials.sbt` file, with the following:

```scala
credentials += Credentials(Path.userHome / ".sbt" / "sonatype_central_credentials")
```

Next create a file `~/.sbt/sonatype_central_credentials`:

```
host=central.sonatype.com
user=<your username>
password=<your password>
```

#### step 4: Configure build.sbt

To publish to a Maven repository, you'll need to configure a few
settings so that the correct metadata is generated.

**Note**: To publish to the Central Portal, `ThisBuild / publishTo`
must be set to the `localStaging` repository:

```scala
// new setting for the Central Portal
ThisBuild / publishTo := {
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if (isSnapshot.value) Some("central-snapshots" at centralSnapshots)
  else localStaging.value
}
```

Add these settings at the end of `build.sbt` or a separate `publish.sbt`:

```scala
ThisBuild / organization := "com.example.project2"
ThisBuild / organizationName := "example"
ThisBuild / organizationHomepage := Some(url("http://example.com/"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/your-account/your-project"),
    "scm:git@github.com:your-account/your-project.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id = "Your identifier",
    name = "Your Name",
    email = "your@email",
    url = url("http://your.url")
  )
)

ThisBuild / description := "Some description about your project."
ThisBuild / licenses := List(
  "Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")
)
ThisBuild / homepage := Some(url("https://github.com/example/project"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle := true

// new setting for the Central Portal
ThisBuild / publishTo := {
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if (isSnapshot.value) Some("central-snapshots" at centralSnapshots)
  else localStaging.value
}
```

The full format of a `pom.xml` (an end product of the project configuration
used by Maven) file is [outlined here](https://maven.apache.org/pom.html).
You can add more data to it with the `pomExtra` option in `build.sbt`.

#### step 5: Stage the artifacts

From sbt shell run:

```
> publishSigned
```

#### step 6: Upload or release the bundle

From sbt shell run:

```
> sonaUpload
```

This will upload the bundle to the [Central Portal][central-portal].
Hit the "Publish" button to publish to the Central Repository.

If you want to automate the publishing, run:

```
> sonaRelease
```

It might take 10 minutes to a few hours for the published artifacts to be
visible on the Central Repository <https://repo1.maven.org/maven2/>.

### Optional steps

#### Publishing SNAPSHOTs

In general, the use of SNAPSHOT artifacts should be limited to short-term testing,
and we do not recommend publishing SNAPSHOTs publicly.
However, should you decide to publish SNAPSHOTs, you can enable it from the Central Portal per namespace.
See Sonatype's [Publishing -SNAPSHOT Releases][publish-portal-snapshots] guide for details.

#### Tag-based publishing via sbt-ci-release

You can further optimize the publishing flow by using the [sbt-ci-release][sbt-ci-release] plugin.

Once you set it up, all you have to do is push a git tag to trigger a release.

#### Integrate with the release process

To automate the publishing approach above with the [sbt-release plugin]
[sbt-release], you should simply add the publishing commands as steps in the
`releaseProcess` task:

```
...
releaseStepCommand("sonatypeOpen \"your groupId\" \"Some staging name\""),
...
releaseStepCommand("publishSigned"),
...
releaseStepCommand("sonaRelease"),
...
```

<a id="ossrh"></a>
### Publishing to the Legacy OSSRH

#### Credentials for the Legacy OSSRH

The credentials for your OSSRH account need to be stored
somewhere safe (*e.g. NOT in the repository*). Common convention is a
`$global_base$/sonatype.sbt` file, with the following:

```scala
credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credentials")
```

Next create a file `~/.sbt/sonatype_credentials`:

```
realm=Sonatype Nexus Repository Manager
host=oss.sonatype.org
user=<your username>
password=<your password>
```

> *Note:* The first two strings must be `"Sonatype Nexus Repository Manager"`
> and `"oss.sonatype.org"` for Coursier to use the credentials. If you are using
> an OSSRH account created between February 2021 and May 2025, use `"s01.oss.sonatype.org"`
> instead of `"oss.sonatype.org"`

#### sbt setup for the Legacy OSSRH

```scala
ThisBuild / publishTo := {
  // For accounts created after Feb 2021:
  // val nexus = "https://s01.oss.sonatype.org/"
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
```

#### The Legacy OSSRH publishing step

From sbt shell run:

```
> publishSigned
```

Check the published artifacts in the [Nexus Repository Manager][sonatype-nexus]
(same login as Sonatype's Jira account).
Close the staging repository and promote the release to central, by hitting
"Close" button, then "Release" button.

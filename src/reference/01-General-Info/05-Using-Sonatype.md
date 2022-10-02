---
out: Using-Sonatype.html
---

  [sonatype-ossrhguide]: https://central.sonatype.org/publish/publish-guide/
  [sonatype-signup]: https://issues.sonatype.org/secure/Signup!default.jspa
  [sonatype-new-project]: https://issues.sonatype.org/secure/CreateIssue.jspa?issuetype=21&pid=10134
  [sonatype-requirements]: https://central.sonatype.org/publish/requirements/
  [sonatype-coordinates]: https://central.sonatype.org/publish/requirements/coordinates/
  [sonatype-nexus]: https://oss.sonatype.org/#welcome
  [sonatype-pgp]: https://central.sonatype.org/pages/working-with-pgp-signatures.html
  [sbt-pgp]: https://github.com/sbt/sbt-pgp#sbt-pgp
  [sbt-sonatype]: https://github.com/xerial/sbt-sonatype
  [sbt-release]: https://github.com/sbt/sbt-release
  [gnupg]: https://www.gnupg.org/

Using Sonatype
--------------

Deploying to sonatype is easy! Just follow these simple steps:

### Sonatype setup

The reference process for configuring and publishing to Sonatype is
described in their [OSSRH Guide][sonatype-ossrhguide].
In short, you need two publicly available URLs:

* the website of the project e.g. https://github.com/sonatype/nexus-public
* the project's source code e.g. https://github.com/sonatype/nexus-public.git

The [OSSRH Guide][sonatype-ossrhguide] walks you through the required
process of setting up the account with Sonatype. It’s as simple as
[creating a Sonatype's JIRA account][sonatype-signup] and then a
[New Project ticket][sonatype-new-project]. When creating the account, try to
use the same domain in your email address that the project is hosted on.
It makes it easier for Sonatype to validate the relationship with the groupId requested in
the ticket, but it is not the only method used to confirm the ownership.

Creation of the *New Project ticket* is as simple as:

* providing the name of the library in the ticket’s subject,
* naming the groupId for distributing the library (make sure
  it matches the root package of your code). Sonatype provides
  additional hints on choosing the right groupId for publishing your library in
  [Choosing your coordinates guide][sonatype-coordinates].
* providing the SCM and Project URLs to the source code and homepage of the
  library.

After creating your Sonatype account on JIRA, you can log in
to the [Nexus Repository Manager][sonatype-nexus] using the same credentials,
although this is not required in the guide, it can be helpful later to check
on published artifacts.

> *Note:* Sonatype advises that responding to a **New Project ticket** might
> take up to two business days, but in my case it was a few minutes.

### sbt setup

To address Sonatype's [requirements]
[sonatype-requirements] for publishing to the central repository and to simplify the publishing process, you can
use two community plugins. The [sbt-pgp plugin][sbt-pgp] can sign the files with GPG/PGP.
(Optionally [sbt-sonatype][sbt-sonatype] can publish to a Sonatype repository nicer.)

#### step 1: PGP Signatures

Follow [Working with PGP Signatures][sonatype-pgp].

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

In short, add the following line to your `~/.sbt/1.0/plugins/gpg.sbt` file to
enable it globally for SBT projects:

```
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.1.2")
```

> *Note:* The plugin is a solution to sign artifacts. It works with the GPG command line tool.

Make sure that the `gpg` command is in PATH available to the sbt.

#### step 3: Credentials

The credentials for your Sonatype OSSRH account need to be stored
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
> a new OSSRH account created after February 2021, use `"s01.oss.sonatype.org"`
> instead of `"oss.sonatype.org"`

#### step 4: Configure build.sbt

To publish to a maven repository, you'll need to configure a few
settings so that the correct metadata is generated.

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
ThisBuild / publishTo := {
  // For accounts created after Feb 2021:
  // val nexus = "https://s01.oss.sonatype.org/"
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true
```

The full format of a `pom.xml` (an end product of the project configuration
used by Maven) file is [outlined here](https://maven.apache.org/pom.html).
You can add more data to it with the `pomExtra` option in `build.sbt`.

#### step 5: Publishing

From sbt shell run:

```
> publishSigned
```

Check the published artifacts in the [Nexus Repository Manager][sonatype-nexus]
(same login as Sonatype's Jira account).

Close the staging repository and promote the release to central, by hitting
"Close" button, then "Release" button.

### Optional steps

#### sbt-sonatype

> *Note:* sbt-sonatype is a third-party plugin meaning it is not covered by Lightbend subscription.

To simplify the usage of the Sonatype's Nexus, add the following line to
`project/plugins.sbt` to import the [sbt-sonatype plugin][sbt-sonatype] to your project:

```
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.13")
```

This plugin will facilitate the publishing process, but in short, these are
the main steps for publishing the libraries to the repository:

1. Create a new staging repository:
   `sonatypeOpen "your groupId" "Some staging name"`
2. Sign and publish the library to the staging repository:
   `publishSigned`
3. You can and should check the published artifacts in the
   [Nexus Repository Manager][sonatype-nexus] (same login as Sonatype's
   Jira account)
4. Close the staging repository and promote the release to central:
   `sonatypeRelease`

Below are some important keys to take note of when using this plugin. [Read here][sbt-sonatype]
for more information.

```scala
// This becomes a simplified version of the above key.
publishTo := sonatypePublishToBundle.value
// Set this to the same value set as your credential files host.
sonatypeCredentialHost := "oss.sonatype.org"
// Set this to the repository to publish to using `s01.oss.sonatype.org`
// for accounts created after Feb. 2021.
sonatypeRepository := "https://oss.sonatype.org/service/local"
```

After publishing you have to follow the
[release workflow of Nexus](https://central.sonatype.org/publish/release/).

> *Note:* the sbt-sonatype plugin can also be used to publish to other non-sonatype
> repositories

#### Publishing tips

Use staged releases to test across large projects of independent releases
before pushing the full project.

> *Note:* An error message of `PGPException: checksum mismatch at 0 of 20`
> indicates that you got the passphrase wrong. We have found at least on
> OS X that there may be issues with characters outside the 7-bit ASCII
> range (e.g. Umlauts). If you are absolutely sure that you typed the
> right phrase and the error doesn't disappear, try changing the
> passphrase.

> *Note:* If you are using a new OSSRH account created after February 2021,
> use `"s01.oss.sonatype.org"` instead of `"oss.sonatype.org"`

#### Integrate with the release process

> *Note:* sbt-release is a third-party plugin meaning it is not covered by Lightbend subscription.

To automate the publishing approach above with the [sbt-release plugin]
[sbt-release], you should simply add the publishing commands as steps in the
`releaseProcess` task:

```
...
releaseStepCommand("sonatypeOpen \"your groupId\" \"Some staging name\""),
...
releaseStepCommand("publishSigned"),
...
releaseStepCommand("sonatypeRelease"),
...
```

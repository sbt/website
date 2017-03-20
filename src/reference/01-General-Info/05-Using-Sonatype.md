---
out: Using-Sonatype.html
---

  [sonatype-ossrhguide]: http://central.sonatype.org/pages/ossrh-guide.html
  [sonatype-signup]: https://issues.sonatype.org/secure/Signup!default.jspa
  [sonatype-new-project]: https://issues.sonatype.org/secure/CreateIssue.jspa?issuetype=21&pid=10134
  [sonatype-requirements]: http://central.sonatype.org/pages/requirements.html
  [sonatype-coordinates]: http://central.sonatype.org/pages/choosing-your-coordinates.html
  [sonatype-nexus]: https://oss.sonatype.org/#welcome
  [sonatype-pgp]: http://central.sonatype.org/pages/working-with-pgp-signatures.html
  [sbt-pgp]: http://scala-sbt.org/sbt-pgp/
  [sbt-sonatype]: https://github.com/xerial/sbt-sonatype
  [sbt-release]: https://github.com/sbt/sbt-release
  [gnupg]: https://www.gnupg.org/

Using Sonatype
---------------------
Deploying to sonatype is easy! Just follow these simple steps:

## Sonatype setup
> *Note:* The reference process for configuring and publishing to Sonatype is 
described in their [OSSRH Guide][sonatype-ossrhguide].
 
In short, you need two publicly available URLs: 
* the website of the project e.g. https://github.com/sonatype/nexus-oss
* the project's source code e.g. https://github.com/sonatype/nexus-oss.git

The [OSSRH Guide][sonatype-ossrhguide] walks you through the required 
process of setting up the account with Sonatype. It’s as simple as 
[creating a Sonatype's JIRA account][sonatype-signup] and afterwards creating a 
[New Project ticket][sonatype-new-project]. When creating the account try to 
use the same domain in your email address as the project is hosted on. I guess 
it makes it easier to validate the relationship with the groupId requested in 
the ticket, but is not the only method used to confirm the ownership. 

Creation of the New Project ticket is as simple as:
* providing the name of the library in the ticket’s subject,
* naming the groupId you want to use for distributing the library (make sure 
it is matching the root package of your code). Sonatype provides you with 
additional hints on choosing the right groupId for publishing your library in 
[Choosing your coordinates guide][sonatype-coordinates].
* providing the SCM and Project URLs to the source code and homepage of the 
library.

> *Note:* After creating you Sonatype account (on their JIRA) you can login 
using the same credentials to the [Nexus Repository Manager][sonatype-nexus] 
which is not required to be used in this guide, but can be used later to check 
on the published artifacts.

Notice that Sonatype advises that responding to the New Project ticket might 
take up to two business days, but in my case it was few minutes.

## SBT setup

To address [Sonatype's requirements for publishing to the central repository]
[sonatype-requirements] and to simplify the publishing process I recommend you
to use community plugins [sbt-pgp for signing the files with GPG/PGP][sbt-pgp] 
and [sbt-sonatype for publishing to Sonatype repository][sbt-sonatype]. 

### First - PGP Signatures

Having the PGP key that you want to use, you need to sign the artifacts 
published to the Sonatype repository with the [sbt-pgp plugin][sbt-pgp]. Follow 
the instructions for the plugin and you'll have PGP signed artifacts in no 
time.

In short, add the following line to your ~/.sbt/0.13/plugins/gpg.sbt file to 
enable it globally for SBT projects:
```
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")
```

> *Note:* The plugin is a jvm-only solution to generate PGP keys and sign 
artifacts. It can also work with the GPG command line tool.

If you don't have the PGP keys to sign your code with, one of the ways to 
achieve that is to install the [GNU Privacy Guard][gnupg] and:
* use it to generate the keypair you will use to sign your library,
* publish your certificate to enable remote verification of the signatures,
* make sure that the `gpg` command is in PATH available to the sbt,
* add `useGpg := true` to your `build.sbt` to make the plugin `gpg`-aware 

#### PGP Tips'n'tricks 

If the command to generate your key fails, execute the following commands and 
remove the displayed files:

```
> show */*:pgpSecretRing
[info] /home/username/.sbt/.gnupg/secring.gpg
> show */*:pgpPublicRing
[info] /home/username/.sbt/.gnupg/pubring.gpg
```

If your PGP key has not yet been distributed to the keyserver pool, e.g., 
you've just generated it, you'll need to publish it. You can do so using the 
[sbt-pgp][sbt-pgp] plugin:

```
pgp-cmd send-key keyname hkp://pool.sks-keyservers.net
```

(where keyname is the name or email address used when creating the key or 
hexadecimal identifier for the key.)

If you see no output from sbt-pgp then the key name specified was not
found.

If it fails to run the `SendKey` command you can try another server (for 
example: hkp://keyserver.ubuntu.com). A list of servers can be found at 
[the status page](https://sks-keyservers.net/status/) of sks-keyservers.net.

### Second - configure sonatype integration 

The credentials for your Sonatype OSSRH account need to be stored
somewhere safe (*e.g. NOT in the repository*). Common convention is a 
`$global_base$/sonatype.sbt` file (e.g. `) with the following:

```scala
credentials += Credentials("Sonatype Nexus Repository Manager",
                           "oss.sonatype.org",
                           "<your username>",
                           "<your password>")
```

> *Note:* The first two strings must be `"Sonatype Nexus Repository Manager"`
and `"oss.sonatype.org"` for Ivy to use the credentials.

Now, we want to control what's available in the `pom.xml` file. This
file describes our project in the maven repository and is used by
indexing services for search and discover. This means it's important
that `pom.xml` should have all information we wish to advertise as well
as required info!

First, let's make sure no repositories show up in the POM file. To
publish on maven-central, all *required* artifacts must also be hosted
on maven central. However, sometimes we have optional dependencies for
special features. If that's the case, let's remove the repositories for
optional dependencies in our artifact:

```scala
pomIncludeRepository := { _ => false }
```

To publish to a maven repository, you'll need to configure a few
settings so that the correct metadata is generated.
Specifically, the build should provide data for `organization`, `url`,
`license`, `scm.url`, `scm.connection` and `developer` keys. To generate a 
correct `pom.xml` you can use sbt's build keys or manually through the 
`pomExtra` configuration option in `build.sbt`:
```
pomExtra := (
  <url>http://your.project.url</url>
  <licenses>
    <license>
      <name>BSD-style</name>
      <url>http://www.opensource.org/licenses/bsd-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:your-account/your-project.git</url>
    <connection>scm:git:git@github.com:your-account/your-project.git</connection>
  </scm>
  <developers>
    <developer>
      <id>Your identifier</id>
      <name>Your Name</name>
      <url>http://your.url</url>
    </developer>
  </developers>
)
```

The full format of a pom.xml file is 
[outlined here](https://maven.apache.org/pom.html).

#### Maven configuration tips'n'tricks

Sbt will automatically inject `licenses` and `url` nodes if
they are already present in your build file. Thus an alternative to parts of
the above `pomExtra` is to include the following entries:

```scala
licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php"))

homepage := Some(url("http://jsuereth.com/scala-arm"))
```

This might be advantageous if those keys are used also by other plugins
(e.g. `ls`). You **cannot use both** the sbt `licenses` key and the
`licenses` section in `pomExtra` at the same time, as this will produce
duplicate entries in the final POM file, leading to a rejection in
Sonatype's staging process.


To ensure the POMs are generated and pushed:
```scala
publishMavenStyle := true
```

Setting repositories to publish to:
```scala
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
```

Not publishing the test artifacts (this is the default):
```scala
publishArtifact in Test := false
```

### Third - Publish to the staging repository

> *Note:* sbt-sonatype is a third-party plugin meaning it is not covered by Lightbend subscription.

To simplify the usage of the Sonatype's Nexus, add the following line to 
`build.sbt` to import the [sbt-sonatype plugin][sbt-sonatype] to your project:
```
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")
```

This plugin will facilitate the publishing process, but in short, these are 
the main steps for publishing the libraries to the repository:
1. Create a new staging repository: 
`sonatypeOpen "your groupId" "Some staging name"`
2. Sign and publish the library to the staging repository:
`publishSigned`
> *Note:* You can and should check the published artifacts in the [Nexus 
Repository Manager][sonatype-nexus] (same login as Sonatype's Jira account)
3. Close the staging repository and promote the release to central:
`sonatypeRelease`

After publishing you have to follow the
[release workflow of Nexus](http://central.sonatype.org/pages/releasing-the-deployment.html).

> *Note:* the sbt-sonatype plugin can be used also for other non-sonatype 
repositories

#### Publishing tips'n'tricks

Use staged releases for testing across large projects of independent releases 
before pushing the full project.

> *Note:* An error message of `PGPException: checksum mismatch at 0 of 20`
indicates that you got the passphrase wrong. We have found at least on
OS X that there may be issues with characters outside the 7-bit ASCII
range (e.g. Umlauts). If you are absolutely sure that you typed the
right phrase and the error doesn't disappear, try changing the
passphrase.

### Fourth - Integrate with the release process

> *Note:* sbt-release is a third-party plugin meaning it is not covered by Lightbend subscription.

To automate the above publishing approach with the [sbt-release plugin]
[sbt-release], you should simply add the publishing commands as steps in the
`releaseProcess` option:
```
...
ReleaseStep(action = Command.process("sonatypeOpen \"your groupId\" \"Some staging name\"", _)),
...
ReleaseStep(action = Command.process("publishSigned", _)),
...
ReleaseStep(action = Command.process("sonatypeRelease", _)),
...
```

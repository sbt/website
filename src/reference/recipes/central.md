
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

Publishing to the Central Repo
==============================

~~~admonish note
The recipe section of the documentation focuses on the objectives
with minimal explanations.

See also Sonatype's [Publish guides][sonatype-central-portal-register] for general concepts around publishing to the Central Portal.
~~~

Objective
---------

I want to publish my project to the Central Repository.


Steps
-----

### Preliminary 1: Central Portal registration

Create a Central Portal account, following Sonatype's [Publish guides][sonatype-central-portal-register].

- If you had an OSSRH account, use **Forgot password** flow to convert the account to the new Central Portal, which lets you keep the previous namespace associations.
- If you authenticate via GitHub, `io.github.<user_name>` will automatically be associated with the account.

Follow the steps described in [register a namespace][sonatype-namespace] guide
to associate a domain name with your account.

#### Preliminary 2: PGP key pair

Follow the Sonatype's [GPG guide][sonatype-pgp] to generate a PGP key pair.

[Install GnuPG](https://www.gnupg.org/download/), and verify the version:

```bash
$ gpg --version
gpg (GnuPG/MacGPG2) 2.2.8
libgcrypt 1.8.3
Copyright (C) 2018 Free Software Foundation, Inc.
License GPLv3+: GNU GPL version 3 or later <https://gnu.org/licenses/gpl.html>
```

Next generate a key:

```
$ gpg --gen-key
```

List the keys:

```
$ gpg --list-keys

/home/foo/.gnupg/pubring.gpg
------------------------------

pub   rsa4096 2018-08-22 [SC]
      1234517530FB96F147C6A146A326F592D39AAAAA
uid           [ultimate] your name <you@example.com>
sub   rsa4096 2018-08-22 [E]
```

Distribute the key:

```
$ gpg --keyserver keyserver.ubuntu.com --send-keys 1234517530FB96F147C6A146A326F592D39AAAAA
```

### Step 1: sbt-pgp

The [sbt-pgp plugin][sbt-pgp] can sign the published artifacts with GPG/PGP.
(Optionally [sbt-ci-release][sbt-ci-release] can automate the publishing process.)

Add the following line to your `project/plugins.sbt` file to
enable it for your build:

```scala
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")
```

~~~admonish note
Make sure that the `gpg` command is in PATH available to the sbt.
~~~

### Step 2: Credentials

Generate a user token from the portal to be used for the credentials.
The token must be stored somewhere safe (NOT in the repository).

sbt 2.x can also reads from the environment variables `SONATYPE_USERNAME` and `SONATYPE_PASSWORD` and appends a credential for `central.sonatype.com` out-of-box, which might be useful for automatic publishing from the CI environment, such as GitHub Actions.

```yaml
- run: sbt ci-release
  env:
    PGP_PASSPHRASE: ${{curly_open}} secrets.PGP_PASSPHRASE {{curly_close}}
    PGP_SECRET: ${{curly_open}} secrets.PGP_SECRET {{curly_close}}
    SONATYPE_PASSWORD: ${{curly_open}} secrets.SONATYPE_PASSWORD {{curly_close}}
    SONATYPE_USERNAME: ${{curly_open}} secrets.SONATYPE_USERNAME {{curly_close}}
```

On a local machine, a common convention is a `{{global_base}}/credentials.sbt` file, with the following:

```scala
credentials += Credentials(Path.userHome / ".sbt" / "sonatype_central_credentials")
```

Next create a file `$HOME/.sbt/sonatype_central_credentials`:

```property
host=central.sonatype.com
user=<your username>
password=<your password>
```

### Step 3: Configure build.sbt

To publish to a Maven repository, you'll need to configure a few
settings so that the correct metadata is generated.

**Note**: To publish to the Central Portal, `publishTo`
must be set to the `localStaging` repository:

```scala
// new setting for the Central Portal
publishTo := {
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if version.value.endsWith("-SNAPSHOT") then Some("central-snapshots" at centralSnapshots)
  else localStaging.value
}
```

Add these settings at the end of `build.sbt` or a separate `publish.sbt`:

```scala
organization := "com.example.project2"
organizationName := "example"
organizationHomepage := Some(url("http://example.com/"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/your-account/your-project"),
    "scm:git@github.com:your-account/your-project.git"
  )
)
developers := List(
  Developer(
    id = "Your identifier",
    name = "Your Name",
    email = "your@email",
    url = url("http://your.url")
  )
)

description := "Some description about your project."
licenses := List(License.Apache2)
homepage := Some(url("https://github.com/example/project"))

// Remove all additional repository other than Maven Central from POM
pomIncludeRepository := { _ => false }
publishMavenStyle := true

// new setting for the Central Portal
publishTo := {
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if version.value.endsWith("-SNAPSHOT") then Some("central-snapshots" at centralSnapshots)
  else localStaging.value
}
```

The full format of a `pom.xml` (an end product of the project configuration
used by Maven) file is outlined in [POM Reference](https://maven.apache.org/pom.html).
You can add more data to it with the `pomExtra` option in `build.sbt`.

### Step 4: Stage the artifacts

From sbt shell run:

```bash
> publishSigned
```

### Step 5: Upload or release the bundle

From sbt shell run:

```bash
> sonaUpload
```

This will upload the bundle to the [Central Portal][central-portal].
Hit the "Publish" button to publish to the Central Repository.

If you want to automate the publishing, run:

```bash
> sonaRelease
```

It might take 10 minutes to a few hours for the published artifacts to be
visible on the Central Repository <https://repo1.maven.org/maven2/>.

<!--
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
-->

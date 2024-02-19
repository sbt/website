Setup Notes
===========

See [Installing sbt runner](Setup.md) for the instruction on general setup. Using Coursier or SDKMAN has two advantages.

1. They will install the official packaging by Eclipse Adoptium etc, as opposed to the ["mystery meat OpenJDK builds"](https://mail.openjdk.java.net/pipermail/jdk8u-dev/2019-May/009330.html).
2. They will install `tgz` packaging of sbt that contains all JAR files. (DEB and RPM packages do not to save bandwidth)

This page describes alternative ways of installing the sbt runner. Note that some of the third-party packages may not provide the latest version.

OS specific setup
-----------------

### macOS

#### Homebrew

```bash
$ brew install sbt
```

```admonish warning
Homebrew maintainers have added a dependency to JDK 13 because they want to use more brew dependencies ([brew#50649](https://github.com/Homebrew/homebrew-core/issues/50649)). This causes sbt to use JDK 13 even when `java` available on PATH is JDK 8 or 11. To prevent `sbt` from running on JDK 13, install [jEnv](https://www.jenv.be/) or switch to using [SDKMAN](https://sdkman.io/).
```

### Windows

- [sbt-{{sbtRunnerVersion}}.msi](https://github.com/sbt/sbt/releases/download/v{{sbtRunnerVersion}}/sbt-{{sbtRunnerVersion}}.msi)

#### [Chocolatey](https://chocolatey.org/packages/sbt)

```
> choco install sbt
```

#### [Scoop](https://scoop.sh/)

```
> scoop install sbt
```

### Linux

#### Ubuntu and other Debian-based distributions

[DEB][DEB] package is officially supported by sbt, but it does not contain JAR files to save bandwidth.

Ubuntu and other Debian-based distributions use the DEB format, but usually you don't install your software from a local DEB file. Instead they come with package managers both for the command line (e.g. `apt-get`, `aptitude`) or with a graphical user interface (e.g. Synaptic).
Run the following from the terminal to install `sbt` (You'll need superuser privileges to do so, hence the `sudo`).

```bash
sudo apt-get update
sudo apt-get install apt-transport-https curl gnupg -yqq
echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | sudo tee /etc/apt/sources.list.d/sbt_old.list
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo -H gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import
sudo chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg
sudo apt-get update
sudo apt-get install sbt
```

Package managers will check a number of configured repositories for packages to offer for installation. You just have to add the repository to the places your package manager will check.

Once `sbt` is installed, you'll be able to manage the package in `aptitude` or Synaptic after you updated their package cache. You should also be able to see the added repository at the bottom of the list in System Settings -> Software & Updates -> Other Software:

![Ubuntu Software & Updates Screenshot](/files/ubuntu-sources.png "Ubuntu Software & Updates Screenshot")

`sudo apt-key adv --keyserver hkps://keyserver.ubuntu.com:443 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823` may not work on Ubuntu Bionic LTS (18.04) since it's using a buggy GnuPG, so we are advising to use web API to download the public key in the above.

#### Red Hat Enterprise Linux and other RPM-based distributions

[RPM][RPM] package is officially supported by sbt, but it does not contain JAR files to save bandwidth.

Red Hat Enterprise Linux and other RPM-based distributions use the RPM format.
Run the following from the terminal to install `sbt` (You'll need superuser privileges to do so, hence the `sudo`).

```bash
# remove old Bintray repo file
sudo rm -f /etc/yum.repos.d/bintray-rpm.repo
curl -L https://www.scala-sbt.org/sbt-rpm.repo > sbt-rpm.repo
sudo mv sbt-rpm.repo /etc/yum.repos.d/
sudo yum install sbt
```

On Fedora (31 and above), use `sbt-rpm.repo`:

```bash
# remove old Bintray repo file
sudo rm -f /etc/yum.repos.d/bintray-rpm.repo
curl -L https://www.scala-sbt.org/sbt-rpm.repo > sbt-rpm.repo
sudo mv sbt-rpm.repo /etc/yum.repos.d/
sudo dnf install sbt
```

  [MSI]: $sbt_native_package_base$/v$app_version$/sbt-$windows_app_version$.msi
  [ZIP]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.tgz
  [RPM]: $sbt_rpm_package_base$sbt-$app_version$.rpm
  [DEB]: $sbt_deb_package_base$sbt-$app_version$.deb
  [Manual-Installation]: Manual-Installation.html
  [website127]: https://github.com/sbt/website/issues/127
  [cert-bug]: https://bugs.launchpad.net/ubuntu/+source/ca-certificates-java/+bug/1739631
  [openjdk-devel]: https://pkgs.org/download/java-1.8.0-openjdk-devel

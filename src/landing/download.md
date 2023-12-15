---
title: Download
---

Install sbt with **cs setup**
-----------------------------

Follow [Install](https://www.scala-lang.org/download/) page, and install Scala using Coursier.

```
cs setup
sbt --script-version
```

This should install the latest stable version of `sbt`.

Mac
-----

### SDKMAN!

```
$ sdk install sbt
```

### Homebrew

```
$ brew install sbt
```

⚠️ Homebrew maintainers have added a dependency to JDK 13 because they want to use more brew dependencies ([brew#50649](https://github.com/Homebrew/homebrew-core/issues/50649)). This causes sbt to use JDK 13 even when `java` available on PATH is JDK 8 or 11. To prevent `sbt` from running on JDK 13, install [jEnv](https://www.jenv.be/) or switch to using [SDKMAN](https://sdkman.io/).

Windows
-------

- [sbt-$windowsBuild$.msi](https://github.com/sbt/sbt/releases/download/v$sbtVersion$/sbt-$sbtVersion$.msi)
- [sbt-$sbtVersion$.msi.sha256](https://github.com/sbt/sbt/releases/download/v$sbtVersion$/sbt-$sbtVersion$.msi.sha256)
- [sbt-$sbtVersion$.msi.asc](https://github.com/sbt/sbt/releases/download/v$sbtVersion$/sbt-$sbtVersion$.msi.asc)

### [Chocolatey](https://chocolatey.org/packages/sbt)

```
> choco install sbt
```

### [Scoop](https://scoop.sh/)

```
> scoop install sbt
```

  <div class="distro_debian">
  	<h2>Linux (deb)</h2>
```
echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | sudo tee /etc/apt/sources.list.d/sbt_old.list
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo apt-key add
sudo apt-get update
sudo apt-get install sbt
```
  </div>

  <div class="distro_redhat">
  	<h2>Linux (rpm)</h2>
```
# remove old Bintray repo file
sudo rm -f /etc/yum.repos.d/bintray-rpm.repo || true
curl -L https://www.scala-sbt.org/sbt-rpm.repo > sbt-rpm.repo
sudo mv sbt-rpm.repo /etc/yum.repos.d/
sudo yum install sbt
```
  </div>

Universal packages
------------------

@@@vars

- [sbt-$sbtVersion$.zip](https://github.com/sbt/sbt/releases/download/v$sbtVersion$/sbt-$sbtVersion$.zip)
- [sbt-$sbtVersion$.zip.sha256](https://github.com/sbt/sbt/releases/download/v$sbtVersion$/sbt-$sbtVersion$.zip.sha256)
- [sbt-$sbtVersion$.zip.asc](https://github.com/sbt/sbt/releases/download/v$sbtVersion$/sbt-$sbtVersion$.zip.asc)
- [sbt-$sbtVersion$.tgz](https://github.com/sbt/sbt/releases/download/v$sbtVersion$/sbt-$sbtVersion$.tgz)
- [sbt-$sbtVersion$.tgz.sha256](https://github.com/sbt/sbt/releases/download/v$sbtVersion$/sbt-$sbtVersion$.tgz.sha256)
- [sbt-$sbtVersion$.tgz.asc](https://github.com/sbt/sbt/releases/download/v$sbtVersion$/sbt-$sbtVersion$.tgz.asc)

@@@

### Previous releases

<h4>1.x</h4>
<ul>

<li>
  sbt 1.9.8
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.8/sbt-1.9.8.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.8/sbt-1.9.8.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.8/sbt-1.9.8.msi">.msi</a>)
</li>

<li>
  sbt 1.9.7
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.7/sbt-1.9.7.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.7/sbt-1.9.7.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.7/sbt-1.9.7.msi">.msi</a>)
</li>

<li>
  sbt 1.9.6
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.6/sbt-1.9.6.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.6/sbt-1.9.6.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.6/sbt-1.9.6.msi">.msi</a>)
</li>

<li>
  sbt 1.9.5
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.5/sbt-1.9.5.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.5/sbt-1.9.5.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.5/sbt-1.9.5.msi">.msi</a>)
</li>

<li>
  sbt 1.9.4
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.4/sbt-1.9.4.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.4/sbt-1.9.4.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.4/sbt-1.9.4.msi">.msi</a>)
</li>

<li>
  sbt 1.9.3
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.3/sbt-1.9.3.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.3/sbt-1.9.3.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.3/sbt-1.9.3.msi">.msi</a>)
</li>

<li>
  sbt 1.9.2
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.2/sbt-1.9.2.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.2/sbt-1.9.2.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.2/sbt-1.9.2.msi">.msi</a>)
</li>

<li>
  sbt 1.9.1
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.1/sbt-1.9.1.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.1/sbt-1.9.1.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.1/sbt-1.9.1.msi">.msi</a>)
</li>

<li>
  sbt 1.9.0
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.0/sbt-1.9.0.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.0/sbt-1.9.0.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.9.0/sbt-1.9.0.msi">.msi</a>)
</li>

<li>
  sbt 1.8.3
  (<a href="https://github.com/sbt/sbt/releases/download/v1.8.3/sbt-1.8.3.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.8.3/sbt-1.8.3.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.8.3/sbt-1.8.3.msi">.msi</a>)
</li>

<li>
  sbt 1.8.2
  (<a href="https://github.com/sbt/sbt/releases/download/v1.8.2/sbt-1.8.2.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.8.2/sbt-1.8.2.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.8.2/sbt-1.8.2.msi">.msi</a>)
</li>

<li>
  sbt 1.8.1
  (<a href="https://github.com/sbt/sbt/releases/download/v1.8.1/sbt-1.8.1.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.8.1/sbt-1.8.1.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.8.1/sbt-1.8.1.msi">.msi</a>)
</li>

<li>
  sbt 1.8.0
  (<a href="https://github.com/sbt/sbt/releases/download/v1.8.0/sbt-1.8.0.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.8.0/sbt-1.8.0.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.8.0/sbt-1.8.0.msi">.msi</a>)
</li>

<li>
  sbt 1.7.3
  (<a href="https://github.com/sbt/sbt/releases/download/v1.7.3/sbt-1.7.3.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.7.3/sbt-1.7.3.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.7.3/sbt-1.7.3.msi">.msi</a>)
</li>

<li>
  sbt 1.7.2
  (<a href="https://github.com/sbt/sbt/releases/download/v1.7.2/sbt-1.7.2.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.7.2/sbt-1.7.2.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.7.2/sbt-1.7.2.msi">.msi</a>)
</li>

<li>
  sbt 1.7.1
  (<a href="https://github.com/sbt/sbt/releases/download/v1.7.1/sbt-1.7.1.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.7.1/sbt-1.7.1.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.7.1/sbt-1.7.1.msi">.msi</a>)
</li>

<li>
  sbt 1.7.0
  (<a href="https://github.com/sbt/sbt/releases/download/v1.7.0/sbt-1.7.0.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.7.0/sbt-1.7.0.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.7.0/sbt-1.7.0.msi">.msi</a>)
</li>

<li>
  sbt 1.6.2
  (<a href="https://github.com/sbt/sbt/releases/download/v1.6.2/sbt-1.6.2.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.6.2/sbt-1.6.2.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.6.2/sbt-1.6.2.msi">.msi</a>)
</li>

<li>
  sbt 1.6.1
  (<a href="https://github.com/sbt/sbt/releases/download/v1.6.1/sbt-1.6.1.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.6.1/sbt-1.6.1.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.6.1/sbt-1.6.1.msi">.msi</a>)
</li>

<li>
  sbt 1.6.0
  (<a href="https://github.com/sbt/sbt/releases/download/v1.6.0/sbt-1.6.0.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.6.0/sbt-1.6.0.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.6.0/sbt-1.6.0.msi">.msi</a>)
</li>

<li>
  sbt 1.5.8
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.8/sbt-1.5.8.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.8/sbt-1.5.8.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.8/sbt-1.5.8.msi">.msi</a>)
</li>

<li>
  sbt 1.5.7
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.7/sbt-1.5.7.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.7/sbt-1.5.7.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.7/sbt-1.5.7.msi">.msi</a>)
</li>

<li>
  sbt 1.5.6
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.6/sbt-1.5.6.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.6/sbt-1.5.6.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.6/sbt-1.5.6.msi">.msi</a>)
</li>

<li>
  sbt 1.5.5
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.5/sbt-1.5.5.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.5/sbt-1.5.5.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.5/sbt-1.5.5.msi">.msi</a>)
</li>

<li>
  sbt 1.5.4
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.4/sbt-1.5.4.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.4/sbt-1.5.4.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.4/sbt-1.5.4.msi">.msi</a>)
</li>

<li>
  sbt 1.5.3
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.3/sbt-1.5.3.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.3/sbt-1.5.3.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.3/sbt-1.5.3.msi">.msi</a>)
</li>

<li>
  sbt 1.5.2
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.2/sbt-1.5.2.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.2/sbt-1.5.2.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.2/sbt-1.5.2.msi">.msi</a>)
</li>

<li>
  sbt 1.5.1
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.1/sbt-1.5.1.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.1/sbt-1.5.1.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.1/sbt-1.5.1.msi">.msi</a>)
</li>

<li>
  sbt 1.5.0
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.0/sbt-1.5.0.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.0/sbt-1.5.0.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.5.0/sbt-1.5.0.msi">.msi</a>)
</li>

<li>
  sbt 1.4.9
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.9/sbt-1.4.9.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.9/sbt-1.4.9.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.9/sbt-1.4.9.msi">.msi</a>)
</li>

<li>
  sbt 1.4.8
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.8/sbt-1.4.8.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.8/sbt-1.4.8.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.8/sbt-1.4.8.msi">.msi</a>)
</li>

<li>
  sbt 1.4.7
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.7/sbt-1.4.7.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.7/sbt-1.4.7.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.7/sbt-1.4.7.msi">.msi</a>)
</li>

<li>
  sbt 1.4.6
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.6/sbt-1.4.6.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.6/sbt-1.4.6.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.6/sbt-1.4.6.msi">.msi</a>)
</li>

<li>
  sbt 1.4.5
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.5/sbt-1.4.5.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.5/sbt-1.4.5.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.5/sbt-1.4.5.msi">.msi</a>)
</li>

<li>
  sbt 1.4.4
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.4/sbt-1.4.4.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.4/sbt-1.4.4.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.4/sbt-1.4.4.msi">.msi</a>)
</li>

<li>
  sbt 1.4.3
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.3/sbt-1.4.3.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.3/sbt-1.4.3.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.3/sbt-1.4.3.msi">.msi</a>)
</li>

<li>
  sbt 1.4.2
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.2/sbt-1.4.2.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.2/sbt-1.4.2.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.2/sbt-1.4.2.msi">.msi</a>)
</li>

<li>
  sbt 1.4.1
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.1/sbt-1.4.1.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.1/sbt-1.4.1.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.1/sbt-1.4.1.msi">.msi</a>)
</li>

<li>
  sbt 1.4.0
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.0/sbt-1.4.0.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.0/sbt-1.4.0.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.4.0/sbt-1.4.0.msi">.msi</a>)
</li>

<li>
  sbt 1.3.13
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.13/sbt-1.3.13.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.13/sbt-1.3.13.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.13/sbt-1.3.13.msi">.msi</a>)
</li>

<li>
  sbt 1.3.12
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.12/sbt-1.3.12.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.12/sbt-1.3.12.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.12/sbt-1.3.12.msi">.msi</a>)
</li>

<li>
  sbt 1.3.10
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.10/sbt-1.3.10.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.10/sbt-1.3.10.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.10/sbt-1.3.10.msi">.msi</a>)
</li>

<li>
  sbt 1.3.9
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.9/sbt-1.3.9.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.9/sbt-1.3.9.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.9/sbt-1.3.9.msi">.msi</a>)
</li>

<li>
  sbt 1.3.8
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.8/sbt-1.3.8.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.8/sbt-1.3.8.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.8/sbt-1.3.8.msi">.msi</a>)
</li>

<li>
  sbt 1.3.7
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.7/sbt-1.3.7.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.7/sbt-1.3.7.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.7/sbt-1.3.7.msi">.msi</a>)
</li>

<li>
  sbt 1.3.6
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.6/sbt-1.3.6.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.6/sbt-1.3.6.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.6/sbt-1.3.6.msi">.msi</a>)
</li>

<li>
  sbt 1.3.5
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.5/sbt-1.3.5.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.5/sbt-1.3.5.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.5/sbt-1.3.5.msi">.msi</a>)
</li>

<li>
  sbt 1.3.4
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.4/sbt-1.3.4.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.4/sbt-1.3.4.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.4/sbt-1.3.4.msi">.msi</a>)
</li>

<li>
  sbt 1.3.3
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.3/sbt-1.3.3.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.3/sbt-1.3.3.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.3/sbt-1.3.3.msi">.msi</a>)
</li>

<li>
  sbt 1.3.2
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.2/sbt-1.3.2.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.2/sbt-1.3.2.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.2/sbt-1.3.2.msi">.msi</a>)
</li>

<li>
  sbt 1.3.1
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.1/sbt-1.3.1.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.1/sbt-1.3.1.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.1/sbt-1.3.1.msi">.msi</a>)
</li>

<li>
  sbt 1.3.0
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.0/sbt-1.3.0.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.0/sbt-1.3.0.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.3.0/sbt-1.3.0.msi">.msi</a>)
</li>

<li>
  sbt 1.2.8
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.8/sbt-1.2.8.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.8/sbt-1.2.8.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.8/sbt-1.2.8.msi">.msi</a>)
</li>

<li>
  sbt 1.2.7
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.7/sbt-1.2.7.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.7/sbt-1.2.7.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.7/sbt-1.2.7.msi">.msi</a>)
</li>

<li>
  sbt 1.2.6
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.6/sbt-1.2.6.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.6/sbt-1.2.6.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.6/sbt-1.2.6.msi">.msi</a>)
</li>

<li>
  sbt 1.2.4
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.4/sbt-1.2.4.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.4/sbt-1.2.4.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.4/sbt-1.2.4.msi">.msi</a>)
</li>

<li>
  sbt 1.2.3
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.3/sbt-1.2.3.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.3/sbt-1.2.3.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.3/sbt-1.2.3.msi">.msi</a>)
</li>

<li>
  sbt 1.2.1
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.1/sbt-1.2.1.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.1/sbt-1.2.1.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.1/sbt-1.2.1.msi">.msi</a>)
</li>

<li>
  sbt 1.2.0
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.0/sbt-1.2.0.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.0/sbt-1.2.0.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.2.0/sbt-1.2.0.msi">.msi</a>)
</li>

<li>
  sbt 1.1.6
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.6/sbt-1.1.6.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.6/sbt-1.1.6.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.6/sbt-1.1.6.msi">.msi</a>)
</li>

<li>
  sbt 1.1.5
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.5/sbt-1.1.5.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.5/sbt-1.1.5.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.5/sbt-1.1.5.msi">.msi</a>)
</li>

<li>
  sbt 1.1.4
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.4/sbt-1.1.4.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.4/sbt-1.1.4.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.4/sbt-1.1.4.msi">.msi</a>)
</li>

<li>
  sbt 1.1.2
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.2/sbt-1.1.2.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.2/sbt-1.1.2.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.2/sbt-1.1.2.msi">.msi</a>)
</li>

<li>
  sbt 1.1.1
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.1/sbt-1.1.1.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.1/sbt-1.1.1.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.1/sbt-1.1.1.msi">.msi</a>)
</li>

<li>
  sbt 1.1.0
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.0/sbt-1.1.0.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.0/sbt-1.1.0.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.1.0/sbt-1.1.0.msi">.msi</a>)
</li>

<li>
  sbt 1.0.4
  (<a href="https://github.com/sbt/sbt/releases/download/v1.0.4/sbt-1.0.4.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.0.4/sbt-1.0.4.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.0.4/sbt-1.0.4.msi">.msi</a>)
</li>

<li>
  sbt 1.0.3
  (<a href="https://github.com/sbt/sbt/releases/download/v1.0.3/sbt-1.0.3.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.0.3/sbt-1.0.3.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.0.3/sbt-1.0.3.msi">.msi</a>)
</li>

<li>
  sbt 1.0.2
  (<a href="https://github.com/sbt/sbt/releases/download/v1.0.2/sbt-1.0.2.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.0.2/sbt-1.0.2.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.0.2/sbt-1.0.2.msi">.msi</a>)
</li>

<li>
  sbt 1.0.1
  (<a href="https://github.com/sbt/sbt/releases/download/v1.0.1/sbt-1.0.1.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.0.1/sbt-1.0.1.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.0.1/sbt-1.0.1.msi">.msi</a>)
</li>

<li>
  sbt 1.0.0
  (<a href="https://github.com/sbt/sbt/releases/download/v1.0.0/sbt-1.0.0.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.0.0/sbt-1.0.0.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v1.0.0/sbt-1.0.0.msi">.msi</a>)
</li>

</ul>

<h4>0.13</h4>
<ul>
<li>
  sbt 0.13.18
  (<a href="https://github.com/sbt/sbt/releases/download/v0.13.18/sbt-0.13.18.zip">.zip</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v0.13.18/sbt-0.13.18.tgz">.tgz</a>)
  (<a href="https://github.com/sbt/sbt/releases/download/v0.13.18/sbt-0.13.18.msi">.msi</a>)
</li>

</ul>

Release Timeline
----------------

| sbt series&nbsp; | Initial Release&nbsp;    | Current Release&nbsp; | Status      |
|------------|-------------------------:|:---------------:|-------------|
| 1.x        | [2017-08-11][sbt_1.0.0]  |        ✅        | On-going for 6 years and more |
| 0.13.x     | [2013-08-26][sbt_0.13.0] |        No        | [End of Life on 2018-11-30][sbt_0.13.18] after 5 years |
| 0.12.x     | [2012-08-01][sbt_0.12.0] |        No        | Last released on [2013-06-27][sbt_0.12.4] |
| 0.11.x     | [2011-09-24][sbt_0.11.0] |        No        | Last released on [2012-05-06][sbt_0.11.3] |
| 0.10.x     | [2011-06-02][sbt_0.10.0] |        No        | Last released on [2011-07-14][sbt_0.10.1] |
| 0.9.x      | [2011-02-17][sbt_0.9.0]  |        No        | Last released on [2011-06-01][sbt_0.9.10] |
| 0.7.x      | [2010-02-15][sbt_0.7.0]  |        No        | Last released on [2011-05-12][sbt_0.7.7] |
| 0.5.x      | [2009-07-01][sbt_0.5.1]  |        No        | |
| 0.4.x      | [2009-03-27][sbt_0.4.0]  |        No        | |
| 0.3.x      | [2008-12-17][sbt_0.3.1]  |        No        | |

  [sbt_0.3.1]: https://groups.google.com/g/simple-build-tool/c/lZqxMItxap8/m/dZiVsgNW3EwJ
  [sbt_0.4.0]: https://groups.google.com/g/simple-build-tool/c/MkJPTm60OvU/m/bDhV1u2I1BIJ
  [sbt_0.5.1]: https://groups.google.com/g/simple-build-tool/c/oN5vhhDigqI/m/Zzb0Vmq5xiYJ
  [sbt_0.7.7]: https://groups.google.com/g/simple-build-tool/c/HBCDjeV1JzA/m/JcOrutMlClAJ
  [sbt_0.7.0]: https://groups.google.com/g/simple-build-tool/c/zgegHD8zJzY/m/tyWIALFGiSQJ
  [sbt_0.11.3]: https://groups.google.com/g/simple-build-tool/c/6QyKHMczG3I/m/PnhiJVZSlloJ
  [sbt_0.11.0]: https://groups.google.com/g/simple-build-tool/c/PiRxuWiuyic/m/vAt18ooouNsJ
  [sbt_0.10.1]: https://github.com/sbt/sbt/releases/tag/v0.10.1
  [sbt_0.10.0]: https://github.com/sbt/sbt/releases/tag/v0.10.0
  [sbt_0.9.10]: https://github.com/sbt/sbt/releases/tag/v0.9.10
  [sbt_0.9.0]: https://vimeo.com/20263617
  [sbt_0.12.4]: https://groups.google.com/g/simple-build-tool/c/UHlEuuIg0Xo/m/RzRYphp3lDwJ
  [sbt_0.12.0]: https://groups.google.com/g/simple-build-tool/c/XDTrQL-PcYo/m/DK74LZeqr14J
  [sbt_0.13.0]: https://groups.google.com/g/simple-build-tool/c/0AGST5qPbzw/m/s75DMBYanwoJ
  [sbt_0.13.18]: https://web.archive.org/web/20210918065807/https://www.lightbend.com/blog/scala-sbt-127-patchnotes
  [sbt_1.0.0]: https://web.archive.org/web/20220624215309/https://www.lightbend.com/blog/scala-sbt-100-release

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

 Mac
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




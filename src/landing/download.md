---
layout: defaultIBMbranded
title: Download
---

@@@ div { .arc_mac}

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

@@@

@@@ div { .arc_windows }

Windows
-------

- [sbt-$windowsBuild$.msi](https://piccolo.link/sbt-$windowsBuild$.msi)

### Scoop

```
> scoop install sbt
```

@@@

@@@ div { .arc_linux }

  <div class="distro_debian">
  	<h2>Linux (deb)</h2>
```
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo apt-key add
sudo apt-get update
sudo apt-get install sbt
```
  </div>

  <div class="distro_redhat">
  	<h2>Linux (rpm)</h2>
```
curl https://bintray.com/sbt/rpm/rpm > bintray-sbt-rpm.repo
sudo mv bintray-sbt-rpm.repo /etc/yum.repos.d/
sudo yum install sbt
```
  </div>

@@@

All platforms
-------------

@@@vars

- [sbt-$sbtVersion$.zip](https://piccolo.link/sbt-$sbtVersion$.zip)
- [sbt-$sbtVersion$.zip.sha256](https://piccolo.link/sbt-$sbtVersion$.zip.sha256)
- [sbt-$sbtVersion$.zip.asc](https://piccolo.link/sbt-$sbtVersion$.zip.asc)
- [sbt-$sbtVersion$.tgz](https://piccolo.link/sbt-$sbtVersion$.tgz)
- [sbt-$sbtVersion$.tgz.sha256](https://piccolo.link/sbt-$sbtVersion$.tgz.sha256)
- [sbt-$sbtVersion$.tgz.asc](https://piccolo.link/sbt-$sbtVersion$.tgz.asc)

@@@

### Previous releases

<h4>1.x</h4>
<ul>

<li>
  sbt 1.3.9
  (<a href="https://piccolo.link/sbt-1.3.9.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.3.9.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.3.9.msi">.msi</a>)
</li>

<li>
  sbt 1.3.8
  (<a href="https://piccolo.link/sbt-1.3.8.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.3.8.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.3.8.msi">.msi</a>)
</li>

<li>
  sbt 1.3.7
  (<a href="https://piccolo.link/sbt-1.3.7.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.3.7.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.3.7.msi">.msi</a>)
</li>

<li>
  sbt 1.3.6
  (<a href="https://piccolo.link/sbt-1.3.6.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.3.6.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.3.6.msi">.msi</a>)
</li>

<li>
  sbt 1.3.5
  (<a href="https://piccolo.link/sbt-1.3.5.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.3.5.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.3.5.msi">.msi</a>)
</li>

<li>
  sbt 1.3.4
  (<a href="https://piccolo.link/sbt-1.3.4.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.3.4.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.3.4.msi">.msi</a>)
</li>

<li>
  sbt 1.3.3
  (<a href="https://piccolo.link/sbt-1.3.3.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.3.3.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.3.3.msi">.msi</a>)
</li>

<li>
  sbt 1.3.2
  (<a href="https://piccolo.link/sbt-1.3.2.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.3.2.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.3.2.msi">.msi</a>)
</li>

<li>
  sbt 1.3.1
  (<a href="https://piccolo.link/sbt-1.3.1.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.3.1.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.3.1.msi">.msi</a>)
</li>

<li>
  sbt 1.3.0
  (<a href="https://piccolo.link/sbt-1.3.0.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.3.0.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.3.0.msi">.msi</a>)
</li>

<li>
  sbt 1.2.8
  (<a href="https://piccolo.link/sbt-1.2.8.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.2.8.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.2.8.msi">.msi</a>)
</li>

<li>
  sbt 1.2.7
  (<a href="https://piccolo.link/sbt-1.2.7.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.2.7.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.2.7.msi">.msi</a>)
</li>

<li>
  sbt 1.2.6
  (<a href="https://piccolo.link/sbt-1.2.6.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.2.6.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.2.6.msi">.msi</a>)
</li>

<li>
  sbt 1.2.4
  (<a href="https://piccolo.link/sbt-1.2.4.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.2.4.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.2.4.msi">.msi</a>)
</li>

<li>
  sbt 1.2.3
  (<a href="https://piccolo.link/sbt-1.2.3.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.2.3.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.2.3.msi">.msi</a>)
</li>

<li>
  sbt 1.2.1
  (<a href="https://piccolo.link/sbt-1.2.1.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.2.1.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.2.1.msi">.msi</a>)
</li>

<li>
  sbt 1.2.0
  (<a href="https://piccolo.link/sbt-1.2.0.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.2.0.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.2.0.msi">.msi</a>)
</li>

<li>
  sbt 1.1.6
  (<a href="https://piccolo.link/sbt-1.1.6.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.1.6.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.1.6.msi">.msi</a>)
</li>

<li>
  sbt 1.1.5
  (<a href="https://piccolo.link/sbt-1.1.5.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.1.5.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.1.5.msi">.msi</a>)
</li>

<li>
  sbt 1.1.4
  (<a href="https://piccolo.link/sbt-1.1.4.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.1.4.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.1.4.msi">.msi</a>)
</li>

<li>
  sbt 1.1.2
  (<a href="https://piccolo.link/sbt-1.1.2.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.1.2.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.1.2.msi">.msi</a>)
</li>

<li>
  sbt 1.1.1
  (<a href="https://piccolo.link/sbt-1.1.1.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.1.1.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.1.1.msi">.msi</a>)
</li>

<li>
  sbt 1.1.0
  (<a href="https://piccolo.link/sbt-1.1.0.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.1.0.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.1.0.msi">.msi</a>)
</li>

<li>
  sbt 1.0.4
  (<a href="https://piccolo.link/sbt-1.0.4.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.0.4.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.0.4.msi">.msi</a>)
</li>

<li>
  sbt 1.0.3
  (<a href="https://piccolo.link/sbt-1.0.3.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.0.3.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.0.3.msi">.msi</a>)
</li>

<li>
  sbt 1.0.2
  (<a href="https://piccolo.link/sbt-1.0.2.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.0.2.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.0.2.msi">.msi</a>)
</li>

<li>
  sbt 1.0.1
  (<a href="https://piccolo.link/sbt-1.0.1.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.0.1.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.0.1.msi">.msi</a>)
</li>

<li>
  sbt 1.0.0
  (<a href="https://piccolo.link/sbt-1.0.0.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-1.0.0.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-1.0.0.msi">.msi</a>)
</li>

</ul>

<h4>0.13</h4>
<ul>
<li>
  sbt 0.13.18
  (<a href="https://piccolo.link/sbt-0.13.18.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-0.13.18.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-0.13.18.msi">.msi</a>)
</li>

<li>
  sbt 0.13.17
  (<a href="https://piccolo.link/sbt-0.13.17.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-0.13.17.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-0.13.17.msi">.msi</a>)
</li>

<li>
  sbt 0.13.16
  (<a href="https://piccolo.link/sbt-0.13.16.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-0.13.16.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-0.13.16.msi">.msi</a>)
</li>

<li>
  sbt 0.13.15
  (<a href="https://piccolo.link/sbt-0.13.15.zip">.zip</a>)
  (<a href="https://piccolo.link/sbt-0.13.15.tgz">.tgz</a>)
  (<a href="https://piccolo.link/sbt-0.13.15.msi">.msi</a>)
</li>

</ul>




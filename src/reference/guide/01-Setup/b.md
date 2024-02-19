---
out: Installing-sbt-on-Windows.html
---

  [MSI]: $sbt_native_package_base$/v$app_version$/sbt-$windows_app_version$.msi
  [ZIP]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.tgz
  [AdoptiumOpenJDK]: https://adoptium.net

Installing sbt on Windows
-------------------------

### Install sbt with **cs setup**

Follow [Install](https://www.scala-lang.org/download/) page, and install Scala using Coursier. This should install the latest stable version of `sbt`.

### Install JDK

Follow the link to install [JDK 8 or 11][AdoptiumOpenJDK].

### Installing from a universal package

Download [ZIP][ZIP] or [TGZ][TGZ] package and expand it.

### Windows installer

Download [msi installer][MSI] and install it.

### Installing from a third-party package

> **Note:** Third-party packages may not provide the latest version. Please make
> sure to report any issues with these packages to the relevant
> maintainers.

#### [Scoop](https://scoop.sh/)

```
\$ scoop install sbt
```

#### [Chocolatey](https://chocolatey.org)

```
\$ choco install sbt
```

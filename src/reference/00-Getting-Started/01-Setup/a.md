---
out: Installing-sbt-on-Mac.html
---

  [ZIP]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.tgz
  [Manual-Installation]: Manual-Installation.html
  [AdoptiumOpenJDK]: https://adoptium.net/

Installing sbt on macOS
-----------------------

### Install sbt with **cs setup**

Follow [Install](https://www.scala-lang.org/download/) page, and install Scala using Coursier. This should install the latest stable version of `sbt`.

### Install JDK

Follow the link to install [JDK 8 or 11][AdoptiumOpenJDK], or use [SDKMAN!](https://sdkman.io/).

#### [SDKMAN!](https://sdkman.io/)

@@snip [install.sh]($root$/src/includes/install.sh) {}

### Installing from a universal package

Download [ZIP][ZIP] or [TGZ][TGZ] package, and expand it.

### Installing from a third-party package

> **Note:** Third-party packages may not provide the latest version. Please make
> sure to report any issues with these packages to the relevant
> maintainers.

#### [Homebrew](https://brew.sh/)

```
\$ brew install sbt
```

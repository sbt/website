---
out: Installing-sbt-on-Mac.html
---

  [ZIP]: $sbt_native_package_base$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$/sbt-$app_version$.tgz
  [Manual-Installation]: Manual-Installation.html
  [AdoptOpenJDK]: https://adoptopenjdk.net/

Installing sbt on macOS
-----------------------

### Install JDK

Follow the link to install [JDK 8 or 11][AdoptOpenJDK].

Or use [SDKMAN!](https://sdkman.io/):

```
\$ sdk list java
\$ sdk install java 11.0.4.hs-adpt
```

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

#### [SDKMAN!](https://sdkman.io/)

```
\$ sdk install sbt
```

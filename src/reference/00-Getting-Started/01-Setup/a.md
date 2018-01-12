---
out: Installing-sbt-on-Mac.html
---

  [ZIP]: $sbt_native_package_base$$app_version$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$$app_version$/sbt-$app_version$.tgz
  [Manual-Installation]: Manual-Installation.html
  [oraclejdk8]: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

Installing sbt on Mac
---------------------

### Install JDK

Follow the link to install [Java SE Development Kit 8][oraclejdk8].

### Installing from a universal package

Download [ZIP][ZIP] or [TGZ][TGZ] package, and expand it.

### Installing from a third-party package

> **Note:** Third-party packages may not provide the latest version. Please make
> sure to report any issues with these packages to the relevant
> maintainers.

#### [Homebrew](http://mxcl.github.com/homebrew/)

```
\$ brew install sbt@1
```

#### [Macports](http://macports.org/)

```
\$ port install sbt
```

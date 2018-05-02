---
out: Installing-sbt-on-Mac.html
---

  [ZIP]: $sbt_native_package_base$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$/sbt-$app_version$.tgz
  [Manual-Installation]: Manual-Installation.html
  [oraclejdk8]: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

Mac への sbt のインストール
-------------------------

### Install JDK

Follow the link to install [Java SE Development Kit 8][oraclejdk8].

### ユニバーサルパッケージからのインストール

[ZIP][ZIP] か [TGZ][TGZ] をダウンロードしてきて解凍する。

### サードパーティパッケージを使ってのインストール

> **注意:** サードパーティが提供するパッケージは最新版を使っているとは限らない。
> 何か問題があれば、パッケージメンテナに報告してほしい。

#### [Homebrew](http://mxcl.github.com/homebrew/)

```
\$ brew install sbt@1
```

#### [Macports](http://macports.org/)

```
\$ port install sbt
```

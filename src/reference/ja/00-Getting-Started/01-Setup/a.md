---
out: Installing-sbt-on-Mac.html
---

  [MSI]: $sbt_native_package_base$/v$app_version$/sbt-$windows_app_version$.msi
  [ZIP]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.tgz
  [Manual-Installation]: Manual-Installation.html
  [AdoptiumOpenJDK]: https://adoptium.net

macOS への sbt のインストール
--------------------------

### **cs setup** を用いた sbt のインストール

[Install](https://www.scala-lang.org/download/) に従い Coursier を用いて Scala をインストールする。これは最新の安定版の `sbt` を含む。

### JDK のインストール

リンクをたどって [JDK 8 もしくは JDK 11][AdoptiumOpenJDK] をインストールする、
もしくは [SDKMAN!](https://sdkman.io/) を使う。

#### [SDKMAN!](https://sdkman.io/)

@@snip [install.sh]($root$/src/includes/install.sh) {}

### ユニバーサルパッケージからのインストール

[ZIP][ZIP] か [TGZ][TGZ] をダウンロードしてきて解凍する。

### サードパーティパッケージを使ってのインストール

> **注意:** サードパーティが提供するパッケージは最新版を使っているとは限らない。
> 何か問題があれば、パッケージメンテナに報告してほしい。

#### [Homebrew](https://brew.sh/)

```
\$ brew install sbt
```

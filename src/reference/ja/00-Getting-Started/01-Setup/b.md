---
out: Installing-sbt-on-Windows.html
---

  [MSI]: $sbt_native_package_base$/v$app_version$/sbt-$windows_app_version$.msi
  [ZIP]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.tgz
  [AdoptiumOpenJDK]: https://adoptium.net

Windows への sbt のインストール
----------------------------

### **cs setup** を用いた sbt のインストール

[Install](https://www.scala-lang.org/download/) に従い Coursier を用いて Scala をインストールする。これは最新の安定版の `sbt` を含む。

### JDK のインストール

リンクをたどって [JDK 8 もしくは JDK 11][AdoptiumOpenJDK] をインストールする。

### ユニバーサルパッケージからのインストール

[ZIP][ZIP] か [TGZ][TGZ] をダウンロードしてきて解凍する。

### Windows インストーラ

[msi インストーラ][MSI]をダウンロードしてインストールする。

### サードパーティパッケージを使ってのインストール

> **注意:** サードパーティが提供するパッケージは最新版を使っているとは限らない。
> 何か問題があれば、パッケージメンテナに報告してほしい。

#### [Scoop](https://scoop.sh/)

```
\$ scoop install sbt
```

---
out: Installing-sbt-on-Mac.html
---

  [ZIP]: $sbt_native_package_base$$app_version$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$$app_version$/sbt-$app_version$.tgz
  [Manual-Installation]: Manual-Installation.html

Mac への sbt のインストール
-------------------------

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

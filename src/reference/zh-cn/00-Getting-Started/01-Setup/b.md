---
out: Installing-sbt-on-Windows.html
---

  [MSI]: $sbt_native_package_base$/v$app_version$/sbt-$windows_app_version$.msi
  [ZIP]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.tgz

在 Windows 上安装 sbt
-------------------------

### Install sbt with **cs setup**

Follow [Install](https://www.scala-lang.org/download/) page, and install Scala using Coursier. This should install the latest stable version of `sbt`.

### 通过通用的安装包安装

下载 [ZIP][ZIP] 或者 [TGZ][TGZ] 包并解压。

### 通过 Windows 安装包安装

下载 [msi 安装包][MSI] 并安装。

### 通过第三方的包安装

> **注意：** 第三方的包可能没有提供最新的版本，请记得将任何问题反馈给这些包相关的维护者。

#### 通过 [Scoop](https://scoop.sh/) 安装

```
\$ scoop install sbt
```

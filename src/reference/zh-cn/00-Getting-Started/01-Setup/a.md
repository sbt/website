---
out: Installing-sbt-on-Mac.html
---

  [MSI]: $sbt_native_package_base$/v$app_version$/sbt-$windows_app_version$.msi
  [ZIP]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.tgz
  [Manual-Installation]: Manual-Installation.html

在 macOS 上安装 sbt
---------------------

### Install JDK

Follow the link to install [JDK 8 or 11][AdoptOpenJDK].

Or use [SDKMAN!](https://sdkman.io/):

```
\$ sdk install java \$(sdk list java | grep -o "8\.[0-9]*\.[0-9]*\.hs-adpt" | head -1)
```

#### 通过 [SDKMAN!](https://sdkman.io/) 安装

```
\$ sdk install sbt
```

### 通过通用的包安装

下载 [ZIP][ZIP] 或者 [TGZ][TGZ] 包并解压。

### 通过第三方的包安装

> **注意：** 第三方的包可能没有提供最新的版本，请记得将任何问题反馈给这些包相关的维护者。

#### 通过 [Homebrew](https://brew.sh/) 安装

```
\$ brew install sbt
```

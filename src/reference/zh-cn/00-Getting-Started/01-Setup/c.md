---
out: Installing-sbt-on-Linux.html
---

  [MSI]: $sbt_native_package_base$/v$app_version$/sbt-$windows_app_version$.msi
  [ZIP]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.tgz
  [RPM]: $sbt_rpm_package_base$sbt-$app_version$.rpm
  [DEB]: $sbt_deb_package_base$sbt-$app_version$.deb
  [Manual-Installation]: Manual-Installation.html
  [website127]: https://github.com/sbt/website/issues/12
  [cert-bug]: https://bugs.launchpad.net/ubuntu/+source/ca-certificates-java/+bug/1739631
  [openjdk-devel]: https://pkgs.org/download/java-1.8.0-openjdk-devel

在 Linux 上安装 sbt
-----------------------

### Install sbt with **cs setup**

Follow [Install](https://www.scala-lang.org/download/) page, and install Scala using Coursier. This should install the latest stable version of `sbt`.

### Installing from SDKMAN

To install both JDK and sbt, consider using [SDKMAN](https://sdkman.io/).

@@snip [install.sh]($root$/src/includes/install.sh) {}

Using Coursier or SDKMAN has two advantages.

1. They will install the official packaging by Eclipse Adoptium, as opposed to the ["mystery meat OpenJDK builds"](https://mail.openjdk.java.net/pipermail/jdk8u-dev/2019-May/009330.html).
2. They will install `tgz` packaging of sbt that contains all JAR files. (DEB and RPM packages do not to save bandwidth)

### Install JDK

You must first install a JDK. We recommend **Eclipse Adoptium Temurin JDK 8**, **JDK 11**, or **JDK 17**.

The details around the package names differ from one distribution to another. For example, Ubuntu xenial (16.04LTS) has [openjdk-8-jdk](https://packages.ubuntu.com/hu/xenial/openjdk-8-jdk). Redhat family calls it [java-1.8.0-openjdk-devel][openjdk-devel].

### 通过通用的安装包安装

下载 [ZIP][ZIP] 或者 [TGZ][TGZ] 包并解压。

### Ubuntu和其他基于Debian的发行版

[DEB][DEB] 安装包由sbt官方支持。

Ubuntu和其他基于Debian的发行版使用DEB格式，但通常你不从本地的DEB文件安装软件。相反，他们由程序包管理器安装，通过命令行（如`apt-get`，`aptitude`）或图形用户界面 （如Synaptic）。
从终端运行下面的命令安装`sbt`（你需要超级用户权限，因此需要`sudo`）。

    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | sudo tee /etc/apt/sources.list.d/sbt_old.list
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo apt-key add
    sudo apt-get update
    sudo apt-get install sbt

软件包管理器将检查若干个提供安装软件包的配置存储库。sbt 二进制文件发布到 Bintray，而Bintray 方便地提供了APT资源库。你只需要将存储库添加到你的软件包管理器将检查的地方。
一旦安装了`sbt`，你会能够在`aptitude`或Synaptic的包缓存更新后管理了。你也应该能够看到添加的存储库，在底部的``System Settings -> Software & Updates -> Other Software``：

![Ubuntu Software & Updates Screenshot](../files/ubuntu-sources.png "Ubuntu Software & Updates Screenshot")

### 红帽企业版Linux和其他基于RPM的发行版

[RPM][RPM] 安装包由sbt官方支持。

红帽企业版Linux和其他基于RPM的发行版使用RPM格式。
从终端运行下面的命令安装`sbt`（你需要超级用户权限，因此需要`sudo`）。

    # remove old Bintray repo file
    sudo rm -f /etc/yum.repos.d/bintray-rpm.repo
    curl -L https://www.scala-sbt.org/sbt-rpm.repo > sbt-rpm.repo
    sudo mv sbt-rpm.repo /etc/yum.repos.d/
    sudo yum install sbt

On Fedora (31 and above), use `sbt-rpm.repo`:

    # remove old Bintray repo file
    sudo rm -f /etc/yum.repos.d/bintray-rpm.repo
    curl -L https://www.scala-sbt.org/sbt-rpm.repo > sbt-rpm.repo
    sudo mv sbt-rpm.repo /etc/yum.repos.d/
    sudo dnf install sbt

> **注意：** 请将任何和这两个包相关的问题反馈到 [sbt-launcher-package](https://github.com/sbt/sbt-launcher-package) 项目。

### Gentoo

在 sbt 官方的树中没有提供 ebuild。 但是有从二进制合并 sbt 的 [ebuilds](https://github.com/whiter4bbit/overlays/tree/master/dev-java/sbt-bin)。
可以通过以下方式从这些 ebuilds 中合并 sbt：

    emerge dev-java/sbt

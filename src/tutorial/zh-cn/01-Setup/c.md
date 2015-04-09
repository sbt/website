---
out: Installing-sbt-on-Linux.html
---

  [ZIP]: $sbt_native_package_base$$app_version$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$$app_version$/sbt-$app_version$.tgz
  [RPM]: $sbt_rpm_package_base$sbt-$app_version$.rpm
  [DEB]: $sbt_deb_package_base$sbt-$app_version$.deb
  [Manual-Installation]: Manual-Installation.html
  [Activator-Installation]: Activator-Installation.html

在 Linux 上安装 sbt
-----------------------

### 通过通用的安装包安装

下载 [ZIP][ZIP] 或者 [TGZ][TGZ] 包并解压。

### Ubuntu和其他基于Debian的发行版

[DEB][DEB] 安装包由sbt官方支持。

Ubuntu和其他基于Debian的发行版使用DEB格式，但通常你不从本地的DEB文件安装软件。相反，他们由程序包管理器安装，通过命令行（如`apt-get`，`aptitude`）或图形用户界面 （如Synaptic）。
从终端运行下面的命令安装`sbt`（你需要超级用户权限，因此需要`sudo`）。


    echo "deb http://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
    sudo apt-get update
    sudo apt-get install sbt

软件包管理器将检查若干个提供安装软件包的配置存储库。sbt 二进制文件发布到 Bintray，而Bintray 方便地提供了APT资源库。你只需要将存储库添加到你的软件包管理器将检查的地方。
一旦安装了`sbt`，你会能够在`aptitude`或Synaptic的包缓存更新后管理了。你也应该能够看到添加的存储库，在底部的``System Settings -> Software & Updates -> Other Software``：

![Ubuntu Software & Updates Screenshot](../files/ubuntu-sources.png "Ubuntu Software & Updates Screenshot")

### 红帽企业版Linux和其他基于RPM的发行版

[RPM][RPM] 安装包由sbt官方支持。

红帽企业版Linux和其他基于RPM的发行版使用RPM格式。
从终端运行下面的命令安装`sbt`（你需要超级用户权限，因此需要`sudo`）。

    curl https://bintray.com/sbt/rpm/rpm > bintray-sbt-rpm.repo
    sudo mv bintray-sbt-rpm.repo /etc/yum.repos.d/
    sudo yum install sbt

sbt 二进制文件发布到 Bintray，而Bintray 方便地提供了RPM资源库。你只需要将存储库添加到你的软件包管理器将检查的地方。

> **注意：** 请将任何和这两个包相关的问题反馈到 [sbt-launcher-package](https://github.com/sbt/sbt-launcher-package) 项目。

### Gentoo

在 sbt 官方的树中没有提供 ebuild。 但是有从二进制合并 sbt 的 [ebuilds](https://github.com/whiter4bbit/overlays/tree/master/dev-java/sbt-bin)。
可以通过以下方式从这些 ebuilds 中合并 sbt：

    mkdir -p /usr/local/portage && cd /usr/local/portage
    git clone git://github.com/whiter4bbit/overlays.git
    echo "PORTDIR_OVERLAY=\$PORTDIR_OVERLAY /usr/local/portage/overlays" >> /etc/make.conf
    emerge sbt-bin

> **注意：** 请将任何和 ebuild 相关的问题反馈到 [这里](https://github.com/whiter4bbit/overlays/issues)。

### Typesafe Activator

参见 [Typesafe Activator安装指南][Activator-Installation].

### 手动安装

参见[手动安装指南][Manual-Installation]。

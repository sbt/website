---
out: Installing-sbt-on-Linux.html
---

  [ZIP]: $sbt_native_package_base$$app_version$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$$app_version$/sbt-$app_version$.tgz
  [RPM]: $sbt_rpm_package_base$sbt-$app_version$.rpm
  [DEB]: $sbt_deb_package_base$sbt-$app_version$.deb
  [Manual-Installation]: Manual-Installation.html
 
Linux への sbt のインストール
--------------------------

### ユニバーサルパッケージからのインストール

[ZIP][ZIP] か [TGZ][TGZ] をダウンロードしてきて解凍する。

### RPM and DEB

以下のパッケージも公式にサポートしている:

  - [RPM][RPM] package
  - [DEB][DEB] package

> **注意:** これらのパッケージに問題があれば、
> [sbt-launcher-package](https://github.com/sbt/sbt-launcher-package)
> プロジェクトに報告してほしい。

### Ubuntu and other Debian-based Linux Distributions

Linux distributions based on Debian, such as Ubuntu use the DEB format, but usually you don't install your software from a local DEB file. Instead they come with package managers both for the command line (e.g. apt-get, aptitude) or with a graphical user interface (e.g. Synaptic).
Luckily, they all use the same Advanced Packaging Tool (APT) conventions:
They will check a number of configured repositories for packages to offer for installation. sbt binaries are published to Bintray, and conveniently Bintray provides an APT repository. You just have to add the repository to the places your package manager will check. So in the directory `/etc/apt/sources.list.d`, create a file `sbt.list` with the contents `deb http://dl.bintray.com/sbt/debian /`

For example like this:

    echo "deb http://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list

(You'll need superuser privileges to do so, hence the `sudo`). You can then use `apt-get install sbt` to install sbt, and you'll be able to see/manage the package in aptitude or Synaptic after you updated their package cache. You should also be able to see the added repository at the bottom of the list in System Settings -> Software & Updates -> Other Software, like here:

![Ubuntu Software & Updates Screenshot](../files/ubuntu-sources.png "Ubuntu Software & Updates Screenshot")

### Gentoo

公式には sbt の ebuild は提供していないけども、バイナリから
sbt をマージする [ebuild](https://github.com/whiter4bbit/overlays/tree/master/dev-java/sbt-bin)
があるみたいだ。この ebuild を使って sbt をマージするには:

    \$ mkdir -p /usr/local/portage && cd /usr/local/portage
    \$ git clone git://github.com/whiter4bbit/overlays.git
    \$ echo "PORTDIR_OVERLAY=\$PORTDIR_OVERLAY /usr/local/portage/overlays" >> /etc/make.conf
    \$ emerge sbt-bin

> **注意:** この ebuild に関する問題があれば
> [ここ](https://github.com/whiter4bbit/overlays/issues)
> に報告してほしい。

### 手動インストール

手動インストールの手順を参照。

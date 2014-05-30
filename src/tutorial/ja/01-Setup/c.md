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

---
out: Installing-sbt-on-Linux.html
---

  [ZIP]: $sbt_native_package_base$/$download_version_str$zip
  [TGZ]: $sbt_native_package_base$/$download_version_str$tgz
  [RPM]: $sbt_rpm_package_base$sbt-$app_version$.rpm
  [DEB]: $sbt_deb_package_base$sbt-$app_version$.deb
  [Manual-Installation]: Manual-Installation.html
  [website127]: https://github.com/sbt/website/issues/12

Linux への sbt のインストール
--------------------------

### ユニバーサルパッケージからのインストール

[ZIP][ZIP] か [TGZ][TGZ] をダウンロードしてきて解凍する。

### Ubuntu 及びその他の Debian ベースの Linux ディストリビューション

[DEB][DEB] は sbt による公式パッケージだ。

Ubuntu 及びその他の Debian ベースのディストリビューションは DEB フォーマットを用いるが、
ローカルの DEB ファイルからソフトウェアをインストールすることは稀だ。
これらのディストロは通常コマンドラインや GUI 上から使えるパッケージ・マネージャがあって
(例: `apt-get`、`aptitude`、Synaptic など)、インストールはそれらから行う。
ターミナル上から以下を実行すると `sbt` をインストールできる (superuser 権限を必要とするため、`sudo` を使っている)。

    echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
    sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
    sudo apt-get update
    sudo apt-get install sbt

パッケージ・マネージャは設定されたリポジトリに指定されたパッケージがあるか確認しにいく。
sbt のバイナリは Bintray にて公開されており、都合の良いことに Bintray は APT リポジトリを提供している。
そのため、このリポジトリをパッケージ・マネージャに追加しさえすればよい。

>**注意** [sbt/website#127][website127] で報告されている通り、https を使用するとセグメンテーション違反が発生する場合がある。

`sbt` を最初にインストールした後は、このパッケージは `aptitude` や Synaptic
上から管理することができる (パッケージ・キャッシュの更新を忘れずに)。
追加された APT リポジトリは「システム設定 -> ソフトウェアとアップデート -> 他のソフトウェア」 の一番下に表示されているはずだ:

![Ubuntu Software & Updates Screenshot](../files/ubuntu-sources.png "Ubuntu Software & Updates Screenshot")

### Red Hat Enterprise Linux 及びその他の RPM ベースのディストリビューション

[RPM][RPM] は sbt による公式パッケージだ。

Red Hat Enterprise Linux 及びその他の RPM ベースのディストリビューションは RPM フォーマットを用いる。
ターミナル上から以下を実行すると `sbt` をインストールできる (superuser 権限を必要とするため、`sudo` を使っている)。

    curl https://bintray.com/sbt/rpm/rpm | sudo tee /etc/yum.repos.d/bintray-sbt-rpm.repo
    sudo yum install sbt

sbt のバイナリは Bintray にて公開されており、Bintray は RPM リポジトリを提供する。
そのため、このリポジトリをパッケージ・マネージャに追加する必要がある。

> **注意:** これらのパッケージに問題があれば、
> [sbt-launcher-package](https://github.com/sbt/sbt-launcher-package)
> プロジェクトに報告してほしい。

### Gentoo

公式には sbt の ebuild は提供されていないが、
バイナリから sbt をマージする [ebuild](https://github.com/whiter4bbit/overlays/tree/master/dev-java/sbt-bin) が公開されているようだ。
この ebuild を使って sbt をマージするには:

    mkdir -p /usr/local/portage && cd /usr/local/portage
    git clone git://github.com/whiter4bbit/overlays.git
    echo "PORTDIR_OVERLAY=\$PORTDIR_OVERLAY /usr/local/portage/overlays" >> /etc/make.conf
    emerge sbt-bin

> **注意:** この ebuild に関する問題は
> [こちら](https://github.com/whiter4bbit/overlays/issues)
> へ報告してほしい。

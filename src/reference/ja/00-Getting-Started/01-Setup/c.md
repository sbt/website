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

Linux への sbt のインストール
--------------------------

### **cs setup** を用いた sbt のインストール

[Install](https://www.scala-lang.org/download/) に従い Coursier を用いて Scala をインストールする。これは最新の安定版の `sbt` を含む。

### SDKMAN からのインストール

JDK と sbt をするのに、[SDKMAN](https://sdkman.io/) の導入を検討してほしい。

@@snip [install.sh]($root$/src/includes/install.sh) {}

Coursier もしくは SDKMAN を使うことには 2つの利点がある。

1. [「闇鍋 OpenJDK ビルド」](https://mail.openjdk.java.net/pipermail/jdk8u-dev/2019-May/009330.html)と揶揄されているディストロ管理の JDK ではなく、Eclipse Adoptium が出している公式のパッケージをインストールできる。
2. sbt の全ての JAR ファイルを含んだ `tgz` パッケージをインストールできる (DEB と RPM版は帯域の節約のために JAR ファイルが含まれていない)。

### JDK のインストール

まず JDK をインストールする必要がある。**Eclipse Adoptium Temurin JDK 8**、**JDK 11**、もしくは **JDK 17** を推奨する。

パッケージ名はディストリビューションによって異なる。例えば、Ubuntu xenial (16.04LTS) には [openjdk-8-jdk](https://packages.ubuntu.com/hu/xenial/openjdk-8-jdk) がある。Redhat 系は [java-1.8.0-openjdk-devel][openjdk-devel] と呼んでいる。

### ユニバーサルパッケージからのインストール

[ZIP][ZIP] か [TGZ][TGZ] をダウンロードしてきて解凍する。

### Ubuntu 及びその他の Debian ベースの Linux ディストリビューション

[DEB][DEB] は sbt による公式パッケージだ。

Ubuntu 及びその他の Debian ベースのディストリビューションは DEB フォーマットを用いるが、
ローカルの DEB ファイルからソフトウェアをインストールすることは稀だ。
これらのディストロは通常コマンドラインや GUI 上から使えるパッケージ・マネージャがあって
(例: `apt-get`、`aptitude`、Synaptic など)、インストールはそれらから行う。
ターミナル上から以下を実行すると `sbt` をインストールできる (superuser 権限を必要とするため、`sudo` を使っている)。

    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | sudo tee /etc/apt/sources.list.d/sbt_old.list
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo apt-key add
    sudo apt-get update
    sudo apt-get install sbt

パッケージ・マネージャは設定されたリポジトリに指定されたパッケージがあるか確認しにいく。
このリポジトリをパッケージ・マネージャに追加しさえすればよい。

`sbt` を最初にインストールした後は、このパッケージは `aptitude` や Synaptic
上から管理することができる (パッケージ・キャッシュの更新を忘れずに)。
追加された APT リポジトリは「システム設定 -> ソフトウェアとアップデート -> 他のソフトウェア」 の一番下に表示されているはずだ:

![Ubuntu Software & Updates Screenshot](../files/ubuntu-sources.png "Ubuntu Software & Updates Screenshot")

**注意**: Ubuntu で  `Server access Error: java.lang.RuntimeException: Unexpected error: java.security.InvalidAlgorithmParameterException: the trustAnchors parameter must be non-empty url=https://repo1.maven.org/maven2/org/scala-sbt/sbt/1.1.0/sbt-1.1.0.pom` という SSL エラーが多く報告されている。[cert-bug][cert-bug] などによると、これは OpenJDK 9 が `/etc/ssl/certs/java/cacerts` に PKCS12 フォーマットを採用したことに起因するらしい。<https://stackoverflow.com/a/50103533/3827> によるとこの問題は Ubuntu Cosmic (18.10) で修正されているが、Ubuntu Bionic LTS (18.04) はリリース待ちらしい。回避策も Stackoverflow を参照。

### Red Hat Enterprise Linux 及びその他の RPM ベースのディストリビューション

[RPM][RPM] は sbt による公式パッケージだ。

Red Hat Enterprise Linux 及びその他の RPM ベースのディストリビューションは RPM フォーマットを用いる。
ターミナル上から以下を実行すると `sbt` をインストールできる (superuser 権限を必要とするため、`sudo` を使っている)。

    # remove old Bintray repo file
    sudo rm -f /etc/yum.repos.d/bintray-rpm.repo
    curl -L https://www.scala-sbt.org/sbt-rpm.repo > sbt-rpm.repo
    sudo mv sbt-rpm.repo /etc/yum.repos.d/
    sudo yum install sbt

On Fedora (31 and above), use `bintray-sbt-rpm.repo`

    # remove old Bintray repo file
    sudo rm -f /etc/yum.repos.d/bintray-rpm.repo
    curl -L https://www.scala-sbt.org/sbt-rpm.repo > sbt-rpm.repo
    sudo mv sbt-rpm.repo /etc/yum.repos.d/
    sudo dnf install sbt

> **注意:** これらのパッケージに問題があれば、
> [sbt](https://github.com/sbt/sbt)
> プロジェクトに報告してほしい。

### Gentoo

公式には sbt の ebuild は提供されていないが、
バイナリから sbt をマージする [ebuild](https://github.com/whiter4bbit/overlays/tree/master/dev-java/sbt-bin) が公開されているようだ。
この ebuild を使って sbt をマージするには:

    emerge dev-java/sbt

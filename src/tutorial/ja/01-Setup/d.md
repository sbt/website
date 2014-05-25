---
out: Manual-Installation.html
---

  [sbt-launch.jar]: $launcher_release_base$$app_version$/sbt-launch.jar

手動インストール
--------------

[sbt-launch.jar][sbt-launch.jar] をダウンロードして起動スクリプトを書くことで手動でインストールできる。

### Unix

[sbt-launch.jar][sbt-launch.jar] を `~/bin` に置く。

`~/bin/sbt` に以下のスクリプトを作成して JAR を起動する:

```
SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M"
java \$SBT_OPTS -jar `dirname \$0`/sbt-launch.jar "\$@"
```

スクリプトを実行可能にする:

```
\$ chmod u+x ~/bin/sbt
```

### Windows

ターミナルの種類と Cygwin を使っているかによって Windows
環境での手動インストールは変わってくる。
いずれにせよ、バッチファイルもしくはスクリプトにパスを通すことでコマンドプロンプトから
`sbt` と打ち込めば sbt が起動できるようにする。
あとは、必要に応じて JVM セッティングを調整する。

#### 非Cygwin

標準の Windows ターミナルを使っている非Cygwin ユーザは、以下のバッチファイル `sbt.bat` を作る:

```
set SCRIPT_DIR=%~dp0
java -Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M -jar "%SCRIPT_DIR%sbt-launch.jar" %*
```

そしてダウンロードしてきた [sbt-launch.jar][sbt-launch.jar] はバッチファイルと同じディレクトリに置く。

#### 標準 Windows ターミナルを使った Cygwin

標準 Windows ターミナルとともに Cygwin を使っている場合は、`~/bin/sbt` という名前で bash スクリプトを作る:

```
SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M"
java \$SBT_OPTS -jar sbt-launch.jar "\$@"
```

sbt-launch.jar の所はダウンロードしてきた [sbt-launch.jar][sbt-launch.jar] へのパスで置き換える。
必要ならば `cygpath` を使う。スクリプトを実行可能にする:

```
\$ chmod u+x ~/bin/sbt
```

#### Ansi ターミナルを使った Cygwin

Ansi ターミナル (Ansi エスケープをサポートして、stty によって設定できる) を使って Cygwin を実行している場合は、`~/bin/sbt` という名前で bash スクリプトを作る:

```
SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M"
stty -icanon min 1 -echo > /dev/null 2>&1
java -Djline.terminal=jline.UnixTerminal -Dsbt.cygwin=true \$SBT_OPTS -jar sbt-launch.jar "\$@"
stty icanon echo > /dev/null 2>&1
```

sbt-launch.jar の所はダウンロードしてきた [sbt-launch.jar][sbt-launch.jar] へのパスで置き換える。
必要ならば `cygpath` を使う。スクリプトを実行可能にする:

```
\$ chmod u+x ~/bin/sbt
```

scala コンソールでバックスペースが正しく動作するためには、バックスペースが
stty で設定された消去文字を送信している必要がある。
デフォルトの Cygwin のターミナル (mintty) を使っていて、
消去文字が Cygwin のデフォルトである ^H を使っている場合は
Options -> Keys "Backspace sends ^H"  の設定をチェックする必要がある。

> **注意:** 他の設定は現在サポートしていない。
> 何か良い方法があれば [pull request](https://github.com/sbt/sbt/blob/0.13/CONTRIBUTING.md)
> を送ってほしい。

訳注:

 - 32bitOSの場合 `-Xmx1536M` だとJVMのメモリの制限によりうまくいかないので、`-Xmx1024M` などに減らす必要がある。
 - sbt0.13.0以降、windows の場合は、 `-Dinput.encoding=Cp1252` を指定しないと矢印キーでの履歴参照などが文字化けするようなので、設定してください。 [詳しい議論などはここを参照](https://github.com/sbt/sbt/issues/871)

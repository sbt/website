[sbt-launch.jar]: http://typesafe.artifactoryonline.com/typesafe/ivy-releases/org.scala-tools.sbt/sbt-launch/0.11.0/sbt-launch.jar

# セットアップ

[[前|Welcome]] _始める sbt 2/14 ページ_ [[次|Hello]]

# 概要

sbt プロジェクトを作るには、以下の手順をたどる:
 
 - sbt をインストールして起動スクリプトを作る。
 - 簡単な [[hello world|Getting Started Hello]] プロジェクトをセットアップする。
   - ソースファイルの入ったプロジェクトディレクトリを作る。
   - ビルド定義を作る。
 - [[実行する|Running]]を読んで、sbt の走らせ方を覚える。
 - [[.sbt ビルド定義|Basic Def]]を読んで、ビルド定義についてもっと詳しく習う。

# sbt のインストール

二つのファイルがいる。[sbt-launch.jar] とそれを実行するスクリプトだ。

## Unix

[sbt-launch.jar] をダウンロードして `~/bin` に置く。

以下の内容を、`sbt` という名前のファイルに書いて `~/bin` に置けば、jar を実行するスクリプトのできあがり:

```text
java -Xmx512M -jar `dirname $0`/sbt-launch.jar "$@"
```

スクリプトを実行ファイルにする:

```text
$ chmod u+x ~/bin/sbt
```

## Windows

バッチファイル `sbt.bat` を作成する:

```text
set SCRIPT_DIR=%~dp0
java -Xmx512M -jar "%SCRIPT_DIR%sbt-launch.jar" %*
```

次に、[sbt-launch.jar] をバッチファイルと同じディレクトリにダウンロードしてくる。コマンドプロンプト上でどのディレクトリからでも `sbt` と打てば `sbt` が実行できるように `sbt.bat` にパスを通す。

## コツと注意

`sbt` の実行に上手くいかない場合は、[[Setup Notes]] のターミナルの文字エンコーディング、HTTP プロキシ、JVM のオプションにかんする説明を参照する。

## 続いては

次は、[[簡単なプロジェクトの作成|Hello]]だ。

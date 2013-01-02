---
title: セットアップ
layout: default
---

[sbt-launch.jar]: http://typesafe.artifactoryonline.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/0.12.1/sbt-launch.jar

# セットアップ

[前](../) _始める sbt 2/14 ページ_ [次](../hello)

# 概要

sbt プロジェクトを作るには、以下の手順をたどる:

 - sbt をインストールして起動スクリプトを作る。
 - 簡単な [hello world](../hello) プロジェクトをセットアップする。
   - ソースファイルの入ったプロジェクトディレクトリを作る。
   - ビルド定義を作る。
 - [実行する](../running)を読んで、sbt の走らせ方を覚える。
 - [.sbt ビルド定義](../basic-def)を読んで、ビルド定義についてもっと詳しく習う。

# sbt のインストール

二つのファイルがいる。[sbt-launch.jar] とそれを実行するスクリプトだ。

## Unix

[sbt-launch.jar] をダウンロードして `~/bin` に置く。

以下の内容を、`sbt` という名前のファイルに書いて `~/bin` に置けば、jar を実行するスクリプトのできあがり:

    java -Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=384M -jar `dirname $0`/sbt-launch.jar "$@"

スクリプトを実行ファイルにする:

    $ chmod u+x ~/bin/sbt

## Windows

バッチファイル `sbt.bat` を作成する:

    set SCRIPT_DIR=%~dp0
    java -Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=384M -jar "%SCRIPT_DIR%sbt-launch.jar" %*

次に、[sbt-launch.jar] をバッチファイルと同じディレクトリにダウンロードしてくる。コマンドプロンプト上でどのディレクトリからでも `sbt` と打てば `sbt` が実行できるように `sbt.bat` にパスを通す。

## コツと注意

`sbt` の実行に上手くいかない場合は、[[Setup Notes]] のターミナルの文字エンコーディング、HTTP プロキシ、JVM のオプションにかんする説明を参照する。

訳注:

 - 32bitOSの場合 `-Xmx1536M` だとJVMのメモリの制限によりうまくいかないので、`-Xmx1024M` などに減らす必要がある。
 - [原文のページ](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html)には、macports、homebrew、gentoo、apt、windows用のmsiのインストーラなどの方法も載っていて、ここに書いてある以外の方法でもインストールすることが可能です。
 - windows の場合は、msi を使用してインストールすると、sbtのコンソールでカラー表示が可能になるので、カラーにしたい方はそちらがオススメです。(windowsの場合は上記に書いてある手動でバッチファイルを置く方法では、カラー表示にならない)

## 続いては

次は、[簡単なプロジェクトの作成](../hello)だ。

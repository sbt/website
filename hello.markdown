---
title: Hello, World
layout: default
---

# Hello, World

[前](../setup) _始める sbt 3/14 ページ_ [次](../directories)

このページは、君が[sbt をインストール](../setup)したことを前提にする。

## ソースコードの入ったプロジェクトディレクトリを作る

一つのソースファイルを含むディレクトリでも、一応有効な sbt プロジェクトとなりうる。試しに、作って実行してみよう:

<pre>
  $ mkdir hello
  $ cd hello
  $ echo 'object Hi { def main(args: Array[String]) = println("Hi!") }' > hw.scala
  $ sbt
  ...
  > run
  ...
  Hi!
</pre>

この例では、sbt は純粋に convention（デフォルトの慣例）だけを使って動作している。
sbt は以下を自動的に検知する:

 - ベースディレクトリ内のソース
 - `src/main/scala` か `src/main/java` 内のソース
 - `src/test/scala` か `src/test/java` 内のテスト
 - `src/main/resources` か `src/test/resources` 内のデータファイル
 - `lib` 内の jar ファイル

デフォルトでは、sbt は sbt 自身が使っている Scala のバージョンを使ってプロジェクトをビルドする。

`sbt run` を用いてプロジェクトを実行したり、`sbt console` を用いて [Scala REPL](http://www.scala-lang.org/node/2097) に入ることができる。`sbt console` は君のプロジェクトにクラスパスを通すから、
君のプロジェクトのコードを使った Scala の例をライブで試すことができる。

## ビルド定義

ほとんどのプロジェクトは何らかの手動設定が必要だ。基本的なビルド設定は `build.sbt` というファイルに書かれ、
プロジェクトのベースディレクトリ (base directory) に置かれる。

例えば、君のプロジェクトが `hello` ディレクトリにあるなら、`hello/build.sbt` をこんな感じで書く:

<pre>
name := "hello"

version := "1.0"

scalaVersion := "2.9.1"
</pre>

[.sbt ビルド定義](../basic-def)で、`build.sbt` の書き方をもっと詳しく説明する。

君のプロジェクトを jar ファイルにパッケージ化する予定なら、最低でも `build.sbt` に name と version は書いておこう。

## sbt バージョンの設定

`hello/project/build.properties` というファイルを作ることで、特定のバージョンの sbt を強制することができる。
このファイルに、以下のように書く:

    sbt.version=0.12.1

0.10 以降は、sbt はリリース間で 99% ソースの互換性を持たせてある。
だけど、sbt バージョンを `project/build.properties` に設定することで混乱を予防することできる。

# 続いては

sbt プロジェクトの[ファイルとディレクトリのレイアウト](../directories)についてみてみよう。

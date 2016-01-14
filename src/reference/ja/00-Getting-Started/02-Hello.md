---
out: Hello.html
---

  [Basic-Def]: Basic-Def.html
  [Setup]: Setup.html

Hello, World
------------

このページは、既に[sbt をインストール][Setup]したことを前提とする。

### ソースコードの入ったプロジェクトディレクトリを作る

一つのソースファイルを含むディレクトリでも、一応有効な sbt プロジェクトとなりうる。試しに、`hello` ディレクトリを作って、以下の内容の `hw.scala` というファイルを作成する:

```scala
object Hi {
  def main(args: Array[String]) = println("Hi!")
}
```

次に `hello` ディレクトリ内から sbt を起動して sbt のインタラクティブコンソールに `run` と入力する。
Linux、Mac OS X の場合、コマンドは以下のようになる:

```
\$ mkdir hello
\$ cd hello
\$ echo 'object Hi { def main(args: Array[String]) = println("Hi!") }' > hw.scala
\$ sbt
...
> run
...
Hi!
```

この例では、sbt はただデフォルトの規約によって動作している。sbt は以下のものを自動的に検知する:

 - ベースディレクトリにあるソースファイル　
 - `src/main/scala` か `src/main/java` 内のソースファイル
 - `src/test/scala` か `src/test/java` 内のテストソースファイル
 - `src/main/resources` か `src/test/resources` 内のデータファイル
 - `lib` 内の jar ファイル

デフォルトでは、sbt は sbt 自身が使っている Scala のバージョンを使ってプロジェクトをビルドする。

`sbt run` でプロジェクトを実行したり、`sbt console` で [Scala REPL](http://www.scala-lang.org/node/2097) に入ることができる。
`sbt console` は君のプロジェクトにクラスパスを通すので、プロジェクトのコードを使った Scala コード例をその場で試すことができる。

### ビルド定義

ほとんどのプロジェクトでは何らかの手動設定が必要になるだろう。
基本的なビルド設定方法はプロジェクトのベースディレクトリに `build.sbt` というファイルとして配置されるものだ。
例えば、君のプロジェクトが `hello` ディレクトリにある場合、`hello/build.sbt` はこんな感じになる：

```scala
lazy val root = (project in file(".")).
  settings(
    name := "hello",
    version := "1.0",
    scalaVersion := "$example_scala_version$"
  )
```

[.sbt ビルド定義][Basic-Def]で、`build.sbt` の書き方をもっと詳しく説明する。
もし君のプロジェクトを jar ファイルにパッケージ化するつもりなら、最低でも `build.sbt` に name と version は書いておこう。

### sbt バージョンの設定

`hello/project/build.properties` というファイルを作ることで、特定のバージョンの sbt を強制することができる。
このファイルに、以下のように書く:

```
sbt.version=$app_version$
```

sbt はリリース間で 99% ソースコード互換性を維持しているが、
sbt バージョンを `project/build.properties` に設定しておくことで、不要な混乱を避けることができるだろう。

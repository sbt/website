---
out: Hello.html
---

  [Basic-Def]: Basic-Def.html
  [Setup]: Setup.html
  [Running]: Running.html

Hello, World
------------

このページは、既に[sbt をインストール][Setup]したことを前提とする。

### sbt new コマンド

sbt 0.13.13 以降を使っている場合は、sbt `new`
コマンドを使って手早く簡単な Hello world ビルドをセットアップすることができる。
以下をターミナルから打ち込む。

```
\$ sbt new sbt/scala-seed.g8
....
Minimum Scala build.

name [My Something Project]: hello

Template applied in ./hello
```

プロジェクト名を入力するプロンプトが出てきたら `hello` と入力する。

これで、`hello` ディレクトリ以下に新しいプロジェクトができた。

### アプリの実行

次に `hello` ディレクトリ内から sbt を起動して sbt のシェルから
`run` と入力する。Linux や OS X の場合、コマンドは以下のようになる:

```
\$ cd hello
\$ sbt
...
> run
...
[info] Compiling 1 Scala source to /xxx/hello/target/scala-2.12/classes...
[info] Running example.Hello
hello
```

[後で][Running]他のタスクもみていく。

### sbt シェルの終了

sbt シェルを終了するには、`exit` と入力するか、Ctrl+D (Unix) か Ctrl+Z (Windows) を押す。

```
> exit
```

### ビルド定義

基本的なビルド設定方法はプロジェクトのベースディレクトリに `build.sbt` というファイルとして配置されるものだ。
例えば、プロジェクトが `hello` ディレクトリにある場合、`hello/build.sbt` はこんな感じになる：

```scala
import Dependencies._

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "$example_scala_version$",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Hello",
    libraryDependencies += scalaTest % Test
  )
```

[.sbt ビルド定義][Basic-Def]で、`build.sbt` の書き方をもっと詳しく説明する。
もしプロジェクトを jar ファイルにパッケージ化するつもりなら、最低でも `build.sbt` に name と version は書いておこう。

### sbt バージョンの設定

`hello/project/build.properties` というファイルを作ることで、特定のバージョンの sbt を強制することができる。
このファイルに、以下のように書く:

```
sbt.version=$app_version$
```

sbt はリリース間で 99% ソースコード互換性を維持しているが、
sbt バージョンを `project/build.properties` に設定しておくことで、不要な混乱を避けることができるだろう。

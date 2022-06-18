---
out: Organizing-Build.html
---

  [Basic-Def]: Basic-Def.html
  [Task-Graph]: Task-Graph.html
  [Using-Plugins]: Using-Plugins.html
  [Library-Dependencies]: Library-Dependencies.html
  [Multi-Project]: Multi-Project.html
  [Plugins]: ../../reference/Plugins.html

ビルドの整理
-----------

このページではビルド構造の整理について説明する。

このガイドの前のページ、特に
[build.sbt][Basic-Def]、
[タスク・グラフ][Task-Graph]、
[ライブラリ依存性][Library-Dependencies]、
そして[マルチプロジェクト・ビルド][Multi-Project]を理解していることが必要になる。

### sbt は再帰的だ

`build.sbt` は sbt の実際の動作を隠蔽している。
sbt のビルドは、Scala コードにより定義されている。そのコード自身もビルドされなければいけない。
当然これも sbt でビルドされる。sbt でやるより良い方法があるだろうか？

`project` ディレクトリは、ビルドをビルドする方法を記述した**ビルドの中のビルド**だ。
これらのビルドを区別するために、一番上のビルドを**プロパービルド** (proper build) 、
`project` 内のビルドを**メタビルド** (meta-build) と呼んだりする。
メタビルド内のプロジェクトは、他のプロジェクトができる全てのことをこなすことができる。
つまり、**ビルド定義もまた sbt プロジェクトなのだ**。

この入れ子構造は永遠に続く。`project/project` ディレクトリを作ることで
ビルド定義のビルド定義プロジェクトをカスタム化することができる。

以下に具体例で説明する:

```
hello/                     # ビルドのルート・プロジェクトのベースディレクトリ

    Hello.scala            # ビルドのルート・プロジェクトのソースファイル
                           # （src/main/scala に入れることもできる）

    build.sbt              # build.sbt は、project/ 内のメタビルドの
                           # ルート・プロジェクトのソースの一部となる。
                           # つまり、プロパービルドのビルド定義

    project/               # メタビルドのルート・プロジェクトのベースディレクトリ
        Dependencies.scala # メタビルドのルート・プロジェクトのソースファイル、
                           # つまり、ビルド定義のソースファイル。
                           # プロパービルドのビルド定義

        assembly.sbt       # これは、project/project 内のメタメタビルドの
                           # ルート・プロジェクトのソースの一部となり、
                           # ビルド定義のビルド定義となる

        project/           # メタメタビルドのルート・プロジェクトのベースディレクトリ

            MetaDeps.scala # project/project/ 内のメタメタビルドの
                           # ルート・プロジェクトのソースファイル
```

_心配しないでほしい！_ 普通はこういうことをする必要は全くない。
しかし、原理を理解しておくことはきっと役立つことだろう。

ちなみに、`.scala` や `.sbt` の拡張子で終わっていればどんなファイル名でもよく、`build.sbt` や `Dependencies.scala` と命名するのは慣例にすぎない。
これは複数のファイルを使うことができるということも意味する。

### ライブラリ依存性を一箇所にまとめる

`project` 内の任意の `.scala` ファイルがビルド定義の一部となることを利用する一つの例として
`project/Dependencies.scala` というファイルを作ってライブラリ依存性を一箇所にまとめるということができる。

```scala
import sbt._

object Dependencies {
  // Versions
  lazy val akkaVersion = "$example_akka_version$"

  // Libraries
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % akkaVersion
  val specs2core = "org.specs2" %% "specs2-core" % "$example_specs2_version$"

  // Projects
  val backendDeps =
    Seq(akkaActor, specs2core % Test)
}
```

この `Dependencies` は `build.sbt` 内で利用可能となる。
定義されている `val` が使いやすいように `Dependencies._` を import しておこう。

```scala
import Dependencies._

ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "$example_scala_version$"

lazy val backend = (project in file("backend"))
  .settings(
    name := "backend",
    libraryDependencies ++= backendDeps
  )
```

マルチプロジェクトでのビルド定義が肥大化して、サブプロジェクト間で同じ依存ライブラリを持っているかを保証したくなったとき、このようなテクニックは有効だ。

### いつ `.scala` ファイルを使うべきか

`.scala` ファイルでは、トップレベルの class や object 定義を含む Scala コードを自由に記述できる。

推奨される方法はマルチプロジェクトを定義する `build.sbt` ファイル内にほとんどのセッティングを定義し、
`project/*.scala` ファイルはタスクの実装や、共有したい値やキーを定義するのに使うことだ。
また `.scala` ファイルを使うかどうかの判断には、君や君のチームがどれくらい Scala に慣れているかということも関係するだろう。

### auto plugin を定義する

上級ユーザ向けのビルドの整理方法として、`project/*.scala` 内に専用の auto plugin を書くという方法がある。
連鎖プラグイン (triggered plugin) を定義することで auto plugin を全サブプロジェクトにカスタムタスクやコマンドを追加する手段として使うことができる。

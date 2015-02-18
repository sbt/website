---
out: Organizing-Build.html
---

  [Basic-Def]: Basic-Def.html
  [More-About-Settings]: More-About-Settings.html
  [Using-Plugins]: Using-Plugins.html
  [Library-Dependencies]: Library-Dependencies.html
  [Multi-Project]: Multi-Project.html
  [Plugins]: ../../reference/Plugins.html

ビルドの整理
-----------

このページではビルド構造の整理について説明する。

このガイドの前のページ、特に
[build.sbt][Basic-Def]、
[ライブラリ依存性][Library-Dependencies]、
そして[マルチプロジェクト・ビルド][Multi-Project]を理解していることが必要になる。

### sbt は再帰的だ

`build.sbt` は sbt の実際の動作を隠蔽している。
sbt のビルドは、Scala コードにより定義されている。そのコード自身もビルドされなければいけない。
当然これも sbt でビルドされる。

`project` ディレクトリは現行のビルドのビルド方法を記述した**ビルドの中のビルド**だ。
これらのビルドを区別するために、一番上のビルドは**プロパービルド** (proper build) と呼んで、
`project` 内のビルドは**メタビルド** (meta-build) という用語で呼ぶことがある。
メタビルド内のプロジェクトは、他のプロジェクトができる全てのことを（理論的には）こなすことができる。
つまり、**ビルド定義は sbt プロジェクトである**ということだ。

この入れ子構造は永遠に続く。`project/project` ディレクトリを作ることで
ビルド定義のビルド定義プロジェクトをカスタム化することができる。

以下に具体例で説明する:

```
hello/                  # ビルドのルート・プロジェクトのベースディレクトリ

    Hello.scala         # ビルドのルート・プロジェクトのソースファイル  
                        # （src/main/scala に入れることもできる）

    build.sbt           # build.sbt は、project/ 内のメタビルドの
                        # ルート・プロジェクトのソースの一部となる。
                        # つまり、プロパービルドのビルド定義

    project/            # メタビルドのルート・プロジェクトのベースディレクトリ
     
        Build.scala     # メタビルドのルート・プロジェクトのソースファイル、
                        # つまり、ビルド定義のソースファイル。
                        # プロパービルドのビルド定義

        build.sbt       # これは、project/project 内のメタメタビルドの
                        # ルート・プロジェクトのソースの一部となり、
                        # ビルド定義のビルド定義となる
               
        project/        # メタメタビルドのルート・プロジェクトのベースディレクトリ

            Build.scala # project/project/ 内のメタメタビルドの
                        # ルート・プロジェクトのソースファイル
```

普通はこういうことをする必要は全く無いので、安心してほしい！
だけど、原理を理解すると役立つことがある。

ちなみに、`.scala` や `.sbt` で終わる全てのファイルが用いられ、
`build.sbt` や `Build.scala` と命名するのは慣例にすぎない。
これは複数のファイルを使っていいということも意味する。

### ライブラリ依存性をまとめる

`project` 内の `.scala` ファイルがビルド定義の一部となることを利用する一つの例として
`project/Dependencies.scala` というファイルを作ってライブラリ依存性を一箇所にまとめるということができる。

```scala
import sbt._

object Dependencies {
  // Versions
  lazy val akkaVersion = "2.3.8"

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

lazy val commonSettings = Seq(
  version := "0.1.0",
  scalaVersion = "$example_scala_version$"
)

lazy val backend = (project in file("backend")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies += backendDeps
  )
```

マルチプロジェクト・ビルドが大きくなってきて、サブプロジェクト間の一貫性を保証したいときに
このようなテクニックが有用になってくる。

### いつ `.scala` ファイルを使うか

`.scala` ファイルでは、クラスやオブジェクト定義を含む Scala コードを自由に書ける。


推奨される方法はセッティングは基本的にマルチプロジェクト `build.sbt` ファイル内で行って、
`project/*.scala` ファイルはタスクの実装や、共有したい値やキーを定義するのに使うことだ。
`.scala` ファイルの利用は君または、君のチームがどれだけ Scala 慣れしてるかにもよる。

### auto plugin を定義する

上級ユーザ向けのビルドの整理方法として、`project/*.scala`
内に専用の auto plugin を書くという方法がある。
連鎖プラグイン (triggered plugin) を定義することで auto plugin
を全サブプロジェクトにカスタムタスクやコマンドを追加する手段として使うことができる。

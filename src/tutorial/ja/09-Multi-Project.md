---
out: Multi-Project.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Directories]: Directories.html
  [Organizing-Build]: Organizing-Build.html

マルチプロジェクト・ビルド
----------------------

このページでは、一つのプロジェクトで複数のプロジェクトを管理する方法を紹介する。

このガイドの前のページ、特に
[build.sbt][Basic-Def] を理解していることが必要になる。

### 複数のプロジェクト

単一のビルドに複数のプロジェクトを入れておくと、
プロジェクト間に依存性がある場合や、
プロジェクトが同時に変更されることが多い場合などで便利だ。

ビルド内のサブプロジェクトは、それぞれに独自の `src/main/scala` を持ち、
`package` を実行すると独自の jar ファイルを生成するなど、
普通のプロジェクト同様に振る舞う。

個々のプロジェクトは lazy val を用いて [Project](../../api/sbt/Project.html) 型の値を宣言することで定義される。例として、以下のようなものがプロジェクトだ:

```scala
lazy val util = project

lazy val core = project
```

変数名はプロジェクトの ID 及びベースディレクトリの名前になる。 ID はコマンドラインからプロジェクトを指定する時に用いられる。ベースディレクトリは以下の様な関数を呼び出す事で変更出来る。上記の例と同じ結果になる記述を明示的に書くと、以下のようになる。

```scala
lazy val util = project.in(file("util"))

lazy val core = project in file("core")
```

#### 共通のセッティング

複数のプロジェクト間に共通のセッティングを抜き出すには、
`commonSettings` という名前で列を作って、
各プロジェクトから `settings` メソッドを呼べばいい。
可変引数を受け取るメソッドに列を渡すのに `_*` が必要なことに注意。

```scala
lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0",
  scalaVersion := "$example_scala_version$"
)

lazy val core = (project in file("core")).
  settings(commonSettings: _*).
  settings(
    // other settings
  )

lazy val util = (project in file("util")).
  settings(commonSettings: _*).
  settings(
    // other settings
  )
```

これで `version` を一箇所で変更すれば、再読み込み後に全サブプロジェクトに反映されるようになった。

### 依存関係

個々のプロジェクトのビルドを他のプロジェクトと完全に独立した形で行うこともできる。しかし、大抵プロジェクトというのは他のプロジェクトと何らかの形で依存関係を持つ。ここには集約かクラスパス依存性といった2種類の依存関係が存在する:

#### 集約

集約は、集約するプロジェクトを走らせる際に集約されるプロジェクトも同時に走らせる必要がある、といった関係のことである。例えば、以下のような例がある。

```scala
lazy val root = (project in file(".")).
  aggregate(util, core)

lazy val util = project

lazy val core = project
```

上の例では、`root` プロジェクトが `util` と `core` を集約している。この状態で sbt を起動してコンパイルしてみよう。3つのプロジェクトが全てコンパイルされることが分かると思う。

_集約プロジェクト内で_（この場合は、ルートの `hello` プロジェクトで）、
タスクごとに集約をコントロールすることができる。
例えば、`update` タスクの集約を以下のようにして回避できる:

```scala
lazy val root = (project in file(".")).
  aggregate(util, core).
  settings(
    aggregate in update := false
  )

[...]
```

`aggregate in update` は、`update` タスクにスコープ付けされた `aggregate` キーだ
（[スコープ][Scopes]参照）。

注意: 集約は、集約されるタスクを順不同に並列実行する。

#### クラスパス依存性

プロジェクトは、他のプロジェクトのコードに依存することができる。
これは、`dependsOn` メソッドを呼び出すことで実現する。
例えば、`core` に `util` のクラスパスが必要な場合は、 `core` の定義を次のように書く:

```scala
lazy val core = project.dependsOn(util)
```

これで `core` 内のコードから `util` のクラスを利用することができる。
これは、プロジェクトをコンパイルするときの順序も作り出す。
この場合、`core` がコンパイルされる前に、`util` が更新（update）され、
コンパイルされる必要がある。

複数のプロジェクトに依存するには、`dependsOn(bar, baz)` というふうに、
`dependsOn` に複数の引数を渡せばいい。

##### コンフィギュレーションごとのクラスパス依存性

`foo dependsOn(bar)` は、`foo` の `Compile` コンフィギュレーションが
`bar` の `Compile` コンフィギュレーションに依存することを意味する。
これを明示的に書くと、`dependsOn(bar % "compile->compile")` となる。

この `"compile->compile"` 内の `->` は、「依存する」という意味だから、
`"test->compile"` は、`foo` の `Test` コンフィギュレーションが
`bar` の `Compile` コンフィギュレーションに依存することを意味する。

`->config` の部分を省くと、`->compile` だと解釈されるため、
`dependsOn(bar % "test")` は、`foo` の `Test` コンフィギュレーションが
`bar` の `Compile` コンフィギュレーションに依存することを意味する。

特に、`Test` が `Test` に依存することを意味する `"test->test"` は役に立つ宣言だ。
これにより、例えば、`bar/src/test/scala` にテストのためのユーティリティコードを
置いておき、それを `foo/src/test/scala` 内のコードから利用することができる。

複数のコンフィギュレーション依存性を宣言する場合は、セミコロンで区切る。
例えば、`dependsOn(bar % "test->test;compile->compile")` と書ける。

### デフォルトルートプロジェクト

もしプロジェクトがルートディレクトリに定義されてなかったら、 sbt はビルド時に他のプロジェクトを集約するデフォルトプロジェクトを勝手に生成する。

プロジェクト `hello-foo` は、`base = file("foo")` と共に定義されているため、
サブディレクトリ `foo` に置かれる。
そのソースは、`foo/Foo.scala` のように `foo` の直下に置かれるか、
`foo/src/main/scala` 内に置かれる。
ビルド定義ファイルを除いては、通常の sbt [ディレクトリ構造][Directories]が `foo` 以下に適用される。

`foo` 内の全ての `.sbt` ファイル、例えば `foo/build.sbt` は、
`hello-foo` プロジェクトにスコープ付けされた上で、ビルド全体のビルド定義に取り込まれる。

ルートプロジェクトが `hello` にあるとき、`hello/build.sbt`、`hello/foo/build.sbt`、
`hello/bar/build.sbt` においてそれぞれ別々のバージョンを定義してみよう（例: `version := "0.6"`）。
次に、インタラクティブプロンプトで `show version` と打ち込んでみる。
以下のように表示されるはずだ（定義したバージョンによるが）:

```
> show version
[info] hello-foo/*:version
[info] 	0.7
[info] hello-bar/*:version
[info] 	0.9
[info] hello/*:version
[info] 	0.5
```

`hello-foo/*:version` は、`hello/foo/build.sbt` 内で定義され、
`hello-bar/*:version` は、`hello/bar/build.sbt` 内で定義され、
`hello/*:version` は、`hello/build.sbt` 内で定義される。
[スコープ付けされたキーの構文][Scopes]を復習しておこう。
それぞれの `version` キーは、`build.sbt` の場所により、
特定のプロジェクトにスコープ付けされている。
だけど、三つの `build.sbt` とも同じビルド定義の一部だ。

`.scala` ファイルは、上に示したように、単にプロジェクトとそのベースディレクトリを列挙するだけの簡単なものにして、
_それぞれのプロジェクトのセッティングは、そのプロジェクトのベースディレクトリ直下の
`.sbt` ファイル内で宣言することができる_。
_全てのセッティングを `.scala` ファイル内で宣言することは義務付けられいるわけではない。_

ビルド定義の全てを単一の `project` ディレクトリ内の場所にまとめるために、
`.scala` ファイル内にセッティングも含めてしまうほうが洗練されていると思うかもしれない。
ただし、これは好みの問題だから、好きにやっていい。

サブプロジェクトは、`project` サブディレクトリや、`project/*.scala` ファイルを持つことができない。
`foo/project/Build.scala` は無視される。

### プロジェクトの切り替え

sbt インタラクティブプロンプトから、`projects` と打ち込むことでプロジェクトの全リストが表示され、
`project <プロジェクト名>` で、現在プロジェクトを選択できる。
`compile` のようなタスクを実行すると、それは現在プロジェクトに対して実行される。
これにより、ルートプロジェクトをコンパイルせずに、サブプロジェクトのみをコンパイルすることができる。

また `subProjectID/compile` のように、他のプロジェクト ID を明示的に指定することで、そのプロジェクトのタスクを実行することもできる。

### Common code

`.sbt` ファイルで定義された値は、他の `.sbt` ファイルからは見えない。 `.sbt` ファイル間のコードを共有するためには、 ビルドルートにある `project/` ディレクトリに Scala ファイルを用意すれば良い。

詳細は[ビルドの整理][Organizing-Build]を見てほしい。

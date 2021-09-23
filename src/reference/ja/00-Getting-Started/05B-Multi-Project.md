---
out: Multi-Project.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Directories]: Directories.html
  [Organizing-Build]: Organizing-Build.html

マルチプロジェクト・ビルド
----------------------

このページでは、一つのビルドで複数のサブプロジェクトを管理する方法を紹介する。
このガイドのこれまでのページを読んでおいてほしい。
特に [build.sbt][Basic-Def] を理解していることが必要になる。

### 複数のサブプロジェクト

一つのビルドに複数の関連するサブプロジェクトを入れておくと、
サブプロジェクト間に依存性がある場合や同時に変更されることが多い場合に便利だ。

ビルド内の個々のサブプロジェクトは、それぞれ独自のソースディレクトリを持ち、
`package` を実行すると独自の jar ファイルを生成するなど、概ね通常のプロジェクトと同様に動作する。

個々のプロジェクトは lazy val を用いて [Project](../../api/sbt/Project.html) 型の値を宣言することで定義される。例として、以下のようなものがプロジェクトだ:

```scala
lazy val util = (project in file("util"))

lazy val core = (project in file("core"))
```

val で定義された名前はプロジェクトの ID 及びベースディレクトリの名前になる。
ID は sbt シェルからプロジェクトを指定する時に用いられる。

ベースディレクトリ名が ID と同じ名前であるときは省略することができる。

```scala
lazy val util = project

lazy val core = project
```

#### ビルドワイド・セッティング

複数プロジェクトに共通なセッティングをくくり出す場合、
セッティングを `ThisBuild` にスコープ付けする。
ただし、右辺値には純粋な値か `Global` もしくは `ThisBuild`
にスコープ付けされたセッティングしか置くことができない、
またサブプロジェクトにスコープ付けされたセッティングがデフォルトで存在しない必要があるというという制約がある。
（[スコープ][Scopes]参照）

```scala
ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "$example_scala_version$"

lazy val core = (project in file("core"))
  .settings(
    // other settings
  )

lazy val util = (project in file("util"))
  .settings(
    // other settings
  )
```

これで `version` を一箇所で変更すれば、再読み込み後に全サブプロジェクトに反映されるようになる。

#### 共通のセッティング

複数プロジェクトに共通なセッティングをくくり出す場合、
`commonSettings` という名前のセッティングの Seq を作って、
それを引数として各プロジェクトの `settings` メソッドを呼び出せばよい。

```scala
lazy val commonSettings = Seq(
  target := { baseDirectory.value / "target2" }
)

lazy val core = (project in file("core"))
  .settings(
    commonSettings,
    // other settings
  )

lazy val util = (project in file("util"))
  .settings(
    commonSettings,
    // other settings
  )
```


### 依存関係

一つのビルドの中の個々のプロジェクトはお互いに完全に独立した状態であってもよいが、
普通、何らかの形で依存関係を持っているだろう。
ここでは集約（`aggregate`）とクラスパス（`classpath`）という二種類の依存関係がある。

#### 集約

集約とは、集約する側のプロジェクトであるタスクを実行するとき、集約される側の複数のプロジェクトでも同じタスクを実行するという関係を意味する。例えば、

```scala
lazy val root = (project in file("."))
  .aggregate(util, core)

lazy val util = (project in file("util"))

lazy val core = (project in file("core"))
```

上の例では、`root` プロジェクトが `util` と `core` を集約している。
この状態で sbt を起動してコンパイルしてみよう。
3 つのプロジェクトが全てコンパイルされることが分かると思う。

_集約プロジェクト内で_（この場合は `root` プロジェクトで）、
タスクごとに集約をコントロールすることができる。
例えば、`update` タスクの集約を以下のようにして回避できる:

```scala
lazy val root = (project in file("."))
  .aggregate(util, core)
  .settings(
    aggregate in update := false
  )

[...]
```

`aggregate in update` は、`update` タスクにスコープ付けされた `aggregate` キーだ
（[スコープ][Scopes]参照）。

注意: 集約は、集約されるタスクを順不同に並列実行する。

#### クラスパス依存性

あるプロジェクトが、他のプロジェクトにあるコードに依存させたい場合、
`dependsOn` メソッドを呼び出して実現すればよい。

例えば、`core` に `util` のクラスパスが必要な場合は `core` の定義を次のように書く:

```scala
lazy val core = project.dependsOn(util)
```

これで `core` 内のコードから `util` の class を利用することができるようになった。

また、これにより `core` がコンパイルされる前に `util` の `update` と `compile` が実行されている必要があるので
プロジェクト間でコンパイル実行が順序付けられることになる。

複数のプロジェクトに依存するには、`dependsOn(bar, baz)` というふうに、
`dependsOn` に複数の引数を渡せばよい。

##### コンフィギュレーションごとのクラスパス依存性

`foo dependsOn(bar)` は、`foo` の `Compile` コンフィギュレーションが
`bar` の `Compile` コンフィギュレーションに依存することを意味する。
これを明示的に書くと、`dependsOn(bar % "compile->compile")` となる。

この `"compile->compile"` 内の `->` は、「依存する」という意味で、
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

### サブプロジェクト間の依存性

多くのファイルとサブプロジェクトを持った巨大なビルドでは
sbt は全てのファイルを監視したり、大量に発生するディスクやシステム I/O
によって高性能とは言えない反応になるかもしれない。

一つの対策として sbt は `compile` を呼び出した時に依存するサブプロジェクトのコンパイルを
行うかどうかを制御する `trackInternalDependencies` と `exportToInternal`
というセッティングがある。両者とも
`TrackLevel.NoTracking`、`TrackLevel.TrackIfMissing`、`TrackLevel.TrackAlways`
という 3つの値を取ることができる。デフォルトは両方とも `TrackLevel.TrackAlways` だ。

`trackInternalDependencies` が `TrackLevel.TrackIfMissing`
に設定されると、sbt は `*.class` ファイル
(`exportJars` が `true` の場合は JAR ファイル)
が一切無い場合を除き自動的に内部 (サブプロジェクト) 依存性をコンパイルすることを止める。

`TrackLevel.NoTracking` に設定すると内部依存性のコンパイルは無視される。
ただし、クラスパスは通常どおり追加されるため、依存性グラフは依存性だと表示する。
この動機は開発時に大量のサブプロジェクトの変更の確認に伴う I/O
オーバーヘッドを回避することにある。全てのサブプロジェクトを `TrackIfMissing`
に設定する方法を以下に示す。

```scala
ThisBuild / trackInternalDependencies := TrackLevel.TrackIfMissing
ThisBuild / exportJars := true

lazy val root = (project in file("."))
  .aggregate(....)
```

`exportToInternal` セッティングは依存された側から内部トラッキングをオプトアウトすることを可能にして、
これを使うことでほとんどのサブプロジェクトは追跡したいが、一部を抜きたいという時に使える。
`trackInternalDependencies` と `exportToInternal` の交叉が実際の追跡レベルを決定する。
以下が 1つのサブプロジェクトをオプトアウトさせる例だ:

```scala
lazy val dontTrackMe = (project in file("dontTrackMe"))
  .settings(
    exportToInternal := TrackLevel.NoTracking
  )
```

### デフォルトルートプロジェクト

もしプロジェクトがルートディレクトリに定義されてなかったら、 sbt はビルド時に他のプロジェクトを集約するデフォルトプロジェクトを勝手に生成する。

プロジェクト `hello-foo` は、`base = file("foo")` と共に定義されているため、
サブディレクトリ `foo` に置かれる。
そのソースは、`foo/Foo.scala` のように `foo` の直下に置かれるか、
`foo/src/main/scala` 内に置かれる。
ビルド定義ファイルを除いては、通常の sbt [ディレクトリ構造][Directories]が `foo` 以下に適用される。

### プロジェクトの切り替え

sbt インタラクティブプロンプトから、`projects` と入力することでプロジェクトの全リストが表示され、
`project <プロジェクト名>` で、カレントプロジェクトを選択できる。
`compile` のようなタスクを実行すると、それはカレントプロジェクトに対して実行される。
これにより、ルートプロジェクトをコンパイルせずに、サブプロジェクトのみをコンパイルすることができる。

また `subProjectID/compile` のように、プロジェクト ID を明示的に指定することで、そのプロジェクトのタスクを実行することもできる。

### 共通のコード

`.sbt` ファイルで定義された値は、他の `.sbt` ファイルからは見えない。 `.sbt` ファイル間でコードを共有するためには、 ベースディレクトリにある `project/` 配下に Scala ファイルを用意すればよい。

詳細は[ビルドの整理][Organizing-Build]を参照。

### 補足: サブプロジェクトビルド定義ファイル

`foo` 内の全ての `.sbt` ファイル、例えば `foo/build.sbt` は、
`hello-foo` プロジェクトにスコープ付けされた上で、ビルド全体のビルド定義に取り込まれる。

ルートプロジェクトが `hello` にあるとき、`hello/build.sbt`、`hello/foo/build.sbt`、
`hello/bar/build.sbt` においてそれぞれ別々のバージョンを定義してみよう（例: `version := "0.6"`）。
次に、インタラクティブプロンプトで `show version` と打ち込んでみる。
以下のように表示されるはずだ（定義したバージョンによるが）:

```
> show version
[info] hello-foo/*:version
[info]  0.7
[info] hello-bar/*:version
[info]  0.9
[info] hello/*:version
[info]  0.5
```

`hello-foo/*:version` は、`hello/foo/build.sbt` 内で定義され、
`hello-bar/*:version` は、`hello/bar/build.sbt` 内で定義され、
`hello/*:version` は、`hello/build.sbt` 内で定義される。
[スコープ付けされたキーの構文][Scopes]を復習しておこう。
それぞれの `version` キーは、`build.sbt` の場所により、
特定のプロジェクトにスコープ付けされている。
だが、三つの `build.sbt` とも同じビルド定義の一部だ。

スタイルの選択:

- 各サブプロジェクトのセッティングはそのベースディレクトリ直下の `*.sbt` ファイル内で宣言することができる。その場合、`build.sbt` は `lazy val foo = (project in file("foo"))` といった形で最小の project 宣言のみを行いセッティングは書かない。
- 全てのプロジェクト宣言とセッティングをルートの `build.sbt` に書けば全てのビルド定義を 1つのファイルにまとめることができるので、その方法を推奨する。ただし、これは好みの問題だから、好きにやっていい。

**注意**: サブプロジェクトは、`project` サブディレクトリや、`project/*.scala` ファイルを持つことができない。
`foo/project/Build.scala` は無視される。

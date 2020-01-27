---
out: Scopes.html
---

  [MavenScopes]: https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Scope
  [Basic-Def]: Basic-Def.html
  [Task-Graph]: Task-Graph.html
  [Library-Dependencies]: Library-Dependencies.html
  [Multi-Project]: Multi-Project.html
  [Inspecting-Settings]: ../../docs/Inspecting-Settings.html
  [Scope-Delegation]: Scope-Delegation.html

スコープ
-------

このページではスコープの説明をする。前のページの
[.sbt ビルド定義][Basic-Def]、
[タスク・グラフ][Task-Graph]
を読んで理解したことを前提とする。

### キーに関する本当の話

[前のページでは][Basic-Def]、あたかも `name` のようなキーは単一の sbt の Map のキー・値ペアの項目に対応するかのように説明をしてきた。
しかし、それは実際よりも物事を単純化している。

実のところ、全てのキーは「スコープ」と呼ばれる文脈に関連付けられた値を複数もつことができる。

以下に具体例で説明する:

 - ビルド定義に複数のプロジェクト (サブプロジェクトとも呼ばれる) があれば、それぞれのプロジェクトにおいて同じキーが別の値を取ることができる。
 - メインのソースとテストとのソースが異なるようにコンパイルしたければ、`compile` キーは別の値をとることができる。
 - （jar パッケージの作成のオプションを表す）`packageOption` キーはクラスファイルのパッケージ（`packageBin`）とソースコードのパッケージ（`packageSrc`）で異なる値をとることができる。

スコープによって値が異なる可能性があるため、_あるキーへの単一の値は存在しない_。

しかし、**スコープ付きキー**には単一の値が存在する。

 [これまで見てきた][Basic-Def]ように sbt がプロジェクトを記述するキーと値のマップを生成するためのセッティングキーのリストを処理していると考えるなら、
そのキーと値の Map におけるキーとは、実は_スコープ付き_キーである。
また、（`build.sbt` などの）ビルド定義内のセッティングもまたスコープ付きキーである。

スコープは、暗黙に存在していたり、デフォルトのものがあったりするが、
もしそのデフォルトが適切でなければ `build.sbt` で必要なスコープを指定する必要があるだろう。

### スコープ軸

**スコープ軸**（scope axis）は、`Option[A]` に似た型コンストラクタであり、
スコープの各成分を構成する。

スコープ軸は三つある:

 - サブプロジェクト
 - 依存性コンフィギュレーション
 - タスク

**軸**という概念に馴染みがなければ、RGB 色空間を例に取ってみるといいかもしれない。

![color cube](../files/rgb_color_solid_cube.png)

RGB 色モデルにおいて、全ての色は赤、緑、青の成分を軸とする立方体内の点として表すことができ、それぞれの成分は数値化することができる。
同様に、sbt におけるスコープはサブプロジェクト、コンフィギュレーション、タスクの**タプル**により成り立つ:

```scala
projA / Compile / console / scalacOptions
```

これは以下のスコープ付きキーを sbt 1.1 で導入されたスラッシュ構文で書いたものだ:

```scala
scalacOptions in (
  Select(projA: Reference),
  Select(Compile: ConfigKey),
  Select(console.key)
)
```

#### サブプロジェクト軸によるスコープ付け

[一つのビルドに複数のプロジェクトを入れる][Multi-Project]場合、それぞれのプロジェクトにセッティングが必要だ。
つまり、キーはプロジェクトによりスコープ付けされる。

プロジェクト軸は `ThisBuild` という「ビルド全体」を表す値に設定することもでき、その場合はセッティングは単一のプロジェクトではなくビルド全体に適用される。
ビルドレベルでのセッティングは、プロジェクトが特定のセッティングを定義しない場合のフォールバックとして使われることがよくある。

#### 依存性コンフィギュレーション軸によるスコープ付け

**依存性コンフィギュレーション**（dependency configuration、もしく単に「コンフィギュレーション」）
は、ライブラリ依存性のグラフを定義し、独自のクラスパス、ソース、生成パッケージなどをもつことができる。
コンフィギュレーションの概念は、sbt が [マネージ依存性][Library-Dependencies] に使っている Ivy と、[MavenScopes][MavenScopes] に由来する。

sbt で使われる代表的なコンフィギュレーションには以下のものがある:

 - `Compile` は、メインのビルド（`src/main/scala`）を定義する。
 - `Test` は、テスト（`src/test/scala`）のビルド方法を定義する。
 - `Runtime` は、`run` タスクのクラスパスを定義する。

デフォルトでは、コンパイル、パッケージ化と実行に関するキーの全ては依存性コンフィグレーションにスコープ付けされているため、
依存性コンフィギュレーションごとに異なる動作をする可能性がある。
その最たる例が `compile`、`package` と `run` のタスクキーだが、
（`sourceDirectories` や `scalacOptions` や `fullClasspath` など）それらのキーに_影響を及ぼす_全てのキーもコンフィグレーションにスコープ付けされている。

もう一つコンフィギュレーションで大切なのは、他のコンフィギュレーションを拡張できることだ。
以下に代表的なコンフィギュレーションの拡張関係を図で示す。

![dependency configurations](../files/sbt-configurations.png)

`Test` と `IntegrationTest` は `Runtime` を拡張し、`Runtime` は `Compile` を拡張し、
`CompileInternal` は `Compile`、`Optional`、`Provided` の 3つを拡張する。

#### タスク軸によるスコープ付け

セッティングはタスクの動作に影響を与えることもできる。例えば、`packageSrc` は `packageOptions` セッティングの影響を受ける。

これをサポートするため、（`packageSrc` のような）タスクキーは、（`packageOption` のような）別のキーのスコープとなりえる。

パッケージを構築するさまざまなタスク（`packageSrc`、`packageBin`、`packageDoc`）は、`artifactName` や `packageOption` などのパッケージ関連のキーを共有することができる。これらのキーはそれぞれのパッケージタスクに対して独自の値を取ることができる。

#### Zero スコープ成分

各スコープ軸は、`Some(_)` のようにその軸の型のインスタンスを持つか、`Zero` という特殊な値を持つことができる。
つまり、`Zero` は `None` と同様だと考えることができる。

`Zero` は全てのスコープ軸に対応する普遍的なフォールバックであるが、多くの場合直接それを使うのは sbt 本体もしくはプラグインの作者に限定されるべきだ。

`Global` は、全ての軸を `Zero` とするスコープ、`Zero / Zero / Zero` だ。そのため、`Global / someKey` は `Zero / Zero / Zero / someKey` を略記したものだと考えることができる。

### ビルド定義からスコープを参照する

`build.sbt` で裸のキーを使ってセッティングを作った場合は、(現プロジェクト / コンフィグレーション `Zero` / タスク `Zero`) にスコープ付けされる:

```scala
lazy val root = (project in file("."))
  .settings(
    name := "hello"
  )
```

sbt を実行して、`inspect name` と入力して、キーが
`ProjectRef(uri("file:/private/tmp/hello/"), "root") / name` により提供されていることを確認しよう。つまり、プロジェクトは、
`ProjectRef(uri("file:/private/tmp/hello/"), "root")` で、コンフィギュレーション軸もタスク軸も表示されない (これは `Zero` を意味する)。

右辺項に置かれた裸のキーも (現プロジェクト / コンフィグレーション `Zero` / タスク `Zero`) にスコープ付けされる:

@@snip [build.sbt]($root$/src/sbt-test/ref/scopes/build.sbt) { #unscoped }

全てのスコープ軸の型には `/` 演算子が導入されている。
`/` は引数としてキーもしくは別のスコープ軸を受け取ることができる。
これをやる意味は全くないけど、例として `Compile` コンフィギュレーションでスコープ付けされた `name` の設定を以下に示す:

@@snip [build.sbt]($root$/src/sbt-test/ref/scopes/build.sbt) { #confScoped }

また、`packageBin` タスクでスコープ付けされた `name` の設定（これも意味なし！ただの例だよ）:

@@snip [build.sbt]($root$/src/sbt-test/ref/scopes/build.sbt) { #taskScoped }

もしくは、例えば `Compile` コンフィギュレーションの `packageBin` の `name` など、複数のスコープ軸でスコープ付けする:

@@snip [build.sbt]($root$/src/sbt-test/ref/scopes/build.sbt) { #confAndTaskScoped }

もしくは、全ての軸に対して `Global` を使う:

@@snip [build.sbt]($root$/src/sbt-test/ref/scopes/build.sbt) { #global }

（`Global / concurrentRestrictions` は、`Zero / Zero / Zero / concurrentRestrictions` へと暗黙の変換が行われ、全ての軸を `Zero` に設定する。
タスクとコンフィギュレーションは既にデフォルトで `Zero` であるため、事実上行なっているのはプロジェクトを `Zero` に指定することだ。つまり、`ProjectRef(uri("file:/tmp/hello/"), "root") / Zero / Zero / concurrentRestrictions` ではなく、`Zero / Zero / Zero / concurrentRestrictions` が定義される。）

### sbt シェルからのスコープ付きキーの参照方法

コマンドラインと sbt シェルにおいて、sbt はスコープ付きキーを以下のように表示する（そして、パースする）:

```
ref / Config / intask / キー
```

 - `ref` は、サブプロジェクト軸を特定する。これは `<プロジェクト-id>`、`ProjectRef(uri("file:..."), "id")`、もしくは「ビルド全体」を意味する `ThisBuild` という値を取ることができる。
 - `Config` は、コンフィギュレーション軸を特定し、大文字から始まる Scala 識別子を使う。
 - `intask` は、タスク軸を特定する。
 - `キー` は、スコープ付けされるキーを特定する。

全ての軸において、`Zero` を使うことができる。

スコープ付きキーの一部を省略すると、以下の手順で推論される:

 - プロジェクトを省略した場合は、カレントプロジェクトが使われる。
 - `Config` もしくは `intask` を省略した場合は、キーに依存したコンフィギュレーションが自動検知される。

さらに詳しくは、[Interacting with the Configuration System][Inspecting-Settings] 参照。

### sbt シェルでのスコープ付きキーの表記例

- `fullClasspath` はキーのみを指定し、デフォルトスコープを用いる。ここでは、カレントプロジェクト、キーに依存したコンフィギュレーション、`Zero` タスクスコープとなる。
- `Test / fullClasspath` はコンフィギュレーションを指定する。つまりプロジェクト軸とタスク軸はデフォルトを用いつつも `Test`コンフィギュレーションにおける `fullClasspath` というキーを表す。
- `root / fullClasspath` は `root` というプロジェクトid によって特定されるプロジェクトをプロジェクト軸に指定する。
- `root / Zero / fullClasspath` は `root` プロジェクトと、デフォルトのコンフィギュレーションの代わりに `Zero` をコンフィギュレーション軸に指定する。
- `doc / fullClasspath` は `fullClasspath` キーを `doc` タスク、プロジェクト軸とコンフィギュレーション軸はデフォルト値へと指定する。
- `ProjectRef(uri("file:/tmp/hello/"), "root") / Test / fullClasspath`
  はプロジェクト `ProjectRef(uri("file:/tmp/hello/"), "root")`、Test コンフィギュレーション、デフォルトのタスク軸を指定する。
- `ThisBuild / version` はプロジェクト軸をこの「ビルド全体」である `ThisBuild`、デフォルトのコンフィギュレーション軸へと指定する。
- `Zero / fullClasspath` はプロジェクト軸を `Zero`、コンフィギュレーション軸をデフォルト値へと指定する。
- `root / Compile / doc / fullClasspath` は 3つ全てのスコープ軸を指定する。

### スコープの検査

sbt シェルで `inspect` コマンドを使ってキーとそのスコープを把握することができる。
例えば、`inspect Test/fullClasspath` と試してみよう:

```
\$ sbt
sbt:Hello> inspect Test / fullClasspath
[info] Task: scala.collection.Seq[sbt.internal.util.Attributed[java.io.File]]
[info] Description:
[info]  The exported classpath, consisting of build products and unmanaged and managed, internal and external dependencies.
[info] Provided by:
[info]  ProjectRef(uri("file:/tmp/hello/"), "root") / Test / fullClasspath
[info] Defined at:
[info]  (sbt.Classpaths.classpaths) Defaults.scala:1639
[info] Dependencies:
[info]  Test / dependencyClasspath
[info]  Test / exportedProducts
[info]  Test / fullClasspath / streams
[info] Reverse dependencies:
[info]  Test / testLoader
[info] Delegates:
[info]  Test / fullClasspath
[info]  Runtime / fullClasspath
[info]  Compile / fullClasspath
[info]  fullClasspath
[info]  ThisBuild / Test / fullClasspath
[info]  ThisBuild / Runtime / fullClasspath
[info]  ThisBuild / Compile / fullClasspath
[info]  ThisBuild / fullClasspath
[info]  Zero / Test / fullClasspath
[info]  Zero / Runtime / fullClasspath
[info]  Zero / Compile / fullClasspath
[info]  Global / fullClasspath
[info] Related:
[info]  Compile / fullClasspath
[info]  Runtime / fullClasspath
```

一行目からこれが（[.sbt ビルド定義][Basic-Def]で説明されているとおり、セッティングではなく）タスクであることが分かる。
このタスクの戻り値は `scala.collection.Seq[sbt.Attributed[java.io.File]]` の型をとる。

"Provided by" は、この値を定義するスコープ付きキーを指し、この場合は、
`ProjectRef(uri("file:/tmp/hello/"), "root") / Test / fullClasspath`
（`Test` コンフィギュレーションと `ProjectRef(uri("file:/tmp/hello/"), "root")` プロジェクトにスコープ付けされた `fullClasspath` キー）。

"Dependencies" に関しては、[前のページ][Task-Graph]で解説した。

"Delegates" (委譲) に関してはまた後で。

今度は、（`inspect Test/fullClasspath` のかわりに）`inspect fullClasspath` を試してみて、違いをみてみよう。
コンフィグレーションが省略されたため、`Compile` だと自動検知される。
そのため、`inspect Compile/fullClasspath` は `inspect fullClasspath` と同じになるはずだ。

次に、`inspect ThisBuild / Zero / fullClasspath` も実行して違いを比べてみよう。
`fullClasspath` はデフォルトでは、`Zero` スコープには定義されていない。

より詳しくは、[Interacting with the Configuration System][Inspecting-Settings] 参照。

### いつスコープを指定するべきか

あるキーが、通常スコープ付けされている場合は、スコープを指定してそのキーを使う必要がある。
例えば、`compile` タスクは、デフォルトで `Compile` と `Test` コンフィギュレーションにスコープ付けされているけど、
これらのスコープ外には存在しない。

そのため、`compile` キーに関連付けられた値を変更するには、`Compile / compile` か `Test / compile` のどちらかを書く必要がある。
素の `compile` を使うと、コンフィグレーションにスコープ付けされた標準のコンパイルタスクをオーバーライドするかわりに、カレントプロジェクトにスコープ付けされた新しいコンパイルタスクを定義してしまう。

_"Reference to undefined setting"_ のようなエラーに遭遇した場合は、スコープを指定していないか、間違ったスコープを指定したことによることが多い。
君が使っているキーは何か別のスコープの中で定義されている可能性がある。
エラーメッセージの一部として sbt は、意味したであろうものを推測してくれるから、"Did you mean Compile / compile?" を探そう。

キーの名前はキーの**一部**でしかないと考えることもできる。
実際の所は、全てのキーは名前と（三つの軸を持つ）スコープによって構成される。
つまり、`Compile / packageBin / packageOptions` という式全体でキー名だということだ。
単に `packageOptions` と言っただけでもキー名だけど、それは別のキーだ
（スラッシュ無しのキーのスコープは暗黙で決定され、現プロジェクト、`Zero` コンフィグレーション、`Zero` タスクとなる）。

### ビルドレベル・セッティング

サブプロジェクト間に共通なセッティングを一度に定義するための上級テクニックとしてセッティングを
`ThisBuild` にスコープ付けするという方法がある。

もし特定のサブプロジェクトにスコープ付けされたキーが見つから無かった場合、
sbt はフォールバックとして `ThisBuild` 内を探す。
この仕組みを利用して、
`version`、 `scalaVersion`、 `organization`
といったよく使われるキーに対してビルドレベルのデフォルトのセッティングを定義することができる。

```scala
ThisBuild / organization := "com.example",
ThisBuild / scalaVersion := "$example_scala_version$",
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    publish / skip := true
  )

lazy val core = (project in file("core"))
  .settings(
    // other settings
  )

lazy val util = (project in file("util"))
  .settings(
    // other settings
  )
```

便宜のため、セッティング式のキーと本文の両方を `ThisBuild`
にスコープ付けする
`inThisBuild(...)` という関数が用意されている。
セッティング式を渡すと、それに `ThisBuild /` を可能な所に追加したのと同じものが得られる。

ただし、後で説明する[スコープ委譲][Scope-Delegation]の性質上、ビルドレベル・セッティングは
純粋な値または `Global` か `ThisBuild` にスコープ付けされたセッティングのみを代入するべきだ。

### スコープ委譲

スコープ付きキーは、そのスコープに関連付けられた値がなければ未定義であることもできる。

全てのスコープ軸に対して、sbt には他のスコープ値からなるフォールバック検索パス（fallback search path）がある。
通常は、より特定のスコープに関連付けられた値が見つからなければ、sbt は、`ThisBuild` など、より一般的なスコープから値を見つけ出そうとする。

この機能により、より一般的なスコープで一度だけ値を代入して、複数のより特定なスコープがその値を継承することを可能とする。
[スコープ委譲][Scope-Delegation]に関する詳細は後ほど解説する。

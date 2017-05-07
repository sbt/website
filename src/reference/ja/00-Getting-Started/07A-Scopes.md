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
scalacOptions in (projA, Compile, console)
```

より正確には、以下のようになっている:

```scala
scalacOptions in (Select(projA: Reference),
                  Select(Compile: ConfigKey),
                  Select(console.key))
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

#### グローバルスコープ成分

それぞれのスコープ軸は、その軸の型のインスタンスを代入する（例えば、タスク軸にはタスクを代入する）か、
もしくは、`Global` という特殊な値を代入することができる。これは `*` とも表記される。つまり、`Global` は `None` と同様だと考えることができる。

`*` は全てのスコープ軸に対応する普遍的なフォールバックであるが、多くの場合直接それを使うのは sbt 本体もしくはプラグインの作者に限定されるべきだ。

分かりづらいことに、ビルド定義内で `someKey in Global` と書いた場合、暗黙の変換によってこれは `someKey in (Global, Global, Global)` に変換される。

### ビルド定義からスコープを参照する

`build.sbt` で裸のキーを使ってセッティングを作った場合は、(現プロジェクト, `Global` コンフィグレーション, `Global` タスク) にスコープ付けされる:

```scala
lazy val root = (project in file("."))
  .settings(
    name := "hello"
  )
```

sbt を実行して、`inspect name` と入力して、キーが　`{file:/home/hp/checkout/hello/}default-aea33a/*:name` により提供されていることを確認しよう。つまり、プロジェクトは、`{file:/home/hp/checkout/hello/}default-aea33a` で、コンフィギュレーションは `*` で、タスクは表示されていない（グローバルを指す）ということだ。

右辺項に置かれた裸のキーも (現プロジェクト, `Global` コンフィグレーション, `Global` タスク) にスコープ付けされる:

```scala
organization := name.value
```


キーにはオーバーロードされた `.in` メソッドがあり、それによりスコープを設定できる。
`.in(...)` への引数として、どのスコープ軸のインスタンスでも渡すことができる。
これをやる意味は全くないけど、例として `Compile` コンフィギュレーションでスコープ付けされた `name` の設定を以下に示す:

```scala
name in Compile := "hello"
```

また、`packageBin` タスクでスコープ付けされた `name` の設定（これも意味なし！ただの例だよ）:

```scala
name in packageBin := "hello"
```

もしくは、例えば `Compile` コンフィギュレーションの `packageBin` の `name` など、複数のスコープ軸でスコープ付けする:

```scala
name in (Compile, packageBin) := "hello"
```

もしくは、全ての軸に対して `Global` を使う:

```scala
// concurrentRestrictions in (Global, Global, Global) と同じ
concurrentRestrictions in Global := Seq(
  Tags.limitAll(1)
)
```

（`concurrentRestrictions in Global` は、`concurrentRestrictions in (Global, Global, Global)` へと暗黙の変換が行われ、全ての軸を `Global` に設定する。
タスクとコンフィギュレーションは既にデフォルトで `Global` であるため、事実上行なっているのはプロジェクトを `Global` に指定することだ。つまり、`{file:/home/hp/checkout/hello/}default-aea33a/*:concurrentRestrictions` ではなく、`*/*:concurrentRestrictions` が定義される。）

### sbt シェルからのスコープ付きキーの参照方法

コマンドラインと sbt シェルにおいて、sbt はスコープ付きキーを以下のように表示する（そして、パースする）:

```
{<ビルド-uri>}<プロジェクト-id>/コンフィギュレーション:タスクキー::キー
```

 - `{<ビルド-uri>}<プロジェクト-id>` は、サブプロジェクト軸を特定する。<プロジェクト-id> がなければ、サブプロジェクト軸は「ビルド全体」スコープとなる。
 - `コンフィギュレーション` は、コンフィギュレーション軸を特定する。
 - `タスクキー` は、タスク軸を特定する。
 - `キー` は、スコープ付けされるキーを特定する。

全ての軸において、`*` を使って `Global` スコープを表すことができる。

スコープ付きキーの一部を省略すると、以下の手順で推論される:

 - プロジェクトを省略した場合は、カレントプロジェクトが使われる。
 - コンフィグレーションを省略した場合は、キーに依存したコンフィギュレーションが自動検知される。
 - タスクを省略した場合は、`Global` タスクが使われる。

さらに詳しくは、[Interacting with the Configuration System][Inspecting-Settings] 参照。

### スコープ付きキーの表記例

- `fullClasspath` はキーのみを指定し、デフォルトスコープを用いる。ここでは、カレントプロジェクト、キーに依存したコンフィギュレーション、グローバルタスクスコープとなる。
- `test:fullClasspath` はコンフィギュレーションを指定する。つまりプロジェクト軸とタスク軸はデフォルトを用いつつも `test`コンフィギュレーションにおける `fullClasspath` というキーを表す。
- `*:fullClasspath` はデフォルトコンフィギュレーションを用いずに `Global` コンフィギュレーションを用いる事を明示している。
- `doc::fullClasspath` はプロジェクト軸とコンフィギュレーション軸はデフォルトを用いつつ、 `doc` タスクスコープにおける `fullClasspath` というキーを表す。
- `{file:/home/hp/checkout/hello/}default-aea33a/test:fullClasspath` は `{file:/home/hp/checkout/hello/}` をルートディレクトリにビルドした際に含まれる `default-aea33a` というプロジェクトを指定している。さらにこのプロジェクト内の `test` コンフィギュレーションを用いる事も明示している。
- `{file:/home/hp/checkout/hello/}/test:fullClasspath` は `{file:/home/hp/checkout/hello/}` のビルド全体をプロジェクトの軸とする。
- `{.}/test:fullClasspath` は `{.}` で指定されたルートディレクトリのビルド全体をプロジェクト軸に取る。`{.}` は Scala code において `ThisBuild` と記述できる。
- `{file:/home/hp/checkout/hello/}/compile:doc::fullClasspath` は3つのスコープ軸全てを指定している。

### スコープの検査

sbt シェルで `inspect` コマンドを使ってキーとそのスコープを把握することができる。
例えば、`inspect test:full-classpath` と試してみよう:

```
\$ sbt
> inspect test:fullClasspath
[info] Task: scala.collection.Seq[sbt.Attributed[java.io.File]]
[info] Description:
[info]  The exported classpath, consisting of build products and unmanaged and managed, internal and external dependencies.
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}default-aea33a/test:fullClasspath
[info] Dependencies:
[info]  test:exportedProducts
[info]  test:dependencyClasspath
[info] Reverse dependencies:
[info]  test:runMain
[info]  test:run
[info]  test:testLoader
[info]  test:console
[info] Delegates:
[info]  test:fullClasspath
[info]  runtime:fullClasspath
[info]  compile:fullClasspath
[info]  *:fullClasspath
[info]  {.}/test:fullClasspath
[info]  {.}/runtime:fullClasspath
[info]  {.}/compile:fullClasspath
[info]  {.}/*:fullClasspath
[info]  */test:fullClasspath
[info]  */runtime:fullClasspath
[info]  */compile:fullClasspath
[info]  */*:fullClasspath
[info] Related:
[info]  compile:fullClasspath
[info]  compile:fullClasspath(for doc)
[info]  test:fullClasspath(for doc)
[info]  runtime:fullClasspath
```

一行目からこれが（[.sbt ビルド定義][Basic-Def]で説明されているとおり、セッティングではなく）タスクであることが分かる。
このタスクの戻り値は `scala.collection.Seq[sbt.Attributed[java.io.File]]` の型をとる。

"Provided by" は、この値を定義するスコープ付きキーを指し、この場合は、
`{file:/home/hp/checkout/hello/}default-aea33a/test:fullClasspath`
（`test` コンフィギュレーションと `{file:/home/hp/checkout/hello/}default-aea33a` プロジェクトにスコープ付けされた `fullClasspath` キー）。

"Dependencies" に関しては、[前のページ][Task-Graph]で解説した。

"Delegates" (委譲) に関してはまた後で。

今度は、（`inspect test:full-class` のかわりに）`inspect fullClasspath` を試してみて、違いをみてみよう。
コンフィグレーションが省略されたため、`compile` だと自動検知される。
そのため、`inspect compile:fullClasspath` は `inspect fullClasspath` と同じになるはずだ。

次に、`inspect *:fullClasspath` も実行して違いを比べてみよう。
`fullClasspath` はデフォルトでは、`Global` スコープには定義されていない。

より詳しくは、[Interacting with the Configuration System][Inspecting-Settings] 参照。

### いつスコープを指定するべきか

あるキーが、通常スコープ付けされている場合は、スコープを指定してそのキーを使う必要がある。
例えば、`compile` タスクは、デフォルトで `Compile` と `Test` コンフィギュレーションにスコープ付けされているけど、
これらのスコープ外には存在しない。

そのため、`compile` キーに関連付けられた値を変更するには、`compile in Compile` か `compile in Test` のどちらかを書く必要がある。
素の `compile` を使うと、コンフィグレーションにスコープ付けされた標準のコンパイルタスクをオーバーライドするかわりに、カレントプロジェクトにスコープ付けされた新しいコンパイルタスクを定義してしまう。

_"Reference to undefined setting"_ のようなエラーに遭遇した場合は、スコープを指定していないか、間違ったスコープを指定したことによることが多い。
君が使っているキーは何か別のスコープの中で定義されている可能性がある。
エラーメッセージの一部として sbt は、君が意味したであろうものを推測してくれるから、"Did you mean compile:compile?" を探そう。

キーの名前はキーの_一部_であると考えることもできる。
実際の所は、全てのキーは名前と（三つの軸を持つ）スコープによって構成される。
つまり、`packageOptions in (Compile, packageBin)` という式全体でキー名だということだ。
単に `packageOptions` と言っただけでもキー名だけど、それは別のキーだ
（`in` 無しのキーのスコープは暗黙で決定され、現プロジェクト、`Global` コンフィグレーション、`Global` タスクとなる）。

### ビルドレベル・セッティング

サブプロジェクト間に共通なセッティングを一度に定義するための上級テクニックとしてセッティングを
`ThisBuild` にスコープ付けするという方法がある。

もし特定のサブプロジェクトにスコープ付けされたキーが見つから無かった場合、
sbt はフォールバックとして `ThisBuild` 内を探す。
この仕組みを利用して、
`version`、 `scalaVersion`、 `organization`
といったよく使われるキーに対してビルドレベルのデフォルトのセッティングを定義することができる。

便宜のため、セッティング式のキーと本文の両方を `ThisBuild`
にスコープ付けする
`inThisBuild(...)` という関数が用意されている。
セッティング式を渡すと、それに `in ThisBuild` を可能な所に追加したのと同じものが得られる。

```scala
lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      // Same as:
      // organization in ThisBuild := "com.example"
      organization := "com.example",
      scalaVersion := "$example_scala_version$",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Hello",
    publish := (),
    publishLocal := ()
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

ただし、後で説明する[スコープ委譲][Scope-Delegation]の性質上、ビルドレベル・セッティングを単純な値の代入以外に使うことは推奨しない。

### スコープ委譲

スコープ付きキーは、そのスコープに関連付けられた値がなければ未定義であることもできる。

全てのスコープ軸に対して、sbt には他のスコープ値からなるフォールバック検索パス（fallback search path）がある。
通常は、より特定のスコープに関連付けられた値が見つからなければ、sbt は、`ThisBuild` など、より一般的なスコープから値を見つけ出そうとする。

この機能により、より一般的なスコープで一度だけ値を代入して、複数のより特定なスコープがその値を継承することを可能とする。
[スコープ委譲][Scope-Delegation]に関する詳細は後ほど解説する。

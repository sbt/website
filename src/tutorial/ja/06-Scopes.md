---
out: Scopes.html
---

  [MavenScopes]: http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Scope
  [Basic-Def]: Basic-Def.html
  [More-About-Settings]: More-About-Settings.html
  [Library-Dependencies]: Library-Dependencies.html
  [Multi-Project]: Multi-Project.html
  [Inspecting-Settings]: ../../docs/Inspecting-Settings.html

スコープ
-------

このページではスコープの説明をする。君が、前のページの [.sbt ビルド定義][Basic-Def]を読んで理解したことを前提とする。

### キーに関する本当の話

[これまでは][Basic-Def]、あたかも `name` のようなキーは単一の sbt のマップのキー・値ペアの項目に対応するフリをして話を進めてきた。
それは単純化した話だ。

実のところは、全てのキーは、「スコープ」と呼ばれる文脈に関連付けられた値を複数もつことができる。

以下に具体例で説明する:

 - ビルド定義に複数のプロジェクトがあれば、それぞれのプロジェクトにおいて同じキーが別の値を取ることができる。
 - メインのソースとテストとのソースが異なるようにコンパイルしたければ、`compile` キーは別の値をとることができる。
 - （jar パッケージの作成のオプションを表す）`packageOption` キーはクラスファイルのパッケージ（`packageBin`）とソースコードのパッケージ（`packageSrc`）で異なる値をとることができる。

スコープによって値が異なる可能性があるため、_あるキーへの単一の値は存在しない_。

しかし、_スコープ付き_キーには単一の値が存在する。

[これまで見てきた][Basic-Def]ように、sbt が、プロジェクトを記述するキー・値のマップを生成するためにセッティングのリストを処理していくことを考えると、このキー・値マップ内のキーは、_スコープ付き_キーであることが分かる。
また、（`build.sbt` などの）ビルド定義内の、セッティングもスコープ付きキーに適用されるものだ。

スコープは、デフォルトがあったり、暗示されていたりするが、デフォルトが間違っていれば `build.sbt` にてスコープを指定しなければいけない。

### スコープ軸

_スコープ軸_（scope axis）は、型であり、そのインスタンスは独自のスコープを定義する
（つまり、各インスタンスはキーの独自の値を持つことができる）。

スコープ軸は三つある:

 - プロジェクト
 - コンフィギュレーション
 - タスク

#### プロジェクト軸によるスコープ付け

[一つのビルドに複数のプロジェクトを入れる][Multi-Project]場合、それぞれのプロジェクトにセッティングが必要だ。
つまり、キーはプロジェクトによりスコープ付けされる。

プロジェクト軸は「ビルド全体」に設定することもでき、その場合はセッティングは単一のプロジェクトではなくビルド全体に適用される。
ビルドレベルでのセッティングは、プロジェクトが特定のセッティングを定義しない場合のフォールバックとして使われることがよくある。

#### コンフィギュレーション軸によるスコープ付け

_コンフィギュレーション_（configuration）は、ビルドの種類を定義し、独自のクラスパス、ソース、生成パッケージなどをもつことができる。
コンフィギュレーションの概念は、sbt が [マネージ依存性][Library-Dependencies] に使っている Ivy と、[MavenScopes][MavenScopes] に由来する。

sbt で使われるコンフィギュレーションには以下のものがある:

 - `Compile` は、メインのビルド（`src/main/scala`）を定義する。
 - `Test` は、テスト（`src/test/scala`）のビルド方法を定義する。
 - `Runtime` は、`run` タスクのクラスパスを定義する。

デフォルトでは、コンパイル、パッケージ化、と実行に関するキーの全てはコンフィグレーションにスコープ付けされているため、
コンフィギュレーションごとに異なる動作をする可能性がある。
その最たる例が `compile`、`package` と `run` のタスクキーだが、
（`sourceDirectories` や `scalacOptions` や `fullClasspath` など）それらのキーに_影響を及ぼす_全てのキーもコンフィグレーションにスコープ付けされている。

#### タスク軸によるスコープ付け

セッティングはタスクの動作に影響を与えることもできる。例えば、`pakcageSrc` は `packageOptions` セッティングの影響を受ける。

これをサポートするため、（`packageSrc` のような）タスクキーは、（`packageOption` のような）別のキーのスコープとなりえる。

パッケージを構築するさまざまなタスク（`packageSrc`、`packageBin`、`packageDoc`）は、`artifactName` や `packageOption` などのパッケージ関連のキーを共有することができる。これらのキーはそれぞれのパッケージタスクに対して独自の値を取ることができる。

### グローバルスコープ

それぞれのスコープ軸は、その軸の型のインスタンスを代入する（例えば、タスク軸にはタスクを代入する）か、
もしくは、`Global` という特殊な値を代入することができる。

`Global` は、予想通りのもので、その軸の全てのインスタンスに対して適用されるセッティングの値だ。
例えば、タスク軸が `Global` ならば、全てのタスクに適用される。

### 委譲

スコープ付きキーは、そのスコープに関連付けられた値がなければ未定義であることもできる。

全てのスコープに対して、sbt には他のスコープからなるフォールバック検索パス（fallback search path）がある。
通常は、より特定のスコープに関連付けられた値が見つからなければ、
sbt は、`Global` や、ビルド全体スコープなど、より一般的なスコープから値を見つけ出そうとする。

この機能により、より一般的なスコープで一度値を代入することで、複数のより特定なスコープがその値を継承することを可能とする。

以下に、`inspect` を使ったキーのフォールバック検索パス、別名「委譲」（delegate）の探し方を説明する。

### sbt 実行中のスコープ付きキーの参照方法

コマンドラインとインタラクティブモードにおいて、sbt はスコープ付きキーを以下のように表示し（パースする）:

```
{<ビルド-uri>}<プロジェクト-id>/コンフィギュレーション:タスクキー::キー
```

 - `{<ビルド-uri>}<プロジェクト-id>` は、プロジェクト軸を特定する。<プロジェクト-id> がなければ、プロジェクト軸は「ビルド全体」スコープとなる。
 - `コンフィギュレーション` は、コンフィギュレーション軸を特定する。
 - `タスクキー` は、タスク軸を特定する。
 - `キー` は、スコープ付けされるキーを特定する。

全ての軸において、`*` を使って `Global` スコープを表すことができる。

スコープ付きキーの一部を省略すると、以下の手順で推論される:

 - プロジェクトを省略した場合は、現在のプロジェクトが使われる。
 - コンフィグレーションを省略した場合は、キーに依存したコンフィギュレーションが自動検知される。
 - タスクを省略した場合は、`Global` タスクが使われる。

さらに詳しくは、[Interacting with the Configuration System][Inspecting-Settings] 参照。

### スコープ付きキーの表記例

- `fullClasspath` はキーのみを指定し、デフォルトスコープを用いる。ここでは、現在のプロジェクト、キーに依存したコンフィギュレーション、グローバルタスクスコープとなる。
- `test:fullClasspath` はコンフィギュレーションを指定する。つまりプロジェクト軸とタスク軸はデフォルトを用いつつも `test`コンフィギュレーションにおける `fullClasspath` というキーを表す。
- `*:fullClasspath` はデフォルトコンフィギュレーションを用いずに `Global` コンフィギュレーションを用いる事を明示している。
- `doc::fullClasspath` はプロジェクト軸とコンフィギュレーション軸はデフォルトを用いつつ、 `doc` タスクスコープにおける `fullClasspath` というキーを表す。
- `{file:/home/hp/checkout/hello/}default-aea33a/test:fullClasspath` は `{file:/home/hp/checkout/hello/}` をルートディレクトリにビルドした際に含まれる `default-aea33a` というプロジェクトを指定している。さらにこのプロジェクト内の `test` コンフィギュレーションを用いる事も明示している。
- `{file:/home/hp/checkout/hello/}/test:fullClasspath` は `{file:/home/hp/checkout/hello/}` のビルド全体をプロジェクトの軸とする。
- `{.}/test:fullClasspath` は `{.}` で指定されたルートディレクトリのビルド全体をプロジェクト軸に取る。`{.}` は Scala code において `ThisBuild` と記述できる。
- `{file:/home/hp/checkout/hello/}/compile:doc::fullClasspath` は3つのスコープ軸全てを指定している。

### スコープの検査

sbt のインタラクティブモード内で `inspect` コマンドを使ってキーとそのスコープを理解することができる。
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
`{file:/home/hp/checkout/hello/}default-aea33a/test:full-classpath`
（`test` コンフィギュレーションと `{file:/home/hp/checkout/hello/}default-aea33a` プロジェクトにスコープ付けされた `full-classpath` キー）。

"Dependencies" は、まだ意味不明だろうけど、[次のページ][More-About-Settings]まで待ってて。

ここで委譲も見ることができ、もし値が定義されていなければ、sbt は以下を検索する:

 - 他の二つのコンフィギュレーション（`runtime:full-classpath` と `compile:full-classpath`）。
   これらのスコープ付きキーは、プロジェクトは特定されていないため「現プロジェクト」で、タスクも特定されていない `Global` だ。
 - `Global` に設定されたコンフィギュレーション (`*:full-classpath`)。プロジェクトはまだ特定されていないため「現プロジェクト」で、タスクもまだ特定されていないため `Global` だ。
 - `{.}` 別名 `ThisBuild` に設定されたプロジェクト（つまり、特定のプロジェクトではなく、ビルド全体）。
 - `Global` に設定されたプロジェクト軸（`*/test:full-classpath`）（プロジェクトが特定されていない場合は、現プロジェクトを意味するため、`Global` を検索することは新しく、`*` と「プロジェクトが未表示」はプロジェクト軸に対して異なる値を持ち、`*/test:full-classpath` と `test:full-classpath` は等価ではない。）
 - プロジェクトとコンフィギュレーションの両方とも `Global` を設定する（`*/*:full-classpath`）（特定されていないタスクは `Global` であるため、`*/*:full-classpath` は三つの軸全てが `Global` を取る。）

今度は、（`inspect test:full-class` のかわりに）`inspect full-classpath` を試してみて、違いをみてみよう。
コンフィグレーションが省略されたため、`compile` だと自動検知される。
そのため、`inspect compile:full-classpath` は `inspect full-classpath` と同じになるはずだ。

次に、`inspect *:full-classpath` も実行して違いを比べてみよう。
`full-classpath` はデフォルトでは、`Global` コンフィギュレーションには定義されていない。

より詳しくは、[Interacting with the Configuration System][Inspecting-Settings] 参照。

### ビルド定義からスコープを参照する

`build.sbt` で裸のキーを使ってセッティングを作った場合は、現プロジェクト、`Global` コンフィグレーション、`Global` タスクにスコープ付けされる:

```scala
name := "hello"
```

sbt を実行して、`inspect name` と入力して、キーが　`{file:/home/hp/checkout/hello/}default-aea33a/*:name` により提供されていることを確認しよう。つまり、プロジェクトは、`{file:/home/hp/checkout/hello/}default-aea33a` で、コンフィギュレーションは `*` で、タスクは表示されていない（グローバルを指す）ということだ。

<!-- TODO: Fix this. -->

`build.sbt` は常に単一のプロジェクトのセッティングを定義するため、「現プロジェクト」は今 `build.sbt` で定義しているプロジェクトを指す。
（[マルチプロジェクト・ビルド][Multi-Project]の場合は、プロジェクトごとに `build.sbt` がある。）

キーにはオーバーロードされた `in` メソッドがあり、それによりスコープを設定できる。
`in` への引数として、どのスコープ軸のインスタンスでも渡すことができる。
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
name in Global := "hello"
```

（`name in Global`  は、スコープ軸である `Global` を全ての軸を `Global` に設定したスコープに暗黙の変換が行われる。
タスクとコンフィギュレーションは既にデフォルトで `Global` であるため、事実上行なっているのはプロジェクトを `Global` に指定することだ。つまり、`{file:/home/hp/checkout/hello/}default-aea33a/*:name` ではなく、`*/*:name` が定義される。）

Scala に慣れていない場合に注意して欲しいのは、`in` や `:=` はただのメソッドであって、魔法ではないということだ。
Scala ではキレイに書くことができるけど、Java 風に以下のようにも書き下すこともできる:

```scala
name.in(Compile).:=("hello")
```

こんな醜い構文で書く必要は一切無いけど、これらが実際にメソッドであることを示している。

### いつスコープを指定するべきか

あるキーが、通常スコープ付けされている場合は、スコープを指定してそのキーを使う必要がある。
例えば、`compile` タスクは、デフォルトで `Compile` と `Test` コンフィギュレーションにスコープ付けされているけど、
これらのスコープ外には存在しない。

そのため、`compile` キーに関連付けられた値を変更するには、`compile in Compile` か `compile in Test` のどちらかを書く必要がある。
素の `compile` を使うと、コンフィグレーションにスコープ付けされた標準のコンパイルタスクをオーバーライドするかわりに、現在のプロジェクトにスコープ付けされた新しいコンパイルタスクを定義してしまう。

_"Reference to undefined setting"_ のようなエラーに遭遇した場合は、スコープを指定していないか、間違ったスコープを指定したことによることが多い。
君が使っているキーは何か別のスコープの中で定義されている可能性がある。
エラーメッセージの一部として sbt は、君が意味したであろうものを推測してくれるから、"Did you mean compile:compile?" を探そう。

キーの名前はキーの_一部_であると考えることもできる。
実際の所は、全てのキーは名前と（三つの軸を持つ）スコープによって構成される。
つまり、`packageOptions in (Compile, packageBin)` という式全体でキー名だということだ。
単に `packageOptions` と言っただけでもキー名だけど、それは別のキーだ
（`in` 無しのキーのスコープは暗黙で決定され、現プロジェクト、`Global` コンフィグレーション、`Global` タスクとなる）。

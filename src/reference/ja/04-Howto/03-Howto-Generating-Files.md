---
out: Howto-Generating-Files.html
---

  [modify-package-contents]: Howto-Package.html#modify-package-contents

ソースファイル/リソースファイルの生成
----------------

sbt にはソースコードやリソースの生成を行うタスクを登録する標準的なフックが用意されている。

<a name="sources"></a>

### ソースコードの生成

ソースコードを生成するタスクでは、ソースコードを `sourceManaged` のサブディレクトリに生成し、
生成した File オブジェクトを返すように実装するのがよいだろう。
タスクの実装の核となる、ソースを生成する関数のシグネチャは次のようになる。

```scala
def makeSomeSources(base: File): Seq[File]
```

ソースを生成するタスクは `sourceGenerators` キーに追加する。
ここでは、実行結果の値ではなくタスク自体を追加するため、通常の `value` ではなく、 `taskValue`を使う。
`sourceGenerators` には生成するソースが main か test かに応じて、それぞれ `Compile`、 `Test`のスコープ付けをしておく。
大まかな定義は次のようになる。

```scala
Compile / sourceGenerators += <task of type Seq[File]>.taskValue
```

これは、 `def makeSomeSources(base: File): Seq[File]` を用いて次のように書ける。

```scala
Compile / sourceGenerators += Def.task {
  makeSomeSources((Compile / sourceManaged).value / "demo")
}.taskValue
```

より具体的な例を示そう。
次の例では、 source generator は、実行するとコンソールに `"Hi"` と表示する `Test.scala` というアプリケーションオブジェクトを生成する。

```scala
Compile / sourceGenerators += Def.task {
  val file = (Compile / sourceManaged).value / "demo" / "Test.scala"
  IO.write(file, """object Test extends App { println("Hi") }""")
  Seq(file)
}.taskValue
```

これを `run` タスクで実行すると、次のように `"Hi"` と表示されるだろう。

```
> run
[info] Running Test
Hi
```

テスト用のソースコードを生成したい場合は、上記の `Compile` の部分を `Test` に変更する。

**注意:** 
ビルドを効率化するために、 `sourceGenerators` では、呼び出しの度にソースの生成を行うのではなく、
`sbt.Tracked.{ inputChanged, outputChanged }` などを用いて、必ず入力値に基づいたキャッシングを行うべきである。

デフォルトでは、生成したソースコードはビルド成果物のパッケージには含まれない。
追加するには、別途 mappings への追加が必要になる。
この詳細は、[Adding files to a package][modify-package-contents]を参照して欲しい。
source generator は Java のソースも Scala のソースも1つの Seq で一緒に返すが、
後続の処理は拡張子を元にそれらを区別できる。

<a name="resources"></a>

### リソースの生成

リソースを生成するタスクは、リソースを `resourceManaged` のサブディレクトリに生成し、
生成した File オブジェクトを返すように実装するのがよいだろう。
ソースの生成の場合と同様に、タスクの実装の核となる、リソースを生成する関数のシグネチャは次のようになる。

```scala
def makeSomeResources(base: File): Seq[File]
```

リソースを生成するタスクは `resourceGenerators` キーに追加する。
ここでも、実行結果の値ではなくタスク自体を追加するため、通常の `value` ではなく、 `taskValue`を使う。
`resourceGenerators` にも、生成するリソースが main か test かに応じて、それぞれ `Compile`、 `Test`のスコープ付けをしておく。
大まかな定義は次のようになる。

```scala
Compile / resourceGenerators += <task of type Seq[File]>.taskValue
```

これは、 `def makeSomeResources(base: File): Seq[File]` を用いて次のように書ける。

```scala
Compile / resourceGenerators += Def.task {
  makeSomeResources((Compile / resourceManaged).value / "demo")
}.taskValue
```

上記の例を、`run` タスク、または `package`タスク (`compile`タスクでないことに注意) で実行すると、
`resourceManaged` が示す `"target/scala-*/resource_managed"` の中に `demo` というファイルが生成される。
デフォルトでは、生成したリソースはビルド成果物のパッケージには含まれない。
追加するには、別途 mappings への追加が必要になる。
こちらについても、詳細は [Adding files to a package][modify-package-contents] を参照して欲しい。

次の例では、アプリケーション名とバージョンが書かれた `myapp.properties`というプロパティファイルが生成される。

```scala
Compile / resourceGenerators += Def.task {
  val file = (Compile / resourceManaged).value / "demo" / "myapp.properties"
  val contents = "name=%s\\nversion=%s".format(name.value,version.value)
  IO.write(file, contents)
  Seq(file)
}.taskValue
```

テスト用のリソースとして扱いたい場合は `Compile` を `Test` に変更する。

**注意:**
ビルドを効率化するために、 `resourceGenerators` では、呼び出しの度にリソースの生成を行うのではなく、
`sbt.Tracked.{ inputChanged, outputChanged }` などを用いて、必ず入力値に基づいたキャッシングを行うべきである。

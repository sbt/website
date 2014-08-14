---
out: More-About-Settings.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Full-Def]: Full-Def.html
  [Keys]: ../../sxr/sbt/Keys.scala.html

他の種類のセッティング
-------------------

このページでは、基本的な `:=` メソッドを超えた、より高度な `Settings` の作り方を説明する。
君が、[.sbt ビルド定義][Basic-Def]と[スコープ][Scopes]を読んだことを前提とする。

### 復習: セッティング

ビルド定義は `Setting` のリストを作り、それが sbt の（キーと値のペアのマップで表現される）ビルドの記述を変換するのに使われるということは[覚えている][Basic-Def]と思う。
`Setting` は、は古いマップをインプットにとり、新たなマップをアウトプットに出す変換だ。新たなマップが sbt の新しい内部状態となる。

セッティングは種類により異なる方法でマップを変換する。[これまでは][Basic-Def]、`:=` メソッドをみてきた。

`:=` が作る `Setting` は、不変の固定値を新たに変換されたマップに代入する。
例えば、マップを `name := "hello"` というセッティングで変換すると、新しいマップは `name` キーの中に `"hello"` を格納する。

セッティングがその効果を発揮するにはセッティングのマスターリストに入らなくてはいけない
（`build.sbt` の全ての行は自動的にそのリストに入るけど、
[.scala ファイル][Full-Def]の場合は、sbt が検知しない場所に `Setting` を作ってしまうことができる）。

### 既存の値に追加する: `+=` と `++=`

`:=` による置換が最も単純な変換だけど、キーには他のメソッドもある。
`SettingKey[T]` の `T` が列の場合、つまりキーの値の型が列の場合は、置換のかわりに列に追加することができる。

 - `+=` は、列に単一要素を追加する。
 - `++=` は、別の列を連結する。

例えば、`sourceDirectories in Compile` というキーの値の型は `Seq[File]` だ。
デフォルトで、このキーの値は `src/main/scala` を含む。
（君がどうしても非標準じゃないと気が済まないので）`source` という名前のディレクトリに入ったソースもコンパイルしたいとすると、
以下のようにして設定できる:

```scala
sourceDirectories in Compile += new File("source")
```

もしくは、sbt パッケージに入っている `file()` 関数を使って:

```scala
sourceDirectories in Compile += file("source")
```

（`file()` は、単に新しい `File` 作る）

`++=` を使って複数のディレクトリを一度に加える事もできる:

```scala
sourceDirectories in Compile ++= Seq(file("sources1"), file("sources2"))
```

このでの `Seq(a, b, c, ...)` は、列を構築する標準的な Scala の構文だ。

デフォルトのソースディレクトリを完全に置き換えてしまいたい場合は、当然 `:=` を使えばいい:

```scala
sourceDirectories in Compile := Seq(file("sources1"), file("sources2"))
```

### 他のキーの値を基に値を計算

タスクやセッティングのキーの値を使って他のタスクやセッティングのキーの値を設定してみる。値をつける関数には `:=` や `+=` や `++=` を用いて、引数に付ける値を入れてやればよい。

最初の例として、プロジェクトの名前と同じ組織名を定義してみよう。

```scala
// プロジェクトの後に組織名を付ける (どちらも型は SettingKey[String])
organization := name.value
```

他にもディレクトリ名を用いてプロジェクトの名前をつけるとか。

```scala
// name は Key[String]、 baseDirectory は Key[File]
// ディレクトリ名を取ってからプロジェクトの名前を付ける
name := baseDirectory.value.getName
```

これは `java.io.File` オブジェクトにある `getName` メソッドを用いて、 `baseDirectry` から取った値へと変換している。

複数の入力値を用いる場合も同様である。

```scala
name := "project " + name.value + " from " + organization.value + " version " + version.value
```

ここでも前に宣言された組織名やバージョンのセッティング値を用いて名前が付けられている。

#### 依存性を持ったセッティング

`name <<= baseDirectory(_.getName)` というセッティングにおいて、`name` は、`baseDirectory` に_依存性_（dependency）を持つ。上記を `build.sbt` に書いて、sbt のインタラクティブモードで走らせ、`inspect name` と打ち込むと、以下のように表示される（一部抜粋）:

```
[info] Dependencies:
[info]  *:baseDirectory
```

このようにして、sbt はどのセッティングが別のセッティングに依存するかを知っている。
タスクを記述するセッティングもあるため、この方法でタスク間の依存性も作ることができる。

例えば、`inspect compile` すると、`compile-inputs` に依存することが分かり、
`compile-input` を inspect すると、それがまた別のキーに依存していることが分かる。
依存性の連鎖をたどっていくと、魔法に出会う。
例えば、`compile` と打ち込むと、sbt は自動的に `update` を実行する。
これが「とにかくちゃんと動く」理由は、`compile` の計算にインプットとして必要な値が、`update` の計算を先に行うことを強制しているからだ。

このようにして、sbt の全てのビルドの依存性は、明示的には宣言されず、_自動化_されている。
あるキーの値を別の計算で使うと、その計算はキーに依存する。とにかくちゃんと動く！

#### セッティングが未定義の場合

セッティングが `:=` や `+=` や `++=` を使って自分自身や他のキーへの依存性を作る場合、
依存されたキーには値が存在しなくてはならない。
存在しなければ、sbt に怒られる。
例えば、_"Reference to undefined setting"_ なんて言われるかもしれない。
これが起こった場合は、キーが定義されている正しい[スコープ][Scopes]で使っているか確認しよう。

環状の依存性を作ってしまうことも可能で、これもまたエラーになり、sbt に怒られる。

#### 他のキーの値を基にしたタスク

あるタスクの値を定義するために他のタスクの値を計算する必要があるかもしれない。そのような場合には、`:=` や `+=` や `++=` の引数に、`Def.task` と `taskValue` を用いた値を使えば良い。

例として、`sourceGenerators` にプロジェクトのルートディレクトリや `Compile`コンフィギュレーション時のクラスパスを加える事を考えてみよう。

```scala
sourceGenerators in Compile += Def.task {
  myGenerator(baseDirectory.value, (managedClasspath in Compile).value)
}.taskValue
```

#### 依存性を持ったタスク

[.sbt ビルド定義][Basic-Def]でみた通り、タスクキーは `:=` などでセッティングを作ると
`Setting[T]` ではなく、`Setting[Task[T]]`を作る。タスクを定義する際の入力には、セッティングの値を用いることができるが、セッテイングを定義する際にタスクを入力とすることはできない。

（[Keys][Keys] より）以下の二つのキーを例に説明する:

```scala
val scalacOptions = taskKey[Seq[String]]("Options for the Scala compiler.")
val checksums = settingKey[Seq[String]]("The list of checksums to generate and to verify for dependencies.")
```

（`scalacOptions` と `checksums` は、同じ値の型を持つ二つのキーで、片方がタスクというだけで、お互い全く関係のないキーだ。）

`build.sbt` で `scalacOptions` を `checksums` のエイリアスにすることは出来るのだが、その逆は出来ない。つまり、以下のような例ではコンパイルが通る:

```scala
// scalacOptions タスクは checksums セッティングの値を用いて定義される
scalacOptions := checksums.value
```

逆方向への依存、つまりタスクの値に依存したセッティングキーの値を定義することは、どのようにしても出来ない。なぜなら、セッティングキーの値はプロジェクトのロード時に一度だけしか計算されないためである。逆に、タスクは何度も実行毎に評価される可能性がある。

```scala
// checksums セッティングは scalacOptions タスクに関連付けても、値が定まらないかもしれない
checksums := scalacOptions.value
```

### 依存性の結合: `+=` and `++=`

既存のセッティングやタスクへさらにキーを結合する際は、`+=` を用いれば良い。

例えば、プロジェクト名を使って名付けたカバレッジレポートがあるとして、それを `clean` が削除するファイルのリストに追加したいとする:

```scala
cleanFiles += file("coverage-report-" + name.value + ".txt")
```

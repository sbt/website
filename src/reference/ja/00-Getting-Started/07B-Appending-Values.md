---
out: More-About-Settings.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Keys]: ../../sxr/sbt/Keys.scala.html

他の種類のセッティング
-------------------

このページでは、基本的な `:=` メソッドを超えた、より高度な `Settings` の作り方を説明する。
君が[.sbt ビルド定義][Basic-Def]と[スコープ][Scopes]を読んだことを前提とする。

### 復習: セッティング

ビルド定義は `Setting` のリストを作り、それが sbt の（キーと値のペアの Map で表現される）ビルドの記述を変換するのに使われるということは[覚えている][Basic-Def]と思う。
セッティング（`Setting`）は古い Map を入力としてとり、新しい Map を出力する変換である。そして、新しい Map が sbt の新しい内部状態となる。

セッティングは種類により異なる方法で Map を変換する。[これまでは][Basic-Def]、`:=` メソッドをみてきた。

`:=` が作る `Setting` は、不変の固定値を新たに変換された Map に代入する。
例えば Map を `name := "hello"` というセッティングで変換すると、新しい Map は `name` キーの中に `"hello"` を格納する。

### 既存の値に追加する: `+=` と `++=`

`:=` による置換が最も単純な変換だが、キーには他のメソッドもある。
`SettingKey[T]` の `T` が列の場合、つまりキーの値の型が列の場合は、置換のかわりに列に追加することができる。

 - `+=` は、列に単一要素を追加する。
 - `++=` は、別の列を連結する。

例えば、`sourceDirectories in Compile` というキーの値の型は `Seq[File]` だ。
デフォルトで、このキーの値は `src/main/scala` を含む。
（どうしても標準的なやり方では気が済まない君が）`source` という名前のディレクトリに入ったソースもコンパイルしたい場合、
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

ここでの `Seq(a, b, c, ...)` は、列を構築する標準的な Scala の構文だ。

デフォルトのソースディレクトリを完全に置き換えてしまいたい場合は、当然 `:=` を使えばいい:

```scala
sourceDirectories in Compile := Seq(file("sources1"), file("sources2"))
```

### 他のキーの値を基に値を計算

タスクキーやセッティングキーの値を使って他のタスクキー、セッティングキーの値を設定してみる。
これらの値を返すメソッドは特別なもので、単に `:=` や `+=` や `++=` の引数の中で呼び出してやればよい。

まず一つ目の例として、プロジェクトの名前と同じ organization（訳注：Ivy のもの、Maven でいう groupId）を定義してみよう。

```scala
// プロジェクトの後に組織名を付ける (どちらも型は SettingKey[String])
organization := name.value
```

次にこれはディレクトリ名を用いてプロジェクトの名前をつける例。

```scala
// name は Key[String]、 baseDirectory は Key[File]
// ディレクトリ名を取ってからプロジェクトの名前を付ける
name := baseDirectory.value.getName
```

この例では標準的な `java.io.File` オブジェクトの `getName` メソッドを使って  `baseDirectry` の値を変換している。

複数の入力値を用いる場合も同様である。例えばこのようになる：

```scala
name := "project " + name.value + " from " + organization.value + " version " + version.value
```

既に宣言されている name の値だけでなく organization や version といったセッティングの値を使って、新たに name というセッティングが設定されている。

#### 依存性を持ったセッティング

`name <<= baseDirectory(_.getName)` というセッティングにおいて、`name` は、`baseDirectory` への_依存性_（dependency）を持つ。上記の内容を `build.sbt` に記述して sbt のインタラクティブモードを立ち上げ、`inspect name` と入力すると、以下のように表示されるだろう（一部抜粋）:

```
[info] Dependencies:
[info]  *:baseDirectory
```

これは sbt が、どのセッティングが他のセッティングに依存しているかをどう把握しているかを示している。
タスクを記述するセッティングを思い出してほしい。そう、タスク間に依存関係を持たせることも可能であるということだ。

例えば `inspect compile` すれば compile は別のキー `compileInputs` に依存するということが分かり、
`compileInputs` を inspect すると、それがまた別のキーに依存していることが分かる。
依存性の連鎖をたどっていくと、魔法に出会う。
例えば `compile` と打ち込むと、sbt は自動的に `update` を実行する。
これが「とにかくちゃんと動く」理由は、`compile` の計算に入力として必要な値が sbt に `update` の計算を先に行うことを強制しているからだ。

このようにして、sbt の全てのビルドの依存性は、明示的には宣言されず、_自動化_されている。
あるキーの値を別の計算で使うと、その計算はキーに依存することになる。
とにかくちゃんと動いてくれるというわけだ！

#### セッティングが未定義の場合

セッティングが `:=` や `+=` や `++=` を使って自分自身や他のキーへの依存が生まれるとき、その依存されるキーの値が存在しなくてならない。
もしそれが存在しなければ sbt に怒られることになるだろう。例えば、_"Reference to undefined setting"_ のようなエラーだ。
これが起こった場合は、キーが定義されている正しい[スコープ][Scopes]で使っているか確認しよう。

これはエラーになるが、循環した依存性を作ってしまうことも起こりうる。sbt が君がそうしてしまったことを教えてくれるだろう。

#### 他のキーの値を基にしたタスク

あるタスクの値を定義するために他のタスクの値を計算する必要があるかもしれない。
そのような場合には、`:=` や `+=` や `++=` の引数に `Def.task` と `taskValue` を使えばよい。

例として、`sourceGenerators` にプロジェクトのベースディレクトリやコンパイル時のクラスパスを加える設定をみてみよう。

```scala
sourceGenerators in Compile += Def.task {
  myGenerator(baseDirectory.value, (managedClasspath in Compile).value)
}.taskValue
```

#### 依存性を持ったタスク

[.sbt ビルド定義][Basic-Def]でみたように、タスクキーは `:=` などでセッティングを作ると `Setting[T]` ではなく、`Setting[Task[T]]`を作る。
タスク定義の入力にセッティングの値を用いることができるが、セッティング定義の入力にタスクをとることはできない。

（[Keys][Keys] より）以下の二つのキーを例に説明する:

```scala
val scalacOptions = taskKey[Seq[String]]("Options for the Scala compiler.")
val checksums = settingKey[Seq[String]]("The list of checksums to generate and to verify for dependencies.")
```

（`scalacOptions` と `checksums` はお互い何の関係もない、ただ同じ値の型を持つ二つのキーで片方がタスクというだけだ）

`build.sbt` の中で `scalacOptions` を `checksums` のエイリアスにすることはできるが、その逆はできない。例えば、以下の例はコンパイルが通る:

```scala
// scalacOptions タスクは checksums セッティングの値を用いて定義される
scalacOptions := checksums.value
```

逆方向への依存、つまりタスクの値に依存したセッティングキーの値を定義することはどうしてもできない。
なぜなら、セッティングキーの値はプロジェクトのロード時に一度だけしか計算されず、毎回再実行されるべきタスクが毎回実行されなくなってしまうからだ。

```scala
// checksums セッティングは scalacOptions タスクに関連付けても、値が定まらないかもしれない
checksums := scalacOptions.value
```

### 依存性を用いた追加: `+=` と `++=`

他のキーを使って既存のセッティングキーやタスクキーへ値を追加するには `+=` を使えばよい。

例えば、プロジェクト名を使って名付けたカバレッジレポートがあって、それを `clean` が削除するファイルリストに追加するなら、このようになる:

```scala
cleanFiles += file("coverage-report-" + name.value + ".txt")
```

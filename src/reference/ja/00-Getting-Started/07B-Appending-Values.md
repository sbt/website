---
out: Appending-Values.html
---

  [Scopes]: Scopes.html

値の追加
-------

### 既存の値に追加する: `+=` と `++=`

`:=` による置換が最も単純な変換だが、キーには他のメソッドもある。
`SettingKey[T]` の `T` が列の場合、つまりキーの値の型が列の場合は、置換のかわりに列に追加することができる。

 - `+=` は、列に単一要素を追加する。
 - `++=` は、別の列を連結する。

例えば、`Compile / sourceDirectories` というキーの値の型は `Seq[File]` だ。
デフォルトで、このキーの値は `src/main/scala` を含む。
（どうしても標準的なやり方では気が済まない君が）`source` という名前のディレクトリに入ったソースもコンパイルしたい場合、
以下のようにして設定できる:

```scala
Compile / sourceDirectories += new File("source")
```

もしくは、sbt パッケージに入っている `file()` 関数を使って:

```scala
Compile / sourceDirectories += file("source")
```

（`file()` は、単に新しい `File` 作る）

`++=` を使って複数のディレクトリを一度に加える事もできる:

```scala
Compile / sourceDirectories ++= Seq(file("sources1"), file("sources2"))
```

ここでの `Seq(a, b, c, ...)` は、列を構築する標準的な Scala の構文だ。

デフォルトのソースディレクトリを完全に置き換えてしまいたい場合は、当然 `:=` を使えばいい:

```scala
Compile / sourceDirectories := Seq(file("sources1"), file("sources2"))
```

#### セッティングが未定義の場合

セッティングが `:=` や `+=` や `++=` を使って自分自身や他のキーへの依存が生まれるとき、その依存されるキーの値が存在しなくてならない。
もしそれが存在しなければ sbt に怒られることになるだろう。例えば、_"Reference to undefined setting"_ のようなエラーだ。
これが起こった場合は、キーが定義されている正しい[スコープ][Scopes]で使っているか確認しよう。

これはエラーになるが、循環した依存性を作ってしまうことも起こりうる。sbt が君がそうしてしまったことを教えてくれるだろう。

#### 他のキーの値を基にしたタスク

あるタスクの値を定義するために他のタスクの値を計算する必要があるかもしれない。
そのような場合には、`:=` や `+=` や `++=` の引数に `Def.task` を使えばよい。

例として、`sourceGenerators` にプロジェクトのベースディレクトリやコンパイル時のクラスパスを加える設定をみてみよう。

```scala
Compile / sourceGenerators += Def.task {
  myGenerator(baseDirectory.value, (Compile / managedClasspath).value)
}
```

### 依存性を用いた追加: `+=` と `++=`

他のキーを使って既存のセッティングキーやタスクキーへ値を追加するには `+=` を使えばよい。

例えば、プロジェクト名を使って名付けたカバレッジレポートがあって、それを `clean` が削除するファイルリストに追加するなら、このようになる:

```scala
cleanFiles += file("coverage-report-" + name.value + ".txt")
```

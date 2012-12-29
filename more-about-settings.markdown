---
title: 他の種類のセッティング
layout: default
---

[Keys]: http://www.scala-sbt.org/release/sxr/Keys.scala.html "Keys.scala"

# 他の種類のセッティング

[前](../scope) _始める sbt 8/14 ページ_ [次](../library-dependencies)

このページでは、基本的な `:=` メソッドを超えた、より高度な `Settings` の作り方を説明する。
君が、[.sbt ビルド定義](../basic-def)と[スコープ](../scope)を読んだことを前提とする。

## 復習: セッティング

ビルド定義は `Setting` のリストを作り、それが sbt の（キーと値のペアのマップで表現される）ビルドの記述を変換するのに使われるということは[覚えている](../basic-def)と思う。
`Setting` は、は古いマップをインプットにとり、新たなマップをアウトプットに出す変換だ。新たなマップが sbt の新しい内部状態となる。

セッティングは種類により異なる方法でマップを変換する。[これまでは](../basic-def)、`:=` メソッドをみてきた。

`:=` が作る `Setting` は、不変の固定値を新たに変換されたマップに代入する。
例えば、マップを `name := "hello"` というセッティングで変換すると、新しいマップは `name` キーの中に `"hello"` を格納する。

セッティングがその効果を発揮するにはセッティングのマスターリストに入らなくてはいけない
（`build.sbt` の全ての行は自動的にそのリストに入るけど、
[.scala ファイル](../full-def)の場合は、sbt が検知しない場所に `Setting` を作ってしまうことができる）。

## 既存の値に追加する: `+=` と `++=`

`:=` による置換が最も単純な変換だけど、キーには他のメソッドもある。
`SettingKey[T]` の `T` が列の場合、つまりキーの値の型が列の場合は、置換のかわりに列に追加することができる。

 - `+=` は、列に単一要素を追加する。
 - `++=` は、別の列を連結する。

例えば、`sourceDirectories in Compile` というキーの値の型は `Seq[File]` だ。
デフォルトで、このキーの値は `src/main/scala` を含む。
（君がどうしても非標準じゃないと気が済まないので）`source` という名前のディレクトリに入ったソースもコンパイルしたいとすると、
以下のようにして設定できる:

    sourceDirectories in Compile += new File("source")

もしくは、sbt パッケージに入っている `file()` 関数を使って:

    sourceDirectories in Compile += file("source")

（`file()` は、単に新しい `File` 作る）

`++=` を使って複数のディレクトリを一度に加える事もできる:

    sourceDirectories in Compile ++= Seq(file("sources1"), file("sources2"))

このでの `Seq(a, b, c, ...)` は、列を構築する標準的な Scala の構文だ。

デフォルトのソースディレクトリを完全に置き換えてしまいたい場合は、当然 `:=` を使えばいい:

    sourceDirectories in Compile := Seq(file("sources1"), file("sources2"))

## 値の変換: `~=`

`sourceDirectories in Compile` の_先頭に_要素を追加したり、デフォルトのディレクトリを filter したい場合はどうすればいいだろう？

既存の値に依存する `Setting` を作ることができる。

 - `~=` は、セッティングの既存の値に関数を適用して、同じ型の新たな値を作る

`sourceDirectories in Compile` を変更するには、以下のように `~=` を用いる:

    // src/main/scala を filter out する
    sourceDirectories in Compile ~= { srcDirs => srcDirs filter(!_.getAbsolutePath.endsWith("src/main/scala")) }

ここでは、`srcDirs` は匿名関数のパラメータで、`sourceDirectories in Compile` の古い値が匿名関数に渡される。
この関数の戻り値が `sourceDirectories in Compile` の新たな値となる。

もっと簡単な例だと:

    // プロジェクト名を大文字にする
    name ~= { _.toUpperCase }

関数は、キーの値を同じ型の別の値に変換するため、キーの型が `SettingKey[T]` か `TaskKey[T]` のとき、`~=` に渡す関数は常に `T => T` 型でなければいけない。

## 別のキーの値から値を計算する: `<<=`

`~=` は、キーの古い値に基づいて新たな値を定義する。
だけど、_他の_キーの値に基づいて値を定義したいとしたらどうだろう？

 - `<<=` は、任意の他のキー（複数のキーも可）を使って新たな値を計算する

`<<=` は、型 `Initialize[T]` の引数を一つ取る。
`Initialize[T]` のインスタンスは、あるキーの集合に関連付けられた値をインプットに取り、その値に基づいて型`T`の値を返す。
つまり、型`T` の値を初期化する。

（`:=`、`+=`、`~=` その他同様）`Initialize[T]` を受け取った `<<=` は、`Setting[T]` を返す。

### 単純な `Initialize[T]`: 単一のキーに依存した `<<=`

全てのキーは `Initilize` trait を拡張するため、最も単純な `Initialize` は、ただのキーだ:

    // 意味はないけど、妥当だ
    name <<= name

`Initialize[T]` として取り扱った場合、`SettingKey[T]` はその現在値を計算する。
そのため、`name <<= name` は `name` の値を `name` の値に代入する。

_別の_キーをキーに代入することで、少しは役に立つようになる。キーは同じ値の型を持たなくてはいけない。

    // プロジェクト名を使って組織名を命名する（両者とも SettingKey[String]）
    organization <<= name

（注意: これが別のキーへのエイリアスの作り方だ）

値の型が同一じゃない場合は、`Initialize[T]` から例えば `Initialize[S]` みたいな感じで別の型にしてやる必要がある。
これには、以下のように `Initialize` の `apply` メソッドを使う:

    // name は Key[String] で、baseDirectory は Key[File] だ。
    // プロジェクトの現在ディレクトリに基づいて名前を付ける。
    name <<= baseDirectory.apply(_.getName)

`apply` は Scala の特殊なメソッドで、関数の呼び出し構文を使ってオブジェクトを叩くことができるため、このように書ける:

    name <<= baseDirectory(_.getName)

これは、`baseDirectory` の値を、`File` を受け取り `String` を返す `_.getName` という関数を使って変換する。
`getName` は、普通の `java.io.File` オブジェクトにあるメソッドだ。

### 依存性を持ったセッティング

`name <<= baseDirectory(_.getName)` というセッティングにおいて、`name` は、`baseDirectory` に_依存性_（dependency）を持つ。上記を `build.sbt` に書いて、sbt のインタラクティブモードで走らせ、`inspect name` と打ち込むと、以下のように表示される（一部抜粋）:

    [info] Dependencies:
    [info] 	*:base-directory

このようにして、sbt はどのセッティングが別のセッティングに依存するかを知っている。
タスクを記述するセッティングもあるため、この方法でタスク間の依存性も作ることができる。

例えば、`inspect compile` すると、`compile-inputs` に依存することが分かり、
`compile-input` を inspect すると、それがまた別のキーに依存していることが分かる。
依存性の連鎖をたどっていくと、魔法に出会う。
例えば、`compile` と打ち込むと、sbt は自動的に `update` を実行する。
これが「とにかくちゃんと動く」理由は、`compile` の計算にインプットとして必要な値が、`update` の計算を先に行うことを強制しているからだ。

このようにして、sbt の全てのビルドの依存性は、明示的には宣言されず、_自動化_されている。
あるキーの値を別の計算で使うと、その計算はキーに依存する。とにかくちゃんと動く！

### 複雑な `Initialize[T]`: 複数のキーへ依存する `<<=`

複数のキーへの依存性をサポートするために、sbt は、`Initialize` オブジェクトのタプルに `apply` メソッドと `identity` メソッドを追加する。
Scala では、タプルを `(1, "a")` のように書く（これは、`(Int, String)` の型を持つ）。

例えば、ここに三つの `Initialize` オブジェクトから成るタプルがあるとするとき、
その型は `(Initialize[A], Initialize[B], Initialize[C])` だ。
全ての `SettingKey[T]` は、`Initialize[T]` のインスタンスでもあるため、この `Initialize` オブジェクトはキーでもよい。

以下に、全てのキーが文字列の場合の単純な例を示す:

    // 三つの SettingKey[String] のタプル。三つの Initialize[String] の他プリでもある。
    (name, organization, version)

`Initialize` のタプルの `apply` メソッドは、関数を引数として受け取る。
タプル中のそれぞれの `Initialize` を使って、sbt は対応する値を計算する（キーの現在値）。
これらの値は関数に渡され、その関数は_単一の_値を返し、これは新たな `Initialize` でラッピングされる。
明示的な型を書き下すと（Scala はこれを強要しない）、こんな感じ:

    val tuple: (Initialize[String], Initialize[String], Initialize[String]) = (name, organization, version)
    val combined: Initialize[String] = tuple.apply({ (n, o, v) =>
        "project " + n + " from " + o + " version " + v })
    val setting: Setting[String] = name <<= combined

それぞれのキーは既に `Initialize` 型だけど、（キーのような）単純な `Initialize` をタプルに入れて、`appy` メソッドを呼び出すことで最大九つまで一つの `Initialize` として合成できる。

`SettingKey[T]` の `<<=` メソッドは、`Initialize[T]` を受け取るため、このテクニックを使って複数の任意のキーへの依存性を作ることができる。

Scala では関数の呼び出し構文が `apply` メソッドを呼び出すため、明示的な `.apply` を省いて、`tupple` を関数として扱い、以下のように書きなおすことができる:

    val tuple: (Initialize[String], Initialize[String], Initialize[String]) = (name, organization, version)
    val combined: Initialize[String] = tuple({ (n, o, v) =>
        "project " + n + " from " + o + " version " + v })
    val setting: Setting[String] = name <<= combined

`.sbt` ファイルに書くことが許されているのは単一の式だけであり、複数の文は書けないため、
`build.sbt` では、このような `val` を中間値として使ったコードは動作しない。

そこで、`build.sbt` では、以下のような、より簡略化した構文が用いられる:

    name <<= (name, organization, version) { (n, o, v) => "project " + n + " from " + o + " version " + v }

ここでは、`Initialize` のタプル（`SettingKey` のタプルでもある）が関数のようにはたらき、
`{}` で囲まれた匿名関数を受け取り、`Initialize[T]`（`T` は匿名関数の戻り値の型）を返している。

`Initialize` のタプルは、`identity` というメソッドも持ち、これは単にタプル値を `Initialize` に入れて返す。
`(a: Initialize[A], b: Initialize[B]).identity`
は、`Initialize[(A, B)]` 型の値を返す。
`identity` は、二つの `Initialize` を値を変更したり失うこと無く一つに合成する。

### セッティングが未定義の場合

セッティングが `~=` や `<<=` を使って自分自身や他のキーへの依存性を作る場合、
依存されたキーには値が存在しなくてはならない。
存在しなければ、sbt に怒られる。
例えば、_"Reference to undefined setting"_ なんて言われるかもしれない。
これが起こった場合は、キーが定義されている正しい[スコープ](../scope)で使っているか確認しよう。

環状の依存性を作ってしまうことも可能で、これもまたエラーになり、sbt に怒られる。

### 依存性を持ったタスク

[.sbt ビルド定義](../basic-def)でみた通り、タスクキーは `:=`、`<<=`、その他でセッティングを作ると
`Setting[T]` ではなく、`Setting[Task[T]]`を作る。
同様に、タスクキーは `Initialize[T]` ではなく、`Initialize[Task[T]]` のインスタンスで、
タスクキーの `<<=` は `Initialize[Task[T]]` をパラメータとして受け取る。

この実践上の大切さは、非タスクのセッティングはタスクを依存性としてもつことができないということだ。

（[Keys] より）以下の二つのキーを例に説明する:

    val scalacOptions = TaskKey[Seq[String]]("scalac-options", "Options for the Scala compiler.")
    val checksums = SettingKey[Seq[String]]("checksums", "The list of checksums to generate and to verify for dependencies.")

（`scalacOptions` と `checksums` は、同じ値の型を持つ二つのキーで、片方がタスクというだけで、お互い全く関係のないキーだ。）

どちらか一方をもう片方のエイリアスにしようとしても、`build.sbt` をコンパイルすることができない:

<pre>
scalacOptions <<= checksums

checksums <<= scalacOptions
</pre>

問題は、`scalacOptions.<<=` は、`Initialize[Task[Seq[String]]]` を受け取り、
`checksums.<<=` は、`Initialize[Seq[String]]` を受け取るということだ。
だけど、`Initialize[T]` から `Initialize[Task[T]]` に変換する方法があり、`map` と呼ばれる。

（`identity` は標準の Scala 関数で、与えられたインプットを返す）

これを_逆方向_、つまりセッティングキーをタスクキーに依存されることは不可能だ。
これは、セッティングキーがプロジェクトの読み込み時に一度だけ計算されるため、
タスクが再実行されなくなってしまうが、
タスクは毎回再実行されることが期待されているからだ。

タスクはセッティングと他のタスクとの両方に依存することができる。
`apply` のかわりに `map` を使うことで、`Initialize[T]` のかわりに `Initialize[Task[T]]` を作る。
非タスクセッティングでの `apply` の用法は以下のようだ:

    name <<= (name, organization, version) { (n, o, v) => "project " + n + " from " + o + " version " + v }

(`(name, organization, version)` には apply メソッドがあるため、これは中括弧 `{}` で囲まれた匿名関数をパラメータとして受け取る関数だ。)

`Initialize[Task[T]]` を作るには、`apply` のかわりに `map` を使う:

<pre>
// （<<= の左辺値の）name はタスクではなく、かつ map を使っているため、でコンパイルが通らない
name <<= (name, organization, version) map { (n, o, v) => "project " + n + " from " + o + " version " + v }

// packageBin はタスクであり、かつ map を使っているため、コンパイルは通る
packageBin in Compile <<= (name, organization, version) map { (n, o, v) => file(o + "-" + n + "-" + v + ".jar") }

// name がタスクではなく、かつ apply を使っているため、コンパイルは通る
name <<= (name, organization, version) { (n, o, v) => "project " + n + " from " + o + " version " + v }

// packageBin はタスクであり、かつ apply を使っているため、コンパイルは通らない
packageBin in Compile <<= (name, organization, version) { (n, o, v) => file(o + "-" + n + "-" + v + ".jar") }
</pre>

_まとめると:_ キーのタプルを `Initialize[Task[T]]` に変換したいときは `map` を使う。
キーのタプルを `Initialize[T]` に変換したいときは `apply` を使う。
`<<=` の左辺のキーが `SettingKey[T]` ではなくて、`TaskKey[T]` であるときに、`Initialize[Task[T]]` が必要となる。

### エイリアスに必要なのは `:=` ではなく、`<<=`

あるキーが別のキーのエイリアスになって欲しいとき、つい `:=` を使って以下のような間違ったエイリアスを作ってしまうかもしれない:

    // 動作しないし、役に立たない
    packageBin in Compile := packageDoc in Compile

問題は `:=` の引数は値（タスクの場合は値を返す関数）でなくちゃいけないことだ。
`TaskKey[File]` である `packageBin` の場合は、`File` もしくは `=> File` 関数でなければいけない。
`packageDoc` は、キーであり、`File` ではない。

正しい方法は、キーを受け取る `<<=` を使うことだ（実際に受け取っているのは `Initialize` だけど、全てのキーは `Initialize` のインスタンスでもある）:

    // 動作するけど、やっぱり役に立たない
    packageBin in Compile <<= packageDoc in Compile

ここで、`<<=` は、後で（sbt がタスクを実行したとき）ファイルを返す計算である `Initialize[Task[File]]` を期待する。
これが思った通りの振る舞いだ。つまり、タスクのエイリアスを作ったときに期待されるのは、そのをタスクを実行することであって、
sbt がプロジェクトを読み込んだ時に一回だけ値を読み込むことではない。

（ちなみに、`packageBin` のようなパッケージ化タスクは、グローバルではなく、コンフィギュレーションごとに定義されているので、`in Compile` スコープが無ければ「未定義」エラーが発生する。）

## 依存性を持った追加: `<+=` and `<++=`

リストに追加するためのメソッドにはもう二つあり、それらは `+=` や `++=` を `<<=` と組み合わせたものだ。
つまり、他のキーへの依存性を使いながらリストの新しい値や連結するための別のリストを計算できる。

これらのメソッドは、依存性から得られた値を変換するのに書く関数が `T` のかわりに `Seq[T]` を返さなくてはいけないこと以外の点では
`<<=` と全く同じように動作する。

当然、`<<=` のように既存の値を置き換えるのではなく、`<+=` と `<++=` は既存の値に追加するわけだけど。

例えば、プロジェクト名を使って名付けたカバレッジレポートがあるとして、それを `clean` が削除するファイルのリストに追加したいとする:

    cleanFiles <+= (name) { n => file("coverage-report-" + n + ".txt") }

## 続いては

これでセッティングを使って色々できるようになったから、よく使われる、ある特定のキーの説明をしよう: `libraryDependencies`。
続きは、[ライブラリ依存性の説明](../library-dependencies)で。

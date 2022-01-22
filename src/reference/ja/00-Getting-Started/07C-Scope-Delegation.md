---
out: Scope-Delegation.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html

スコープ委譲 (.value の照会)
--------------------------

このページはスコープ委譲を説明する。前のページの
[.sbt ビルド定義][Basic-Def]、
[スコープ][Scopes]
を読んで理解したことを前提とする。

スコープ付けの説明が全て終わったので、`.value` 照会の詳細を解説できる。
難易度は高めなので、始めてこのガイドを読む場合はこのページは飛ばしてもいい。

これまでに習ったことをおさらいしておこう。

- スコープは、サブプロジェクト軸、コンフィギュレーション軸、タスク軸という 3つの軸の成分を持つタプルである。
- 全てのスコープ軸には、`Zero` 特殊なスコープ成分がある。
- **サブプロジェクト軸**においてのみ、`ThisBuild` 特殊なスコープ成分がある。
- `Test` コンフィギュレーションは `Runtime` を拡張し、`Runtime` は `Compile` を拡張する。
- build.sbt に書かれたキーは、デフォルトで `\${current subproject} / Zero / Zero` にスコープ付けされる。
- キーは、`/` 演算子を使ってさらにスコープ付けできる。

以下のようなビルド定義を考える:

@@snip [build.sbt]($root$/src/sbt-test/ref/scope-delegation/x/build.sbt) { #fig1 }

`foo` のセッティング本文内において、スコープ付きキー `Test / bar` への依存性が宣言されている。
しかし、`projX` において `Test / bar` が未定義であるにも関わらず、sbt
は別のスコープ付きキーへと解決して `foo` は `2` に初期化される。

sbt はキーのフォールバックのための検索パスを厳密に定義し、これを**スコープ委譲** (scope delegation) と呼ぶ。
この機能により、より一般的なスコープで一度だけ値を代入して、複数のより特定なスコープがその値を継承することを可能とする。

### スコープ委譲のルール

スコープ委譲のルールは以下の通り:

- ルール 1: スコープ軸は以下の優先順位を持つ: サブプロジェクト軸、コンフィギュレーション軸、そしてタスク軸。
- ルール 2: あるスコープが与えられたとき、委譲スコープは以下の順にタスク軸を置換することで検索される:
  与えられたタスクスコープ、それから `Zero` (これはタスクスコープ付けを行わないもののこと)。
- ルール 3: あるスコープが与えられたとき、委譲スコープは以下の順にコンフィギュレーション軸を置換することで検索される:
  与えられたコンフィギュレーション、その親、その親の親...、そして `Zero` ( これはコンフィギュレーションのスコープ付けを行わないものと同じ)。
- ルール 4: あるスコープが与えられたとき、委譲スコープは以下の順にサブプロジェクト軸を置換することで検索される:
  与えられたサブプロジェクト、`ThisBuild` そして `Zero`。
- ルール 5: 委譲されたスコープ付きのキー及びそれが依存するセッティングとタスクは、元のコンテキストを一切引き継がずに評価される。

それぞれのルールを以下に説明していく。

### ルール 1: スコープ軸の優先順位

- ルール 1: スコープ軸は以下の優先順位を持つ: サブプロジェクト軸、コンフィギュレーション軸、そしてタスク軸。

言い換えると、2つのスコープ候補があるとき、一方がサブプロジェクト軸により特定な値を持つとき、コンフィギュレーションやタスク軸のスコープに関わらず必ず勝つということだ。
同様に、サブプロジェクトが同じ場合、コンフィギュレーションに特定な値を持つものがタスクのスコープ付けに関わらず勝つ。
「より特定」とは何かは、以下のルールで定義していく。

### ルール 2: タスク軸の委譲

- ルール 2: あるスコープが与えられたとき、委譲スコープは以下の順にタスク軸を置換することで検索される:
  与えられたタスクスコープ、それから `Zero` (これはタスクスコープ付けを行わないもののこと)。

ここでやっとキーが与えられたとき sbt がどのようにして委譲スコープを生成するかの具体的なルールが出てきた。
任意の `(xxx / yyy).value` が与えられたときに、どのような検索パスを取るかを示していることに注目してほしい。

**練習問題 A**: 以下のビルド定義を考える:

```scala
lazy val projA = (project in file("a"))
  .settings(
    name := {
      "foo-" + (packageBin / scalaVersion).value
    },
    scalaVersion := "2.11.11"
  )
```

`name in projA` (sbt シェルだと `projA/name`) の値は何か?

1. `"foo-2.11.11"`
2. `"foo-$example_scala_version$"`
3. その他

正解は `"foo-2.11.11"`。
`.settings(...)` 内において、`scalaVersion` は自動的に `projA / Zero / Zero` にスコープ付けされるため、
`packageBin / scalaVersion` は `projA / Zero / packageBin / scalaVersion` となる。
そのスコープ付きキーは未定義だ。
ルール 2に基いて、sbt はタスク軸を `Zero` に置換して `projA / Zero / Zero` になる (`projA / scalaVersion`)。
そのスコープ付きキーは `"2.11.11"` として定義されている。

### ルール 3: コンフィギュレーション軸の検索パス

- ルール 3: あるスコープが与えられたとき、委譲スコープは以下の順にコンフィギュレーション軸を置換することで検索される:
  与えられたコンフィギュレーション、その親、その親の親...、そして `Zero` ( これはコンフィギュレーションのスコープ付けを行わないものと同じ)。

これを説明する例は上に見た `projX` だ:

@@snip [build.sbt]($root$/src/sbt-test/ref/scope-delegation/x/build.sbt) { #fig1 }

フルスコープを書き出してみると `projX / Test / Zero` となる。
また、`Test` コンフィギュレーションは `Runtime` を拡張し、`Runtime` は `Compile` を拡張することを思い出してほしい。

`Test / bar` は未定義だが、ルール3 に基いて sbt
は `projX / Test / Zero`、`projX / Runtime / Zero`、そして
`projX / Compile / Zero` の順に `bar` をスコープ付けして検索していく。
最後のものが見つかり、それは `Compile / bar` だ。

### ルール 4: サブプロジェクト軸の検索パス

- ルール 4: あるスコープが与えられたとき、委譲スコープは以下の順にサブプロジェクト軸を置換することで検索される:
  与えられたサブプロジェクト、`ThisBuild` そして `Zero`。

**練習問題 B**: 以下のビルド定義を考える:

```scala
ThisBuild / organization := "com.example"

lazy val projB = (project in file("b"))
  .settings(
    name := "abc-" + organization.value,
    organization := "org.tempuri"
  )
```

`name in projB` (sbt シェルだと `projB/name`) の値は何か?

1. `"abc-com.example"`
2. `"abc-org.tempuri"`
3. その他

正解は `abc-org.tempuri` だ。
ルール 4に基づき、最初の検索パスは `projB / Zero / Zero` にスコープ付けされた `organization` で、
これは `projB` 内で `"org.tempuri"` として定義されている。
これは、ビルドレベルのセッティングである `ThisBuild / organization` よりも高い優先順位を持つ。

#### スコープ軸の優先順位、再び

**練習問題 C**: 以下のビルド定義を考える:

@@snip [build.sbt]($root$/src/sbt-test/ref/scope-delegation/x/build.sbt) { #fig_c }

`projC / name` の値は何か?

1. `"foo-2.12.2"`
2. `"foo-2.11.11"`
3. その他

正解は `foo-2.11.11`。
`projC / Zero / packageBin` にスコープ付けされた `scalaVersion` は未定義だ。
ルール 2 は `projC / Zero / Zero` を見つける。ルール 4 は `ThisBuild / Zero / packageBin` を見つける。
ルール 1 の規定により、より特定なサブプロジェクト軸が勝ち、それは
`projC / Zero / Zero` で `"2.11.11"` と定義されている。

**練習問題 D**: 以下のビルド定義を考える:

@@snip [build.sbt]($root$/src/sbt-test/ref/scope-delegation/x/build.sbt) { #fig_d }

`projD/test` を実行した場合の出力は何か?

1. `List()`
2. `List(-Ywarn-unused-import)`
3. その他

正解は `List(-Ywarn-unused-import)`。
ルール 2 は `projD / Compile / Zero` を見つけ、
ルール 3 は `projD / Zero / console` を見つけ、
ルール 4 は `ThisBuild / Zero / Zero` を見つける。
`projD / Compile / Zero` はサブプロジェクト軸に `projD` を持ち、
またコンフィギュレーション軸はタスク軸よりも高い優先順位を持つのでルール 1 は
`projD / Compile / Zero` を選択する。

次に、`Compile / scalacOptions` は `scalacOptions.value` を参照するため、
`projD / Zero / Zero` のための委譲を探す必要がある。
ルール 4 は `ThisBuild / Zero / Zero` を見つけ、これは `List(-Ywarn-unused-import)` に解決される。

### inspect コマンドは委譲スコープを列挙する

何が起こっているのか手早く調べたい場合は `inspect` を使えばいい。

```
sbt:projd> inspect projD / Compile / console / scalacOptions
[info] Task: scala.collection.Seq[java.lang.String]
[info] Description:
[info]  Options for the Scala compiler.
[info] Provided by:
[info]  ProjectRef(uri("file:/tmp/projd/"), "projD") / Compile / scalacOptions
[info] Defined at:
[info]  /tmp/projd/build.sbt:9
[info] Reverse dependencies:
[info]  projD / test
[info]  projD / Compile / console
[info] Delegates:
[info]  projD / Compile / console / scalacOptions
[info]  projD / Compile / scalacOptions
[info]  projD / console / scalacOptions
[info]  projD / scalacOptions
[info]  ThisBuild / Compile / console / scalacOptions
[info]  ThisBuild / Compile / scalacOptions
[info]  ThisBuild / console / scalacOptions
[info]  ThisBuild / scalacOptions
[info]  Zero / Compile / console / scalacOptions
[info]  Zero / Compile / scalacOptions
[info]  Zero / console / scalacOptions
[info]  Global / scalacOptions
....
```

"Provided by" は `projD / Compile / console / scalacOptions` が
`projD / Compile / scalacOptions` によって提供されることを表示しているのに注目してほしい。
"Delegates" 以下に**全て**の委譲スコープ候補が優先順に列挙されている!

- サブプロジェクト軸が `projD` にスコープ付けされているスコープが当然最初に表示されて、`ThisBuild`、`Zero` と続いている。
- サブプロジェクト内だと、コンフィギュレーション軸が `Compile` にスコープ付けされいるのが最初に表示されて、`Zero` にフォールバックしている。
- 最後に、タスク軸は与えられたタスクスコープ付けの `cosole /` が来て、次にタスクスコープ無しが来ている。

### .value 参照 vs 動的ディスパッチ

- ルール 5: 委譲されたスコープ付きのキー及びそれが依存するセッティングとタスクは、元のコンテキストを一切引き継がずに評価される。

スコープ委譲はオブジェクト指向言語のクラス継承に似ていると思うかもしれないが、注意するべき違いがある。
Scala のような OO言語では、`Shape` トレイトに `drawShape` というメソッドがあれば、たとえそれが
`Shape` トレイトの他のメソッドから呼ばれているとしても子クラス側で振る舞いをオーバーライドすることができ、これは動的ディスパッチと呼ばれる。

一方 sbt は、スコープ委譲によってあるスコープをより一般的なスコープに委譲することができ、
例えばプロジェクトレベルのセッティングからビルドレベルのセッティングへ委譲といったことができるが、
ビルドレベルのセッティングはプロジェクトレベルのセッティングを参照することはできない。

**練習問題 E**: 以下のビルド定義を考える:

```scala
lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.2",
      version      := scalaVersion.value + "_0.1.0"
    )),
    name := "Hello"
  )

lazy val projE = (project in file("e"))
  .settings(
    scalaVersion := "2.11.11"
  )
```

`projE / version` の値は何か?

1. `"2.12.2_0.1.0"`
2. `"2.11.11_0.1.0"`
3. その他

正解は `"2.12.2_0.1.0"`。
`projE / version` は `ThisBuild / version` に委譲する。
一方 `ThisBuild / version` は `ThisBuild / scalaVersion` に依存する。
このように振る舞うため、ビルドレベルのセッティングは単純な値の代入に限定するべきだ。

**練習問題 F**: 以下のビルド定義を考える:

```scala
ThisBuild / scalacOptions += "-D0"
scalacOptions += "-D1"

lazy val projF = (project in file("f"))
  .settings(
    compile / scalacOptions += "-D2",
    Compile / scalacOptions += "-D3",
    Compile / compile / scalacOptions += "-D4",
    test := {
      println("bippy" + (Compile / compile / scalacOptions).value.mkString)
    }
  )
```

`projF/test` を実行した場合の出力は何か?

1. `"bippy-D4"`
2. `"bippy-D2-D4"`
3. `"bippy-D0-D3-D4"`
4. その他

正解は `"bippy-D0-D3-D4"`。
これは、[Paul Phillips](https://gist.github.com/paulp/923154ab2d61882195cdea47483592ca)
さんが考案した練習問題を元にしている。

`someKey += "x"` は以下のように展開されるため、全てのルールをデモする素晴らしい問題だ。

```scala
someKey += {
  val old = someKey.value
  old :+ "x"
}
```

このとき、古い方の `.value` を取得するときに委譲が発生して、ルール5 に基いてそれは別のスコープ付きキー扱いする必要がある。
まずは `+=` を取り除いて、古い `.value` の委譲が何になるかをコメントで注釈する。

```scala
ThisBuild / scalacOptions := {
  // Global / scalacOptions <- Rule 4
  val old = (ThisBuild / scalacOptions).value
  old :+ "-D0"
}

scalacOptions := {
  // ThisBuild / scalacOptions <- Rule 4
  val old = scalacOptions.value
  old :+ "-D1"
}

lazy val projF = (project in file("f"))
  .settings(
    compile / scalacOptions := {
      // ThisBuild / scalacOptions <- Rules 2 and 4
      val old = (compile / scalacOptions).value
      old :+ "-D2"
    },
    Compile / scalacOptions := {
      // ThisBuild / scalacOptions <- Rules 3 and 4
      val old = (Compile / scalacOptions).value
      old :+ "-D3"
    },
    Compile / compile / scalacOptions := {
      // projF / Compile / scalacOptions <- Rules 1 and 2
      val old = (Compile / compile / scalacOptions).value
      old :+ "-D4"
    },
    test := {
      println("bippy" + (Compile / compile / scalacOptions).value.mkString)
    }
  )
```

評価するとこうなる:

```scala
ThisBuild / scalacOptions := {
  Nil :+ "-D0"
}

scalacOptions := {
  List("-D0") :+ "-D1"
}

lazy val projF = (project in file("f"))
  .settings(
    compile / scalacOptions := List("-D0") :+ "-D2",
    Compile / scalacOptions := List("-D0") :+ "-D3",
    Compile / compile / scalacOptions := List("-D0", "-D3") :+ "-D4",
    test := {
      println("bippy" + (Compile / compile / scalacOptions).value.mkString)
    }
  )
```

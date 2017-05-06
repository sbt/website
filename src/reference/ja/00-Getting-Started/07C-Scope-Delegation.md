---
out: Scope-Delegation.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html

スコープ委譲 (.value の照会)
--------------------------

このページはスコープ委譲を説明する。前のページの
[.sbt ビルド定義][Basic-Def]、
[スコープ][Scopes-Graph]
を読んで理解したことを前提とする。

スコープ付けの説明が全て終わったので、`.value` 照会の詳細を解説できる。
難易度は高めなので、始めてこのガイドを読む場合はこのページは飛ばしてもいい。

`Global` という用語はスコープ成分としての `*` と、
`(Global, Global, Global)` の短縮形の両方の意味で使われて分かりづらいので、
このページでスコープ成分を指すときは `*` というシンボルを用いる。

これまでに習ったことをおさらいしておこう。

- スコープは、サブプロジェクト軸、コンフィギュレーション軸、タスク軸という 3つの軸の成分を持つタプルである。
- 全てのスコープ軸には、`*` (`Global` とも呼ばれる) 特殊なスコープ成分がある。
- **サブプロジェクト軸**においてのみ、`ThisBuild` (シェルでは `{.}` と表記される) 特殊なスコープ成分がある。
- `Test` コンフィギュレーションは `Runtime` を拡張し、`Runtime` は `Compile` を拡張する。
- build.sbt に書かれたキーは、デフォルトで `(\${current subproject}, *, *)` にスコープ付けされる。
- キーは、`.in(...)` メソッドを使ってさらにスコープ付けできる。

以下のようなビルド定義を考える:

```scala
lazy val foo = settingKey[Int]("")
lazy val bar = settingKey[Int]("")

lazy val projX = (project in file("x"))
  .settings(
    foo := {
      (bar in Test).value + 1
    },
    bar in Compile := 1
  )
```

`foo` のセッティング本文内において、スコープ付きキー `(bar in Test)` への依存性が宣言されている。
しかし、`projX` において `bar in Test` が未定義であるにも関わらず、sbt
は別のスコープ付きキーへと解決して `foo` は `2` に初期化される。

sbt はキーのフォールバックのための検索パスを厳密に定義し、これを**スコープ委譲** (scope delegation) と呼ぶ。
この機能により、より一般的なスコープで一度だけ値を代入して、複数のより特定なスコープがその値を継承することを可能とする。

### スコープ委譲のルール

スコープ委譲のルールは以下の通り:

- ルール 1: スコープ軸は以下の優先順位を持つ: サブプロジェクト軸、コンフィギュレーション軸、そしてタスク軸。
- ルール 2: あるスコープが与えられたとき、委譲スコープは以下の順にタスク軸を置換することで検索される:
  与えられたタスクスコープ、それから `*` (`Global`、これはタスクスコープ付けを行わないもののこと)。
- ルール 3: あるスコープが与えられたとき、委譲スコープは以下の順にコンフィギュレーション軸を置換することで検索される:
  与えられたコンフィギュレーション、その親、その親の親...、そして `*` (`Global` これはコンフィギュレーションのスコープ付けを行わないものと同じ)。
- ルール 4: あるスコープが与えられたとき、委譲スコープは以下の順にサブプロジェクト軸を置換することで検索される:
  与えられたサブプロジェクト、`ThisBuild` そして `*` (`Global`)。
- ルール 5: 委譲されたスコープ付きのキー及びそれが依存するセッティングとタスクは、元のコンテキストを一切引き継がずに評価される。

それぞれのルールを以下に説明していく。

### ルール 1: スコープ軸の優先順位

- ルール 1: スコープ軸は以下の優先順位を持つ: サブプロジェクト軸、コンフィギュレーション軸、そしてタスク軸。

言い換えると、2つのスコープ候補があるとき、一方がサブプロジェクト軸により特定な値を持つとき、コンフィギュレーションやタスク軸のスコープに関わらず必ず勝つということだ。
同様に、サブプロジェクトが同じ場合、コンフィギュレーションに特定な値を持つものがタスクのスコープ付けに関わらず勝つ。
「より特定」とは何かは、以下のルールで定義していく。

### ルール 2: タスク軸の委譲

- ルール 2: あるスコープが与えられたとき、委譲スコープは以下の順にタスク軸を置換することで検索される:
  与えられたタスクスコープ、それから `*` (`Global`、これはタスクスコープ付けを行わないもののこと)。

ここでやっとキーが与えられたとき sbt がどのようにして委譲スコープを生成するかの具体的なルールが出てきた。
任意の `(xxx in yyy).value` が与えられたときに、どのような検索パスを取るかを示していることに注目してほしい。

**練習問題 A**: 以下のビルド定義を考える:

```scala
lazy val projA = (project in file("a"))
  .settings(
    name := {
      "foo-" + (scalaVersion in packageBin).value
    },
    scalaVersion := "2.11.11"
  )
```

`name in projA` (sbt シェルだと `projA/name`) の値は何か?

1. `"foo-2.11.11"`
2. `"foo-$example_scala_version$"`
3. その他

正解は `"foo-2.11.11"`。
`.settings(...)` 内において、`scalaVersion` は自動的に `(projA, *, *)` にスコープ付けされるため、
`scalaVersion in packageBin` は `scalaVersion in (projA, *, packageBin)` となる。
そのスコープ付きキーは未定義だ。
ルール 2に基いて、sbt はタスク軸を `*` に置換して `(projA, *, *)` になる (シェル表記だと `proj/scalaVersion`)。
そのスコープ付きキーは `"2.11.11"` として定義されている。

### ルール 3: コンフィギュレーション軸の検索パス

- ルール 3: あるスコープが与えられたとき、委譲スコープは以下の順にコンフィギュレーション軸を置換することで検索される:
  与えられたコンフィギュレーション、その親、その親の親...、そして `*` (`Global` これはコンフィギュレーションのスコープ付けを行わないものと同じ)。

これを説明する例は上に見た `projX` だ:

```scala
lazy val foo = settingKey[Int]("")
lazy val bar = settingKey[Int]("")

lazy val projX = (project in file("x"))
  .settings(
    foo := {
      (bar in Test).value + 1
    },
    bar in Compile := 1
  )
```

フルスコープを書き出してみると `(projX, Test, *)` となる。
また、`Test` コンフィギュレーションは `Runtime` を拡張し、`Runtime` は `Compile` を拡張することを思い出してほしい。

`(bar in Test)` は未定義だが、ルール3 に基いて sbt
は `(projX, Test, *)`、`(projX, Runtime, *)`、そして
`(projX, Compile, *)` の順に `bar` をスコープ付けして検索していく。
最後のものが見つかり、それは `bar in Compile` だ。

### ルール 4: サブプロジェクト軸の検索パス

- ルール 4: あるスコープが与えられたとき、委譲スコープは以下の順にサブプロジェクト軸を置換することで検索される:
  与えられたサブプロジェクト、`ThisBuild` そして `*` (`Global`)。

**練習問題 B**: 以下のビルド定義を考える:

```scala
organization in ThisBuild := "com.example"

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
ルール 4に基づき、最初の検索パスは `(projB, *, *)` にスコープ付けされた `organization` で、
これは `projB` 内で `"org.tempuri"` として定義されている。
これは、ビルドレベルのセッティングである `organization in ThisBuild` よりも高い優先順位を持つ。

#### スコープ軸の優先順位、再び

**練習問題 C**: 以下のビルド定義を考える:

```scala
scalaVersion in (ThisBuild, packageBin) := "2.12.2"

lazy val projC = (project in file("c"))
  .settings(
    name := {
      "foo-" + (scalaVersion in packageBin).value
    },
    scalaVersion := "2.11.11"
  )
```

`name in projC` の値は何か?

1. `"foo-2.12.2"`
2. `"foo-2.11.11"`
3. その他

正解は `foo-2.11.11`。
`(projC, *, packageBin)` にスコープ付けされた `scalaVersion` は未定義だ。
ルール 2 は `(projC, *, *)` を見つける。ルール 4 は `(ThisBuild, *, packageBin)` を見つける。
ルール 1 の規定により、より特定なサブプロジェクト軸が勝ち、それは
`(projC, *, *)` で `"2.11.11"` と定義されている。

**練習問題 D**: 以下のビルド定義を考える:

```scala
scalacOptions in ThisBuild += "-Ywarn-unused-import"

lazy val projD = (project in file("d"))
  .settings(
    test := {
      println((scalacOptions in (Compile, console)).value)
    },
    scalacOptions in console -= "-Ywarn-unused-import",
    scalacOptions in Compile := scalacOptions.value // added by sbt
  )
```

`projD/test` を実行した場合の出力は何か?

1. `List()`
2. `List(-Ywarn-unused-import)`
3. その他

正解は `List(-Ywarn-unused-import)`。
ルール 2 は `(projD, Compile, *)` を見つけ、
ルール 3 は `(projD, *, console)` を見つけ、
ルール 4 は `(ThisBuild, *, *)` を見つける。
`(projD, Compile, *)` はサブプロジェクト軸に `projD` を持ち、
またコンフィギュレーション軸はタスク軸よりも高い優先順位を持つのでルール 1 は
`(projD, Compile, *)` を選択する。

次に、`scalacOptions in Compile` は `scalacOptions.value` を参照するため、
`(projD, *, *)` のための委譲を探す必要がある。
ルール 4 は `(ThisBuild, *, *)` を見つけ、これは `List(-Ywarn-unused-import)` に解決される。

### inspect コマンドは委譲スコープを列挙する

何が起こっているのか手早く調べたい場合は `inspect` を使えばいい。

```
Hello> inspect projD/compile:console::scalacOptions
[info] Task: scala.collection.Seq[java.lang.String]
[info] Description:
[info]  Options for the Scala compiler.
[info] Provided by:
[info]  {file:/Users/xxxx/}projD/compile:scalacOptions
[info] Defined at:
[info]  /Users/xxxx/build.sbt:47
[info] Reverse dependencies:
[info]  projD/compile:console
[info]  projD/*:test
[info] Delegates:
[info]  projD/compile:console::scalacOptions
[info]  projD/compile:scalacOptions
[info]  projD/*:console::scalacOptions
[info]  projD/*:scalacOptions
[info]  {.}/compile:console::scalacOptions
[info]  {.}/compile:scalacOptions
[info]  {.}/*:console::scalacOptions
[info]  {.}/*:scalacOptions
[info]  */compile:console::scalacOptions
[info]  */compile:scalacOptions
[info]  */*:console::scalacOptions
[info]  */*:scalacOptions
....
```

"Provided by" は `projD/compile:console::scalacOptions` が
`projD/compile:scalacOptions` によって提供されることを表示しているのに注目してほしい。
"Delegates" 以下に**全て**の委譲スコープ候補が優先順に列挙されている!

- サブプロジェクト軸が `projD` にスコープ付けされているスコープが当然最初に表示されて、`ThisBuild` (`{.}`)、`*` と続いている。
- サブプロジェクト内だと、コンフィギュレーション軸が `Compile` にスコープ付けされいるのが最初に表示されて、`*` にフォールバックしている。
- 最後に、タスク軸は与えられたタスクスコープ付けの `cosole::` が来て、次にタスクスコープ無しが来ている。

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

`projE/version` の値は何か?

1. `"2.12.2_0.1.0"`
2. `"2.11.11_0.1.0"`
3. その他

正解は `"2.12.2_0.1.0"`。
`projD/version` は `version in ThisBuild` に委譲する。
一方 `version in ThisBuild` は `scalaVersion in ThisBuild` に依存する。
このように振る舞うため、ビルドレベルのセッティングは単純な値の代入に限定するべきだ。

**練習問題 F**: 以下のビルド定義を考える:

```scala
scalacOptions in ThisBuild += "-D0"
scalacOptions += "-D1"

lazy val projF = (project in file("f"))
  .settings(
    scalacOptions in compile += "-D2",
    scalacOptions in Compile += "-D3",
    scalacOptions in (Compile, compile) += "-D4",
    test := {
      println("bippy" + (scalacOptions in (Compile, compile)).value.mkString)
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
scalacOptions in ThisBuild := {
  // scalacOptions in Global <- ルール 4
  val old = (scalacOptions in ThisBuild).value
  old :+ "-D0"
}

scalacOptions := {
  // scalacOptions in ThisBuild <- ルール 4
  val old = scalacOptions.value
  old :+ "-D1"
}

lazy val projF = (project in file("f"))
  .settings(
    scalacOptions in compile := {
      // scalacOptions in ThisBuild <- ルール 2 と 4
      val old = (scalacOptions in compile).value
      old :+ "-D2"
    },
    scalacOptions in Compile := {
      // scalacOptions in ThisBuild <- ルール 3 と 4
      val old = (scalacOptions in Compile).value
      old :+ "-D3"
    },
    scalacOptions in (Compile, compile) := {
      // scalacOptions in (projF, Compile) <- ルール 1 と 2
      val old = (scalacOptions in (Compile, compile)).value
      old :+ "-D4"
    },
    test := {
      println("bippy" + (scalacOptions in (Compile, compile)).value.mkString)
    }
  )
```

評価するとこうなる:

```scala
scalacOptions in ThisBuild := {
  Nil :+ "-D0"
}

scalacOptions := {
  List("-D0") :+ "-D1"
}

lazy val projF = (project in file("f"))
  .settings(
    scalacOptions in compile := List("-D0") :+ "-D2",
    scalacOptions in Compile := List("-D0") :+ "-D3",
    scalacOptions in (Compile, compile) := List("-D0", "-D3") :+ "-D4",
    test := {
      println("bippy" + (scalacOptions in (Compile, compile)).value.mkString)
    }
  )
```

---
out: Custom-Settings.html
---

  [Basic-Def]: Basic-Def.html
  [More-About-Settings]: More-About-Settings.html
  [Using-Plugins]: Using-Plugins.html
  [Full-Def]: Full-Def.html
  [Input-Tasks]: ../../docs/Input-Tasks.html
  [Plugins]: ../../docs/Plugins.html
  [Tasks]: ../../docs/Tasks.html
  [Keys]: ../../sxr/sbt/Keys.scala.html
  [Defaults]: ../../sxr/sbt/Defaults.scala.html
  [Scaladocs-IO]: ../api/index.html#sbt.IO\$

カスタムセッティングとタスク
------------------------

このページでは、独自のセッティングやタスクの作成を紹介する。

このページを理解するには、このガイドの前のページ、
特に [build.sbt][Basic-Def] と
[他の種類のセッティング][More-About-Settings]
を読んである必要がある。

### キーの定義

[Keys][Keys] は、キーの定義の方法で満載だ。
多くのキーは、[Defaults][Defaults] で実装されている。

キーは三つの型のうちどれかを持つ。`SettingKey` と `TaskKey` は、
[.sbt ビルド定義][Basic-Def]で説明した。`InputKey` に関しては、
[Input Tasks][Input-Tasks] を見てほしい。

以下に [Keys][Keys] からの具体例を示す:

```scala
val scalaVersion = settingKey[String]("The version of Scala used for building.")
val clean = taskKey[Unit]("Deletes files produced by the build, such as generated sources, compiled classes, and task caches.")
```

キーのコンストラクタは、二つの文字列のパラメータを取る。
キー名（`"scala-version"`）と解説文（`"The version of scala used for building."`）だ。

[.sbt ビルド定義][Basic-Def]でみたとおり、`SettingKey[T]` 内の型パラメータ `T` は、セッティングの値の型を表す。
`TaskKey[T]` 内の `T` は、タスクの結果の型を表す。
また、[.sbt ビルド定義][Basic-Def]でみたとおり、
セッティングはプロジェクトが再読み込みされるまでは固定値を持ち、
タスクは「タスク実行」のたび（sbt のインタラクティブモードかバッチモードでコマンドが打ち込まれるたび）に再計算される。

[.sbt file][Basic-Def] や [.scala file][Full-Def] や [a plugin][Using-Plugins] でキーを定義する事が出来る。
`.scala` ビルド定義ファイル内の `Build` オブジェクト内の `val`、
もしくはプラグイン内の `Plugin` オブジェクト内の `val` は全て `.sbt` ファイルに自動的にインポートされる。

### タスクの実装

キーを定義したら、次はなんらかのタスクからそのキーを使ってみよう。
独自のタスクを定義してもいいし、既存のタスクを再定義する予定なのかもしれない。
いずれにせよ、やることは同じだ。
もしタスクに他のセッティングやタスクへの依存性が無ければ、`:=` を使ってタスクのキーになんらかのコードを関連付ける:

```scala
val sampleStringTask = taskKey[String]("A sample string task.")

val sampleIntTask = taskKey[Int]("A sample int task.")

sampleStringTask := System.getProperty("user.home")

sampleIntTask := {
  val sum = 1 + 2
  println("sum: " + sum)
  sum
}
```

もしタスクに依存性があれば、[他の種類のセッティング][More-About-Settings]で説明したとおり `value` を使って値を参照する。

タスクは、ただの Scala のコードであるため、実装の一番難しい部分は、多くの場合 sbt 特定の問題ではない。
難しいのは、実行したい何らかの「中身」の部分を書くことで、
例えば HTML を整形したいとすると、
今度は HTML のライブラリを利用する必要があるかもしれない
（その場合は、[ビルド定義にライブラリ依存性を追加して][Using-Plugins]、その HTML ライブラリに基づいたコードを書く）。

sbt には、いくつかのユーティリティ・ライブラリや便利な関数があって、
特にファイルやディレクトリの取り扱いには [Scaladocs-IO][Scaladocs-IO] にある API を重宝する。

### プラグインを使おう！

`.scala` ファイルに大量のカスタムコードがあることに気づいたら、
プラグインを作って複数のプロジェクト間で再利用できないか考えてみよう。

[以前にちょっと触れた][Using-Plugins]し、
[詳しい解説はここにあるけど][Plugins]、
プラグインを作るのはすごく簡単だ。

このページは簡単な味見だけで、
カスタムタスクに関しては [Tasks][Tasks]
ページで詳細に解説されている。

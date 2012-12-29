---
title: カスタムセッティングとタスク
layout: default
---

[Keys]: http://www.scala-sbt.org/release/sxr/Keys.scala.html "Keys.scala"
[Defaults]: http://www.scala-sbt.org/release/sxr/Defaults.scala.html "Defaults.scala"
[IO]: http://www.scala-sbt.org/release/api/index.html#sbt.IO$ "IO object"

# カスタムセッティングとタスク

[前](../multi-project) _始める sbt 13/14 ページ_ [次](../summary)

このページでは、独自のセッティングやタスクの作成を紹介する。

このページを理解するには、このガイドの前のページ、
特に [build.sbt](../basic-def) と
[他の種類のセッティング](../more-about-settings)を読んである必要がある。

## キーの定義

[Keys] は、キーの定義の方法で満載だ。
多くのキーは、[Defaults] で実装されている。

キーは三つの型のうちどれかを持つ。`SettingKey` と `TaskKey` は、
[.sbt ビルド定義](../basic-def)で説明した。`InputKey` に関しては、
[Input Tasks](http://www.scala-sbt.org/release/docs/Extending/Input-Tasks.html) を見てほしい。

以下に [Keys] からの具体例を示す:

<pre>
val scalaVersion = SettingKey[String]("scala-version", "The version of Scala used for building.")
val clean = TaskKey[Unit]("clean", "Deletes files produced by the build, such as generated sources, compiled classes, and task caches.")
</pre>

キーのコンストラクタは、二つの文字列のパラメータを取る。
キー名（`"scala-version"`）と解説文（`"The version of scala used for building."`）だ。

[.sbt ビルド定義](../basic-def)でみたとおり、`SettingKey[T]` 内の型パラメータ `T` は、セッティングの値の型を表す。
`TaskKey[T]` 内の `T` は、タスクの結果の型を表す。
また、[.sbt ビルド定義](../basic-def)でみたとおり、
セッティングはプロジェクトが再読み込みされるまでは固定値を持ち、
タスクは「タスク実行」のたび（sbt のインタラクティブモードかバッチモードでコマンドが打ち込まれるたび）に再計算される。

（[.scala ビルド定義](../full-def)でみたように、）`.scala` ファイル内、
もしくは（[プラグインの使用](../using-plugins)でみたように、）プラグイン内でキーを定義することができる。
`.scala` ビルド定義ファイル内の `Build` オブジェクト内の `val`、
もしくはプラグイン内の `Plugin` オブジェクト内の `val` は全て `.sbt` ファイルに自動的にインポートされる。

## タスクの実装

キーを定義したら、次はなんらかのタスクからそのキーを使ってみよう。
独自のタスクを定義してもいいし、既存のタスクを再定義する予定なのかもしれない。
いずれにせよ、やることは同じだ。
もしタスクに他のセッティングやタスクへの依存性が無ければ、`:=` を使ってタスクのキーになんらかのコードを関連付ける:

<pre>
sampleStringTask := System.getProperty("user.home")

sampleIntTask := {
  val sum = 1 + 2
  println("sum: " + sum)
  sum
}
</pre>

もしタスクに依存性があれば、[他の種類のセッティング](../more-about-settings)で説明したとおり、代わりに `<<=` を使う。

タスクは、ただの Scala のコードであるため、実装の一番難しい部分は、多くの場合 sbt 特定の問題ではない。
難しいのは、実行したい何らかの「中身」の部分を書くことで、
例えば HTML を整形したいとすると、
今度は HTML のライブラリを利用する必要があるかもしれない
（その場合は、[ビルド定義にライブラリ依存性を追加して](../using-plugins)、その HTML ライブラリに基づいたコードを書く）。

sbt には、いくつかのユーティリティ・ライブラリや便利な関数があって、
特にファイルやディレクトリの取り扱いには [IO] にある API を重宝する。

## 置換しない場合のタスクの拡張

既存のタスクを実行して、他の別のアクションも実行したい場合は、
`~=` か `<<=` を用いて、既存のタスクをインプットとして取り（これはそのタスクを実行することを意味する）、
既存の実装が完了した後で、別に好きな事をできる。

<pre>
// 以下の二つのセッティングは等価だ。
intTask <<= intTask map { (value: Int) => value + 1 }
intTask ~= { (value: Int) => value + 1 }
</pre>

## プラグインを使おう！

`.scala` ファイルに大量のカスタムコードがあることに気づいたら、
プラグインを作って複数のプロジェクト間で再利用できないか考えてみよう。

[以前にちょっと触れた](../using-plugins)し、
[詳しい解説はここにあるけど](http://www.scala-sbt.org/release/docs/Extending/Plugins.html)、
プラグインを作るのはすごく簡単だ。

## 続いては

このページは簡単な味見だけで、
カスタムタスクに関しては [Tasks](http://www.scala-sbt.org/release/docs/Detailed-Topics/Tasks.html) ページで詳細に解説されている。

そろそろ「始める sbt」も終わりだ！[簡単なまとめ](../summary)に進む。

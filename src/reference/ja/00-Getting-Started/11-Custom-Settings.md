---
out: Custom-Settings.html
---

  [Basic-Def]: Basic-Def.html
  [Task-Graph]: Task-Graph.html
  [Using-Plugins]: Using-Plugins.html
  [Organizing-Build]: Organizing-Build.html
  [Input-Tasks]: ../../docs/Input-Tasks.html
  [Plugins]: ../../docs/Plugins.html
  [Tasks]: ../../docs/Tasks.html
  [Keys]: ../../api/sbt/Keys\$.html
  [Defaults]: https://github.com/sbt/sbt/blob/develop/main/src/main/scala/sbt/Defaults.scala
  [Scaladocs-IO]: ../../api/sbt/io/IO\$.html

カスタムセッティングとタスク
------------------------

このページでは、独自のセッティングやタスクの作成を紹介する。

このページを理解するには、このガイドの前のページ、
特に [build.sbt][Basic-Def] と
[タスク・グラフ][Task-Graph] を読んである必要がある。

### キーを定義する

[Keys][Keys] は、キーをどのように定義するかを示すサンプル例が満載だ。
多くのキーは、[Defaults][Defaults] で実装されている。

キーには 3 つの型がある。
`SettingKey` と `TaskKey` は [.sbt ビルド定義][Basic-Def]で説明した。
`InputKey` に関しては [Input Tasks][Input-Tasks] を見てほしい。

以下に [Keys][Keys] からの具体例を示す:

```scala
val scalaVersion = settingKey[String]("The version of Scala used for building.")
val clean = taskKey[Unit]("Deletes files produced by the build, such as generated sources, compiled classes, and task caches.")
```

キーのコンストラクタは、二つの文字列のパラメータを取る。
キー名（`"scala-version"`）と解説文（`"The version of scala used for building."`）だ。

[.sbt ビルド定義][Basic-Def]でみた通り、`SettingKey[T]` 内の型パラメータ `T` は、セッティングの値の型を表す。
`TaskKey[T]` 内の `T` は、タスクの結果の型を表す。

また、[.sbt ビルド定義][Basic-Def]でみた通り、セッティングはプロジェクトが再読み込みされるまでは固定値を持ち、
タスクは「タスク実行」の度（sbt のインタラクティブモードかバッチモードでコマンドが入力される度）に再計算される。

キーは [.sbt ファイル][Basic-Def]、[.scala ファイル][Organizing-Build]、または [auto plugin][Using-Plugins] 内で定義する事が出来る。
有効化された auto plugin の `autoImport` オブジェクト内で定義された `val` は全て `.sbt` ファイルに自動的にインポートされる。

### タスクを実装する

タスクで使えるキーを定義したら、次はそのキーをタスク定義の中で使ってみよう。
自前のタスクを定義しようとしているかもしれないし、既存のタスクを再定義してようと考えているかもしれないが、
いずれにせよ、やることは同じだ。`:=` を使ってタスクのキーになんらかのコードを関連付けよう:

```scala
val sampleStringTask = taskKey[String]("A sample string task.")
val sampleIntTask = taskKey[Int]("A sample int task.")

ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "$example_scala_version$"

lazy val library = (project in file("library"))
  .settings(
    sampleStringTask := System.getProperty("user.home"),
    sampleIntTask := {
      val sum = 1 + 2
      println("sum: " + sum)
      sum
    }
  )
```

もしタスクに依存してるものがあれば、[タスク・グラフ][More-About-Settings]で説明したとおり `value` を使ってその値を参照すればよい。

タスクを実装する上で一番難しい点は、多くの場合 sbt 固有の問題ではない。なぜならタスクはただの Scala コードだからだ。
難しいのはそのタスクが実行したいことの「本体」部分を書くことだ。

例えば HTML を整形したいとすると、今度は HTML のライブラリを利用したくなるかもしれない
（おそらく[ビルド定義にライブラリ依存性を追加して][Using-Plugins]、その HTML ライブラリに基づいたコードを書けばよいだろう）。

sbt には、いくつかのユーティリティライブラリや便利な関数があり、特にファイルやディレクトリの取り扱いには [Scaladocs-IO][Scaladocs-IO] にある API がしばしば重宝するだろう。

### タスクの実行意味論

カスタムタスクから `value` を使って他のタスクに依存するとき、
タスクの実行意味論 (execution semantics) に注意する必要がある。
ここでいう実行意味論とは、実際_どの時点で_タスクが評価されるかを決定するものとする。

`sampleIntTask` を例に取ると、タスク本文の各行は一行ずつ正格評価 (strict evaluation) されているはずだ。
これは逐次実行の意味論だ:

```scala
sampleIntTask := {
  val sum = 1 + 2        // first
  println("sum: " + sum) // second
  sum                    // third
}
```

実際には JVM は `sum` を `3` とインライン化したりするかもしれないが、観測可能なタスクの**作用**は、各行ずつ逐次実行したものと同一のものとなる。

次に、`startServer` と `stopServer` という 2つのカスタムタスクを定義して、`sampleIntTask` を以下のように書き換えたとする:

```scala
val startServer = taskKey[Unit]("start server")
val stopServer = taskKey[Unit]("stop server")
val sampleIntTask = taskKey[Int]("A sample int task.")
val sampleStringTask = taskKey[String]("A sample string task.")

ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "$example_scala_version$"

lazy val library = (project in file("library"))
  .settings(
    startServer := {
      println("starting...")
      Thread.sleep(500)
    },
    stopServer := {
      println("stopping...")
      Thread.sleep(500)
    },
    sampleIntTask := {
      startServer.value
      val sum = 1 + 2
      println("sum: " + sum)
      stopServer.value // THIS WON'T WORK
      sum
    },
    sampleStringTask := {
      startServer.value
      val s = sampleIntTask.value.toString
      println("s: " + s)
      s
    }
  )
```

`sampleIntTask` を sbt のインタラクティブ・プロンプトから実行すると以下の結果となる:

```
> sampleIntTask
stopping...
starting...
sum: 3
[success] Total time: 1 s, completed Dec 22, 2014 5:00:00 PM
```

何が起こったのかを考察するために、`sampleIntTask` を視覚化してみよう:

![task-dependency](../files/task-dependency00.png)

素の Scala のメソッド呼び出しと違って、タスクの `value` メソッドの呼び出しは正格評価されない。
代わりに、`sampleIntTask` が `startServer` タスクと `stopServer` タスクに依存するということを表すマークとして機能する。
`sampleIntTask` がユーザによって呼び出されると、sbt のタスクエンジンは以下を行う:

- `sampleIntTask` を評価する**前**にタスク依存性を評価する。(半順序)
- タスク依存性が独立ならば、並列に評価しようとする (並列性)
- 各タスクは一度のコマンド実行に対して 1 回のみ評価される (非重複)

#### タスク依存性の非重複化

非重複化を説明するために、sbt インタラクティブ・プロンプトから `sampleStringTask` を実行する。

```
> sampleStringTask
stopping...
starting...
sum: 3
s: 3
[success] Total time: 1 s, completed Dec 22, 2014 5:30:00 PM
```

`sampleStringTask` は `startServer` と `sampleIntTask` の両方に依存し、
`sampleIntTask` もまた `startServer` タスクに依存するため、`startServer` はタスク依存性として 2 度現れる。
しかし、`value` はタスク依存性を表記するだけなので、評価は 1 回だけ行われる。
以下は `sampleStringTask` の評価を視覚化したものだ:

![task-dependency](../files/task-dependency01.png)

もしタスク依存性を非重複化しなければ、`Test / test` のタスク依存性として `Test / compile`
が何度も現れるため、テストのソースコードを何度もコンパイルすることになる。

#### 終了処理タスク

`stopServer` タスクはどう実装するべきだろうか?
タスクは依存性を保持するものなので、終了処理タスクという考えはタスクの実行モデルにそぐわないものだ。
最後の処理そのものもタスクになるべきで、そのタスクが他の中間タスクに依存すればいい。
例えば、`stopServer` が　`sampleStringTask` に依存するべきだが、
その時点で `stopServer` は `sampleStringTask` と呼ばれるべきだろう。

```scala
lazy val library = (project in file("library"))
  .settings(
    startServer := {
      println("starting...")
      Thread.sleep(500)
    },
    sampleIntTask := {
      startServer.value
      val sum = 1 + 2
      println("sum: " + sum)
      sum
    },
    sampleStringTask := {
      startServer.value
      val s = sampleIntTask.value.toString
      println("s: " + s)
      s
    },
    sampleStringTask := {
      val old = sampleStringTask.value
      println("stopping...")
      Thread.sleep(500)
      old
    }
  )
```

これが動作することを調べるために、インタラクティブ・プロンプトから `sampleStringTask` を実行してみよう:

```
> sampleStringTask
starting...
sum: 3
s: 3
stopping...
[success] Total time: 1 s, completed Dec 22, 2014 6:00:00 PM
```

![task-dependency](../files/task-dependency02.png)

#### 素の Scala を使おう

何かが起こったその後に別の何かが起こることを保証するもう一つの方法は Scala を使うことだ。
例えば `project/ServerUtil.scala` に簡単な関数を書いたとすると、タスクは以下のように書ける:

```scala
sampleIntTask := {
  ServerUtil.startServer
  try {
    val sum = 1 + 2
    println("sum: " + sum)
  } finally {
    ServerUtil.stopServer
  }
  sum
}
```

素のメソッド呼び出しは逐次実行の意味論に従うので、全ては順序どおりに実行される。
非重複化もされなくなるので、それは気をつける必要がある。

### プラグイン化しよう

`.scala` ファイルに大量のカスタムコードがあることに気づいたら、
プラグインを作って複数のプロジェクト間で再利用できないか考えてみよう。

[以前にちょっと触れた][Using-Plugins]し、[詳しい解説はここにある][Plugins]が、
プラグインを作るのはとても簡単だ。

このページは簡単な味見だけで、カスタムタスクに関しては [Tasks][Tasks]ページで詳細に解説されている。

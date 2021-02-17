---
out: Task-Graph.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Make]: https://en.wikipedia.org/wiki/Make_(software)
  [Ant]: https://ant.apache.org/
  [Rake]: https://ruby.github.io/rake/

タスク・グラフ
------------

[ビルド定義][Basic-Def]に引き続き、このページでは `build.sbt` 定義をより詳しく解説する。

`settings` をキーと値のペア群だと考えるよりも、
より良いアナロジーは、辺を事前発生 (happens-before) 関係とするタスクの**有向非巡回グラフ** (DAG)
だと考える事だ。
これを**タスク・グラフ**と呼ぼう。

### 用語に関して

重要な用語をおさらいしておく。

- セッティング/タスク式: `.settings(...)` 内のエントリー。
- キー: セッティング式の左辺項。`SettingKey[A]`、 `TaskKey[A]`、もしくは `InputKey[A]` となる。
- セッティング: `SettingKey[A]` を持つセッティング式によって定義される。値はロード時に一度だけ計算される。
- タスク: `TaskKey[A]` を持つタスク式によって定義される。値は呼び出さるたびに計算される。

### 他のタスクへの依存性の宣言

`build.sbt` DSL では `.value` メソッドを用いて他のタスクやセッティングへの依存性を表現する。
この `value` メソッドは特殊なもので、`:=` (もしくは後に見る `+=` や `++=`) の右辺項内でしか使うことができない。

最初の例として、`update` と `clean` というタスクに依存した形で
`scalacOption` を定義したいとする。
（[Keys](../../api/sbt/Keys\$.html) より）以下の二つのキーを例に説明する。

**注意**: ここで計算される `scalacOptions` の値はナンセンスなもので、説明のためだけのものだ:

```scala
val scalacOptions = taskKey[Seq[String]]("Options for the Scala compiler.")
val update = taskKey[UpdateReport]("Resolves and optionally retrieves dependencies, producing a report.")
val clean = taskKey[Unit]("Deletes files produced by the build, such as generated sources, compiled classes, and task caches.")
```

以下のように `scalacOptions` を再配線できる:

```scala
scalacOptions := {
  val ur = update.value  // update タスクは scalacOptions よりも事前発生する
  val x = clean.value    // clean タスクは scalacOptions よりも事前発生する
  // ---- scalacOptions はここから始まる ----
  ur.allConfigurations.take(3)
}
```

`update.value` と `clean.value` はタスク依存性を宣言していて、
`ur.allConfigurations.take(3)` がタスクの本文となる。

`.value` は普通の Scala のメソッド呼び出しではない。
`build.sbt` DSL はマクロを用いてこれらをタスクの本文から持ち上げる。
** `update` と `clean` の両タスクとも、本文内のどの行に現れようと、
タスクエンジンが `scalacOption`
の開始中括弧 (`{`) を評価するときには既に完了済みである。**

具体例で説明しよう:

```scala
ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "$example_scala_version$"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    scalacOptions := {
      val out = streams.value // streams タスクは scalacOptions よりも事前発生する
      val log = out.log
      log.info("123")
      val ur = update.value   // update タスクは scalacOptions よりも事前発生する
      log.info("456")
      ur.allConfigurations.take(3)
    }
  )
```

次に、sbt シェル内で `scalacOptions` と打ち込む:

```
> scalacOptions
[info] Updating {file:/xxx/}root...
[info] Resolving jline#jline;2.14.1 ...
[info] Done updating.
[info] 123
[info] 456
[success] Total time: 0 s, completed Jan 2, 2017 10:38:24 PM
```

`val ur = ...` は `log.info("123")` と
`log.info("456")` の間に挟まっているが、
`update` タスクは両者よりも事前発生している。

もう一つの例:

```scala
ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "$example_scala_version$"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    scalacOptions := {
      val ur = update.value  // update task happens-before scalacOptions
      if (false) {
        val x = clean.value  // clean task happens-before scalacOptions
      }
      ur.allConfigurations.take(3)
    }
  )
```

sbt シェル内で `run` それから `scalacOptions` と打ち込む:

```
> run
[info] Updating {file:/xxx/}root...
[info] Resolving jline#jline;2.14.1 ...
[info] Done updating.
[info] Compiling 1 Scala source to /Users/eugene/work/quick-test/task-graph/target/scala-2.12/classes...
[info] Running example.Hello
hello
[success] Total time: 0 s, completed Jan 2, 2017 10:45:19 PM
> scalacOptions
[info] Updating {file:/xxx/}root...
[info] Resolving jline#jline;2.14.1 ...
[info] Done updating.
[success] Total time: 0 s, completed Jan 2, 2017 10:45:23 PM
```

ここで `target/scala-2.12/classes/` を探してみてほしい。
`if (false)` に囲まれていても `clean` タスクが実行されたため、そのディレクトリは存在しないはずだ。

もう一つ重要なのは、`update` と `clean` のタスクの間では順序付けの保証が無いことだ。
`update` してから `clean` が実行されるかもしれないし、
`clean` してから `update` が実行されるかもしれないし、
両者が並列に実行される可能性もある。

### .value 呼び出しのインライン化

上で解説したように、`.value` は他のタスクやセッティングへの依存性を表現するための特殊なメソッドだ。
build.sbt に慣れるまでは、`.value` の呼び出しをタスク本文の一番上にまとめておくことをお勧めする。

しかし、慣れてくると `.value` 呼び出しをインライン化して、
タスクやセッティングを簡略に書きたいと思うようになるだろう。
変数名をいちいち考えなくてもいいのも楽だ。

インライン化するとこう書ける:

```scala
scalacOptions := {
  val x = clean.value
  update.value.allConfigurations.take(3)
}
```

`.value` の呼び出しがインライン化されていようが、タスク本文内のどこに書かれていても
タスク本文に入る前に評価は完了する。

#### タスクのインスペクト

上の例では `scalacOptions` は `update` と `clean` というタスクに**依存性** (dependency) を持つ。
上のタスクを `build.sbt` に書いて、sbt シェル内から `inspect scalacOptions` と打ち込むと以下のように表示される (一部抜粋):

```
> inspect scalacOptions
[info] Task: scala.collection.Seq[java.lang.String]
[info] Description:
[info]  Options for the Scala compiler.
....
[info] Dependencies:
[info]  *:clean
[info]  *:update
....
```

これは sbt が、どのセッティングが他のセッティングに依存しているかをどう把握しているかを示している。

また、`inspect tree compile` と打ち込むと、`compile` は `incCompileSetup`
に依存していて、それは `dependencyClasspath` などの他のキーに依存していることが分かる。
依存性の連鎖をたどっていくと、魔法に出会う。

```
> inspect tree compile
[info] compile:compile = Task[sbt.inc.Analysis]
[info]   +-compile:incCompileSetup = Task[sbt.Compiler\$IncSetup]
[info]   | +-*/*:skip = Task[Boolean]
[info]   | +-compile:compileAnalysisFilename = Task[java.lang.String]
[info]   | | +-*/*:crossPaths = true
[info]   | | +-{.}/*:scalaBinaryVersion = 2.12
[info]   | |
[info]   | +-*/*:compilerCache = Task[xsbti.compile.GlobalsCache]
[info]   | +-*/*:definesClass = Task[scala.Function1[java.io.File, scala.Function1[java.lang.String, Boolean]]]
[info]   | +-compile:dependencyClasspath = Task[scala.collection.Seq[sbt.Attributed[java.io.File]]]
[info]   | | +-compile:dependencyClasspath::streams = Task[sbt.std.TaskStreams[sbt.Init\$ScopedKey[_ <: Any]]]
[info]   | | | +-*/*:streamsManager = Task[sbt.std.Streams[sbt.Init\$ScopedKey[_ <: Any]]]
[info]   | | |
[info]   | | +-compile:externalDependencyClasspath = Task[scala.collection.Seq[sbt.Attributed[java.io.File]]]
[info]   | | | +-compile:externalDependencyClasspath::streams = Task[sbt.std.TaskStreams[sbt.Init\$ScopedKey[_ <: Any]]]
[info]   | | | | +-*/*:streamsManager = Task[sbt.std.Streams[sbt.Init\$ScopedKey[_ <: Any]]]
[info]   | | | |
[info]   | | | +-compile:managedClasspath = Task[scala.collection.Seq[sbt.Attributed[java.io.File]]]
[info]   | | | | +-compile:classpathConfiguration = Task[sbt.Configuration]
[info]   | | | | | +-compile:configuration = compile
[info]   | | | | | +-*/*:internalConfigurationMap = <function1>
[info]   | | | | | +-*:update = Task[sbt.UpdateReport]
[info]   | | | | |
....
```

例えば `compile` と打ち込むと、sbt は自動的に `update` を実行する。
これが「とにかくちゃんと動く」理由は、`compile` の計算に入力として必要な値が sbt に `update` の計算を先に行うことを強制しているからだ。

このようにして、sbt の全てのビルドの依存性は、明示的には宣言されず、自動化されている。 あるキーの値を別の計算で使うと、その計算はキーに依存することになる。

#### 他のセッティングに依存したタスクの定義

`scalacOptions` はタスク・キーだ。
何らかの値に既に設定されていて、Scala 2.12 以外の場合は
`"-Xfatal-warnings"` と `"-deprecation"` を除外したいとする。

```scala
lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    organization := "com.example",
    scalaVersion := "$example_scala_version$",
    version := "0.1.0-SNAPSHOT",
    scalacOptions := List("-encoding", "utf8", "-Xfatal-warnings", "-deprecation", "-unchecked"),
    scalacOptions := {
      val old = scalacOptions.value
      scalaBinaryVersion.value match {
        case "2.12" => old
        case _      => old filterNot (Set("-Xfatal-warnings", "-deprecation").apply)
      }
    }
  )
```

sbt シェルで試すとこうなるはずだ:

```
> show scalacOptions
[info] * -encoding
[info] * utf8
[info] * -Xfatal-warnings
[info] * -deprecation
[info] * -unchecked
[success] Total time: 0 s, completed Jan 2, 2017 11:44:44 PM
> ++2.11.8!
[info] Forcing Scala version to 2.11.8 on all projects.
[info] Reapplying settings...
[info] Set current project to Hello (in build file:/xxx/)
> show scalacOptions
[info] * -encoding
[info] * utf8
[info] * -unchecked
[success] Total time: 0 s, completed Jan 2, 2017 11:44:51 PM
```

次に ([Keys](../../api/sbt/Keys\$.html) より) 以下の二つのキーを例に説明する:

```scala
val scalacOptions = taskKey[Seq[String]]("Options for the Scala compiler.")
val checksums = settingKey[Seq[String]]("The list of checksums to generate and to verify for dependencies.")
```

**注意**: `scalacOptions` と `checksums`はお互い何の関係もない、ただ同じ値の型を持つ二つのキーで片方がタスクというだけだ。

`build.sbt` の中で `scalacOptions` を `checksums`
のエイリアスにすることはできるが、その逆はできない。例えば、以下の例はコンパイルが通る:

```scala
// scalacOptions タスクは checksums セッティングの値を用いて定義される
scalacOptions := checksums.value
```


逆方向への依存、つまりタスクの値に依存したセッティングキーの値を定義することはどうしてもできない。
なぜなら、セッティングキーの値はプロジェクトのロード時に一度だけしか計算されず、毎回再実行されるべきタスクが毎回実行されなくなってしまうからだ。

```scala
// 悪い例: checksums セッティングは scalacOptions タスクに関連付けて定義することはできない!
checksums := scalacOptions.value
```

#### 他のセッティングに依存したセッティングの定義

実行のタイミングという観点から見ると、セッティングはロード時に評価される特殊なタスクと考えることができる。

プロジェクトの名前と同じ `organization` を定義してみよう。

```scala
// プロジェクトの name に基いて organization 名を付ける (どちらも型は SettingKey[String])
organization := name.value
```

実用的な例もみてみる。
これは `Compile / scalaSource` というキーを `scalaBinaryVersion` が `"2.11"`
の場合のみ別のディレクトリに再配線する。

```scala
Compile / scalaSource := {
  val old = (Compile / scalaSource).value
  scalaBinaryVersion.value match {
    case "2.11" => baseDirectory.value / "src-2.11" / "main" / "scala"
    case _      => old
  }
}
```

### そもそも build.sbt DSL は何のためにある?

`build.sbt` DSL は、セッティングやタスクの有向非巡回グラフを構築するためのドメイン特化言語だ。
セッティング式はセッティング、タスク、そしてそれらの間の依存性をエンコードする。

この構造は
[Make][Make] (1976)、 [Ant][Ant] (2000)、 [Rake][Rake] (2003)
などにも共通する。

#### Make 入門

Makefile の基本的な構文は以下のようになる:

```
target: dependencies
[tab] system command1
[tab] system command2
```

対象 (target、デフォルトの target は `all` と呼ばれる) が与えられたとき、

1. Make は対象の依存性が既にビルドされたかを調べて、ビルドされていないものをビルドする。
2. Make は順番にシステムコマンドを実行する。

`Makefile` の具体例で説明しよう:

```
CC=g++
CFLAGS=-Wall

all: hello

hello: main.o hello.o
    \$(CC) main.o hello.o -o hello

%.o: %.cpp
    \$(CC) \$(CFLAGS) -c \$< -o \$@
```

`make` を実行すると、デフォルトで　`all` という名前の対象を選択する。
その対象は `hello` を依存性として列挙するが、それは未だビルドされいないので、Make は次に `hello` をビルドする。

次に、Make は `hello` という対象の依存性がビルド済みかを調べる。
`hello` は `main.o` と `hello.o` という 2つの対象を列挙する。
これらの対象が最後のパターンマッチを用いたルールによってビルドされた後でやっと
`main.o` と `hello.o` をリンクするシステムコマンドが実行される。

`make` を実行しているだけなら、対象として何がほしいのかだけを考えればよく、
中間成果物をビルドするための正確なタイミングやコマンドなどは Make がやってくれる。
これを依存性指向プログラミングもしくはフローベースプログラミングだと考えることができる。
DSL は対象の依存性を記述するが、アクションはシステムコマンドに委譲されるため、正確には
Make はハイブリッドシステムに分類される。

#### Rake

このハイブリッド性も実は Make の後継である Ant、Rake、sbt といったツールにも受け継がれている。
Rakefile の基本的な構文をみてほしい:

```ruby
task name: [:prereq1, :prereq2] do |t|
  # actions (may reference prereq as t.name etc)
end
```

Rake でのブレークスルーは、アクションをシステムコマンドの代わりにプログラミング言語を使って記述したことだ。

#### ハイブリッド・フローベースプログラミングの利点

ビルドをこのように構成する動機がいくつかある。

第一は非重複化だ。フローベースプログラミングではあるタスクが複数のタスクから依存されていても一度だけしか実行されない。
例えば、タスクグラフ上の複数のタスクが `Compile / compile` に依存していたとしても、実際のコンパイルは唯一一回のみ実行される。

第二は並列処理だ。タスクグラフを用いることでタスクエンジンは相互に非依存なタスクを並列にスケジュールすることができる。

第三は関心事の分離と柔軟さだ。
タスクグラフはビルドの作者が複数のタスクを異なる方法で配線することを可能にする。
一方、sbt やプラグインはコンパイルやライブラリ依存性の管理といった機能を再利用な形で提供できる。

### まとめ

ビルド定義のコアなデータ構造は、辺を事前発生 (happens-before) 関係とするタスクの DAG だ。
`build.sbt` は、依存性指向プログラミングもしくはフローベースプログラミングを表現するための DSL で、`Makefile` や `Rakefile` に似ている。

フローベースプログラミングを行う動機は、非重複化、並列処理、とカスタム化の容易さだ。

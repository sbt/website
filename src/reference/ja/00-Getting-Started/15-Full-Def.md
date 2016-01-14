---
out: Full-Def.html
---

  [Basic-Def]: Basic-Def.html
  [More-About-Settings]: More-About-Settings.html
  [Using-Plugins]: Using-Plugins.html
  [Multi-Project]: Multi-Project.html

付録: .scala ビルド定義
---------------------

このページでは、旧式の `.scala` ビルド定義の説明をする。
以前のバージョンの sbt で複数のプロジェクトを扱うには `.scala` ビルド定義を使う以外しかなかったが、
sbt 0.13 からは[マルチプロジェクト .sbt ビルド定義][Basic-Def]が追加され、現在はそのスタイルが推奨されている。

このページは、このガイドのこれまでのページ、特に 
[.sbt ビルド定義][Basic-Def] と
[他の種類のセッティング][More-About-Settings]を読んでいることを前提とする。

### `build.sbt` と `Build.scala` の関係

ビルド定義の中で、`.sbt` と `.scala` を混ぜて使うには、両者の関係を理解する必要がある。

実際に 2 つのファイルを使って説明しよう。
まず、プロジェクトが `hello` というディレクトリにあるなら 
`hello/project/Build.scala` を以下のように作る:

```scala
import sbt._
import Keys._

object HelloBuild extends Build {
  val sampleKeyA = settingKey[String]("demo key A")
  val sampleKeyB = settingKey[String]("demo key B")
  val sampleKeyC = settingKey[String]("demo key C")
  val sampleKeyD = settingKey[String]("demo key D")

  override lazy val settings = super.settings ++
    Seq(
      sampleKeyA := "A: in Build.settings in Build.scala",
      resolvers := Seq()
    )

  lazy val root = Project(id = "hello",
    base = file("."),
    settings = Seq(
      sampleKeyB := "B: in the root project settings in Build.scala"
    ))
}
```

次に `hello/build.sbt` を以下のような内容で作成する:

```scala
sampleKeyC in ThisBuild := "C: in build.sbt scoped to ThisBuild"

sampleKeyD := "D: in build.sbt"
```

sbt のインタラクティブプロンプトを起動する。
`inspect sampleKeyA` と入力すると、以下のように表示されるはずだ（一部抜粋）:

```
[info] Setting: java.lang.String = A: in Build.settings in Build.scala
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}/*:sampleKeyA
```

次に `inspect sampleKeyC` と入力すると、以下のように表示される:

```
[info] Setting: java.lang.String = C: in build.sbt scoped to ThisBuild
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}/*:sampleKeyC
```

二つの値とも "Provided by" は同じスコープを表示していることに注目してほしい。
つまり、`.sbt` ファイルの `sampleKeyC in ThisBuild` は、
`.scala` ファイルの `Build.settings` リストにセッティングを追加することと等価ということだ。
sbt はビルド全体にスコープ付けされたセッティングを両者から取り込んでビルド定義を作成する。

次は、`inspect sampleKeyB`:

```
[info] Setting: java.lang.String = B: in the root project settings in Build.scala
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}hello/*:sampleKeyB
```

`sampleKeyB` は、ビルド全体（`{file:/home/hp/checkout/hello/}`）ではなく、
特定のプロジェクト（`{file:/home/hp/checkout/hello/}hello`）
にスコープ付けされいることに注意してほしい。

もうお分かりだと思うが、`inspect sampleKeyD` は `sampleKeyB` に対応する:

```
[info] Setting: java.lang.String = D: in build.sbt
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}hello/*:sampleKeyD
```

sbt は `.sbt` ファイルからのセッティングを `Build.settings` と `Project.settings` に_追加し_、
これは `.sbt` 内のセッティングの優先順位が高いことを意味する。
`Build.scala` を変更して、`build.sbt` でも設定されている `sampleKeyC` か `sampleKeyD` キーを設定してみよう。
`build.sbt` 内のセッティングが `Build.scala` 内のそれに「勝って」優先されるはずだ。

もう一つ気づいたかもしれないが、`sampleKeyC` と `sampleKeyD` は `build.sbt` でそのまま使うことができる。
これは sbt が `Build` オブジェクトのコンテンツを自動的に `.sbt` ファイルにインポートすることにより実現されている。
具体的には `build.sbt` ファイル内で `import HelloBuild._` が暗黙に呼ばれている。

まとめると:

 - `.scala` ファイル内で、`Build.settings` にセッティングを追加すると、
   自動的にビルド全体にスコープ付けされる。
 - `.scala` ファイル内で、`Project.settings` にセッティングを追加すると、
   自動的にプロジェクトにスコープ付けされる。
 - `.scala` ファイルに書いた全ての `Build` オブジェクトのコンテンツは
   `.sbt` ファイルにインポートされる。
 - `.sbt` ファイル内のセッティングは `.scala` ファイルのセッティングに_追加_される。
 - `.sbt` ファイル内のセッティングは、明示的に指定されない限り
   プロジェクトにスコープ付けされる。

### インタラクティブモードにおけるビルド定義

sbt のインタラクティブプロンプトでカレントプロジェクトを
`project/` 内のビルド定義プロジェクトに切り替えることができる。
`reload plugins` と打ち込むことで切り替わる:

```
> reload plugins
[info] Set current project to default-a0e8e4 (in build file:/home/hp/checkout/hello/project/)
> show sources
[info] ArrayBuffer(/home/hp/checkout/hello/project/Build.scala)
> reload return
[info] Loading project definition from /home/hp/checkout/hello/project
[info] Set current project to hello (in build file:/home/hp/checkout/hello/)
> show sources
[info] ArrayBuffer(/home/hp/checkout/hello/hw.scala)
>
```

上記にあるとおり、`reload return` を使ってビルド定義プロジェクトから普通のプロジェクトに戻る。

### 注意: 全てが immutable だ

`build.sbt` 内のセッティングが、`Build` や `Project` オブジェクトの `settings` フィールドに
追加されると考えるのは間違っている。
そうではなく、`Build` や `Project` のセッティングリストと `build.sbt` のセッティングが
連結されて別の immutable なリストになって、それが sbt に使われるというのが正しい。
`Build` と `Project` オブジェクトは、immutable なコンフィギュレーションであり、
ビルド定義の全体からすると、たった一部にすぎない。

事実、セッティングには他にも出どころがある。具体的には、以下の順で追加される:

 - `.scala` ファイル内の `Build.settings` と `Project.settings` 。
 - ユーザ定義のグローバルセッティング。例えば、`~/.sbt/build.sbt` に_全て_のプロジェクトに影響するセッティングを定義できる。
 - プラグインによって注入されるセッティング、次の[プラグインの使用][Using-Plugins]参照。
 - プロジェクトの `.sbt` ファイル内のセッティング。
 - （`project` 内のプロジェクトである）ビルド定義プロジェクトの場合は、グローバルプラグイン（~/.sbt/plugins）が追加される。
   [プラグインの使用][Using-Plugins]で詳細が説明される。

後続のセッティングは古いものをオーバーライドする。このリスト全体でビルド定義が構成される。

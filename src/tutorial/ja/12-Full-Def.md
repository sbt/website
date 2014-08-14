---
out: Full-Def.html
---

  [Basic-Def]: Basic-Def.html
  [More-About-Settings]: More-About-Settings.html
  [Using-Plugins]: Using-Plugins.html
  [Multi-Project]: Multi-Project.html

.scala ビルド定義
----------------

このページは、このガイドのこれまでのページ、特に 
[.sbt ビルド定義][Basic-Def] と
[他の種類のセッティング][More-About-Settings]を読んでいることを前提とする。

### sbt は再帰的だ

`build.sbt` は単純化しすぎていて、sbt の実際の動作を隠蔽している。
sbt のビルドは、Scala コードにより定義されている。そのコード自身もビルドされなければいけない。
当然これも sbt でビルドされる。

`project` ディレクトリは君のプロジェクトのビルド方法を記述した_プロジェクトの中のプロジェクトだ_。
`project` 内のプロジェクトは、他のプロジェクトができる全てのことを（理論的には）こなすことができる。
つまり、_ビルド定義は sbt プロジェクトである_ということだ_。

この入れ子構造は永遠に続く。`project/project` ディレクトリを作ることで
ビルド定義のビルド定義プロジェクトをカスタム化することができる。

以下に具体例で説明する:

```
hello/                  # プロジェクトのベースディレクトリ

    Hello.scala         # プロジェクトのソースファイル  
                        # （src/main/scala に入れることもできる）

    build.sbt           # build.sbt は、project/ 内のビルド定義プロジェクトの
                        # 一部となる

    project/            # ビルド定義プロジェクトのベースディレクトリ
	   
        Build.scala     # project/ プロジェクトのソースファイル、
                        # つまり、ビルド定義のソースファイル

        build.sbt       # これは、project/project 内のビルド定義プロジェクトの
		                    # 一部となり、ビルド定義のビルド定義となる
						   
        project/        # ビルド定義プロジェクトのためのビルド定義プロジェクトの
                        # ベースディレクトリ

            Build.scala # project/project/ プロジェクトのソースファイル
```

普通はこういうことをする必要は全く無いので、_安心してほしい！_ 
だけど、原理を理解すると役立つことがある。

ちなみに、`.scala` や `.sbt` で終わる全てのファイルが用いられ、
`build.sbt` や `Build.scala` と命名するのは慣例にすぎない。
これは複数のファイルを使っていいということも意味する。

### ビルド定義プロジェクトにおける `.scala` ソースファイル

`.sbt` ファイルは、その兄弟の `project` ディレクトリにマージされる。
プロジェクトの構造をもう一度見てみる:

```
hello/                  # プロジェクトのベースディレクトリ

    build.sbt           # build.sbt は、project/ 内のビルド定義プロジェクトの
                        # 一部となる

    project/            # ビルド定義プロジェクトのベースディレクトリ

        Build.scala     # project/ プロジェクトのソースファイル、
                        # つまり、ビルド定義のソースファイル
```

`build.sbt` 内の Scala 式は別々にコンパイルされ、
`Build.scala`（もしくは、`project/` ディレクトリ内の他の `.scala` ファイル）
に編入される。

_ベースディレクトリの `.sbt` ファイルは、
ベースディレクトリ直下の `project` 内のビルド定義プロジェクトの一部となる。_

つまり、`.sbt` ファイルは、ビルド定義プロジェクトにセッティングを追加するための、
便利な略記法ということだ。

### `build.sbt` と `Build.scala` の関係

ビルド定義の中で、`.sbt` と `.scala` を混ぜて使うには、両者の関係を理解する必要がある。

以下に、具体例で説明する。プロジェクトが `hello` にあるとすると、
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

次に、`hello/build.sbt` を以下のように書く:

```scala
sampleKeyC in ThisBuild := "C: in build.sbt scoped to ThisBuild"

sampleKeyD := "D: in build.sbt"
```

sbt のインタラクティブプロンプトを起動する。`inspect sampleKeyA` と打ち込むと、以下のように表示されるはず（一部抜粋）:

```
[info] Setting: java.lang.String = A: in Build.settings in Build.scala
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}/*:sampleKeyA
```

次に、`inspect sampleKeyC` と打ち込むと、以下のように表示される:

```
[info] Setting: java.lang.String = C: in build.sbt scoped to ThisBuild
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}/*:sampleKeyC
```

二つの値とも、"Provided by" は同じスコープを表示していることに注意してほしい。
つまり、`.sbt` ファイルの `sampleKeyC in ThisBuild` は、
`.scala` ファイルの `Build.settings` リストにセッティングを追加するのと等価とういうことだ。
sbt は、ビルド全体にスコープ付けされたセッティングを両者から取り込んでビルド定義を作成する。

次は、`inspect sampleKeyB`:

```
[info] Setting: java.lang.String = B: in the root project settings in Build.scala
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}hello/*:sampleKeyB
```

`sampleKeyB` は、
ビルド全体（`{file:/home/hp/checkout/hello/}`）ではなく、
特定のプロジェクト（`{file:/home/hp/checkout/hello/}hello`）
にスコープ付けされいることに注意してほしい。

もうお分かりだと思うが、`inspect sampleKeyD` は `sampleKeyB` に対応する:

```
[info] Setting: java.lang.String = D: in build.sbt
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}hello/*:sampleKeyD
```

sbt は `.sbt` ファイルからのセッティングを
`Build.settings` と `Project.settings` に_追加する_ため、
これは `.sbt` 内のセッティングの優先順位が高いことを意味する。
`Build.scala` を変更して、`build.sbt` でも設定されている
`sampleKeyC` か `sampleKeyD` キーを設定してみよう。
`build.sbt` 内のセッティングが、`Build.scala` 内のそれに「勝つ」はずだ。

もう一つ気づいたかもしれないが、`sampleKeyC` と `sampleKeyD` は `build.sbt` でそのまま使うことができる。
これは、sbt が `Build` オブジェクトのコンテンツを自動的に `.sbt` ファイルにインポートすることにより実現されている。
具体的には、`build.sbt` ファイル内で `import HelloBuild._` が暗黙に呼ばれている。

まとめてみると:

 - `.scala` ファイル内で、`Build.settings` にセッティングを追加すると、
   自動的にビルド全体にスコープ付けされる。
 - `.scala` ファイル内で、`Project.settings` にセッティングを追加すると、
   自動的にプロジェクトにスコープ付けされる。
 - `.scala` ファイルに書いた全ての `Build` オブジェクトのコンテンツは
   `.sbt` ファイルにインポートされる。
 - `.sbt` ファイル内のセッティングは `.scala` ファイルのセッティングに_追加_される。
 - `.sbt` ファイル内のセッティングは、明示的に指定されない限り
   プロジェクトにスコープ付けされる。

### いつ `.scala` ファイルを使うか

`.scala` ファイルでは、セッティング式の羅列に限定されない。
`val`、`object` やメソッド定義など、Scala コードを自由に書ける。

_推奨される方法の一つとしては、`.scala` ファイルは `val` や `object` やメソッド定義を
くくり出すのに使用して、セッティングの定義は `.sbt` で行うことだ。_

### インタラクティブモードにおけるビルド定義

sbt のインタラクティブプロンプトの現プロジェクトを
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

### 注意: 全て immutable だ

`build.sbt` 内のセッティングが、`Build` や `Project` オブジェクトの `settings` フィールドに
追加されると考えるのは間違っている。
そうじゃなくて、`Build` や `Project` のセッティングリストと `build.sbt` のセッティングが
連結されて別の不変リストになって、それが sbt に使われるというのが正しい。
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

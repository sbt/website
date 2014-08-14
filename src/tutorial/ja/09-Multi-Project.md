---
out: Multi-Project.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Directories]: Directories.html
  [Full-Def]: Full-Def.html

マルチプロジェクト・ビルド
----------------------

このページでは、一つのプロジェクトで複数のプロジェクトを管理する方法を紹介する。

このガイドの前のページ、特に
[build.sbt][Basic-Def] を理解していることが必要になる。

### 複数のプロジェクト

単一のビルドに複数のプロジェクトを入れておくと、
プロジェクト間に依存性がある場合や、
プロジェクトが同時に変更されることが多い場合などで便利だ。

ビルド内のサブプロジェクトは、それぞれに独自の `src/main/scala` を持ち、
`package` を実行すると独自の jar ファイルを生成するなど、
普通のプロジェクト同様に振る舞う。

### `.scala` ファイル内でのプロジェクトの定義

複数のプロジェクトを持つには、全てのプロジェクトとその関係を `.scala` ファイルで宣言する必要があり、
`.sbt` ファイルからは不可能だ。
だけど、それぞれのプロジェクトのセッティングは `.sbt` ファイルからでも定義することができる。
以下に、ルートプロジェクト `hello` が、二つのサブプロジェクト `hello-foo` と `hello-bar` を
集約（aggregate）する `.scala` ビルド定義を例に説明する:

```scala
import sbt._
import Keys._

object HelloBuild extends Build {
    lazy val root = Project(id = "hello",
                            base = file(".")) aggregate(foo, bar)

    lazy val foo = Project(id = "hello-foo",
                           base = file("foo"))

    lazy val bar = Project(id = "hello-bar",
                           base = file("bar"))
}
```

sbt は、リフレクションを用いて `Build` オブジェクト内の
`Project` 型を持ったフィールドを検索することで、`Project` オブジェクトの全リストを作成する。

プロジェクト `hello-foo` は、`base = file("foo")` と共に定義されているため、
サブディレクトリ `foo` に置かれる。
そのソースは、`foo/Foo.scala` のように `foo` の直下に置かれるか、
`foo/src/main/scala` 内に置かれる。
ビルド定義ファイルを除いては、通常の sbt [ディレクトリ構造][Directories]が `foo` 以下に適用される。

`foo` 内の全ての `.sbt` ファイル、例えば `foo/build.sbt` は、
`hello-foo` プロジェクトにスコープ付けされた上で、ビルド全体のビルド定義に取り込まれる。


ルートプロジェクトが `hello` にあるとき、`hello/build.sbt`、`hello/foo/build.sbt`、
`hello/bar/build.sbt` においてそれぞれ別々のバージョンを定義してみよう（例: `version := "0.6"`）。
次に、インタラクティブプロンプトで `show version` と打ち込んでみる。
以下のように表示されるはずだ（定義したバージョンによるが）:

```
> show version
[info] hello-foo/*:version
[info] 	0.7
[info] hello-bar/*:version
[info] 	0.9
[info] hello/*:version
[info] 	0.5
```

`hello-foo/*:version` は、`hello/foo/build.sbt` 内で定義され、
`hello-bar/*:version` は、`hello/bar/build.sbt` 内で定義され、
`hello/*:version` は、`hello/build.sbt` 内で定義される。
[スコープ付けされたキーの構文][Scopes]を復習しておこう。
それぞれの `version` キーは、`build.sbt` の場所により、
特定のプロジェクトにスコープ付けされている。
だけど、三つの `build.sbt` とも同じビルド定義の一部だ。

`.scala` ファイルは、上に示したように、単にプロジェクトとそのベースディレクトリを列挙するだけの簡単なものにして、
_それぞれのプロジェクトのセッティングは、そのプロジェクトのベースディレクトリ直下の
`.sbt` ファイル内で宣言することができる_。
_全てのセッティングを `.scala` ファイル内で宣言することは義務付けられいるわけではない。_

ビルド定義の全てを単一の `project` ディレクトリ内の場所にまとめるために、
`.scala` ファイル内にセッティングも含めてしまうほうが洗練されていると思うかもしれない。
ただし、これは好みの問題だから、好きにやっていい。

サブプロジェクトは、`project` サブディレクトリや、`project/*.scala` ファイルを持つことができない。
`foo/project/Build.scala` は無視される。

### 集約

もし望むなら、ビルド内のプロジェクトは、お互いに対して完全に独立であることができる。

だけど、上の例では、`aggregate(foo, bar)` というメソッドが呼び出されていることが分かる。
これは、`hello-foo` と `hello-bar` を、ルートプロジェクト下に集約する。

集約とは、集約プロジェクトで実行されたタスクが部分プロジェクトでも実行されることを意味する。
例のような、二つのサブプロジェクトがある状態で sbt を起動して、`compile` を実行してみよう。
三つのプロジェクト全てがコンパイルされたことが分かると思う。

_集約プロジェクト内で_（この場合は、ルートの `hello` プロジェクトで）、
タスクごとに集約をコントロールすることができる。
例えば `hello/build.sbt` 内で、`update` タスクの集約を以下のようにして回避できる:

```scala
aggregate in update := false
```

`aggregate in update` は、`update` タスクにスコープ付けされた `aggregate` キーだ
（[スコープ][Scopes]参照）。

注意: 集約は、集約されるタスクを順不同に並列実行する。

### クラスパス依存性

プロジェクトは、他のプロジェクトのコードに依存することができる。
これは、`dependsOn` メソッドを呼び出すことで実現する。
例えば、`hello-foo` が `hello-bar` のクラスパスが必要な場合は、
`Build.scala` 内に以下のように書く:

```scala
    lazy val foo = Project(id = "hello-foo",
                           base = file("foo")) dependsOn(bar)
```

これで `hello-foo` 内のコードから `hello-bar` のクラスを利用することができる。
これは、プロジェクトをコンパイルするときの順序も作り出す。
この場合、`hello-foo` がコンパイルされる前に、`hello-bar` が更新（update）され、
コンパイルされる必要がある。

複数のプロジェクトに依存するには、`dependsOn(bar, baz)` というふうに、
`dependsOn` に複数の引数を渡せばいい。

#### コンフィギュレーションごとのクラスパス依存性

`foo dependsOn(bar)` は、`foo` の `Compile` コンフィギュレーションが
`bar` の `Compile` コンフィギュレーションに依存することを意味する。
これを明示的に書くと、`dependsOn(bar % "compile->compile")` となる。

この `"compile->compile"` 内の `->` は、「依存する」という意味だから、
`"test->compile"` は、`foo` の `Test` コンフィギュレーションが
`bar` の `Compile` コンフィギュレーションに依存することを意味する。

`->config` の部分を省くと、`->compile` だと解釈されるため、
`dependsOn(bar % "test")` は、`foo` の `Test` コンフィギュレーションが
`bar` の `Compile` コンフィギュレーションに依存することを意味する。

特に、`Test` が `Test` に依存することを意味する `"test->test"` は役に立つ宣言だ。
これにより、例えば、`bar/src/test/scala` にテストのためのユーティリティコードを
置いておき、それを `foo/src/test/scala` 内のコードから利用することができる。

複数のコンフィギュレーション依存性を宣言する場合は、セミコロンで区切る。
例えば、`dependsOn(bar % "test->test;compile->compile")` と書ける。

### プロジェクトの切り替え

sbt インタラクティブプロンプトから、`projects` と打ち込むことでプロジェクトの全リストが表示され、
`project <プロジェクト名>` で、現在プロジェクトを選択できる。
`compile` のようなタスクを実行すると、それは現在プロジェクトに対して実行される。
これにより、ルートプロジェクトをコンパイルせずに、サブプロジェクトのみをコンパイルすることができる。

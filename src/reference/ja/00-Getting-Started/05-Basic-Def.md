---
out: Basic-Def.html
---

  [Keys]: ../../api/sbt/Keys\$.html
  [Task-Graph]: Task-Graph.html
  [Bare-Def]: Bare-Def.html
  [Full-Def]: Full-Def.html
  [Running]: Running.html
  [Library-Dependencies]: Library-Dependencies.html
  [Input-Tasks]: ../../docs/Input-Tasks.html

ビルド定義
---------

このページでは、多少の「理論」も含めた sbt のビルド定義 (build definition) と `build.sbt` の構文を説明する。
sbt 0.13.13 など最近のバージョンをインストール済みで、
[sbt の使い方][Running]を分かっていて、「始める sbt」の前のページも読んだことを前提とする。

このページでは `build.sbt` ビルド定義を紹介する。

### sbt バージョンの指定

ビルド定義の一部としてビルドに用いる sbt のバージョンを指定する。
これによって異なる sbt ランチャーを持つ複数の人がいても同じプロジェクトを同じようにビルドすることができる。
そのためには、`project/build.properties`
という名前のファイルを作成して以下のように
sbt バージョンを指定する:

```
sbt.version=$app_version$
```

もしも指定されたバージョンがローカルマシンに無ければ、
`sbt` ランチャーは自動的にダウンロードを行う。
このファイルが無ければ、`sbt` ランチャーは任意のバージョンを選択する。
これはビルドの移植性を下げるため、推奨されない。

### ビルド定義とは何か

**ビルド定義**は、`build.sbt`
にて定義され、プロジェクト (型は [Project](../../api/sbt/Project.html))
の集合によって構成される。
プロジェクトという用語が曖昧であることがあるため、このガイドではこれらを**サブプロジェクト**と呼ぶことが多い。

例えば、カレントディレクトリにあるサブプロジェクトは `build.sbt` に以下のように定義できる：

@@snip [build.sbt]($root$/src/sbt-test/ref/basic/build.sbt) {}

それぞれのサブプロジェクトは、キーと値のペアによって詳細が設定される。

例えば、`name` というキーがあるが、それはサブプロジェクト名という文字列の値に関連付けられる。
キーと値のペア列は `.settings(...)` メソッド内に列挙される:

@@snip [build.sbt]($root$/src/sbt-test/ref/basic/build.sbt) {}

### `build.sbt` はどのように settings を定義するか

`build.sbt` において定義されるサブプロジェクトは、キーと値のペア列を持つと言ったが、
このペアは**セッティング式** (setting expression) と呼ばれ、**build.sbt DSL** にて記述される。

```scala
ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "$example_scala_version$"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "hello"
  )
```

build.sbt DSL を詳しくみてみよう:<br>
![setting expression](../files/setting-expression.png)<br>
<br>
それぞれのエントリーは**セッティング式** (setting expression) と呼ばれる。
中にはタスク式と呼ばれるものもある。この違いはこのページの後で説明する。

セッティング式は以下の 3部から構成される:

1. 左辺項を**キー** (key) という。
2. **演算子**。この場合は `:=`。
3. 右辺項は**本文** (body)、もしくは**セッティング本文**という。

左辺値の `name`、`version`、および `scalaVersion` は**キー**である。
キーは
[`SettingKey[T]`](../../api/sbt/SettingKey.html)、
[`TaskKey[T]`](../../api/sbt/TaskKey.html)、もしくは
[`InputKey[T]`](../../api/sbt/InputKey.html) のインスタンスで、
`T` はその値の型である。キーの種類に関しては後述する。

`name` キーは `SettingKey[String]` に型付けされているため、
`name` の `:=` 演算子も `String` に型付けされている。
誤った型の値を使おうとするとビルド定義はコンパイルエラーになる:

```scala
lazy val root = (project in file("."))
  .settings(
    name := 42  // コンパイルできない
  )
```

`build.sbt` 内には `val`、`lazy val`、`def` を定義することもできる。
`build.sbt` において、トップレベルで `object` や `class` を定義することはできない。
それらが必要なら `project/` 配下にScala ソースファイル (`.scala`) を置くべきだろう。

### キー

#### 種類

キーには三種類ある:

 - `SettingKey[T]`: 一度だけ値が計算されるキー（値はサブプロジェクトの読み込み時に計算され、保存される）。
 - `TaskKey[T]`: 毎回再計算される**タスク**を呼び出す、副作用を伴う可能性のある値のキー。
 - `InputKey[T]`: コマンドラインの引数を入力として受け取るタスクのキー。
 　「始める sbt」では `InputKey` を説明しないので、このガイドを終えた後で、[Input Tasks][Input-Tasks] を読んでみよう。

#### 組み込みのキー

組み込みのキーは [Keys][Keys] と呼ばれるオブジェクトのフィールドにすぎない。
`build.sbt` は、自動的に `import sbt.Keys._` するため、`sbt.Keys.name` は `name` として参照することができる。

#### カスタムキー

カスタムキーは `settingKey`、 `taskKey`、 `inputKey` といった生成メソッドを用いて定義する。
どのメソッドでもキーに関連する型パラメータを必要とする。
キーの名前は `val` で宣言された変数の名前がそのまま用いられる。
例として、新しく `hello` と名づけたキーを定義してみよう。

```scala
lazy val hello = taskKey[Unit]("An example task")
```

実は `.sbt` ファイルには、設定を記述するのに必要な `val` や `def` を含めることもできる。
これらの定義はファイル内のどこで書かれてもプロジェクトの設定より前に評価される。

> **注意** 一般的に、初期化順問題を避けるために val の代わりに lazy val が用いられることが多い。

#### タスクキーかセッティングキーか

`TaskKey[T]` は、_タスク_を定義しているといわれる。タスクは、`compile` や `package` のような作業だ。
タスクは `Unit` を返すかもしれないし（`Unit` は、Scala での `void` だ）、
タスクに関連した値を返すかもしれない。例えば、`package` は作成した jar ファイルを値として返す `TaskKey[File]` だ。

例えばインタラクティブモードの sbt プロンプトに `compile` と入力するなど、何らかのタスクを実行する度に、
sbt はそのタスクを一回だけ再実行する。

サブプロジェクトを記述する sbt のキーと値の列は、`name` のようなセッティング (setting) であれば、
その文字列の値をキャッシュすることができるが、
`compile` のようなタスク（task）の場合は実行可能コードを保持しておく必要がある
（たとえその実行可能コードが最終的に文字列を返したとしても、それは毎回再実行されなければならない）。

_あるキーがあるとき、それは常にタスクかただのセッティングかのどちらかを参照する。_
つまり、キーの「タスク性」（毎回再実行するかどうか）はそのキーの特性であり、その値にはよらない。

### タスクとセッティングの定義

`:=` を使うことで、タスクに任意の演算を代入することができる。
セッティングを定義すると、その値はプロジェクトがロードされた時に一度だけ演算が行われる。
タスクを定義すると、その演算はタスクの実行毎に毎回再実行される。

例えば、少し前に宣言した `hello` というタスクはこのように実装できる:

```scala
lazy val hello = taskKey[Unit]("An example task")

lazy val root = (project in file("."))
  .settings(
    hello := { println("Hello!") }
  )
```

セッティングの定義は既に何度か見ていると思うが、プロジェクト名の定義はこのようにできる:

```scala
lazy val root = (project in file("."))
  .settings(
    name := "hello"
  )
```

#### タスクとセッティングの型

型システムの視点から考えると、タスクキー (task key) から作られた `Setting` は、セッティングキー (setting key) から作られたそれとは少し異なるものだ。
`taskKey := 42` は `Setting[Task[T]]` の戻り値を返すが、`settingKey := 42` は `Setting[T]` の戻り値を返す。
タスクが実行されるとタスクキーは型`T` の値を返すため、ほとんどの用途において、これによる影響は特にない。

`T` と `Task[T]` の型の違いによる影響が一つある。
それは、セッティングキーはキャッシュされていて、再実行されないため、タスキキーに依存できないということだ。
このことについては、後ほどの[タスク・グラフ][Task-Graph]にて詳しくみていく。

### sbt シェルにおけるキー

sbt のインタラクティブモードからタスクの名前を入力することで、どのタスクでも実行することができる。
それが `compile` と入力することでコンパイルタスクが起動する仕組みだ。つまり、`compile` はタスクキーだ。

タスクキーのかわりにセッティングキーの名前を入力すると、セッティングキーの値が表示される。
タスクキーの名前を入力すると、タスクを実行するが、その戻り値は表示されないため、
タスクの戻り値を表示するには素の `<タスク名>` ではなく、`show <タスク名>` と入力する。
Scala の慣例にならい、ビルド定義ファイル内ではキーはキャメルケース（`camelCase`）で命名する。

あるキーについてより詳しい情報を得るには、sbt インタラクティブモードで `inspect <キー名>` と入力する。
`inspect` が表示する情報の中にはまだよく分からない点もあるかもしれないが、一番上にはセッティングの値の型と、セッティングの簡単な説明が表示されていることだろう。

### `build.sbt` 内の import 文

`build.sbt` の一番上に import 文を書くことができ、それらは空行で分けなくてもよい。

デフォルトでは以下のものが自動的にインポートされる:

```scala
import sbt._
import Keys._
```

(さらに、auto plugin があれば `autoImport` 以下の名前がインポートされる。)

### Bare .sbt ビルド定義

セッティングは、`.settings(...)` の呼び出しの中だけではなく `build.sbt` に直書きすることができ、
これは 「bare style」と呼ばれる。

```scala
ThisBuild / version := "1.0"
ThisBuild / scalaVersion := "$example_scala_version$"
```

この構文は `ThisBuild` にスコープ付けされたセッティングを書いたり、プラグインを追加するのに向いている。
スコープやプラグインに関してはまた後ほど。

### ライブラリへの依存性を加える

サードパーティのライブラリに依存するには二つの方法がある。
第一は `lib/` に jar ファイルを入れてしまう方法で（アンマネージ依存性、unmanged dependency）、
第二はマネージ依存性（managed dependency）を加えることで、`build.sbt` ではこのようになる:

```scala
val derby = "org.apache.derby" % "derby" % "10.4.1.3"

ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "$example_scala_version$"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    libraryDependencies += derby
  )
```

これで Apache Derby ライブラリのバージョン 10.4.1.3 へのマネージ依存性を加えることができた。

`libraryDependencies` キーは二つの複雑な点がある:
`:=` ではなく `+=` を使うことと、`%` メソッドだ。
後で[タスク・グラフ][Task-Graph]で説明するが、`+=` はキーの古い値を上書きする代わりに新しい値を追加する。
`%` メソッドは文字列から Ivy モジュール ID を構築するのに使われ、これは[ライブラリ依存性][Library-Dependencies]で説明する。

ライブラリ依存性に関する詳細については、このガイドの後ろの方までとっておくことにする。
後ほど[一ページ][Library-Dependencies]を割いて丁寧に説明する。

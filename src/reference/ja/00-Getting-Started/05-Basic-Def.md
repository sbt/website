---
out: Basic-Def.html
---

  [Keys]: ../../sxr/sbt/Keys.scala.html
  [More-About-Settings]: More-About-Settings.html
  [Bare-Def]: Bare-Def.html
  [Full-Def]: Full-Def.html
  [Running]: Running.html
  [Library-Dependencies]: Library-Dependencies.html
  [Input-Tasks]: ../../docs/Input-Tasks.html

.sbt ビルド定義
--------------

このページでは、多少の「理論」も含めた sbt のビルド定義 (build definition) と `build.sbt` の構文を説明する。
君が[sbt の使い方][Running]を分かっていて、「始める sbt」の前のページも読んだことを前提とする。

### 3 種類のビルド定義

ビルド定義には以下の 3 種類がある:

1. マルチプロジェクトでの `.sbt` ビルド定義
2. bare `.sbt` ビルド定義
3. `.scala` ビルド定義

このページでは最も新しいマルチプロジェクトでの `.sbt` ビルド定義を紹介する。
これは従来の 2 つのビルド定義の長所を組み合わせたもので、全ての用途に適している。
以前に書かれたビルド定義を使って作業をするときは、古い種類のものも目にするかもしれない。
それらに関しては (このガイドの後ほどの) [bare .sbt ビルド定義][Bare-Def]と [.scala ビルド定義][Full-Def]を参照。

さらに、ビルド定義は `project/` ディレクトリ直下に置かれた `.scala` で終わるファイルを含むことができ、そこで共通の関数や値を定義することもできる。

### ビルド定義とは何か

** ここは読んで下さい **

あらかじめ決められたディレクトリを走査し、ビルド定義に関するファイル群を処理した後、最終的に sbt は `Project` の定義群を作る。

例えば、カレントディレクトリにあるプロジェクトの [Project](../../api/sbt/Project.html) 定義は `build.sbt` に以下のように記述できる：

```scala
lazy val root = (project in file("."))
```

それぞれのプロジェクトは、そのプロジェクトを記述する不変 Map （キーと値のペア群のセット）に関連付けられる。

例えば、`name` というキーがあるが、それはプロジェクト名という文字列の値に関連付けられる。

_ビルド定義ファイルは直接的には sbt の Map に影響を与えない。_

その代わり、ビルド定義は `Setting[T]` 型のオブジェクトを含む巨大なリストを作る。
`T` は Map における値の型だ。（Scala の `Setting[T]` は Java の `Setting<T>` と同様。）
`Setting` は、新しいキーと値のペアの追加、既存の値への付加のような_ Map の変換_を記述する。
（不変データ構造と関数型プログラミングの精神に則り、この変換は新しい Map を返し、古い Map は更新されない。）

カレントディレクトリに位置するプロジェクトに対してプロジェクト名のための `Setting[String]`
を関連付けるためには以下のように書く:

```scala
lazy val root = (project in file(".")).
  settings(
    name := "hello"
  )
```

この `Setting[String]` は `name` キーを追加（もしくは置換）して `"hello"` という値に設定することで Map を変換する。
変換された Map は新しい sbt の Map となる。

 Map を作るにあたり、同じキーへのすべての変更が同時に実行されるため、そして、他のキーに依存する値の処理が依存するキーの後に処理されるように、
sbt はまず `Setting` のリストをソートする。
次にソートされた `Setting` のリストを順番にみていって、一つずつ Map に適用する。

まとめ: _ビルド定義とは `Setting[T]` のリストを持った `Project` を定義するものであり、
`Setting[T]` は sbt が持つキーと値のペアの Map に作用する変換を表し、その `T` は値の型である。_

### `build.sbt` はどのように settings を定義するか

`build.sbt` が定義する `Project` は `settings` と呼ばれる Scala の式のリストを持つ。

以下に具体例を示す:

```scala
lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0",
  scalaVersion := "$example_scala_version$"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "hello"
  )
```

それぞれの `Setting` は Scala の式で定義される。
`settings` 内の式は、それぞれ独立しており、完全な Scala の文ではなく、式である。

`build.sbt` 内には `val`、`lazy val`、`def` を定義することもできる。
`build.sbt` において、トップレベルで `object` や `class` を定義することはできない。
それらが必要なら `project/` 配下に `.scala` ビルド定義として完全な Scala ソースファイルを置くべきだろう。

左辺値の `name`、`version`、および `scalaVersion` は _キー_である。
キーは `SettingKey[T]`、`TaskKey[T]`、もしくは `InputKey[T]` のインスタンスで、
`T` はその値の型である。キーの種類に関しては後述する。

キーには `:=` というメソッドがあり、このメソッドは `Setting[T]` を返す。
Java っぽい構文でこのメソッドを呼び出すこともできる:

```scala
lazy val root = (project in file(".")).
  settings(
    name.:=("hello")
  )
```

しかし、Scala なら `name := "hello"` と書くこともできる（Scala では全てのメソッドがどちらの構文でも書ける）。

`name` キーの `:=` メソッドは `Setting` を返すが、特に `Setting[String]` を返す。
`String` は `name` 自体の型にも表れていて、そちらは `SettingKey[String]` となる。
ここで返された `Setting[String]` は、sbt の Map における `name` というキーを追加または置換して `"hello"` という値に設定する変換である。

誤った型の値を使おうとするとビルド定義はコンパイルエラーになる:

```scala
lazy val root = (project in file(".")).
  settings(
    name := 42  // コンパイルできない
  )
```

### キー

#### 種類

キーには三種類ある:

 - `SettingKey[T]`: 一度だけ値が計算されるキー（値はプロジェクトの読み込み時に計算され、保存される）。
 - `TaskKey[T]`: 毎回再計算される_タスク_を呼び出す、副作用を伴う可能性のある値のキー。
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
`val` や `def` を用いた定義群は空白行によって他の設定から区切らなければいけない。

> **注意** 一般的に、初期化順問題を避けるために val の代わりに lazy val が用いられることが多い。

#### タスクキーかセッティングキーか

`TaskKey[T]` は、_タスク_を定義しているといわれる。タスクは、`compile` や `package` のような作業だ。
タスクは `Unit` を返すかもしれないし（`Unit` は、Scala での `void` だ）、
タスクに関連した値を返すかもしれない。例えば、`package` は作成した jar ファイルを値として返す `TaskKey[File]` だ。

例えばインタラクティブモードの sbt プロンプトに `compile` と入力するなど、何らかのタスクを実行する度に、
sbt はそのタスクを一回だけ再実行する。

プロジェクトを記述する sbt の Map は、`name` のようなセッティング (setting) であれば、
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

lazy val root = (project in file(".")).
  settings(
    hello := { println("Hello!") }
  )
```

セッティングの定義は既に何度か見ていると思うが、プロジェクト名の定義はこのようにできる:

```scala
lazy val root = (project in file(".")).
  settings(
    name := "hello"
  )
```

#### タスクとセッティングの型

型システムの視点から考えると、タスクキー (task key) から作られた `Setting` は、セッティングキー (setting key) から作られたそれとは少し異なるものだ。
`taskKey := 42` は `Setting[Task[T]]` の戻り値を返すが、`settingKey := 42` は `Setting[T]` の戻り値を返す。
タスクが実行されるとタスクキーは型`T` の値を返すため、ほとんどの用途において、これによる影響は特にない。

`T` と `Task[T]` の型の違いによる影響が一つある。
それは、セッティングキーはキャッシュされていて、再実行されないため、タスキキーに依存できないということだ。
このことについては、後ほどの[他の種類のセッティング][More-About-Settings]にて詳しくみていく。

### sbt インタラクティブモードにおけるキー

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
import Process._
import Keys._
```

（さらに、[.scala ファイル][Full-Def]がある場合は、それらの全ての `Build` と `Plugin` の内容もインポートされる。
これに関しては、[.scala ビルド定義][Full-Def]でさらに詳しく。）

### ライブラリへの依存性を加える

サードパーティのライブラリに依存するには二つの方法がある。
第一は `lib/` に jar ファイルを入れてしまう方法で（アンマネージ依存性、unmanged dependency）、
第二はマネージ依存性（managed dependency）を加えることで、`build.sbt` ではこのようになる:

```scala
val derby = "org.apache.derby" % "derby" % "10.4.1.3"

lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0",
  scalaVersion := "2.11.4"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "hello",
    libraryDependencies += derby
  )
```

これで Apache Derby ライブラリのバージョン 10.4.1.3 へのマネージ依存性を加えることができた。

`libraryDependencies` キーは二つの複雑な点がある:
`:=` ではなく `+=` を使うことと、`%` メソッドだ。
後で[他の種類のセッティング][More-About-Settings]で説明するが、`+=` はキーの古い値を上書きする代わりに新しい値を追加する。
`%` メソッドは文字列から Ivy モジュール ID を構築するのに使われ、これは[ライブラリ依存性][Library-Dependencies]で説明する。

ライブラリ依存性に関する詳細については、このガイドの後ろの方までとっておくことにする。
後ほど[一ページ][Library-Dependencies]を割いて丁寧に説明する。

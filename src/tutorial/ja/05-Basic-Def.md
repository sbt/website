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
君が、[sbt の使い方][Running]を分かっていて、「始める sbt」の前のページも読んだことを前提とする。

### 3種類のビルド定義

ビルド定義には以下の 3種類がある:

1. マルチ・プロジェクト `.sbt` ビルド定義
2. bare `.sbt` ビルド定義
3. `.scala` ビルド定義

このページでは最も新しいマルチ・プロジェクト `.sbt` ビルド定義を紹介する。これは、従来あった 2つのビルド定義の長所を組み合わせたもので、全ての状況において使うことができる。
以前に書かれたビルド定義を使って作業をするときは、古い種類のものも目にするかもしれない。
それらに関しては (このガイドの後ほどの) [bare .sbt ビルド定義][Bare-Def]と [.scala ビルド定義][Full-Def]を参照。

さらに、ビルド定義は `project/` ディレクトリ直下に置かれた `.scala` で終わるファイルを含むことができ、そこで共通の関数や値を定義することもできる。

### ビルド定義って何？

** ここは読んで下さい **

あらかじめ決められたディレクトリを走査して、ビルド定義に関わるファイル群を処理した後、sbt は `Project` の定義群を最終的に作る。

例えば、現在のディレクトリに位置するプロジェクトの [Project](../../api/sbt/Project.html) 定義を以下のようにして `build.sbt` に書くことができる:

```scala
lazy val root = (project in file("."))
```

それぞれのプロジェクトは、そのプロジェクトを記述した不可変マップ（キーと値のペア）に関連付けられる。

例えば、`name` というキーがあり、それは文字列の値、つまり君のプロジェクト名に関連付けられる。


_ビルド定義ファイルは直接には sbt のマップに影響を与えない。_

その代わり、ビルド定義は、型が `Setting[T]` のオブジェクトを含んだ巨大なリストを作る。
`T` はマップ内の値の型だ。（Scala の `Setting[T]` は Java の `Setting<T>` と同様。）
`Setting` は、新しいキーと値のペアや、既存の値への追加など、_マップの変換_を記述する。
（関数型プログラミングの精神に則り、変換は新しいマップを返し、古いマップは更新されない。）

現在のディレクトリに位置するプロジェクトに対してプロジェクト名のための `Setting[String]`
を関連付けるためには以下のように書く:

```scala
lazy val root = (project in file(".")).
  settings(
    name := "hello"
  )
```

この `Setting[String]` は `name` キーを追加（もしくは置換）して `"hello"` という値に設定することでマップを変換する。
変換されたマップは新しい sbt のマップとなる。

マップを作るために、sbt はまず、同じキーへの変更が一緒に起き、かつ他のキーに依存する値の処理が依存するキーの後にくるように `Setting` のリストをソートする。
次に、sbt はソートされた `Setting` のリストを順番にみていって、一つづつマップに適用する。

まとめ: _ビルド定義は `Setting[T]` のリストを持った `Project` を定義して、`Setting[T]` は sbt のキー・値ペアへの変換を表し、`T` は値の型を指す。_

### `build.sbt` はどう設定値を定義するか

`build.sbt` が定義する `Project` は `settings` と呼ばれる Scala の式のリストを持つ。


以下に具体例で説明しよう:

```scala
lazy val root = (project in file(".")).
  settings(
    name := "hello",
    version := "1.0",
    scalaVersion := "$example_scala_version$"
  )
```

それぞれの `Setting` は Scala の式で表される。
`settings` 内の式は、それぞれ独立しており、完全な Scala 文ではなく、式だ。

`build.sbt` 内には、他に `val`、`lazy val`、`def` を定義することができる。
トップレベルでの `object` とクラスの定義は `build.sbt` 内では禁止されている。
それらは `project/` ディレクトリ直下の `.scala` ファイルに置かれる。

左辺値の `name`、`version`、および `scalaVersion` は _キー_だ。
キーは `SettingKey[T]`、`TaskKey[T]`、もしくは `InputKey[T]` のインスタンスで、
`T` はその値の型だ。キーの種類に関しては後で説明しよう。

キーには `:=` メソッドがあり、`Setting[T]` を返す。
Java 的な構文でこのメソッドを呼び出すこともできる:

```scala
lazy val root = (project in file(".")).
  settings(
    name.:=("hello")
  )
```

だけど、Scala では `name := "hello"` と書ける（Scala では全てのメソッドがどちらの構文でも書ける）。

`name` キーの `:=` メソッドは `Setting` を返すが、特に `Setting[String]` を返す。
`String` は、`name` の型にもあらわれ、これは、`SettingKey[String]` となっている。
この場合、返された `Setting[String]` は、キーを追加（もしくは置換）して `"hello"` という値に設定するマップの変換だ。

間違った型の値を使うと、ビルド定義はコンパイルできない:

```scala
lazy val root = (project in file(".")).
  settings(
    name := 42  // コンパイルできない
  )
```

### Keys

#### 種類

キーには三種類ある:

 - `SettingKey[T]`: 値が一度だけ計算されるキー（値はプロジェクトの読み込み時に計算され、保存される）。
 - `TaskKey[T]`: 毎回再計算される_タスク_を呼び出す、副作用を伴う可能性のある値のキー。
 - `InputKey[T]`: コマンドラインの引数を受け取るタスクキー。
 　「初めての sbt」では `InputKey` を説明しないので、このガイドを終えた後で、
   [Input Tasks][Input-Tasks] を読んでみよう。


#### 組み込みキー

組み込みのキーは [Keys][Keys] と呼ばれるオブジェクトのフィールドにすぎない。
`build.sbt` は、自動的に `import sbt.Keys._` するため、
`sbt.Keys.name` は `name` として呼ぶことができる。

#### カスタムキー

カスタムキーは `settingKey`、 `taskKey`、 `inputKey` といった関数を用いて定義する。どの関数でもキーに関連する型パラメータを必要とする。キーの名前は `val` で宣言された変数の名前がそのまま用いられる。例えば、新しく `hello` と名づけたキーを定義してみよう。

```scala
lazy val hello = taskKey[Unit]("An example task")
```

実は `.sbt` ファイルには、設定を記述するのに必要な `val` や `def` を含めることもできる。
これらの定義はファイル内のどこで書かれてもプロジェクトの設定より前に評価される。
`val` や `def` を用いた定義群は空白行によって他の設定から区切らなければいけない。

> **注意** 初期化順問題を避けるために val の代わりに lazy val を用いている。

#### タスクキーかセッティングキーか

`TaskKey[T]` は、_タスク_を定義しているといわれる。タスクは、`compile` や `package` のような作業だ。
タスクは `Unit` を返すかもしれないし（`Unit` は、Scala での `void` だ）、
タスクに関連した値を返すかもしれない。例えば、`package` は作成した jar ファイルを値として返す `TaskKey[File]` だ。

例えばインタラクティブモードの sbt プロンプトに `compile` と打ち込むなどして、
タスクを実行するたびに、sbt は関連したタスクを一回だけ再実行する。

プロジェクトを記述した sbt のマップは、`name` のようなセッティング (setting) ならば、その文字列の値をキャッシュすることができるけど、
`compile` のようなタスク（task）の場合は実行可能コードを保存する必要がある
（たとえその実行可能コードが最終的に同じ文字列を返したとしても、それは毎回再実行されなければいけない）。

_あるキーがあるとき、それは常にタスクか素のセッティングかのどちらかを参照する。_
つまり、キーの「タスク性」（毎回再実行するかどうか）はキーの特性であり、値にはよらない。

### タスクとセッティングの定義

`:=` を使うことで、タスクに任意の演算を代入することができる。セッティングを定義すると、その値はプロジェクトがロードされた時に一度だけ演算が行われる。タスクを定義すると、その演算はタスクの実行毎に毎回再実行される。

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

sbt のインタラクティブモードからタスクの名前を打ち込むことで、どのタスクでも実行することができる。
それが `compile` と打ち込むことでコンパイルタスクが起動する仕組みだ。つまり、`compile` はタスクキーなのだ。

タスクキーのかわりにセッティングキーの名前を入力すると、セッティングキーの値が表示される。
タスクキーの名前を入力すると、タスクを実行するが、その戻り値は表示されないため、
タスクの戻り値を表示するには素の `<タスク名>` ではなく、`show <タスク名>` と入力する。Scala の慣例にのっとり、ビルド定義ファイル内ではキーはキャメルケース（`camelCase`）で命名する。

あるキーについてより詳しい情報を得るためには、sbt インタラクティブモードで `inspect <キー名>` と打ち込む。
`inspect` が表示する情報の中にはまだ分からないこともあると思うけど、一番上にはセッティングの値の型と、セッテイングの簡単な説明がある。

### `build.sbt` 内の import 文

`build.sbt` の一番上に import 文を書くことができ、それらは空行でわけなくてもいい。

自動的に以下のものがデフォルトでインポートされる:

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
lazy val root = (project in file(".")).
  settings(
    name := "hello",
    libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3"
  )
```

これで Apache Derby ライブラリのバージョン 10.4.1.3 へのマネージ依存性を加えることができた。

`libraryDependencies` キーは二つの複雑な点がある:
`:=` ではなく `+=` を使うことと、`%` メソッドだ。
後で[他の種類のセッティング][More-About-Settings]で説明するけど、`+=` はキーの古い値を上書きするかわりに新しい値を追加する。
`%` メソッドは文字列から Ivy モジュール ID を構築するのに使われ、これは[ライブラリ依存性][Library-Dependencies]で説明する。

ライブラリ依存性に関しては、このガイドの後ほどまで少しおいておくことにする。
後で、[一ページ分][Library-Dependencies]をさいてちゃんと説明する。

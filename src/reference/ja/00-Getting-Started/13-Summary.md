---
out: Summary.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Using-Plugins]: Using-Plugins.html
  [getting-help]: ../../docs/Faq.html#getting-help
  [Organizing-Build]: Organizing-Build.html

まとめ
-----

このページではこのガイドを総括する。

sbt を使うのに、理解すべき概念の数はさほど多くない。
確かに、これらには多少の学習曲線があるが、
sbt にはこれらの概念_以外_のことは特にないとも考えることもできる。
sbt は、強力なコア・コンセプトだけを用いて全てを実現している。

この「始める sbt」シリーズをここまで読破したのであれば、知るべきことが何かはもう分かっているはずだ。

### sbt: コア・コンセプト

 - Scala の基本。Scala の構文に慣れていると役立つのは言うまでもない。
   Scala の設計者自身による [Scalaスケーラブルプログラミング](https://book.impress.co.jp/books/1119101190)
   （[原著](https://www.artima.com/shop/programming_in_scala_4ed)）は、素晴らしい入門書だ。
 - [.sbt ビルド定義][Basic-Def]  
   - ビルド定義はタスクとタスク間のそ依存性の大きな DAG だ。
   - `Setting` を作成するために `:=`、`+=`、`++=` のようなキーに定義されたメソッドを呼び出す。
   - 各セッティングは、キーにより決定された固有の型の値を持つ。
   - _タスク_は、特殊なセッティングで、タスクを実行するたびに、キーの値を生成する計算が再実行される。
	 非タスクのセッティングは、ビルド定義の読み込み時に一度だけ値が計算される。
 - [スコープ][Scopes]
   - それぞれのキーは、異なるスコープごとにそれぞれ別の値を持つことができる。
   - スコープ付けは、コンフィギュレーション、プロジェクト、タスクの三つの軸を用いることができる。
   - スコープ付けによって、プロジェクト毎、タスク毎、コンフィギュレーション毎に、異なるふるまいを持たせることができる。
   - コンフィギュレーションは、メインのもの（`Compile`）や、テスト用のもの（`Test`）のようなビルドの種類だ。
   - プロジェクト軸には（個々のサブプロジェクトだけでなく）「ビルド全体」を指すスコープもある。
   - スコープはより一般的なスコープにフォールバックする、これを_委譲_（delegate）という。
   - `build.sbt` にほとんどの設定を置くが、class 定義や大きめのタスク実装などは `.scala` ビルド定義を使う。
   - ビルド定義はそれ自体も project ディレクトリをルートとする sbt プロジェクトである。
 - [プラグイン][Using-Plugins]はビルド定義の拡張だ。
   - プラグインは、`addSbtPlugin` メソッドを用いて `project/plugins.sbt` に追加する。
     （プロジェクトのベースディレクトリにある `build.sbt` ではないことに注意）

上記のうち、一つでも分からないことがあれば、[質問してみるか][getting-help]、このガイドをもう一度読み返すか、sbt のインタラクティブモードで実験してみよう。

健闘を祈る！

### 上級者への注意

<!-- TODO: Link to reference. The rest of this wiki consists of deeper dives and less-commonly-needed
information. -->

sbt はオープンソースであるため、いつでも[ソース](https://github.com/sbt/sbt)を見れることも忘れずに！

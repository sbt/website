---
title: プラグインの使用
layout: default
---

# プラグインの使用

[前](../full-def) _始める sbt 11/14 ページ_ [次](../multi-project)

このガイドの前のページをまず読んでほしい。
特に [build.sbt](../basic-def)、
[ライブラリ依存性](../library-dependencies)、
[.scala ビルド定義](../full-def)を理解していることが必要になる。

## プラグインって何?

プラグインは、新たなセッティングを追加するなどして、ビルド定義を拡張する。
そのセッティングは、新しいタスクを加えることもでき、
例えば、テストカバレッジレポートを生成する `code-coverage` というタスクをプラグインが提供することができる。

## プラグインの追加

### 短い答

プロジェクトが `hello` ディレクトリにあるなら、`hello/project/build.sbt` を編集して、
プラグインの場所を resolver として追加して、次にプラグインの Ivy モジュール ID と共に `addSbtPlugin`
を呼び出す:

<pre>
resolvers += Classpaths.typesafeResolver

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse" % "1.4.0")
</pre>

プラグインがデフォルトのリポジトリのどれかにあるならば、当然 resolver を追加する必要は無い。

と、まあこんな感じなんだけど、中で何が起こってるか理解するためには、続きを読んでほしい。

### 仕組み

以前に説明した
[sbt プロジェクトの再帰的な性質](../full-def)と、
[マネージ依存性](../library-dependencies)を、まず理解してほしい。

#### ビルド定義の依存性

プラグインを追加することは、_ビルド定義にライブラリ依存性を追加する_ことを意味する。
そのためには、ビルド定義のビルド定義を編集すればいい。

`hello` プロジェクトがあるとき、そのビルド定義プロジェクトは
`hello/*.sbt` と `hello/project/*.scala` から構成されることを思い出してほしい:

<pre>

   hello/                  # プロジェクトのベースディレクトリ

       build.sbt           # build.sbt は、project/ 内のビルド定義プロジェクトの
                           # 一部となる

       project/            # ビルド定義プロジェクトのベースディレクトリ

           Build.scala     # project/ プロジェクトのソースファイル、
                           # つまり、ビルド定義のソースファイル
</pre>

`hello` プロジェクトにマネージ依存性を追加する場合は、
`libraryDependencies` セッティングを、`hello/*.sbt` か、`hellp/project/*.scala` かの
どちらかで追加する。

具体例で、`hello/build.sbt` に追加してみよう:

<pre>
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % "test"
</pre>

これを加えた後で sbt を起動して、インタラクティブモードから `show dependency-classpath`
と打ち込むと、derby の jar ファイルがクラスパスに含まれているのが分かると思う。

プラグインを追加するには、再帰の一段深い段階で同じ事をすればいい。
_ビルド定義プロジェクト_に新たな依存ライブラリを追加したい。
これは、ビルド定義のビルド定義の `libraryDependencies` セッティングを変更することを意味する。

`hello` プロジェクトがあるとき、ビルド定義のビルド定義は、
`hello/project/*.sbt` と `hello/project/project/*.scala` にある。

最も単純な「プラグイン」は、sbt のための特殊なサポートを持たない、ただの jar ファイルだ。
具体例で説明すると、`hello/project/build.sbt` を開いて、以下を追加してみよう:

<pre>
libraryDependencies += "net.liftweb" % "lift-json" % "2.0"
</pre>

次に、sbt のインタラクティブプロンプトから `reload plugins` と入力して、
ビルド定義プロジェクトに入って、`show dependency-classpath` を試してみよう。
lift-json の jar にクラスパスが通っていることが分かるはずだ。
これは、`Build.scala` や `build.sbt` 内でタスクを実装するのに
lift-json のクラスが利用できることを意味する。
例えば、JSON ファイルをパーズしてそれに基づいて他のファイルを生成するといったことができる。
`reload return` を使ってビルド定義プロジェクトから親プロジェクトに戻れるのを
覚えておこう。

（つまらない sbt の遊びを一つ: `reload plugins` を繰り返し打ち込んでみよう。
気づいたときには、`project/project/project/project/project/project/` 内のプロジェクトに
迷いこむだろう。特に役に立たないから、あんまり気にしなくてもいい。
これは、`target` ディレクトリをずっと下まで作りだすから、その掃除も後でしなくちゃいけない。）

#### `addSbtPlugin`

`addSbtPlugin` は、ただの便利メソッドだ。定義を見てみよう:

<pre>
def addSbtPlugin(dependency: ModuleID): Setting[Seq[ModuleID]] =
                libraryDependencies <+= (sbtVersion in update,scalaVersion) { (sbtV, scalaV) => sbtPluginExtra(dependency, sbtV, scalaV) }
</pre>

[他の種類のセッティング](../more-about-settings)で説明したように、
`<+=` は、`<<=` と `+=` を組み合わせるということは覚えているかな。
つまり、これは他のセッティングの基づいて値を作って、それを `libraryDependencies` に追加する。
具体的には、値は、`sbtVersion in update`（`update` タスクにスコープ付けされた
sbt のバージョン）
と`scalaVersion`（プロジェクトのコンパイルに用いられる Scala のバージョン、
つまり、ビルド定義のコンパイルに使われるもの）の二つに基づいている。
`sbtPluginExtra` は、モジュールID に、sbt と Scala のバージョン情報を追加する。

#### `plugins.sbt`

（プロジェクトが `hello` にあるとき）`hello/build.sbt` との混乱を避けるため、
プラグインへの依存性は、`hello/project/plugins.sbt` に列挙する人もいる。
sbt は `.sbt` ファイルが何と呼ばれようと気にしないため、
`build.sbt` も `project/plugins.sbt` も慣習にすぎない。
sbt が気にするのは、`.sbt` ファイルが_どこに置かれているのか_ということだ。
`hello/*.sbt` は、`hello` の依存性を含み、
`hello/project/*.sbt` は、`hello` のビルド定義の依存性を含む。

## プラグインはセッティングやインポートを自動追加できる

ある意味では、プラグインは、ビルド定義の `libraryDependencies` に追加される jar ファイルにすぎない。
それにより、上での lift-json を使った例のようにビルド定義から jar を利用することができる。

だけど、sbt プラグインとして意図された jar ファイルは、もう一歩進んだことができる。

適当なプラグインの jar ファイルをダンロードしてきて
（[例えば、sbteclipse とか](http://repo.typesafe.com/typesafe/ivy-releases/com.typesafe.sbteclipse/sbteclipse/scala_2.9.1/sbt_0.11.0/1.4.0/jars/sbteclipse.jar)）、
`jar xf` で解凍すると、
`sbt/sbt.plugins` というテキストファイルが含まれていることが分かる。
`sbt/sbt.plugins` の各行には以下のようにオブジェクト名が書かれている:


<pre>
com.typesafe.sbteclipse.SbtEclipsePlugin
</pre>

`com.typesafe.sbteclipse.SbtEclipsePlugin` は、
`sbt.Plugin` を拡張するオブジェクトの名前だ。
`sbt.Plugin` trait はとても単純なものだ:

<pre>
trait Plugin
{
        def settings: Seq[Setting[_]] = Nil
}
</pre>

sbt は、`sbt/sbt.plugins` に列挙されたオブジェクトを探す。
`com.typesafe.sbteclipse.SbtEclipsePlugin` を見つけると、
プロジェクトのセッティングに `com.typesafe.sbteclipse.SbtEclipsePlugin.settings` を加える。
また、ビルド定義の `.sbt` ファイルの評価時に
`import com.typesafe.sbteclipse.SbtEclipsePlugin._` を実行して、
プラグインが `.sbt` ファイルに対して値、オブジェクト、そしてメソッドを提供することを可能とする。

## プラグインからのセッティングの手動追加

プラグインが、`Plugin` オブジェクトの `settings` フィールドを用いて
セッティングを定義する場合は、
何をしなくても自動的に追加される。

だけど、[マルチプロジェクトビルド](../multi-project)内のどのプロジェクトがプラグインを
使うかをコントロールできなくなってしまうため、通常プラグインはこの方法を避けることが多い。

sbt は、複数のセッティングをまとめて追加できる `seq` メソッドを提供する。
あるプラグインが以下のような定義だとする:

<pre>
object MyPlugin extends Plugin {
   val myPluginSettings = Seq(settings in here)
}
</pre>

このとき、以下のようにして `build.sbt` に全てのセッティングをまとめて追加できる:

<pre>
seq(myPluginSettings: _*)
</pre>

`_*` 構文を見慣れてないという人のための解説:

 - `seq` は、可変長引数を受け取る: `def seq(settings: Setting[_]*)`
 - `_*` は列を可変長引数へと変換する

つまり、`build.sbt` に `seq(myPluginSettings: _*)` と書けば、
`myPluginSettings` 内の全てのセッティングをプロジェクトに追加できる。

## プラグインの作成

ここまで読めば、sbt プラグインの_作成_も知っているも同然だ。
一つだけ覚えておく事があって、それは `build.sbt` 内で
`sbtPlugin := true` と設定することだ。
プロジェクトの `sbtPlugin` が `true` の場合は、jar ファイルをパッケージ化するときに
コンパイルされたクラスを検査して、
検出された `Plugin` のインスタンスを `sbt/sbt.plugins` に書き出す。
`sbtPlugin := true` は、また、sbt をプロジェクトのクラスパスに通すため、
プラグインの実装に sbt の API を利用することができる。

プラグインの作成に関する詳細は、[[Plugins]] と [[Plugins Best Practices]] を参照。

## グローバル・プラグイン

`~/.sbt/plugins` で設定することで、全てのプロジェクトに対して一括してプラグインをインストールすることができる。
`~/.sbt/plugins` は、sbt プロジェクトで、そのクラスパスは全ての sbt ビルド定義にエクスポートされる。
大まかに言うと、`~/.sbt/plugins` 内の `.sbt` ファイルは、それが全てのプロジェクトの
`project/` ディレクトリに入っているかのように振る舞い、
`~/.sbt/plugins/project/` 内の `.scala` ファイルは、それが全てのプロジェクトの
`project/project/` ディレクトリに入っているかのように振る舞う。

`~/.sbt/plugins/build.sbt` を作って、そこに `addSbtPlugin()` 式を書くことで
全プロジェクトにプラグインを追加することができる。

## 利用可能なプラグイン

[プラグインのリスト](http://www.scala-sbt.org/release/docs/Community/Community-Plugins.html)がある。

特に人気のプラグインは:

 - IDE 専用プラグイン（sbt プロジェクトを IDE にインポートする）
 - [xsbt-web-plugin](https://github.com/JamesEarlDouglas/xsbt-web-plugin) などの、web フレームワークプラグイン

[リストを見てほしい](http://www.scala-sbt.org/release/docs/Community/Community-Plugins.html)。

## 続いては

[マルチプロジェクト・ビルド](../multi-project)に続く。

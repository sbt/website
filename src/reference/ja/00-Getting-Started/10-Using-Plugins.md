---
out: Using-Plugins.html
---

  [Basic-Def]: Basic-Def.html
  [Library-Dependencies]: Library-Dependencies.html
  [Multi-Project]: Multi-Project.html
  [global-vs-local-plugins]: ../../docs/Best-Practices.html#global-vs-local-plugins
  [Community-Plugins]: ../../docs/Community-Plugins.html
  [Plugins]: ../../docs/Plugins.html
  [Plugins-Best-Practices]: ../../docs/Plugins-Best-Practices.html
  [Task-Graph]: Task-Graph.html

プラグインの使用
--------------

このガイドのこれまでのページを読んでおいてほしい。
特に [build.sbt][Basic-Def]、
[タスク・グラフ][Task-Graph]、
と[ライブラリ依存性][Library-Dependencies]を理解していることが必要になる。

### プラグインとは何か

sbt のプラグインは、最も一般的には新しいセッティングを追加することでビルド定義を拡張するものである。
その新しいセッティングは新しいタスクでもよい。
例えば、テストカバレッジレポートを生成する `codeCoverage` というタスクを追加するプラグインなどが考えられる。

### プラグインの宣言

プロジェクトが `hello` ディレクトリにあり、ビルド定義に sbt-site プラグインを追加する場合、
`hello/project/site.sbt` を新しく作成し、
Ivy のモジュール ID を `addSbtPlugin` メソッドに渡してプラグイン依存性を定義する:

```scala
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.7.0")
```

sbt-assembly プラグインを追加するなら、以下のような内容で `hello/project/assembly.sbt` をつくる:

```scala
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.2")
```

全てのプラグインがデフォルトのリポジトリに存在するわけではないので、
プラグインのドキュメントでそのプラグインが見つかるリポジトリを resolvers に追加するよう指示されていることもあるだろう。

```scala
resolvers += Resolver.sonatypeRepo("public")
```

プラグインは普通、プロジェクトでそのプラグインの機能を有効にするためのセッティング群を提供している。
これは次のセクションで説明する。

### auto plugin の有効化と無効化

プラグインは、自身が持つセッティング群がビルド定義に自動的に追加されるよう宣言することができ、
その場合、プラグインの利用者は何もしなくてもいい。

sbt 0.13.5 から、プラグインを自動的に追加して、そのセッティング群と依存関係がプロジェクトに設定されていることを安全に保証する [auto plugin][Plugins] という機能が追加された。

auto plugin の多くはデフォルトのセッティング群を自動的に追加するが、中には明示的な有効化を必要とするものもある。

明示的な有効化が必要な auto plugin を使っている場合は、以下を `build.sbt` に追加する必要がある:

```scala
lazy val util = (project in file("util"))
  .enablePlugins(FooPlugin, BarPlugin)
  .settings(
    name := "hello-util"
  )
```
`enablePlugins` メソッドを使えば、そのプロジェクトで使用したい auto plugin を明示的に定義できる。
逆に `disablePlugins` メソッドを使ってプラグインを除外することもできる。
例えば、`util` から `IvyPlugin` のセッティングを除外したいとすると、`build.sbt` を以下のように変更する:

```scala
lazy val util = (project in file("util"))
  .enablePlugins(FooPlugin, BarPlugin)
  .disablePlugins(plugins.IvyPlugin)
  .settings(
    name := "hello-util"
  )
```

明示的な有効化が必要か否かは、それぞれの auto plugin がドキュメントで明記しておくべきだ。
あるプロジェクトでどんな auto plugin が有効化されているか気になったら、
sbt コンソールから `plugins` コマンドを実行してみよう。

例えば、このようになる。

```
> plugins
In file:/home/jsuereth/projects/sbt/test-ivy-issues/
        sbt.plugins.IvyPlugin: enabled in scala-sbt-org
        sbt.plugins.JvmPlugin: enabled in scala-sbt-org
        sbt.plugins.CorePlugin: enabled in scala-sbt-org
        sbt.plugins.JUnitXmlReportPlugin: enabled in scala-sbt-org
```

ここでは、`plugins` の表示によって sbt のデフォルトのプラグインが全て有効化されていることが分かる。
sbt のデフォルトセッティングは 3 つのプラグインによって提供される:

1.  `CorePlugin`: タスクの並列実行などのコア機能。
2.  `IvyPlugin`: モジュールの公開や依存性の解決機能。
3.  `JvmPlugin`: Java/Scala プロジェクトのコンパイル/テスト/実行/パッケージ化。

さらに `JUnitXmlReportPlugin` は実験的に junit-xml の生成機能を提供する。

古くからある auto plugin ではないプラグインは、[マルチプロジェクトビルド][Multi-Project]内に
異なるタイプのプロジェクトを持つことができるように、セッティング群を明示的に追加することを必要とする。

各プラグインのドキュメントに設定方法が明記されているかと思うが、
一般的にはベースとなるセッティング群を追加して、必要に応じてカスタマイズするというパターンが多い。

例えば sbt-site プラグインの例で説明すると `site.sbt` というファイルを新しく作って

```scala
site.settings
```

を `site.sbt` に記述することで有効化できる。

ビルド定義がマルチプロジェクトの場合は、プロジェクトに直接追加する:

```scala
// don't use the site plugin for the `util` project
lazy val util = (project in file("util"))

// enable the site plugin for the `core` project
lazy val core = (project in file("core"))
  .settings(site.settings)
```

### グローバル・プラグイン

プラグインを `$global_plugins_base$` 以下で宣言することで全てのプロジェクトに対して一括してプラグインをインストールすることができる。
`$global_plugins_base$` はそのクラスパスをすべての sbt ビルド定義に対して export する sbt プロジェクトだ。
大雑把に言えば、`$global_plugins_base$` 内の `.sbt` ファイルや `.scala` ファイルは、それが全てのプロジェクトの `project/` ディレクトリに入っているかのようにふるまう。

`$global_plugins_base$build.sbt` を作って、そこに `addSbtPlugin()` 式を書くことで
全プロジェクトにプラグインを追加することができる。
しかし、これを多用するとマシン環境への依存性を増やしてしまうことになるので、この機能は注意してほどほどに使うべきだ。
[ベスト・プラクティス][global-vs-local-plugins]も参照してほしい。


### 利用可能なプラグイン

[プラグインのリスト](../../docs/Community-Plugins.html)がある。

特に人気のプラグインは:

 - IDE のためのプラグイン（sbt プロジェクトを好みの IDE にインポートするためのもの）
 - [xsbt-web-plugin](https://github.com/earldouglas/xsbt-web-plugin) のような Web フレームワークをサポートするプラグイン

プラグイン開発の方法など、プラグインに関する詳細は [Plugins][Plugins] を参照。
ベストプラクティスを知りたいなら、[ベスト・プラクティス][Plugins-Best-Practices] を見てほしい。

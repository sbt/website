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

プラグインの使用
--------------

このガイドの前のページをまず読んでほしい。
特に [build.sbt][Basic-Def]
と
[ライブラリ依存性][Library-Dependencies]を理解していることが必要になる。

### プラグインって何?

プラグインは、新たなセッティングを追加するなどして、ビルド定義を拡張する。
そのセッティングは、新しいタスクを加えることもでき、
例えば、テストカバレッジレポートを生成する `codeCoverage` というタスクをプラグインが提供することができる。

### プラグインの宣言

プロジェクトが `hello` ディレクトリにあって、ビルド定義に
sbt-site を追加するとした場合、`hello/project/site.sbt` を新規作成して、
プラグインの Ivy モジュール ID と共に `addSbtPlugin`
を呼び出す:

```scala
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.7.0")
```

sbt-assembly を追加したければ、`hello/project/assembly.sbt` を作って以下を書く:

```scala
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.2")
```

全てのプラグインがデフォルトのリポジトリにある訳では無いので、
プラグインの説明書にレポジトリの追加する手順が書かれているかもしれない:

```scala
resolvers += Resolver.sonatypeRepo("public")
```

通常、プラグインは、プロジェクトに追加されるセッティングを提供することで機能を追加する。
これを次に説明しよう。

### auto plugin の有効化と無効化

プラグインはビルド定義に自動的に追加されるセッティングを宣言することができ、
その場合は何もしなくてもいい。

sbt 0.13.5 より新しい [auto plugin][Plugins] というプラグインを自動的に追加して、
セッティングの依存性がプロジェクトにあることを安全に保証する機構ができた。
auto plugin の多くはデフォルトセッティングを自動的に追加するけども、
中には明示的な有効化を必要とするものもある。

明示的な有効化が必要な auto plugin を使っている場合は、以下を `build.sbt` に追加する:

```scala
lazy val util = (project in file("util")).
  enablePlugins(FooPlugin, BarPlugin).
  settings(
    name := "hello-util"
  )
```

プロジェクトは `enablePlugins` メソッドを用いて使用したい auto plugin
を明示的に定義することができる。

プロジェクトは、`disablePlugins` メソッドを用いてプラグインを除外することもできる。
例えば、`util` から `IvyPlugin` のセッティングを除外したいとすると、`build.sbt` を以下のように変更する:

```scala
lazy val util = (project in file("util")).
  enablePlugins(FooPlugin, BarPlugin).
  disablePlugins(plugins.IvyPlugin).
  settings(
    name := "hello-util"
  )
```

明示的な有効化が必要かはそれぞれの auto plugin がドキュメントに書くべきだ。
現行プロジェクトにおいてどの auto plugin が有効化されているかが気になるなら、
sbt コンソールから `plugins` コマンドを実行してみてほしい。

例えば、


```
> plugins
In file:/home/jsuereth/projects/sbt/test-ivy-issues/
        sbt.plugins.IvyPlugin: enabled in scala-sbt-org
        sbt.plugins.JvmPlugin: enabled in scala-sbt-org
        sbt.plugins.CorePlugin: enabled in scala-sbt-org
        sbt.plugins.JUnitXmlReportPlugin: enabled in scala-sbt-org
```

ここでは、`plugins` の表示によって sbt のデフォルトのプラグインが全て有効化されていることが分かる。
sbt のデフォルトセッティングは 3つのプラグインによって提供される:

1.  `CorePlugin`: タスクの並列実行などのコア機能。
2.  `IvyPlugin`: モジュールの公開や依存性の解決機能。
3.  `JvmPlugin`: Java/Scala プロジェクトのコンパイル/テスト/実行/パッケージ化。

さらに `JUnitXmlReportPlugin` は実験的に junit-xml の生成機能を提供する。

古い auto plugin ではないプラグインは、、[マルチプロジェクトビルド][Multi-Project]内に
異なるタイプのプロジェクトを持つことができるようにセッティングを明示的に追加することを必要とする。
それぞれのプラグインのドキュメンテーションに設定方法が書いてあると思うけど、
典型的にはベースとなるセッティングを追加して、必要に応じてカスタム化というパターンが多い。

sbt-site を用いて具体例で説明すると、`site.sbt` というファイルを新しく作って

```scala
site.settings
```

以上を `site.sbt` に書くことで有効化することができる。

もし、ビルド定義がマルチプロジェクトの場合は、プロジェクトに直接追加する:

```scala
// don't use the site plugin for the `util` project
lazy val util = (project in file("util"))

// enable the site plugin for the `core` project
lazy val core = (project in file("core")).
  settings(site.settings : _*)
```

### グローバル・プラグイン

プラグインを `$global_plugins_base$` 以下で宣言することで全てのプロジェクトに対して一括してプラグインをインストールすることができる。
`$global_plugins_base$` は、sbt プロジェクトで、そのクラスパスは全ての sbt ビルド定義にエクスポートされる。
大まかに言うと、`$global_plugins_base$` 内の `.sbt` ファイルは、それが全てのプロジェクトの
`project/` ディレクトリに入っているかのように振る舞う。

`$global_plugins_base$build.sbt` を作って、そこに `addSbtPlugin()` 式を書くことで
全プロジェクトにプラグインを追加することができる。
しかし、これを多用するとマシン環境への依存性を増やしてしまうことになるので、この機能は注意してほどほどに使うべきだ。
[ベスト・プラクティス][global-vs-local-plugins]も参照してほしい。


### 利用可能なプラグイン

[プラグインのリスト](../../docs/Community-Plugins.html)がある。

特に人気のプラグインは:

 - IDE 専用プラグイン（sbt プロジェクトを IDE にインポートする）
 - [xsbt-web-plugin](https://github.com/JamesEarlDouglas/xsbt-web-plugin) などの、web フレームワークプラグイン

プラグインの作成などプラグインに関する詳細は [Plugins][Plugins] とプラグインの[ベスト・プラクティス][Plugins-Best-Practices] を参照。

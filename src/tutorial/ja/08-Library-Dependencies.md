---
title: ライブラリ依存性
layout: default
---

[Keys]: http://www.scala-sbt.org/release/sxr/sbt/Keys.scala.html "Keys.scala"
[Apache Ivy]: http://ant.apache.org/ivy/
[Ivy revisions]: http://ant.apache.org/ivy/history/2.3.0-rc1/ivyfile/dependency.html#revision
[Extra attributes]: http://ant.apache.org/ivy/history/2.3.0-rc1/concept.html#extra
[through Ivy]: http://ant.apache.org/ivy/history/latest-milestone/concept.html#checksum
[ScalaCheck]: http://scalacheck.org
[specs]: http://code.google.com/p/specs/
[ScalaTest]: http://scalatest.org

# ライブラリ依存性

[前](../more-about-settings) _始める sbt 9/14 ページ_ [次](../full-def)

このページは、このガイドのこれまでのページ、特に [.sbt ビルド定義](../basic-def)、[スコープ](../scope)、と
[他の種類のセッティング](../more-about-settings)を読んでいることを前提とする。

ライブラリ依存性は二つの方法で加えることができる:

 - `lib` ディレクトリに jar ファイルを入れることでできる_アンマネージ依存性_（unmanaged dependencies）
 - ビルド定義に設定され、リポジトリから自動でダウンロードされる_マネージ依存性_（managed dependencies）

## アンマネージ依存性

ほとんどの人は、アンマネージ依存性ではなく、マネージ依存性を使っている。だけど、始めはアンマネージの方が簡単なので分かりやすい。

アンマネージ依存性を説明すると、こんな感じになる。jar ファイルを `lib` に入れると、それはプロジェクトのクラスパスに追加される。
以上！

例えば、[ScalaCheck]、[specs]、[ScalaTest] などのテスト用の jar を `lib` に加えることもできる。

`lib` の依存性は（`compile`、`test`、`run`、そして `console` の）全てのクラスパスに追加される。
もし、どれか一つのクラスパスを変えたい場合は、例えば `dependencyClasspath in Compile` や
`dependencyClasspath in Runtime` などを適宜調整する必要がある。
`~=` を使って既存のクラスパスの値を受け取り、いらないものを filter で外して、新しいクラスパスの値を返せばいい。
`~=` の詳細に関しては、[他の種類のセッティング](../more-about-settings)参照。

アンマネージ依存性を利用するのに、`build.sbt` には何も書かなくてもいいけど、
デフォルトの `lib` 以外のディレクトリを使いたい場合は、
`unmanaged-base` キーを変更することができる。

`lib` のかわりに、`custom_lib` を使うには:

    unmanagedBase <<= baseDirectory { base => base / "custom_lib" }

`baseDirectory` はプロジェクトのルートディレクトリで、
[他の種類のセッティング](../more-about-settings)で説明したとおり、ここでは `unmanagedBase`
を `<<=` を使って `baseDirectory` の値に基づいて変更している。

他には、`unmanged-jars` という `unmanaged-base` ディレクトリに入っている jar ファイルのリストを返すタスクがある。
複数のディレクトリを使うとか、何か別の複雑なことを行う場合は、この `unmanaged-jar` タスクを何か別のものに変える必要があるかもしれない。

## マネージ依存性

sbt は、[Apache Ivy] を使ってマネージ依存性を実装するため、既に Maven か Ivy に慣れていれば、違和感無く入り込めるだろう。

### `libraryDependencies` キー

依存性を `libraryDependencies` セッティングに列挙するだけで、普通はうまくいく。
Maven POM ファイルや、Ivy コンフィギュレーションファイルを書くなどして、依存性を外部で設定してしまって、
sbt にその外部コンフィギュレーションファイルを使わせるということも可能だ。
これに関しては、[Library Management] を参照。

依存性の宣言は、以下のようになる。ここで、`groupId`、`artifactId`、と `revision` は文字列だ:

    libraryDependencies += groupID % artifactID % revision

もしくは、以下のようになる。このときの `configuration` も文字列だ。

    libraryDependencies += groupID % artifactID % revision % configuration

`libraryDependencies` は [Keys] で以下のように定義されている:

     val libraryDependencies = SettingKey[Seq[ModuleID]]("library-dependencies", "Declares managed dependencies.")

`%` メソッドは、文字列から `ModuleID` オブジェクトを作り、その `ModuleID` を `libraryDependencies` に追加するだけでいい。

当然、sbt は（Ivy を通じて）モジュールをどこからダウンロードしてくるかを知らなければいけない。
もしモジュールが sbt に最初から入っているデフォルトのリポジトリの一つにあれば、ちゃんと動く。
例えば、Apache Derby はデフォルトのリポジトリにある:

    libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3"

これを `build.sbt` に打ち込んで、`update` を実行すると、sbt は Derby を
`~/.ivy2/cache/org.apache.derby/` にダウンロードするはずだ。
（ちなみに、`update` は `compile` の依存性であるため、手動で `update` と打ち込む必要がある状況は普通は無い。）

当然、`++=` を使って一度に依存ライブラリのリストを追加することもできる:

<pre>
libraryDependencies ++= Seq(
    groupID % artifactID % revision,
    groupID % otherID % otherRevision
)
</pre>

`libraryDependencies` に対して `:=`、`<<=`、`<+=`、その他を使う機会があるかもしれないが、稀だろう。

### `%%` を使って正しい Scala バージョンを入手する

`groupID % artifactID % revision` のかわりに、
`groupID %% artifactID % revision` を使うと（違いは groupID の後ろの `%%`）、
sbt はプロジェクトの Scala バージョンをアーティファクト名に追加する。
これはただの略記法なので、`%%` 無しで書くこともできる:

    libraryDependencies += "org.scala-tools" % "scala-stm_2.9.1" % "0.3"

ビルドのバージョンが `scalaVersion` が `2.9.1` だとすると、以下は等価だ:

    libraryDependencies += "org.scala-tools" %% "scala-stm" % "0.3"

多くの依存ライブラリが複数の Scala バージョンに対してコンパイルされていて、
プロジェクトに合ったものを選択したいときに使うというのが考えだ。

実践上での問題として、多くの場合依存ライブラリは少しズレた Scala バージョンが使われることがあるけど、
`%%` はそこまは賢くない。そのため、依存ライブラリが `2.9.0` までしか出てなくて、
プロジェクトが `scalaVersion := "2.9.1"` の場合、`2.9.0` の依存ライブラリが多分動作するにも関わらず `%%` を使うことができない。
もし、`%%` が動かなくなったら、依存ライブラリが使っている実際のバージョンを確認して、
動くだろうバージョン（それががあればの話だけど）に決め打ちすればいい。

詳しくは、[Cross Build] を参照。

### Ivy revision

`groupID % artifactID % revision` の `revision` は、単一の固定されたバージョン番号じゃなくてもいい。
Ivy は与えられた制限の中でモジュールの最新の revision を選ぶことができる。
`"1.6.1"` のような固定 revision ではなく、`"latest.integration"`、`"2.9.+"`、や `"[1.0,)"` など指定できる。
詳しくは、[Ivy revisions] を参照。

### Resolvers

全てのパッケージが一つのサーバに置いてあるとは限らない。
sbt は、デフォルトで standard Maven2 repository のリポジトリを使う。
もし依存ライブラリがこのデフォルトのリポジトリに無ければ、Ivy がそれを見つけられるように _resolver_ を追加する必要がある。

リポジトリを追加するには、以下のようにする:

    resolvers += name at location

例えば:

    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

[Keys] で定義されいる `resolvers` キーは以下のようになっている:

    val resolvers = SettingKey[Seq[Resolver]]("resolvers", "The user-defined additional resolvers for automatically managed dependencies.")

`at` メソットは、二つの文字列から `Resolver` オブジェクトを作る。

sbt は、リポジトリとして追加すれば、ローカル Maven リポジトリも検索することができる:

    resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

他の種類のリポジトリの定義の詳細に関しては、[Resolvers] 参照。

### デフォルトの resolver のオーバーライド

`resolvers` は、デフォルトの resolver を含まず、ビルド定義によって加えられる追加のものだけを含む。

`sbt` は、`resolvers` をデフォルトのリポジトリと組み合わせて `external-resolvers` を形成する。　

そのため、デフォルトの resolver を変更したり、削除したい場合は、`resolvers` ではなく、`external-resolvers` をオーバーライドする必要がある。

### コンフィギュレーションごとの依存性

依存ライブラリをテストコード（`Test` コンフィギュレーションでコンパイルされる `src/test/scala` 内のコード）から使いたいが、
メインのコードでは使わないということがよくある。

ある依存ライブラリが `Test` コンフィギュレーションのクラスパスには出てきて欲しいけど、
`Compile` コンフィギュレーションではいらない場合は、以下のように `% "test"` と追加する:

    libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % "test"

sbt のインタラクティブモードで `show compile:dependency-classpath` と打ち込んでも、Derby は出てこないはずだ。
だけど、`show test:dependency-classpath` と打ち込むと、Derby の jar がリストにあるのが確認できる。

普通は、[ScalaCheck]、[specs]、[ScalaTest] などのテスト関連の依存ライブラリは `% "test"` と共に定義される。

# 続いては

ライブラリの依存性に関しては、もうこの入門用のページで見つからない情報があれば、[このページ](../library-management)に
もう少し詳細やコツが書いてある。

「始める sbt」を順に読んでいるならば、次は、[.scala ビルド定義](../full-def)に進む。

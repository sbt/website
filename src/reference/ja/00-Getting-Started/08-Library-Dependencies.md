---
out: Library-Dependencies.html
---

  [Keys]: ../../api/sbt/Keys\$.html
  [Apache Ivy]: https://ant.apache.org/ivy/
  [Ivy revisions]: https://ant.apache.org/ivy/history/2.3.0-rc1/ivyfile/dependency.html#revision
  [Extra attributes]: https://ant.apache.org/ivy/history/2.3.0-rc1/concept.html#extra
  [through Ivy]: https://ant.apache.org/ivy/history/latest-milestone/concept.html#checksum
  [ScalaCheck]: https://scalacheck.org
  [Specs2]: http://specs2.org
  [ScalaTest]: https://www.scalatest.org
  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Task-Graph]: Task-Graph.html
  [external-maven-ivy]: ../../docs/Library-Management.html#external-maven-ivy
  [Cross-Build]: ../../docs/Cross-Build.html
  [Resolvers]: ../../docs/Resolvers.html
  [Library-Management]: ../../docs/Library-Management.html

ライブラリ依存性
--------------

このページは、このガイドのこれまでのページ、特に
[.sbt ビルド定義][Basic-Def]、[スコープ][Scopes]、と
[タスク・グラフ][Task-Graph]
を読んでいることを前提とする。

ライブラリ依存性は二つの方法で加えることができる:

 - `lib` ディレクトリに jar ファイルを入れることでできる_アンマネージ依存性_（unmanaged dependencies）
 - ビルド定義に設定され、リポジトリから自動でダウンロードされる_マネージ依存性_（managed dependencies）

### アンマネージ依存性（Unmanaged Dependencies）

ほとんどの人はアンマネージ依存性ではなくマネージ依存性を使う。
しかし、アンマネージの方が最初に始めるにあたってはより簡単かもしれない。

アンマネージ依存性はこんな感じのものだ: jar ファイルを `lib` 配下に置いておけばプロジェクトのクラスパスに追加される、以上！

[ScalaCheck][ScalaCheck]、[Specs2][Specs2]、[ScalaTest][ScalaTest] のようなテスト用の jar ファイルも `lib` に配置できる。

`lib` 配下の依存ライブラリは（`compile`、`test`、`run`、そして `console` の）全てのクラスパスに追加される。
もし、どれか一つのクラスパスを変えたい場合は、例えば `Compile / dependencyClasspath` や
`Runtime / dependencyClasspath` などを適宜調整する必要がある。

アンマネージ依存性を利用するのに、`build.sbt` には何も書く必要はないが、デフォルトの `lib` 以外のディレクトリを使いたい場合は `unmanagedBase` キーで変更することができる。

`lib` のかわりに、`custom_lib` を使うならこのようになる:

```scala
unmanagedBase := baseDirectory.value / "custom_lib"
```

`baseDirectory` はプロジェクトのベースディレクトリで、
[タスク・グラフ][Task-Graph]で説明したとおり、ここでは `unmanagedBase`
を `value` を使って取り出した `baseDirectory` の値を用いて変更している。

他には、`unmangedJars` という `unmanagedBase` ディレクトリに入っている jar ファイルのリストを返すタスクがある。
複数のディレクトリを使うとか、何か別の複雑なことを行う場合は、この `unmanagedJar` タスクを何か別のものに変える必要があるかもしれない。
例えば `Compile` コンフィギュレーション時に `lib`ディレクトリのファイルを無視したい、など。

```scala
Compile / unmanagedJars := Seq.empty[sbt.Attributed[java.io.File]]
```

### マネージ依存性（Managed Dependencies）

sbt は [Apache Ivy] を使ってマネージ依存性を実装しているので、既に Maven か Ivy に慣れているなら、違和感無く入り込めるだろう。

#### `libraryDependencies` キー

大体の場合、依存性を `libraryDependencies` セッティングに列挙するだけでうまくいくだろう。
Maven POM ファイルや、Ivy コンフィギュレーションファイルを書くなどして、依存性を外部で設定してしまって、
sbt にその外部コンフィギュレーションファイルを使わせるということも可能だ。
これに関しては、[Library Management] を参照。

依存性の宣言は、以下のようになる。ここで、`groupId`、`artifactId`、と `revision` は文字列だ:

```scala
libraryDependencies += groupID % artifactID % revision
```

もしくは、以下のようになる。このときの `configuration` は文字列もしくは `Configuration` の値だ (`Test` など)。

```scala
libraryDependencies += groupID % artifactID % revision % configuration
```

`libraryDependencies` は [Keys] で以下のように定義されている:

```scala
val libraryDependencies = settingKey[Seq[ModuleID]]("Declares managed dependencies.")
```

`%` メソッドは、文字列から `ModuleID` オブジェクトを作るので、君はその `ModuleID` を `libraryDependencies` に追加するだけでいい。

当然ながら、sbt は（Ivy を通じて）モジュールをどこからダウンロードしてくるかを知っていなければならない。
もしそのモジュールが sbt に初めから入っているデフォルトのリポジトリの一つに存在していれば、何もしなくてもそのままで動作する。
例えば、Apache Derby は Maven2 の標準リポジトリ（訳注: sbt にあらかじめ入っているデフォルトリポジトリの一つ）に存在している:

```scala
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3"
```

これを `build.sbt` に記述して `update` を実行すると、sbt は Derby を `\$COURSIER_CACHE/https/repo1.maven.org/maven2/org/apache/derby/` にダウンロードするはずだ。
（ちなみに、`update` は `compile` の依存性であるため、ほとんどの場合、手動で `update` と入力する必要はないだろう）

もちろん `++=` を使って依存ライブラリのリストを一度に追加することもできる:

```scala
libraryDependencies ++= Seq(
  groupID % artifactID % revision,
  groupID % otherID % otherRevision
)
```

`libraryDependencies` に対して `:=` を使う機会があるかもしれないが、おそらくそれは稀だろう。

#### `%%` を使って正しい Scala バージョンを入手する

`groupID % artifactID % revision` のかわりに、
`groupID %% artifactID % revision` を使うと（違いは groupID の後ろの二つ連なった `%%`）、
sbt はプロジェクトの Scala のバイナリバージョンをアーティファクト名に追加する。
これはただの略記法なので `%%` 無しで書くこともできる:

```scala
libraryDependencies += "org.scala-stm" % "scala-stm_2.13" % "$example_scala_stm_version$"
```

君のビルドの Scala バージョンが `$example_scala213$` だとすると、以下の設定は上記と等価だ（"org.scala-stm" の後ろの二つ連なった %% に注意）:

```scala
libraryDependencies += "org.scala-stm" %% "scala-stm" % "$example_scala_stm_version$"
```

多くの依存ライブラリは複数の Scala バイナリバージョンに対してコンパイルされており、
ライブラリの利用者はバイナリ互換性のあるものを選択したいと思うはずである。

詳しくは、[Cross Build][Cross-Build] を参照。

#### Ivy revision

`groupID % artifactID % revision` の `revision` は、単一の固定されたバージョン番号でなくてもよい。
Ivy は指定されたバージョン指定の制限の中でモジュールの最新の revision を選ぶことができる。
`"1.6.1"` のような固定 revision ではなく、`"latest.integration"`、`"2.9.+"`、や `"[1.0,)"` など指定できる。
詳しくは、[Ivy revisions] を参照。

<!-- TODO: Add aliases -->

#### Resolvers

全てのパッケージが一つのサーバに置いてあるとは限らない。
sbt は、デフォルトで Maven の標準リポジトリ（訳注：Maven Central Repository）を使う。
もし依存ライブラリがデフォルトのリポジトリに存在しないなら、Ivy がそれを見つけられるよう _resolver_ を追加する必要がある。

リポジトリを追加するには、以下のように:

```scala
resolvers += name at location
```

二つの文字列の間の特別な `at` を使う。

例えばこのようになる:

```scala
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
```

[Keys][Keys] で定義されている `resolvers` キーは以下のようになっている:

```scala
val resolvers = settingKey[Seq[Resolver]]("The user-defined additional resolvers for automatically managed dependencies.")
```

`at` メソッドは、二つの文字列から `Resolver` オブジェクトを作る。

sbt は、リポジトリとして追加すれば、ローカル Maven リポジトリも検索することができる:

```scala
resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
```

こんな便利な指定方法もある:

```scala
resolvers += Resolver.mavenLocal
```

他の種類のリポジトリの定義の詳細に関しては、[Resolvers] 参照。

#### デフォルトの resolver のオーバーライド

`resolvers` は、デフォルトの resolver を含まず、ビルド定義によって加えられる追加のものだけを含む。

`sbt` は、`resolvers` をデフォルトのリポジトリと組み合わせて `external-resolvers` を形成する。　

そのため、デフォルトの resolver を変更したり、削除したい場合は、`resolvers` ではなく、`external-resolvers` をオーバーライドする必要がある。

#### コンフィギュレーションごとの依存性

依存ライブラリをテストコード（`Test` コンフィギュレーションでコンパイルされる `src/test/scala` 内のコード）から使いたいが、
メインのコードでは使わないということがよくある。

ある依存ライブラリが `Test` コンフィギュレーションのクラスパスには出てきてほしいが、`Compile` コンフィギュレーションでは要らないという場合は、以下のように `% "test"` と追加する:

```scala
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % "test"
```

`Test` コンフィグレーションの型安全なバージョンを使ってもよい:

```scala
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % Test
```

この状態で sbt のインタラクティブモードで `show Compile/dependencyClasspath` と入力しても Derby は出てこないはずだ。
だが、`show Test/dependencyClasspath` と入力すると、Derby の jar がリストに含まれていることを確認できるだろう。

普通は、[ScalaCheck][ScalaCheck]、[Specs2][Specs2]、[ScalaTest][ScalaTest] などのテスト関連の依存ライブラリは `% "test"` と共に定義される。

ライブラリの依存性に関しては、もうこの入門用のページで見つからない情報があれば、[このページ][Library-Management]に
もう少し詳細やコツが書いてある。

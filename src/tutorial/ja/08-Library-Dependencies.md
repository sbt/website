---
out: Library-Dependencies.html
---

  [Keys]: ../sxr/sbt/Keys.scala.html
  [Apache Ivy]: https://ant.apache.org/ivy/
  [Ivy revisions]: https://ant.apache.org/ivy/history/2.3.0-rc1/ivyfile/dependency.html#revision
  [Extra attributes]: https://ant.apache.org/ivy/history/2.3.0-rc1/concept.html#extra
  [through Ivy]: https://ant.apache.org/ivy/history/latest-milestone/concept.html#checksum
  [ScalaCheck]: http://scalacheck.org
  [specs]: http://code.google.com/p/specs/
  [ScalaTest]: http://scalatest.org
  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [More-About-Settings]: More-About-Settings.html
  [external-maven-ivy]: ../../docs/Library-Management.html#external-maven-ivy
  [Cross-Build]: ../../docs/Cross-Build.html
  [Resolvers]: ../../docs/Resolvers.html
  [Library-Management]: ../../docs/Library-Management.html

ライブラリ依存性
--------------

このページは、このガイドのこれまでのページ、特に
[.sbt ビルド定義][Basic-Def]、[スコープ][Scopes]、と
[他の種類のセッティング][More-About-Settings]
を読んでいることを前提とする。

ライブラリ依存性は二つの方法で加えることができる:

 - `lib` ディレクトリに jar ファイルを入れることでできる_アンマネージ依存性_（unmanaged dependencies）
 - ビルド定義に設定され、リポジトリから自動でダウンロードされる_マネージ依存性_（managed dependencies）

### アンマネージ依存性

ほとんどの人は、アンマネージ依存性ではなく、マネージ依存性を使っている。だけど、始めはアンマネージの方が簡単なので分かりやすい。

アンマネージ依存性を説明すると、こんな感じになる。jar ファイルを `lib` に入れると、それはプロジェクトのクラスパスに追加される。
以上！

例えば、[ScalaCheck][ScalaCheck]、[specs][specs]、[ScalaTest][ScalaTest] などのテスト用の jar を `lib` に加えることもできる。

`lib` の依存性は（`compile`、`test`、`run`、そして `console` の）全てのクラスパスに追加される。
もし、どれか一つのクラスパスを変えたい場合は、例えば `dependencyClasspath in Compile` や
`dependencyClasspath in Runtime` などを適宜調整する必要がある。

アンマネージ依存性を利用するのに、`build.sbt` には何も書かなくてもいいけど、
デフォルトの `lib` 以外のディレクトリを使いたい場合は、
`unmanagedBase` キーを変更することができる。

`lib` のかわりに、`custom_lib` を使うには:

```scala
unmanagedBase := baseDirectory.value / "custom_lib"
```

`baseDirectory` はプロジェクトのルートディレクトリで、
[他の種類のセッティング][More-About-Settings]で説明したとおり、ここでは `unmanagedBase`
を `value` を使って取り出した `baseDirectory` の値を用いて変更している。

他には、`unmangedJars` という `unmanagedBase` ディレクトリに入っている jar ファイルのリストを返すタスクがある。
複数のディレクトリを使うとか、何か別の複雑なことを行う場合は、この `unmanagedJar` タスクを何か別のものに変える必要があるかもしれない。
例えば `Compile` コンフィギュレーション時に `lib`ディレクトリのファイルを無視したい、など。

```scala
unmanagedJars in Compile := Seq.empty[sbt.Attributed[java.io.File]]
```

### マネージ依存性

sbt は、[Apache Ivy] を使ってマネージ依存性を実装するため、既に Maven か Ivy に慣れていれば、違和感無く入り込めるだろう。

#### `libraryDependencies` キー

依存性を `libraryDependencies` セッティングに列挙するだけで、普通はうまくいく。
Maven POM ファイルや、Ivy コンフィギュレーションファイルを書くなどして、依存性を外部で設定してしまって、
sbt にその外部コンフィギュレーションファイルを使わせるということも可能だ。
これに関しては、[Library Management] を参照。

依存性の宣言は、以下のようになる。ここで、`groupId`、`artifactId`、と `revision` は文字列だ:

```scala
libraryDependencies += groupID % artifactID % revision
```

もしくは、以下のようになる。このときの `configuration` も文字列だ。

```scala
libraryDependencies += groupID % artifactID % revision % configuration
```

`libraryDependencies` は [Keys] で以下のように定義されている:

```scala
val libraryDependencies = SettingKey[Seq[ModuleID]]("library-dependencies", "Declares managed dependencies.")
```

`%` メソッドは、文字列から `ModuleID` オブジェクトを作り、その `ModuleID` を `libraryDependencies` に追加するだけでいい。

当然、sbt は（Ivy を通じて）モジュールをどこからダウンロードしてくるかを知らなければいけない。
もしモジュールが sbt に最初から入っているデフォルトのリポジトリの一つにあれば、ちゃんと動く。
例えば、Apache Derby はデフォルトのリポジトリにある:

```scala
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3"
```

これを `build.sbt` に打ち込んで、`update` を実行すると、sbt は Derby を
`~/.ivy2/cache/org.apache.derby/` にダウンロードするはずだ。
（ちなみに、`update` は `compile` の依存性であるため、手動で `update` と打ち込む必要がある状況は普通は無い。）

当然、`++=` を使って一度に依存ライブラリのリストを追加することもできる:

```scala
libraryDependencies ++= Seq(
    groupID % artifactID % revision,
    groupID % otherID % otherRevision
)
```

`libraryDependencies` に対して `:=`、その他を使う機会があるかもしれないが、稀だろう。

#### `%%` を使って正しい Scala バージョンを入手する

`groupID % artifactID % revision` のかわりに、
`groupID %% artifactID % revision` を使うと（違いは groupID の後ろの `%%`）、
sbt はプロジェクトの Scala バージョンをアーティファクト名に追加する。
これはただの略記法なので、`%%` 無しで書くこともできる:

```scala
libraryDependencies += "org.scala-tools" % "scala-stm_2.9.1" % "0.3"
```

ビルドのバージョンが `scalaVersion` が `2.9.1` だとすると、以下は等価だ:

```scala
libraryDependencies += "org.scala-tools" %% "scala-stm" % "0.3"
```

多くの依存ライブラリが複数の Scala バージョンに対してコンパイルされていて、
プロジェクトに合ったものを選択したいときに使うというのが考えだ。

実践上での問題として、多くの場合依存ライブラリは少しズレた Scala バージョンが使われることがあるけど、
`%%` はそこまでは賢くない。そのため、依存ライブラリが `2.10.1` までしか出てなくて、
プロジェクトが `scalaVersion := "2.10.4"` の場合、`2.10.1` の依存ライブラリが多分動作するにも関わらず `%%` を使うことができない。
もし、`%%` が動かなくなったら、依存ライブラリが使っている実際のバージョンを確認して、
動くだろうバージョン（それがあればの話だけど）に決め打ちすればいい。

詳しくは、[Cross Build] を参照。

#### Ivy revision

`groupID % artifactID % revision` の `revision` は、単一の固定されたバージョン番号じゃなくてもいい。
Ivy は与えられた制限の中でモジュールの最新の revision を選ぶことができる。
`"1.6.1"` のような固定 revision ではなく、`"latest.integration"`、`"2.9.+"`、や `"[1.0,)"` など指定できる。
詳しくは、[Ivy revisions] を参照。

<!-- TODO: Add aliases -->

#### Resolvers

全てのパッケージが一つのサーバに置いてあるとは限らない。
sbt は、デフォルトで standard Maven2 repository のリポジトリを使う。
もし依存ライブラリがこのデフォルトのリポジトリに無ければ、Ivy がそれを見つけられるように _resolver_ を追加する必要がある。

リポジトリを追加するには、以下のようにする:

```scala
resolvers += name at location
```

例えば:

```scala
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
```

[Keys][Keys] で定義されいる `resolvers` キーは以下のようになっている:

```scala
val resolvers = settingKey[Seq[Resolver]]("The user-defined additional resolvers for automatically managed dependencies.")
```

`at` メソッドは、二つの文字列から `Resolver` オブジェクトを作る。

sbt は、リポジトリとして追加すれば、ローカル Maven リポジトリも検索することができる:

```scala
resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
```

他の種類のリポジトリの定義の詳細に関しては、[Resolvers] 参照。

#### デフォルトの resolver のオーバーライド

`resolvers` は、デフォルトの resolver を含まず、ビルド定義によって加えられる追加のものだけを含む。

`sbt` は、`resolvers` をデフォルトのリポジトリと組み合わせて `external-resolvers` を形成する。　

そのため、デフォルトの resolver を変更したり、削除したい場合は、`resolvers` ではなく、`external-resolvers` をオーバーライドする必要がある。

#### コンフィギュレーションごとの依存性

依存ライブラリをテストコード（`Test` コンフィギュレーションでコンパイルされる `src/test/scala` 内のコード）から使いたいが、
メインのコードでは使わないということがよくある。

ある依存ライブラリが `Test` コンフィギュレーションのクラスパスには出てきて欲しいけど、
`Compile` コンフィギュレーションではいらない場合は、以下のように `% "test"` と追加する:

```scala
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % "test"
```

以下のように書くことで、 `Test` コンフィギュレーションに合わせた型安全なバーションを用いることが出来るかもしれない:

```scala
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % Test
```

sbt のインタラクティブモードで `show compile:dependency-classpath` と打ち込んでも、Derby は出てこないはずだ。
だけど、`show test:dependency-classpath` と打ち込むと、Derby の jar がリストにあるのが確認できる。

普通は、[ScalaCheck][ScalaCheck]、[specs][specs]、[ScalaTest][ScalaTest] などのテスト関連の依存ライブラリは `% "test"` と共に定義される。

ライブラリの依存性に関しては、もうこの入門用のページで見つからない情報があれば、[このページ][Library-Management]に
もう少し詳細やコツが書いてある。

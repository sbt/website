---
out: Directories.html
---

  [Hello]: Hello.html
  [Setup]: Setup.html
  [Full-Def]: Full-Def.html
  [Maven]: http://maven.apache.org/

ディレクトリ構造
--------------

このページは、君が
[sbt をインストール][Setup]して、
[Hello, World][Hello] を見たことを前提にする。

### ベースディレクトリ

sbt 用語では「ベースディレクトリ」(base directory) はプロジェクトが入ったディレクトリを指す。
[Hello, World](../hello) での例のように、`hello/build.sbt` と `hello/hw.scala` が入った
`hello` プロジェクトを作ったとすると、ベースディレクトリは `hello` だ。

### ソースコード

ソースコードは `hello/hw.scala` のようにプロジェクトのベースディレクトリに置くこともできる。
だけど、ほとんどの人は、本物のプロジェクトではそうしない。ゴチャゴチャしすぎるからね。

sbt はデフォルトで [Maven][Maven] と同じディレクトリ構造を使う（全てのパスはベースディレクトリからの相対パスとする）:

```
src/
  main/
    resources/
      <メインの jar に含むファイル>
    scala/
      <メインの Scala ソース>
    java/
      <メインの Java ソース>
  test/
    resources/
      <テストの jar に含むファイル>
    scala/
      <テストの Scala ソース>
    java/
      <テストの Java ソース>
```

`src/` 内の他のディレクトリは無視される。あとは、隠れディレクトリも無視される。

### sbt ビルド定義ファイル

プロジェクトのベースディレクトリに `build.sbt` があるのはもう分かった。
他の sbt 関連のファイルは　`project` サブディレクトリに置かれる。

`project` には `.scala` ファイルを含むことができ、それは `.sbt` ファイルと
組み合わさって一つのビルド定義を構成する。詳しくは、[.scala ビルド定義][Full-Def]を参照。

```
build.sbt
project/
  Build.scala
```

`project` 内に `.sbt` があるのを見ることがあるかもしれないけど、それはプロジェクトの
ベースディレクトリ下の `.sbt` とは別物だ。これに関しても、他に前提となる知識が必要なので、
[後で説明する][Full-Def]。

### ビルド成果物

生成されたファイル（コンパイルされたクラス、パッケージ化された jar ファイル、マネージファイル、キャッシュ、とドキュメンテーション）は、デフォルトで `target` ディレクトリに置かれる。

### バージョン管理の設定

君の `.gitignore` （もしくは、他のバージョン管理システムの同様のファイル）は以下を含むべきだ:

```
target/
```

これは（ディレクトリだけにマッチさせるために）語尾の `/` はつけているけど、
（普通の `target/` に加えて `project/target/` にもマッチさせるために）先頭の `/` は意図して
つけていないことに注意。

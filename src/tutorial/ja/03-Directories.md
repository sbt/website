---
out: Directories.html
---

  [Hello]: Hello.html
  [Setup]: Setup.html
  [Organizing-Build]: Organizing-Build.html
  [Maven]: https://maven.apache.org/

ディレクトリ構造
--------------

このページは、
[sbt をインストール][Setup]して、
[Hello, World][Hello] を読んだことを前提とする。

### ベースディレクトリ

sbt 用語では「ベースディレクトリ(base directory) 」はプロジェクトが入ったディレクトリを指す。
[Hello, World][Hello] での例のように、`hello/build.sbt` と `hello/hw.scala` が入った
`hello` プロジェクトを作った場合、ベースディレクトリは `hello` となる。

### ソースコード

ソースコードは `hello/hw.scala` のようにプロジェクトのベースディレクトリに置くこともできる。
しかし、現実のプロジェクトでは、ほとんどの場合、そのようにはしないだろう。きっとプロジェクトがゴチャゴチャになってしまうからだ。

sbt はデフォルトで [Maven][Maven] と同じディレクトリ構造を使う（全てのパスはベースディレクトリからの相対パスとする）:

```
src/
  main/
    resources/
      <メインの jar に含むデータファイル>
    scala/
      <メインの Scala ソースファイル>
    java/
      <メインの Java ソースファイル>
  test/
    resources/
      <テストの jar に含むデータファイル>
    scala/
      <テストの Scala ソースファイル>
    java/
      <テストの Java ソースファイル>
```

`src/` 内の他のディレクトリは無視される。また、隠しディレクトリも無視される。

### sbt ビルド定義ファイル

プロジェクトのベースディレクトリに `build.sbt` を置くことはご理解いただけたかと思う。
それ以外の sbt 関連の設定ファイルは `project` 配下のサブディレクトリに置かれる。

`project` には `.scala` ファイルを含むことができ、それは `.sbt` ファイルと組み合わさって一つの完全なビルド定義を構成する。
詳しくは、[ビルドの整理][Organizing-Build]を参照。

```
build.sbt
project/
  Build.scala
```

`project` 内に `.sbt` があるのを見ることがあるかもしれないが、それはプロジェクトのベースディレクトリ下の `.sbt` とはまた別物だ。
これに関しては他に前提となる知識が必要なので[後ほど説明する][Organizing-Build]。

### ビルド成果物

生成されたファイル（コンパイルされたクラスファイル、パッケージ化された jar ファイル、managed 配下のファイル、キャッシュとドキュメンテーション）は、デフォルトでは `target` ディレクトリに出力される。

### バージョン管理の設定

`.gitignore` （もしくは、他のバージョン管理システムの同様のファイル）には以下を追加しておくとよいだろう:

```
target/
```

ここでは（ディレクトリだけにマッチさせるために）語尾の `/` は意図的につけていて、一方で
（普通の `target/` に加えて `project/target/` にもマッチさせるために）先頭の `/` は意図的に
つけていないことに注意。

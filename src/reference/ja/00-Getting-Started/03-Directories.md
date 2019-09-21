---
out: Directories.html
---

  [ByExample]: sbt-by-example.html
  [Setup]: Setup.html
  [Organizing-Build]: Organizing-Build.html
  [Maven]: https://maven.apache.org/

ディレクトリ構造
--------------

このページは、
[sbt をインストール][Setup]して、
[例題でみる sbt][ByExample] を読んだことを前提とする。

### ベースディレクトリ

sbt 用語では「ベースディレクトリ(base directory) 」はプロジェクトが入ったディレクトリを指す。
[例題でみる sbt][ByExample] での例のように、`/tmp/foo-build/build.sbt` が入った
`hello` プロジェクトを作った場合、ベースディレクトリは `/tmp/foo-build` となる。

### ソースコード

sbt はデフォルトで [Maven][Maven] と同じディレクトリ構造を使う（全てのパスはベースディレクトリからの相対パスとする）:

```
src/
  main/
    resources/
       <メインの jar に含むデータファイル>
    scala/
       <メインの Scala ソースファイル>
    scala-2.12/
       <メインの Scala 2.12 に特定のソースファイル>
    java/
       <メインの Java ソースファイル>
  test/
    resources/
       <テストの jar に含むデータファイル>
    scala/
       <テストの Scala ソースファイル>
    scala-2.12/
       <テストの Scala 2.12 に特定のソースファイル>
    java/
       <テストの Java ソースファイル>
```

`src/` 内の他のディレクトリは無視される。また、隠しディレクトリも無視される。

ソースコードは `hello/app.scala` のようにプロジェクトのベースディレクトリに置くこともできるが、
小さいプロジェクトはともかくとして、通常のプロジェクトでは
`src/main/` 以下のディレクトリにソースを入れて整理するのが普通だ。
ベースディレクトリに `*.scala` ソースコードを配置できるのは小手先だけのトリックに見えるかもしれないが、
この機能は[後ほど][Organizing-Build]重要になる。

### sbt ビルド定義ファイル

ビルド定義はプロジェクトのベースディレクトリ以下の `build.sbt`
(実は `*.sbt` ならどのファイルでもいい) にて記述する。

```
build.sbt
```

### ビルドサポートファイル

`build.sbt` の他に、`project`
ディレクトリにはヘルパーオブジェクトや一点物のプラグインを定義した
`*.scala` ファイルを含むことができる。
詳しくは、[ビルドの整理][Organizing-Build]を参照。

```
build.sbt
project/
  Dependencies.scala
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

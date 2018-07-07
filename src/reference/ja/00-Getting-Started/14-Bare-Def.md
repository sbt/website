---
out: Bare-Def.html
---

  [Full-Def]: Full-Def.html
  [Basic-Def]: Basic-Def.html

付録: bare .sbt ビルド定義
------------------------

このページでは旧式の `.sbt` ビルド定義の説明をする。
現在の推奨は[マルチプロジェクト .sbt ビルド定義][Basic-Def]だ。

### bare .sbt ビルド定義とは何か

明示的に [Project](../../api/sbt/Project.html) を定義する
[マルチプロジェクト .sbt ビルド定義][Basic-Def]や [.scala ビルド定義][Full-Def]と違って
bare ビルド定義は `.sbt` ファイルの位置から暗黙にプロジェクトが定義される。

`Project` を定義する代わりに、bare `.sbt` ビルド定義は `Setting[_]` 式のリストから構成される。

```scala
name := "hello"

version := "1.0"

scalaVersion := "$example_scala_version$"
```

### (0.13.7 以前) 設定は空白行で区切る

**注意**: 0.13.7 以降は空白行の区切りを必要としない。

こんな風に `build.sbt` を書くことはできない。

```scala
// 空白行がない場合はコンパイルしない
name := "hello"
version := "1.0"
scalaVersion := "2.10.3"
```

sbt はどこまでで式が終わってどこからが次の式なのかを判別するために、何らかの区切りを必要とする。

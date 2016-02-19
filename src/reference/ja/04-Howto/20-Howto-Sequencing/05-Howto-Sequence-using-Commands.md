---
out: Howto-Sequence-using-Commands.html
---

### コマンドを用いた逐次実行

副作用にしか使っていなくて、人間がコマンドを打ち込んでいるのを真似したいだけならば、カスタムコマンドを作れば済むことかもしれない。これは例えば、リリース手順とかに役立つ。

これは sbt そのもののビルドスクリプトから抜粋だ:

```scala
  commands += Command.command("releaseNightly") { state =>
    "stampVersion" ::
      "clean" ::
      "compile" ::
      "publish" ::
      "bintrayRelease" ::
      state
  }
```

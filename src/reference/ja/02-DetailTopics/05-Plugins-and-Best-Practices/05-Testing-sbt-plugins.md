---
out: Testing-sbt-plugins.html
---

  [sbtScriptedTests]: https://github.com/sbt/sbt/tree/1.x/sbt/src/sbt-test
  [xsbt-web-plugin]: https://github.com/earldouglas/xsbt-web-plugin/tree/master/src/sbt-test
  [sbt-assembly]: https://github.com/sbt/sbt-assembly/tree/master/src/sbt-test/sbt-assembly
  [feabb2]: https://github.com/earldouglas/xsbt-web-plugin/commit/feabb2eb554940d9b28049bd0618b6a790d9e141

sbt プラグインをテストする
-----------------------

テストの話をしよう。一度プラグインを書いてしまうと、どうしても長期的なものになってしまう。新しい機能を加え続ける（もしくはバグを直し続ける）ためにはテストを書くのが合理的だ。

### scripted test framework

sbt は、scripted test framework というものが付いてきて、ビルドの筋書きをスクリプトに書くことができる。これは、もともと 変更の自動検知や、部分コンパイルなどの複雑な状況下で sbt 自体をテストするために書かれたものだ:

> ここで、仮に B.scala を削除するが、A.scala には変更を加えないものとする。ここで、再コンパイルすると、A から参照される B が存在しないために、エラーが得られるはずだ。
> [中略 (非常に複雑なことが書いてある)]
>
> scripted test framework は、sbt が以上に書かれたようなケースを的確に処理しているかを確認するために使われている。

このフレームワークは scripted-plugin 経由で利用可能だ。
このページはプラグインにどのようにして scripted-plugin を導入するかを解説する。

### ステップ 1: snapshot

scripted-plugin はプラグインをローカルに publish するため、まずは version を **-SNAPSHOT** なものに設定しよう。ここで SNAPSHOT を使わないと、あなたと世界のあなた以外の人が別々のアーティファクトを観測するといった酷い不整合な状態に入り込む場合があるからだ。

### ステップ 2: SbtPlugin

`build.sbt` で `SbtPlugin` を enable する。

```scala
lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-something"
  )
```

以下のセッティングを `build.sbt` に加える:

```scala
lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-something",
    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false
  )
```

**注意**: `SbtPlugin` は sbt 1.2.1 以上を必要とする。

### ステップ 3: src/sbt-test

`src/sbt-test/<テストグループ>/<テスト名>` というディレクトリ構造を作る。とりあえず、`src/sbt-test/<プラグイン名>/simple` から始めるとする。

ここがポイントなんだけど、`simple` 下にビルドを作成する。プラグインを使った普通のビルド。手動でテストするために、いくつか既にあると思うけど。以下に、`build.sbt` の例を示す:

```scala
lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.10.6",
    assembly / assemblyJarName := "foo.jar"
  )
```

これが、`project/plugins.sbt`:

```scala
sys.props.get("plugin.version") match {
  case Some(x) => addSbtPlugin("com.eed3si9n" % "sbt-assembly" % x)
  case _ => sys.error("""|The system property 'plugin.version' is not defined.
                         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
}
```

これは [earldouglas/xsbt-web-plugin@feabb2][feabb2] から拝借してきた技で、これで scripted テストに version を渡すことができる。

他に、`src/main/scala/hello.scala` も用意した:

```scala
object Main {
  def main(args: Array[String]): Unit = {
    println("hello")
  }
}
```

### ステップ 4: スクリプトを書く

次に、好きな筋書きを記述したスクリプトを、テストビルドのルート下に置いた `test` というファイルに書く。

```
# ファイルが作成されたかを確認
> assembly
\$ exists target/scala-2.10/foo.jar
```

スクリプトの文法は以下の通り:

1. **`#`** は一行コメントを開始する
2. **`>`** `name` はタスクを sbt に送信する（そして結果が成功したかをテストする）
3. **`\$`** `name arg*` はファイルコマンドを実行する（そして結果が成功したかをテストする）
4. **`->`** `name` タスクを sbt に送信するが、失敗することを期待する
5. **`-\$`** `name arg*` ファイルコマンドを実行するが、失敗することを期待する

ファイルコマンドは以下のとおり:

- **`touch`** `path+` は、ファイルを作成するかタイムスタンプを更新する
- **`delete`** `path+` は、ファイルを削除する
- **`exists`** `path+` は、ファイルが存在するか確認する
- **`mkdir`** `path+` は、ディレクトリを作成する
- **`absent`** `path+` は、はファイルが存在しないことを確認する
- **`newer`** `source target` は、`source` の方が新しいことを確認する
- **`must-mirror`** `source target` は、`source` が同一であることを確認する
- **`pause`** は、enter が押されるまで待つ
- **`sleep`** `time` は、スリープする
- **`exec`** `command args*` は、別のプロセスでコマンドを実行する
- **`copy-file`** `fromPath toPath` は、ファイルをコピーする
- **`copy`** `fromPath+ toDir` は、パスを相対構造を保ったまま `toDir` 下にコピーする
- **`copy-flat`** `fromPath+ toDir` は、パスをフラットに `toDir` 下にコピーする

ということで、僕のスクリプトは、`assembly` タスクを実行して、`foo.jar` が作成されたかをチェックする。もっと複雑なテストは後ほど。

### ステップ 5: スクリプトを実行する
スクリプトを実行するためには、プラグインのプロジェクトに戻って、以下を実行する:

```
> scripted
```

これはテストビルドをテンポラリディレクトリにコピーして、`test` スクリプトを実行する。もし全て順調にいけば、まず `publishLocal` の様子が表示され、以下のようなものが表示される:

```
Running sbt-assembly / simple
[success] Total time: 18 s, completed Sep 17, 2011 3:00:58 AM
```

### ステップ 6: カスタムアサーション

ファイルコマンドは便利だけど、実際のコンテンツをテストしないため、それだけでは不十分だ。コンテンツをテストする簡単な方法は、テストビルドにカスタムのタスクを実装してしまうことだ。

上記の hello プロジェクトを例に取ると、生成された jar が "hello" と表示するかを確認したいとする。`scala.sys.process.Process` を用いて jar を走らせることができる。失敗を表すには、単にエラーを投げればいい。以下に `build.sbt` を示す:

```scala
import scala.sys.process.Process

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.10.6",
    assembly / assemblyJarName := "foo.jar",
    TaskKey[Unit]("check") := {
      val process = Process("java", Seq("-jar", (crossTarget.value / "foo.jar").toString))
      val out = (process!!)
      if (out.trim != "bye") sys.error("unexpected output: " + out)
      ()
    }
  )
```

ここでは、テストが失敗するのを確認するため、わざと "bye" とマッチするかテストしている。

これが `test`:

```
# ファイルが作成されたかを確認
> assembly
\$ exists target/foo.jar

# hello って言うか確認
> check
```

`scripted` を走らせると、意図通りテストは失敗する:

```
[info] [error] {file:/private/var/folders/Ab/AbC1EFghIj4LMNOPqrStUV+++XX/-Tmp-/sbt_cdd1b3c4/simple/}default-0314bd/*:check: unexpected output: hello
[info] [error] Total time: 0 s, completed Sep 21, 2011 8:43:03 PM
[error] x sbt-assembly / simple
[error]    {line 6}  Command failed: check failed
[error] {file:/Users/foo/work/sbt-assembly/}default-373f46/*:scripted: sbt-assembly / simple failed
[error] Total time: 14 s, completed Sep 21, 2011 8:00:00 PM
```

### ステップ 7: テストをテストする

慣れるまでは、テスト自体がちゃんと振る舞うのに少し時間がかかるかもしれない。ここで使える便利なテクニックがいくつある。

まず最初に試すべきなのは、ログバッファリングを切ることだ。

```
> set scriptedBufferLog := false
```

これにより、例えばテンポラリディレクトリの場所などが分かるようになる:

```
[info] [info] Set current project to default-c6500b (in build file:/private/var/folders/Ab/AbC1EFghIj4LMNOPqrStUV+++XX/-Tmp-/sbt_8d950687/simple/project/plugins/)
...
```

テスト中にテンポラリディレクトリを見たいような状況があるかもしれない。`test` スクリプトに以下の一行を加えると、scripted はエンターキーを押すまで一時停止する:

```
\$ pause
```

もしうまくいかなくて、 `sbt/sbt-test/sbt-foo/simple` から直接 `sbt` を実行しようと思っているなら、それは止めたほうがいい。正しいやり方はディレクトリごと別の場所にコピーしてから走らせることだ。

### ステップ 8: インスパイアされる

sbt プロジェクト下には文字通り [100+ の scripted テストがある][sbtScriptedTests]。色々眺めてみて、インスパイアされよう。

例えば、以下に by-name と呼ばれるものを示す:

```
> compile

# change => Int to Function0
\$ copy-file changes/A.scala A.scala

# Both A.scala and B.scala need to be recompiled because the type has changed
-> compile
```

[xsbt-web-plugin][xsbt-web-plugin] や [sbt-assembly][sbt-assembly] にも scripted テストがある。

これでおしまい！プラグインをテストしてみた経験などを聞かせて下さい！

---
out: Running.html
---

  [Hello]: Hello.html
  [Setup]: Setup.html
  [Triggered-Execution]: ../docs/Triggered-Execution.html
  [Command-Line-Reference]: ../docs/Command-Line-Reference.html

実行
----

このページではプロジェクトをセットアップした後の `sbt` の使い方を説明する。
君が[sbt をインストール](../setup)して、[Hello, World](../hello)か他のプロジェクトを作ったことを前提にする。

### インタラクティブモード

プロジェクトのディレクトリで、sbt を引数なしで実行する:

```
\$ sbt
```

sbt をコマンドライン引数なしで実行するとインタラクティブモードが開始する。
インタラクティブモードにはコマンドプロンプト（とタブ補完と履歴も！）がある。

例えば、`compile` と sbt プロンプトに打ち込む:

```
> compile
```

もう一度 `compile` するには、上矢印を押して、エンターを押す。

君のプログラムを実行するには、`run` と打ち込む。

インタラクティブモードを終了するには、`exit` と打ち込むか、Ctrl+D (Unix) か Ctrl+Z (Windows) を用いる。

### バッチモード

sbt アクションを空白で区切られたリストとして引数に渡すことで、sbt をバッチモードで実行することができる。
引数を取る sbt コマンドに関しては、コマンドと引数の両方を引用符で囲むことで一つの引数として `sbt` に渡す。
例えば、

```
\$ sbt clean compile "testOnly TestA TestB"
```

この例では、`testOnly` は `TestA` と `TestB` の二つの引数を取る。
アクションは順に実行される（`clean`、`compile`、そして `testOnly`）。

### 継続的ビルドとテスト

編集-コンパイル-テストのサイクルを速めるために、ソースファイルを保存するたびに
sbt を使って自動的に再コンパイルすることができる。

ソースファイルが変更されたことを検知してアクションを実行するには、
アクションの先頭に `~` を書く。例えば、インタラクティブモードで、これを試してみよう:

```
> ~ compile
```

エンターを押すと、変更の監視を中止できる。

先頭の `~` はインタラクティブモードでもバッチモードでも使うことができる。

詳しくは、[Triggered Execution][Triggered-Execution] 参照。

### よく使われるコマンド

以下に、最もよく使われる sbt コマンドを紹介する。より完全な一覧は
[Command Line Reference][Command-Line-Reference] にある。

<table>
  <tr>
    <td><tt>clean</tt></td>
    <td>全ての生成されたファイル（<tt>target</tt> ディレクトリ）を削除する。</td>
  </tr>
  <tr>
    <td><tt>compile</tt></td>
    <td>メインのソース
    (<tt>src/main/scala</tt> と <tt>src/main/java</tt> ディレクトリにある
    をコンパイルする。</td>
  </tr>
  <tr>
    <td><tt>test</tt></td>
    <td>全てのテストをコンパイルし実行する。</td>
  </tr>
  <tr>
    <td><tt>console</tt></td>
    <td>コンパイル済みソースと依存ライブラリにクラスパスを通して、Scala インタプリタを開始する。
  sbt に戻るには、<tt>:quit</tt> と打ち込むか、Ctrl+D (Unix) か Ctrl+Z (Windows) を使う。</td>
  </tr>
  <tr>
    <td><nobr><tt>run &lt;argument&gt;*</tt></nobr></td>
    <td><tt>sbt</tt> と同じ仮想マシン上で、プロジェクトのメインクラスを実行する。</td>
  </tr>
  <tr>
    <td><tt>package</tt></td>
    <td><tt>src/main/resources</tt> 内のファイルと <tt>src/main/scala</tt> と
    <tt>src/main/java</tt>
    からコンパイルされたクラスを含む jar を作る。</td>
  </tr>
  <tr>
    <td><tt>help &lt;command&gt;</tt></td>
    <td>指定されたコマンドの詳しい説明を表示する。コマンドが指定されていない場合は、
  全てのコマンドの簡単な説明を表示する。</td>
  </tr>
  <tr>
    <td><tt>reload</tt></td>
    <td>ビルド定義（<tt>build.sbt</tt>、 <tt>project/*.scala</tt>、
    <tt>project/*.sbt</tt> ファイル）を再読み込みする。
  ビルド定義を変更した場合に必要。</td>
  </tr>
</table>

### タブ補完

インタラクティブモードには、空のプロンプトの状態を含め、タブ補完がある。
sbt の特殊な慣例として、タブを一度押すとよく使われる候補だけが表示され、
複数回押すと、より回りくどい候補が表示される。

### 履歴コマンド

インタラクティブモードは、たとえ sbt を終了して再起動した後でも履歴を覚えている。
履歴にアクセスする最も簡単な方法は矢印キーを使うことだ。以下のコマンドも使うことができる:

<table>
  <tr>
    <td><tt>!</tt></td>
    <td>履歴コマンドのヘルプを表示する。</td>
  </tr>
  <tr>
    <td><tt>!!</tt></td>
    <td>直前のコマンドを再実行する。</td>
  </tr>
  <tr>
    <td><tt>!:</tt></td>
    <td>全てのコマンド履歴を表示する。</td>
  </tr>  
  <tr>
    <td><tt>!:n</tt></td>
    <td>最後の <tt>n</tt> コマンドを表示する。</td>
  </tr>
  <tr>
    <td><tt>!n</tt></td>
    <td><tt>!:</tt> で表示されたインデックス <tt>n</tt> のコマンドを実行する。</td>
  </tr>
  <tr>
    <td><tt>!-n</tt></td>
    <td><tt>n</tt>個前のコマンドを実行する。</td>
  </tr>
  <tr>
    <td><tt>!string</tt></td>
    <td>'string' から始まる最近のコマンドを実行する。</td>
  </tr>
  <tr>
    <td><tt>!?string</tt></td>
    <td>'string' を含む最近のコマンドを実行する。</td>
  </tr>
</table>

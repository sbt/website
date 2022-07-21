---
out: IDE.html
---

  [metals]: https://scalameta.org/metals/
  [intellij]: https://www.jetbrains.com/idea/
  [lsp]: https://microsoft.github.io/language-server-protocol/
  [intellij-scala-plugin-2021-2]: https://blog.jetbrains.com/scala/2021/07/27/intellij-scala-plugin-2021-2/#Compiler-based_highlighting
  [vscode]: https://code.visualstudio.com/
  [neovim]: https://neovim.io/
  [bsp]: https://build-server-protocol.github.io/
  [vscode-debugging]: https://code.visualstudio.com/docs/editor/debugging
  [intellij-debugging]: https://www.jetbrains.com/help/idea/debugging-code.html
  [nvim-metals]: https://github.com/scalameta/nvim-metals
  [lsp.lua]: https://github.com/scalameta/nvim-metals/discussions/39#discussion-82302

IDE との統合
----------

エディタと sbt だけで Scala のコードを書くことも可能だが、今日日のプログラマの多くは統合開発環境 (IDE) を用いる。
Scala の IDE は [Metals][metals] と [IntelliJ IDEA][intellij] の二強で、それぞれ sbt ビルドとの統合をサポートする。

- [Metals のビルドサーバとして sbt を用いる](#metals)
- [IntelliJ IDEA へのインポート](#intellij-import)
- [IntelliJ IDEA のビルドサーバとして sbt を用いる](#intellij-bsp)
- [Metals フロントエンドとして Neovim を用いる](#nvim-metals)

<a id="metals"></a>
### Metals のビルドサーバとして sbt を用いる

[Metals][metals] は、Scala のためのオープンソースな**言語サーバ**であり、[VS Code][vscode] その他の
[LSP][lsp] をサポートするエディタのバックエンドとして機能することができる。
一方で Metals は、[Build Server Protocol][bsp] (BSP) 経由で sbt を含む異なる**ビルドサーバ**をサポートする。

VS Code で Metals を使うには:

1. Extensions タブから Metals をインストールする:<br>
   ![Metals](../files/metals0.png)
2. `build.sbt` ファイルを含むディレクトリを開く。
3. メニューバーより View > Command Palette... (macOS だと `Cmd-Shift-P`) を開き Metals: Switch build server と打ち込み、「sbt」を選択する。<br>
   ![Metals](../files/metals2.png)
4. インポート処理が完了したら、Scala のファイルを開いてみてコード補完が機能していることを確認する:<br>
   ![Metals](../files/metals3.png)

一部のサブプロジェクトを BSP へ入れたく無い場合は、以下のセッティングを使うことができる。

```scala
bspEnabled := false
```

コードに変更を加えて保存 (macOS だと `Cmd-S`) すると、Metals は sbt を呼び出して実際のビルド作業を行う。

Igal Tabachnik さんの [Using BSP effectively in IntelliJ and Scala](https://hmemcpy.com/2021/09/bsp-and-intellij/) という記事が参考になる。

#### VS Code でのインタラクティブ・デバッグ

1. コードにブレークポイントを設定することで、Metals はインタラクティブ・デバッグをサポートする:<br>
   ![Metals](../files/metals4.png)
2. 単体テストを右クリックして「Debug Test」を選ぶことでインタラクティブ・デバッグを開始する。
   テストがブレークポイントに当たると、変数の値を検査することができる。<br>
   ![Metals](../files/metals5.png)

インタラクティブ・デバッグが開始してからの操作方法の詳細は VS Code ドキュメンテーションの [Debugging][vscode-debugging] ページ参照。

#### sbt セッションへのログイン

Metals がビルドサーバとして sbt を使う間、シンクライアントを使って同じ sbt セッションにログインすることができる。

- Terminal セクションから `sbt --client` と打ち込む。<br>
  ![Metals](../files/metals6.png)

これで Metals が開始した sbt セッションにログインすることができた。その中でコードが既にコンパイルされた状態から `testOnly` その他のタスクを実行できる。

<a id="intellij-import"></a>
### IntelliJ IDEA へのインポート

[IntelliJ IDEA][intellij] は JetBrains社が開発した IDE で、Community Edition は Apache v2 ライセンスの元でオープンソース化されている。
IntelliJ は sbt を含む多くのビルドツールと統合して、プロジェクトをインポートすることができる。
これは従来の方法で、BSP よりも多くの場合安定性が高い。

IntelliJ IDEA にビルドをインポートするには:

1. Plugins タブから Scala プラグインをインストールする:<br>
   ![IntelliJ](../files/intellij1.png)
2. Projects から `build.sbt` ファイルを含んだディレクトリを開く:<br>
   ![IntelliJ](../files/intellij2.png)
3. インポート処理が完了したら、Scala のファイルを開いてみてコード補完が機能していることを確認する。

IntelliJ Scala プラグインは独自の軽量コンパイラエンジンを用いてエラーの検知を行うが、これは高速であるが正しくないこともある。[Compiler-based highlighting][intellij-scala-plugin-2021-2] といって、 IntelliJ を Scala コンパイラを使ってエラー・ハイライトを行うように設定することも可能だ。

#### IntelliJ IDEA でのインタラクティブ・デバッグ

1. コードにブレークポイントを設定することで、IntelliJ はインタラクティブ・デバッグをサポートする:<br>
   ![IntelliJ](../files/intellij4.png)
2. 単体テストを右クリックして「Debug &lt;テスト名&gt;」を選ぶことでインタラクティブ・デバッグを開始する。
   もしくは、単体テストの左側にある緑色の「実行」アイコンをクリックする。
   テストがブレークポイントに当たると、変数の値を検査することができる。<br>
   ![IntelliJ](../files/intellij5.png)

インタラクティブ・デバッグが開始してからの操作方法の詳細は IntelliJ ドキュメンテーションの [Debug code][intellij-debugging] ページ参照。

<a id="intellij-bsp"></a>
### IntelliJ IDEA のビルドサーバとして sbt を用いる (上級者向け)

IntelliJ へビルドをインポートするということは、事実上 IntelliJ をビルドツールやコンパイラとして採用してコードを書いているということだ ([compiler-based highlighting][intellij-scala-plugin-2021-2] も参照)。
多くのユーザはそのエキスペリエンスで満足しているが、一方でコードベースによってはコンパイラエラーが間違っていたり、ソース生成を行うプラグインと動作しなかったり、sbt と同一のビルド意味論を用いてコードを書きたいと思う人もいる。
幸いなことに、現代の IntelliJ は [Build Server Protocol][bsp] (BSP) 経由で sbt を含む異なる**ビルドサーバ**をサポートする。

IntelliJ において BSP を使う利点は、実際のビルド作業を sbt を用いて行うため、今までも sbt セッションを立ち上げながら IntelliJ を使っていた人は、二重でコンパイルしなくてもよくなるという利点がある。

<table class="table table-striped">
  <tr>
    <th><nobr></th>
    <th>IntelliJ へインポート</th>
    <th>BSP を使った IntelliJ</th>
  </tr>
  <tr>
    <td>信頼性</td>
    <td>✅ 安定した動作</td>
    <td>⚠️ 技術的に枯れていないため、UX 問題などにあう可能性がある</td>
  </tr>
  <tr>
    <td>応答性</td>
    <td>✅</td>
    <td>⚠️</td>
  </tr>
  <tr>
    <td>正確性</td>
    <td>⚠️ 独自のコンパイラを用いた型検査。scalac に設定することも可能。</td>
    <td>✅ Zinc + Scala コンパイラを用いた型検査</td>
  </tr>
  <tr>
    <td><nobr>ソース生成</nobr></td>
    <td>❌ ソース生成するごとに再同期が必要</td>
    <td>✅</td>
  </tr>
  <tr>
    <td>ビルド</td>
    <td>❌ sbt を併用すると二重ビルドが必要になる</td>
    <td>✅</td>
  </tr>
</table>

IntelliJ のビルドサーバとして sbt を用いるには:

1. Plugins タブから Scala プラグインをインストールする。
2. BSP を使うには、Project タブの Open ボタンは使ってはいけない:<br>
   ![IntelliJ](../files/intellij7.png)
3. メニューバーより New > "Project From Existing Sources" をクリックするか、Find Action (macOS だと `Cmd-Shift-P`) より「Existing」 と打ち込んで「Import Project From Existing Sources」を探す:<br>
   ![IntelliJ](../files/intellij8.png)
4. `build.sbt` ファイルを開く。ダイアログが表示されたら **BSP** を選択する:<br>
   ![IntelliJ](../files/intellij9.png)
5. 「tool to import the BSP workspace」として **sbt (recommended)** を選択する:<br>
   ![IntelliJ](../files/intellij10.png)
6. インポート処理が完了したら、Scala のファイルを開いてみてコード補完が機能していることを確認する:<br>
   ![IntelliJ](../files/intellij11.png)

一部のサブプロジェクトを BSP へ入れたく無い場合は、以下のセッティングを使うことができる。

```scala
bspEnabled := false
```

- Preferences より BSP と検索して、「build automatically on file save」を選択し、「export sbt projects to Bloop before import」を外す:<br>
  ![IntelliJ](../files/intellij12.png)

コードに変更を加えて保存 (macOS だと `Cmd-S`) すると、IntelliJ は sbt を呼び出して実際のビルド作業を行う。

#### sbt セッションへのログイン

シンクライアントを使って既存の sbt セッションにログインすることができる。

- Terminal セクションから `sbt --client` と打ち込む。<br>
  ![IntelliJ](../files/intellij6.png)

これで IntelliJ が開始した sbt セッションにログインすることができた。その中でコードが既にコンパイルされた状態から `testOnly` その他のタスクを実行できる。

<a id="nvim-metals"></a>
### Metals フロントエンドとして Neovim を用いる (上級者向け)

[Neovim][neovim] は、Vim エディタのモダンなフォークで、組み込みで [LSP][lsp] をサポートしていたりする。
そのため Neovim は Metals のフロントエンドとして設定可能だ。

Metals メンテナの一人である Chris Kipps さんが [nvim-metals][nvim-metals] というプラグインを作っており、これは Metals 機能を網羅的にサポートする。
nvim-metals をインストールするには、Chris Kipps さんの [lsp.lua][lsp.lua] を元に
`\$XDG_CONFIG_HOME/nvim/lua/` 以下に設定ファイルを書き、自分の好みに合わせていく。
例えば、vim-plug など別のプラグインマネージャを使っている場合はプラグインの部分をコメントアウトする必要がある。

`init.vim` から以下のようにして読み込める:

```
lua << END
require('lsp')
END
```

`lsp.lua` によると、`g:metals_status` はステータスラインに表示させるべきと書いてあり、これは lualine.nvim などを使って実現できる。

1. 次に、sbt を使ったビルドの Scala ファイルを Neovim を用いて開く。
2. プロンプトが表示されたら `:MetalsInstall` を実行する。
3. `:MetalsStartServer` を実行する。
4. ステータスラインの設定がうまくいっていれば、「Connecting to sbt」、「Indexing」などと表示される。<br>
   <img src="../files/nvim0.png" width="900">
5. Insert モードに入るとコード補完が作動し、タブを使って候補を見ていくことができる:<br>
   <img src="../files/nvim1.png" width="900">

- 変更を保存するとビルドが自動で行われ、コンパイルエラーがあった場合はコード中の余白に表示される:<br>
  <img src="../files/nvim2.png" width="900">

#### 定義へのジャンプ

1. カーソル下のシンボルの定義へは `gD` を使ってジャンプできる (具体的なキーバインドは好みのものにカスタマイズできる):<br>
   <img src="../files/nvim3.png" width="900">
2. `Ctrl-O` を使って古いバッファーに戻る。

#### ホバリング

- 「マウスオーバー」のようにカーソル下のシンボルの型情報を表示させるには、Normal モードで `K` を使う:<br>
   <img src="../files/nvim4.png" width="900">

#### エラーの列挙

1. 全てのコンパイラエラーと警告を列挙するには `<leader>aa` を使う:<br>
   <img src="../files/nvim5.png" width="900">
2. これは標準の quickfix リストを使っているので、`:cnext` や `:cprev` といったコマンドを使ってエラーや警告を見ていける。
3. エラーだけ見たい場合は、`<leader>ae` を使う。

#### Neovim でのインタラクティブ・デバッグ

1. nvim-dap のおかげで、Neovim はインタラクティブ・デバッグをサポートする。`<leader>dt` を用いてブレークポイントを設定していく:<br>
   <img src="../files/nvim6.png" width="900">
2. 単体テストを開き、ビルド済みかをホバリング (`K`) で確認して、debug continue (`<leader>dc`) でデバッガを開始する。
   プロンプトが表示されたら、「1: RunOrTest」を選ぶ。
3. テストがブレークポイントに当たると、debug hovering (`<leader>dK`) を使って変数の値を検査することができる:<br>
   <img src="../files/nvim7.png" width="900">
4. 再度 debug continue (`<leader>dc`) してセッションを終了させる。

詳細は [nvim-metals][nvim-metals] 参考。

#### sbt セッションへのログイン

シンクライアントを使って既存の sbt セッションにログインすることができる。

1. 新しい vim ウィンドウに `:terminal` と打ち込んで組み込みのターミナルを立ち上げる。
2. `sbt --client` と打ち込む<br>
   <img src="../files/nvim8.png" width="900">

Neovim の中だが、タブ補完なども普通に動作する。

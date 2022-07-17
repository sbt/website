---
out: IDE.html
---

  [metals]: https://scalameta.org/metals/
  [intellij]: https://www.jetbrains.com/idea/
  [lsp]: https://microsoft.github.io/language-server-protocol/
  [vscode]: https://code.visualstudio.com/
  [bsp]: https://build-server-protocol.github.io/
  [vscode-debugging]: https://code.visualstudio.com/docs/editor/debugging
  [intellij-debugging]: https://www.jetbrains.com/help/idea/debugging-code.html

IDE との統合
----------

エディタと sbt だけで Scala のコードを書くことも可能だが、今日日のプログラマの多くは統合開発環境 (IDE) を用いる。
Scala の IDE は [Metals][metals] と [IntelliJ IDEA][intellij] の二強で、それぞれ sbt ビルドとの統合をサポートする。

- [Metals のビルドサーバとして sbt を用いる](#metals)
- [IntelliJ IDEA へのインポート](#intellij)

<a id="metals"></a>
### Metals のビルドサーバとして sbt を用いる

[Metals][metals] は、Scala のためのオープンソースな**言語サーバ**であり、[VS Code][vscode] その他の
[LSP][lsp] をサポートするエディタのバックエンドとして機能することができる。
一方で Metals は、[Build Server Protocol][bsp] (BSP) 経由で sbt を含む異なる**ビルドサーバ**をサポートする。

VS Code で Metals を使うには:

1. Extensions タブから Metals をインストールする:
   ![Metals](../files/metals0.png)
2. `build.sbt` ファイルを含むディレクトリを開く。
3. View > Command Palette... (macOS だと `Cmd-Shift-P`) に Metals: Switch build server と打ち込み、「sbt」を選択する。
   ![Metals](../files/metals2.png)
4. インポート処理が完了したら、Scala のファイルを開いてみてコード補完が機能していることを確認する:
   ![Metals](../files/metals3.png)

一部のサブプロジェクトを BSP へ入れたく無い場合は、以下のセッティングを使うことができる。

```scala
bspEnabled := false
```

コードに変更を加えて保存 (macOS だと `Cmd-S`) すると、Metals は sbt を呼び出して実際のビルド作業を行う。

#### VS Code でのインタラクティブ・デバッグ

1. コードにブレークポイントを設定することで、Metals はインタラクティブ・デバッグをサポートする:
   ![Metals](../files/metals4.png)
2. 単体テストを右クリックして「Debug Test」を選ぶことでインタラクティブ・デバッグを開始する。
   テストがブレークポイントに当たると、変数の値を検査することができる。
   ![Metals](../files/metals5.png)

インタラクティブ・デバッグが開始してからの操作方法の詳細は VS Code ドキュメンテーションの [Debugging][vscode-debugging] ページ参照。

#### sbt セッションへのログイン

Metals がビルドサーバとして sbt を使う間、シンクライアントを使って同じ sbt セッションにログインすることができる。

- Terminal セクションから `sbt --client` と打ち込む。
  ![Metals](../files/metals6.png)

これで Metals が開始した sbt セッションにログインすることができた。その中でコードが既にコンパイルされた状態から `testOnly` その他のタスクを実行できる。

<a id="intellij"></a>
### IntelliJ IDEA へのインポート

[IntelliJ IDEA][intellij] は JetBrains社が開発した IDE で、Community Edition は Apache v2 ライセンスの元でオープンソース化されている。
IntelliJ は sbt を含む多くのビルドツールと統合して、プロジェクトをインポートすることができる。

IntelliJ IDEA にビルドをインポートするには:

1. Plugins タブから Scala プラグインをインストールする:
   ![IntelliJ](../files/intellij1.png)
2. Projects から `build.sbt` ファイルを含んだディレクトリを開く:
   ![IntelliJ](../files/intellij2.png)
3. インポート処理が完了したら、Scala のファイルを開いてみてコード補完が機能していることを確認する:
   ![IntelliJ](../files/intellij3.png)

#### IntelliJ IDEA でのインタラクティブ・デバッグ

1. コードにブレークポイントを設定することで、IntelliJ はインタラクティブ・デバッグをサポートする:
   ![IntelliJ](../files/intellij4.png)
2. 単体テストを右クリックして「Debug &lt;テスト名&gt;」を選ぶことでインタラクティブ・デバッグを開始する。
   テストがブレークポイントに当たると、変数の値を検査することができる。
   ![IntelliJ](../files/intellij5.png)

インタラクティブ・デバッグが開始してからの操作方法の詳細は IntelliJ ドキュメンテーションの [Debug code][intellij-debugging] ページ参照。

#### sbt セッションへのログイン

シンクライアントを使って既存の sbt セッションにログインすることができる。

- Terminal セクションから `sbt --client` と打ち込む。
  ![IntelliJ](../files/intellij6.png)

これで IntelliJ が開始した sbt セッションにログインすることができた。その中で `testOnly` その他のタスクを実行できる。

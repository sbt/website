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
- [IntelliJ IDEA のビルドサーバとして sbt を用いる](#intellij-bsp)
- [IntelliJ IDEA へのインポート](#intellij-import)

<a id="metals"></a>
### Metals のビルドサーバとして sbt を用いる

[Metals][metals] は、Scala のためのオープンソースな**言語サーバ**であり、[VS Code][vscode] その他の
[LSP][lsp] をサポートするエディタのバックエンドとして機能することができる。
一方で Metals は、[Build Server Protocol][bsp] (BSP) 経由で sbt を含む異なる**ビルドサーバ**をサポートする。

VS Code で Metals を使うには:

1. Extensions タブから Metals をインストールする:
   ![Metals](../files/metals0.png)
2. `build.sbt` ファイルを含むディレクトリを開く。
3. メニューバーより View > Command Palette... (macOS だと `Cmd-Shift-P`) を開き Metals: Switch build server と打ち込み、「sbt」を選択する。
   ![Metals](../files/metals2.png)
4. インポート処理が完了したら、Scala のファイルを開いてみてコード補完が機能していることを確認する:
   ![Metals](../files/metals3.png)

一部のサブプロジェクトを BSP へ入れたく無い場合は、以下のセッティングを使うことができる。

```scala
bspEnabled := false
```

コードに変更を加えて保存 (macOS だと `Cmd-S`) すると、Metals は sbt を呼び出して実際のビルド作業を行う。

Igal Tabachnik さんの [Using BSP effectively in IntelliJ and Scala](https://hmemcpy.com/2021/09/bsp-and-intellij/) という記事が参考になる。

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

<a id="intellij-bsp"></a>
### IntelliJ IDEA のビルドサーバとして sbt を用いる

[IntelliJ IDEA][intellij] は JetBrains社が開発した IDE で、Community Edition は Apache v2 ライセンスの元でオープンソース化されている。
独自のコンパイラエンジンの他に IntelliJ は [Build Server Protocol][bsp] (BSP) 経由で sbt を含む異なる**ビルドサーバ**をサポートする。

IntelliJ のビルドサーバとして sbt を用いるには:

1. Plugins タブから Scala プラグインをインストールする:
   ![IntelliJ](../files/intellij1.png)
2. BSP を使うには、Project タブの Open ボタンは使ってはいけない:
   ![IntelliJ](../files/intellij7.png)
3. メニューバーより New > "Project From Existing Sources" をクリックするか、Find Action (macOS だと `Cmd-Shift-P`) より「Existing」 と打ち込んで「Import Project From Existing Sources」を探す:
   ![IntelliJ](../files/intellij8.png)
4. `build.sbt` ファイルを開く。ダイアログが表示されたら **BSP** を選択する:
   ![IntelliJ](../files/intellij9.png)
5. 「tool to import the BSP workspace」として **sbt (recommended)** を選択する:
   ![IntelliJ](../files/intellij10.png)
6. インポート処理が完了したら、Scala のファイルを開いてみてコード補完が機能していることを確認する:
   ![IntelliJ](../files/intellij11.png)

一部のサブプロジェクトを BSP へ入れたく無い場合は、以下のセッティングを使うことができる。

```scala
bspEnabled := false
```

- Preferences より BSP と検索して、「build automatically on file save」を選択し、「export sbt projects to Bloop before import」を外す:
  ![IntelliJ](../files/intellij12.png)

コードに変更を加えて保存 (macOS だと `Cmd-S`) すると、IntelliJ は sbt を呼び出して実際のビルド作業を行う。

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

これで IntelliJ が開始した sbt セッションにログインすることができた。その中でコードが既にコンパイルされた状態から `testOnly` その他のタスクを実行できる。

<a id="intellij-import"></a>
### IntelliJ IDEA へのインポート

IntelliJ は sbt を含む多くのビルドツールと統合して、プロジェクトをインポートすることができる。
これは従来の方法で、この方法が安定している場合もある。

IntelliJ IDEA にビルドをインポートするには:

1. Plugins タブから Scala プラグインをインストールする:
2. Projects から `build.sbt` ファイルを含んだディレクトリを開く:
   ![IntelliJ](../files/intellij2.png)
3. インポート処理が完了したら、Scala のファイルを開いてみてコード補完が機能していることを確認する。

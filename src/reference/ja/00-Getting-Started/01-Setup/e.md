---
out: Activator-Installation.html
---

  [Manual-Installation]: Manual-Installation.html

Typesafe Activator (sbt を含む) のインストール 
-------------------------------------------

Typesafe Activator は `activator ui` と `activator new` という
2つのコマンドを追加するカスタム版の sbt だ。
つまり、`activator` は sbt のスーパーセットであると言える。

Activator は [typesafe.com](http://typesafe.com/get-started) で入手できる。

このガイドで `sbt ~test` というようなコマンドラインがあれば、
`activator ~test` と打ち込めばそのまま動作するはずだ。
Activator の「中の人」は sbt なので、全ての
Activator プロジェクトは sbt で開くことができ、そのまた逆も成り立つ。

Activator をダウンロードすると `activator` スクリプトと
`activator-launch.jar` が含まれている。これは[手動インストール][Manual-Installation]で
解説されている sbt スクリプトと sbt launcher JAR に相当する。
以下が sbt の[手動インストール][Manual-Installation]との違いだ:

- 引数なしで `activator` と入力すると `activator shell` モードか
  `activator ui` モードに入るかを推論する。
  コマンドラインプロンプトを強制したい場合は `activator shell` と入力する。
- `activator new` を使うことで豊富な
　[テンプレートのカタログ](https://typesafe.com/activator/templates)を元にプロジェクトを新規作成することができる。
  例えば、`play-scala` テンプレートを使うと Scala の [Play Framework](https://playframework.com) アプリを作れる。
- `activator ui` は、クイックスタート UI を起動する。
  これを使ってテンプレート付属のチュートリアルを読みながら作業できる
  (カタログ内の多くのテンプレートにはチュートリアルが付属している)。

Activator には、起動スクリプトと起動JAR のみのミニマル版ダウンロードと、
Scala、Akka、そして Play Framework の JAR がすぐに使えるよう
Ivy リポジトリを含む完全版ダウンロードの 2 種類がある。

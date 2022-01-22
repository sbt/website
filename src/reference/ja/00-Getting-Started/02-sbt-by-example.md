---
out: sbt-by-example.html
---

  [Basic-Def]: Basic-Def.html
  [Setup]: Setup.html
  [Running]: Running.html
  [Essential-sbt]: https://www.scalawilliam.com/essential-sbt/

例題でみる sbt
-------------

このページは、
[sbt 1 をインストール][Setup]したことを前提とする。

sbt の内部がどうなっているかや理由みたいなことを解説する代わりに、例題を次々と見ていこう。

### 最小 sbt ビルドを作る

```
\$ mkdir foo-build
\$ cd foo-build
\$ touch build.sbt
```

### sbt シェルを起ち上げる

```
\$ sbt
[info] Updated file /tmp/foo-build/project/build.properties: set sbt.version to 1.1.4
[info] Loading project definition from /private/tmp/foo-build/project
[info] Loading settings from build.sbt ...
[info] Set current project to foo-build (in build file:/private/tmp/foo-build/)
[info] sbt server started at local:///Users/eed3si9n/.sbt/1.0/server/abc4fb6c89985a00fd95/sock
sbt:foo-build>
```

### sbt シェルを終了させる

sbt シェルを終了させるには、`exit` と入力するか、Ctrl+D (Unix) か Ctrl+Z (Windows) を押す。

```
sbt:foo-build> exit
```

### プロジェクトをコンパイルする

表記の慣例として `sbt:...>` や `>` というプロンプトは、sbt シェルに入っていることを意味することにする。

```
\$ sbt
sbt:foo-build> compile
```

### コード変更時に再コンパイルする

`compile` コマンド (やその他のコマンド) を `~` で始めると、プロジェクト内のソース・ファイルが変更されるたびにそのコマンドが自動的に再実行される。

```
sbt:foo-build> ~compile
[success] Total time: 0 s, completed May 6, 2018 3:52:08 PM
1. Waiting for source changes... (press enter to interrupt)
```

### ソース・ファイルを書く

上記のコマンドは走らせたままにする。別のシェルかファイルマネージャーからプロジェクトのディレクトリへ行って、`src/main/scala/example` というディレクトリを作る。次に好きなエディタを使って `example` ディレクトリ内に以下のファイルを作成する:

```scala
package example

object Hello {
  def main(args: Array[String]): Unit = {
    println("Hello")
  }
}
```

この新しいファイルは実行中のコマンドが自動的に検知したはずだ:

```
[info] Compiling 1 Scala source to /private/tmp/foo-build/target/scala-2.12/classes ...
[info] Done compiling.
[success] Total time: 2 s, completed May 6, 2018 3:53:42 PM
2. Waiting for source changes... (press enter to interrupt)
```

`~compile` を抜けるには `Enter` を押す。

### 以前のコマンドを実行する

sbt シェル内で上矢印キーを 2回押して、上で実行した `compile` コマンドを探す。

```
sbt:foo-build> compile
```

### ヘルプを読む

`help` コマンドを使って、基礎コマンドの一覧を表示する。

```
sbt:foo-build> help

  about                                          Displays basic information about sbt and the build.
  tasks                                          Lists the tasks defined for the current project.
  settings                                       Lists the settings defined for the current project.
  reload                                         (Re)loads the current project or changes to plugins project or returns from it.
  new                                            Creates a new sbt build.
  projects                                       Lists the names of available projects or temporarily adds/removes extra builds to the session.
  project                                        Displays the current project or changes to the provided `project`.

....
```

特定のタスクの説明を表示させる:

```
sbt:foo-build> help run
Runs a main class, passing along arguments provided on the command line.
```

### アプリを実行する

```
sbt:foo-build> run
[info] Packaging /private/tmp/foo-build/target/scala-2.12/foo-build_2.12-0.1.0-SNAPSHOT.jar ...
[info] Done packaging.
[info] Running example.Hello
Hello
[success] Total time: 1 s, completed May 6, 2018 4:10:44 PM
```

### sbt シェルから ThisBuild / scalaVersion をセットする

```
sbt:foo-build> set ThisBuild / scalaVersion := "$example_scala213$"
[info] Defining ThisBuild / scalaVersion
```

`scalaVersion` セッティングを確認する:

```
sbt:foo-build> scalaVersion
[info] $example_scala213$
```

### セッションを build.sbt へと保存する

アドホックに設定したセッティングは `session save` で保存できる。

```
sbt:foo-build> session save
[info] Reapplying settings...
```

`build.sbt` ファイルは以下のようになったはずだ:

```scala
ThisBuild / scalaVersion := "$example_scala213$"
```

### プロジェクトに名前を付ける

エディタを使って、`build.sbt` を以下のように変更する:

@@snip [name]($root$/src/sbt-test/ref/example-name/build.sbt) {}

### ビルドの再読み込み

`reload` コマンドを使ってビルドを再読み込みする。このコマンドは `build.sbt` を読み直して、そこに書かれたセッティングを再適用する。

```
sbt:foo-build> reload
[info] Loading project definition from /private/tmp/foo-build/project
[info] Loading settings from build.sbt ...
[info] Set current project to Hello (in build file:/private/tmp/foo-build/)
sbt:Hello>
```

プロンプトが `sbt:Hello>` に変わったことに注目してほしい。

### libraryDependencies に ScalaTest を追加する

エディタを使って、`build.sbt` を以下のように変更する:

@@snip [scalatest]($root$/src/sbt-test/ref/example-scalatest/build.sbt) {}

`reload` コマンドを使って、`build.sbt` の変更を反映させる。

```
sbt:Hello> reload
```

### テストを実行する

```
sbt:Hello> test
```

### 差分テストを継続的に実行する

```
sbt:Hello> ~testQuick
```

### テストを書く

上のコマンドを走らせたままで、エディタから `src/test/scala/HelloSpec.scala` という名前のファイルを作成する:

@@snip [scalatest]($root$/src/sbt-test/ref/example-scalatest/src/test/scala/HelloSpec.scala) {}

`~testQuick` が検知したはずだ:

```
2. Waiting for source changes... (press enter to interrupt)
[info] Compiling 1 Scala source to /private/tmp/foo-build/target/scala-2.12/test-classes ...
[info] Done compiling.
[info] HelloSpec:
[info] - Hello should start with H *** FAILED ***
[info]   assert("hello".startsWith("H"))
[info]          |       |          |
[info]          "hello" false      "H" (HelloSpec.scala:5)
[info] Run completed in 135 milliseconds.
[info] Total number of tests run: 1
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 0, failed 1, canceled 0, ignored 0, pending 0
[info] *** 1 TEST FAILED ***
[error] Failed tests:
[error]   HelloSpec
[error] (Test / testQuick) sbt.TestsFailedException: Tests unsuccessful
```

### テストが通るようにする

エディタを使って `src/test/scala/HelloSpec.scala` を以下のように変更する:

@@snip [scalatest]($root$/src/sbt-test/ref/example-scalatest/changes/HelloSpec.scala) {}

テストが通過したことを確認して、`Enter` を押して継続的テストを抜ける。

### ライブラリ依存性を追加する

エディタを使って `build.sbt` を以下のように変更する:

@@snip [example-library]($root$/src/sbt-test/ref/example-library/build.sbt) {}

### Scala REPL を使う

New York の現在の天気を調べてみる:

```scala
sbt:Hello> console
[info] Starting scala interpreter...
Welcome to Scala 2.12.7 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_171).
Type in expressions for evaluation. Or try :help.

scala> :paste
// Entering paste mode (ctrl-D to finish)

import scala.concurrent._, duration._
import gigahorse._, support.okhttp.Gigahorse
import play.api.libs.json._

Gigahorse.withHttp(Gigahorse.config) { http =>
  val baseUrl = "https://www.metaweather.com/api/location"
  val rLoc = Gigahorse.url(baseUrl + "/search/").get.
    addQueryString("query" -> "New York")
  val fLoc = http.run(rLoc, Gigahorse.asString)
  val loc = Await.result(fLoc, 10.seconds)
  val woeid = (Json.parse(loc) \\ 0 \\ "woeid").get
  val rWeather = Gigahorse.url(baseUrl + s"/\$woeid/").get
  val fWeather = http.run(rWeather, Gigahorse.asString)
  val weather = Await.result(fWeather, 10.seconds)
  ({Json.parse(_: String)} andThen Json.prettyPrint)(weather)
}

// Ctrl+D を押してペーストモードを抜ける

// Exiting paste mode, now interpreting.

import scala.concurrent._
import duration._
import gigahorse._
import support.okhttp.Gigahorse
import play.api.libs.json._
res0: String =
{
  "consolidated_weather" : [ {
    "id" : 5325278131781632,
    "weather_state_name" : "Light Rain",
    "weather_state_abbr" : "lr",
    "wind_direction_compass" : "W",
    "created" : "2019-11-23T09:16:43.892336Z",
    "applicable_date" : "2019-11-23",
    "min_temp" : 0.36,
    "max_temp" : 8.375,
    "the_temp" : 3.98,
    "wind_speed" : 4.813710565158143,
    "wind_direction" : 266.48254020294627,
    "air_pressure" : 1017,
    "humidity" : 58,
    "visibility" : 15.37583015191283,
    "predictability" : 75
  }, {
    "id" : 6428406054912000,
    "weather_state_name" : "Heavy Rain",
    "weather_state_abbr" : "hr",
  ...

scala> :q // これで REPL を抜ける
```

### サブプロジェクトを作成する

`build.sbt` を以下のように変更する:

@@snip [example-sub1]($root$/src/sbt-test/ref/example-sub1/build.sbt) {}

`reload` コマンドを使って `build.sbt` の変更を反映させる。

### 全てのサブプロジェクトを列挙する

```
sbt:Hello> projects
[info] In file:/private/tmp/foo-build/
[info]   * hello
[info]     helloCore
```

### サブプロジェクトをコンパイルする

```
sbt:Hello> helloCore/compile
```

### サブプロジェクトに ScalaTest を追加する

`build.sbt` を以下のように変更する:

@@snip [example-sub2]($root$/src/sbt-test/ref/example-sub2/build.sbt) {}

### コマンドをブロードキャストする

`hello` に送ったコマンドを `helloCore` にもブロードキャストするために集約を設定する:

@@snip [example-sub3]($root$/src/sbt-test/ref/example-sub3/build.sbt) {}

`reload` 後、`~testQuick` は両方のサブプロジェクトに作用する:

```scala
sbt:Hello> ~testQuick
```

`Enter` を押して継続的テストを抜ける。

### hello が helloCore に依存するようにする

サブプロジェクト間の依存関係を定義するには `.dependsOn(...)` を使う。ついでに、Gigahorse への依存性も `helloCore` に移そう。

@@snip [example-sub4]($root$/src/sbt-test/ref/example-sub4/build.sbt) {}

### Play JSON を使って JSON をパースする

`helloCore` に Play JSON を追加しよう。

@@snip [example-weather-build]($root$/src/sbt-test/ref/example-weather/build.sbt) {}

`reload` 後、`core/src/main/scala/example/core/Weather.scala` を追加する:

```scala
package example.core

import gigahorse._, support.okhttp.Gigahorse
import scala.concurrent._, duration._
import play.api.libs.json._

object Weather {
  lazy val http = Gigahorse.http(Gigahorse.config)

  def weather: Future[String] = {
    val baseUrl = "https://www.metaweather.com/api/location"
    val locUrl = baseUrl + "/search/"
    val weatherUrl = baseUrl + "/%s/"
    val rLoc = Gigahorse.url(locUrl).get.
      addQueryString("query" -> "New York")
    import ExecutionContext.Implicits.global
    for {
      loc <- http.run(rLoc, parse)
      woeid = (loc \\ 0 \\ "woeid").get
      rWeather = Gigahorse.url(weatherUrl format woeid).get
      weather <- http.run(rWeather, parse)
    } yield (weather \\\\ "weather_state_name")(0).as[String].toLowerCase
  }

  private def parse = Gigahorse.asString andThen Json.parse
}
```

次に `src/main/scala/example/Hello.scala` を以下のように変更する:

```scala
package example

import scala.concurrent._, duration._
import core.Weather

object Hello {
  def main(args: Array[String]): Unit = {
    val w = Await.result(Weather.weather, 10.seconds)
    println(s"Hello! The weather in New York is \$w.")
    Weather.http.close()
  }
}
```

アプリを走らせてみて、うまくいったか確認する:

```
sbt:Hello> run
[info] Compiling 1 Scala source to /private/tmp/foo-build/core/target/scala-2.12/classes ...
[info] Done compiling.
[info] Compiling 1 Scala source to /private/tmp/foo-build/target/scala-2.12/classes ...
[info] Packaging /private/tmp/foo-build/core/target/scala-2.12/hello-core_2.12-0.1.0-SNAPSHOT.jar ...
[info] Done packaging.
[info] Done compiling.
[info] Packaging /private/tmp/foo-build/target/scala-2.12/hello_2.12-0.1.0-SNAPSHOT.jar ...
[info] Done packaging.
[info] Running example.Hello
Hello! The weather in New York is mostly cloudy.
```

### sbt-native-packger プラグインを追加する

エディタを使って `project/plugins.sbt` を追加する:

@@snip [example-weather-plugins]($root$/src/sbt-test/ref/example-weather/changes/plugins.sbt) {}

次に `build.sbt` を以下のように変更して `JavaAppPackaging` を追加する:

@@snip [example-weather-build2]($root$/src/sbt-test/ref/example-weather/changes/build.sbt) {}

### 配布用の .zip ファイルを作る

```
sbt:Hello> dist
[info] Wrote /private/tmp/foo-build/target/scala-2.12/hello_2.12-0.1.0-SNAPSHOT.pom
[info] Wrote /private/tmp/foo-build/core/target/scala-2.12/hello-core_2.12-0.1.0-SNAPSHOT.pom
[info] Your package is ready in /private/tmp/foo-build/target/universal/hello-0.1.0-SNAPSHOT.zip
```

パッケージ化されたアプリの実行は以下のように行う:

```
\$ /tmp/someother
\$ cd /tmp/someother
\$ unzip -o -d /tmp/someother /tmp/foo-build/target/universal/hello-0.1.0-SNAPSHOT.zip
\$ ./hello-0.1.0-SNAPSHOT/bin/hello
Hello! The weather in New York is mostly cloudy.
```

### アプリを Docker化させる

```
sbt:Hello> Docker/publishLocal
....
[info] Successfully built b6ce1b6ab2c0
[info] Successfully tagged hello:0.1.0-SNAPSHOT
[info] Built image hello:0.1.0-SNAPSHOT
```

Docker化されたアプリは以下のように実行する:

```
\$ docker run hello:0.1.0-SNAPSHOT
Hello! The weather in New York is mostly cloudy
```

### version を設定する

`build.sbt` を以下のように変更する:

@@snip [example-weather-build3]($root$/src/sbt-test/ref/example-weather/changes/build3.sbt) {}

### Switch scalaVersion temporarily

```
sbt:Hello> ++2.12.14!
[info] Forcing Scala version to 2.12.14 on all projects.
[info] Reapplying settings...
[info] Set current project to Hello (in build file:/private/tmp/foo-build/)
```

`scalaVersion` セッティングを確認する:

```
sbt:Hello> scalaVersion
[info] helloCore / scalaVersion
[info]  2.12.14
[info] scalaVersion
[info]  2.12.14
```

このセッティングは `reload` 後には無くなる。

### dist タスクのインスペクト

`dist` タスクのことをもっと調べるために、`help` と `inspect` を実行してみる。

```scala
sbt:Hello> help dist
Creates the distribution packages.
sbt:Hello> inspect dist
```

依存タスクに対して `inspect` を再帰的に呼び出すには `inspect tree` を使う。

```scala
sbt:Hello> inspect tree dist
[info] dist = Task[java.io.File]
[info]   +-Universal / dist = Task[java.io.File]
....
```

### バッチモード

sbt のコマンドをターミナルから直接渡して sbt をバッチモードで実行することができる。

```
\$ sbt clean "testOnly HelloSpec"
```

**Note**: バッチモードでの実行は JVM のスピンアップと JIT を毎回行うため、**ビルドかなり遅くなる。**
普段のコーディングでは sbt シェル、
もしくは `~testQuick` のような継続的テストを使うことを推奨する。

### sbt new コマンド

sbt `new` コマンドを使って手早く簡単な Hello world ビルドをセットアップすることができる。

```
\$ sbt new sbt/scala-seed.g8
....
A minimal Scala project.

name [My Something Project]: hello

Template applied in ./hello
```

プロジェクト名を入力するプロンプトが出てきたら `hello` と入力する。

これで、`hello` ディレクトリ以下に新しいプロジェクトができた。

### クレジット

本ページは William "Scala William" Narmontas さん作の [Essential sbt][essential-sbt] というチュートリアルに基づいて書かれた。

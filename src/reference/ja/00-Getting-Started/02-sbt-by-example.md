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
[info] Updated file /tmp/foo-build/project/build.properties: set sbt.version to 1.9.3
[info] welcome to sbt 1.9.3 (Eclipse Adoptium Java 17.0.8)
[info] Loading project definition from /tmp/foo-build/project
[info] loading settings for project foo-build from build.sbt ...
[info] Set current project to foo-build (in build file:/tmp/foo-build/)
[info] sbt server started at local:///Users/eed3si9n/.sbt/1.0/server/abc4fb6c89985a00fd95/sock
[info] started sbt server
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
[success] Total time: 0 s, completed 28 Jul 2023, 13:32:35
[info] 1. Monitoring source files for foo-build/compile...
[info]    Press <enter> to interrupt or '?' for more options.
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
[info] Build triggered by /tmp/foo-build/src/main/scala/example/Hello.scala. Running 'compile'.
[info] compiling 1 Scala source to /tmp/foo-build/target/scala-2.12/classes ...
[success] Total time: 0 s, completed 28 Jul 2023, 13:38:55
[info] 2. Monitoring source files for foo-build/compile...
[info]    Press <enter> to interrupt or '?' for more options.
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

<command> (; <command>)*                       Runs the provided semicolon-separated commands.
about                                          Displays basic information about sbt and the build.
tasks                                          Lists the tasks defined for the current project.
settings                                       Lists the settings defined for the current project.
reload                                         (Re)loads the current project or changes to plugins project or returns from it.
new                                            Creates a new sbt build.
new                                            Creates a new sbt build.
projects                                       Lists the names of available projects or temporarily adds/removes extra builds to the session.

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
[info] running example.Hello
Hello
[success] Total time: 0 s, completed 28 Jul 2023, 13:40:31
```

### sbt シェルから ThisBuild / scalaVersion をセットする

```
sbt:foo-build> set ThisBuild / scalaVersion := "$example_scala213$"
[info] Defining ThisBuild / scalaVersion
[info] The new value will be used by Compile / bspBuildTarget, Compile / dependencyTreeCrossProjectId and 50 others.
[info]  Run `last` for details.
[info] Reapplying settings...
[info] set current project to foo-build (in build file:/tmp/foo-build/)
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
[info] set current project to foo-build (in build file:/tmp/foo-build/)
[warn] build source files have changed
[warn] modified files:
[warn]   /tmp/foo-build/build.sbt
[warn] Apply these changes by running `reload`.
[warn] Automatically reload the build when source changes are detected by setting `Global / onChangedBuildSource := ReloadOnSourceChanges`.
[warn] Disable this warning by setting `Global / onChangedBuildSource := IgnoreSourceChanges`.
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
[info] welcome to sbt 1.9.3 (Eclipse Adoptium Java 17.0.8)
[info] loading project definition from /tmp/foo-build/project
[info] loading settings for project hello from build.sbt ...
[info] set current project to Hello (in build file:/tmp/foo-build/)
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

上のコマンドを走らせたままで、エディタから `src/test/scala/example/HelloSpec.scala` という名前のファイルを作成する:

@@snip [scalatest]($root$/src/sbt-test/ref/example-scalatest/src/test/scala/example/HelloSpec.scala) {}

`~testQuick` が検知したはずだ:

```
[info] 2. Monitoring source files for hello/testQuick...
[info]    Press <enter> to interrupt or '?' for more options.
[info] Build triggered by /private/tmp/foo-build/src/test/scala/HelloSpec.scala. Running 'testQuick'.
[info] compiling 1 Scala source to /private/tmp/foo-build/target/scala-2.13/test-classes ...
[info] HelloSpec:
[info] - Hello should start with H *** FAILED ***
[info]   "hello" did not start with "H" (HelloSpec.scala:5)
[info] Run completed in 44 milliseconds.
[info] Total number of tests run: 1
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 0, failed 1, canceled 0, ignored 0, pending 0
[info] *** 1 TEST FAILED ***
[error] Failed tests:
[error]         HelloSpec
[error] (Test / testQuick) sbt.TestsFailedException: Tests unsuccessfull
```

### テストが通るようにする

エディタを使って `src/test/scala/example/HelloSpec.scala` を以下のように変更する:

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
Welcome to Scala 2.13.11 (OpenJDK 64-Bit Server VM, Java 17).
Type in expressions for evaluation. Or try :help.

scala> :paste
// Entering paste mode (ctrl-D to finish)

import sttp.client4.quick._
import sttp.client4.Response

val newYorkLatitude: Double = 40.7143
val newYorkLongitude: Double = -74.006
val response: Response[String] = quickRequest
.get(
    uri"https://api.open-meteo.com/v1/forecast?latitude=\$newYorkLatitude&longitude=\$newYorkLongitude&current_weather=true"
)
.send()

println(ujson.read(response.body).render(indent = 2))

// press Ctrl+D

// Exiting paste mode, now interpreting.

{
    "latitude": 40.710335,
    "longitude": -73.99307,
    "generationtime_ms": 0.36704540252685547,
    "utc_offset_seconds": 0,
    "timezone": "GMT",
    "timezone_abbreviation": "GMT",
    "elevation": 51,
    "current_weather": {
        "temperature": 21.3,
        "windspeed": 16.7,
        "winddirection": 205,
        "weathercode": 3,
        "is_day": 1,
        "time": "2023-08-04T10:00"
    }
}
import sttp.client4.quick._
import sttp.client4.Response
val newYorkLatitude: Double = 40.7143
val newYorkLongitude: Double = -74.006
val response: sttp.client4.Response[String] = Response({"latitude":40.710335,"longitude":-73.99307,"generationtime_ms":0.36704540252685547,"utc_offset_seconds":0,"timezone":"GMT","timezone_abbreviation":"GMT","elevation":51.0,"current_weather":{"temperature":21.3,"windspeed":16.7,"winddirection":205.0,"weathercode":3,"is_day":1,"time":"2023-08-04T10:00"}},200,,List(:status: 200, content-encoding: deflate, content-type: application/json; charset=utf-8, date: Fri, 04 Aug 2023 10:09:11 GMT),List(),RequestMetadata(GET,https://api.open-meteo.com/v1/forecast?latitude=40.7143&longitude...

scala> :q // to quit
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

### uJson を使って JSON をパースする

`helloCore` に uJson を追加しよう。

@@snip [example-weather-build]($root$/src/sbt-test/ref/example-weather/build.sbt) {}

`reload` 後、`core/src/main/scala/example/core/Weather.scala` を追加する:

```scala

package example.core

import sttp.client4.quick._
import sttp.client4.Response

object Weather {
    def weather() = {
        val response: Response[String] = quickRequest
            .get(
                uri"https://api.open-meteo.com/v1/forecast?latitude=\$newYorkLatitude&longitude=\$newYorkLongitude&current_weather=true"
            )
            .send()
        val json = ujson.read(response.body)
        json.obj("current_weather")("temperature").num
    }

    private val newYorkLatitude: Double = 40.7143
    private val newYorkLongitude: Double = -74.006
}
```

次に `src/main/scala/example/Hello.scala` を以下のように変更する:

```scala
package example

import example.core.Weather

object Hello {
    def main(args: Array[String]): Unit = {
        val temp = Weather.weather()
        println(s"Hello! The current temperature in New York is \$temp C.")
    }
}
```

アプリを走らせてみて、うまくいったか確認する:

```
sbt:Hello> run
[info] compiling 1 Scala source to /tmp/foo-build/core/target/scala-2.13/classes ...
[info] compiling 1 Scala source to /tmp/foo-build/target/scala-2.13/classes ...
[info] running example.Hello
Hello! The current temperature in New York is 22.7 C.
```

### sbt-native-packger プラグインを追加する

エディタを使って `project/plugins.sbt` を追加する:

@@snip [example-weather-plugins]($root$/src/sbt-test/ref/example-weather/changes/plugins.sbt) {}

次に `build.sbt` を以下のように変更して `JavaAppPackaging` を追加する:

@@snip [example-weather-build2]($root$/src/sbt-test/ref/example-weather/changes/build.sbt) {}

### 配布用の .zip ファイルを作る


```
sbt:Hello> reload
...
sbt:Hello> dist
[info] Wrote /private/tmp/foo-build/target/scala-2.13/hello_2.13-0.1.0-SNAPSHOT.pom
[info] Main Scala API documentation to /tmp/foo-build/target/scala-2.13/api...
[info] Main Scala API documentation successful.
[info] Main Scala API documentation to /tmp/foo-build/core/target/scala-2.13/api...
[info] Wrote /tmp/foo-build/core/target/scala-2.13/hello-core_2.13-0.1.0-SNAPSHOT.pom
[info] Main Scala API documentation successful.
[warn] [1] The maintainer is empty
[warn] Add this to your build.sbt
[warn]   maintainer := "your.name@company.org"
[success] All package validations passed
[info] Your package is ready in /tmp/foo-build/target/universal/hello-0.1.0-SNAPSHOT.zip
```

パッケージ化されたアプリの実行は以下のように行う:

```
\$ /tmp/someother
\$ cd /tmp/someother
\$ unzip -o -d /tmp/someother /tmp/foo-build/target/universal/hello-0.1.0-SNAPSHOT.zip
\$ ./hello-0.1.0-SNAPSHOT/bin/hello
Hello! The current temperature in New York is 22.7 C.
``

### アプリを Docker化させる

```
sbt:Hello> Docker/publishLocal
....
[info] Built image hello with tags [0.1.0-SNAPSHOT]
```

Docker化されたアプリは以下のように実行する:

```
\$ docker run hello:0.1.0-SNAPSHOT
Hello! The current temperature in New York is 22.7 C.
``

### version を設定する

`build.sbt` を以下のように変更する:

@@snip [example-weather-build3]($root$/src/sbt-test/ref/example-weather/changes/build3.sbt) {}

### Switch scalaVersion temporarily

```
sbt:Hello> ++3.3.0!
[info] Forcing Scala version to 3.3.0 on all projects.
[info] Reapplying settings...
[info] Set current project to Hello (in build file:/private/tmp/foo-build/)
```

`scalaVersion` セッティングを確認する:

```
sbt:Hello> scalaVersion
[info] helloCore / scalaVersion
[info]  3.3.0
[info] scalaVersion
[info]  3.3.0
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

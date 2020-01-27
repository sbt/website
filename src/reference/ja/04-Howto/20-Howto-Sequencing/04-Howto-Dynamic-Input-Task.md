---
out: Howto-Dynamic-Input-Task.html
---

### Def.inputTaskDyn を用いた動的インプットタスクの定義

ここで、プラグインが `openbrowser` というブラウザを開くタスクを既に提供していると仮定する。それをインプットタスクの後で呼び出す方法を考察する。

#### build.sbt v1

```scala
lazy val runopen = inputKey[Unit]("run and then open the browser")
lazy val openbrowser = taskKey[Unit]("open the browser")

lazy val root = (project in file("."))
  .settings(
    runopen := (Def.inputTaskDyn {
      import sbt.complete.Parsers.spaceDelimited
      val args = spaceDelimited("<args>").parsed
      Def.taskDyn {
        (Compile / run).toTask(" " + args.mkString(" ")).value
        openbrowser
      }
    }).evaluated,
    openbrowser := {
      println("open browser!")
    }
  )
```

#### build.sbt v2

この動的インプットタスクを `Compile / run` に再配線するのは複雑な作業だ。内側の `Compile / run` は既に継続タスクの中に入ってしまっているので、単純に再配線しただけだと循環参照を作ってしまうことになる。
この循環を断ち切るためには、`Compile / run` のクローンである `Compile / actualRun` を導入する必要がある:

```scala
lazy val actualRun = inputKey[Unit]("The actual run task")
lazy val openbrowser = taskKey[Unit]("open the browser")

lazy val root = (project in file("."))
  .settings(
    Compile / run := (Def.inputTaskDyn {
      import sbt.complete.Parsers.spaceDelimited
      val args = spaceDelimited("<args>").parsed
      Def.taskDyn {
        (Compile / actualRun).toTask(" " + args.mkString(" ")).value
        openbrowser
      }
    }).evaluated,
    Comile / actualRun := Defaults.runTask(
      Runtime / fullClasspath,
      Compile / run / mainClass,
      Compile / run / runner
    ).evaluated,
    openbrowser := {
      println("open browser!")
    }
  )
```

この `Compile / actualRun` の実装は Defaults.scala にある `run` の実装からコピペしてきた。

これで `run foo` をシェルから打ち込むと、`Compile / actualRun` を引数とともに評価して、その後で `openbrowser` タスクを評価するようになった。

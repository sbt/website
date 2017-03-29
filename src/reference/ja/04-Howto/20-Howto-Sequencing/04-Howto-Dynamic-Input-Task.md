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
        (run in Compile).toTask(" " + args.mkString(" ")).value
        openbrowser
      }
    }).evaluated,
    openbrowser := {
      println("open browser!")
    }
  )
```

#### build.sbt v2

この動的インプットタスクを `run in Compile` に再配線するのは複雑な作業だ。内側の `run in Compile` は既に継続タスクの中に入ってしまっているので、単純に再配線しただけだと循環参照を作ってしまうことになる。
この循環を断ち切るためには、`run in Compile` のクローンである `actualRun in Compile` を導入する必要がある:

```scala
lazy val actualRun = inputKey[Unit]("The actual run task")
lazy val openbrowser = taskKey[Unit]("open the browser")

lazy val root = (project in file("."))
  .settings(
    run in Compile := (Def.inputTaskDyn {
      import sbt.complete.Parsers.spaceDelimited
      val args = spaceDelimited("<args>").parsed
      Def.taskDyn {
        (actualRun in Compile).toTask(" " + args.mkString(" ")).value
        openbrowser
      }
    }).evaluated,
    actualRun in Compile := Defaults.runTask(
      fullClasspath in Runtime,
      mainClass in (Compile, run),
      runner in (Compile, run)
    ).evaluated,
    openbrowser := {
      println("open browser!")
    }
  )
```

この `actualRun in Compile` の実装は Defaults.scala にある `run` の実装からコピペしてきた。

これで `run foo` をシェルから打ち込むと、`actualRun in Compile` を引数とともに評価して、その後で `openbrowser` タスクを評価するようになった。

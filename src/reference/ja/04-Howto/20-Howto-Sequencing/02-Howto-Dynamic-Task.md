---
out: Howto-Dynamic-Task.html
---

  [Howto-Sequential-Task]: Howto-Sequential-Task.html
  [Tasks]: Tasks.html

### Def.taskDyn を用いて動的タスクを定義する

[逐次タスク][Howto-Sequential-Task]だけで十分じゃなければ、次のステップは[動的タスク][Tasks]だ。純粋な型 `A` の値を返すことを期待する `Def.task` と違って、`Def.taskDyn` は `sbt.Def.Initialize[sbt.Task[A]]` という型のタスク・エンジンが残りの計算を継続するタスクを返す。

`Compile / compile` を実行した後で [scalastyle-sbt-plugin](http://www.scalastyle.org/sbt.html) の `Compile / scalastyle` タスクを実行するカスタムタスク、`compilecheck` を実装してみよう。

#### project/build.properties

```
sbt.version=$app_version$
```

#### project/style.sbt

```
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
```

#### build.sbt v1

```scala
lazy val compilecheck = taskKey[sbt.inc.Analysis]("compile and then scalastyle")

lazy val root = (project in file("."))
  .settings(
    compilecheck := (Def.taskDyn {
      val c = (Compile / compile).value
      Def.task {
        val x = (Compile / scalastyle).toTask("").value
        c
      }
    }).value
  )
```

これで逐次タスクと同じものができたけども、違いは最初のタスクの結果である `c` を返していることだ。

#### build.sbt v2

`Compile / compile` の戻り値と同じ型を返せるようになったので、もとのキーをこの動的タスクで再配線 (rewire) できるかもしれない。

```scala
lazy val root = (project in file("."))
  .settings(
    Compile / compile := (Def.taskDyn {
      val c = (Compile / compile).value
      Def.task {
        val x = (Compile / scalastyle).toTask("").value
        c
      }
    }).value
  )
```

これで、`Compile / compile` をシェルから呼び出してやりたかったことをやらせれるようになった。

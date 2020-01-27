---
out: Howto-Dynamic-Input-Task.html
---

### Defining a dynamic input task with Def.inputTaskDyn

Let's suppose that there's a task already that does the browser opening called `openbrowser` because of a plugin. Here's how we can sequence a task after an input tasks.

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

Trying to rewire `Compile / run` is going to be complicated. Since the reference to the inner `Compile / run` is already inside the continuation task, simply rewiring `runopen` to `Compile / run` will create a cyclic reference.
To break the cycle, we will introduce a clone of `Compile / run` called `Compile / actualRun`:

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

\* Note that some tasks (ie. `testOnly`) will fail with trailing spaces, so a right trim (`.replaceAll("\\s+\$", "")`) of the string built for `toTask` might be needed to handle empty `args`.\

The `Compile / actualRun`'s implementation was copy-pasted from `run` task's implementation in Defaults.scala.

Now we can call `run foo` from the shell and it will evaluate `Compile / actualRun` with the passed in argument, and then evaluate the `openbrowser` task.

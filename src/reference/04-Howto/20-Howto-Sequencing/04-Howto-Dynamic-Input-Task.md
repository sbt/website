---
out: Howto-Dynamic-Input-Task.html
---

### Defining a dynamic input task with Def.inputTaskDyn

Let's suppose that there's a task already that does the bowser opening called `openbrowser` because of a plugin. Here's how we can sequence a task after an input tasks.

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

Trying to rewire `run in Compile` is going to be complicated. Since the reference to the inner `run in Compile` is already inside the continuation task, simply rewiring `runopen` to `run in Compile` will create a cyclic reference.
To break the cycle, we will introduce a clone of `run in Compile` called `actualRun in Compile`:

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

\* Note that some tasks (ie. `testOnly`) will fail with trailing spaces, so a right trim (`.replaceAll("\\s+\$", "")`) of the string built for `toTask` might be needed to handle empty `args`.\

The `actualRun in Compile`'s implementation was copy-pasted from `run` task's implementation in Defaults.scala.

Now we can call `run foo` from the shell and it will evaluate `actualRun in Compile` with the passed in argument, and then evaluate the `openbrowser` task.

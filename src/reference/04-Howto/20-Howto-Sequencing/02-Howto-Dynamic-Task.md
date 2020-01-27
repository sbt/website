---
out: Howto-Dynamic-Task.html
---

  [Howto-Sequential-Task]: Howto-Sequential-Task.html
  [Tasks]: Tasks.html

### Defining a dynamic task with Def.taskDyn

If [sequential task][Howto-Sequential-Task] is not enough, another step up is [the dynamic task][Tasks]. Unlike `Def.task` which expects you to return pure value `A`, with a `Def.taskDyn` you return a task `sbt.Def.Initialize[sbt.Task[A]]` which the task engine can continue the rest of the computation with.

Let's try implementing a custom task called `compilecheck` that runs `Compile / compile` and then `Compile / scalastyle` task added by [scalastyle-sbt-plugin](http://www.scalastyle.org/sbt.html).


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

Now we have the same thing as the sequential task, except we can now return the result `c` from the first task.

#### build.sbt v2

If we can return the same return type as `Compile / compile`, might actually rewire the key to our dynamic task.

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

Now we can actually call `Compile / compile` from the shell and make it do what we want it to do.

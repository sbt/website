---
out: Custom-Settings.html
---

  [Basic-Def]: Basic-Def.html
  [More-About-Settings]: More-About-Settings.html
  [Using-Plugins]: Using-Plugins.html
  [Organizing-Build]: Organizing-Build.html
  [Input-Tasks]: ../docs/Input-Tasks.html
  [Plugins]: ../docs/Plugins.html
  [Tasks]: ../docs/Tasks.html

Custom settings and tasks
-------------------------

This page gets you started creating your own settings and tasks.

To understand this page, be sure you've read earlier pages in the
Getting Started Guide, especially [.build.sbt][Basic-Def] and
[more kinds of setting][More-About-Settings].

### Defining a key

[Keys](../sxr/sbt/Keys.scala.html) is packed with examples
illustrating how to define keys. Most of the keys are implemented in
[Defaults](../sxr/sbt/Defaults.scala.html).

Keys have one of three types. `SettingKey` and `TaskKey` are described in
[.sbt build definition][Basic-Def]. Read about `InputKey` on the
[Input Tasks][Input-Tasks] page.

Some examples from [Keys](../sxr/sbt/Keys.scala.html):

```scala
val scalaVersion = settingKey[String]("scala-version", "The version of Scala used for building.")
val clean = taskKey[Unit]("clean", "Deletes files produced by the build, such as generated sources, compiled classes, and task caches.")
```

The key constructors have two string parameters: the name of the key
(`"scala-version"`) and a documentation string
(`"The version of scala used for building."`).

Remember from [.sbt build definition][Basic-Def] that the type
parameter `T` in `SettingKey[T]` indicates the type of value a setting has.
`T` in `TaskKey[T]` indicates the type of the task's result. Also remember
from [.sbt build definition][Basic-Def] that a setting has a fixed
value until project reload, while a task is re-computed for every "task
execution" (every time someone types a command at the sbt interactive
prompt or in batch mode).

Keys may be defined in an [.sbt file][Basic-Def],
a [.scala file][Organizing-Build], or in an [auto plugin][Using-Plugins].
Any `val`s found under `autoImport` object of an enabled auto plugin
will be imported automatically into your `.sbt` files.

### Implementing a task

Once you've defined a key for your task, you'll need to complete it with
a task definition. You could be defining your own task, or you could be
planning to redefine an existing task. Either way looks the same; use `:=`
to associate some code with the task key:

```scala
val sampleStringTask = taskKey[String]("A sample string task.")
val sampleIntTask = taskKey[Int]("A sample int task.")

lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0-SNAPSHOT"
)

lazy val library = (project in file("library")).
  settings(commonSettings: _*).
  settings(
    sampleStringTask := System.getProperty("user.home"),
    sampleIntTask := {
      val sum = 1 + 2
      println("sum: " + sum)
      sum
    }
  )
```

If the task has dependencies, you'd reference their value using `value`,
as discussed in [more kinds of setting][More-About-Settings].

The hardest part about implementing tasks is often not sbt-specific;
tasks are just Scala code. The hard part could be writing the "body" of
your task that does whatever you're trying to do. For example, maybe
you're trying to format HTML in which case you might want to use an HTML
library (you would
[add a library dependency to your build definition][Using-Plugins] and
write code based on the HTML library, perhaps).

sbt has some utility libraries and convenience functions, in particular
you can often use the convenient APIs in
[IO](../api/index.html#sbt.IO\$) to manipulate files and directories.

### Execution semantics of tasks

When depending on other tasks from a custom task using `value`,
an important detail to note is the execution semantics of the tasks.
By execution semantics, we mean exactly *when* these tasks are evaluated.

We if take `sampeIntTask` for instance, each line in the body of the task
should be strictly evaluated one after the other. That is sequential semantics:

```scala
sampleIntTask := {
  val sum = 1 + 2        // first
  println("sum: " + sum) // second
  sum                    // third
}
```

In reality JVM may inline the `sum` to `3`, but the observable *effect* of the
task will remain identical as if each line were executed one after the other.

Now suppose we define two more custom tasks `startServer` and `stopServer`,
and modify `sampeIntTask` as follows:

```scala
val startServer = taskKey[Unit]("start server")
val stopServer = taskKey[Unit]("stop server")
val sampleIntTask = taskKey[Int]("A sample int task.")
val sampleStringTask = taskKey[String]("A sample string task.")

lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0-SNAPSHOT"
)

lazy val library = (project in file("library")).
  settings(commonSettings: _*).
  settings(
    startServer := {
      println("starting...")
      Thread.sleep(500)
    },
    stopServer := {
      println("stopping...")
      Thread.sleep(500)
    },
    sampleIntTask := {
      startServer.value
      val sum = 1 + 2
      println("sum: " + sum)
      stopServer.value // THIS WON'T WORK
      sum
    },
    sampleStringTask := {
      startServer.value
      val s = sampleIntTask.value.toString
      println("s: " + s)
      s
    }
  )
```

Running `sampleIntTask` from sbt interactive prompt results to the following:

```
> sampleIntTask
stopping...
starting...
sum: 3
[success] Total time: 1 s, completed Dec 22, 2014 5:00:00 PM
```

To review what happened, let's look at a graphical notation of `sampleIntTask`:

![task-dependency](files/task-dependency00.png)

Unlike plain Scala method calls, invoking `value` method on tasks will not
be evaluated strictly. Instead, they simply act as placeholders to denote
that `sampleIntTask` depends on `startServer` and `stopServer` tasks.
When `sampleIntTask` is invoked by you, sbt's tasks engine will:

- evaluate the task dependencies *before* evaluating `sampleIntTask` (partial ordering)
- try to evaluate task dependencies in parallel if they are independent (parallelization)
- each task dependency will be evaluated once and only once per command execution (deduplication)

#### Deduplication of task dependencies

To demonstrate the last point, we can run `sampleStringTask` from sbt
interactive prompt.

```
> sampleStringTask
stopping...
starting...
sum: 3
s: 3
[success] Total time: 1 s, completed Dec 22, 2014 5:30:00 PM
```

Because `sampleStringTask` depends on both `startServer` and `sampleIntTask` task,
and `sampleIntTask` also depends on `startServer` task, it appears twice as task dependency.
If this was a plain Scala method call it would be evaluated twice,
but since `value` is just denoting a task dependency, it will be evaluated once.
The following is a graphical notation of how `sampeStringTask`'s evalutation:

![task-dependency](files/task-dependency01.png)

If we did not deduplicate the task dependencies, we will end up
compiling test source code many times when `test` task is invoked
since `compile in Test` appears many times as a task dependency of `test in Test`.

#### Cleanup task

How should one implement `stopServer` task?
The notion of cleanup task does not fit into the execution model of tasks because
tasks are about tracking dependencies.
The last operation should become the task that depends
on other intermediate tasks. For instance `stopServer` should depend on `sampleStringTask`,
at which point `stopServer` should be the `sampleStringTask`.

```scala
lazy val library = (project in file("library")).
  settings(commonSettings: _*).
  settings(
    startServer := {
      println("starting...")
      Thread.sleep(500)
    },
    sampleIntTask := {
      startServer.value
      val sum = 1 + 2
      println("sum: " + sum)
      sum
    },
    sampleStringTask := {
      startServer.value
      val s = sampleIntTask.value.toString
      println("s: " + s)
      s
    },
    sampleStringTask := {
      val old = sampleStringTask.value
      println("stopping...")
      Thread.sleep(500)
      old      
    }
  )
```

To demonstrate that it works, run `sampleStringTask` from the interactive prompt:

```
> sampleStringTask
starting...
sum: 3
s: 3
stopping...
[success] Total time: 1 s, completed Dec 22, 2014 6:00:00 PM
```

![task-dependency](files/task-dependency02.png)

#### Use plain Scala

Another way of making sure that something happens after some other thing is to use Scala.
Implement a simple function in `project/ServerUtil.scala` for example, and you can write:

```scala
sampleIntTask := {
  ServerUtil.startServer
  try {
    val sum = 1 + 2
    println("sum: " + sum)
  } finally {
    ServerUtil.stopServer
  } 
  sum
}
```

Since plain method calls follow sequential semantics, everything happens in order.
There's no deduplication, so you have to be careful about that.

### Turn them into plugins

If you find you have a lot of custom code, consider moving it to a
plugin for re-use across multiple builds.

It's very easy to create a plugin, as [teased earlier][Using-Plugins]
and [discussed at more length here][Plugins].

This page has been a quick taste; there's much much more about custom
tasks on the [Tasks][Tasks] page.

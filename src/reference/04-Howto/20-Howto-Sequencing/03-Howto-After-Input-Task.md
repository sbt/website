---
out: Howto-After-Input-Task.html
---

  [Input-Tasks]: Input-Tasks.html

### Doing something after an input task

Thus far we've mostly looked at tasks. There's another kind of tasks called input tasks that accepts user input from the shell.
A typical example for this is the `run in Compile` task. The `scalastyle` task is actually an input task too. See [input task][Input-Tasks] for the details of the input tasks.

Now suppose we want to call `run in Compile` task and then open the browser for testing purposes.

#### src/main/scala/Greeting.scala

```scala
object Greeting extends App {
  println("hello " + args.toList)
}
```

#### build.sbt v1

```scala
lazy val runopen = inputKey[Unit]("run and then open the browser")

lazy val root = (project in file("."))
  .settings(
    runopen := {
      (run in Compile).evaluated
      println("open browser!")
    }
  )
```

Here, I'm faking the browser opening using `println` as the side effect. We can now call this task from the shell:

```
> runopen foo
[info] Compiling 1 Scala source to /x/proj/...
[info] Running Greeting foo
hello List(foo)
open browser!
```

#### build.sbt v2

We can actually remove `runopen` key, by rewriting the new input task to `run in Compile`:

```scala
lazy val root = (project in file("."))
  .settings(
    run in Compile := {
      (run in Compile).evaluated
      println("open browser!")
    }
  )
```

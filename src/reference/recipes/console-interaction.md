# Getting user inputs

```admonish note
The recipe section of the documentation focuses on the objectives
with minimal explanations about the basics.

Generally, we recommend the use of [input task](../reference/input-task.md) rather than making a task or a command interactive.
```

## Objective

I want to read user input from the console in a task or a command.

## Steps

1. Use the `interactionService` key to acquire `sbt.InteractionService`:

```scala
lazy val demo1 = taskKey[Int]("demo1")

scalaVersion := "{{scala3_example_version}}"

demo1 := Def.uncached {
  val srv = interactionService.value
  srv.readLine(prompt = "enter a number: ", mask = false) match
    case Some(x) => x.toInt
    case None    => sys.error("error getting an input!")
}
```

2. For commands, use `CommandLineUIService` directly:

```scala
lazy val demo2 = Command.command("demo2"): s0 =>
  val srv = CommandLineUIService
  srv.readLine(prompt = "enter a number: ", mask = false) match
    case Some(x) => x.toInt
    case None    => sys.error("error getting an input!")
  s0

LocalRootProject / commands += demo2
```

## Test

```bash
$ sbt --client
sbt:demo> demo1
enter a number: 1
[success] elapsed time: 1 s
sbt:demo> demo2
enter a number: 1
sbt:demo> shutdown
[info] disconnected

$ sbt --server
sbt:demo> demo1
enter a number: 1
[success] elapsed time: 1 s
sbt:demo> demo2
enter a number: 1
sbt:demo> exit
[info] shutting down sbt server
```

## Alternative

You can also use `scala.io.StdIn.readLine` function.

```scala
lazy val demo3 = taskKey[Int]("demo3")
demo3 := Def.uncached {
  import scala.io.StdIn
  Option(StdIn.readLine("enter a number: ")) match
    case Some(x) => x.toInt
    case None    => sys.error("error getting an input!")
}
```

## Explanation

sbt 2 runs in client-server mode by default, involving sbtn and the sbt server process. This means that when a task prompts the user for input, the prompting happens on the client (sbtn) while the task executes on the server.
This client-server separation also existed in sbt 0.13 when Activator provided a web-based frontend to sbt. To abstract over simple user interactions, sbt provides `interactionService`, initialized to `CommandLineUIService`. This key is also supported in both sbt 1.x and 2.x, where it enables sbtn terminal virtualization.

Alternatively, you can use `scala.io.StdIn.readLine` inside a task or command definition. Note that this relies on the sbt server proxying terminal interactions to sbtn over the wire.

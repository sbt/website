---
out: Custom-Settings.html
---

  [Basic-Def]: Basic-Def.html
  [More-About-Settings]: More-About-Settings.html
  [Using-Plugins]: Using-Plugins.html
  [Input-Tasks]: http://www.scala-sbt.org/release/docs/Extending/Input-Tasks.html
  [Plugins]: http://www.scala-sbt.org/release/docs/Extending/Plugins.html
  [Tasks]: http://www.scala-sbt.org/release/docs/Extending/Tasks.html

Custom settings and tasks
-------------------------

This page gets you started creating your own settings and tasks.

To understand this page, be sure you've read earlier pages in the
Getting Started Guide, especially [.build.sbt][Basic-Def] and
[more kinds of setting][More-About-Settings].

### Defining a key

[Keys](../../sxr/sbt/Keys.scala.html) is packed with examples
illustrating how to define keys. Most of the keys are implemented in
[Defaults](../../sxr/sbt/Defaults.scala.html).

Keys have one of three types. `SettingKey` and `TaskKey` are described in
[.sbt build definition][Basic-Def]. Read about `InputKey` on the
[Input Tasks][Input-Tasks] page.

Some examples from [Keys](../../sxr/sbt/Keys.scala.html):

```scala
val scalaVersion = settingKey[String]("The version of Scala used for building.")
val clean = taskKey[Unit]("Deletes files produced by the build, such as generated sources, compiled classes, and task caches.")
```

The key constructors have two string parameters: the name of the key
(`"scalaVersion"`) and a documentation string
(`"The version of scala used for building."`).

Remember from [.sbt build definition][Basic-Def] that the type
parameter `T` in `SettingKey[T]` indicates the type of value a setting has.
`T` in `TaskKey[T]` indicates the type of the task's result. Also remember
from [.sbt build definition][Basic-Def] that a setting has a fixed
value until project reload, while a task is re-computed for every "task
execution" (every time someone types a command at the sbt interactive
prompt or in batch mode).

<!-- TODO: FIX THIS -->

Keys may be defined in a [.sbt file][Basic-Def],
[.scala file][Full-Def], or in [a plugin][Using-Plugins]. Any `val`
found in a `Build` object in your `.scala` build definition files or any `val`
found in a `Plugin` object from a plugin will be imported automatically
into your `.sbt` files.

### Implementing a task

Once you've defined a key for your task, you'll need to complete it with
a task definition. You could be defining your own task, or you could be
planning to redefine an existing task. Either way looks the same; use `:=`
to associate some code with the task key:

```scala
val sampleStringTask = taskKey[String]("A sample string task.")

val sampleIntTask = taskKey[Int]("A sample int task.")

sampleStringTask := System.getProperty("user.home")

sampleIntTask := {
  val sum = 1 + 2
  println("sum: " + sum)
  sum
}
```

If the task has dependencies, you'd reference their value using `value`,
as discussed in [more kinds of setting][More-About-Settings].

The hardest part about implementing tasks is often not sbt-specific;
tasks are just Scala code. The hard part could be writing the "meat" of
your task that does whatever you're trying to do. For example, maybe
you're trying to format HTML in which case you might want to use an HTML
library (you would
[add a library dependency to your build definition][Using-Plugins] and
write code based on the HTML library, perhaps).

sbt has some utility libraries and convenience functions, in particular
you can often use the convenient APIs in
[IO](../../api/index.html#sbt.IO\$) to manipulate files and directories.

### Use plugins!

If you find you have a lot of custom code, consider moving it to a
plugin for re-use across multiple builds.

It's very easy to create a plugin, as [teased earlier][Using-Plugins]
and [discussed at more length here][Plugins].

This page has been a quick taste; there's much much more about custom
tasks on the [Tasks][Tasks] page.

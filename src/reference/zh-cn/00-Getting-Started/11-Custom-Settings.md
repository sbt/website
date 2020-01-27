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

自定义设置和任务
-------------------------

这一小节讲解如何创建自定义设置和任务。

在理解本节之前，请先阅读 sbt 入门前面的章节，尤其是 [.sbt 构建定义][Basic-Def]和[更多关于设置][More-About-Settings]。

### 定义一个键

[这里](../../api/sbt/Keys\$.html)介绍了如何定义键。大多数的默认键定义在[这里](https://github.com/sbt/sbt/blob/develop/main/src/main/scala/sbt/Defaults.scala)。

键有三种类型。`SettingKey` 和 `TaskKey` 在 [.sbt 构建定义][Basic-Def]讲解。关于 `InputKey` 的内容在[输入任务][Input-Tasks]页面。

列举一些来自 [Keys](../../api/sbt/Keys\$.html) 的例子：

```scala
val scalaVersion = settingKey[String]("scala的版本")
val clean = taskKey[Unit]("删除构建产生的文件，包括生成的 source 文件，编译的类和任务缓存。")
```

键的构造函数有两个字符串参数：键的名称（ `“scalaVersion”` ）和文档字符串（ `“用于构建工程的scala的版本。 ”` ）。

还记得[ .sbt 构建定义][Basic-Def]中，类型 `T` 在 `SettingKey[T]` 中表示的设置的值的类型。类型 `T` 在 `TaskKey [T]` 中指示任务的结果的类型。
在[ .sbt 构建定义][Basic-Def]中，一个设置有一个固定的值，直到项目重新加载。任务会在每一个“任务执行”（用户在交互输入中或在batch模式下输入一个命令）被重新计算。

键可以在定义在[.sbt 构建定义][Basic-Def]，[.scala 文件][Organizing-Build]或一个[自动插件][Using-Plugins]中。任何在启用的自动插件的`autoImport`对象的 `val` 将被自动导入
到你的 `.sbt` 文件。

### 执行任务

一旦你定义了一个任务的键，你需要用它完成任务定义。你可以定义自己的任务，或者重新定义现有的任务。无论哪种方式看起来是一样的；用 `:=` 使任务的键和部分代码相关联：

```scala
val sampleStringTask = taskKey[String]("A sample string task.")
val sampleIntTask = taskKey[Int]("A sample int task.")

ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "$example_scala_version$"

lazy val library = (project in file("library"))
  .settings(
    sampleStringTask := System.getProperty("user.home"),
    sampleIntTask := {
      val sum = 1 + 2
      println("sum: " + sum)
      sum
    }
  )
```

在[更多关于设置][More-About-Settings]里有描述，如果任务有依赖关系，你使用 `value` 来引用值。

有关任务实现最困难的部分往往不是 sbt 专用；任务只是 Scala 代码。困难的部分可能是写你的任务体，即做什么，或者说你正在试图做的。例如，你要格式化 HTML，在这种情况下，你可能需要使用一个 HTML 库（也许你将[为构建定义添加一个库的依赖][Using-Plugins]来编写基于 HTML 库代码）。

sbt 具有一些实用工具库和方便的函数，特别是可以经常使用 API 中的 [IO](../../api/sbt/io/IO\$.html) 来操作文件和目录。

### 任务的执行语义

当从依赖于其他任务的自定义任务中使用`value`时，一个要注意的重要细节是是任务的执行语义。对执行语义，我们的意思是到底*何时*这些任务被取值。

以`sampeIntTask`为例，任务体中的每一行应严格地一个接一个被取值。这就是顺序语义：

```scala
sampleIntTask := {
  val sum = 1 + 2        // first
  println("sum: " + sum) // second
  sum                    // third
}
```

在现实中，JVM可能内联`sum`为`3`，但任务可观察到的*行为*仍将与严格地一个接一个被执行完全相同。

现在假设我们定义了另外两个的自定义任务`startServer`和`stopServer`，并修改`sampeIntTask`，如下所示：

```scala
val startServer = taskKey[Unit]("start server")
val stopServer = taskKey[Unit]("stop server")
val sampleIntTask = taskKey[Int]("A sample int task.")
val sampleStringTask = taskKey[String]("A sample string task.")

ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "$example_scala_version$"

lazy val library = (project in file("library"))
  .settings(
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

从sbt交互式提示符中运行`sampleIntTask`将得到如下结果：

```
> sampleIntTask
stopping...
starting...
sum: 3
[success] Total time: 1 s, completed Dec 22, 2014 5:00:00 PM
```

若要查看发生了什么事，让我们看一下`sampleIntTask`图形表示：

![task-dependency](../files/task-dependency00.png)

不同于普通的Scala方法调用，调用任务的`value`方法将不被严格取值。相反，他们只是充当占位符来表示`sampleIntTask`依赖于`startServer`和`stopServer`任务。当你调用`sampleIntTask`时，sbt的任务引擎将：

- 在对`sampleIntTask`取值前对依赖任务取值（偏序）
- 如果依赖任物是相互独立的，尝试并行取值（并行）
- 每次命令执行，每个任务依赖项将被评估且仅被评估一次（去重）

#### 任务依赖项去重

为证明这最后一点，我们可以从 sbt 交互式提示符运行 `sampleStringTask`。

```
> sampleStringTask
stopping...
starting...
sum: 3
s: 3
[success] Total time: 1 s, completed Dec 22, 2014 5:30:00 PM
```

因为`sampleStringTask`依赖于`startServer`和`sampleIntTask`两个任务，而`sampleIntTask`也依赖于`startServer`任务，它作为任务依赖出现了两次。如果这是一个普通的 Scala 方法调用，它会被计算两次，但由于任务的依赖项被标记为`value`类型，它将只被计算一次。以下是`sampeStringTask`如何取值的图形表示：

![task-dependency](../files/task-dependency01.png)

如果我们不做重复任务相关项的去重，则当我们执行`test`时最终会编译测试源代码很多次，因为`Test / compile`作为`Test / test`的依赖项出现了很多次。

#### 清理任务

应该如何实现`stopServer`任务？清理任务的概念并不适合任务的执行模型，因为任务关心的是依赖项跟踪。最后一次操作应成为依赖其他中间任务的任务。例如`stopServer`应依赖于`sampleStringTask`，在其中`stopServer`应该是 `sampleStringTask`。

```scala
lazy val library = (project in file("library"))
  .settings(
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

为了证明它可以工作，在交互式提示符中运行 `sampleStringTask`：

```
> sampleStringTask
starting...
sum: 3
s: 3
stopping...
[success] Total time: 1 s, completed Dec 22, 2014 6:00:00 PM
```

![task-dependency](../files/task-dependency02.png)



#### 直接使用Scala

确保一些事发生在其它一些事物之后的另一种方式是使用Scala。例如，在`project/ServerUtil.scala`中实现一个简单的函数，你可以编写：

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

因为普通的方法调用遵循顺序语义，所有事情按顺序发生。这里没有去重，所以你必须要小心。

### 将它们转为插件

如果你发现自己有很多自定义代码，可以考虑将其移动到插件，从而可以在多个构建中重复利用。

创建一个插件很容易，在[使用插件][Using-Plugins]和[插件][Plugins]中有详细讨论。

本小节是个快速的向导；更多关于自定义任务可以在[任务][Tasks]中找到。

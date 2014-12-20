---
out: Custom-Settings.html
---

  [Basic-Def]: Basic-Def.html
  [More-About-Settings]: More-About-Settings.html
  [Using-Plugins]: Using-Plugins.html
  [Full-Def]: Full-Def.html
  [Input-Tasks]: ../docs/Input-Tasks.html
  [Plugins]: ../docs/Plugins.html
  [Tasks]: ../docs/Tasks.html

自定义设置和任务
-------------------------

这一小节讲解如何创建自定义设置和任务。

在理解本节之前，请先阅读 sbt 入门前面的章节，尤其是 [.sbt 构建定义][Basic-Def]和[更多关于设置][More-About-Settings]。

### 定义一个键

[这里](../../sxr/sbt/Keys.scala.html)介绍了如何定义键。大多数的默认键定义在[这里](../../sxr/sbt/Defaults.scala.html)。

键有三种类型。`SettingKey` 和 `TaskKey` 在 [.sbt 构建定义][Basic-Def]讲解。关于 `InputKey` 的内容在[输入任务][Input-Tasks]页面。

列举一些来自 [Keys](../../sxr/sbt/Keys.scala.html) 的例子：

```scala
val scalaVersion = settingKey[String]("scala的版本")
val clean = taskKey[Unit]("删除构建产生的文件，包括生成的 source 文件，编译的类和任务缓存。")
```

键的构造函数有两个字符串参数：键的名称（ `“scalaVersion”` ）和文档字符串（ `“用于构建工程的scala的版本。 ”` ）。

还记得[ .sbt 构建定义][Basic-Def]中，类型 `T` 在 `SettingKey[T]` 中表示的设置的值的类型。类型 `T` 在 `TaskKey [T]` 中指示任务的结果的类型。
在[ .sbt 构建定义][Basic-Def]中，一个设置有一个固定的值，直到项目重新加载。任务会在每一个“任务执行”（用户在交互输入中或在batch模式下输入一个命令）被重新计算。

键可以在定义在[ .sbt 构建定义][Basic-Def]，[.scala 文件][Full-Def]或[插件][Using-Plugins]。任何在 `.scala` 构建定义文件发现的 `val`，`Build` 对象或 `Plugin` 对象中 plugin 的 `val` 将被自动导入
到你的 `.sbt` 文件。

### 执行任务

一旦你定义了一个任务的键，你需要用它完成任务定义。你可以定义自己的任务，或者重新定义现有的任务。无论哪种方式看起来是一样的；用 `:=` 使任务的键和部分代码相关联：

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

在[更多关于设置][More-About-Settings]里有描述，如果任务有依赖关系，你使用 `value` 来引用值。

有关任务实现最困难的部分往往不是 sbt 专用；任务只是 Scala 代码。困难的部分可能是写你的任务做什么，或者说你正在试图做的。例如，你要格式化 HTML，在这种情况下，你可能需要使用一个 HTML 库（也许你将[为构建定义添加一个库的依赖][Using-Plugins]来编写基于 HTML 库代码）。

sbt 具有一些实用工具库和方便的函数，特别是可以经常使用 API 中的 [IO](../../api/index.html#sbt.IO\$) 来操作文件和目录。

### 使用插件

如果你发现自己有很多自定义代码，可以考虑将其移动到插件，从而可以在多个构建中重复利用。

创建一个插件很容易，在[使用插件][sing-Plugins]和[插件][Plugins]中有详细讨论。

本小节是个快速的向导；更多关于自定义任务可以在[任务][Tasks]中找到。
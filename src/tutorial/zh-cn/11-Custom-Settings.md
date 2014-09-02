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

在理解本节之前, 请先阅读sbt 入门前面的章节, 尤其是 [.sbt 构建定义][Basic-Def] 和
[更多关于设置][More-About-Settings].

### 定义一个键

[这里](../sxr/sbt/Keys.scala.html) 介绍了如何定义键. 大多数的默认键定义在
[这里](../sxr/sbt/Defaults.scala.html).

键有三种类型。 `SettingKey`和 `TaskKey` 的讲解在
[.sbt构建定义][Basic-Def] 。阅读关于`InputKey`的内容在
[输入任务] [Input-Tasks]页面。

列举一些例子来自 [Keys](../sxr/sbt/Keys.scala.html):

```scala
val scalaVersion = settingKey [String]("scala的版本")
val clean = taskKey[Unit]("删除构建产生的文件，包括生成的 source 文件，编译的类和任务缓存。")
```


键的构造函数有两个字符串参数：键的名称
（ `“scalaVersion”` ）和文档字符串
（ `“用于构建工程的scala的版本。 ”` ） 。

还记得[.sbt构建定义][Basic-Def]中，类型`T`在` SettingKey [T]`中表示的设置的值的类型。类型`T`在` TaskKey [T] `中指示任务的结果的类型。
在[.sbt构建定义][Basic-Def]中，一个设置有一个固定的值，直到项目重装。任务会在每一个“任务执行”（用户输入一个命令算一次）被重新计算。


键可以在定义在[.sbt构建定义][Basic-Def]，
[.scala file][Full-Def]或[插件] [Using-Plugins]。任何在` .scala`构建定义文件发现的`val`, ` Build`对象或` Plugin`对象中plugin的`val`将被自动导入
到你的` .sbt`文件。

###执行任务

一旦你定义了一个任务的键，你需要用它完成
任务定义。您可以定义自己的任务，或者你可以
重新定义现有的任务。无论哪种方式看起来是一样的;用`：=`使
任务的键和部分代码相关联：

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

在[more kinds of setting][More-About-Settings]里有描述，如果任务有依赖关系，你使用`value`来引用值。

有关任务实现最困难的部分往往不是SBT专用;任务只是Scala代码。困难的部分可能是写你的任务做什么，或者说你正在试图做的。例如，你要格式化HTML，在这种情况下，你可能需要使用一个HTML库（[您将添加一个库的依赖][Using-Plugins]，以构建定义和编写基于HTML库代码）。

SBT具有一定的实用工具库和方便的函数，特别是可以经常使用API中的[IO](../api/index.html#sbt.IO\$)来操作文件和目录。

###使用插件！

如果你发现你有很多的自定义代码，可以考虑将其移动到
插件来重复利用多重构建。

很容易创建一个插件，在[Using-Plugins][sing-Plugins] 和[Plugins][Plugins]讨论。


本小节是个快速的向导;更多关于自定义
任务可以在[任务] [Tasks]找到。

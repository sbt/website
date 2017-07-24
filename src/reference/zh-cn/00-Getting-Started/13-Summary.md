---
out: Summary.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Using-Plugins]: Using-Plugins.html
  [getting-help]: ../docs/faq.html#getting-help

总结
-----------------------

这一节将入门指南总结一下。

为了使用 sbt，有一些概念你必须理解。这有一些学习曲线，但是乐观的讲， *除了* 这些概念对于 sbt 并不多。sbt 用一小部分核心概念来使得它工作。

如果你已经阅读过所有的入门指南，现在你知道了你需要知道什么。

### sbt: 核心概念

-   Scala 基础。不可否认，熟悉 Scala 语法非常有帮助。[Programming in Scala](https://www.artima.com/shop/programming_in_scala_3ed)，Scala 的作者写的非常好的介绍。
-   [.sbt 构建定义][Basic-Def]
-   你的构建定义是一个大的 `Setting` 对象列表，sbt 使用 `Setting` 转换之后的键值对执行 task。
-   为了创建 `Setting`，在一个 key 上调用其中的一个方法：`:=`，`+=` 或者 `++=`。
-   没有可变的状态，至于转换；例如，一个 `Setting` 将 sbt 的键值对集合转换成一个新的集合。不会就地改变任何代码。
-   每一个设置都有一个特定类型的值，由 key 决定。
-   *tasks* 是特殊的设置，通过 key 产生 value 的计算在每次出发 task 的时候都会重新执行。Non-task 计算只会在构建定义的第一次加载时执行。
-   [Scopes][Scopes]
-   每一个 key 都可能有多个 value，按照 scope 划分。
-   scope 会用三个轴：configuration，project，task。
-   scope 允许你按项目、按 task、按 configuration 有不同的行为。
-   一个 configuration 是一种类型的构建，例如 `Compile` 或者 `Test`。
-   project 轴也支持 "构建全局" scope。
-   scopes 回滚或 *代理* 到更通用的 scope。
-   将大部分配置放在 `build.sbt` 中，但是用 `.scala` 构建定义文件定义类和更大的 task 实现。
-   构建定义是一个 sbt 项目，来自于项目目录。
-   [插件][Using-Plugins]是对构建定义的扩展
-   通过在 `addSbtPlugin` 方法在 `project/plugins.sbt` 中添加插件。（不是在项目基目录下的 `build.sbt` 中）。

如果你怀疑这些细枝末节中的任何一个，请[寻求帮助][getting-help]，返回重新阅读或者在 sbt 的交互式命令行中做实验。

祝你好运！

### 附录

因为 sbt 是一个开源项目，别忘记签出项目[源代码](https://github.com/sbt/sbt)！

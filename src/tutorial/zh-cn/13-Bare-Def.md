---
out: Bare-Def.html
---

  [More-About-Settings]: More-About-Settings.html
  [Full-Def]: Full-Def.html
  [Basic-Def]: Basic-Def.html

裸露的 .sbt 构建定义
------------------

这一小节讲述一种老式的 `.sbt` 构建定义。
现在推荐使用 [Multi-project .sbt build definition][Basic-Def]。

### 什么是裸露的 .sbt 构建定义

不像 [多项目 .sbt 构建定义][Basic-Def] 和 [.scala 构建定义][Full-Def] 显示的定义一个 [项目](../api/sbt/Project.html)，
裸露的构建定义会根据 `.sbt` 文件所在的位置隐式的顶一个项目。

裸露的 `.sbt` 构建定义由一个 `Setting[_]` 表达式列表组成，而不是定义 `Project`。

```scala
name := "hello"

version := "1.0"

scalaVersion := "$example_scala_version$"
```

### (在 0.13.7 之前) 设置项必须以空行分隔

**注意**：这种空行限定在 0.13.7 之后将不再需要。

你不能像这样写裸露的 build.sbt：

```scala
// 没有空行，会编译不过
name := "hello"
version := "1.0"
scalaVersion := "2.10.3"
```

sbt 需要有分隔符在辨别一个表达式从哪里开始，到哪里结束。

---
out: Organizing-Build.html
---

  [Basic-Def]: Basic-Def.html
  [More-About-Settings]: More-About-Settings.html
  [Using-Plugins]: Using-Plugins.html
  [Library-Dependencies]: Library-Dependencies.html
  [Multi-Project]: Multi-Project.html
  [Plugins]: ../reference/Plugins.html

组织构建
--------------------

本页面将讨论构建结构的组织。

本小节假设你已经阅读了之前的章节，尤其是 [build.sbt][Basic-Def]，[库依赖][Library-Dependencies]和[多工程构建][Multi-Project]。

### sbt是递归的

`build.sbt` 很简单，隐藏了 sbt 是如何工作的。sbt 构建是用 Scala 代码定义的。代码本身也必须是能被构建的。有比 sbt 更好的建立方式么？

`project` 目录 *是你的工程内另一个工程的项目*，它知道如何构建你的工程。为了区分这两种构建，我们有时使用**正常构建**表示你的构建，使用**元构建**指代在 `project`中的构建。在元构建中的项目能做任何其他项目可以做的事情。 *你的构建定义是一个 sbt 项目。*

递归可以继续下去。如果你喜欢, 你可以通过创建 `project/project/` 目录稍稍调整项目的构建定义。

下面是一个例子：

```
hello/                  # 项目的基目录

    Hello.scala         # 一个项目源文件（也可以在src/main/scala）

    build.sbt           # build.sbt 是project/ 中元构建根项目的源代码。是构建定义项目的一部分。

    project/            # 元构建根项目的基目录

        Build.scala     # 元构建根项目的一个源文件，是你的构建定义的构建定义源文件

        build.sbt       # 元元构建的根项目——project/project的源代码；构建定义的构建定义

        project/        # 元元构建的根项目的基目录；构建定义的构建定义工程

            Build.scala # project/project/ 元元构建的根项目中的源文件
```

*不用担心！* 大部分时候不需要 `project/project/` 目录。但是理解它是有帮助的。

另外，任何以 `.scala` 或者 `.sbt` 结尾的文件都会被使用，命名为 `build.sbt` 和 `Build.scala`只是惯例。多个文件也是允许的。

### 在同一个地方跟踪依赖项

用`project`下的`.scala`文件组成构建定义的一个实际用例是创建`project/Dependencies.scala`来在同一个地方跟踪依赖项。

```scala
import sbt._

object Dependencies {
  // Versions
  lazy val akkaVersion = "$example_akka_version$"

  // Libraries
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % akkaVersion
  val specs2core = "org.specs2" %% "specs2-core" % "$example_specs2_version$"

  // Projects
  val backendDeps =
    Seq(akkaActor, specs2core % Test)
}
```

`Dependencies`对象将在`build.sbt`中可用。如果要让使用`val`的代码更加简单，可以引入`Dependencies._`。

```scala
import Dependencies._

ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "$example_scala_version$"

lazy val backend = (project in file("backend"))
  .settings(
    name := "backend",
    libraryDependencies ++= backendDeps
  )
```

当你的多工程构建变得很大，并且想要确保子项目有一致的依赖关系时，这种技术很有用。

### 何时用 `.scala` 文件

在 `.scala` 文件，你可以写任意的 Scala 代码，包括顶层的类和对象。

推荐的方法是定义大部分设置在多工程的 `build.sbt` 文件中，并且使用 `project/*.scala` 文件来做任务实现或在多个文件中共享键值。对`.scala`文件的使用也取决于你的团队对scala的熟练程度。

### 定义自动插件

对于更高级的用户，另一种方式组织你的构建是在`project/*.scala`中定义一次性[自动插件][Plugins]。通过定义触发的插件，自动插件可以用作一种简便方法来注入跨所有子项目的自定义任务和命令。

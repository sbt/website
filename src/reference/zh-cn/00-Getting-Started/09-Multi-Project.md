---
out: Multi-Project.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Directories]: Directories.html
  [Organizing-Build]: Organizing-Build.html

多项目构建
--------------------

这一小节介绍在一个构建中定义多个项目。

请先阅读入门指南前面的内容，尤其需要在阅读本小节之前理解 [.sbt 构建定义][Basic-Def]。

### 多项目

将多个相关的项目定义在一个构建中是很有用的，尤其是如果它们依赖另一个，而且你倾向于一起修改它们。

每个子项目在构建中都有它们自己的源文件夹，当打包时生成各自的 jar 文件，而且通常和其他的项目一样运转。

通过声明一个类型为 [Project](../../api/sbt/Project.html) 的 lazy val 定义一个项目，例如：

```scala
lazy val util = project

lazy val core = project
```

val 的名称被用作项目的 ID 和基目录名。该 ID 用于在命令行中引用该项目。基目录可能通过 `in` 方法改变。例如，下面是一个更加显示的方式来实现前一个例子：

```scala
lazy val util = project.in(file("util"))

lazy val core = project in file("core")
```

#### 公共设定

To factor out common settings across multiple projects, create a sequence named `commonSettings` and call `settings` method on each project.
要跨多个项目提取公共设置，请创建一个名为`commonSettings`的序列，并在每个项目上调用`settings`方法。

```scala
lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0",
  scalaVersion := "$example_scala_version$"
)

lazy val core = (project in file("core"))
  .settings(
    commonSettings,
    // other settings
  )

lazy val util = (project in file("util"))
  .settings(
    commonSettings,
    // other settings
  )
```

现在我们可以在一处修改`version`，当重新加载构建时，将在各个子项目相应更新。

### 依赖

构建中的项目完全可以彼此独立，但是通常情况下它们会有依赖上的一些相关性。有两种类型的依赖：aggregate 和 classpath。

#### Aggregation

Aggregation 意味着在 aggregate 项目上执行一个 task 也会在 aggregated 的项目执行。例如，

```scala
lazy val root = (project in file(".")).aggregate(util, core)

lazy val util = project

lazy val core = project
```

在上面的例子中，root 项目聚合了 `util` 和 `core`。像例子中一样，随着有两个子项目的情况下启动 sbt，然后尝试编译。你应该会看到全部的三个项目都被编译了。

*在进行聚合的项目中*，像这个例子中的 root 项目一样，你可以按 task 来控制聚合。例如，为了避免聚合 `update` task：

```scala
lazy val root = (project in file("."))
  .aggregate(util, core)
  .settings(
    aggregate in update := false
  )

[...]
```

`aggregate in update` 是 `update` task 的 scope 下的聚合的 key。（参见 [scopes][Scopes]。）

注意：聚合会并行的执行聚合的 task，task 之间的顺序是不确定的。

#### Classpath 依赖

一个项目可能依赖另一个项目的代码。这是通过添加 `dependsOn` 方法来实现的。例如，如果 core 在 classpath 中需要 util，你将这样定义 core：

```scala
lazy val core = project.dependsOn(util)
```

现在 `core` 中的代码可以使用 `util` 的类。在编译时也会在两个项目之间创建顺序；在编译 core 之前，`util` 必须被更新和编译过。

为了依赖多个项目，像这样 `dependsOn(bar, baz)` 给 `dependsOn` 多个参数。

##### configuration 粒度的 classpath 依赖

`foo dependsOn(bar)` 表示 `foo` 中的 `compile` configuration 依赖于 `bar` 中的 `compile` configuration。你可以显示的写成这样：`dependsOn(bar % "compile->compile")`。

`"compile->compile"` 中的 `->` 表示 "depends on"，所以 `"test->compile"` 表示 `foo` 中的 `test` configuration 将依赖于 `bar` 中的 `compile` configuration。

省略 `->config` 部分暗示 `->compile`，所以 `dependsOn(bar % "test")` 表示 `foo` 中的 `test` configuration 依赖于 `bar` 中的 `Compile` configuration。

一个实用的声明 `"test->test"` 表示 `test` 依赖于 `test`。例如，这样允许你将测试工具代码放在 `bar/src/test/scala` 中，然后在 `foo/src/test/scala` 中使用这些代码，

对于一个依赖你可以有多个 configuration，以分号分隔，例如：`dependsOn(bar % "test->test;compile->compile")`。

### 默认的 root 项目

如果在构建中根目录没有定义项目，sbt 会在构建中创建一个默认的项目并将其他项目也聚合起来。

因为 `hello-foo` 项目定义了 `base = file("foo")`，它将会被包含在 foo 子目录中。
它的源文件可以直接放在 `foo` 下，像 `foo/Foo.scala`，或者在 `foo/src/main/scala` 中。通常 sbt 的 [目录结构][Directories] 应用在 `foo` 目录下除了构建定义文件。

### 交互式引导项目

在 sbt 的命令行中，输入 `projects` 列出你的项目，执行 `project <projectname>` 可以选择当前项目。当你执行 task 像 `compile`，它会在当前项目上执行。
所以你没有必要去编译 root 项目，你可以只编译子项目。

你可以通过显示的指定项目 ID 在另一个项目上执行一个 task，例如 `subProjectID/compile`。

### 通用代码

在一个 `.sbt` 文件中的定义对于其他的 `.sbt` 文件不可见。为了在不同的 `.sbt` 文件中共享代码，在构建根目录下的 `project/` 目录下定义一个或多个 Scala 文件。

参见 [组织构建][Organizing-Build] 获取详细内容。

### Appendix: Subproject build definition files

`foo` 中的任何 `.sbt` 文件，比如说 `foo/build.sbt`，将会和整个构建合并，但是在 `hello-foo` 项目的 scope 中。

如果你的的整个项目都在 hello 中，尝试在 `hello/build.sbt`，`hello/bar/build.sbt` 和 `hello/foo/build.sbt` 中定义一个不同的版本（`version := "0.6"`）。
现在在 sbt 的命令行中执行 `show version`。你应该得到这样的信息（随着你定义的任何版本）：

```
> show version
[info] hello-foo/*:version
[info]  0.7
[info] hello-bar/*:version
[info]  0.9
[info] hello/*:version
[info]  0.5
```

`hello-foo/*:version` 定义在 `hello/foo/build.sbt` 中，`hello-bar/*:version` 定义在 `hello/bar/build.sbt` 中，`hello/*:version` 定义在 `hello/build.sbt` 中。
记住 [scoped keys 的语法][Scopes]。每个 `version` key 在对应的项目的 scope 中，基于 `build.sbt` 文件的位置。但是所有的三个 `build.sbt` 文件都只是整个构建定义的一部分。

Style choices:

- Each subproject's settings can go into `*.sbt` files in the base directory of that project,
  while the root `build.sbt` declares only minimum project declarations in the form of `lazy val foo = (project in file("foo"))` without the settings.
- We recommend putting all project declarations and settings in the root `build.sbt` file
  in order to keep all build definition under a single file. However, it up to you.

在子项目中，你不能有项目的子目录或者 `project/*.scala` 文件。`foo/project/Build.scala` 将会被忽略。

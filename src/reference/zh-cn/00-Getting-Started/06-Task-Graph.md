---
out: Task-Graph.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Make]: https://en.wikipedia.org/wiki/Make_(software)
  [Ant]: https://ant.apache.org/
  [Rake]: https://ruby.github.io/rake/

任务图
-----

> This page was translated mostly with Google Translate. Please send a pull request to improve it.

继[.sbt 构建定义][Basic-Def]，
此页面更详细地解释了 `build.sbt` 定义。

与其将 `settings` 视为键值对，
不如将其更好地比喻为将任务表示为边 happens-before 的任务的有向无环图（DAG）。
我们将此称为**任务图** (task graph)。

### 术语

在深入探讨之前，让我们先回顾一下关键术语。

- setting/task 式: `.settings(...)` 条目。
- key: setting 式的左侧。 它可以是 `SettingKey[A]`，`TaskKey[A]` 或 `InputKey[A]`。
- setting: 由带有 `SettingKey[A]` 的 setting 式定义。 该值在加载期间仅计算一次。
- task: 由带有 `TaskKey[A]` 的 task 式定义。 每次调用时都会计算该值。

### 声明对其他任务的依赖

在 `build.sbt` DSL中，我们使用 `.value` method 来表示对另一个任务或 setting 的依赖性。
value method 是特殊的，只能在 `:=` 的参数中调用（或 `+=` 或 `++=` 我们将在后面介绍）。

作为第一个示例，请考虑定义依赖于 `update` 和 `clean` 任务的 `scalacOption`。
这些是这些 key 的定义（来自 [Keys](../../api/sbt/Keys\$.html)）。

**注意**：下面计算的值对于 `scalaOptions` 是毫无 `scalaOptions`，仅用于演示目的：

```scala
val scalacOptions = taskKey[Seq[String]]("Options for the Scala compiler.")
val update = taskKey[UpdateReport]("Resolves and optionally retrieves dependencies, producing a report.")
val clean = taskKey[Unit]("Deletes files produced by the build, such as generated sources, compiled classes, and task caches.")
```

这是我们如何重新连接 `scalacOptions`:

```scala
scalacOptions := {
  val ur = update.value  // update task happens-before scalacOptions
  val x = clean.value    // clean task happens-before scalacOptions
  // ---- scalacOptions begins here ----
  ur.allConfigurations.take(3)
}
```

`update.value` 和 `clean.value` 声明了任务依赖性，
而 `ur.allConfigurations.take(3)` 是任务的主体。

`.value` 不是正常的 Scala method 调用。
`build.sbt` DSL 使用宏将它们提升到任务主体之外。
**在任务引擎评估 `scalacOptions` 的打开 `{`，无论它出现在主体中的哪一行， `update` 和 `clean` 任务都已完成**。

请参见以下示例：

```scala
ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "$example_scala_version$"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    scalacOptions := {
      val out = streams.value // streams task happens-before scalacOptions
      val log = out.log
      log.info("123")
      val ur = update.value   // update task happens-before scalacOptions
      log.info("456")
      ur.allConfigurations.take(3)
    }
  )
```

接下来，在 sbt shell 中键入 `scalacOptions`:

```
> scalacOptions
[info] Updating {file:/xxx/}root...
[info] Resolving jline#jline;2.14.1 ...
[info] Done updating.
[info] 123
[info] 456
[success] Total time: 0 s, completed Jan 2, 2017 10:38:24 PM
```

即使 `val ur = ...` 出现在 `log.info("123")` 和 `log.info("456")`，`update` 任务的评估还是要先于它们进行。

这是另一个例子：

```scala
ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "$example_scala_version$"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    scalacOptions := {
      val ur = update.value  // update task happens-before scalacOptions
      if (false) {
        val x = clean.value  // clean task happens-before scalacOptions
      }
      ur.allConfigurations.take(3)
    }
  )
```

接下来，在 sbt shell 中键入 `run`，然后键入 `scalacOptions`。

```
> run
[info] Updating {file:/xxx/}root...
[info] Resolving jline#jline;2.14.1 ...
[info] Done updating.
[info] Compiling 1 Scala source to /Users/eugene/work/quick-test/task-graph/target/scala-2.12/classes...
[info] Running example.Hello
hello
[success] Total time: 0 s, completed Jan 2, 2017 10:45:19 PM
> scalacOptions
[info] Updating {file:/xxx/}root...
[info] Resolving jline#jline;2.14.1 ...
[info] Done updating.
[success] Total time: 0 s, completed Jan 2, 2017 10:45:23 PM
```

现在，如果您检查 `target/scala-2.12/classes/`，它将不存在，因为即使它在 `if (false)` 内， `clean` 任务也已运行。

需要注意的另一件事是，不能保证 `update` 和 `clean` 任务的顺序。
他们可能同时运行 `update` 然后 `clean`，`clean` 然后 `update` 或同时运行。

### 内联 .value 调用

如上所述，`.value` 是一种特殊的 method，用于表达对其他任务和 setting 的依赖性。
在您熟悉 `build.sbt` 之前，我们建议您将所有 `.value` 调用放在任务正文的顶部。

但是，当您变得更加舒适时，您可能希望内联 `.value` 调用，因为它可以使 task/setting 更简洁，并且不必提供变量名。

我们内联了一些示例：

```scala
scalacOptions := {
  val x = clean.value
  update.value.allConfigurations.take(3)
}
```

请注意，`.value` 调用是内联的还是放在任务正文中的任何位置，在进入任务正文之前仍会对它们进行评估。

#### 检查任务

在上面的示例中，`scalacOptions` 对 `update` 和 `clean` 任务具有**依赖性**。
如果将以上内容放置在 `build.sbt` 并运行 sbt shell，则键入 `inspect scalacOptions`，您应该看到（部分）：

```
> inspect scalacOptions
[info] Task: scala.collection.Seq[java.lang.String]
[info] Description:
[info]  Options for the Scala compiler.
....
[info] Dependencies:
[info]  *:clean
[info]  *:update
....
```

这就是 sbt 如何知道哪些任务取决于哪些其他任务的方式。

例如，如果您 `inspect tree compile` 您将看到它依赖于另一个 key `incCompileSetup`，而后者又依赖于其他 key，如 `dependencyClasspath`。 继续遵循依赖性链，魔术就会发生。

```
> inspect tree compile
[info] compile:compile = Task[sbt.inc.Analysis]
[info]   +-compile:incCompileSetup = Task[sbt.Compiler\$IncSetup]
[info]   | +-*/*:skip = Task[Boolean]
[info]   | +-compile:compileAnalysisFilename = Task[java.lang.String]
[info]   | | +-*/*:crossPaths = true
[info]   | | +-{.}/*:scalaBinaryVersion = 2.12
[info]   | |
[info]   | +-*/*:compilerCache = Task[xsbti.compile.GlobalsCache]
[info]   | +-*/*:definesClass = Task[scala.Function1[java.io.File, scala.Function1[java.lang.String, Boolean]]]
[info]   | +-compile:dependencyClasspath = Task[scala.collection.Seq[sbt.Attributed[java.io.File]]]
[info]   | | +-compile:dependencyClasspath::streams = Task[sbt.std.TaskStreams[sbt.Init\$ScopedKey[_ <: Any]]]
[info]   | | | +-*/*:streamsManager = Task[sbt.std.Streams[sbt.Init\$ScopedKey[_ <: Any]]]
[info]   | | |
[info]   | | +-compile:externalDependencyClasspath = Task[scala.collection.Seq[sbt.Attributed[java.io.File]]]
[info]   | | | +-compile:externalDependencyClasspath::streams = Task[sbt.std.TaskStreams[sbt.Init\$ScopedKey[_ <: Any]]]
[info]   | | | | +-*/*:streamsManager = Task[sbt.std.Streams[sbt.Init\$ScopedKey[_ <: Any]]]
[info]   | | | |
[info]   | | | +-compile:managedClasspath = Task[scala.collection.Seq[sbt.Attributed[java.io.File]]]
[info]   | | | | +-compile:classpathConfiguration = Task[sbt.Configuration]
[info]   | | | | | +-compile:configuration = compile
[info]   | | | | | +-*/*:internalConfigurationMap = <function1>
[info]   | | | | | +-*:update = Task[sbt.UpdateReport]
[info]   | | | | |
....
```

例如，当您键入 `compile` sbt 时，它会自动执行 `update`。
它之所以行之有效，是因为作为 `compile` 计算的输入所需的值需要 sbt 首先进行 `update` 计算。

这样，sbt 中的所有构建依赖项都是**自动的**，而不是显式声明的。
如果在另一个计算中使用 key 的值，则该计算取决于该 key。

#### 定义依赖于其他 setting 的任务

`scalacOptions` 是 task key。
假设已经将其设置为某些值，但是您想为非 2.12 过滤掉 `"-Xfatal-warnings"` 和 `"-deprecation"`。

```scala
lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    organization := "com.example",
    scalaVersion := "$example_scala_version$",
    version := "0.1.0-SNAPSHOT",
    scalacOptions := List("-encoding", "utf8", "-Xfatal-warnings", "-deprecation", "-unchecked"),
    scalacOptions := {
      val old = scalacOptions.value
      scalaBinaryVersion.value match {
        case "2.12" => old
        case _      => old filterNot (Set("-Xfatal-warnings", "-deprecation").apply)
      }
    }
  )
```

这是它在 sbt shell 上的外观：

```
> show scalacOptions
[info] * -encoding
[info] * utf8
[info] * -Xfatal-warnings
[info] * -deprecation
[info] * -unchecked
[success] Total time: 0 s, completed Jan 2, 2017 11:44:44 PM
> ++2.11.8!
[info] Forcing Scala version to 2.11.8 on all projects.
[info] Reapplying settings...
[info] Set current project to Hello (in build file:/xxx/)
> show scalacOptions
[info] * -encoding
[info] * utf8
[info] * -unchecked
[success] Total time: 0 s, completed Jan 2, 2017 11:44:51 PM
```

接下来，使用这两个 key (来自 [Keys](../../api/sbt/Keys\$.html)):

```scala
val scalacOptions = taskKey[Seq[String]]("Options for the Scala compiler.")
val checksums = settingKey[Seq[String]]("The list of checksums to generate and to verify for dependencies.")
```

**注意**： `scalacOptions` 和 `checksums` 彼此无关。 它们只是两个具有相同值类型的键，其中一个是一项任务。

可以编译一个将 `build.sbt` 别名为 `checksums` `scalacOptions`，但不能以其他方式编译。 例如，这是允许的：

```scala
// The scalacOptions task may be defined in terms of the checksums setting
scalacOptions := checksums.value
```

没有**其他**方向可以走。 也就是说，setting key 不能依赖于 task key。
这是因为 setting key 仅在 subproject 加载时计算一次，因此该任务不会每次都重新运行，并且任务希望每次都重新运行。

```scala
// Bad example: The checksums setting cannot be defined in terms of the scalacOptions task!
checksums := scalacOptions.value
```

#### 定义取决于其他 setting 的 setting

在执行时间方面，我们可以将 setting 视为在加载期间评估的特殊任务。

考虑将 subproject 组织定义为与项目名称相同。

```scala
// name our organization after our project (both are SettingKey[String])
organization := name.value
```

Here's a realistic example.
This rewires `Compile / scalaSource` key to a different directory
only when `scalaBinaryVersion` is `"2.11"`.

```scala
Compile / scalaSource := {
  val old = (Compile / scalaSource).value
  scalaBinaryVersion.value match {
    case "2.11" => baseDirectory.value / "src-2.11" / "main" / "scala"
    case _      => old
  }
}
```

### build.sbt DSL 的意义是什么？

build.sbt DSL 是一种领域特定语言，用于构建设置和任务的 DAG。 setting 式对 setting，任务及其之间的依赖关系进行编码。

这种结构在 [Make][Make] (1976)，[Ant][Ant] (2000)，和 [Rake][Rake] (2003) 中很常见。

#### Make 简介

基本的 Makefile 语法如下所示：

```
target: dependencies
[tab] system command1
[tab] system command2
```

给定一个目标（默认目标名为 `all`），

1. Make 检查目标的依赖项是否已构建，并构建尚未构建的任何依赖项。
2. Make 按顺序运行系统命令。

让我们看一下 `Makefile`：

```
CC=g++
CFLAGS=-Wall

all: hello

hello: main.o hello.o
    \$(CC) main.o hello.o -o hello

%.o: %.cpp
    \$(CC) \$(CFLAGS) -c \$< -o \$@
```

运行 `make`，默认情况下它将选择名为 `all` 的目标。
目标将 `hello` 作为其依赖项列出，但尚未建立，因此 Make 将建立 `hello` 。

接下来，Make 检查是否已经建立了 `hello` 目标的依赖关系。
`hello` 列出了两个目标： `main.o` 和 `hello.o`。
一旦使用最后一个模式匹配规则创建了这些目标，
只有执行系统命令，才能将 `main.o` 和 `hello.o` 链接到 `hello`。

如果您只是运行 `make`，则可以专注于作为目标的目标，
并且 `Make` 会确定构建中间产品所需的确切时间和命令。
我们可以将其视为面向依赖的编程或基于 flow-based 编程。
`Make` 实际上被认为是混合系统，因为虽然 DSL 描述了任务相关性，但操作被委派给系统命令。

#### Rake

对于 Make 后继者（例如Ant，Rake 和 sbt），这种混合状态仍在继续。
看一下 Rakefile 的基本语法：

```ruby
task name: [:prereq1, :prereq2] do |t|
  # actions (may reference prereq as t.name etc)
end
```

Rake 的突破之处在于它使用一种编程语言来描述操作而不是系统命令。

#### 基于混合 flow-based 编程的好处

以这种方式组织构建有多种动机。

首先是重复数据删除。 使用基于 flow-based 编程，即使一个任务由多个任务依赖，它也只能执行一次。
例如，即使沿着任务图的多个任务依赖 `Compile / compile` 也将只执行一次。

第二是并行处理。 使用任务图，任务引擎可以并行调度互不相关的任务。

第三是关注点和灵活性的分离。 任务图使构建用户可以以不同的方式将任务连接在一起，而 sbt 和插件可以提供各种功能（例如，编译和库依赖管理）作为可重复使用的功能。

### 摘要

构建定义的核心数据结构是任务的DAG，其中边缘表示 happens-before 关系。
`build.sbt` 是一种 DSL，旨在表达面向依赖的程序或基于 flow-based 程序，类似于 `Makefile` 和 `Rakefile`。

基于 flow-based 编程的主要动机是重复数据删除，并行处理和可定制性。

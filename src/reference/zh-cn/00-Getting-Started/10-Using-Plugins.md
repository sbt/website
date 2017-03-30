---
out: Using-Plugins.html
---

  [Basic-Def]: Basic-Def.html
  [Library-Dependencies]: Library-Dependencies.html
  [Multi-Project]: Multi-Project.html
  [global-vs-local-plugins]: ../docs/Best-Practices.html#global-vs-local-plugins
  [Community-Plugins]: ../docs/Community-Plugins.html
  [Plugins]: ../docs/Plugins.html
  [Plugins-Best-Practices]: ../docs/Plugins-Best-Practices.html

使用插件
-------------

请先阅读入门指南前面的内容，尤其需要在阅读本小节之前理解 [build.sbt][Basic-Def] 和 [库依赖][Library-Dependencies]。

### 什么是插件

插件继承了构建定义，大多数通常是通过添加设置。新的设置可以是新的 task。例如，一个插件可以添加一个 `codeCoverage` task 来生成一个测试覆盖率报告。

### 声明一个插件

如果你的项目在 `hello` 目录下，而且你正在往构建定义中添加一个 sbt-site 插件，创建 `hello/project/site.sbt` 并且通过传递插件的 Ivy 模块 ID 声明插件依赖 给 `addSbtPlugin`：

```scala
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.7.0")
```

如果你添加 sbt-assembly，像下面这样创建 `hello/project/assembly.sbt` ：

```scala
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.2")
```

不是所有的插件都在同一个默认的仓库中，而且一个插件的文档会指导你添加能够找到它的仓库：

```scala
resolvers += Resolver.sonatypeRepo("public")
```

插件通常提供设置将它添加到项目并且开启插件功能。这些将在下一小节描述。

### 启用和禁用自动插件

一个插件能够声明它自己的设置被自动添加到构建定义中去，在这种情况下你不需要为添加它做任何事情。

作为 0.13.5 版本的 sbt，有一个新的特性叫[自动插件][Plugins]，它能够在自动的、安全的、确保所有依赖都在项目里的前提下开启插件。很多自动插件应该能够自动开启，然而有些却需要显式开启。

如果你正在使用一个需要显示开启的自动插件，那么你需要添加这样的代码到你的 `build.sbt` 文件：

```scala
lazy val util = (project in file("util"))
  .enablePlugins(FooPlugin, BarPlugin)
  .settings(
    name := "hello-util"
  )
```

`enablePlugins` 方法允许项目显式定义它们需要使用的自动插件。

项目也可以使用 `disablePlugins` 方法排除掉一些插件。例如，如果我们希望能够从 `util` 中移除 `IvyPlugin` 插件的设置，我们将 `build.sbt` 修改如下：

```scala
lazy val util = (project in file("util"))
  .enablePlugins(FooPlugin, BarPlugin)
  .disablePlugins(plugins.IvyPlugin)
  .settings(
    name := "hello-util"
  )
```

自动插件会在文档中说明是否需要显示的开启。如果你对一个项目中开启了哪些插件好奇，只需要在 sbt 命令行中执行 `plugins` 命令。

例如：

```
> plugins
In file:/home/jsuereth/projects/sbt/test-ivy-issues/
        sbt.plugins.IvyPlugin: enabled in scala-sbt-org
        sbt.plugins.JvmPlugin: enabled in scala-sbt-org
        sbt.plugins.CorePlugin: enabled in scala-sbt-org
        sbt.plugins.JUnitXmlReportPlugin: enabled in scala-sbt-org
```

这里， `plugins` 的输出显示 sbt 默认的插件都被开启了。sbt 默认的设置通过3个插件提供：

1.  `CorePlugin`: 提供对 task 的核心并行控制。
2.  `IvyPlugin`: 提供发布、解析模块的机制。
3.  `JvmPlugin`: 提供编译、测试、执行、打包 Java/Scala 项目的机制。

另外，`JUnitXmlReportPlugin` 提供对生成 junit-xml 的试验性支持。

老的非自动的插件通常需要显示的添加设置，以致于[多项目构建][Multi-Project]可以有不同的项目类型。插件的文档会指出如何配置它，但是特别是对于老的插件，这包含添加对插件必要的基本设置和自定义。

例如，对于 sbt-site 插件，为了在项目中开启它，需要创建包含如下内容的 `site.sbt` 文件来。

```scala
site.settings
```

如果构建定义了多个项目，往项目中直接添加如下内容替而代之：

```scala
// 在 `util` 项目中不使用 site 插件
lazy val util = (project in file("util"))

// 在`core` 项目中开启 site 插件
lazy val core = (project in file("core"))
  .settings(site.settings)
```

### 全局插件

可以一次给所有项目安装插件，只要在 `$global_plugins_base$` 中声明它们。`$global_plugins_base$` 是一个将自己的 classpath 导出给所有项目的 sbt 构建定义。
概略地讲，在 `$global_plugins_base$` 中的任何 `.sbt` 或者 `.scala` 文件就和所有项目的 `project/` 目录下的一样。

为了一次给所有的项目添加插件，你可以创建 `$global_plugins_base$/build.sbt` 并且添加 `addSbtPlugin()` 表达式。因为这样做会增加机器上的依赖，所以这个特性应该少用。
参见[最佳实践][global-vs-local-plugins]。

### 可用的插件

这里有一个可用的[插件列表][Community-Plugins]。

一些特别流行的插件如下：

-   对 IDE 的支持（为了将 sbt 项目导入到 IDE）
-   对 web 框架的支持，例如[xsbt-web-plugin](https://github.com/earldouglas/xsbt-web-plugin)。

更多详细信息，包含开发插件的方法，参见[插件][Plugins]。
关于最佳实践，参见[插件最佳实践][Plugins-Best-Practices]。

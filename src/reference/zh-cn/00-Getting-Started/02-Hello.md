---
out: Hello.html
---

  [Basic-Def]: Basic-Def.html
  [Setup]: Setup.html

Hello, World
------------

这一小节假设你已经 [安装 sbt][Setup] 了。

### 创建一个有源码的项目目录

一个合法的 sbt 项目可以是一个包含单个源码文件的目录。
尝试创建一个 `hello` 目录，包含内容如下的源码文件 `hw.scala`：

```scala
object Hi {
  def main(args: Array[String]) = println("Hi!")
}
```

现在在 `hello` 目录下启动 sbt，然后执行 `run` 命令进入到 sbt 的交互式命令行。在 Linux 或者 OS X 上的命令可能是这样：

```
\$ mkdir hello
\$ cd hello
\$ echo 'object Hi { def main(args: Array[String]) = println("Hi!") }' > hw.scala
\$ sbt
...
> run
...
Hi!
```

在这个例子中，sbt 完全按照约定工作。sbt 将会自动找到以下内容：

-   项目根目录下的源文件
-   `src/main/scala` 或 `src/main/java` 中的源文件
-   `src/test/scala` 或 `src/test/java` 中的测试文件
-   `src/main/resources` 或 `src/test/resources` 中的数据文件
-   `lib` 中的 jar 文件

默认情况下，sbt 会用和启动自身相同版本的 Scala 来构建项目。
你可以通过执行 `sbt run` 来运行项目或者通过 `sbt console` 进入 [Scala REPL](http://www.scala-lang.org/node/2097)。`sbt console` 已经帮你
设置好项目的 classpath，所以你可以根据项目的代码尝试实际的 Scala 示例。

### 构建定义

大多数项目需要一些手动设置。基本的构建设置都放在项目根目录的 `build.sbt` 文件里。
例如，如果你的项目放在 `hello` 下，在 `hello/build.sbt` 中可以这样写：

```scala
lazy val root = (project in file("."))
  .settings(
    name := "hello",
    version := "1.0",
    scalaVersion := "$example_scala_version$"
  )
```

在 [.sbt 构建定义][Basic-Def] 这节中你将会学到更多关于
如何编写 `build.sbt` 脚本的东西。

如果你准备将你的项目打包成一个 jar 包，在 `build.sbt` 中至少要写上 name 和 version。

### 设置 sbt 版本

你可以通过创建 `hello/project/build.properties` 文件强制指定一个版本的 sbt。在这个文件里，编写如下内容来强制使用 $app_version$：

```
sbt.version=$app_version$
```

sbt 在不同的 release 版本中是 99% 兼容的。但是在 `project/build.properties` 文件中设置 sbt 的版本仍然能避免一些潜在的混淆。

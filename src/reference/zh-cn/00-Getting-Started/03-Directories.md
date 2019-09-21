---
out: Directories.html
---

  [Hello]: Hello.html
  [Setup]: Setup.html
  [Organizing-Build]: Organizing-Build.html

目录结构
-------------------

这一小节假设你已经 [安装 sbt][Setup] 并且已经阅读过 [Hello, World][Hello] 了。

### 基础目录

在 sbt 的术语里，“基础目录”是包含项目的目录。所以，如果你创建了一个和 [Hello, World][Hello] 一样的项目 `hello` ，包含 `hello/build.sbt` 和 `hello/hw.scala`， `hello` 就是基础目录。

### 源代码

源代码可以像 `hello/hw.scala` 一样的放在项目的基础目录中。然而，大多数人不会在真实的项目中这样做，因为太杂乱了。
sbt 和 [Maven](https://maven.apache.org/) 的默认的源文件的目录结构是一样的（所有的路径都是相对于基础目录的）：

```
src/
  main/
    resources/
       <files to include in main jar here>
    scala/
       <main Scala sources>
    scala-2.12/
       <main Scala 2.12 specific sources>
    java/
       <main Java sources>
  test/
    resources
       <files to include in test jar here>
    scala/
       <test Scala sources>
    scala-2.12/
       <test Scala 2.12 specific sources>
    java/
       <test Java sources>
```

`src/` 中其他的目录将被忽略。而且，所有的隐藏目录也会被忽略。

### sbt 构建定义文件

你已经在项目的基础目录中看到了 `build.sbt`。其他的 sbt 文件在 `project` 子目录中。
`project` 目录可以包含 `.scala` 文件，这些文件最后会和 `.sbt` 文件合并共同构成完整的构建定义。想知道更多请参见 [组织构建][Organizing-Build]。

```
build.sbt
project/
  Build.scala
```

你可能在 `project/` 中也看到了 `.sbt` 文件，但是它不等同于项目基础目录中的 `.sbt` 文件。这将在 [稍后][Organizing-Build] 解释，因为首先你需要一些背景知识。

### 构建产品

构建出来的文件（编译的 classes，打包的 jars，托管文件，caches 和文档）默认写在 `target` 目录中。

### 配置版本管理

你的 `.gitignore` 文件（或者其他版本控制系统等同的文件）应该包含：

```
target/
```

注意：这里后面需要跟一个 `/` （只匹配目录）且前面不能有 `/` （除了匹配普通的 `target/` 还匹配 `project/target/` ）。
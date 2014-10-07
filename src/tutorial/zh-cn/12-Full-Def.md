---
out: Full-Def.html
---

  [Basic-Def]: Basic-Def.html
  [More-About-Settings]: More-About-Settings.html
  [Using-Plugins]: Using-Plugins.html

.scala 构建定义
-----------------------

本小节假设你已经阅读了之前的章节，尤其是 [.sbt构建定义][[Basic-Def]]和[更多关于设置][More-About-Settings].

### sbt是递归的

`build.sbt` 很简答，隐藏了sbt是如何工作的. sbt建立是用scala代码定义的。代码本身是必须被建立的。有比.sbt更好的建立方式么？



`project` 目录 *另一个工程的项目* 知道如何建立一个工程。在`project`内部的项目能做任何其他项目可以做的事情. *你的构建定义是一个sbt 项目*

递归可以继续下去.如果你喜欢, 你可以稍稍调整
项目的构建定义, 比如创建
`project/project/`目录.

下面是一个例子：
```
hello/                  # 项目的基目录

    Hello.scala         # 一个项目源文件


    build.sbt           # build.sbt 是project/ 中构建定义的一部分。

    project/            # 构建定义项目的基目录

        Build.scala     # 构建定义源文件

        build.sbt       # 项目构建定义的一部分


        project/        # 构建定义项目的基目录

            Build.scala 
```
不用担心！大部分时候不需要`project/project/`目录。但是理解它是有帮助的。

文件以`.scala` or `.sbt`结尾，一般命名`build.sbt` and `Build.scala`。实际上，这只是一个惯例，更多文件是允许的。

### `.scala` 源文件在构建定义项目

`.sbt` 文件被融入它的兄弟目录. 再次看项目布局：
```
hello/                  # 项目的基目录

    build.sbt           # build.sbt 是project/ 中构建定义的一部分。

    project/            # 构建定义项目的基目录

        Build.scala     # 构建定义源文件

```
在build.sbt中的scala表达式被延续编译，和`Build.scala`融入在一起。（和其他在`project/`目录下的`.scala` 文件)

在基目录中的`*.sbt` 文件变成项目构建定义的一部分。

`.sbt`文件形式，对于在构建定义项目中增加设定，是一种方便的缩写


### 关联 build.sbt 和 Build.scala

为了融合`.sbt` 和 `.scala`文件在你的构建定义中，你需要理解他们是如何关联的。

下面两个文件描述了这个过程。第一，如果你的项目在hello文件夹内，创建`hello/project/Build.scala`：
```scala
import sbt._
import Keys._

object HelloBuild extends Build {
  val sampleKeyA = settingKey[String]("demo key A")
  val sampleKeyB = settingKey[String]("demo key B")
  val sampleKeyC = settingKey[String]("demo key C")
  val sampleKeyD = settingKey[String]("demo key D")

  override lazy val settings = super.settings ++
    Seq(
      sampleKeyA := "A: in Build.settings in Build.scala",
      resolvers := Seq()
    )

  lazy val root = Project(id = "hello",
    base = file("."),
    settings = Seq(
      sampleKeyB := "B: in the root project settings in Build.scala"
    ))
}
```

现在，创建`hello/build.sbt`:

```scala
sampleKeyC in ThisBuild := "C: in build.sbt scoped to ThisBuild"

sampleKeyD := "D: in build.sbt"
```

打开sbt终端命令窗口. 敲入 `inspect sampleKeyA`，你将会看到:

```
[info] Setting: java.lang.String = A: in Build.settings in Build.scala
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}/*:sampleKeyA
```

然后敲入 `inspect sampleKeyC`，你将会看到:

```
[info] Setting: java.lang.String = C: in build.sbt scoped to ThisBuild
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}/*:sampleKeyC
```
注意"Provided by"会显示两个value的相同作用范围。那是`.sbt`文件中的`sampleKeyC in ThisBuild`等同与放置一个设置在`Build.settings`列表，在`.scala` 文件中。sbt会抽取构建范围内的设置从
两个地方，而创建构建定义。

然后, `inspect sampleKeyB`:

```
[info] Setting: java.lang.String = B: in the root project settings in Build.scala
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}hello/*:sampleKeyB
```

注意 `sampleKeyB` 是被作用的这个项目：
`({file:/home/hp/checkout/hello/}hello)` 而不是整个构建
`({file:/home/hp/checkout/hello/})`.

你应该已经猜测到了, `inspect sampleKeyD` 等同于 `sampleKeyB`:

```
[info] Setting: java.lang.String = D: in build.sbt
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}hello/*:sampleKeyD
```



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

sbt附加设置从`.sbt`文件到`Build.settings`和`Project.setting`的设置。另句话说，`.sbt`设置拥有优先权。
尝试改变`Build.scala`，是的它能设置健值`sanokeC` 或者`sampleD`，
他们已经在`build.sbt`设置过。`build.sbt`中的设置应该能赢过`Build.sbt`。

另一要点应该被注意到：`sampleKeyC`和`sampleKeyD`是可以在`build.sbt`内部获得的。这是因为sbt引入`Build`中的内容到`.sbt`文件中。在这种情况下，`import HelloBuild._`是被隐含引入在`build.sbt`文件中。

总结：
- 在`.scala`文件中，可以在`Build.settings`中增加设置，会自动增加构建作用域。
- 在`.scala`文家中，可以在`Project.settings`中增加设置，会自动增加构建作用域。
- 任何在`.scala`中的`Build`对象将会把它的内容倒入到`.sbt`文件中。
- 在`.sbt`文件中的设置被`依附`到`.scala`中的设置。
- 在`.sbt`文家中的设置是在项目作用域的除非你指定它在其他域。


### 何时用`.scala`文件

在`.scala`文件,你可以写任意的scala代码, 包括顶层的类和对象. 另外, 它没有对空白行的限制，
因为它是一个`.scala`文件。
推荐的方法是定义设置在`.sbt`文件中，用`.scala`文件用于任务实现，或者共享键值在`.sbt`文件中。


### 命令窗口构建定义项目

又可以转换sbt 命令模式，为了有构建定义项目在`project/`目录内，作当前的项目。可以敲`plugins`来实现。

```
> reload plugins
[info] Set current project to default-a0e8e4 (in build file:/home/hp/checkout/hello/project/)
> show sources
[info] ArrayBuffer(/home/hp/checkout/hello/project/Build.scala)
> reload return
[info] Loading project definition from /home/hp/checkout/hello/project
[info] Set current project to hello (in build file:/home/hp/checkout/hello/)
> show sources
[info] ArrayBuffer(/home/hp/checkout/hello/hw.scala)
>
```
你可以用`reload return` 离开构建定义项目，回到你的项目。

### 提醒：总是不可改变的。

It would be wrong to think that the settings in `build.sbt` are added to
the `settings` fields in `Build` and `Project` objects. Instead, the `settings`
list from `Build` and `Project`, and the settings from `build.sbt`, are
concatenated into another immutable list which is then used by sbt. The
`Build` and `Project` objects are "immutable configuration" forming only
part of the complete build definition.

这将是错误的认为，在` build.sbt`的设置将被添加到在` Build`和`Project`对象中的 `settings`领域。相反，`Build`和` Project`中的 settings`列 ，以及` build.sbt`设置列表，被
串连到另一个不可变的列表中，然后用SBT 。该
` Build`和` Project`对象是“不可改变的配置”形成
完整的构建定义的一部分

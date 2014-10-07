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




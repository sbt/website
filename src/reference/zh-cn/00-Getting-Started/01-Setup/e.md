---
out: Activator-Installation.html
---

  [Manual-Installation]: Manual-Installation.html

安装 Typesafe Activator (包含sbt)
---------------------

Typesafe Activator 是sbt的一个自定义版本，它添加两个额外的命令`activator ui`和`activator new`。`activator`命令简言之就是sbt的一个超集。

你可以从 [typesafe.com](http://typesafe.com/platform/getstarted)获得Activator。

If you see a command line such as `sbt ~test` in the documentation, you will also be able to type `activator ~test`. Any Activator project can be opened in sbt and vice versa because Activator is "sbt powered."

如果你在文档中看到一个命令行如`sbt ~test`，你也将可以键入`activator ~test`。任意一个Activator工程能都可以用sbt打开，反之亦然，因为Activator是"基于sbt"的。

Activator下载包括`activator`脚本和一个`activator-launch.jar`，分别相当于[手动安装][Manual-Installation]所述的sbt脚本和启动jar。这里Activator和一个[手动安装][Manual-Installation]的sbt之间的差异如下：

 * 键入不带参数的`activator`将尝试猜测是否进入`activator shell`或`activator ui`模式；键入`activator shell`来明确进入命令行提示符。
 * `activator new`允许你从一个大的[项目模板目录](https://typesafe.com/activator/templates)中创建项目，例如`play-scala`模板是一个[Play Framework](http://playframework.com)Scala应用程序骨架
 * `activator ui`启动一种快速启动用户界面，可用于从模板目录浏览教程（在目录中的许多模板有陪同教程）。

Activator提供两种下载；小的"minimal"下载包只包含包装脚本和启动jar，而大的"full"下载包含预装的Ivy缓存，包括Scala，Akka和Play框架。
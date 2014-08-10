---
out: Basic-Def.html
---

  [More-About-Settings]: More-About-Settings.html
  [Full-Def]: Full-Def.html
  [Running]: Running.html
  [Library-Dependencies]: Library-Dependencies.html
  [Input-Tasks]: ../docs/Input-Tasks.html

.sbt 构建定义
---------------------

这一小节描述 sbt 构建定义，包含一些“理论”和 `build.sbt` 的语法。假设你已经知道如何 [使用 sbt][Running] 并且阅读过入门指南前面的几小节。

### `.sbt` vs `.scala` 构建定义

一个 sbt 构建定义可以包含项目基目录中以 `.sbt` 结尾的文件，也可以包含项目基目录下 `project/` 子目录中以 `.scala` 结尾的文件。

这一小节将讨论适用于绝大多数场景的 `.sbt` 文件。`.scala` 文件通常适用于两种场景，一是在 `.sbt` 文件之间共享代码，二是用于更加复杂的构建定义。参见 [.scala 构建定义][Full-Def] （在入门指南的后面）了解
更多关于 `.scala` 文件的内容。

### 什么是构建定义？

sbt 在检查项目和处理构建定义文件之后，形成一个不可变的映射表（immutable map）（一些键值对的集合）来描述构建。

例如，一个 key 为 `name` 映射到一个字符串 value 作为项目名称的。

*构建定义文件不会直接影响 sbt 的 map。*

替而代之的是，构建定义会创建一个庞大的类型为 `Setting[T]` 的对象列表，`T` 是映射表中值（value）的类型。一个 `Setting` 描述的是一次 *对映射表（map）的转换*，
像增加一个新的键值对或者附加到一个已经存在的 value 上。（在函数式编程的使用不可变数据结构、值，在 map 上做一次转换后返回一个新的 map 的宗旨下，它不会现场更新旧的 map。）

在 `build.sbt` 中，你可以想这样为项目名称创建一个 `Setting[String]`：

```scala
name := "hello"
```

这个 `Setting[String]` 会通过增加（或着替换） name 作为 key，并给 value 赋值为 `"hello"` 对 map 做一次转换。转换后的 map 成为 sbt 新的 map。

为了创建这个 map，sbt 会先对所有的设置的列表进行排序，这样的话能将对同一个 key 的改变一起操作，而且如果 value 依赖于其他的 key，会先处理其他被依赖的 key。
然后， sbt 会对 `Settings` 排好序的列表进行遍历，把每一项都按顺序应用到 map 中。

总结：一个构建定义为一个类型为 `Setting[T]` 的列表，`Setting[T]` 是会影响到 sbt 保存键值对的 map 的一种转换，`T` 是每一个 value 的类型。

### 如何在 build.sbt 中定义设置

`build.sbt` 定义了一个 `Seq[Setting[_]]`；它是一个以空行分隔的 Scala 表达式的列表，每一个表达式都会成为序列 （sequence）中的一个元素。
如果你在 `.sbt` 文件的开头写上 `Seq(`，在末尾写上 `)`，并且将每一个空行替换成为一个逗号，你会看到和 `.scala` 中等效的代码。

下面是一个例子：

```scala
name := "hello"

version := "1.0"

scalaVersion := "2.10.3"
```

每一项 `Setting` 都定义为一个 Scala 表达式。在 `build.sbt` 中的表达式是相互独立的，而且它们仅仅是表达式，不是完整的 Scala 语句。这些表达式可以用 `val`，`lazy val`，`def` 声明。
`build.sbt` 不允许使用顶层的 `object` 和`class`。它们必须写到 `project/` 目录下的完整的 Scala 源文件中。

在左边，`name`， `version` 和 `scalaVersion` 都是 *键（keys）*。一个键（key）就是一个 `SettingKey[T]`，`TaskKey[T]` 或者 `InputKey[T]` 的实例，`T` 是期待的 value 的类型。
key 的类别将在下面讲解。

键（Keys）有一个返回 `Setting[T]` 的 `:=` 方法。你可以像使用 Java 的语法一样调用该方法：

```scala
name.:=("hello")
```

但是，Scala 允许这样 `name := "hello"` 调用（在 Scala 中，一个只有单个参数的方法可以使用任何一种语法调用）。

键（key）`name` 上的 `:=` 方法会返回一个 `Setting`，在这里特指 `Setting[String]`。`String` 也出现在 `name` 自身的类型 `SettingKey[String]` 中。
在这个例子中，返回的 `Setting[String]` 是一个在 sbt 的 map 中增加或者替换键为 `name` 的转换，赋值为 `"hello"`。

如果你使用了错误类型的 value，构建定义会编译不通过：

```scala
name := 42  // 编译不通过
```

### 设置必须以空行分隔

你不能像这样编写 build.sbt： 

```scala
// 编译不通过，没有空行
name := "hello"
version := "1.0"
scalaVersion := "2.10.3"
```

sbt 需要一种分隔符来告诉它一个表达式在哪里结束，下一个表达式从哪里开始。

`.sbt` 文件包含了一个 Scala 表达式列表，不是一个单独的 Scala 程序。这些表达式会被分开然后分别传递给编译器。

### 键（Keys）

#### 类型（Types）

有三种类型的 key：

- `SettingKey[T]`: 一个 key 对应一个只计算一次的 value（这个值在加载项目的时候计算，然后一直保存着）。
- `TaskKey[T]`: 一个 key 对应一个称之为 *task* 的 value，每次都会重新计算，可能存在潜在的副作用。
- `InputKey[T]`: 一个可以对应一个可以接收命令行参数的 task。详细内容参见 [Input Tasks][Input-Tasks]。

#### 内置的 Keys

内置的 keys 实际上是对象 [Keys](../sxr/sbt/Keys.scala.html) 的字段。一个 `build.sbt` 会隐式包含 `import sbt.Keys._`，所以可以通过 `name` 取到 `sbt.Keys.name`。

#### 自定义 Keys

可以通过它们各自的创建方法：`settingKey`，`taskKey` 和 `inputKey` 自定义 keys。每个方法都期待 key 和 value 的类型已经一段描述。
key 的名称取自于 `val` 变量的变量名。例如，给一个新的 task `hello` 定义一个 key，

```scala
lazy val hello = taskKey[Unit]("一个 task 示例")
```

这里我们用事实说明了 `.sbt` 文件除了可以包含设置（settings），还可以包含 `val`s 和 `def`s。所有这些定义都会在设置（settings）之前被计算而跟它们在文件里定义的位置无关。
`val`s 和 `def`s 必须以空行和设置（settings）分隔。

> **注意：** 通常，使用 lazy val 而不是 val 可以避免初始化时顺序的问题。

#### Task vs Setting keys

`TaskKey[T]` 是用来定义 *task* 的。Tasks 就是像 `compile` 或者 `package` 这样的操作。它们可能返回 `Unit`（`Unit` 在 Scala 中表示 `void`），或者可能返回 task 的返回值，
例如 `package` 就是一个类型为 `TaskKey[File]` 的 task 它的返回值是它生成的 jar 文件。

每当你执行一个 task，例如在 sbt 命令行中输入 `compile`，sbt 只会将涉及到的每个 task 执行一次。

sbt 描述项目的 map 会将设置（setting）保存为固定的字符串，比如像 name；但是它不得不保存一个 task 的可执行的代码，比如像 `compile` -- 即使这段可执行的代码最终返回一个字符串，它也会每次都重新执行。

*一个给定的 key 总是指一个 task 或者 一个普通的设置（setting）。* 也就是说，"taskiness" (无论是否每次都重新执行）是 key 的一个属性（property），而不是一个值（value）。

### 定义 tasks 和 settings

你可以使用 `:=` 给一个 setting 赋一个值或者给一个 task 赋一种计算。对于 setting，这个值 value 只会在项目加载的时候执行一次。对于 task，这个计算会在 task 每次执行的时候重新计算。

例如，实现前面一部分中的 `hello` task：

```scala
hello := { println("Hello!") }
```

我们已经在定义项目名称时见过定义 settings 的例子，

```scala
name := "hello"
```

#### Tasks 和 Settings 的类型

从类型系统的角度来讲，通过 task key 创建的 `Setting` 和通过 setting key 创建的 `Setting` 有稍微不同。`taskKey := 42` 的类型是 `Setting[Task[T]]` 而 `settingKey := 42`
的类型是 `Setting[T]`。这对于绝大多数情况并无影响；task key 在执行的时候仍然创建一个类型为 `T` 的 value。

`T` 类型和 `Task[T]` 类型的不同的含义是：一个 setting 不能依赖一个 task，因为一个 setting 只会在项目加载的时候计算一次，不会重新计算。更多关于 [更多关于设置][More-About-Settings] 的内容很快就会讲到。

### sbt 交互模式中的 Keys

在 sbt 的交互模式下，你可以输入任何 task 的 name 来执行该 task。这就是为什么输入 `compile` 就是执行 `compile` task。`compile` 就是该 task 的 key。

如果你输入的是一个 setting key 的 name 而不是一个 task key 的 name，setting key 的值（value）会显示出来。输入一个 task key 的 name 会执行该 task 但是不会显示执行结果的值（value）；输入 `show <task name>` 而不是
简单的 `<task name>` 可以看到该 task 的执行结果。对于 key name 的一个约定就是使用 `camelCase`，这样命令行里的 name 和 Scala 的标识符就一样了。

了解更多关于任何 key 内容，可以在 sbt 交互模式的命令行里输入 `inspect <keyname>`。虽然 `inspect` 显示的一些信息没有意义，但是在顶部会显示 setting 的 value 的类型和 setting 的简介。

### build.sbt 中的引入

你可以将引入语句放在 `build.sbt` 的顶部；它们可以不用空行分隔。

下面是一些默认的引入：

```scala
import sbt._
import Process._
import Keys._
```

（另外，如果你有 [.scala 文件][Full-Def]，这些文件中任何 `Build` 或者 `Plugin` 对象你的内容都会被引入。更多关于这些的内容放在 [.scala 构建定义][Full-Def]。）

### 添加依赖库

有两种方式添加第三方的依赖。一种是将 jar 文件 放入 `lib/`（非托管的依赖）中，另一种是添加托管的依赖，在 `build.sbt` 中像这样：

```scala
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3"
```

就是像这样添加版本为 10.4.1.3 的 Apache Derby 库作为依赖。

key `libraryDependencies` 包含两个方面的复杂性：`+=` 方法而不是 `:=`，第二个就是 `%` 方法。`+=` 方法是将新的值附加该 key 的旧值后面而不是替换它，这将在 
[更多关于设置][More-About-Settings] 中介绍。`%` 方法是用来从字符串构造 Ivy 模块 ID 的，将在 [库依赖][Library-Dependencies] 中介绍。

目前我们跳过了库依赖的一些细节，直到入门指南的后面部分。后面有一节 [库依赖][Library-Dependencies] 来介绍这些内容。
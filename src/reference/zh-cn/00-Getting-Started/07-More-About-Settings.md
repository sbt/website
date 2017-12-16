---
out: More-About-Settings.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html

更多关于设置
---------------------

这一小节将介绍除了用基本的 `:=` 方法创建设置，还有其他的方法可以创建。假设你已经阅读了 [.sbt 构建定义][Basic-Def] 和 [scope][Scopes]。

### 回顾：设置

还记得在 [.sbt 构建定义][Basic-Def] 中，一个构建定义创建了一个 `Setting` 列表，然后这些 `Setting` 被用来对 sbt 的构建描述做转换（它是一个保存键值对的 map）。一个 Setting 就是将 sbt 之前的 map 作为输入并且输出一个新的 map 的转换。
这个新的 map 就是 sbt 的新状态。

不同 setting 通过不同的方式对该 map 进行转换。之前在 [.sbt 构建定义][Basic-Def] 中，你已经阅读了 `:=` 方法相关的内容。

通过 `:=` 创建的 `Setting` 会往转换之后新的 map 中放入一个固定的常量。例如，如果你通过 `name := "hello"` 对 map 做一次转换，新的 map 中 key `name` 就保存着一个字符串 `"hello"`。

### 追加值： `+=` 和 `++=`

通过 `:=` 方法赋值是最简单的转换，但是 key 也有很多其他的方法。如果 `SettingKey[T]` 中的 `T` 是一个列表，例如，一个 key 的值的类型是 sequence，你就可以往列表中追加而不是替换。

- `+=` 会追加单个元素到列表中。
- `++=` 会连接两个列表。

例如，一个 key `sourceDirectories in Compile` 的值是 `Seq[File]`。默认情况下该 key 的值会包含 `src/main/scala`。如果你也想编译叫做 source 的目录下的源代码（因为你不得不成为非标准的），你可以添加该目录：

```scala
sourceDirectories in Compile += new File("source")
```

或者，遵循约定使用 sbt 包中的 `file()` 函数：

```scala
sourceDirectories in Compile += file("source")
```

（`file()` 只是创建了一个新的`File`。）

你可以用 `++=` 一次添加多个目录：

```scala
sourceDirectories in Compile ++= Seq(file("sources1"), file("sources2"))
```

`Seq(a, b, c, ...)` 是 Scala 用来构建列表的标准语法。

要完全替换默认的 source 目录，当然可以使用 `:=` 方法：

```scala
sourceDirectories in Compile := Seq(file("sources1"), file("sources2"))
```

### 依赖于其他 key 的值计算值

引用另一个 task 或者 setting 的值只需要调用它们各自的 value 方法。该 value 方法比较特殊而且只能在 `:=`，`+=` 或者 `++=` 方法的参数上调用。

作为第一个例子，考虑定义一个名称和 project 一样的 organization。

```scala
// name our organization after our project (both are SettingKey[String])
organization := name.value
```

或者，设置的和项目目录名称一样：

```scala
// name is a Key[String], baseDirectory is a Key[File]
// name the project after the directory it's inside
name := baseDirectory.value.getName
```

这个转换中采用 `java.io.File` 里面的标准方法 `getName` 取得了 `baseDirectory` 的值。

采用多个输入是类似的。例如，

```scala
name := "project " + name.value + " from " + organization.value + " version " + version.value
```

通过将 name 之前的值和 organization 以及 version 的值拼接起来，组成 name 的新值。

#### 包含依赖的设置

在 `name := baseDirectory.value.getName` 设置中，`name` 会 *依赖于* `baseDirectory`。如果你将上面的代码写入 `build.sbt` 中，并且启动 sbt 的交互模式，然后输入 `inspect name`，你应该看到（部分地）：

```
[info] Dependencies:
[info]  *:baseDirectory
```

这就是 sbt 知道一个 setting 如何依赖于另一个 setting。还记得一些 setting 描述了 task，所以这种方式也创建了 task 之间的依赖关系。

例如，如果你执行 `inspect compile` 你会看到它依赖了另一个 key `compileInputs`，而且如果你执行 `inspect compileInputs` 它还会依赖于其他的 key。一直追溯依赖链会有魔法发生。例如当你输入 `compile` 时，
sbt 自动执行了 `update`。它可以工作是因为 `compile` 计算需要的输入的值需要 sbt 先执行 `update` 计算。

这样，sbt 中所有的构建依赖都是 *自动的* 而不是显示声明的。如果你在另一个计算中用到了该 key 的值，那么那个计算就会依赖该 key。它就是可以工作！

#### 当设置未定义时

无论何时一个设置用 `:=`，`+=` 或者 `++=` 时依赖于自己或者另一个 key 的值，它依赖的值必须存在。如果不存在，sbt 就会抱怨。例如，它可能会说 *“引用了未定义的设置”*。
当这发生时，确认一下你使用的 key 在 [scope][Scopes] 中并且已经定义了。

在sbt中创建循环引用是可能的，这是错误的；如果你循环引用了，sbt 会告诉你。

#### 依赖于其他 key 的值的 task
  
你可以计算一些 task 或者 setting 的值来定义另一个 task 或者为另一个 task 追加值。通过使用 `Def.task` 作为`:=`， `+=` 或者 `++=`的参数可以做到。

作为第一个例子，考虑追加一个使用项目基目录和编译 classpath 的 source generator。

```scala
sourceGenerators in Compile += Def.task {
  myGenerator(baseDirectory.value, (managedClasspath in Compile).value)
}
```

#### 包含依赖的 task

在 [.sbt 构建定义][Basic-Def] 中提到过，当你通过 `:=` 或其他方法创建一个设置时，task key 创建的是 `Setting[Task[T]]` 而不是 `Setting[T]`。
Setting 可以是 Task 的输入，但 Task 不能是 Setting 的输入。

以这两个 key 为例（从 [Keys](../../api/sbt/Keys\$.html) 中）：

```scala
val scalacOptions = taskKey[Seq[String]]("Options for the Scala compiler.")
val checksums = settingKey[Seq[String]]("The list of checksums to generate and to verify for dependencies.")
```

（`scalacOptions` 和 `checksums` 互相没有关系，它们只是有相同值类型的两个 key，其中一个是 task。）

可以编译 `build.sbt` 将 `scalacOptions` 映射到 `checksums`，但是反过来不可以。例如，这样是允许的：

```scala
// scalacOptions task 会依赖 checksums setting 来定义
scalacOptions := checksums.value
```

反向的操作是 *不可能* 的。也就是说，一个 setting 的 key 不能依赖于一个 task 的 key。是因为一个 setting 的 key 只会在项目加载的时候计算一次，所以 task 不会每次都重新执行，而 task 期待每次都重新计算。

```scala
// checksums setting 不能依赖 scalacOptions task 来定义
checksums := scalacOptions.value
```

### 追加依赖：`+=` 和 `++=`

当追加到一个已经存在的 setting 或者 task 时可以使用另一些 key，就像它们可以通过 `:=` 赋值一样。例如，比方说你有一个以项目名称命名的覆盖率报告，而且你想在每次清除文件的时候都清除它：

```scala
cleanFiles += file("coverage-report-" + name.value + ".txt")
```

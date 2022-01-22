---
out: Appending-Values.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html

追加值
-----

### 追加值： `+=` 和 `++=`

通过 `:=` 方法赋值是最简单的转换，但是 key 也有很多其他的方法。如果 `SettingKey[T]` 中的 `T` 是一个列表，例如，一个 key 的值的类型是 sequence，你就可以往列表中追加而不是替换。

- `+=` 会追加单个元素到列表中。
- `++=` 会连接两个列表。

例如，一个 key `Compile / sourceDirectories` 的值是 `Seq[File]`。默认情况下该 key 的值会包含 `src/main/scala`。如果你也想编译叫做 source 的目录下的源代码（因为你不得不成为非标准的），你可以添加该目录：

```scala
Compile / sourceDirectories += new File("source")
```

或者，遵循约定使用 sbt 包中的 `file()` 函数：

```scala
Compile / sourceDirectories += file("source")
```

（`file()` 只是创建了一个新的`File`。）

你可以用 `++=` 一次添加多个目录：

```scala
Compile / sourceDirectories ++= Seq(file("sources1"), file("sources2"))
```

`Seq(a, b, c, ...)` 是 Scala 用来构建列表的标准语法。

要完全替换默认的 source 目录，当然可以使用 `:=` 方法：

```scala
Compile / sourceDirectories := Seq(file("sources1"), file("sources2"))
```

#### 当设置未定义时

无论何时一个设置用 `:=`，`+=` 或者 `++=` 时依赖于自己或者另一个 key 的值，它依赖的值必须存在。如果不存在，sbt 就会抱怨。例如，它可能会说 *“引用了未定义的设置”*。
当这发生时，确认一下你使用的 key 在 [scope][Scopes] 中并且已经定义了。

在sbt中创建循环引用是可能的，这是错误的；如果你循环引用了，sbt 会告诉你。

#### 依赖于其他 key 的值的 task

你可以计算一些 task 或者 setting 的值来定义另一个 task 或者为另一个 task 追加值。通过使用 `Def.task` 作为`:=`， `+=` 或者 `++=`的参数可以做到。

作为第一个例子，考虑追加一个使用项目基目录和编译 classpath 的 source generator。

```scala
Compile / sourceGenerators += Def.task {
  myGenerator(baseDirectory.value, (Compile / managedClasspath).value)
}
```

### 追加依赖：`+=` 和 `++=`

当追加到一个已经存在的 setting 或者 task 时可以使用另一些 key，就像它们可以通过 `:=` 赋值一样。例如，比方说你有一个以项目名称命名的覆盖率报告，而且你想在每次清除文件的时候都清除它：

```scala
cleanFiles += file("coverage-report-" + name.value + ".txt")
```

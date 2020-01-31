---
out: Scope-Delegation.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html

Scope 委托 (.value 查找)
--------------------------------

> This page was translated mostly with Google Translate. Please send a pull request to improve it.

此页面描述 scope 委托。 假定您已经阅读并理解了先前的页面
[.sbt 构建定义][Basic-Def] 和 [scopes][Scopes]。

既然我们已经涵盖了 scope 界定的所有细节，我们就可以详细解释 `.value` 查找。 如果您是第一次阅读此页面，则可以跳过本节。

总结到目前为止我们已经学到的东西：

- scope 是三个轴上的组件的元组: subproject 轴、configuration 轴、task 轴。
- 任何 scope 轴都有一个特殊的 scope 组件 `Zero`。
- 在 subproject 轴上有一个特殊的 scope 组件 `ThisBuild`。
- `Test` 扩展了 `Runtime`，而 `Runtime` 扩展了 `Compile` configuration。
- 默认情况下，放置在 build.sbt 中的 key 的 scope 为 `\${current subproject} / Zero / Zero`。
- 可以使用 `/` 运算符确定 key 的 scope。

现在，假设我们具有以下构建定义:

@@snip [build.sbt]($root$/src/sbt-test/ref/scope-delegation/x/build.sbt) { #fig1 }

在 `foo` 的 setting 主体内部，声明了对 scoped key `Test / bar` 的依赖。
但是，尽管在 `projX `中未定义 `Test / bar`，sbt 仍然能够将 `Test / bar`
解析为另一个 scoped key，导致 `foo` 初始化为 `2`。

sbt 具有定义明确的后备搜索路径，称为 **scope 委托**。
此功能使您可以在更广泛的 scope 内设置一次值，从而允许多个更特定的 scope 继承该值。

### scope 委托规则

以下是 scope 委托的规则：

- 规则1： scope 轴具有以下优先级：subproject 轴，configuration 轴，然后是 task 轴。
- 规则2：在给定 scope 的情况下，可以通过按以下顺序替换 task 轴来搜索委托 scope： 给定的 task scope，然后是 `Zero` （这是 scope 的非 task scope 版本）。
- 规则3：在给定 scope 的情况下，可以通过按以下顺序替换 configuration 轴来搜索委托 scope： 给定 configuration，其父项，其父项等等，然后 `Zero`（与无作用域的 configuration 轴相同）。
- 规则4：给定一个 scope ，通过按以下顺序替换 subproject 轴来搜索委托 scope： 给定的 subproject，`ThisBuild`，然后为 `Zero`。
- 规则5：在不携带原始上下文的情况下，评估委托 scoped key 及其相关的 settings/tasks。

我们将在本页面的其余部分中查看每个规则。

### 规则1: scope 轴优先级

- 规则1： scope 轴具有以下优先级：subproject 轴，configuration 轴，然后是 task 轴。

换句话说，给定两个作用域候选者，如果一个在 subproject 轴上具有更特定的值，则无论 configuration 或 task scope 如何，它将始终获胜。
同样，如果 subproject 相同，则无论 task scope 如何，具有更具体 configuration 值的子项目将始终获胜。
我们将看到更多定义**更具体的**规则。

### 规则2: task 轴委托

- 规则2：在给定 scope 的情况下，可以通过按以下顺序**替换** task 轴来搜索委托 scope： 给定的 task scope，然后是 `Zero` （这是 scope 的非 task scope 版本）。

对于给定 key，sbt 将如何生成委托 scope，这里有一个具体规则。 记住，我们试图显示给定任意 `(xxx / yyy).value` 的搜索路径。

**练习题 A**: 给出以下构建定义：

```scala
lazy val projA = (project in file("a"))
  .settings(
    name := {
      "foo-" + (packageBin / scalaVersion).value
    },
    scalaVersion := "2.11.11"
  )
```

`projA / name` 的值是什么?

1. `"foo-2.11.11"`
2. `"foo-$example_scala_version$"`
3. 还有什么吗

答案是 `"foo-2.11.11"`。
在 `.settings(...)` 内部，`scalaVersion` 的 scope 将自动设置为 `projA / Zero / Zero`，
因此 `packageBin / scalaVersion` 变为 `projA / Zero / packageBin / scalaVersion`。
该特定 scoped key 是未定义的。
通过使用规则2，sbt 将把 task 轴替换 `Zero` 作为 `projA / Zero / Zero` （或 `projA / scalaVersion`）。该 scoped key 定义为 `"2.11.11"`。

### 规则3：configuration 轴搜索路径

- 规则3：在给定 scope 的情况下，可以通过按以下顺序替换 configuration 轴来搜索委托 scope： 给定 configuration，其父项，其父项等等，然后 `Zero`（与无作用域的 configuration 轴相同）。

我们前面看到的例子是 `projX`：

@@snip [build.sbt]($root$/src/sbt-test/ref/scope-delegation/x/build.sbt) { #fig1 }

如果我们再次写出完整 scope，`projX / Test / Zero` 。
还记得 `Test` 扩展了 `Runtime` ，`Runtime` 扩展了 `Compile` 。

`Test / bar` 是未定义的，但是由于规则3，sbt 将查找 scope 为 `projX / Test / Zero`，
`projX / Runtime / Zero`，然后 `projX / Compile / Zero`。
找到最后一个，即 `Compile / bar`。

### 规则4：subproject 轴搜索路径

- 规则4：给定一个 scope ，通过按以下顺序替换 subproject 轴来搜索委托 scope： 给定的 subproject，`ThisBuild`，然后为 `Zero`。

**练习题 B**: 给出以下构建定义：

```scala
ThisBuild / organization := "com.example"

lazy val projB = (project in file("b"))
  .settings(
    name := "abc-" + organization.value,
    organization := "org.tempuri"
  )
```

`projB / name` 的值是什么？

1. `"abc-com.example"`
2. `"abc-org.tempuri"`
3. 还有什么吗

答案是 `abc-org.tempuri`。
因此，根据规则4，第一个搜索路径是具有 `projB / Zero / Zero` scope 的 `organization`，在 `projB` 中定义为 `"org.tempuri"`。
它的优先级高于构建级别 setting `ThisBuild / organization`。

#### scope 轴优先级再次

**练习题 C**: 给出以下构建定义：

@@snip [build.sbt]($root$/src/sbt-test/ref/scope-delegation/x/build.sbt) { #fig_c }

`projC / name` 值是什么？

1. `"foo-2.12.2"`
2. `"foo-2.11.11"`
3. 还有什么吗

答案是 `foo-2.11.11`。
scope 为 `projC / Zero / packageBin` 的 `scalaVersion` 未定义。


`scalaVersion` scoped to `projC / Zero / packageBin` is undefined.
规则2找到 `projC / Zero / Zero`。 规则4找到 `ThisBuild / Zero / packageBin`。
在这种情况下，规则1决定在 subproject 轴上赢得更具体的价值，这是定义为 `"2.11.11"` 的 `projC / Zero / Zero`。

**练习题 D**: 给出以下构建定义：

@@snip [build.sbt]($root$/src/sbt-test/ref/scope-delegation/x/build.sbt) { #fig_d }

如果您进行了 `projD/test` 您会看到什么？

1. `List()`
2. `List(-Ywarn-unused-import)`
3. 还有什么吗

答案是 `List(-Ywarn-unused-import)`。
规则2找到 `projD / Compile / Zero`，
规则3找到 `projD / Zero / console`，
规则4找到 `ThisBuild / Zero / Zero`。
规则1选择 `projD / Compile / Zero` 因为它具有 subproject 轴 `projD`，并且 configuration 轴的优先级高于 task 轴。

接下来， `Compile / scalacOptions` 引用 `scalacOptions.value`，我们接下来需要找到 `projD / Zero / Zero` 的委托。 规则4找到 `ThisBuild / Zero / Zero`，然后解析为 `List(-Ywarn-unused-import)`。

### inspect 命令列出委托

您可能需要快速查找正在发生的事情。
这是可以使用 `inspect` 地方。

```
sbt:projd> inspect projD / Compile / console / scalacOptions
[info] Task: scala.collection.Seq[java.lang.String]
[info] Description:
[info]  Options for the Scala compiler.
[info] Provided by:
[info]  ProjectRef(uri("file:/tmp/projd/"), "projD") / Compile / scalacOptions
[info] Defined at:
[info]  /tmp/projd/build.sbt:9
[info] Reverse dependencies:
[info]  projD / test
[info]  projD / Compile / console
[info] Delegates:
[info]  projD / Compile / console / scalacOptions
[info]  projD / Compile / scalacOptions
[info]  projD / console / scalacOptions
[info]  projD / scalacOptions
[info]  ThisBuild / Compile / console / scalacOptions
[info]  ThisBuild / Compile / scalacOptions
[info]  ThisBuild / console / scalacOptions
[info]  ThisBuild / scalacOptions
[info]  Zero / Compile / console / scalacOptions
[info]  Zero / Compile / scalacOptions
[info]  Zero / console / scalacOptions
[info]  Global / scalacOptions
```

请注意，"Provided by" 如何显示 `projD / Compile / console / scalacOptions` 提供了 `projD / Compile / scalacOptions`。
同样在 "Delegates" (委托)，按优先顺序列出了**所有**可能的委托候选人！

- 首先列出在 subproject 轴上具有 `projD` scope 的所有 scope，然后列出 `ThisBuild` 和 `Zero`。
- 在 subproject 中，首先列出在 configuration 轴上具有 `Compile` scope 的 scope，然后退回到 `Zero`。
- 首先列出在 task 轴上具有 task scope `console /` 的所有 scope，然后列出没有 task scope `console /` 的所有 scope。

### .value 查找与动态调度

- 规则5：在不携带原始上下文的情况下，评估委托 scoped key 及其相关的 settings/tasks。

请注意，scope 委托感觉类似于面向对象语言中的类继承，但是有区别。
在像 Scala 这样的 OO语言中，如果在 trait `Shape` 上有一个名为 `drawShape` 的 method，则即使 `Shape` trait 中的其他 method 使用了 `drawShape`，其子类也可以覆盖行为，这称为动态调度。

但是，在 sbt 中，scope 委托可以将 scope 委托给更通用的 scope，例如将 project-level 的 setting 委托给 build-level setting，但是该 build-level setting 不能引用 project-level setting。

**练习题 E**: 给出以下构建定义：

```scala
lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.2",
      version      := scalaVersion.value + "_0.1.0"
    )),
    name := "Hello"
  )

lazy val projE = (project in file("e"))
  .settings(
    scalaVersion := "2.11.11"
  )
```

`projE / version` 返回什么？

1. `"2.12.2_0.1.0"`
2. `"2.11.11_0.1.0"`
3. 还有什么吗

答案是 `2.12.2_0.1.0`。
`projE / version` 委托 `ThisBuild / version`，
它取决于 `ThisBuild / scalaVersion`。
因此，build-level setting 应主要限于简单的值分配。

**练习题 F**: 给出以下构建定义：

```scala
ThisBuild / scalacOptions += "-D0"
scalacOptions += "-D1"

lazy val projF = (project in file("f"))
  .settings(
    compile / scalacOptions += "-D2",
    Compile / scalacOptions += "-D3",
    Compile / compile / scalacOptions += "-D4",
    test := {
      println("bippy" + (Compile / compile / scalacOptions).value.mkString)
    }
  )
```

`projF / test` 显示什么？

1. `"bippy-D4"`
2. `"bippy-D2-D4"`
3. `"bippy-D0-D3-D4"`
4. 还有什么吗

答案是 `"bippy-D0-D3-D4"`。
这是 [Paul Phillips](https://gist.github.com/paulp/923154ab2d61882195cdea47483592ca) 最初创建的练习的变体。
这是所有规则的很好展示，因为 `someKey += "x"` 扩展为

```scala
someKey := {
  val old = someKey.value
  old :+ "x"
}
```

检索旧值将导致委托，并且由于规则5，它将转到另一个 scoped key。
让我们先摆脱 `+=`，然后为旧值注释委托：

```scala
ThisBuild / scalacOptions := {
  // Global / scalacOptions <- Rule 4
  val old = (ThisBuild / scalacOptions).value
  old :+ "-D0"
}

scalacOptions := {
  // ThisBuild / scalacOptions <- Rule 4
  val old = scalacOptions.value
  old :+ "-D1"
}

lazy val projF = (project in file("f"))
  .settings(
    compile / scalacOptions := {
      // ThisBuild / scalacOptions <- Rules 2 and 4
      val old = (compile / scalacOptions).value
      old :+ "-D2"
    },
    Compile / scalacOptions := {
      // ThisBuild / scalacOptions <- Rules 3 and 4
      val old = (Compile / scalacOptions).value
      old :+ "-D3"
    },
    Compile / compile / scalacOptions := {
      // projF / Compile / scalacOptions <- Rules 1 and 2
      val old = (Compile / compile / scalacOptions).value
      old :+ "-D4"
    },
    test := {
      println("bippy" + (Compile / compile / scalacOptions).value.mkString)
    }
  )
```

变成：

```scala
ThisBuild / scalacOptions := {
  Nil :+ "-D0"
}

scalacOptions := {
  List("-D0") :+ "-D1"
}

lazy val projF = (project in file("f"))
  .settings(
    compile / scalacOptions := List("-D0") :+ "-D2",
    Compile / scalacOptions := List("-D0") :+ "-D3",
    Compile / compile / scalacOptions := List("-D0", "-D3") :+ "-D4",
    test := {
      println("bippy" + (Compile / compile / scalacOptions).value.mkString)
    }
  )
```

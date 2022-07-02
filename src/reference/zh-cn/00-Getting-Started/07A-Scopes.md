---
out: Scopes.html
---

  [Basic-Def]: Basic-Def.html
  [More-About-Settings]: More-About-Settings.html
  [Library-Dependencies]: Library-Dependencies.html
  [Multi-Project]: Multi-Project.html
  [Inspecting-Settings]: ../docs/Inspecting-Settings.html

Scope
------

这一小节介绍 scope。假设你已经阅读并且理解了前一小节 [.sbt 构建定义][Basic-Def]。

### 关于 Key 的所有故事

[前一小节][Basic-Def] 我们认为像 `name` 的一个 key 相当于 sbt 保存键值对的 map 中的一条记录，这只是一种简化。

事实上，每一个 key 可以在多个上下文中关联一个值，每个上下文称之为 “scope”。

一些具体的例子：

- 如果在你的构建定义中有多个项目，在每个项目中同一个 key 可以有不同的值。
- 如果你想根据不同的情形编译它们，key `compile` 对于 main 源文件和 test 源文件可以有不同的值。
- Key `packageOptions`（包含创建 jar 包的一些选项）可以有不同的值，在对 class 文件打包时是 `packageBin`，对源代码打包时是 `packageSrc`。

*给定的 key `name` 没有单一的值*，因为在不同的 scope 下它的值不同。

然而，给定的 `scoped` key 的值是单一的。

如果你想起 [前面][Basic-Def] 我们讨论过的，sbt 生成一个 map 来处理描述项目的 settings 列表，这个 map 中的 key 就是 *scope 下的* key。构建定义中定义的每一个 setting（例如在 `build.sbt` 中）都有一个 scope 下的 key。

一般 scope 是隐式的或者是默认的，但是一旦默认的是错误的，你就需要在 `build.sbt` 中指定你期待的 scope。

### Scope 轴

*Scope 轴* 是一种类型，该类型的每个实例都能定义自己的 scope（也就是说，每个实例的 key 可以有自己唯一的值）。

有三种类型的 scope 轴：

- Projects
- Configurations
- Tasks

#### 通过 Project 轴划分 Scope

如果你将 [多个项目][Multi-Project] 放在同一个构建中，每个项目需要有属于自己的 settings。也就是说，keys 可以根据项目被局限在不同的上下文中。

Project 轴可以设置成构建全局的，因此一个 setting 可以应用到全局构建而不是单个项目。当一个项目没有定义项目层级的 setting 的时候，构建层级的 setting 通常作为默认的设置。

#### 通过 Configuration 轴划分 Scope

一个 *configuration* 定义一种特定的构建，可能包含它自己的 classpath，源文件和生成的包等。Configuration 的概念来自于它用来管理 [库依赖][Library-Dependencies] 的 Ivy 
和 [MavenScopes](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Scope)。

在 sbt 中你可以看到这些 configurations：

- `Compile` 定义主构建（`src/main/scala`）
- `Test` 定义如何构建测试（`src/test/scala`）
- `Runtime` 为 task `run` 定义 classpath

默认情况下，和编译、打包、运行相关的所有 key 都局限于一个 configuration，因此在不同的 configuration 中可能产生不同的效果。最明显的例子就是 task key：`compile`，`package` 和 `run`；
然而能够 *影响* 到这些 key 的所有其他 key（例如 `sourceDirectories`，`scalacOptions` 和 `fullClasspath`）也都局限于该 configuration。

#### 通过 Task 轴划分 Scope

Settings 可以影响一个 task 如何工作。例如，task `packageSrc` 就会被 setting `packageOptions` 影响。

为了支持这种特性，一个 task key（例如 `packageSrc`）可以作为另一个 key（例如 `packageOptions`）的 scope。

一些和打包相关的 task（`packageSrc`，`packageBin`，`packageDoc`）可以共享和打包相关的 key，例如 `artifactName` 和 `packageOptions`。这些 key 对于每个打包的 task 可以有唯一的值。

### 全局 Scope

每一种 scope 轴都可以用和该轴类型一致的实例代替（例如 task 轴可以用一个 task 代替），或者该轴可以被特定的值 `Global` 代替。

`Global` 的意义就是你所期待的：将 setting 的值应用到该轴所有的实例上。例如如果一个 task 轴的值是 `Global`，那么该 setting 的值将被应用到所有的 task 上。

### 代理

如果在一个 scope 中某一个 key 没有关联的值，那么该 key 就是未定义的。

对于每一个 scope，sbt 有由其他 scope 构成的替代选项的搜索路径。通常，如果一个 key 在特定的 scope 下没有关联的值，sbt 会尝试从更加一般的 scope（例如 `Global` scope 或者构建全局 scope）中获取值。

这个特性允许你一旦在更加一般的 scope 中设置了某一项设置的值之后，使得多个特定的 scope 能够继承该值。

你可以像下面这样用 `inspect` 命令查看一个 key 的替代选项的查找路径或者“代理”。往下看。

### 运行 sbt 时涉及 scope 下的 key

在命令行的交互模式下，sbt 像这样显示（和解析）scope 下的 keys：

```
{<build-uri>}<project-id>/config:intask::key
```

- `{<build-uri>}/<project-id>` 标识 project 轴。如果 project 轴有构建全局 scope，将没有 `<project-id>` 部分。
- `config` 标识 configuration 轴。
- `intask` 标识 task 轴。
- `key` 标识 scope 下的 key。

“*”号可以出现在任意轴，参考 `Global` scope。

如果你省略部分 scoped key，它会像下面这样推断：

- 如果省略 project，当前的 project 会被使用。
- 如果省略 configuration 或者 task，会自动检测 key 所有依赖的 configuration。

更多详细内容，请参见 [与 Configuration 系统交互][Inspecting-Settings]。

### 使用 scoped key 标识的例子

- `fullClasspath` 仅仅指定了一个 key，所以会使用默认的 scope：当前的 project，key 所依赖的 configuration 和全局 task 的 scope。
- `Test/fullClasspath` 指定为 configuration，所以这个 `fullClasspath` 就在 `test` configuration scope 下，其他两个 scope 轴均为默认值。
- `*:fullClasspath` 将 configuration 指定为 `Global`，而不是默认的 configuration。
- `doc::fullClasspath` 将 key `fullClasspath` 局限在 `doc` task 下，project 轴和 configuration 轴还是默认的。
- `{file:/home/hp/checkout/hello/}default-aea33a/test:fullClasspath` 指定了一个 project，在 `{file:/home/hp/checkout/hello/}default-aea33a` 中，`{file:/home/hp/checkout/hello/}` 标识 project，
而且 project id 在 `default-aea33a` 构建中。也指定了 configuration 为 `test`，但是将 task 轴留为默认的。
- `{file:/home/hp/checkout/hello/}/test:fullClasspath` 将构建为 `{file:/home/hp/checkout/hello/}` 的 project 轴设置为全局构建。
- `{.}/test:fullClasspath` 将构建为 `{.}` 的 project 轴设置为全局构建。`{.}` 可以在 Scala 代码中写成 `ThisBuild`。
- `{file:/home/hp/checkout/hello/}/compile:doc::fullClasspath` 设置了全部的三个 scope 轴。

### 审查 scope

在 sbt 的交互模式下，你可以使用 inspect 命令来理解 key 和它对应的 scope。尝试 `inspect Test/fullClasspath`，

```
\$ sbt
> inspect Test/fullClasspath
[info] Task: scala.collection.Seq[sbt.Attributed[java.io.File]]
[info] Description:
[info]  The exported classpath, consisting of build products and unmanaged and managed, internal and external dependencies.
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}default-aea33a/test:fullClasspath
[info] Dependencies:
[info]  test:exportedProducts
[info]  test:dependencyClasspath
[info] Reverse dependencies:
[info]  test:runMain
[info]  test:run
[info]  test:testLoader
[info]  test:console
[info] Delegates:
[info]  test:fullClasspath
[info]  runtime:fullClasspath
[info]  compile:fullClasspath
[info]  *:fullClasspath
[info]  {.}/test:fullClasspath
[info]  {.}/runtime:fullClasspath
[info]  {.}/compile:fullClasspath
[info]  {.}/*:fullClasspath
[info]  */test:fullClasspath
[info]  */runtime:fullClasspath
[info]  */compile:fullClasspath
[info]  */*:fullClasspath
[info] Related:
[info]  compile:fullClasspath
[info]  compile:fullClasspath(for doc)
[info]  test:fullClasspath(for doc)
[info]  runtime:fullClasspath
```

在第一行，你可以看到这是一个 task（和 [.sbt 构建定义][Basic-Def] 中介绍的 setting 相对）。该 task 得到的值的类型为 `scala.collection.Seq[sbt.Attributed[java.io.File]]`。

“Provided by” 表明定义该值的 scoped key，在这个例子中是 `{file:/home/hp/checkout/hello/}default-aea33a/test:fullClasspath`（`fullClasspath` 在 `test` configuration 中且在 `{file:/home/hp/checkout/hello/}default-aea33a` project 下）。

“Dependencies” 可能没有意义；请继续阅读 [下一节][More-About-Settings]。

你也可以看到一些代理；如果没有定义，sbt 会通过以下途径查找：

- 其他两个 configuration（`Runtime/fullClasspath` 和 `Compile/fullClasspath`）。在这些 scoped key中，project 没有指定的话就意味着是 “当前 project” 而且 task 没有指定的话就意味着是 `Global`。
- 当 project 没有指定 “当前 project” 并且 task 没有指定为 `Global` 时，configuration 会被设置成 `Global`（`*:fullClasspath`）。
- 当全局构建中没有指定特定的 project 时，project 会被设置成 `{.}` 或者 `ThisBuild`。
- 将 project 轴设置成 `Global`（`*/test:fullClasspath`）（记住，不指定 project 表示用当前的 current，所以这里查找 `Global` 是一个新的方式；例如：`*` 和 “显示没有 project” 对于 project 轴是不一样的；例如：`*/test:fullClasspath` 和 `test:fullClasspath` 不是一回事）。
- project 轴和 configuration 轴都会被设置成 `Global`（`*/*:fullClasspath`）（还记得我们已经说过不指定 task 表示用 `Global`，所以 `*/*:fullClasspath` 表示三个轴都用 `Global`）。

尝试用 `inspect fullClasspath`（和上面例子中的 inspect `test:fullClasspath` 相对）来查看它们的不同。因为 configuration 被省略了，sbt 自动检测并设置为 `compile`。
因此 `inspect Compile/fullClasspath` 得到的结果看起来应该和 `inspect fullClasspath` 得到的结果一样。

尝试用 `inspect *:fullClasspath` 作为对比。默认情况下，`fullClasspath` 没有定义在 `Global` configuration 中。

更多详细的内容请参见 [与 Configuration 系统交互][Inspecting-Settings]。

### 在构建定义中涉及 scope

如果你创建的 `build.sbt` 中有一个bare key，它的作用于将是当前的 project 下，configuration 和 task 均为 `Global`：

```scala
lazy val root = (project in file("."))
  .settings(
    name := "hello"
  )
```

启动 sbt 并且执行 `inspect name` 可以看到它由 `{file:/home/hp/checkout/hello/}default-aea33a/*:name` 提供，也就是说，project 是 `{file:/home/hp/checkout/hello/}default-aea33a`，
configuration 是 `*`（表示全局），task 没有显示出来（实际上也是全局）。

Keys 会调用一个重载的 in 方法设置 scope。传给 in 方法的参数可以是任何 scope 轴的实例。比如说，你可以将 `name` 局限在 `Compile` configuration 中，尽管没有真实的理由要这样做：

```scala
Compile / name := "hello"
```

或者你可以把 `name` 局限在 `packageBin` task 中（没有什么意义！仅仅是个例子）：

```scala
name in packageBin := "hello"
```

或者你可以把 `name` 局限在多个 scope 轴中，例如在 `Compile` configuration 的 `packageBin` task 中：

```scala
name in (Compile, packageBin) := "hello"
```

或者你可以用 `Global` 表示所有的轴：

```scala
name in Global := "hello"
```

（`name in Global` 隐式的把 scope 轴的值 `Global` 转换为 scope 所有轴的值均为 `Global`；task 和 configuration 默认是 `Global`，因此这里的效果是将 project 设置成 `Global`，
也就是说，定义了 `*/*:name` 而不是 `{file:/home/hp/checkout/hello/}default-aea33a/*:name`）

如果你之前不熟悉 Scala，提醒一下：in 和 `:=` 仅仅是方法，不是魔法，理解这点很重要。Scala 让你用一种更好的方式编写它们，但是你也可以用 Java 的风格：

```scala
name.in(Compile).:=("hello")
```

毫无理由使用这种丑陋的语法，但是它阐明这实际上是方法。

### 指定 scope 的时机

如果一个 key 通常的作用域有问题，你需要指定 scope。例如，`compile` task 默认是在 `Compile` 和 `Test` configuration 的 scope 中，而且在这些 scope 之外它并不存在。

为了改变 key `compile` 的值，你需要写成 `Compile / compile` 或者 `Test / compile`。用普通的 `compile` 会在当前 project 的 scope 中定义一个新的 task，而不是覆盖 configuration 的 scope 标准的 `compile` task。

如果你遇到像 *“引用未定义的设置”* 这样的错误，通常是你指定 scope 失败了，或者你指定了一个错误的 scope。你使用的 key 可能定义在其他的 scope 中。sbt 会尝试在错误消息里面提示你的想法是什么；如 “你是指 Compile/compile？”

一种方式是你可以这样认为，name 只是 key 的 *一部分*。实际上，所有的 key 都有 name 和 scope 组成（scope 有三个轴）。换句话说，`packageOptions in (Compile, packageBin)` 是表示 key name 的完整的表达式。
其简写 `packageOptions` 也是一个 key name，但是是不同的（对于没有 in 方法的 key，会隐式的假设一个 scope：当前的 project，global
config，global task）。

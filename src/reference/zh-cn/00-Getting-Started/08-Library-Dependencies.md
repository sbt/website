---
out: Library-Dependencies.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [More-About-Settings]: More-About-Settings.html
  [external-maven-ivy]: ../docs/Library-Management.html#external-maven-ivy
  [Cross-Build]: ../docs/Cross-Build.html
  [Resolvers]: ../docs/Resolvers.html
  [Library-Management]: ../docs/Library-Management.html

库依赖
--------------------

阅读这一小节时，假设你已经阅读过新手入门前面的内容，特别是 [.sbt 构建定义][Basic-Def]，[Scopes][Scopes] 和 [更多关于设置][More-About-Settings]。

可以通过下面这两种方式添加库依赖：

-   *非托管依赖* 为放在 `lib` 目录下的 jar 文件
-   *托管依赖* 配置在构建定义中，并且会自动从仓库（repository）中下载

### 非托管依赖

大多数人会用托管依赖而不是非托管依赖。但是非托管依赖在起步阶段会简单很多。

非托管依赖像这样工作：将 jar 文件放在 `lib` 文件夹下，然后它们将会被添加到项目的 classpath 中。没有更多的事情了！

你也可以将测试依赖的 jar 文件放在 `lib` 目录下，比如 [ScalaCheck](https://scalacheck.org/)，[Specs2](http://specs2.org)，[ScalaTest](https://www.scalatest.org/)。

`lib` 目录下的所有依赖都会在 classpaths（对 `compile`， `test`， `run` 和 `console` 都成立）。如果你想对其中的一个改变 classpath，
你需要做适当调整，例如 `Compile / dependencyClasspath` 或者 `Runtime / dependencyClasspath`。

如果用非托管依赖的话，不用往 `build.sbt` 文件中添加任何内容，不过你可以改变 `unmanagedBase` key，如果你想用一个不同的目录而非 `lib`。

用 `custom_lib` 替代 `lib`：

```scala
unmanagedBase := baseDirectory.value / "custom_lib"
```

`baseDirectory` 是项目的根目录，所以在这里你依据 `baseDirectory` 的值改变了 `unmanagedBase`，通过在 [更多关于设置][More-About-Settings] 中介绍的一个特殊的 `value` 方法。

同时也有一个列举 `unmanagedBase` 目录下所有 jar 文件的 task 叫 `unmanagedJars`。如果你想用多个目录或者做一些更加复杂的事情，你可能需要用一个可以做其他事情的 task 替换整个 `unmanagedJars` task，
例如清空 `Compile` configuration 的列表，不考虑 `lib` 目录下的文件：

```scala
Compile / unmanagedJars := Seq.empty[sbt.Attributed[java.io.File]]
```

### 托管依赖

sbt 使用 [Apache Ivy](https://ant.apache.org/ivy/) 来实现托管依赖，所以如果你对 Ivy 或者 Maven 比较熟悉的话，你不会有太多的麻烦。

#### `libraryDependencies` Key

大多数时候，你可以很简单的在 `libraryDependencies` 设置项中列出你的依赖。也可以通过 Maven POM 文件或者 Ivy 配置文件来配置依赖，而且可以通过 sbt 来调用这些外部的配置文件。
你可以从[这里][external-maven-ivy]获取更详细的内容。

可以像这样定义一个依赖，其中 `groupId`， `artifactId` 和 `revision` 都是字符串：

```scala
libraryDependencies += groupID % artifactID % revision
```

或者像这样， 用字符串或者 `Configuration` val (`Test`) 当做 `configuration`：

```scala
libraryDependencies += groupID % artifactID % revision % configuration
```

`libraryDependencies` 在 [Keys](../../api/sbt/Keys\$.html#libraryDependencies:sbt.SettingKey[Seq[sbt.librarymanagement.ModuleID]]) 中像这样声明：

```scala
val libraryDependencies = settingKey[Seq[ModuleID]]("Declares managed dependencies.")
```

方法 `%` 从字符串创建 `ModuleID` 对象，然后将 `ModuleID` 添加到 `libraryDependencies` 中。

当然，要让 sbt（通过 Ivy）知道从哪里下载模块。如果你的模块和 sbt 来自相同的某个默认的仓库，这样就会工作。例如，Apache Derby 在标准的 Maven2 仓库中：

```scala
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3"
```

如果你在 `build.sbt` 中输入上面这些内容，然后执行 `update`，sbt 会将 Derby 下载到 `\$COURSIER_CACHE/https/repo1.maven.org/maven2/org/apache/derby`。（顺便提一下， `compile` 依赖于 `update`，所以
大多数时候不需要手动的执行 `update`。）

当然，你也可以通过 `++=` 一次将所有依赖作为一个列表添加：

```scala
libraryDependencies ++= Seq(
  groupID % artifactID % revision,
  groupID % otherID % otherRevision
)
```

在很少情况下，你也会需要在 `libraryDependencies` 上用 `:=` 方法。

#### 通过 `%%` 方法获取正确的 Scala 版本

如果你用是 `groupID %% artifactID % revision` 而不是 `groupID % artifactID % revision`（区别在于 `groupID` 后面是 `%%`），sbt 会在 工件名称中加上项目的 Scala 版本号。
这只是一种快捷方法。你可以这样写不用 `%%`：

```scala
libraryDependencies += "org.scala-stm" % "scala-stm_2.13" % "$example_scala_stm_version$"
```

假设这个构建的 `scalaVersion` 是 `$example_scala213$`，下面这种方式是等效的（注意 `"org.scala-stm"` 后面是 `%%`）：

```scala
libraryDependencies += "org.scala-stm" %% "scala-stm" % "$example_scala_stm_version$"
```

这个想法是很多依赖都会被编译给多个 Scala 版本，而你想确保和项目匹配的jar是二进制兼容的。

参见 [交叉构建][Cross-Build] 获取更多信息。

#### Ivy 修正

`groupID % artifactID % revision` 中的 `revision` 不需要是一个固定的版本号。Ivy 能够根据你指定的约束选择一个模块的最新版本。你指定 `"latest.integration"`，`"2.9.+"` 或者 `"[1.0,)"`，而不是
一个固定的版本号，像 `"1.6.1"`。参看 [Ivy 修订](https://ant.apache.org/ivy/history/2.3.0/ivyfile/dependency.html#revision) 文档获取详细内容。

#### 解析器

不是所有的依赖包都放在同一台服务器上，sbt 默认使用标准的 Maven2 仓库。如果你的依赖不在默认的仓库中，你需要添加 *resolver* 来帮助 Ivy 找到它。

通过以下形式添加额外的仓库：

```scala
resolvers += name at location
```

在两个字符串中间有一个特殊的 `at`。

例如：

```scala
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
```

`resolvers` key 在 [Keys](../../api/sbt/Keys\$.html#resolvers:sbt.SettingKey[Seq[sbt.librarymanagement.Resolver]]) 中像这样定义：

```scala
val resolvers = settingKey[Seq[Resolver]]("用户为托管依赖定义的额外的解析器。")
```

`at` 方法通过两个字符串创建了一个 `Resolver` 对象。

sbt 会搜索你的本地 Maven 仓库如果你将它添加为一个仓库：

```scala
resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
```

或者，为了方便起见：

```scala
resolvers += Resolver.mavenLocal
```

参见 [解析器][Resolvers] 获取更多关于定义其他类型的仓库的内容。

#### 覆写默认的解析器

`resolvers` 不包含默认的解析器，仅仅通过构建定义添加额外的解析器。

sbt 将 `resolvers` 和一些默认的仓库组合起来构成 `externalResolvers`。

然而，为了改变或者移除默认的解析器，你需要覆写`externalResolvers` 而不是 `resolvers`。

#### Per-configuration dependencies

通常会有依赖只被测试代码使用（在 `src/test/scala` 中，通过 `Test` configuration 编译）而没有在主应用中使用。

如果你想要一个依赖只在 `Test` configuration 的 classpath 中出现而不是 `Compile` configuration，像这样添加 `% "test"`：

```scala
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % "test"
```

也可能也会像这样使用类型安全的 `Test` configuration：

```scala
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % Test
```

现在，如果你在 sbt 的命令提示行里输入 `show Compile/dependencyClasspath`，你不应该看到 derby jar。但是如果你输入 `show Test/dependencyClasspath`，
你应该在列表中看到 derby jar。

通常，测试相关的依赖，如 [ScalaCheck](https://scalacheck.org/)，
[Specs2](http://specs2.org) 和 [ScalaTest](https://www.scalatest.org/) 将会被定义为 `% "test"`。

库依赖更详细的内容和技巧在[这里][Library-Management]。

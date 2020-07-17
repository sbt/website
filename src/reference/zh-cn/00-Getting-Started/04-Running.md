---
out: Running.html
---

  [Hello]: Hello.html
  [Setup]: Setup.html
  [Triggered-Execution]: ../docs/Triggered-Execution.html
  [Command-Line-Reference]: ../docs/Command-Line-Reference.html

运行
-------

这一小节将讲述在你建立好项目之后如何去使用 sbt。假设你已经 [安装 sbt][Setup] 并且已经创建过 [Hello, World][Hello] 项目或其他项目。

### 交互模式

在你的项目目录下运行 sbt 不跟任何参数：

```
\$ sbt
```

执行 sbt 不跟任何命令行参数将会进入交互模式。交互模式有一个命令行（含有 tab 自动补全功能和历史记录）。

例如，在 sbt 命令行里输入 `compile`：

```
> compile
```

再次 `compile`，只需要按向上的方向键，然后回车。
输入 `run` 来启动程序。
输入 `exit` 或者 Ctrl+D （Unix）或者 Ctrl+Z （Windows）可以退出交互模式。

### 批处理模式

你也可以用批处理模式来运行 sbt，可以以空格为分隔符指定参数。对于接受参数的 sbt 命令，将命令和参数用引号引起来一起传给 sbt。例如：

```
\$ sbt clean compile "testOnly TestA TestB"
```

在这个例子中，`testOnly` 有两个参数 `TestA` 和 `TestB`。这个命令会按顺序执行（`clean`， `compile`， 然后 `testOnly`）。

### 持续构建和测试

为了加快编辑-编译-测试循环，你可以让 sbt 在你保存源文件时自动重新编译或者跑测试。
在命令前面加上前缀 `~` 后，每当有一个或多个源文件发生变化时就会自动运行该命令。例如，在交互模式下尝试：

```
> ~ compile
```

按回车键停止监视变化。
你可以在交互模式或者批处理模式下使用 `~` 前缀。
参见 [触发执行][Triggered-Execution] 获取详细信息。

### 常用命令

下面是一些非常常用的的 sbt 命令。更加详细的列表请参见 [命令行参考][Command-Line-Reference]。

<table class="table table-striped">
  <tr>
    <td><tt>clean</tt></td>
    <td>删除所有生成的文件 （在 <tt>target</tt> 目录下）。</td>
  </tr>
  <tr>
    <td><tt>compile</tt></td>
    <td>编译源文件（在 <tt>src/main/scala</tt> 和
   <tt>src/main/java</tt> 目录下）。</td>
  </tr>
  <tr>
    <td><tt>test</tt></td>
    <td>编译和运行所有测试。</td>
  </tr>
  <tr>
    <td><tt>console</tt></td>
    <td>进入到一个包含所有编译的文件和所有依赖的 classpath 的 Scala 解析器。输入 <tt>:quit</tt>，
   Ctrl+D （Unix），或者 Ctrl+Z （Windows） 返回到 sbt。</td>
  </tr>
  <tr>
    <td><nobr><tt>run &lt;参数&gt;*</tt></nobr></td>
    <td>在和 sbt 所处的同一个虚拟机上执行项目的 main class。</td>
  </tr>
  <tr>
    <td><tt>package</tt></td>
    <td>将 <tt>src/main/resources</tt> 下的文件和 <tt>src/main/scala</tt> 以及 <tt>src/main/java</tt> 中编译出来的 class 文件打包成一个 jar 文件。</td>
  </tr>
  <tr>
    <td><tt>help &lt;命令&gt;</tt></td>
    <td>显示指定的命令的详细帮助信息。如果没有指定命令，会显示所有命令的简介。</td>
  </tr>
  <tr>
    <td><tt>reload</tt></td>
    <td>重新加载构建定义（<tt>build.sbt</tt>， <tt>project/*.scala</tt>， <tt>project/*.sbt</tt> 这些文件中定义的内容)。在修改了构建定义文件之后需要重新加载。</td>
  </tr>
</table>

### Tab 自动补全

交互模式下包括空的命令行都有 tab 自动补全。sbt 的一个特别的约定是，当按 tab 键一次的时候可能只会显示所有命令中最有可能的自动补全的子集，当按多次时才会显示详细的选项。

### 命令历史记录

交互模式有历史记录，即使你退出 sbt 然后重新进入。最简单的访问历史记录的方法时用上方向键。还支持以下一些命令：

<table class="table table-striped">
  <tr>
    <td><tt>!</tt></td>
    <td>显示历史记录命令帮助。</td>
  </tr>
  <tr>
    <td><tt>!!</tt></td>
    <td>重新执行前一条命令。</td>
  </tr>
  <tr>
    <td><tt>!:</tt></td>
    <td>显示所有之前的命令。</td>
  </tr>  
  <tr>
    <td><tt>!:n</tt></td>
    <td>显示之前的最后 <tt>n</tt> 条命令。</td>
  </tr>
  <tr>
    <td><tt>!n</tt></td>
    <td>执行 <tt>!:</tt> 命令显示的结果中下标为 <tt>n</tt> 的命令。</td>
  </tr>
  <tr>
    <td><tt>!-n</tt></td>
    <td>执行从该命令往前数第 n 条命令。</td>
  </tr>
  <tr>
    <td><tt>!string</tt></td>
    <td>执行最近执行过的以 string 打头的命令。</td>
  </tr>
  <tr>
    <td><tt>!?string</tt></td>
    <td>执行最近执行过的包含 string 的命令。</td>
  </tr>
</table>

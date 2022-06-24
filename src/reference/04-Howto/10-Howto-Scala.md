---
out: Howto-Scala.html
---

  [Cross-Build]: Cross-Build.html
  [Console-Project]: Console-Project.html

Configure and use Scala
-----------------------

<a name="version"></a>

### Set the Scala version used for building the project

The `scalaVersion` configures the version of Scala used for compilation.
By default, sbt also adds a dependency on the Scala library with this
version. See the next section for how to disable this automatic
dependency. If the Scala version is not specified, the version sbt was
built against is used. It is recommended to explicitly specify the
version of Scala.

For example, to set the Scala version to "2.11.1",

```scala
scalaVersion := "2.11.1"
```

<a name="noauto"></a>

### Disable the automatic dependency on the Scala library

sbt adds a dependency on the Scala standard library by default. To
disable this behavior, set the `autoScalaLibrary` setting to false.

```scala
autoScalaLibrary := false
```

<a name="temporary"></a>

### Temporarily switch to a different Scala version

To set the Scala version in all scopes to a specific value, use the `++`
command. For example, to temporarily use Scala 2.10.4, run:

```
> ++ 2.10.4
```

<a name="local"></a>

### Use a local Scala installation for building a project

Defining the `scalaHome` setting with the path to the Scala home
directory will use that Scala installation. sbt still requires
`scalaVersion` to be set when a local Scala version is used. For
example,

```scala
scalaVersion := "2.10.0-local"

scalaHome := Some(file("/path/to/scala/home/"))
```

<a name="cross"></a>

### Build a project against multiple Scala versions

See [cross building][Cross-Build].

<a name="consoleQuick"></a>

### Enter the Scala REPL with a project's dependencies on the classpath, but not the compiled project classes

The `consoleQuick` action retrieves dependencies and puts them on the
classpath of the Scala REPL. The project's sources are not compiled, but
sources of any source dependencies are compiled. To enter the REPL with
test dependencies on the classpath but without compiling test sources,
run `Test/consoleQuick`. This will force compilation of main sources.

<a name="console"></a>

### Enter the Scala REPL with a project's dependencies and compiled code on the classpath

The `console` action retrieves dependencies and compiles sources and
puts them on the classpath of the Scala REPL. To enter the REPL with
test dependencies and compiled test sources on the classpath, run
`Test/console`.

<a name="consoleProject"></a>

### Enter the Scala REPL with plugins and the build definition on the classpath

```
> consoleProject
```

For details, see the [consoleProject][Console-Project]
page.

<a name="initial"></a>

### Define the initial commands evaluated when entering the Scala REPL

Set `console / initialCommands` to set the initial statements to
evaluate when `console` and `consoleQuick` are run. To configure
`consoleQuick` separately, use `consoleQuick / initialCommands`. For
example,

```scala
console / initialCommands := """println("Hello from console")"""

consoleQuick / initialCommands := """println("Hello from consoleQuick")"""
```

The `consoleProject` command is configured separately by
`consoleProject / initialCommands`. It does not use the value from
`console / initialCommands` by default. For example,

```scala
consoleProject / initialCommands := """println("Hello from consoleProject")"""
```

<a name="cleanup"></a>

### Define the commands evaluated when exiting the Scala REPL

Set `console / cleanupCommands` to set the statements to evaluate after
exiting the Scala REPL started by `console` and `consoleQuick`. To
configure `consoleQuick` separately, use
`consoleQuick / cleanupCommands`. For example,

```scala
console / cleanupCommands := """println("Bye from console")"""

consoleQuick / cleanupCommands := """println("Bye from consoleQuick")"""
```

The `consoleProject` command is configured separately by
`consoleProject / cleanupCommands`. It does not use the value from
`console / cleanupCommands` by default. For example,

```scala
consoleProject / cleanupCommands := """println("Bye from consoleProject")"""
```

<a name="embed"></a>

### Use the Scala REPL from project code

sbt runs tests in the same JVM as sbt itself and Scala classes are not
in the same class loader as the application classes. This is also the
case in `console` and when `run` is not forked. Therefore, when using
the Scala interpreter, it is important to set it up properly to avoid an
error message like:

```
Failed to initialize compiler: class scala.runtime.VolatileBooleanRef not found.
** Note that as of 2.8 scala does not assume use of the java classpath.
** For the old behavior pass -usejavacp to scala, or if using a Settings
** object programmatically, settings.usejavacp.value = true.
```

The key is to initialize the Settings for the interpreter using
*embeddedDefaults*. For example:

```scala
val settings = new Settings
settings.embeddedDefaults[MyType]
val interpreter = new Interpreter(settings, ...)
```

Here, `MyType` is a representative class that should be included on the
interpreter's classpath and in its application class loader. For more
background, see the
[original proposal](https://gist.github.com/404272) that resulted in
*embeddedDefaults* being added.

Similarly, use a representative class as the type argument when using
the *break* and *breakIf* methods of *ILoop*, as in the following
example:

```scala
def x(a: Int, b: Int) = {
  import scala.tools.nsc.interpreter.ILoop
  ILoop.breakIf[MyType](a != b, "a" -> a, "b" -> b )
}
```    

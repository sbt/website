---
out: Howto-Scala.html
---

Configure and use Scala
-----------------------

The `scalaVersion` configures the version of Scala used for compilation.
By default, sbt also adds a dependency on the Scala library with this
version. See the next section for how to disable this automatic
dependency. If the Scala version is not specified, the version sbt was
built against is used. It is recommended to explicitly specify the
version of Scala.

For example, to set the Scala version to "2.9.2",

    scalaVersion := "2.9.2"

sbt adds a dependency on the Scala standard library by default. To
disable this behavior, set the `autoScalaLibrary` setting to false.

    autoScalaLibrary := false

To set the Scala version in all scopes to a specific value, use the `++`
command. For example, to temporarily use Scala 2.8.2, run:

```
> ++ 2.8.2
```

Defining the `scalaHome` setting with the path to the Scala home
directory will use that Scala installation. sbt still requires
`scalaVersion` to be set when a local Scala version is used. For
example,

    scalaVersion := "2.10.0-local"

    scalaHome := Some(file("/path/to/scala/home/"))

See `cross building </Detailed-Topics/Cross-Build>`.

The `consoleQuick` action retrieves dependencies and puts them on the
classpath of the Scala REPL. The project's sources are not compiled, but
sources of any source dependencies are compiled. To enter the REPL with
test dependencies on the classpath but without compiling test sources,
run `test:consoleQuick`. This will force compilation of main sources.

The `console` action retrieves dependencies and compiles sources and
puts them on the classpath of the Scala REPL. To enter the REPL with
test dependencies and compiled test sources on the classpath, run
`test:console`.

```
> consoleProject
```

For details, see the `consoleProject </Detailed-Topics/Console-Project>`
page.

Set `initialCommands in console` to set the initial statements to
evaluate when `console` and `consoleQuick` are run. To configure
`consoleQuick` separately, use `initialCommands in consoleQuick`. For
example,

    initialCommands in console := """println("Hello from console")"""

    initialCommands in consoleQuick := """println("Hello from consoleQuick")"""

The `consoleProject` command is configured separately by
`initialCommands in consoleProject`. It does not use the value from
`initialCommands in console` by default. For example,

    initialCommands in consoleProject := """println("Hello from consoleProject")"""

Set `cleanupCommands in console` to set the statements to evaluate after
exiting the Scala REPL started by `console` and `consoleQuick`. To
configure `consoleQuick` separately, use
`cleanupCommands in consoleQuick`. For example,

    cleanupCommands in console := """println("Bye from console")"""

    cleanupCommands in consoleQuick := """println("Bye from consoleQuick")"""

The `consoleProject` command is configured separately by
`cleanupCommands in consoleProject`. It does not use the value from
`cleanupCommands in console` by default. For example,

    cleanupCommands in consoleProject := """println("Bye from consoleProject")"""

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

    val settings = new Settings
    settings.embeddedDefaults[MyType]
    val interpreter = new Interpreter(settings, ...)

Here, MyType is a representative class that should be included on the
interpreter's classpath and in its application class loader. For more
background, see the
[original proposal](https://gist.github.com/404272) that resulted in
*embeddedDefaults* being added.

Similarly, use a representative class as the type argument when using
the *break* and *breakIf* methods of *ILoop*, as in the following
example:

    def x(a: Int, b: Int) = {
      import scala.tools.nsc.interpreter.ILoop
      ILoop.breakIf[MyType](a != b, "a" -> a, "b" -> b )
    }

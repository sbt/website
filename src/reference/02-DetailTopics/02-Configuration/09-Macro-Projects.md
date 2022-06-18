---
out: Macro-Projects.html
---

Macro Projects
--------------

### Introduction

Some common problems arise when working with macros.

1.  The current macro implementation in the compiler requires that macro
    implementations be compiled before they are used. The solution is
    typically to put the macros in a subproject or in their own
    configuration.
2.  Sometimes the macro implementation should be distributed with the
    main code that uses them and sometimes the implementation should not
    be distributed at all.

The rest of the page shows example solutions to these problems.

### Defining the Project Relationships

The macro implementation will go in a subproject in the `macro/`
directory. The core project in the `core/` directory will depend
on this subproject and use the macro. This configuration is shown in the
following build definition. `build.sbt`:

```scala
lazy val commonSettings = Seq(
  scalaVersion := "$example_scala_version$",
  organization := "com.example"
)
lazy val scalaReflect = Def.setting { "org.scala-lang" % "scala-reflect" % scalaVersion.value }

lazy val core = (project in file("core"))
  .dependsOn(macroSub)
  .settings(
    commonSettings,
    // other settings here
  )

lazy val macroSub = (project in file("macro"))
  .settings(
    commonSettings,
    libraryDependencies += scalaReflect.value
    // other settings here
  )
```

This specifies that the macro implementation goes in
`macro/src/main/scala/` and tests go in `macro/src/test/scala/`. It also
shows that we need a dependency on the compiler for the macro
implementation. As an example macro, we'll use `desugar` from
[macrocosm](https://github.com/retronym/macrocosm). `macro/src/main/scala/demo/Demo.scala`:

```scala
package demo

import language.experimental.macros
import scala.reflect.macros.blackbox.Context

object Demo {

  // Returns the tree of `a` after the typer, printed as source code.
  def desugar(a: Any): String = macro desugarImpl

  def desugarImpl(c: Context)(a: c.Expr[Any]) = {
    import c.universe._

    val s = show(a.tree)
    c.Expr(
      Literal(Constant(s))
    )
  }
}
```

`macro/src/test/scala/demo/Usage.scala`:

```scala
package demo

object Usage {
   def main(args: Array[String]): Unit = {
      val s = Demo.desugar(List(1, 2, 3).reverse)
      println(s)
   }
}
```

This can be then run at the console:

```
\$ sbt
> macroSub/Test/run
scala.collection.immutable.List.apply[Int](1, 2, 3).reverse
```

Actual tests can be defined and run as usual with `macro/test`.

The main project can use the macro in the same way that the tests do.
For example,

`core/src/main/scala/MainUsage.scala`:

```scala
package demo

object Usage {
   def main(args: Array[String]): Unit = {
      val s = Demo.desugar(List(6, 4, 5).sorted)
      println(s)
   }
}
```

```
\$ sbt
> core/run
scala.collection.immutable.List.apply[Int](6, 4, 5).sorted[Int](math.this.Ordering.Int)
```

### Common Interface

Sometimes, the macro implementation and the macro usage should share
some common code. In this case, declare another subproject for the
common code and have the main project and the macro subproject depend on
the new subproject. For example, the project definitions from above
would look like:


```scala
lazy val commonSettings = Seq(
  scalaVersion := "$example_scala_version$",
  organization := "com.example"
)
lazy val scalaReflect = Def.setting { "org.scala-lang" % "scala-reflect" % scalaVersion.value }

lazy val core = (project in file("core"))
  .dependsOn(macroSub, util)
  .settings(
    commonSettings,
    // other settings here
  )

lazy val macroSub = (project in file("macro"))
  .dependsOn(util)
  .settings(
    commonSettings,
    libraryDependencies += scalaReflect.value
    // other settings here
  )

lazy util = (project in file("util"))
  .settings(
    commonSettings,
    // other setting here
  )
```

Code in `util/src/main/scala/` is available for both the `macroSub` and
`main` projects to use.

### Distribution

To include the macro code with the core code, add the binary and source
mappings from the macro subproject to the core project. And also
macro subproject should be removed from core project dependency in
publishing. For example, the `core` Project definition above would now
look like:

```scala
lazy val core = (project in file("core"))
  .dependsOn(macroSub % "compile-internal, test-internal")
  .settings(
    commonSettings,
    // include the macro classes and resources in the main jar
    Compile / packageBin / mappings ++= (macroSub / Compile / packageBin / mappings).value,
    // include the macro sources in the main source jar
    Compile / packageSrc / mappings ++= (macroSub / Compile / packageSrc / mappings).value
  )
```

You may wish to disable publishing the macro implementation. This is
done by overriding `publish` and `publishLocal` to do nothing:

```scala
lazy val macroSub = (project in file("macro"))
  .settings(
    commonSettings,
    libraryDependencies += scalaReflect.value,
    publish := {},
    publishLocal := {}
  )
```

The techniques described here may also be used for the common interface
described in the previous section.

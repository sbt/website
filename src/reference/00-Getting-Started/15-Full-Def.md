---
out: Full-Def.html
---

  [Basic-Def]: Basic-Def.html
  [Using-Plugins]: Using-Plugins.html

Appendix: .scala build definition
---------------------------------

This page describes an old style of `.scala` build definition.
In the previous versions of sbt, `.scala` was the only way to create multi-project build definition,
but sbt 0.13 added [multi-project .sbt build definition][Basic-Def],
which is the recommended style.

We assume you've read previous pages in the Getting Started
Guide, *especially* [.sbt build definition][Basic-Def].

### Relating build.sbt to Build.scala

To mix `.sbt` and `.scala` files in your build definition, you need to
understand how they relate.

The following two files illustrate. First, if your project is in hello,
create `hello/project/Build.scala` as follows:

```scala
import sbt._
import Keys._

object HelloBuild extends Build {
  val sampleKeyA = settingKey[String]("demo key A")
  val sampleKeyB = settingKey[String]("demo key B")
  val sampleKeyC = settingKey[String]("demo key C")
  val sampleKeyD = settingKey[String]("demo key D")

  override lazy val settings = super.settings ++
    Seq(
      sampleKeyA := "A: in Build.settings in Build.scala",
      resolvers := Seq()
    )

  lazy val root = Project(id = "hello",
    base = file("."),
    settings = Seq(
      sampleKeyB := "B: in the root project settings in Build.scala"
    ))
}
```

Now, create `hello/build.sbt` as follows:

```scala
sampleKeyC in ThisBuild := "C: in build.sbt scoped to ThisBuild"

sampleKeyD := "D: in build.sbt"
```

Start up the sbt interactive prompt. Type `inspect sampleKeyA` and you
should see (among other things):

```
[info] Setting: java.lang.String = A: in Build.settings in Build.scala
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}/*:sampleKeyA
```

and then `inspect sampleKeyC` and you should see:

```
[info] Setting: java.lang.String = C: in build.sbt scoped to ThisBuild
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}/*:sampleKeyC
```

Note that the "Provided by" shows the same scope for the two values.
That is, `sampleKeyC in ThisBuild` in a `.sbt` file is equivalent to placing
a setting in the `Build.settings` list in a `.scala` file. sbt takes
build-scoped settings from both places to create the build definition.

Now, `inspect sampleKeyB`:

```
[info] Setting: java.lang.String = B: in the root project settings in Build.scala
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}hello/*:sampleKeyB
```

Note that `sampleKeyB` is scoped to the project
`({file:/home/hp/checkout/hello/}hello)` rather than the entire build
`({file:/home/hp/checkout/hello/})`.

As you've probably guessed, `inspect sampleKeyD` matches `sampleKeyB`:

```
[info] Setting: java.lang.String = D: in build.sbt
[info] Provided by:
[info]  {file:/home/hp/checkout/hello/}hello/*:sampleKeyD
```

sbt *appends* the settings from `.sbt` files to the settings from
`Build.settings` and `Project.settings` which means `.sbt` settings take
precedence. Try changing `Build.scala` so it sets key `sampleC` or `sampleD`,
which are also set in `build.sbt`. The setting in `build.sbt` should "win"
over the one in `Build.scala`.

One other thing you may have noticed: `sampleKeyC` and `sampleKeyD` were
available inside `build.sbt`. That's because sbt imports the contents of
your `Build` object into your `.sbt` files. In this case
`import HelloBuild._` was implicitly done for the `build.sbt` file.

In summary:

- In `.scala` files, you can add settings to `Build.settings` for sbt to
  find, and they are automatically build-scoped.
- In `.scala` files, you can add settings to `Project.settings` for sbt to
  find, and they are automatically project-scoped.
- Any `Build` object you write in a `.scala` file will have its contents
  imported and available to `.sbt` files.
- The settings in `.sbt` files are *appended* to the settings in `.scala`
  files.
- The settings in `.sbt` files are project-scoped unless you explicitly
  specify another scope.

### The build definition project in interactive mode

You can switch the sbt interactive prompt to have the build definition
project in `project/` as the current project. To do so, type
reload `plugins`.

```
> reload plugins
[info] Set current project to default-a0e8e4 (in build file:/home/hp/checkout/hello/project/)
> show sources
[info] ArrayBuffer(/home/hp/checkout/hello/project/Build.scala)
> reload return
[info] Loading project definition from /home/hp/checkout/hello/project
[info] Set current project to hello (in build file:/home/hp/checkout/hello/)
> show sources
[info] ArrayBuffer(/home/hp/checkout/hello/hw.scala)
>
```

As shown above, you use `reload return` to leave the build definition
project and return to your regular project.

### Reminder: it's all immutable

It would be wrong to think that the settings in `build.sbt` are added to
the `settings` fields in `Build` and `Project` objects. Instead, the `settings`
list from `Build` and `Project`, and the settings from `build.sbt`, are
concatenated into another immutable list which is then used by sbt. The
`Build` and `Project` objects are "immutable configuration" forming only
part of the complete build definition.

In fact, there are other sources of settings as well. They are appended
in this order:

-   Settings from `Build.settings` and `Project.settings` in your `.scala`
    files.
-   Your user-global settings; for example in `$global_sbt_file$` you can
    define settings affecting *all* your projects.
-   Settings injected by plugins, see [using plugins][Using-Plugins]
    coming up next.
-   Settings from `.sbt` files in the project.
-   Build definition projects (i.e. projects inside `project`) have
    settings from global plugins (`$global_plugins_base$`) added.
    [Using plugins][Using-Plugins] explains this more.

Later settings override earlier ones. The entire list of settings forms
the build definition.

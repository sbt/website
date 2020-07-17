---
out: Cross-Build-Plugins.html
---

Cross building plugins
----------------------

Like we are able to cross build against multiple Scala versions, we can cross build sbt 0.13 plugins while staying on sbt 1.x.

```scala
crossSbtVersions := Vector("1.2.8", "0.13.18")
```

If you need to make changes specific to a sbt version, you can now include them into `src/main/scala-sbt-0.13`
and `src/main/scala-sbt-1.0`. To switch between the sbt versions use

```
^^ 0.13.18

[info] Setting `sbtVersion in pluginCrossBuild` to 0.13.18
[info] Set current project to sbt-something (in build file:/xxx/sbt-something/)
```

or `^compile` to cross compile.

### Mixing libraries and sbt plugins in a build

When you want to mix both libraries and sbt plugins into a multi-project build,
it's more convenient to drive the sbt version based on the Scala version.

You can do that as follows:

```scala
ThisBuild / crossScalaVersions := Seq("2.10.7", "2.12.10")

lazy val core = (project in file("core"))

lazy val plugin = (project in file("sbt-something"))
  .enablePlugins(SbtPlugin)
  .dependsOn(core)
  .settings(
    // change the sbt version based on Scala version
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.10" => "0.13.18"
        case "2.12" => "1.2.8"
      }
    }
  )
```

This is a technique discovered by [@jroper](https://github.com/jroper) in [sbt-pgp#115](https://github.com/sbt/sbt-pgp/pull/115). It works because sbt 0.13 and 1.x series use different Scala binary versions.

Using the setting, you can now use Scala cross building commands such as `+compile` and `+publish`.

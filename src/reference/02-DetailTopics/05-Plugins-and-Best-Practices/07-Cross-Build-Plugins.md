---
out: Cross-Build-Plugins.html
---

Cross Build Plugins
-------------------

Like we are able to cross build against multiple Scala versions, we can cross build sbt 1.0 plugins while staying on sbt 0.13. This is useful because we can port one plugin at a time.

1. If the plugin depends on libraries, make sure there are Scala 2.12 artifacts for them.
2. Use the latest sbt 0.13.15.
3. Append the following settings to your plugin project:

```scala
  .settings(
    scalaVersion := "$scala_version$",
    sbtVersion in Global := "$app_version$",
    scalaCompilerBridgeSource := {
      val sv = appConfiguration.value.provider.id.version
      ("org.scala-sbt" % "compiler-interface" % sv % "component").sources
    }
  )
```

Hopefully the last step will be simplified using @jrudolph's sbt-cross-building in the future.
If you run into problems upgrading a plugin, please report to [GitHub issue](https://github.com/sbt/sbt/issues).

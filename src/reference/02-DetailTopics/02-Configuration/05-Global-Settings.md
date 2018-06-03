---
out: Global-Settings.html
---

Global Settings
---------------

### Basic global configuration file

Settings that should be applied to all projects can go in
`$global_sbt_file$` (or any file in `$global_base$` with a `.sbt`
extension). Plugins that are defined globally in `$global_plugins_base$`
are available to these settings. For example, to change the default
`shellPrompt` for your projects:

`$global_sbt_file$`

```scala
shellPrompt := { state =>
  "sbt (%s)> ".format(Project.extract(state).currentProject.id)
}
```

You can also configure plugins globally added in `$global_plugin_sbt_file$`
(see next paragraph) in that file, but you need to use fully qualified
names for their properties. For example, for sbt-eclipse property `withSource`
documented in https://github.com/sbt/sbteclipse/wiki/Using-sbteclipse,
you need to use:

```scala
com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys.withSource := true
```


### Global Settings using a Global Plugin

The `$global_plugins_base$` directory is a global plugin project. This
can be used to provide global commands, plugins, or other code.

To add a plugin globally, create `$global_plugin_sbt_file$` containing
the dependency definitions. For example:

```scala
addSbtPlugin("org.example" % "plugin" % "1.0")
```

To change the default `shellPrompt` for every project using this
approach, create a local plugin `$global_shellprompt_scala$`:

```scala
import sbt._
import Keys._

object ShellPrompt extends AutoPlugin {
  override def trigger = allRequirements

  override def projectSettings = Seq(
    shellPrompt := { state =>
      "sbt (%s)> ".format(Project.extract(state).currentProject.id) }
  )
}
```

The `$global_plugins_base$` directory is a full project that is
included as an external dependency of every plugin project. In practice,
settings and code defined here effectively work as if they were defined
in a project's `project/` directory. This means that
`$global_plugins_base$` can be used to try out ideas for plugins such as
shown in the `shellPrompt` example.

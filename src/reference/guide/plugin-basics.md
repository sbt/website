  [Basic-Def]: Basic-Def.html
  [Library-Dependencies]: Library-Dependencies.html
  [Multi-Project]: Multi-Project.html
  [global-vs-local-plugins]: ../docs/Best-Practices.html#global-vs-local-plugins
  [Community-Plugins]: ../community-plugins.md
  [Plugins]: ../docs/Plugins.html
  [Plugins-Best-Practices]: ../docs/Plugins-Best-Practices.html
  [Task-Graph]: Task-Graph.html
  [Scaladex]: https://index.scala-lang.org/search?platform=sbt2

Plugin basics
=============

What is a plugin?
-----------------

A plugin extends the build definition, most commonly by adding new settings and tasks.
For example, a plugin could add `githubWorkflowGenerate` task to generate GitHub Actions YAML.

Finding the plugin versions using Scaladex
------------------------------------------

You can use [Scaladex][Scaladex] to search for plugins, and find out the latest version of the plugin.

Declaring a plugin
------------------

If your project is in directory `hello`, and
if you are adding sbt-github-actions to the build definition,
create `hello/project/plugins.sbt`
and declare the plugin dependency by passing the plugin's module ID
to `addSbtPlugin(...)`:

```scala
// In project/plugins.sbt

addSbtPlugin("com.github.sbt" % "sbt-github-actions" % "0.28.0")
```

If you're adding sbt-assembly, add the following:

```scala
// In project/plugins.sbt

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.3.1")
```

See [Source dependency plugin](../recipes/source-dependency-plugin.md) recipe for an experimental technique of using plugins hosted on git repos.

Plugins usually provide settings and tasks that get added to a subproject to enable
the plugin's functionality. This is described in the next section.

Enabling and disabling auto plugins
-----------------------------------

A plugin can declare that its settings be automatically added to the build definition,
in which case you don't have to do anything to add them.

The <!-- [auto plugins][Plugins] --> auto plugins feature enables
plugins to automatically, and safely, ensure their settings and
dependencies are on a project. Many auto plugins should have their default
settings automatically.

If you're using an auto plugin that requires explicit enablement, then you
have to add the following to your `build.sbt`:

```scala
lazy val util = (project in file("util"))
  .enablePlugins(FooPlugin, BarPlugin)
  .settings(
    name := "hello-util"
  )
```

The `enablePlugins` method allows projects to explicitly define the
auto plugins they wish to consume.

Projects can also exclude plugins using the `disablePlugins`
method. For example, if we wish to remove the `IvyPlugin` settings
from `util`, we modify our `build.sbt` as follows:

```scala
lazy val util = (project in file("util"))
  .enablePlugins(FooPlugin, BarPlugin)
  .disablePlugins(plugins.IvyPlugin)
  .settings(
    name := "hello-util"
  )
```

Auto plugins should document whether they need to be explicitly enabled. If you're
curious which auto plugins are enabled for a given project, just run the
`plugins` command on the sbt console.

```scala
sbt:hello> plugins
In build /tmp/hello/:
  Enabled plugins in hello:
    sbt.plugins.CorePlugin
    sbt.plugins.DependencyTreePlugin
    sbt.plugins.Giter8TemplatePlugin
    sbt.plugins.IvyPlugin
    sbt.plugins.JUnitXmlReportPlugin
    sbt.plugins.JvmPlugin
    sbt.plugins.SemanticdbPlugin
Plugins that are loaded to the build but not enabled in any subprojects:
  sbt.ScriptedPlugin
  sbt.plugins.SbtPlugin
```

Here, the `plugins` output is showing that the sbt default plugins are all
enabled. sbt's default settings are provided via 7 plugins:

1.  `CorePlugin`: Provides the core parallelism controls for tasks.
2.  `DependencyTreePlugin`: Provides dependency tree tasks.
3.  `Giter8TemplatePlugin`: Provides `sbt new` support.
4.  `IvyPlugin`: Provides the mechanisms to publish/resolve modules.
5.  `JUnitXmlReportPlugin`: Provides support for generating junit-xml.
6.  `JvmPlugin`: Provides the mechanisms to compile/test/run/package
    Java/Scala projects.
7.  `SemanticdbPlugin`: Provides support for generating SemanticDB.

<!--
### Global plugins

Plugins can be installed for all your projects at once by declaring them
in `$global_plugins_base$`. `$global_plugins_base$` is an sbt project whose
classpath is exported to all sbt build definition projects. Roughly
speaking, any `.sbt` or `.scala` files in `$global_plugins_base$` behave as if
they were in the `project/` directory for all projects.

You can create `$global_plugins_base$build.sbt` and put `addSbtPlugin()`
expressions in there to add plugins to all your projects at once.
Because doing so would increase the dependency on the machine environment,
this feature should be used sparingly. See
[Best Practices][global-vs-local-plugins].
-->

Available plugins
-----------------

In addition to [Scaladex][Scaladex], there's
also [a list of available plugins][Community-Plugins].

<!--
For more details, including ways of developing plugins, see
[Plugins][Plugins].
For best practices, see
[Plugins-Best-Practices][Plugins-Best-Practices].
-->

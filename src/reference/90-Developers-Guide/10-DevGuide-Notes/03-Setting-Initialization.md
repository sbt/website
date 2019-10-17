---
out: Setting-Initialization.html
---

Setting Initialization
----------------------

This page outlines the mechanisms by which sbt loads settings for a
particular build, including the hooks where users can control the
ordering of everything.

As stated elsewhere, sbt constructs its initialization graph and task
graph via `Setting[_]` objects. A setting is something which can take
the values stored at other `Keys` in the build state, and generates a new
value for a particular build key. sbt converts all registered
`Setting[_]` objects into a giant linear sequence and *compiles* them
into a task graph. This task graph is then used to execute your build.

All of sbt's loading semantics are contained within the
[Load.scala](https://github.com/sbt/sbt/blob/develop/main/src/main/scala/sbt/internal/Load.scala) file. It is approximately
the following:

![image](files/settings-initialization-load-ordering.png)

The blue circles represent actions happening when sbt loads a project.
We can see that sbt performs the following actions in load:

1. Compile the user-level project (`~/.sbt/<version>/`)

    a. Load any plugins defined by this project (`~/.sbt/<version>/plugins/*.sbt` and `~/.sbt/<version>/plugins/project/*.scala`)
    b. Load all settings defined (`~/.sbt/<version>/*.sbt` and `~/.sbt/<version>/plugins/*.scala`)

2.  Compile the current project (`<working-directory/project`)

    a. Load all defined plugins (`project/plugins.sbt` and `project/project/*.scala`)
    b. Load/Compile the project (`project/*.scala`)

3.  Load project `*.sbt` files (`build.sbt` and friends).

Each of these loads defines several sequences of settings. The diagram
shows the two most important:

-   `buildSettings` - These are settings defined to be `in ThisBuild` or
    directly against the `Build` object. They are initialized *once* for
    the build. You can add these, e.g. in `build.sbt` file:

    ```scala
    ThisBuild / foo := "hi"
    ```

-   `projectSettings` - These are settings specific to a project. They
    are specific to a *particular subproject* in the build. A plugin
    may be contributing its settings to more than one project, in which
    case the values are duplicated for each project. You add project
    specific settings, eg. in `project/build.scala`:

    ```scala
    lazy val root = (project in file(".")).settings(...)
    ```

After loading/compiling all the build definitions, sbt has a series of
`Seq[Setting[_]]` that it must order. As shown in the diagram, the
default inclusion order for sbt is:

1.  All AutoPlugin settings
2.  All settings defined in the user directory
    (`~/.sbt/<version>/*.sbt`)
3.  All local configurations (`build.sbt`)

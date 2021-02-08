---
out: Custom-Dependency-Configuration.html
---

  [Testing]: Testing.html
  [additional-test-configurations]: Testing.html#additional-test-configurations

How to define a custom dependency configuration
-----------------------------------------------

A *dependency configuration* (or *configuration* for short) defines
a graph of library dependencies, potentially with its own
classpath, sources, generated packages, etc. The dependency configuration concept
comes from Ivy, which sbt used to use for
managed dependencies [Library Dependencies][Library-Dependencies], and from
[MavenScopes](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Scope).

Some configurations you'll see in sbt:

- `Compile` which defines the main build (`src/main/scala`).
- `Test` which defines how to build tests (`src/test/scala`).
- `Runtime` which defines the classpath for the `run` task.

### Cautions on custom dependency configurations

A custom configuration should be considered only when you are
introducing either a new set of source code or its own
library dependencies (like `Test`).

In general, it would be a bad idea to introduce configuration
merely as a way to namespace keys.

One drawback of the custom configuration is that the users will be confused
about the complexity around scoping. They might be familar with
subprojects and tasks, but it becomes complicated when configuration scoping
is involved.

Another drawback is that there is limited support from sbt.
For instance, you can express that a configuration is meant to `extend`
another configuration, but there is no inheritance of settings.
You have to provide all expected settings and tasks.
This means that when a new features are added to sbt, there's a good
chance the custom configurations will not be covered.
The same goes for third-party plugins.

### Example basic custom configuration

Here's an example of a minimum custom configuration.

#### project/FuzzPlugin.scala

@@snip [FuzzPlugin.scala]($root$/src/sbt-test/ref/custom-config/project/FuzzPlugin.scala) {}

#### build.sbt

@@snip [build.sbt]($root$/src/sbt-test/ref/custom-config/build.sbt) {}

### Example sandbox configuration

One sometimes useful technique with a configuration is adding a side graph
to the user's project so Coursier would download some JARs,
which your task can invoke. This is called a sandbox configuration.
This can be used for instance to invoke Scala 2.13 CLI version of scalafmt.
As of sbt 1.4.x there's a limitation so the sandbox configuration
must use the same Scala version as the user's subproject.

#### project/ScalafmtPlugin.scala

@@snip [ScalafmtPlugin.scala]($root$/src/sbt-test/ref/custom-config/project/ScalafmtPlugin.scala) {}

Enabling `ScalafmtPlugin` would add `scalafmt` task, which runs the CLI.

```
sbt:custom-configs> scalafmt --version
[info] running (fork) org.scalafmt.cli.Cli --version
[info] scalafmt 2.7.5
[success] Total time: 3 s, completed Feb 8, 2021 12:01:34 AM
sbt:custom-configs> scalafmt
[info] running (fork) org.scalafmt.cli.Cli
[info] Reformatting...
       Reformatting...
[success] Total time: 6 s, completed Feb 8, 2021 12:01:40 AM
```

### How do I add a test configuration?

See the [Additional test configurations][additional-test-configurations] section of
[Testing][Testing].

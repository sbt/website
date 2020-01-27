---
out: Artifacts.html
---

  [Library-Management]: Library-Management.html

Artifacts
---------

### Selecting default artifacts

By default, the published artifacts are the main binary jar, a jar
containing the main sources and resources, and a jar containing the API
documentation. You can add artifacts for the test classes, sources, or
API or you can disable some of the main artifacts.

To add all test artifacts:

```scala
lazy val app = (project in file("app"))
  .settings(
    Test / publishArtifact := true,
  )
```

To add them individually:

```scala
lazy val app = (project in file("app"))
  .settings(
    // enable publishing the jar produced by `Test/package`
    Test / packageBin / publishArtifact := true,

    // enable publishing the test API jar
    Test / packageDoc / publishArtifact := true,

    // enable publishing the test sources jar
    Test / packageSrc / publishArtifact := true,
  )
```

To disable main artifacts individually:

```scala
lazy val app = (project in file("app"))
  .settings(
    // disable publishing the main jar produced by `package`
    Compile / packageBin / publishArtifact := false,

    // disable publishing the main API jar
    Compile / packageDoc / publishArtifact := false,

    // disable publishing the main sources jar
    Compile / packageSrc / publishArtifact := false,
  )
```

### Modifying default artifacts

Each built-in artifact has several configurable settings in addition to
`publishArtifact`. The basic ones are `artifact` (of type
`SettingKey[Artifact]`), `mappings` (of type `TaskKey[(File, String)]`),
and `artifactPath` (of type `SettingKey[File]`).
They are scoped by `(Config / <task>)` as indicated in the previous section.

To modify the type of the main artifact, for example:

```scala
Compile / packageBin / artifact := {
  val prev: Artifact = (Compile / packageBin / artifact).value
  prev.withType("bundle")
}
```

The generated artifact name is determined by the `artifactName` setting.
This setting is of type `(ScalaVersion, ModuleID, Artifact) => String`.
The ScalaVersion argument provides the full Scala version String and the
binary compatible part of the version String. The String result is the
name of the file to produce. The default implementation is
`Artifact.artifactName _`. The function may be modified to produce
different local names for artifacts without affecting the published
name, which is determined by the `artifact` definition combined with the
repository pattern.

For example, to produce a minimal name without a classifier or cross
path:

```scala
artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
  artifact.name + "-" + module.revision + "." + artifact.extension
}
```

(Note that in practice you rarely want to drop the classifier.)

Finally, you can get the `(Artifact, File)` pair for the artifact by
mapping the `packagedArtifact` task. Note that if you don't need the
`Artifact`, you can get just the File from the package task (`package`,
`packageDoc`, or `packageSrc`). In both cases, mapping the task to get
the file ensures that the artifact is generated first and so the file is
guaranteed to be up-to-date.

For example:

```scala
val myTask = taskKey[Unit]("My task.")

myTask :=  {
  val (art, file) = (Compile / packageBin / packagedArtifact).value
  println("Artifact definition: " + art)
  println("Packaged file: " + file.getAbsolutePath)
}
```

### Defining custom artifacts

In addition to configuring the built-in artifacts, you can declare other
artifacts to publish. Multiple artifacts are allowed when using Ivy
metadata, but a Maven POM file only supports distinguishing artifacts
based on classifiers and these are not recorded in the POM.

Basic `Artifact` construction look like:

```scala
Artifact("name", "type", "extension")
Artifact("name", "classifier")
Artifact("name", url: URL)
Artifact("name", Map("extra1" -> "value1", "extra2" -> "value2"))
```

For example:

```scala
Artifact("myproject", "zip", "zip")
Artifact("myproject", "image", "jpg")
Artifact("myproject", "jdk15")
```

See the
[Ivy documentation](https://ant.apache.org/ivy/history/2.3.0/ivyfile/dependency-artifact.html)
for more details on artifacts. See the
[Artifact API](../api/sbt/librarymanagement/Artifact\$.html) for combining the
parameters above and specifying [Configurations] and extra attributes.

To declare these artifacts for publishing, map them to the task that
generates the artifact:

```scala
val myImageTask = taskKey[File](...)

myImageTask := {
  val artifact: File = makeArtifact(...)
  artifact
}

addArtifact(Artifact("myproject", "image", "jpg"), myImageTask)
```

`addArtifact` returns a sequence of settings (wrapped in a
[SettingsDefinition](../api/sbt/internal/util/Init\$SettingsDefinition.html)). In a
full build configuration, usage looks like:

```scala
lazy val app = (project in file("app"))
  .settings(
    addArtifact(...)
  )
```

### Publishing .war files

A common use case for web applications is to publish the `.war` file
instead of the `.jar` file.

```scala
lazy val app = (project in file("app"))
  .settings(
    // disable .jar publishing
    Compile / packageBin / publishArtifact := false,

    // create an Artifact for publishing the .war file
    Compile / packageWar / artifact := {
      val prev: Artifact = (Compile / packageWar / artifact).value
      prev.withType("war").withExtension("war")
    },

    // add the .war file to what gets published
    addArtifact(Compile / packageWar / artifact, packageWar),
  )
```

### Using dependencies with artifacts

To specify the artifacts to use from a dependency that has custom or
multiple artifacts, use the `artifacts` method on your dependencies. For
example:

```scala
libraryDependencies += ("org" % "name" % "rev").artifacts(Artifact("name", "type", "ext"))
```

The `from` and `classifer` methods (described on the
[Library Management][Library-Management] page) are actually convenience
methods that translate to `artifacts`:

```scala
def from(url: String) = artifacts(Artifact(name, new URL(url)))
def classifier(c: String) = artifacts(Artifact(name, c))
```

That is, the following two dependency declarations are equivalent:

```scala
libraryDependencies += ("org.testng" % "testng" % "5.7").classifier("jdk15")

libraryDependencies += ("org.testng" % "testng" % "5.7").artifacts(Artifact("testng", "jdk15"))
```

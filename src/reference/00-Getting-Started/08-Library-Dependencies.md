---
out: Library-Dependencies.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Task-Graph]: Task-Graph.html
  [external-maven-ivy]: ../docs/Library-Management.html#External+Maven+or+Ivy
  [Cross-Build]: ../docs/Cross-Build.html
  [Resolvers]: ../docs/Resolvers.html
  [Library-Management]: ../docs/Library-Management.html

Library dependencies
--------------------

This page assumes you've already read the earlier Getting Started pages, in
particular [build definition][Basic-Def], [scopes][Scopes], and
[task graph][Task-Graph].

Library dependencies can be added in two ways:

-   *unmanaged dependencies* are jars dropped into the `lib` directory
-   *managed dependencies* are configured in the build definition and
    downloaded automatically from repositories

### Unmanaged dependencies

Most people use managed dependencies instead of unmanaged. But unmanaged
can be simpler when starting out.

Unmanaged dependencies work like this: add jars to `lib` and they will be
placed on the project classpath. Not much else to it!

You can place test jars such as
[ScalaCheck](https://scalacheck.org/),
[Specs2](http://specs2.org), and
[ScalaTest](https://www.scalatest.org/) in `lib` as well.

Dependencies in `lib` go on all the classpaths (for `compile`, `test`, `run`,
and `console`). If you wanted to change the classpath for just one of
those, you would adjust `Compile / dependencyClasspath` or
`Runtime / dependencyClasspath` for example.

There's nothing to add to `build.sbt` to use unmanaged dependencies,
though you could change the `unmanagedBase` key if you'd like to use a
different directory rather than `lib`.

To use `custom_lib` instead of `lib`:

```scala
unmanagedBase := baseDirectory.value / "custom_lib"
```

`baseDirectory` is the project's root directory, so here you're changing
`unmanagedBase` depending on `baseDirectory` using the special `value` method
as explained in [task graph][Task-Graph].

There's also an `unmanagedJars` task which lists the jars from the
`unmanagedBase` directory. If you wanted to use multiple directories or do
something else complex, you might need to replace the whole
`unmanagedJars` task with one that does something else, e.g. empty the list for
`Compile` configuration regardless of the files in `lib` directory:

```scala
Compile / unmanagedJars := Seq.empty[sbt.Attributed[java.io.File]]
```

### Managed Dependencies

sbt uses [Coursier](https://get-coursier.io/) to implement managed
dependencies, so if you're familiar with Coursier, Apache Ivy or Maven, you won't have
much trouble.

#### The `libraryDependencies` key

Most of the time, you can simply list your dependencies in the setting
`libraryDependencies`. It's also possible to write a Maven POM file or Ivy
configuration file to externally configure your dependencies, and have
sbt use those external configuration files. You can learn more about
that [here][external-maven-ivy].

Declaring a dependency looks like this, where `groupId`, `artifactId`, and
`revision` are strings:

```scala
libraryDependencies += groupID % artifactID % revision
```

or like this, where `configuration` can be a string or a `Configuration` value (such as `Test`):

```scala
libraryDependencies += groupID % artifactID % revision % configuration
```

`libraryDependencies` is declared in
[Keys](../api/sbt/Keys\$.html#libraryDependencies:sbt.SettingKey[Seq[sbt.librarymanagement.ModuleID]]) like
this:

```scala
val libraryDependencies = settingKey[Seq[ModuleID]]("Declares managed dependencies.")
```

The `%` methods create `ModuleID` objects from strings, then you add those
`ModuleID` to `libraryDependencies`.

Of course, sbt (via Coursier) has to know where to download the module. If
your module is in one of the default repositories sbt comes with, this
will just work. For example, Apache Derby is in the standard Maven2
repository:

```scala
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3"
```

If you type that in `build.sbt` and then `update`, sbt should download Derby
to [the Coursier cache](https://get-coursier.io/docs/cache). (By the way, `update` is a dependency
of `compile` so there's no need to manually type `update` most of the time.)

Of course, you can also use `++=` to add a list of dependencies all at
once:

```scala
libraryDependencies ++= Seq(
  groupID % artifactID % revision,
  groupID % otherID % otherRevision
)
```

In rare cases you might find reasons to use `:=` with `libraryDependencies`
as well.

#### Getting the right Scala version with `%%`

If you use `organization %% moduleName % version` rather than
`organization % moduleName % version` (the difference is the double `%%` after
the `organization`), sbt will add your project's binary Scala version to the artifact
name. This is just a shortcut. You could write this without the `%%`:

```scala
libraryDependencies += "org.scala-stm" % "scala-stm_2.13" % "$example_scala_stm_version$"
```

Assuming the `scalaVersion` for your build is `$example_scala213$`, the following is
identical (note the double `%%` after `"org.scala-stm"`):

```scala
libraryDependencies += "org.scala-stm" %% "scala-stm" % "$example_scala_stm_version$"
```

The idea is that many dependencies are compiled for multiple Scala
versions, and you'd like to get the one that matches your project
to ensure binary compatibility.

See [Cross Building][Cross-Build] for some more detail on this.

#### Ivy revisions

The `version` in `organization % moduleName % version` does not have to be a
single fixed version. Ivy can select the latest revision of a module
according to constraints you specify. Instead of a fixed revision like
`"1.6.1"`, you specify `"latest.integration"`, `"2.9.+"`, or `"[1.0,)"`. See the
[Ivy
revisions](https://ant.apache.org/ivy/history/2.3.0/ivyfile/dependency.html#revision)
documentation for details.

<!-- TODO: Add aliases -->

Occasionally a Maven "version range" is used to specify a dependency
(transitive or otherwise), such as `[1.3.0,)`.  If a specific version
of the dependency is declared in the build, and it satisfies the
range, then sbt will use the specified version.  Otherwise, Coursier could
go out to the Internet to find the latest version.  This would result
to a surprising behavior where the effective version keeps changing
over time, even though there's a specified version of the library that
satisfies the range condition.

Maven version ranges will be replaced with its lower bound if the
build so that when a satisfactory version is found in the dependency
graph it will be used.  You can disable this behavior using the JVM
flag `-Dsbt.modversionrange=false`.

#### Resolvers

Not all packages live on the same server; sbt uses the standard Maven2
repository by default. If your dependency isn't on one of the default
repositories, you'll have to add a *resolver* to help Ivy find it.

To add an additional repository, use

```scala
resolvers += name at location
```

with the special `at` between two strings.

For example:

```scala
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
```

The `resolvers` key is defined in
[Keys](../api/sbt/Keys\$.html#resolvers:sbt.SettingKey[Seq[sbt.librarymanagement.Resolver]]) like this:

```scala
val resolvers = settingKey[Seq[Resolver]]("The user-defined additional resolvers for automatically managed dependencies.")
```

The `at` method creates a `Resolver` object from two strings.

sbt can search your local Maven repository if you add it as a
repository:

```scala
resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
```

or, for convenience:

```scala
resolvers += Resolver.mavenLocal
```

See [Resolvers][Resolvers] for details on defining other types of
repositories.

#### Overriding default resolvers

`resolvers` does not contain the default resolvers; only additional ones
added by your build definition.

sbt combines `resolvers` with some default repositories to form
`externalResolvers`.

Therefore, to change or remove the default resolvers, you would need to
override `externalResolvers` instead of `resolvers`.

#### Per-configuration dependencies

Often a dependency is used by your test code (in `src/test/scala`, which
is compiled by the `Test` configuration) but not your main code.

If you want a dependency to show up in the classpath only for the `Test`
configuration and not the `Compile` configuration, add `% "test"` like this:

```scala
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % "test"
```

You may also use the type-safe version of `Test` configuration as follows:

```scala
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % Test
```

Now, if you type `show Compile/dependencyClasspath` at the sbt interactive
prompt, you should not see the derby jar. But if you type
`show Test/dependencyClasspath`, you should see the derby jar in the list.

Typically, test-related dependencies such as
[ScalaCheck](https://scalacheck.org/),
[Specs2](http://specs2.org), and
[ScalaTest](https://www.scalatest.org/) would be defined with `% "test"`.

There are more details and tips-and-tricks related to library
dependencies on [this page][Library-Management].

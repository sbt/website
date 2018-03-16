---
out: Library-Management.html
---

  [Setup]: Setup.html
  [Basic-Def]: Basic-Def.html
  [Full-Def]: Full-Def.html
  [Library-Dependencies]: Library-Dependencies.html
  [Update-Report]: Update-Report.html
  [Paths]: Paths.html
  [Resolvers]: Resolvers.html
  [Publishing]: Publishing.html
  [Cross-Build]: Cross-Build.html
  [Cached-Resolution]: Cached-Resolution.html

ライブラリの管理
------------------

「始める sbt」でも[ライブラリ依存性][Library-Dependencies]に関して紹介している。
まだ読んでいなければ、そちらを先に読んでおくと良いだろう。

*ドキュメントのメンテナンスに係る注意* 

このページと「始める sbt」にはいくつか重複がある。
重複を排除できるとよいが、こちらのページではチェックサムや外部のIvyファイルなど、発展的なトピックを扱う。

### はじめに
sbtでライブラリを管理する方法は2つある。
一つは手動で管理する方法，もう一つは自動で管理する方法で、これらは組み合わせることもできる。このページではこれら2つの方法についてそれぞれ解説する。
なお、このページに記載されている設定は、[.sbt ファイル][Basic-Def]に直接書くか，[.scala ファイル][Full-Def]中の`Project.settings`に追加することで動作する。


### 手動で依存性を管理する

手動で依存性を管理するには、利用したいjarファイルを`lib`ディレクトリに配置する。
sbtはコンパイル時や、テスト時、コードを走らせる際や、インタープリタを利用する間、これらのjarファイルをクラスパス上に配置してくれる。
このディレクトリに対するjarの追加、削除、更新など、管理は自身で行う必要がある。
この方法なら、jarを配置するディレクトリを他のディレクトリに変えたい場合以外でプロジェクトの定義を変更する必要はない。

jarを配置するディレクトリを変更したい場合は、プロジェクト定義の中の`unmanagedBase`という設定を変更する。
`comtom_lib/`というディレクトリを使うには次のような設定になる。

```scala
unmanagedBase := baseDirectory.value / "custom_lib"
```

さらに細かい制御をしたい場合は、`unmanagedJars`タスクをoverrideする。
このタスクがsbtに手動で管理する依存性を渡すための全ての制御を担っている。
このタスクはデフォルトではおおよそ次のような実装になっている。

```scala
Compile / unmanagedJars := (baseDirectory.value ** "*.jar").classpath
```

デフォルトディレクトリに加えて、他の複数のディレクトリからjarを追加したい場合は次のようにすればよい。

```scala
Compile / unmanagedJars ++= {
    val base = baseDirectory.value
    val baseDirectories = (base / "libA") +++ (base / "b" / "lib") +++ (base / "libC")
    val customJars = (baseDirectories ** "*.jar") +++ (base / "d" / "my.jar")
    customJars.classpath
}
```

パスの記述に関しては、[こちら][Paths]に詳細なドキュメントがある。

### 自動で依存性を管理する

こちらの方法では、自身のプロジェクトが直接依存しているライブラリを記述する。
すると、sbtはこれらの依存ライブラリを取得し、自動で更新してくれる。
これらの依存性は次の3つの方法で記述できる。．

-   プロジェクト定義中で宣言する。
-   MavenのPOMファイルを利用する (ただしPOMファイルで記述できるのはライブラリの依存性のみで、リポジトリは記述できない)
-   Ivyのコンフィギュレーションや設定ファイル

sbtは、これら全ての方法を[Apache Ivy](https://ant.apache.org/ivy/)を使って実装している。
デフォルトではプロジェクト定義中のインライン宣言が使われるが、外部のコンフィギュレーションを明示的に指定することもできる。
次節以降では、自動で依存性を管理するための各方法について説明する。

#### インライン宣言

インライン宣言は、自動で取得する依存性を記述するためのもっとも基本的な方法だ。
この機能はIvyの全てのコンフィギュレーションを記述するよりも、少ない労力で依存性を記述できるように作られたものだ。

##### 依存性の記述

依存性を宣言するには次のように記述する。

```scala
libraryDependencies += groupID % artifactID % revision
```

もしくは

```scala
libraryDependencies += groupID % artifactID % revision % configuration
```

コンフィギュレーションについての詳細は、[configurations](#ivy-configurations)を参照してほしい。
もちろん複数の依存性を記述することも可能だ。

```scala
libraryDependencies ++= Seq(
  groupID %% artifactID % revision,
  groupID %% otherID % otherRevision
)
```

もしsbtでビルドされた依存性を利用している場合は、次のように最初の`%`の代わりに`%%`を使うこともできる。

```scala
libraryDependencies += groupID %% artifactID % revision
```

このように記述しておくと，sbtは自身が利用しているScalaのバージョンに対してビルドされたjarを自動的に選択してくれる。
この記述でエラーが出た場合，利用しているScalaのバージョンに対してビルドされたjarがpublishされていない可能性が高い．
これに関する詳細は、[Cross Build][Cross-Build]を参照して欲しい。

Ivyには、リビジョンに関する制約を記述しておくことで、その制約下での最新のリビジョンを選択してくれる機能がある。
`"1.6.1"`のような固定のリビジョンを指定する代わりに、`"latest.integration"`, `"2.9.+"`, or `"[1.0,)"`のような制約を記述できる。
これに関する詳細は、[Ivy revisions](https://ant.apache.org/ivy/history/2.3.0/ivyfile/dependency.html#revision)を見て欲しい.


##### Resolvers

sbtはデフォルトでは標準のMaven2リポジトリを利用する。

リポジトリを追加するには、次のように記述する。

```scala
resolvers += name at location
```

具体的な例は次の通りだ。

```scala
libraryDependencies ++= Seq(
    "org.apache.derby" % "derby" % "10.4.1.3",
    "org.specs" % "specs" % "1.6.1"
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
```

リポジトリとして追加すると、sbtはlocalのMavenリポジトリも検索するようになる。

```scala
resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
```

これら以外のリポジトリの記述方法については、[Resolvers][Resolvers]に詳しく書いてある。


##### Override default resolvers

`resolvers` configures additional, inline user resolvers. By default,
`sbt` combines these resolvers with default repositories (Maven Central
and the local Ivy repository) to form `externalResolvers`. To have more
control over repositories, set `externalResolvers` directly. To only
specify repositories in addition to the usual defaults, configure
`resolvers`.

For example, to use the Sonatype OSS Snapshots repository in addition to
the default repositories,

```scala
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
```

To use the local repository, but not the Maven Central repository:

```scala
externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = false)
```

##### Override all resolvers for all builds

The repositories used to retrieve sbt, Scala, plugins, and application
dependencies can be configured globally and declared to override the
resolvers configured in a build or plugin definition. There are two
parts:

1.  Define the repositories used by the launcher.
2.  Specify that these repositories should override those in build
    definitions.

The repositories used by the launcher can be overridden by defining
`~/.sbt/repositories`, which must contain a `[repositories]` section
with the same format as the `Launcher` configuration file. For example:

```
[repositories]
local
my-maven-repo: https://example.org/repo
my-ivy-repo: https://example.org/ivy-repo/, [organization]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext]
```

A different location for the repositories file may be specified by the
`sbt.repository.config` system property in the sbt startup script. The
final step is to set `sbt.override.build.repos` to true to use these
repositories for dependency resolution and retrieval.

##### Explicit URL

If your project requires a dependency that is not present in a
repository, a direct URL to its jar can be specified as follows:

```scala
libraryDependencies += "slinky" % "slinky" % "2.1" from "https://slinky2.googlecode.com/svn/artifacts/2.1/slinky.jar"
```

The URL is only used as a fallback if the dependency cannot be found
through the configured repositories. Also, the explicit URL is not
included in published metadata (that is, the pom or ivy.xml).

##### Disable Transitivity

By default, these declarations fetch all project dependencies,
transitively. In some instances, you may find that the dependencies
listed for a project aren't necessary for it to build. Projects using
the Felix OSGI framework, for instance, only explicitly require its main
jar to compile and run. Avoid fetching artifact dependencies with either
`intransitive()` or `notTransitive()`, as in this example:

```scala
libraryDependencies += "org.apache.felix" % "org.apache.felix.framework" % "1.8.0" intransitive()
```

##### Classifiers

You can specify the classifier for a dependency using the `classifier`
method. For example, to get the jdk15 version of TestNG:

```scala
libraryDependencies += "org.testng" % "testng" % "5.7" classifier "jdk15"
```

For multiple classifiers, use multiple `classifier` calls:

```scala
libraryDependencies += 
  "org.lwjgl.lwjgl" % "lwjgl-platform" % lwjglVersion classifier "natives-windows" classifier "natives-linux" classifier "natives-osx"
```

To obtain particular classifiers for all dependencies transitively, run
the `updateClassifiers` task. By default, this resolves all artifacts
with the `sources` or `javadoc` classifier. Select the classifiers to
obtain by configuring the `transitiveClassifiers` setting. For example,
to only retrieve sources:

```scala
transitiveClassifiers := Seq("sources")
```

##### Exclude Transitive Dependencies

To exclude certain transitive dependencies of a dependency, use the
`excludeAll` or `exclude` methods. The `exclude` method should be used
when a pom will be published for the project. It requires the
organization and module name to exclude. For example,

```scala
libraryDependencies += 
  "log4j" % "log4j" % "1.2.15" exclude("javax.jms", "jms")
```

The `excludeAll` method is more flexible, but because it cannot be
represented in a pom.xml, it should only be used when a pom doesn't need
to be generated. For example,

```scala
libraryDependencies +=
  "log4j" % "log4j" % "1.2.15" excludeAll(
    ExclusionRule(organization = "com.sun.jdmk"),
    ExclusionRule(organization = "com.sun.jmx"),
    ExclusionRule(organization = "javax.jms")
  )
```

See [ModuleID](../api/sbt/librarymanagement/ModuleID.html) for API details.

In certain cases a transitive dependency should be excluded from
all dependencies. This can be achieved by setting up `ExclusionRules`
in `excludeDependencies`. 

```scala
excludeDependencies ++= Seq(
  // commons-logging is replaced by jcl-over-slf4j
  ExclusionRule("commons-logging", "commons-logging")
)
```

##### Download Sources

Downloading source and API documentation jars is usually handled by an
IDE plugin. These plugins use the `updateClassifiers` and
`updateSbtClassifiers` tasks, which produce an `Update-Report`
referencing these jars.

To have sbt download the dependency's sources without using an IDE
plugin, add `withSources()` to the dependency definition. For API jars,
add `withJavadoc()`. For example:

```scala
libraryDependencies += 
  "org.apache.felix" % "org.apache.felix.framework" % "1.8.0" withSources() withJavadoc()
```

Note that this is not transitive. Use the `update-*classifiers` tasks
for that.

##### Extra Attributes

[Extra attributes](https://ant.apache.org/ivy/history/2.3.0/concept.html#extra)
can be specified by passing key/value pairs to the `extra` method.

To select dependencies by extra attributes:

```scala
libraryDependencies += "org" % "name" % "rev" extra("color" -> "blue")
```

To define extra attributes on the current project:

```scala
projectID := {
    val previous = projectID.value
    previous.extra("color" -> "blue", "component" -> "compiler-interface")
}
```

##### Inline Ivy XML

sbt additionally supports directly specifying the configurations or
dependencies sections of an Ivy configuration file inline. You can mix
this with inline Scala dependency and repository declarations.

For example:

```scala
ivyXML :=
  <dependencies>
    <dependency org="javax.mail" name="mail" rev="1.4.2">
      <exclude module="activation"/>
    </dependency>
  </dependencies>
```

##### Ivy Home Directory

By default, sbt uses the standard Ivy home directory location
`\${user.home}/.ivy2/`. This can be configured machine-wide, for use by
both the sbt launcher and by projects, by setting the system property
`sbt.ivy.home` in the sbt startup script (described in
[Setup][Setup]).

For example:

```
java -Dsbt.ivy.home=/tmp/.ivy2/ ...
```

##### Checksums

sbt
([through Ivy](https://ant.apache.org/ivy/history/latest-milestone/concept.html#checksum))
verifies the checksums of downloaded files by default. It also publishes
checksums of artifacts by default. The checksums to use are specified by
the *checksums* setting.

To disable checksum checking during update:

```scala
update / checksums := Nil
```

To disable checksum creation during artifact publishing:

```scala
publishLocal / checksums := Nil

publish / checksums := Nil
```

The default value is:

```scala
checksums := Seq("sha1", "md5")
```

<a name="conflict-management"></a>

##### Conflict Management

The conflict manager decides what to do when dependency resolution
brings in different versions of the same library. By default, the latest
revision is selected. This can be changed by setting `conflictManager`,
which has type [ConflictManager](../api/sbt/librarymanagement/ConflictManager.html).
See the
[Ivy documentation](https://ant.apache.org/ivy/history/latest-milestone/settings/conflict-managers.html)
for details on the different conflict managers. For example, to specify
that no conflicts are allowed,

```scala
conflictManager := ConflictManager.strict
```

With this set, any conflicts will generate an error. To resolve a
conflict, you must configure a dependency override, which is explained in a later section.

<a name="eviction-warning"></a>

##### Eviction warning

The following direct dependencies will introduce a conflict on the akka-actor
version because banana-rdf requires akka-actor 2.1.4.

```scala
libraryDependencies ++= Seq(
  "org.w3" %% "banana-rdf" % "0.4",
  "com.typesafe.akka" %% "akka-actor" % "2.3.7",
)
```

The default conflict manager will select the newer version of akka-actor,
2.3.7. This can be confirmed in the output of `show update`, which
shows the newer version as being selected and the older version as evicted.

```
> show update
[info] compile:

[info]  com.typesafe.akka:akka-actor_2.10
[info]    - 2.3.7
...
[info]    - 2.1.4
...
[info]      evicted: true
[info]      evictedReason: latest-revision
...
[info]      callers: org.w3:banana-rdf_2.10:0.4
```

Furthermore, the binary version compatibility of the akka-actor 2.1.4 and 2.3.7 are not guaranteed since the second segment has bumped up. sbt 0.13.6+ detects this automatically and prints out the following warning:

```
[warn] There may be incompatibilities among your library dependencies.
[warn] Here are some of the libraries that were evicted:
[warn]  * com.typesafe.akka:akka-actor_2.10:2.1.4 -> 2.3.7
[warn] Run 'evicted' to see detailed eviction warnings
```

Since akka-actor 2.1.4 and 2.3.7 are not binary compatible, the only way to fix this is to downgrade your dependency to akka-actor 2.1.4, or upgrade banana-rdf to use akka-actor 2.3.

##### Overriding a version

For binary compatible conflicts, sbt provides dependency overrides.
They are configured with the
`dependencyOverrides` setting, which is a set of `ModuleIDs`. For
example, the following dependency definitions conflict because spark
uses log4j 1.2.16 and scalaxb uses log4j 1.2.17:

```scala
libraryDependencies ++= Seq(
   "org.spark-project" %% "spark-core" % "0.5.1",
   "org.scalaxb" %% "scalaxb" % "1.0.0"
)
```

The default conflict manager chooses the latest revision of log4j,
1.2.17:

```
> show update
[info] compile:
[info]    log4j:log4j:1.2.17: ...
...
[info]    (EVICTED) log4j:log4j:1.2.16
...
```

To change the version selected, add an override:

```scala
dependencyOverrides += "log4j" % "log4j" % "1.2.16"
```

This will not add a direct dependency on log4j, but will force the
revision to be 1.2.16. This is confirmed by the output of `show update`:

```
> show update
[info] compile:
[info]    log4j:log4j:1.2.16
...
```

> **Note:** this is an Ivy-only feature and will not be included in a
> published pom.xml.

##### Unresolved dependencies error

Adding the following dependency to your project will result to an unresolved dependencies error of vpp 2.2.1:

```scala
libraryDependencies += "org.apache.cayenne.plugins" % "maven-cayenne-plugin" % "3.0.2"
```

sbt 0.13.6+ will try to reconstruct dependencies tree when it fails to resolve a managed dependency. This is an approximation, but it should help you figure out where the problematic dependency is coming from. When possible sbt will display the source position next to the modules:

```
[warn]  ::::::::::::::::::::::::::::::::::::::::::::::
[warn]  ::          UNRESOLVED DEPENDENCIES         ::
[warn]  ::::::::::::::::::::::::::::::::::::::::::::::
[warn]  :: foundrylogic.vpp#vpp;2.2.1: not found
[warn]  ::::::::::::::::::::::::::::::::::::::::::::::
[warn] 
[warn]  Note: Unresolved dependencies path:
[warn]      foundrylogic.vpp:vpp:2.2.1
[warn]        +- org.apache.cayenne:cayenne-tools:3.0.2
[warn]        +- org.apache.cayenne.plugins:maven-cayenne-plugin:3.0.2 (/foo/some-test/build.sbt#L28)
[warn]        +- d:d_2.10:0.1-SNAPSHOT
```

##### Cached resolution

See [Cached resolution][Cached-Resolution] for performance improvement option.

##### Publishing

See [Publishing][Publishing] for how to publish your project.

<a name="ivy-configurations"></a>

##### Configurations

Ivy configurations are a useful feature for your build when you need
custom groups of dependencies, such as for a plugin. Ivy configurations
are essentially named sets of dependencies. You can read the
[Ivy documentation](https://ant.apache.org/ivy/history/2.3.0/tutorial/conf.html)
for details.

The built-in use of configurations in sbt is similar to scopes in Maven.
sbt adds dependencies to different classpaths by the configuration that
they are defined in. See the description of
[Maven Scopes](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Scope)
for details.

You put a dependency in a configuration by selecting one or more of its
configurations to map to one or more of your project's configurations.
The most common case is to have one of your configurations `A` use a
dependency's configuration `B`. The mapping for this looks like
`"A->B"`. To apply this mapping to a dependency, add it to the end of
your dependency definition:

```scala
libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.3" % "test->compile"
```

This says that your project's `"test"` configuration uses `ScalaTest`'s
`"compile"` configuration. See the
[Ivy documentation](https://ant.apache.org/ivy/history/2.3.0/tutorial/conf.html)
for more advanced mappings. Most projects published to Maven
repositories will use the `"compile"` configuration.

A useful application of configurations is to group dependencies that are
not used on normal classpaths. For example, your project might use a
`"js"` configuration to automatically download jQuery and then include
it in your jar by modifying `resources`. For example:

```scala
val JS = config("js") hide

ivyConfigurations += JS

libraryDependencies += "jquery" % "jquery" % "3.2.1" % "js->default" from "https://code.jquery.com/jquery-3.2.1.min.js"

Compile / resources ++= update.value.select(configurationFilter("js"))
```

The `config` method defines a new configuration with name `"js"` and
makes it private to the project so that it is not used for publishing.
See [Update Report][Update-Report] for more information on selecting
managed artifacts.

A configuration without a mapping (no `"->"`) is mapped to `"default"`
or `"compile"`. The `->` is only needed when mapping to a different
configuration than those. The ScalaTest dependency above can then be
shortened to:

```scala
libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.3" % "test"
```

#### External Maven or Ivy

For this method, create the configuration files as you would for Maven
(`pom.xml`) or Ivy (`ivy.xml` and optionally `ivysettings.xml`).
External configuration is selected by using one of the following
expressions.

##### Ivy settings (resolver configuration)

```scala
externalIvySettings()
```

or

```scala
externalIvySettings(baseDirectory.value / "custom-settings-name.xml")
```

or

```scala
externalIvySettingsURL(url("your_url_here"))
```

##### Ivy file (dependency configuration)

```scala
externalIvyFile()
```

or

```scala
externalIvyFile(Def.setting(baseDirectory.value / "custom-name.xml"))
```

Because Ivy files specify their own configurations, sbt needs to know
which configurations to use for the `compile`, `runtime`, and `test`
classpaths. For example, to specify that the `Compile` classpath should
use the 'default' configuration:

```scala
Compile / classpathConfiguration := config("default")
```

##### Maven pom (dependencies only)

```scala
externalPom()
```

or

```scala
externalPom(Def.setting(baseDirectory.value / "custom-name.xml"))
```

##### Full Ivy Example

For example, a `build.sbt` using external Ivy files might look like:

```scala
externalIvySettings()
externalIvyFile(Def.setting(baseDirectory.value / "ivyA.xml"))
Compile / classpathConfiguration := Compile
Test / classpathConfiguration := Test
Runtime / classpathConfiguration := Runtime
```

##### Forcing a revision (Not recommended)

**Note**: Forcing can create logical inconsistencies so it's no longer recommended.

To say that we prefer the version we've specified over the version from
indirect dependencies, use `force()`:

```scala
libraryDependencies ++= Seq(
  "org.spark-project" %% "spark-core" % "0.5.1",
  "log4j" % "log4j" % "1.2.14" force()
)
```

**Note:** this is an Ivy-only feature and cannot be included in a
published pom.xml.

##### Known limitations

Maven support is dependent on Ivy's support for Maven POMs. Known issues
with this support:

-   Specifying `relativePath` in the `parent` section of a POM will
    produce an error.
-   Ivy ignores repositories specified in the POM. A workaround is to
    specify repositories inline or in an Ivy `ivysettings.xml` file.


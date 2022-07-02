---
out: sbt-new-and-Templates.html
---

  [giter8]: https://www.foundweekends.org/giter8/
  [foundweekends]: https://www.foundweekends.org/
  [CC0]: https://creativecommons.org/publicdomain/zero/1.0/

sbt new and Templates
---------------------

sbt 0.13.13 adds a new command called new, to create new build definitions from a template.
The `new` command is extensible via a mechanism called the [template resolver](#Template+Resolver).

### Trying new command

First, you need sbt's launcher version 0.13.13 or above.
Normally the exact version for the `sbt` launcher does not matter
because it will use the version specified by `sbt.version` in `project/build.properties`;
however for new sbt's launcher 0.13.13 or above is required as the command functions without a `project/build.properties` present.

Next, run:

```
\$ sbt new scala/scala-seed.g8
....
name [hello]:

Template applied in ./hello
```

This ran the template [scala/scala-seed.g8](https://github.com/scala/scala-seed.g8) using [Giter8][giter8], prompted for values for "name" (which has a default value of "hello", which we accepted hitting `[Enter]`), and created a build under `./hello`.

`scala-seed` is the official template for a "minimal" Scala project, but it's definitely not the only one out there.

### Giter8 support

[Giter8][giter8] is a templating project originally started by Nathan Hamblen in 2010, and now maintained by the [foundweekends][foundweekends] project.
The unique aspect of Giter8 is that it uses GitHub (or any other git repository) to host the templates, so it allows anyone to participate in template creation. Here are some of the templates provided by official sources:

- [foundweekends/giter8.g8](https://github.com/foundweekends/giter8.g8)                 (A template for Giter8 templates)
- [scala/scala-seed.g8](https://github.com/scala/scala-seed.g8)                         (Seed template for Scala)
- [scala/scala3.g8](https://github.com/scala/scala3.g8)                                 (A template for Scala 3 projects)
- [scala/hello-world.g8](https://github.com/scala/hello-world.g8)                       (A template to demonstrate a minimal Scala application)
- [scala/scalatest-example.g8](https://github.com/scala/scalatest-example.g8)           (A template for trying out ScalaTest)
- [akka/akka-scala-seed.g8](https://github.com/akka/akka-scala-seed.g8)                 (A minimal seed template for an Akka with Scala build
)
- [akka/akka-java-seed.g8](https://github.com/akka/akka-java-seed.g8)                   (A minimal seed template for an Akka in Java
)
- [playframework/play-scala-seed.g8](https://github.com/playframework/play-scala-seed.g8) (Play Scala Seed Template)
- [playframework/play-java-seed.g8](https://github.com/playframework/play-java-seed.g8)   (Play Java Seed template)
- [lagom/lagom-scala.g8](https://github.com/lagom/lagom-scala.g8/)                      (A [Lagom](https://www.lagomframework.com/) Scala seed template for sbt)
- [lagom/lagom-java.g8](https://github.com/lagom/lagom-java.g8/)                        (A [Lagom](https://www.lagomframework.com/) Java seed template for sbt)
- [scala-native/scala-native.g8](https://github.com/scala-native/scala-native.g8)       (Scala Native)
- [scala-native/sbt-crossproject.g8](https://github.com/scala-native/sbt-crossproject.g8) (sbt-crosspoject)
- [http4s/http4s.g8](https://github.com/http4s/http4s.g8)                               (http4s services)
- [unfiltered/unfiltered.g8](https://github.com/unfiltered/unfiltered.g8)               ([Unfiltered](https://unfiltered.ws/) application)
- [scalatra/scalatra-sbt.g8](https://github.com/scalatra/scalatra-sbt.g8)               (Basic Scalatra template using SBT 0.13.x.)

For more, see [Giter8 templates](https://github.com/foundweekends/giter8/wiki/giter8-templates) on the Giter8 wiki. sbt provides out-of-the-box support for Giter8 templates by shipping with a template resolver for Giter8.

#### Giter8 parameters

You can append Giter8 parameters to the end of the command, so for example to specify a particular branch you can use:

```
\$ sbt new scala/scala-seed.g8 --branch myBranch
```

#### How to create a Giter8 template

See [Making your own templates](https://www.foundweekends.org/giter8/template.html) for the details on how to create a new Giter8 template.

```
\$ sbt new foundweekends/giter8.g8
```

#### Use CC0 1.0 for template licensing

We recommend licensing software templates under [CC0 1.0][CC0],
which waives all copyrights and related rights, similar to the "public domain."

If you reside in a country covered by the Berne Convention, such as the US,
copyright will arise automatically without registration.
Thus, people won't have legal right to use your template if you do not
declare the terms of license.
The tricky thing is that even permissive licenses such as MIT License and Apache License will require attribution to your template in the template user's software.
To remove all claims to the templated snippets, distribute it under CC0, which is an international equivalent to public domain.

```
License
-------
Written in <YEAR> by <AUTHOR NAME> <AUTHOR E-MAIL ADDRESS>
[other author/contributor lines as appropriate]
To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <https://creativecommons.org/publicdomain/zero/1.0/>.
```

### How to extend sbt new

The rest of this page explains how to extend the `sbt new` command
to provide support for something other than Giter8 templates.
You can skip this section if you're not interested in extending `new`.

#### Template Resolver

A template resolver is a partial function that looks at the arguments
after `sbt new` and determines whether it can resolve to a particular template. This is analogous to `resolvers` resolving a `ModuleID` from the Internet.

The `Giter8TemplateResolver` takes the first argument that does not start with a hyphen (`-`), and checks whether it looks like
a GitHub repo or a git repo that ends in ".g8".
If it matches one of the patterns, it will pass the arguments to Giter8 to process.

To create your own template resolver, create a library that has `template-resolver` as a dependency:

```scala
val templateResolverApi = "org.scala-sbt" % "template-resolver" % "0.1"
```

and extend `TemplateResolver`, which is defined as:

```java
package sbt.template;

/** A way of specifying template resolver.
 */
public interface TemplateResolver {
  /** Returns true if this resolver can resolve the given argument.
   */
  public boolean isDefined(String[] arguments);
  /** Resolve the given argument and run the template.
   */
  public void run(String[] arguments);
}
```

Publish the library to sbt community repo or Maven Central.

#### templateResolverInfos

Next, create an sbt plugin that adds a `TemplateResolverInfo` to `templateResolverInfos`.

```scala
import Def.Setting
import Keys._

/** An experimental plugin that adds the ability for Giter8 templates to be resolved
 */
object Giter8TemplatePlugin extends AutoPlugin {
  override def requires = CorePlugin
  override def trigger = allRequirements

  override lazy val globalSettings: Seq[Setting[_]] =
    Seq(
      templateResolverInfos +=
        TemplateResolverInfo(ModuleID("org.scala-sbt.sbt-giter8-resolver", "sbt-giter8-resolver", "0.1.0") cross CrossVersion.binary,
          "sbtgiter8resolver.Giter8TemplateResolver")
    )
}
```

This indirecton allows template resolvers to have a classpath independent from the rest of the build.

---
out: sbt-new-and-Templates.html
---

  [giter8]: http://www.foundweekends.org/giter8/
  [foundweekends]: http://www.foundweekends.org/
  [CC0]: https://creativecommons.org/publicdomain/zero/1.0/

sbt new and Templates
---------------------

sbt 0.13.13 adds `new` command, which helps create new build definitions.
The `new` command is extensible via a mechanism called the template resolver.

### Trying new command

To get started, you need to first install sbt 0.13.13 launcher or above.
Normally the exact version for the `sbt` launcher does not matter
because it will use the one specified in `project/build.properties`;
however in this case we would need 0.13.13+ `sbt` because we would
want it to work without `project/build.properties`.

Next, run:

```
\$ sbt new eed3si9n/hello.g8
....
name [hello]:
scala_version [2.11.8]:

Template applied in ./hello
```

This ran the template [eed3si9n/hello.g8](https://github.com/eed3si9n/hello.g8) using [Giter8][giter8], and create a build under `./hello`.

### Giter8 support

[Giter8][giter8] is a templating project originally started by Nathan Hamblen in 2010, and now maintained by [foundweekends][foundweekends] project.
The unique aspect of Giter8 is that it uses Github (or any other git repository) to host the templates, so it allows anyone to participate in template creation.

sbt provides support for Giter8 templates as a reference implementation of the template resolver concept.

#### How to create a Giter8 template

See [Making your own templates](http://www.foundweekends.org/giter8/template.html) for the details on creating a new Giter8 template.

```
\$ sbt new foundweekends/giter8.g8
```

#### Use CC0 1.0 for template licensing

We recommend licensing software templates under [CC0 1.0][CC0],
which waives all copyrights and related rights, similar to the "public domain."

If you reside in a country covered by Berne Convention, such as the US,
copyright will arise automatically without registration.
Thus, people won't have legal right to use your template if you do not
declare the terms of license.
The tricky thing is that even the most permissive license such as MIT License and Apache License will require attribution in the software.
To remove all claims to the templated snippets, distribute it under CC0, which is an international equivalent to public domain.

```
License
-------
Copyright 20XX, Foo, Inc.
This template is distributed under [CC0 1.0](https://creativecommons.org/publicdomain/zero/1.0/), equivalent of public domain.
```

### How to extend sbt new

The rest of this page explains how to extend `sbt new` command
to provide support for something other than Giter8 templates.
You can skip this section if your not interested in extending `new`.

#### Template Resolver

A template resolver is a partial function that looks at the arguments
after `sbt new` and determines whether it can resolve to a particular template. This is analogous to `resolvers` resolving `ModuleID` from the Internet.

The `Giter8TemplateResolver` checks whether the first non-option argument looks like
a Github repo or a git repo that ends in ".g8".
If it matches the one of the patterns, it will pass the arguments to Giter8 to process.

To create your own template resolver, add create a library that has `template-resolver` as a dependency:

```scala
val templateResolverApi = "org.scala-sbt" % "template-resolver" % "0.1"
```

and extend the `TemplateResolver`:

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

Next, create an sbt plugin that adds `TemplateResolverInfo` to `templateResolverInfos`.

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

This indirecton allows template resolvers to have an isolated classpath independent from the rest of the build.

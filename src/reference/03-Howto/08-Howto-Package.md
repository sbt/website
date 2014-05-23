---
out: Howto-Package.html
---

  [Artifacts]: Artifacts.html
  [Mapping-Files]: Mapping-Files.html

Configure packaging
-------------------

By default, a project exports a directory containing its resources and
compiled class files. Set `exportJars` to true to export the packaged
jar instead. For example,

```scala
exportJars := true
```

The jar will be used by `run`, `test`, `console`, and other tasks that
use the full classpath.

By default, sbt constructs a manifest for the binary package from
settings such as `organization` and `mainClass`. Additional attributes
may be added to the `packageOptions` setting scoped by the configuration
and package task.

Main attributes may be added with `Package.ManifestAttributes`. There
are two variants of this method, once that accepts repeated arguments
that map an attribute of type `java.util.jar.Attributes.Name` to a
String value and other that maps attribute names (type String) to the
String value.

For example,

```scala
packageOptions in (Compile, packageBin) += 
  Package.ManifestAttributes( java.util.jar.Attributes.Name.SEALED -> "true" )
```

Other attributes may be added with `Package.JarManifest`.

```scala
packageOptions in (Compile, packageBin) +=  {
  import java.util.jar.{Attributes, Manifest}
  val manifest = new Manifest
  manifest.getAttributes("foo/bar/").put(Attributes.Name.SEALED, "false")
  Package.JarManifest( manifest )
}
```

Or, to read the manifest from a file:

```scala
packageOptions in (Compile, packageBin) +=  {
  val manifest = Using.fileInputStream( in => new java.util.jar.Manifest(in) )
  Package.JarManifest( manifest )
}
```

The `artifactName` setting controls the name of generated packages. See
the [Artifacts][Artifacts] page for details.

The contents of a package are defined by the `mappings` task, of type
`Seq[(File,String)]`. The `mappings` task is a sequence of mappings from
a file to include in the package to the path in the package. See
[Mapping Files][Mapping-Files] for convenience functions for
generating these mappings. For example, to add the file `in/example.txt`
to the main binary jar with the path "out/example.txt",

```scala
mappings in (Compile, packageBin) += {
  (baseDirectory.value / "in" / "example.txt") -> "out/example.txt"
}
```

Note that `mappings` is scoped by the configuration and the specific
package task. For example, the mappings for the test source package are
defined by the `mappings in (Test, packageSrc)` task.

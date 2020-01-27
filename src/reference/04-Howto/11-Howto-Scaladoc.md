---
out: Howto-Scaladoc.html
---

Generate API documentation
--------------------------

<a name="generate-javadoc-or-scaladoc"></a>

### Select javadoc or scaladoc

sbt will run `javadoc` if there are only Java sources in the project. If
there are any Scala sources, sbt will run `scaladoc`. (This situation
results from `scaladoc` not processing Javadoc comments in Java sources
nor linking to Javadoc.)

<a name="definitive-doc-options"></a>

### Set the options used for generating scaladoc independently of compilation

Scope `scalacOptions` to the `doc` task to configure `scaladoc`. Use
`:=` to definitively set the options without appending to the options
for `compile`. Scope to `Compile` for main sources or to `Test` for test
sources. For example,

```scala
Compile / doc / scalacOptions := Seq("-groups", "-implicits")
```

<a name="additional-doc-options"></a>

### Add options for scaladoc to the compilation options

Scope `scalacOptions` to the `doc` task to configure `scaladoc`. Use
`+=` or `++=` to append options to the base options. To append a single
option, use `+=`. To append a `Seq[String]`, use `++=`. Scope to
`Compile` for main sources or to `Test` for test sources. For example,

```scala
Compile / doc / scalacOptions ++= Seq("-groups", "-implicits")
```

<a name="definitive-javadoc-options"></a>

### Set the options used for generating javadoc independently of compilation

Scope `javacOptions` to the `doc` task to configure `javadoc`. Use `:=`
to definitively set the options without appending to the options for
`compile`. Scope to `Compile` for main sources or to `Test` for test
sources.


<a name="additional-doc-options"></a>

### Add options for javadoc to the compilation options

Scope `javacOptions` to the `doc` task to configure `javadoc`. Use `+=`
or `++=` to append options to the base options. To append a single
option, use `+=`. To append a `Seq[String]`, use `++=`. Scope to
`Compile` for main sources or to `Test` for test sources. For example,

```scala
Compile / doc / javacOptions ++= Seq("-notimestamp", "-linksource")
```

<a name="auto-link"></a>

### Enable automatic linking to the external Scaladoc of managed dependencies

Set `autoAPIMappings := true` for sbt to tell `scaladoc` where it can
find the API documentation for managed dependencies. This requires that
dependencies have this information in its metadata and you are using
`scaladoc` for Scala 2.10.2 or later.

<a name="manual-api-links"></a>

### Enable manual linking to the external Scaladoc of managed dependencies

Add mappings of type `(File, URL)` to `apiMappings` to manually tell
`scaladoc` where it can find the API documentation for dependencies.
(This requires `scaladoc` for Scala 2.10.2 or later.) These mappings are
used in addition to `autoAPIMappings`, so this manual configuration is
typically done for unmanaged dependencies. The `File` key is the
location of the dependency as passed to the classpath. The `URL` value
is the base URL of the API documentation for the dependency. For
example,

```scala
apiMappings += (
  (unmanagedBase.value / "a-library.jar") -> 
    url("https://example.org/api/")
)
```

<a name="define-api-url"></a>

### Define the location of API documentation for a library

Set `apiURL` to define the base `URL` for the Scaladocs for your
library. This will enable clients of your library to automatically link
against the API documentation using `autoAPIMappings`. (This only works
for Scala 2.10.2 and later.) For example,

```scala
apiURL := Some(url("https://example.org/api/"))
```

This information will get included in a property of the published
`pom.xml`, where it can be automatically consumed by sbt.

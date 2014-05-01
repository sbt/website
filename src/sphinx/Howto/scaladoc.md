Generate Documentation
======================

sbt will run javadoc if there are only Java sources in the project. If
there are any Scala sources, sbt will run scaladoc. (This situation
results from scaladoc not processing Javadoc comments in Java sources
nor linking to Javadoc.)

Scope scalacOptions to the doc task to configure scaladoc. Use := to
definitively set the options without appending to the options for
compile. Scope to Compile for main sources or to Test for test sources.
For example, :

    scalacOptions in (Compile,doc) := Seq("-groups", "-implicits")

Scope scalacOptions to the doc task to configure scaladoc. Use += or ++=
to append options to the base options. To append a single option, use
+=. To append a Seq[String], use ++=. Scope to Compile for main sources
or to Test for test sources. For example, :

    scalacOptions in (Compile,doc) ++= Seq("-groups", "-implicits")

Scope javacOptions to the doc task to configure javadoc. Use := to
definitively set the options without appending to the options for
compile. Scope to Compile for main sources or to Test for test sources.

Scope javacOptions to the doc task to configure javadoc. Use += or ++=
to append options to the base options. To append a single option, use
+=. To append a Seq[String], use ++=. Scope to Compile for main sources
or to Test for test sources. For example, :

    javacOptions in (Compile,doc) ++= Seq("-notimestamp", "-linksource")

Set autoAPIMappings := true for sbt to tell scaladoc where it can find
the API documentation for managed dependencies. This requires that
dependencies have this information in its metadata and you are using
scaladoc for Scala 2.10.2 or later.

Add mappings of type (File, URL) to apiMappings to manually tell
scaladoc where it can find the API documentation for dependencies. (This
requires scaladoc for Scala 2.10.2 or later.) These mappings are used in
addition to autoAPIMappings, so this manual configuration is typically
done for unmanaged dependencies. The File key is the location of the
dependency as passed to the classpath. The URL value is the base URL of
the API documentation for the dependency. For example, :

    apiMappings += (
      (unmanagedBase.value / "a-library.jar") -> 
        url("http://example.org/api/")
    )

Set apiURL to define the base URL for the Scaladocs for your library.
This will enable clients of your library to automatically link against
the API documentation using autoAPIMappings. (This only works for Scala
2.10.2 and later.) For example, :

    apiURL := Some(url("http://example.org/api/"))

This information will get included in a property of the published
pom.xml, where it can be automatically consumed by sbt.

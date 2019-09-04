---
out: In-Process-Classloaders.html
---

  [Running Project Code]: Running-Project-Code.html
  [Forking]: Forking.html


### In process class loading

By default, sbt executes the `run` and `test` tasks within its own JVM instance.
It emulates running an external java command by invoking the task in an isolated
`ClassLoader`. Compared to [forking][Forking], this approach reduces the start
start up latency and total runtime. The performance benefit from simply reusing
the JVM is modest. Class loading and linking of the application dependencies
dominate the start up time of many applications. sbt reduces this start up
latency by re-using some of the loaded classes between runs. It does this by
creating a layered `ClassLoader` following the standard delegation model of a java
[ClassLoader](https://docs.oracle.com/javase/8/docs/api/java/lang/ClassLoader.html).
The outermost layer, which always contains the class files and jars specific to
the project, is discarded between runs. The inner layers, however, can be
reused.

Starting with sbt 1.3.0, it is possible to configure the particular approach
that sbt takes to generate layered `ClassLoader` instances. It is specified via
the `classLoaderLayeringStrategy`. There are three possible values:

1. `ScalaLibrary` - The parent of the outermost layer is able to load the
scala standard library as well as the scala reflect library provided it is on
the application classpath. This is the default strategy. It is most similar to
the layered `ClassLoaders` provided by sbt versions < 1.3.0.

2. `AllLibraryJars` - Adds an additional layer for all of the dependency jars
between the scala library layer and the outermost layer. It is the default
strategy when turbo mode is enabled. This strategy can significantly improve the
startup and total runtime performance compared to `ScalaLibrary`. Results may be
inconsistent if any of the libraries have mutable global state because, unlike
`ScalaLibrary`, the global state persists between runs. When any libraries use
java serialization, `AllLibraryJars` should be avoided.

3. Flat - No layering is used. The full classpath, as specified by the
`fullClasspath` key of the task is loaded in the outermost layer. Consider using
as an alternative to fork if any issues are experienced with `ScalaLibrary` or
if the application requires all classes to be loaded in the same `ClassLoader`,
which may be the case for some uses of java serialization.

The `classLoaderLayeringStrategy` can be set in different configurations. For
example, to use the `AllLibraryJars` strategy in the `Test` configuration, add

```
Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.AllLibraryJars
```

to the `build.sbt` file. Assuming no other changes to the `build.sbt` file, The
`run` task will still use `ScalaLibrary` strategy.

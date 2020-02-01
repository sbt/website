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


### Troubleshooting

Java reflection may cause issues when used with layered classloaders because it
is possible that the class method that loads another class via reflection may
not have access to that class to be loaded. This is particularly likely if the
class is loaded using `Class.forName` or
`Thread.currentThread.getContextClassLoader.loadClass`. Consider the following
example:

```scala
package example

import scala.concurrent.{ Await, Future }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object ReflectionExample {
  def main(args: Array[String]): Unit = Await.result(Future {
      val cl = Thread.currentThread.getContextClassLoader
      println(cl.loadClass("example.Foo"))
  }, Duration.Inf)
}
class Foo
```

If one runs `ReflectionExample` with `sbt run` using the sbt default `ScalaLibrary`
strategy, it will fail with a `ClassNotFoundException` because the context
classloader of the thread that backs the future is the scala library classloader
which is not able to load project classes. To work around this limitation
without changing the layering strategy to `Flat`, one can do the following:

1. Use `Class.forName` instead of `ClassLoader.loadClass`. The jvm implicitly
uses the loader of the calling class for loading classes using `Class.forName`.
In this case, `ReflectionExample` is the calling class and it will be in the
same classloader as `Foo` since they are both part of the project classpath.

2. Provide a classloader for loading. In the example above, this can be done by
replacing `val cl = Thread.currentThread.getContextClassLoader` with `val cl =
getClass.getClassLoader`.

For case (2), if the name lookup is performed by a library, then a
`ClassLoader` parameter could be added to the library method that does the
lookup. For example,

```scala
object Library {
  def lookup(name: String): Class[_] =
    Thread.currentThread.getContextClassLoader.loadClass(name)
}
```
could be rewritten to

```scala
object Library {
  def lookup(name: String): Class[_] =
    lookup(name, Thread.currentThread.getContextClassLoader)
  def lookup(name: String, loader: ClassLoader): Class[_] =
    loader.loadClass(name)
}
```

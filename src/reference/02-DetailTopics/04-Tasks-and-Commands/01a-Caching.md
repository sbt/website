---
out: Caching.html
---

  [Basic-Def]: Basic-Def.html
  [Tasks]: Tasks.html
  [apidoc-FileFunction-cached]: $apidoc_base$/api/sbt/util/FileFunction\$.html#cached(cacheBaseDirectory:java.io.File)(action:Set[java.io.File]=>Set[java.io.File]):Set[java.io.File]=>Set[java.io.File]

Caching
-------

Tasks and settings are introduced in the
[getting started guide][Basic-Def], and explained in more detail in [Tasks][Tasks].
You may wish to read them first.

When you define a custom task, you might want to cache the value to avoid unnecessary work.

### Cache.cached

`sbt.util.Cache` provides a basic caching facility:

```scala
package sbt.util

/**
 * A simple cache with keys of type `I` and values of type `O`
 */
trait Cache[I, O] {

  /**
   * Queries the cache backed with store `store` for key `key`.
   */
  def apply(store: CacheStore)(key: I): CacheResult[O]
}
```

We can derive the instances of `Cache[I, O]` from `sjsonnew.JsonFormat` instances for both `I` and `O` by importing `sbt.util.CacheImplicits._` (This also brings in `BasicJsonProtocol`).

To use the cache, we can create a _cached_ function by calling `Cache.cached` with a `CacheStore` (or a file) and a function that does the actual work. Normally, the cache store would be created as `streams.value.cacheStoreFactory / "something"`. In the following REPL example, I will create a cache store from a temp file.

```scala
scala> import sbt._, sbt.util.CacheImplicits._
import sbt._
import sbt.util.CacheImplicits._

scala> def doWork(i: Int): List[String] = {
         println("working...")
         Thread.sleep(1000)
         List.fill(i)("foo")
       }
doWork: (i: Int)List[String]

// use streams.value.cacheStoreFactory.make("something") for real tasks
scala> val store = sbt.util.CacheStore(file("/tmp/something"))
store: sbt.util.CacheStore = sbt.util.FileBasedStore@5a4a6716

scala> val cachedWork: Int => List[String] = Cache.cached(store)(doWork)
cachedWork: Int => List[String] = sbt.util.Cache\$\$\$Lambda\$5577/1548870528@3bb59fba

scala> cachedWork(1)
working...
res0: List[String] = List(foo)

scala> cachedWork(1)
res1: List[String] = List(foo)

scala> cachedWork(3)
working...
res2: List[String] = List(foo, foo, foo)

scala> cachedWork(1)
working...
res3: List[String] = List(foo)
```

As you can see, `cachedWork(1)` is cached when it is called consecutively.

### Previous value

`TaskKey` has a method called `previous` that returns `Option[A]`, which can be used a lightweight tracker.
Suppose we would want to create a task where it initially returns `"hi"`, and append `"!"` for subsequent calls, you can define a `TaskKey[String]` called `hi`, and retrieve its previous value, which would be typed `Option[String]`. The previous value would be `None` the first time, and `Some(x)` for the subsequent calls.

```scala
lazy val hi = taskKey[String]("say hi again")
hi := {
  import sbt.util.CacheImplicits._
  val prev = hi.previous
  prev match {
    case None    => "hi"
    case Some(x) => x + "!"
  }
}
```

We can test this by running `show hi` from the sbt shell:

```
sbt:hello> show hi
[info] hi
[success] Total time: 0 s, completed Aug 16, 2019 12:24:32 AM
sbt:hello> show hi
[info] hi!
[success] Total time: 0 s, completed Aug 16, 2019 12:24:33 AM
sbt:hello> show hi
[info] hi!!
[success] Total time: 0 s, completed Aug 16, 2019 12:24:34 AM
sbt:hello> show hi
[info] hi!!!
[success] Total time: 0 s, completed Aug 16, 2019 12:24:35 AM
```

For each call `hi.previous` contains the previous result from evaluating `hi`.

### Tracked.lastOutput

`sbt.util.Tracked` provides a facility for partial caching that can be mixed and matched with other trackers.

Similar to the previous value associated with task keys, `sbt.util.Tracked.lastOutput` creates a tracker for the last calculated value. `Tracked.lastOutput` offers more flexibility in terms of where to store the value. (This allows the value to be shared across multiple tasks).

Suppose we would initially take an `Int` as the input, and turn it into a `String`, but for subsequent invocation we'd append `"!"`:

```scala
scala> import sbt._, sbt.util.CacheImplicits._
import sbt._
import sbt.util.CacheImplicits._

// use streams.value.cacheStoreFactory.make("last") for real tasks
scala> val store = sbt.util.CacheStore(file("/tmp/last"))
store: sbt.util.CacheStore = sbt.util.FileBasedStore@5a4a6716

scala> val badCachedWork = Tracked.lastOutput[Int, String](store) {
         case (in, None)       => in.toString
         case (in, Some(read)) => read + "!"
       }
badCachedWork: Int => String = sbt.util.Tracked\$\$\$Lambda\$6326/638923124@68c6ff60

scala> badCachedWork(1)
res1: String = 1

scala> badCachedWork(1)
res2: String = 1!

scala> badCachedWork(2)
res3: String = 1!!

scala> badCachedWork(2)
res4: String = 1!!!
```

**Note**: `Tracked.lastOutput` does not invalidate the cache when the input changes.

See the `Tracked.inputChanged` section below to make this work.

### Tracked.inputChanged

To track the changes of input parameters, use `Tracked.inputChanged`.

```scala
scala> import sbt._, sbt.util.CacheImplicits._
import sbt._
import sbt.util.CacheImplicits._

// use streams.value.cacheStoreFactory.make("input") for real tasks
scala> val store = sbt.util.CacheStore(file("/tmp/input"))
store: sbt.util.CacheStore = sbt.util.FileBasedStore@5a4a6716

scala> val tracker = Tracked.inputChanged[Int, String](store) { case (changed, in) =>
         if (changed) {
           println("input changed")
         }
         in.toString
       }
tracker: Int => String = sbt.util.Tracked\$\$\$Lambda\$6357/1296627950@6e6837e4

scala> tracker(1)
input changed
res6: String = 1

scala> tracker(1)
res7: String = 1

scala> tracker(2)
input changed
res8: String = 2

scala> tracker(2)
res9: String = 2

scala> tracker(1)
input changed
res10: String = 1
```

Now, we can nest `Tracked.inputChanged` and `Tracked.lastOutput` to regain the cache invalidation.

```scala
// use streams.value.cacheStoreFactory
scala> val cacheFactory = sbt.util.CacheStoreFactory(file("/tmp/cache"))
cacheFactory: sbt.util.CacheStoreFactory = sbt.util.DirectoryStoreFactory@3a3d3778

scala> def doWork(i: Int): String = {
         println("working...")
         Thread.sleep(1000)
         i.toString
       }
doWork: (i: Int)String

scala> val cachedWork2 = Tracked.inputChanged[Int, String](cacheFactory.make("input")) { case (changed: Boolean, in: Int) =>
         val tracker = Tracked.lastOutput[Int, String](cacheFactory.make("last")) {
           case (in, None)       => doWork(in)
           case (in, Some(read)) =>
             if (changed) doWork(in)
             else read
         }
         tracker(in)
       }
cachedWork2: Int => String = sbt.util.Tracked\$\$\$Lambda\$6548/972308467@1c9788cc

scala> cachedWork2(1)
working...
res0: String = 1

scala> cachedWork2(1)
res1: String = 1
```

One benefit of combining trackers and/or previous value is that we can control the invalidation timing. For example, we can create a cache that works only twice.

```scala
lazy val hi = taskKey[String]("say hi")
lazy val hiCount = taskKey[(String, Int)]("track number of the times hi was called")

hi := hiCount.value._1
hiCount := {
  import sbt.util.CacheImplicits._
  val prev = hiCount.previous
  val s = streams.value
  def doWork(x: String): String = {
    s.log.info("working...")
    Thread.sleep(1000)
    x + "!"
  }
  val cachedWork = Tracked.inputChanged[String, (String, Int)](s.cacheStoreFactory.make("input")) { case (changed: Boolean, in: String) =>
    prev match {
      case None            => (doWork(in), 0)
      case Some((last, n)) =>
        if (changed || n > 1) (doWork(in), 0)
        else (last, n + 1)
    }
  }
  cachedWork("hi")
}
```

This uses `hiCount` task's previous value to track the number of times it got called, and invalidates the cache when `n > 1`.

```scala
sbt:hello> hi
[info] working...
[success] Total time: 1 s, completed Aug 17, 2019 10:36:34 AM
sbt:hello> hi
[success] Total time: 0 s, completed Aug 17, 2019 10:36:35 AM
sbt:hello> hi
[success] Total time: 0 s, completed Aug 17, 2019 10:36:38 AM
sbt:hello> hi
[info] working...
[success] Total time: 1 s, completed Aug 17, 2019 10:36:40 AM
```

<a name="filefunction"></a>
### Tracking file attributes

Files often come up as caching targets, but `java.io.File` just carries the file name, so it's not very useful on its own for the purpose of caching.

For file caching, sbt provides a facility called [sbt.util.FileFunction.cached(...)][apidoc-FileFunction-cached]
to cache file inputs and outputs. The following example implements a cached task
that counts the number of lines in `*.md` and outputs `*.md` under cross target
directory with the number of lines as their contents.

@@snip [build.sbt]($root$/src/sbt-test/ref/caching-file-function/build.sbt) {}

There are two additional arguments for the first parameter list that
allow the file tracking style to be explicitly specified. By default,
the input tracking style is `FilesInfo.lastModified`, based on a file's
last modified time, and the output tracking style is `FilesInfo.exists`,
based only on whether the file exists.

### FileInfo

- `FileInfo.exists` tracks if the file exists
- `FileInfo.lastModified` track the last modified timestamp
- `FileInfo.hash` tracks the SHA-1 content hash
- `FileInfo.full` tracks both the last modified and the content hash

```scala
scala> FileInfo.exists(file("/tmp/cache/last"))
res23: sbt.util.PlainFileInfo = PlainFile(/tmp/cache/last,true)

scala> FileInfo.lastModified(file("/tmp/cache/last"))
res24: sbt.util.ModifiedFileInfo = FileModified(/tmp/cache/last,1565855326328)

scala> FileInfo.hash(file("/tmp/cache/last"))
res25: sbt.util.HashFileInfo = FileHash(/tmp/cache/last,List(-89, -11, 75, 97, 65, -109, -74, -126, -124, 43, 37, -16, 9, -92, -70, -100, -82, 95, 93, -112))

scala> FileInfo.full(file("/tmp/cache/last"))
res26: sbt.util.HashModifiedFileInfo = FileHashModified(/tmp/cache/last,List(-89, -11, 75, 97, 65, -109, -74, -126, -124, 43, 37, -16, 9, -92, -70, -100, -82, 95, 93, -112),1565855326328)
```

There is also `sbt.util.FilesInfo` that accepts a `Set` of `File`s (though this doesn't always work due to complicated abstract type that it uses).

```scala
scala> FilesInfo.exists(Set(file("/tmp/cache/last"), file("/tmp/cache/nonexistent")))
res31: sbt.util.FilesInfo[_1.F] forSome { val _1: sbt.util.FileInfo.Style } = FilesInfo(Set(PlainFile(/tmp/cache/last,true), PlainFile(/tmp/cache/nonexistent,false)))
```

### Tracked.inputChanged

The following example implements a cached task that counts the number of lines in `README.md`.

```scala
lazy val count = taskKey[Int]("")

count := {
  import sbt.util.CacheImplicits._
  val prev = count.previous
  val s = streams.value
  val toCount = baseDirectory.value / "README.md"
  def doCount(source: File): Int = {
    s.log.info("working...")
    IO.readLines(source).size
  }
  val cachedCount = Tracked.inputChanged[ModifiedFileInfo, Int](s.cacheStoreFactory.make("input")) {
    (changed: Boolean, in: ModifiedFileInfo) =>
      prev match {
        case None       => doCount(in.file)
        case Some(last) =>
          if (changed) doCount(in.file)
          else last
      }
  }
  cachedCount(FileInfo.lastModified(toCount))
}
```

We can try this by running `show count` from the sbt shell:

```scala
sbt:hello> show count
[info] working...
[info] 2
[success] Total time: 0 s, completed Aug 16, 2019 9:58:38 PM
sbt:hello> show count
[info] 2
[success] Total time: 0 s, completed Aug 16, 2019 9:58:39 PM

// change something in README.md
sbt:hello> show count
[info] working...
[info] 3
[success] Total time: 0 s, completed Aug 16, 2019 9:58:44 PM
```

This works out-of-box thanks to `sbt.util.FileInfo` implementing `JsonFormat` to persist itself.

### Tracked.outputChanged

The tracking works by stamping the files (collecting file attributes), storing the stamps in a cache, and comparing them later. Sometimes, it's important to pay attention to the timing of when stamping happens. Suppose that we want to format TypeScript files, and use SHA-1 hash to detect changes. Stamping the files _before_ running the formatter would cause the cache to be invalidated in subsequent calls to the task. This is because the formatter itself may modify the TypeScript files.

Use `Tracked.outputChanged` stamps _after_ your work is done to prevent this.

```scala
lazy val compileTypeScript = taskKey[Unit]("compiles *.ts files")
lazy val formatTypeScript = taskKey[Seq[File]]("format *.ts files")

compileTypeScript / sources := (baseDirectory.value / "src").globRecursive("*.ts").get
formatTypeScript := {
  import sbt.util.CacheImplicits._
  val s = streams.value
  val files = (compileTypeScript / sources).value

  def doFormat(source: File): File = {
    s.log.info(s"formatting \$source")
    val lines = IO.readLines(source)
    IO.writeLines(source, lines ++ List("// something"))
    source
  }
  val tracker = Tracked.outputChanged(s.cacheStoreFactory.make("output")) {
     (outChanged: Boolean, outputs: Seq[HashFileInfo]) =>
       if (outChanged) outputs map { info => doFormat(info.file) }
       else outputs map { _.file }
  }
  tracker(() => files.map(FileInfo.hash(_)))
}
```

Type `formatTypeScript` from the sbt shell to see how it works:

```scala
sbt:hello> formatTypeScript
[info] formatting /Users/eed3si9n/work/hellotest/src/util.ts
[info] formatting /Users/eed3si9n/work/hellotest/src/hello.ts
[success] Total time: 0 s, completed Aug 17, 2019 10:07:30 AM
sbt:hello> formatTypeScript
[success] Total time: 0 s, completed Aug 17, 2019 10:07:32 AM
```

One potential drawback of this implementation is that we only have `true/false` information about the fact that any of the files have changed.
This could result in a reformatting of _all_ of the files anytime one file gets changed.

```scala
// make change to one file
sbt:hello> formatTypeScript
[info] formatting /Users/eed3si9n/work/hellotest/src/util.ts
[info] formatting /Users/eed3si9n/work/hellotest/src/hello.ts
[success] Total time: 0 s, completed Aug 17, 2019 10:13:47 AM
```

See the `Tracked.diffOuputs` in the below to prevent this all-or-nothing behavior.

Another potential use for `Tracked.outputChanged` is using it with `FileInfo.exists(_)` to track if the output file still exists.
This is usually not necessary if you output something under `target` directory where caches are also stored.

### Tracked.diffInputs

The `Tracked.inputChanged` tracker only gives `Boolean` value, so when the cache is invalidated we need to redo all the work. Use `Tracked.diffInputs` to track the differences.

`Tracked.diffInputs` reports a datatype called `sbt.util.ChangeReport`:

```scala
/** The result of comparing some current set of objects against a previous set of objects.*/
trait ChangeReport[T] {

  /** The set of all of the objects in the current set.*/
  def checked: Set[T]

  /** All of the objects that are in the same state in the current and reference sets.*/
  def unmodified: Set[T]

  /**
   * All checked objects that are not in the same state as the reference.  This includes objects that are in both
   * sets but have changed and files that are only in one set.
   */
  def modified: Set[T] // all changes, including added

  /** All objects that are only in the current set.*/
  def added: Set[T]

  /** All objects only in the previous set*/
  def removed: Set[T]
  def +++(other: ChangeReport[T]): ChangeReport[T] = new CompoundChangeReport(this, other)

  ....
}
```

Let's see how the report works by printing it out.

```scala
lazy val compileTypeScript = taskKey[Unit]("compiles *.ts files")

compileTypeScript / sources := (baseDirectory.value / "src").globRecursive("*.ts").get
compileTypeScript := {
  val s = streams.value
  val files = (compileTypeScript / sources).value
  Tracked.diffInputs(s.cacheStoreFactory.make("input_diff"), FileInfo.lastModified)(files.toSet) {
    (inDiff: ChangeReport[File]) =>
    s.log.info(inDiff.toString)
  }
}
```

Here's how it looks when you rename a file for example:

```scala
sbt:hello> compileTypeScript
[info] Change report:
[info]  Checked: /Users/eed3si9n/work/hellotest/src/util.ts, /Users/eed3si9n/work/hellotest/src/hello.ts
[info]  Modified: /Users/eed3si9n/work/hellotest/src/util.ts, /Users/eed3si9n/work/hellotest/src/hello.ts
[info]  Unmodified:
[info]  Added: /Users/eed3si9n/work/hellotest/src/util.ts, /Users/eed3si9n/work/hellotest/src/hello.ts
[info]  Removed:
[success] Total time: 0 s, completed Aug 17, 2019 10:42:50 AM
sbt:hello> compileTypeScript
[info] Change report:
[info]  Checked: /Users/eed3si9n/work/hellotest/src/util.ts, /Users/eed3si9n/work/hellotest/src/bye.ts
[info]  Modified: /Users/eed3si9n/work/hellotest/src/hello.ts, /Users/eed3si9n/work/hellotest/src/bye.ts
[info]  Unmodified: /Users/eed3si9n/work/hellotest/src/util.ts
[info]  Added: /Users/eed3si9n/work/hellotest/src/bye.ts
[info]  Removed: /Users/eed3si9n/work/hellotest/src/hello.ts
[success] Total time: 0 s, completed Aug 17, 2019 10:43:37 AM
```

If we had a mapping between `*.ts` files and `*.js` files, then we should be able to make the compilation more incremental. For incremental compilation of Scala, Zinc tracks both the relationship between the `*.scala` and `*.class` files as well as the relationship among `*.scala`. We could make something like that for TypeScript. Save the following as `project/TypeScript.scala`:

```scala
import sbt._
import sjsonnew.{ :*:, LList, LNil}
import sbt.util.CacheImplicits._

/**
 * products - products keep the mapping between source *.ts files and *.js files that are generated.
 * references - references keep the mapping between *.ts files referencing other *.ts files.
 */
case class TypeScriptAnalysis(products: List[(File, File)], references: List[(File, File)]) {
  def ++(that: TypeScriptAnalysis): TypeScriptAnalysis =
    TypeScriptAnalysis(products ++ that.products, references ++ that.references)
}
object TypeScriptAnalysis {
  implicit val analysisIso = LList.iso(
    { a: TypeScriptAnalysis => ("products", a.products) :*: ("references", a.references) :*: LNil },
    { in: List[(File, File)] :*: List[(File, File)] :*: LNil => TypeScriptAnalysis(in._1, in._2) })
}
```

In the `build.sbt`:

```scala
lazy val compileTypeScript = taskKey[TypeScriptAnalysis]("compiles *.ts files")

compileTypeScript / sources := (baseDirectory.value / "src").globRecursive("*.ts").get
compileTypeScript / target := target.value / "js"
compileTypeScript := {
  import sbt.util.CacheImplicits._
  val prev0 = compileTypeScript.previous
  val prev = prev0.getOrElse(TypeScriptAnalysis(Nil, Nil))
  val s = streams.value
  val files = (compileTypeScript / sources).value

  def doCompile(source: File): TypeScriptAnalysis = {
    println("working...")
    val out = (compileTypeScript / target).value / source.getName.replaceAll("""\.ts\$""", ".js")
    IO.touch(out)
    // add a fake reference from any file to util.ts
    val references: List[(File, File)] =
      if (source.getName != "util.ts") List(source -> (baseDirectory.value / "src" / "util.ts"))
      else Nil
    TypeScriptAnalysis(List(source -> out), references)
  }
  Tracked.diffInputs(s.cacheStoreFactory.make("input_diff"), FileInfo.lastModified)(files.toSet) {
    (inDiff: ChangeReport[File]) =>
    val products = scala.collection.mutable.ListBuffer(prev.products: _*)
    val references = scala.collection.mutable.ListBuffer(prev.references: _*)
    val initial = inDiff.modified & inDiff.checked
    val reverseRefs = initial.flatMap(x => Set(x) ++ references.collect({ case (k, `x`) => k }).toSet )
    products --= products.filter({ case (k, v) => reverseRefs(k) || inDiff.removed(k) })
    references --= references.filter({ case (k, v) => reverseRefs(k) || inDiff.removed(k) })
    reverseRefs foreach { x =>
      val temp = doCompile(x)
      products ++= temp.products
      references ++= temp.references
    }
    TypeScriptAnalysis(products.toList, references.toList)
  }
}
```

The above is a fake compilation that just creates `.js` files under `target/js`.

```scala
sbt:hello> compileTypeScript
working...
working...
[success] Total time: 0 s, completed Aug 16, 2019 10:22:58 PM
sbt:hello> compileTypeScript
[success] Total time: 0 s, completed Aug 16, 2019 10:23:03 PM
```

Since we added a reference from `hello.ts` to `util.ts`, if we modified `src/util.ts`, it should trigger the compilation of `src/util.ts` as well as `src/hello.ts`.

```scala
sbt:hello> show compileTypeScript
working...
working...
[info] TypeScriptAnalysis(List((/Users/eed3si9n/work/hellotest/src/util.ts,/Users/eed3si9n/work/hellotest/target/js/util.ts), (/Users/eed3si9n/work/hellotest/src/hello.ts,/Users/eed3si9n/work/hellotest/target/js/hello.ts)),List((/Users/eed3si9n/work/hellotest/src/hello.ts,/Users/eed3si9n/work/hellotest/src/util.ts)))
```

It works.

### Tracked.diffOutputs

`Tracked.diffOutputs` is a finer version of `Tracked.outputChanged` that stamps after the work is done, and also able to report the set of modified files.

This can be used to format only the changed TypeScript files.

```scala
lazy val formatTypeScript = taskKey[Seq[File]]("format *.ts files")

compileTypeScript / sources := (baseDirectory.value / "src").globRecursive("*.ts").get
formatTypeScript := {
  val s = streams.value
  val files = (compileTypeScript / sources).value
  def doFormat(source: File): File = {
    s.log.info(s"formatting \$source")
    val lines = IO.readLines(source)
    IO.writeLines(source, lines ++ List("// something"))
    source
  }
  Tracked.diffOutputs(s.cacheStoreFactory.make("output_diff"), FileInfo.hash)(files.toSet) {
    (outDiff: ChangeReport[File]) =>
    val initial = outDiff.modified & outDiff.checked
    initial.toList map doFormat
  }
}
```

Here's how `formatTypeScript` looks like in the shell:

```scala
sbt:hello> formatTypeScript
[info] formatting /Users/eed3si9n/work/hellotest/src/util.ts
[info] formatting /Users/eed3si9n/work/hellotest/src/hello.ts
[success] Total time: 0 s, completed Aug 17, 2019 9:28:56 AM
sbt:hello> formatTypeScript
[success] Total time: 0 s, completed Aug 17, 2019 9:28:58 AM
```

### Case study: sbt-scalafmt

sbt-scalafmt implements `scalafmt` and `scalafmtCheck` tasks that cooperate with each other.
For example, if `scalafmt` ran successfully, and no changes have been made to the sources, it will skip `scalafmtCheck`'s checking.

Here's a snippet of how that may be implemented:

```scala
private def cachedCheckSources(
  cacheStoreFactory: CacheStoreFactory,
  sources: Seq[File],
  config: Path,
  log: Logger,
  writer: PrintWriter
): ScalafmtAnalysis = {
  trackSourcesAndConfig(cacheStoreFactory, sources, config) {
    (outDiff, configChanged, prev) =>
      log.debug(outDiff.toString)
      val updatedOrAdded = outDiff.modified & outDiff.checked
      val filesToCheck =
        if (configChanged) sources
        else updatedOrAdded.toList
      val failed = prev.failed filter { _.exists }
      val files = (filesToCheck ++ failed.toSet).toSeq
      val result = checkSources(files, config, log, writer)
      // cachedCheckSources moved the outDiff cursor forward,
      // save filesToCheck so scalafmt can later run formatting
      prev.copy(
        failed = result.failed,
        pending = (prev.pending ++ filesToCheck).distinct
      )
  }
}

private def trackSourcesAndConfig(
  cacheStoreFactory: CacheStoreFactory,
  sources: Seq[File],
  config: Path
)(
    f: (ChangeReport[File], Boolean, ScalafmtAnalysis) => ScalafmtAnalysis
): ScalafmtAnalysis = {
  val prevTracker = Tracked.lastOutput[Unit, ScalafmtAnalysis](cacheStoreFactory.make("last")) {
    (_, prev0) =>
    val prev = prev0.getOrElse(ScalafmtAnalysis(Nil, Nil))
    val tracker = Tracked.inputChanged[HashFileInfo, ScalafmtAnalysis](cacheStoreFactory.make("config")) {
      case (configChanged, configHash) =>
        Tracked.diffOutputs(cacheStoreFactory.make("output-diff"), FileInfo.lastModified)(sources.toSet) {
          (outDiff: ChangeReport[File]) =>
          f(outDiff, configChanged, prev)
        }
    }
    tracker(FileInfo.hash(config.toFile))
  }
  prevTracker(())
}
```

In the above, `trackSourcesAndConfig` is a triple-nested tracker that tracks configuration file, source last modified stamps, and the previous value shared between two tasks. To share the previous value across two different tasks, we are using `Tracked.lastOutput` instead of the `.previous` method associated with the keys.

### Summary

Depending on the level of control you need, sbt offers a flexible set of utilities to cache and track values and files.

- `.previous`, `FileFunction.cached`, and `Cache.cached` are the basic cache to get started.
- To invalidate some result based on a change to its input parameters, use `Tracked.inputChanged`.
- File attributes can be tracked as values by using `FileInfo.exists`, `FileInfo.lastModified`, and `FileInfo.hash`.
- `Tracked` offers trackers that are often nested to track input invalidation, output invalidation, and diffing.

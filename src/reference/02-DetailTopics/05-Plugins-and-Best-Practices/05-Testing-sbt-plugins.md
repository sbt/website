---
out: Testing-sbt-plugins.html
---

  [sbtScriptedTests]: https://github.com/sbt/sbt/tree/develop/sbt-app/src/sbt-test
  [xsbt-web-plugin]: https://github.com/earldouglas/xsbt-web-plugin/tree/master/src/sbt-test
  [sbt-assembly]: https://github.com/sbt/sbt-assembly/tree/master/src/sbt-test/sbt-assembly
  [feabb2]: https://github.com/earldouglas/xsbt-web-plugin/commit/feabb2eb554940d9b28049bd0618b6a790d9e141

Testing sbt plugins
-------------------

Let's talk about testing. Once you write a plugin, it turns into a long-term thing. To keep adding new features (or to keep fixing bugs), writing tests makes sense.

### scripted test framework

sbt comes with scripted test framework, which lets you script a build scenario. It was written to test sbt itself on complex scenarios -- such as change detection and partial compilation:

> Now, consider what happens if you were to delete B.scala but do not update A.scala. When you recompile, you should get an error because B no longer exists for A to reference.
> [... (really complicated stuff)]
>
> The scripted test framework is used to verify that sbt handles cases such as that described above.

The framework is made available via scripted-plugin. The rest of this page explains how to include the scripted-plugin into your plugin.

### step 1: snapshot

Before you start, set your version to a **-SNAPSHOT** one because scripted-plugin will publish your plugin locally. If you don't use SNAPSHOT, you could get into a horrible inconsistent state of you and the rest of the world seeing different artifacts.

### step 2: SbtPlugin

Enable `SbtPlugin` in `build.sbt`:

```scala
lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-something"
  )
```

Then add the following settings to `build.sbt`:

```scala
lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-something",
    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false
  )
```

**Note**: You must use sbt 1.2.1 and above to use `SbtPlugin`.

### step 3: src/sbt-test

Make dir structure `src/sbt-test/<test-group>/<test-name>`. For starters, try something like `src/sbt-test/<your-plugin-name>/simple`.

Now ready? Create an initial build in `simple`. Like a real build using your plugin. I'm sure you already have several of them to test manually. Here's an example `build.sbt`:

```scala
lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.10.6",
    assembly / assemblyJarName := "foo.jar"
  )
```

In `project/plugins.sbt`:

```scala
sys.props.get("plugin.version") match {
  case Some(x) => addSbtPlugin("com.eed3si9n" % "sbt-assembly" % x)
  case _ => sys.error("""|The system property 'plugin.version' is not defined.
                         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
}
```

This a trick I picked up from [earldouglas/xsbt-web-plugin@feabb2][feabb2], which allows us to pass version number into the test.

I also have `src/main/scala/hello.scala`:

```scala
object Main {
  def main(args: Array[String]): Unit = {
    println("hello")
  }
}
```

### step 4: write a script

Now, write a script to describe your scenario in a file called `test` located at the root dir of your test project.

```
# check if the file gets created
> assembly
\$ exists target/scala-2.10/foo.jar
```

Here is the syntax for the script:

1. **`#`** starts a one-line comment
2. **`>`** `name` sends a task to sbt (and tests if it succeeds)
3. **`\$`** `name arg*` performs a file command (and tests if it succeeds)
4. **`->`** `name` sends a task to sbt, but expects it to fail
5. **`-\$`** `name arg*` performs a file command, but expects it to fail

File commands are:

- **`touch`** `path+` creates or updates the timestamp on the files
- **`delete`** `path+` deletes the files
- **`exists`** `path+` checks if the files exist
- **`mkdir`** `path+` creates dirs
- **`absent`** `path+` checks if the files don't exist
- **`newer`** `source target` checks if `source` is newer
- **`must-mirror`** `source target` checks if `source` is identical
- **`pause`** pauses until enter is pressed
- **`sleep`** `time` sleeps (in milliseconds)
- **`exec`** `command args*` runs the command in another process
- **`copy-file`** `fromPath toPath` copies the file
- **`copy`** `fromPath+ toDir` copies the paths to `toDir` preserving relative structure
- **`copy-flat`** `fromPath+ toDir` copies the paths to `toDir` flat

So my script will run `assembly` task, and checks if `foo.jar` gets created. We'll cover more complex tests later.

### step 5: run the script
To run the scripts, go back to your plugin project, and run:

```
> scripted
```

This will copy your test build into a temporary dir, and executes the `test` script. If everything works out, you'd see `publishLocal` running, then:

```
Running sbt-assembly / simple
[success] Total time: 18 s, completed Sep 17, 2011 3:00:58 AM
```

### step 6: custom assertion

The file commands are great, but not nearly enough because none of them test the actual contents. An easy way to test the contents is to implement a custom task in your test build.

For my hello project, I'd like to check if the resulting jar prints out "hello". I can take advantage of `scala.sys.process.Process` to run the jar. To express a failure, just throw an error. Here's `build.sbt`:

```scala
import scala.sys.process.Process

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.10.6",
    assembly / assemblyJarName := "foo.jar",
    TaskKey[Unit]("check") := {
      val process = Process("java", Seq("-jar", (crossTarget.value / "foo.jar").toString))
      val out = (process!!)
      if (out.trim != "bye") sys.error("unexpected output: " + out)
      ()
    }
  )
```

I am intentionally testing if it matches "bye", to see how the test fails.

Here's `test`:

```
# check if the file gets created
> assembly
\$ exists target/foo.jar

# check if it says hello
> check
```

Running `scripted` fails the test as expected:

```
[info] [error] {file:/private/var/folders/Ab/AbC1EFghIj4LMNOPqrStUV+++XX/-Tmp-/sbt_cdd1b3c4/simple/}default-0314bd/*:check: unexpected output: hello
[info] [error] Total time: 0 s, completed Sep 21, 2011 8:43:03 PM
[error] x sbt-assembly / simple
[error]    {line 6}  Command failed: check failed
[error] {file:/Users/foo/work/sbt-assembly/}default-373f46/*:scripted: sbt-assembly / simple failed
[error] Total time: 14 s, completed Sep 21, 2011 8:00:00 PM
```

### step 7: testing the test
Until you get the hang of it, it might take a while for the test itself to behave correctly. There are several techniques that may come in handy.

First place to start is turning off the log buffering.


```
> set scriptedBufferLog := false
```

This for example should print out the location of the temporary dir:

```
[info] [info] Set current project to default-c6500b (in build file:/private/var/folders/Ab/AbC1EFghIj4LMNOPqrStUV+++XX/-Tmp-/sbt_8d950687/simple/project/plugins/)
...
```

Add the following line to your `test` script to suspend the test until you hit the enter key:

```
\$ pause
```

If you're thinking about going down to the `sbt/sbt-test/sbt-foo/simple` and running `sbt`, don't do it. The right way, is to copy the dir somewhere else and run it.

### step 8: get inspired

There are literally [100+ scripted tests][sbtScriptedTests] under sbt project itself. Browse around to get inspirations.

For example, here's the one called by-name.

```
> compile

# change => Int to Function0
\$ copy-file changes/A.scala A.scala

# Both A.scala and B.scala need to be recompiled because the type has changed
-> compile
```

[xsbt-web-plugin][xsbt-web-plugin] and [sbt-assembly][sbt-assembly] have some scripted tests too.

That's it! Let me know about your experience in testing plugins!

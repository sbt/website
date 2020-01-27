---
out: Howto-Sequential-Task.html
---

### Defining a sequential task with Def.sequential

sbt 0.13.8 added `Def.sequential` function to run tasks under semi-sequential semantics.
To demonstrate the sequential task, let's create a custom task called `compilecheck` that runs `Compile / compile` and then `Compile / scalastyle` task added by [scalastyle-sbt-plugin](http://www.scalastyle.org/sbt.html).

Here's how to set it up

#### project/build.properties

```
sbt.version=$app_version$
```

#### project/style.sbt

```
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
```

#### build.sbt

```scala
lazy val compilecheck = taskKey[Unit]("compile and then scalastyle")

lazy val root = (project in file("."))
  .settings(
    Compile / compilecheck := Def.sequential(
      Compile / compile,
      (Compile / scalastyle).toTask("")
    ).value
  )
```

To call this task type in `compilecheck` from the shell. If the compilation fails, `compilecheck` would stop the execution.

```
root> compilecheck
[info] Compiling 1 Scala source to /Users/x/proj/target/scala-2.10/classes...
[error] /Users/x/proj/src/main/scala/Foo.scala:3: Unmatched closing brace '}' ignored here
[error] }
[error] ^
[error] one error found
[error] (compile:compileIncremental) Compilation failed
```

Looks like we were able to sequence these tasks.

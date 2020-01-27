---
out: Howto-Sequential-Task.html
---

### Def.sequential を用いて逐次タスクを定義する

sbt 0.13.8 で `Def.sequential` という関数が追加されて、準逐次な意味論でタスクを実行できるようになった。
逐次タスクの説明として `compilecheck` というカスタムタスクを定義してみよう。これは、まず `Compile / compile` を実行して、その後で [scalastyle-sbt-plugin](http://www.scalastyle.org/sbt.html) の `Compile / scalastyle` を呼び出す。

セットアップはこのようになる。

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

このタスクを呼び出すには、シェルから `compilecheck` と打ち込む。もしコンパイルが失敗すると、`compilecheck` はそこで実行を中止する。

```
root> compilecheck
[info] Compiling 1 Scala source to /Users/x/proj/target/scala-2.10/classes...
[error] /Users/x/proj/src/main/scala/Foo.scala:3: Unmatched closing brace '}' ignored here
[error] }
[error] ^
[error] one error found
[error] (compile:compileIncremental) Compilation failed
```

これで、タスクを逐次実行できた。

---
out: Directories.html
---

  [ByExample]: sbt-by-example.html
  [Setup]: Setup.html
  [Organizing-Build]: Organizing-Build.html

Directory structure
-------------------

This page assumes you've [installed sbt][Setup] and seen
[sbt by example][ByExample].

### Base directory

In sbt's terminology, the "base directory" is the directory containing
the project. So if you created a project `hello` containing
`/tmp/foo-build/build.sbt` as in the [sbt by example][ByExample],
`/tmp/foo-build` is your base directory.

### Source code

sbt uses the same directory structure as
[Maven](https://maven.apache.org/) for source files by default (all paths
are relative to the base directory):

```
src/
  main/
    resources/
       <files to include in main jar here>
    scala/
       <main Scala sources>
    scala-2.12/
       <main Scala 2.12 specific sources>
    java/
       <main Java sources>
  test/
    resources
       <files to include in test jar here>
    scala/
       <test Scala sources>
    scala-2.12/
       <test Scala 2.12 specific sources>
    java/
       <test Java sources>
```

Other directories in `src/` will be ignored. Additionally, all hidden
directories will be ignored.

Source code can be placed in the project's base directory as
`hello/app.scala`, which may be OK for small projects,
though for normal projects people tend to keep the projects in
the `src/main/` directory to keep things neat.
The fact that you can place `*.scala` source code in the base directory might seem like
an odd trick, but this fact becomes relevant [later][Organizing-Build].

### sbt build definition files

The build definition is described in `build.sbt` (actually any files named `*.sbt`) in the project's base directory.

```
build.sbt
```

### Build support files

In addition to `build.sbt`, `project` directory can contain `.scala` files
that define helper objects and one-off plugins.
See [organizing the build][Organizing-Build] for more.

```
build.sbt
project/
  Dependencies.scala
```

You may see `.sbt` files inside `project/` but they are not equivalent to
`.sbt` files in the project's base directory. Explaining this will
come [later][Organizing-Build], since you'll need some background information first.

### Build products

Generated files (compiled classes, packaged jars, managed files, caches,
and documentation) will be written to the `target` directory by default.

### Configuring version control

Your `.gitignore` (or equivalent for other version control systems) should
contain:

```
target/
```

Note that this deliberately has a trailing `/` (to match only directories)
and it deliberately has no leading `/` (to match `project/target/` in
addition to plain `target/`).

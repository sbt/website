
  [ByExample]: sbt-by-example.html
  [Setup]: Setup.html
  [Organizing-Build]: Organizing-Build.html

Build layout
============

sbt uses conventions for file placement to make it easy to dive into a new sbt build:

```
.
├── build.sbt
├── project/
│   ├── build.properties
│   ├── Dependencies.scala
│   └── plugins.sbt
├── src/
│   ├── main/
│   │   ├── java/
│   │   ├── resources/
│   │   ├── scala/
│   │   └── scala-2.13/
│   └── test/
│       ├── java/
│       ├── resources/
│       ├── scala/
│       └── scala-2.13/
├── subproject-core/
│   └── src/
│       ├── main/
│       └── test/
├─── subproject-util/
│   └── src/
│       ├── main/
│       └── test/
└── target/
```

- The local root directory `.` is the starting point of your build.
- In sbt's terminology, the *base directory* is the directory containing the subproject. In the above, `.`, `subproject-core`, and `subproject-util` are base directories.
- The build definition is described in `build.sbt` (actually any files named `*.sbt`) in the local root directory.
- The sbt version is tracked in `project/build.properties`.
- Generated files (compiled classes, packaged jars, managed files, caches,
and documentation) will be written to the `target` directory by default.

### Build support files

In addition to `build.sbt`, `project` directory can contain `.scala` files
that define helper objects and one-off plugins.
See [organizing the build][Organizing-Build] for more.

```
.
├── build.sbt
├── project/
│   ├── build.properties
│   ├── Dependencies.scala
│   └── plugins.sbt
....
```

You may see `.sbt` files inside `project/` but they are not equivalent to
`.sbt` files in the project's base directory. Explaining this will
come [later][Organizing-Build], since you'll need some background information first.

### Source code

sbt uses the same directory structure as
[Maven](https://maven.apache.org/) for source files by default (all paths
are relative to the base directory):

```
....
├── src/
│   ├── main/
│   │   ├── java/        <main Java sources>
│   │   ├── resources/   <files to include in main JAR>
│   │   ├── scala/       <main Scala sources>
│   │   └── scala-2.13/  <main Scala 2.13 specific sources>
│   └── test/
│       ├── java/        <test Java sources>
│       ├── resources/   <files to include in test JAR>
│       ├── scala/       <test Scala sources>
│       └── scala-2.13/  <test Scala 2.13 specific sources>
....
```

Other directories in `src/` will be ignored. Additionally, all hidden
directories will be ignored.

Source code can be placed in the project's base directory as
`hello/app.scala`, which may be OK for small projects,
though for normal projects people tend to keep the projects in
the `src/main/` directory to keep things neat.
The fact that you can place `*.scala` source code in the base directory might seem like
an odd trick, but this fact becomes relevant [later][Organizing-Build].

### Configuring version control

Your `.gitignore` (or equivalent for other version control systems) should
contain:

```
target/
```

Note that this deliberately has a trailing `/` (to match only directories)
and it deliberately has no leading `/` (to match `project/target/` in
addition to plain `target/`).

sbt automates building, testing, and deployment of your subprojects from information in the build definition.

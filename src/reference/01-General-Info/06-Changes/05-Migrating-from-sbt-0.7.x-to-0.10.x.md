---
out: Migrating-from-sbt-07x.html
---

  [Getting-Started]: ../tutorial/index.html
  [Setup]: ../tutorial/Setup.html
  [Basic-Def]: ../tutorial/Basic-Def.html

Migrating from 0.7 to 0.10+
---------------------------

The assumption here is that you are familiar with sbt 0.7 but new to sbt
$app_version$.

sbt $app_version$'s many new capabilities can be a bit overwhelming, but
this page should help you migrate to $app_version$ with a minimum of fuss.

### Why move to $app_version$?

1.  Faster builds (because it is smarter at re-compiling only what it
    must)
2.  Easier configuration. For simple projects a single `build.sbt` file
    in your root directory is easier to create than
    project/build/MyProject.scala was.
3.  No more `lib_managed` directory, reducing disk usage and avoiding
    backup and version control hassles.
4.  `update` is now much faster and it's invoked automatically by sbt.
5.  Terser output. (Yet you can ask for more details if something goes
    wrong.)

#### Step 1: Read the Getting Started Guide for sbt $app_version$

Reading the [Getting Started Guide][Getting-Started] will
probably save you a lot of confusion.

#### Step 2: Install sbt $app_version$

Download sbt $app_version$ as described on
[the setup page][Setup].

You can run $app_version$ the same way that you run 0.7.x, either simply:

```
java -jar sbt-launch.jar
```

Or (as most users do) with a shell script, as described on
[the setup page][Setup].

For more details see
[the setup page][Setup].

#### Step 3: A technique for switching an existing project

Here is a technique for switching an existing project to $app_version$ while
retaining the ability to switch back again at will. Some builds, such as
those with subprojects, are not suited for this technique, but if you
learn how to transition a simple project it will help you do a more
complex one next.

### Preserve `project/` for 0.7.x project

Rename your `project/` directory to something like `project-old`. This
will hide it from sbt $app_version$ but keep it in case you want to switch
back to 0.7.x.

### Create `build.sbt` for $app_version$

Create a `build.sbt` file in the root directory of your project. See
[.sbt build definition][Basic-Def] in the Getting
Started Guide, and for simple examples
`/Examples/Quick-Configuration-Examples`. If you have a simple project
then converting your existing project file to this format is largely a
matter of re-writing your dependencies and maven archive declarations in
a modified yet familiar syntax.

This `build.sbt` file combines aspects of the old
`project/build/ProjectName.scala` and `build.properties` files. It looks
like a property file, yet contains Scala code in a special format.

A `build.properties` file like:

```
#Project properties
#Fri Jan 07 15:34:00 GMT 2011
project.organization=org.myproject
project.name=My Project
sbt.version=0.7.7
project.version=1.0
def.scala.version=2.7.7
build.scala.versions=2.8.1
project.initialize=false
```

Now becomes part of your `build.sbt` file with lines like:

```scala
name := "My Project"

version := "1.0"

organization := "org.myproject"

scalaVersion := "2.9.2"
```

Currently, a `project/build.properties` is still needed to explicitly
select the sbt version. For example:

### Run sbt $app_version$

Now launch sbt. If you're lucky it works and you're done. For help
debugging, see below.

### Switching back to sbt 0.7.x

If you get stuck and want to switch back, you can leave your `build.sbt`
file alone. sbt 0.7.x will not understand or notice it. Just rename your
$app_version$ `project` directory to something like `project10` and rename
the backup of your old project from `project-old` to `project` again.

#### FAQs

There's a section in the `FAQ </faq>` about migration from 0.7 that
covers several other important points.

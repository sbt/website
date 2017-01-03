---
out: Hello.html
---

  [Basic-Def]: Basic-Def.html
  [Setup]: Setup.html
  [Running]: Running.html

Hello, World
------------

This page assumes you've [installed sbt][Setup] 0.13.13 or later.

### sbt new command

If you're using sbt 0.13.13 or later, you can use sbt `new` command to quickly setup a simple Hello world build. Type the following command to the terminal.

```
\$ sbt new sbt/scala-seed.g8
....
Minimum Scala build.

name [My Something Project]: hello

Template applied in ./hello
```

When prompted for the project name, type `hello`.

This will create a new project under a directory named `hello`.

### Running your app

Now from inside the `hello` directory, start `sbt` and type `run` at the sbt shell. On Linux or OS X the commands might look like this:

```
\$ cd hello
\$ sbt
...
> run
...
[info] Compiling 1 Scala source to /xxx/hello/target/scala-2.12/classes...
[info] Running example.Hello
hello
```

We will see more tasks [later][Running].

### Exiting sbt shell

To leave sbt shell, type `exit` or use Ctrl+D (Unix) or Ctrl+Z
(Windows).

```
> exit
```

### Build definition

The build definition goes in a file called `build.sbt`, located in the project's base directory.
You can take a look at the file, but don't worry if the details of this build file aren't clear yet.
In [.sbt build definition][Basic-Def] you'll learn more about how to write
a `build.sbt` file.

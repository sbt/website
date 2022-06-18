---
out: Scripts.html
---

  [Setup]: Setup.html

Script mode
-----------

sbt has an alternative entry points that may be used to:

-   Compile and execute a Scala script containing dependency
    declarations or other sbt settings

This entry point should be considered experimental. A notable
disadvantage of these approaches is the startup time involved.

### sbt Script runner

The script runner can run a standard Scala script, but with the
additional ability to configure sbt. sbt settings may be embedded in the
script in a comment block that opens with `/***`.

#### Example

Copy the following script and make it executable. You may need to adjust
the first line depending on your script name and operating system. When
run, the example should retrieve Scala, the required dependencies,
compile the script, and run it directly. For example, if you name it
`script.scala`, you would do on Unix:

```
chmod u+x script.scala
./script.scala
```

<nbsp>

```scala
#!/usr/bin/env sbt -Dsbt.version=1.6.1 -Dsbt.main.class=sbt.ScriptMain -error

/***
ThisBuild / scalaVersion := "$example_scala213$"
libraryDependencies += "org.scala-sbt" %% "io" % "1.6.0"
*/

println("hello")
```

This prints out hello.
If you're used to using IO from sbt, we can use that do basic file operations,
like reading a text file.


```scala
#!/usr/bin/env sbt -Dsbt.version=1.6.1 -Dsbt.main.class=sbt.ScriptMain -error

/***
ThisBuild / scalaVersion := "$example_scala213$"
libraryDependencies += "org.scala-sbt" %% "io" % "1.6.0"
*/

import sbt.io.IO
import sbt.io.Path._
import sbt.io.syntax._
import java.io.File
import java.net.URI
import sys.process._

def file(s: String): File = new File(s)
def uri(s: String): URI = new URI(s)

def processFile(f: File): Unit = {
  val lines = IO.readLines(f)
  lines foreach { line =>
    println(line.toUpperCase)
  }
}

args.toList match {
  case Nil => sys.error("usage: ./script.scala <file>...")
  case xs  => xs foreach { x => processFile(file(x)) }
}
```

This script will take file names as argument and print them out in all upper case.

```
\$ ./script.scala script.scala
#!/USR/BIN/ENV SBT -DSBT.MAIN.CLASS=SBT.SCRIPTMAIN -ERROR
....
```

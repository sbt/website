---
out: Manual-Installation.html
---

  [sbt-launch.jar]: $launcher_release_base$$app_version$/sbt-launch.jar

Installing sbt manually
-----------------------

Manual installation requires downloading [sbt-launch.jar][sbt-launch.jar] and creating a
script to start it.

### Unix

Put [sbt-launch.jar][sbt-launch.jar] in `~/bin`.

Create a script to run the jar, by creating `~/bin/sbt` with these
contents:

```
#!/bin/bash
SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M"
java \$SBT_OPTS -jar `dirname \$0`/sbt-launch.jar "\$@"
```

Make the script executable:

```
\$ chmod u+x ~/bin/sbt
```

### Windows

Manual installation for Windows varies by terminal type and whether
Cygwin is used. In all cases, put the batch file or script on the path
so that you can launch sbt in any directory by typing `sbt` at the command
prompt. Also, adjust JVM settings according to your machine if
necessary.

#### Non-Cygwin

For non-Cygwin users using the standard Windows terminal, create a batch file `sbt.bat`:

```
set SCRIPT_DIR=%~dp0
java -Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M -jar "%SCRIPT_DIR%sbt-launch.jar" %*
```

and put the downloaded [sbt-launch.jar][sbt-launch.jar] in the same directory as the
batch file.

#### Cygwin with the standard Windows termnial

If using Cygwin with the standard Windows terminal, create a bash
script `~/bin/sbt`:

```
SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M"
java \$SBT_OPTS -jar sbt-launch.jar "\$@"
```

Replace sbt-launch.jar with the path to your downloaded [sbt-launch.jar][sbt-launch.jar]
and remember to use cygpath if necessary. Make the script executable:

```
\$ chmod u+x ~/bin/sbt
```

#### Cygwin with an Ansi terminal

Cygwin with an Ansi terminal (supports Ansi escape sequences and is configurable via stty), create a bash script
`~/bin/sbt`:

```
SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M"
stty -icanon min 1 -echo > /dev/null 2>&1
java -Djline.terminal=jline.UnixTerminal -Dsbt.cygwin=true \$SBT_OPTS -jar sbt-launch.jar "\$@"
stty icanon echo > /dev/null 2>&1
```

Replace sbt-launch.jar with the path to your downloaded [sbt-launch.jar][sbt-launch.jar]
and remember to use cygpath if necessary. Then, make the script
executable:

```
\$ chmod u+x ~/bin/sbt
```

In order for backspace to work correctly in the scala console, you need
to make sure your backspace key is sending the erase character as
configured by stty. For the default cygwin terminal (mintty) you can
find a setting under Options -> Keys "Backspace sends ^H" which will
need to be checked if your erase key is the cygwin default of ^H.

> **Note:** Other configurations are currently unsupported. Please [submit a pull
> request](https://github.com/sbt/sbt/blob/0.13/CONTRIBUTING.md)
> implementing or describing that support.

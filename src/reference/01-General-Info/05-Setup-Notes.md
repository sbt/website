---
out: Setup-Notes.html
---

Setup Notes
-----------

### Do not put `sbt-launch.jar` on your classpath.

Do *not* put `sbt-launch.jar` in your `\$SCALA_HOME/lib` directory, your
project's `lib` directory, or anywhere it will be put on a classpath. It
isn't a library.

### sbt JVM options and system properties

If the `JAVA_OPTS` and/or `SBT_OPTS` environment variables are defined, 
their content is passed as command line arguments to the JVM running sbt. 

If a file named `.jvmopt` exists in the  current directory, its content 
is appended to `JAVA_OPTS` at sbt startup. Similarly, if `.sbtopts` 
and/or `/etc/sbt/sbtopts` exit, their content is appended to `SBT_OPTS`.
The default value of `JAVA_OPTS` is `-Dfile.encoding=UTF8`.

You can also specify JVM system properties and command line options 
directly as `sbt` arguments: any `-Dkey=val` argument will be passed 
as-is to the JVM, and any `-J-Xfoo` will be passed as `-Xfoo` to the JVM.

See also `sbt --help` for more details. 

### Terminal encoding

The character encoding used by your terminal may differ from Java's
default encoding for your platform. In this case, you will need to specify
the `file.encoding=<encoding>` system property,  which might look like:

```
export JAVA_OPTS="-Dfile.encoding=Cp1252"
sbt
```

### sbt JVM heap, permgen, and stack sizes

If you find yourself running out of permgen space or your workstation is
low on memory, adjust the JVM configuration as you would for any
application. 

For example a common set of memory-related options is:

```
export SBT_OPTS="-Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled"
sbt
```

Or if you prefer to specify them just for this session:

```
sbt -J-Xmx1536M -J-Xss1M -J-XX:+CMSClassUnloadingEnabled
```

### Boot directory

`sbt` is just a bootstrap; the actual meat of sbt, the
Scala compiler and standard library are by default downloaded to the 
shared directory `\$HOME/.sbt/boot/`.

To change the location of this directory, set the `sbt.boot.directory`
system property. A relative path will be resolved
against the current working directory, which can be useful if you want
to avoid sharing the boot directory between projects. For example, the
following uses the pre-0.11 style of putting the boot directory in
`project/boot/`:

```
sbt -Dsbt.boot.directory=project/boot/
```

### HTTP/HTTPS/FTP Proxy

On Unix, sbt will pick up any HTTP, HTTPS, or FTP proxy settings from
the standard `http_proxy`, `https_proxy`, and `ftp_proxy` environment
variables. If you are behind a proxy requiring authentication, your
`sbt` script must also pass flags to set the `http.proxyUser` and
`http.proxyPassword` properties for HTTP, `ftp.proxyUser` and
`ftp.proxyPassword` properties for FTP, or `https.proxyUser` and
`https.proxyPassword` properties for HTTPS.

For example:

```
sbt -Dhttp.proxyUser=username -Dhttp.proxyPassword=mypassword
```

On Windows, your script should set properties for proxy host, port, and
if applicable, username and password. For example, for HTTP:

```
sbt -Dhttp.proxyHost=myproxy -Dhttp.proxyPort=8080 -Dhttp.proxyUser=username -Dhttp.proxyPassword=mypassword
```

Replace `http` with `https` or `ftp` in the above command line to
configure HTTPS or FTP.

### Path encoding

If you have files on your system that have non-ascii characters in them on a
posix system, e.g. Linux or macOS, it may be necessary to set the `LC_CTYPE`
environment variable. If this environment variable is not set to a UTF-8
compatible locale, e.g. `LC_TYPE=en_US.UTF-8`, then sbt may crash with a
`java.nio.file.InvalidPathException`. To see a list of available locales, run
`locale -a`. For more information about locales, see
[International Language Environments Guide](https://docs.oracle.com/cd/E19455-01/806-0169/6j9hsml3j/index.html).

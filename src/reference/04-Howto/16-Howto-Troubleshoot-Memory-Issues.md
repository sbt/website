---
out: Troubleshoot-Memory-Issues.html
---

Troubleshoot memory issues
--------------------------------

sbt may sometimes run out of memory, leading to a crash or badly degraded
performance. The amount of memory needed by sbt is dependent on the number of
subprojects in the build and the plugins that are enabled. For projects with a
large memory footprint, it may be necessary to start sbt with an increased java
heap size. The default java heap size is 1GB.  To increase it to 2GB, you can
run the following command:

```
sbt -J-Xmx2G
```

Any command argument with a leading `-J` is interpreted as a java vm argument.
To automatically increase the heap to 2GB in a project, create or edit the file
`.sbtopts` and add a line with `-J-Xmx2G`.

When sbt is run in interactive mode or as a server (i.e. it was started
with `sbt --client` or `sbtn`), it is important that each task in the build
clean up all of its resources or the memory footprint of sbt may grow over time.
For example, if the run task starts an Akka
[ActorSystem](https://doc.akka.io/docs/akka/current/general/actor-systems.html#terminating-actorsystem),
it is necessary to shutdown the ActorSystem before run exits or else the
memory utilization of the sbt process will increase each time run is invoked.

In order to fix memory leaks, it is necessary to figure out what classes are
persisting in memory longer than expected. The easiest way to do this is with
the
[jmap](https://docs.oracle.com/en/java/javase/11/tools/jmap.html)
command, which is provided by the jdk, and a jvm memory analyzer tool like
[VisualVM](https://visualvm.github.io). Find the process id of the sbt process
that you with to debug using the `ps` command. Then run
`jmap -dump:format=b,file=leak.hprof \$SBT_PID`. Open the `leak.hprof` file in
VisualVM. It may be obvious what classes are taking up the most memory, but
sometimes it is necessary to click the "Compute Retained Sizes" button. This may
take a while if there is a large heap, but it can identify what classes are
taking up the most memory. Often this will help you identify where there is a
thread that has leaked or a cache that has not been cleared.

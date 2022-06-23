---
out: Running-Project-Code.html
---

  [Forking]: Forking.html

Running Project Code
--------------------

The `run` and `console` actions provide a means for running user code in
the same virtual machine as sbt.

`run` also exists in a variant called `runMain` that takes an
additional initial argument allowing you to specify the fully
qualified name of the main class you want to run.  `run` and`runMain`
share the same configuration and cannot be configured separately.

This page describes the problems with running user code in the same
virtual machine as sbt, how sbt handles these problems, what types of
code can use this feature, and what types of code must use a
[forked jvm][Forking].  Skip to User Code if you just want to see when
you should use a [forked jvm][Forking].

### Problems

#### System.exit

User code can call `System.exit`, which normally shuts down the JVM.
Because the `run` and `console` actions run inside the same JVM as sbt,
this also ends the build and requires restarting sbt.

#### Threads

User code can also start other threads. Threads can be left running
after the main method returns. In particular, creating a GUI creates
several threads, some of which may not terminate until the JVM
terminates. The program is not completed until either `System.exit` is
called or all non-daemon threads terminate.

#### Deserialization and class loading

During deserialization, the wrong class loader might be used for various
complex reasons. This can happen in many scenarios, and running under
SBT is just one of them. This is discussed for instance in issues
#163 and #136. The reason is
explained
[here](https://issues.apache.org/jira/browse/GROOVY-1627).

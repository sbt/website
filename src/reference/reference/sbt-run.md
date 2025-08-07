sbt run
=======

Synopsis
--------

`sbt` \[_query_ / \] `run` \[_args_\]

Description
-----------

The `run` task provides a means for running the user program.

In sbt 1.x and earlier, `run` task ran the user program in the same Java virtual machine (JVM) as the sbt server. sbt 2.x implements _client-side run_: the `run` task creates a sandbox environment that contains the program, sends the information back to sbtn, and sbtn launches the user program in a fresh JVM.

### Motivations

There are several motivations for the client-side run.

1. **sys.exit support**. User code can call `sys.exit`, which normally shuts down the JVM.
In sbt 1.x, we needed to trap these `sys.exit` calls to prevent `run` from shutting down the sbt session, using the JDK SecurityManager; however, TrapExit was dropped in sbt 1.6.0 (2021) since JDK 17 deprecated SecurityManager feature. Because client-side run runs the user program in its own JVM, it can call `sys.exit`.
2. **Isolation**. User code can also start threads, or otherwise allocate resources that can be left running after the main method returns. Running user code in a separate JVM gives isolation between the sbt server and the user code.
3. **sbt server availability**. Since the program will run outside of the sbt server, it can become available to the more requests by other clients, for example test or IDE integration.

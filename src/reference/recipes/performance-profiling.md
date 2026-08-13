Performance profiling
=====================

```admonish note
The recipe section of the documentation focuses on the objectives
with minimal explanations about the basics.
```

Objective
---------

I want to profile sbt to understand what it spent time on.

Solution: traces
----------------

1. Shutdown an existing server if any: `sbt shutdown`
2. Run a command with `--server --traces`

```bash
$ sbt --server --traces compile
....
$ ls target/traces/build.trace
target/traces/build.trace
```

This produces a Chrome tracing file `target/traces/build.trace`,
which can be viewed using [Perfetto](https://ui.perfetto.dev/),
shows how long each task took.

Solution: timings
-----------------

1. Shutdown an existing server if any: `sbt shutdown`
2. Run a command with `--server --timings`

```bash
$ sbt --server --timings compile
....
Total time: 4116 ms
  aaa-build / Compile / compileIncremental      : 731 ms
....
```

This outputs task timing information on the terminal.

Solution: flame graph
---------------------

Install [async-profiler](https://github.com/async-profiler/async-profiler)
by putting `asprof` and `jfrconv` on your `PATH`:

```bash
$ ln -s $HOME/Applications/async-profiler-4.2/bin/asprof $HOME/bin/asprof
$ ln -s $HOME/Applications/async-profiler-4.2/bin/jfrconv $HOME/bin/jfrconv
```

1. Close anything that may affect the profiling, and run sbt in one terminal
2. In another terminal run `jps` to identify the process ID of sbt

   ```bash
   $ jps
   92746 sbt-launch.jar
   92780 Jps
   ```
3. Run `asprof` with the duration flag. `-d 60` means 60s:

   ```bash
   $ asprof -d 60 -f /tmp/flamegraph.jfr <process id>
   ```
   This produces a JFR (Java Flight Recorder) file.
4. Run `jfrconv` as follows to convert the `*.jfr` file to a flame graph:

   ```bash
   $ jfrconv --lines /tmp/flamegraph.jfr /tmp/flamegraph.html
   ```

Alternative: JVM profiling
--------------------------

You can use a profiler like [VisualVM](https://visualvm.github.io/) to identify implementation-level timings.

1. Shutdown all existing servers if any: `sbt shutdownall`
2. Start an sbt session with `--server`
3. Launch VisualVM, and double-click on `xsbt.boot.Boot`
4. Go to Sampler tab, and click CPU
5. Run a command inside the sbt shell
6. Click on the Stop button in Sampler tab

The threads named pool-n-thread-m are the task threads.

---
out: Howto-Triggered.html
---

  [Full-Def]: ../tutorial/Full-Def.html

Triggered execution
-------------------

<a name="basic"></a>

### Run a command when sources change

You can make a command run when certain files change by prefixing the
command with `~`. Monitoring is terminated when `enter` is pressed. This
triggered execution is configured by the `watch` setting, but typically
the basic settings `watchSources` and `pollInterval` are modified as
described in later sections.

The original use-case for triggered execution was continuous
compilation:

```
> ~ test:compile

> ~ compile
```

You can use the triggered execution feature to run any command or task,
however. The following will poll for changes to your source code (main
or test) and run `testOnly` for the specified test.

```
> ~ testOnly example.TestA
```

<a name="multi"></a>

### Run multiple commands when sources change

The command passed to `~` may be any command string, so multiple
commands may be run by separating them with a semicolon. For example,

```
> ~ ;a ;b
```

This runs `a` and then `b` when sources change.

<a name="sources"></a>

### Configure the sources that are checked for changes

-   `watchSources` defines the files for a single project that are
    monitored for changes. By default, a project watches resources and
    Scala and Java sources.
-   `watchTransitiveSources` then combines the `watchSources` for the
    current project and all execution and classpath dependencies (see
    [.scala build definition][Full-Def] for details on inter-project
    dependencies).

To add the file `demo/example.txt` to the files to watch,

```scala
watchSources += baseDirectory.value / "demo" / "examples.txt"
```

<a name="interval"></a>

### Set the time interval between checks for changes to sources

`pollInterval` selects the interval between polling for changes in
milliseconds. The default value is `500 ms`. To change it to `1 s`,

```scala
pollInterval := 1000 // in ms
```

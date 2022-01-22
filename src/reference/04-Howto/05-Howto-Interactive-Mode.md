---
out: Howto-Interactive-Mode.html
---

  [Build-State]: Build-State.html
  [ShellHistory]: Running.html#history

Interactive mode
----------------

<a name="basic_completion"></a>

### Use tab completion

By default, sbt's interactive mode is started when no commands are
provided on the command line or when the `shell` command is invoked.

As the name suggests, tab completion is invoked by hitting the tab key.
Suggestions are provided that can complete the text entered to the left
of the current cursor position. Any part of the suggestion that is
unambiguous is automatically appended to the current text. Commands
typically support tab completion for most of their syntax.

As an example, entering `tes` and hitting tab:

```
> tes<TAB>
```

results in sbt appending a `t`:

```
> test
```

To get further completions, hit tab again:

```
> test<TAB>
testFrameworks   testListeners    testLoader       testOnly         testOptions      test:
```

Now, there is more than one possibility for the next character, so sbt
prints the available options. We will select `testOnly` and get more
suggestions by entering the rest of the command and hitting tab twice:

```
> testOnly<TAB><TAB>
--                      sbt.DagSpecification    sbt.EmptyRelationTest   sbt.KeyTest             sbt.RelationTest        sbt.SettingsTest
```

The first tab inserts an unambiguous space and the second suggests names
of tests to run. The suggestion of `--` is for the separator between
test names and options provided to the test framework. The other
suggestions are names of test classes for one of sbt's modules. Test
name suggestions require tests to be compiled first. If tests have been
added, renamed, or removed since the last test compilation, the
completions will be out of date until another successful compile.

<a name="verbose_completion"></a>

### Show more tab completion suggestions

Some commands have different levels of completion. Hitting tab multiple
times increases the verbosity of completions. (Presently, this feature
is only used by the `set` command.)

<a name="change_keybindings"></a>

### Modify the default JLine keybindings

JLine, used by both Scala and sbt, uses a configuration file for many of
its keybindings. The location of this file can be changed with the
system property `jline.keybindings`. The default keybindings file is
included in the sbt launcher and may be used as a starting point for
customization.

<a name="prompt"></a>

### Configure the prompt string

By default, sbt only displays `>` to prompt for a command. This can be
changed through the `shellPrompt` setting, which has type
`State => String`. [State][Build-State] contains all state
for sbt and thus provides access to all build information for use in the
prompt string.

Examples:

```scala
// set the prompt (for this build) to include the project id.
ThisBuild / shellPrompt := { state => Project.extract(state).currentRef.project + "> " }

// set the prompt (for the current project) to include the username
shellPrompt := { state => System.getProperty("user.name") + "> " }
```

<a name="history"></a>

### Use history

See [sbt shell history][ShellHistory].

<a name="history_file"></a>

### Change the location of the interactive history file

By default, interactive history is stored in the `target/` directory for
the current project (but is not removed by a `clean`). History is thus
separate for each subproject. The location can be changed with the
`historyPath` setting, which has type `Option[File]`. For example,
history can be stored in the root directory for the project instead of
the output directory:

```scala
historyPath := Some(baseDirectory.value / ".history")
```

The history path needs to be set for each project, since sbt will use
the value of `historyPath` for the current project (as selected by the
`project` command).

<a name="share_history"></a>

### Use the same history for all projects

The previous section describes how to configure the location of the
history file. This setting can be used to share the interactive history
among all projects in a build instead of using a different history for
each project. The way this is done is to set `historyPath` to be the
same file, such as a file in the root project's `target/` directory:

```scala
historyPath :=
  Some( (target in LocalRootProject).value / ".history")
```

The `in LocalRootProject` part means to get the output directory for the
root project for the build.

<a name="disable_history"></a>

### Disable interactive history

If, for whatever reason, you want to disable history, set `historyPath`
to `None` in each project it should be disabled in:

```
> historyPath := None
```

<a name="pre_commands"></a>

### Run commands before entering interactive mode

Interactive mode is implemented by the `shell` command. By default, the
`shell` command is run if no commands are provided to sbt on the command
line. To run commands before entering interactive mode, specify them on
the command line followed by `shell`. For example,

```
\$ sbt clean compile shell
```

This runs `clean` and then `compile` before entering the interactive
prompt. If either `clean` or `compile` fails, sbt will exit without
going to the prompt. To enter the prompt whether or not these initial
commands succeed, prepend `"onFailure shell"`, which means to run `shell` if any
command fails. For example,

```
\$ sbt "onFailure shell" clean compile shell
```

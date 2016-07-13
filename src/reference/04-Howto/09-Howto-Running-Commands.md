---
out: Howto-Running-Commands.html
---

Running commands
----------------

<a name="batch"></a>

### Pass arguments to a command or task in batch mode

sbt interprets each command line argument provided to it as a command
together with the command's arguments. Therefore, to run a command that
takes arguments in batch mode, quote the command using double quotes,
and its arguments. For example,

```
\$ sbt "project X" clean "~ compile"
```

<a name="multi"></a>

### Provide multiple commands to run consecutively

Multiple commands can be scheduled at once by prefixing each command
with a semicolon. This is useful for specifying multiple commands where
a single command string is accepted. For example, the syntax for
triggered execution is `~ <command>`. To have more than one command run
for each triggering, use semicolons. For example, the following runs
`clean` and then `compile` each time a source file changes:

```
> ~ ;clean;compile
```

<a name="read"></a>

### Read commands from a file

The `<` command reads commands from the files provided to it as
arguments. Run `help <` at the sbt prompt for details.

<a name="alias"></a>

### Define an alias for a command or task

The `alias` command defines, removes, and displays aliases for commands.
Run `help alias` at the sbt prompt for details.

Example usage:

```
> alias a=about
> alias
    a = about    
> a
[info] This is sbt ...
> alias a=
> alias
> a
[error] Not a valid command: a ...
```

<a name="eval"></a>

### Quickly evaluate a Scala expression

The `eval` command compiles and runs the Scala expression passed to it
as an argument. The result is printed along with its type. For example,

```
> eval 2+2
4: Int
```

Variables defined by an `eval` are not visible to subsequent `eval`s,
although changes to system properties persist and affect the JVM that is
running sbt. Use the Scala REPL (`console` and related commands) for
full support for evaluating Scala code interactively.

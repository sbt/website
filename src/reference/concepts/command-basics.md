Command basics
==============

A _command_ is a system-level building block of sbt, often used to capture user interaction or IDE interaction.

<img src="../files/command.svg" style="width: 50%;"></img>

We can think of each command as a `State => State` function. In sbt, the state represents the following:

1. Build structure (`build.sbt` etc)
2. Your disk (source code, JAR outputs, etc)

Thus, a command would typically modify either the build structure or the disk. For example, the `set` command can apply a setting to modify the build strcuture:

```scala
> set name := "foo"
```

The `act` command can lift a task such as `compile` into a command:

```scala
> compile
```

The compilation would read from the disk and write outputs, or display error messages on the screen.

Commands are sequentially processed
-----------------------------------

Because there is only one state, a characteristic of commands is that they are executed one at a time.

![command](../files/command2.svg)

There are some execptions to this rule, but generally commands run sequentially. One mental image that might be useful is that a command is similar to a cashier taking an order in a cafe, and it will be processed in the sequence it was received.

Tasks run in parallel
---------------------

As mentioned above, the `act` command translates tasks into the command level. While doing so, the `act` command will broadcast the task across the aggregated subprojects and run independent tasks in parallel.

Similarly, the `reload` command that runs during the startup of a session will initialize the settings in parallel.

![act](../files/act.svg)

The role of sbt server
----------------------

sbt server is a service that accepts commands from either the command line or a network API called Build Server Protocol. This mechanism allows both the build user and IDEs to share the same sbt session.

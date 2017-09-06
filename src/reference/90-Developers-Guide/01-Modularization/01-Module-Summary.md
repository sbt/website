---
out: Module-Summary.html
---

  [iorepo]: https://github.com/sbt/io
  [serializationrepo]: https://github.com/sbt/serialization
  [Sbt-Launcher]: Sbt-Launcher.html
  [Compiler-Interface]: Compiler-Interface.html
  [pickling]: https://github.com/scala/pickling
  [utilrepo]: https://github.com/sbt/util
  [librarymanagementrepo]: https://github.com/sbt/librarymanagement
  [zincrepo]: https://github.com/sbt/zinc
  [launcherrepo]: https://github.com/sbt/launcher
  [conscriptrepo]: https://github.com/foundweekends/conscript
  [websiterepo]: https://github.com/sbt/website

### Module summary

The following is a conceptual diagram of the modular layers:

![Module diagram](files/module-diagram.png)

This diagram is arranged such that each layer depends only on the layers underneath it.

#### IO API ([sbt/io][iorepo])

IO API is a low level API to deal with files and directories.

#### Serialization API ([sbt/serialization][serializationrepo])

Serialization API is an opinionated wrapper around [Scala Pickling][pickling].
The responsibility of the serialization API is to turn values into JSON.

#### Util APIs ([sbt/util][utilrepo])

Util APIs provide commonly used features like logging and internal datatypes used by sbt.

#### LibraryManagement API ([sbt/librarymanagement][librarymanagementrepo])

sbt's library management system is based on Apache Ivy, and as such
the concepts and terminology around the library management system are also influenced by Ivy.
The responsibility of the library management API is to calculate the transitive dependency graph,
and download artifacts from the given repositories.

#### IncrementalCompiler API ([sbt/zinc][zincrepo])

Incremental compilation of Scala is so fundamental
that we now seldom think of it as a feature of sbt.
There are number of subprojects/classes involved that are actually internal details,
and we should use this opportunity to hide them.

#### Build API (tbd)

This is the part that's exposed to `build.sbt`.
The responsibility of the module is to load the build files and plugins,
and provide a way for commands to be executed on the state.

This might remain at [sbt/sbt](https://github.com/sbt/sbt).

#### sbt Launcher ([sbt/launcher][launcherrepo])

The sbt launcher provides a generic container that can load and run
programs resolved using the Ivy dependency manager.
sbt uses this as the deployment mechanism, but it can be used for other purposes.

See [foundweekends/conscript][conscriptrepo] and [Launcher][Sbt-Launcher] for more details.

#### Client/Server (tbd)

Currently developed in [sbt/sbt-remote-control](https://github.com/sbt/sbt-remote-control).
sbt Server provides a JSON-based API wrapping functionality of the command line experience.

One of the clients will be the "terminal client",
which subsumes the command line sbt shell.
Other clients that are planned are IDE integrations.

#### Website ([sbt/website][websiterepo])

This website's source.

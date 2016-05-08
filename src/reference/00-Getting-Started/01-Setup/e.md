---
out: Activator-Installation.html
---

  [Manual-Installation]: Manual-Installation.html

Installing Lightbend Activator (including sbt)
---------------------

Lightbend Activator is a custom version of sbt which adds two extra
commands, `activator ui` and `activator new`. The `activator`
command is a superset of sbt, in short.

You can obtain Activator from
[lightbend.com](http://www.lightbend.com/activator/download).

If you see a command line such as `sbt ~test` in the
documentation, you will also be able to type `activator ~test`.
Any Activator project can be opened in sbt and vice versa because
Activator is "sbt powered."

The Activator download includes an `activator` script and an
`activator-launch.jar`, which are equivalent to the sbt script and
launch jar described under
[manual installation][Manual-Installation].  Here are the
differences between Activator and a
[manual installation][Manual-Installation] of sbt:

 * typing `activator` with no arguments will attempt to guess
   whether to enter `activator shell` or `activator ui` mode;
   type `activator shell` to force the command line prompt.
 * `activator new` allows you to create projects from a large
   [catalog of template projects](https://www.lightbend.com/activator/templates),
   for example the `play-scala` template is a skeleton
   [Play Framework](https://playframework.com) Scala app.
 * `activator ui` launches a quick start UI that can be used to
   work through tutorials from the template catalog (many
   templates in the catalog have accompanying tutorials).

Activator offers two downloads; the small "minimal" download
contains only the wrapper script and launch jar, while the large
"full" download contains a preloaded Ivy cache with jars for
Scala, Akka, and the Play Framework.

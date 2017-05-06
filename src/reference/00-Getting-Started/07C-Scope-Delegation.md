---
out: Scope-Delegation.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html

Scope delegation (.value lookup)
--------------------------------

This page describes scope delegation. It assumes you've read and understood the
previous pages, [build definition][Basic-Def] and [scopes][Scopes].

Now that we've covered all the details of scoping, we can explain the `.value`
lookup in detail. It's ok to skip this section if this is your first time reading this page.

Because the term `Global` is used for both a scope component `*`,
and as shorthand for the scope `(Global, Global, Global)`,
in this page we will use the symbol `*` when we mean it as the scope component.

To summarize what we've learned so far:

- A scope is a tuple of components in three axes: the subproject axis, the configuration axis, and the task axis.
- There's a special scope component `*` (also called `Global`) for any of the scope axes.
- There's a special scope component `ThisBuild` (written as `{.}` in shell) for **the subprojects axis** only.
- `Test` extends `Runtime`, and `Runtime` extends `Compile` configuration.
- A key placed in build.sbt is scoped to `(\${current subproject}, *, *)` by default.
- A key can be further scoped using `.in(...)` method.

Now let's suppose we have the following build definition:

```scala
lazy val foo = settingKey[Int]("")
lazy val bar = settingKey[Int]("")

lazy val projX = (project in file("x"))
  .settings(
    foo := {
      (bar in Test).value + 1
    },
    bar in Compile := 1
  )
```

Inside of `foo`'s setting body a dependency on the scoped key `(bar in Test)` is declared.
However, despite `bar in Test` being undefined in `projX`,
sbt is still able to resolve `(bar in Test)` to another scoped key,
resulting in `foo` initialized as `2`.

sbt has a well-defined fallback search path called *scope delegation*.
This feature allows you to set a value once in a more general scope,
allowing multiple more-specific scopes to inherit the value.

### Scope delegation rules

Here are the rules for scope delegation:

- Rule 1: Scope axes have the following precedence: the subproject axis, the configuration axis, and then the task axis.
- Rule 2: Given a scope, delegate scopes are searched by substituting the task axis in the following order:
  the given task scoping, and then `*` (`Global`), which is non-task scoped version of the scope.
- Rule 3: Given a scope, delegate scopes are searched by substituting the configuration axis in the following order:
  the given configuration, its parents, their parents and so on, and then `*` (`Global`, same as unscoped configuration axis).
- Rule 4: Given a scope, delegate scopes are searched by substituting the subproject axis in the following order:
  the given subproject, `ThisBuild`, and then `*` (`Global`).
- Rule 5: A delegated scoped key and its dependent settings/tasks are evaluated without carrying the original context.

We will look at each rule in the rest of this page.

### Rule 1: Scope axis precedence

- Rule 1: Scope axes have the following precedence: the subproject axis, the configuration axis, and then the task axis.

In other words, given two scopes candidates, if one has more specific value on the subproject axis,
it will always win regardless of the configuration or the task scoping.
Similarly, if subprojects are the same, one with more specific configuration value will always win regardless
of the task scoping. We will see more rules to define *more specific*.

### Rule 2: The task axis delegation

- Rule 2: Given a scope, delegate scopes are searched by **substituting** the task axis in the following order:
  the given task scoping, and then `*` (`Global`), which is non-task scoped version of the scope.

Here we have a concrete rule for how sbt will generate delegate scopes given a key.
Remember, we are trying to show the search path given an arbitrary `(xxx in yyy).value`.

**Exercise A**: Given the following build definition:

```scala
lazy val projA = (project in file("a"))
  .settings(
    name := {
      "foo-" + (scalaVersion in packageBin).value
    },
    scalaVersion := "2.11.11"
  )
```

What is the value of `name in projA` (`projA/name` in sbt shell)?

1. `"foo-2.11.11"`
2. `"foo-$example_scala_version$"`
3. something else?

The answer is `"foo-2.11.11"`.
Inside of `.settings(...)`, `scalaVersion` is automatically scoped to `(projA, *, *)`,
so `scalaVersion in packageBin` becomes `scalaVersion in (projA, *, packageBin)`.
That particular scoped key is undefined.
By using Rule 2, sbt will substitute the task axis to `*` as `(projA, *, *)` (or `proj/scalaVersion` in shell).
That scoped key is defined to be `"2.11.11"`.

### Rule 3: The configuration axis search path

- Rule 3: Given a scope, delegate scopes are searched by substituting the configuration axis in the following order:
  the given configuration, its parents, their parents and so on, and then `*` (`Global`, same as unscoped configuration axis).

The example for that is `projX` that we saw earlier:

```scala
lazy val foo = settingKey[Int]("")
lazy val bar = settingKey[Int]("")

lazy val projX = (project in file("x"))
  .settings(
    foo := {
      (bar in Test).value + 1
    },
    bar in Compile := 1
  )
```

If we write out the full scope again, it's `(projX, Test, *)`.
Also recall that `Test` extends `Runtime`, and `Runtime` extends `Compile`.

`(bar in Test)` is undefined, but due to Rule 3 sbt will look for
`bar` scoped in `(projX, Test, *)`, `(projX, Runtime, *)`, and then
`(projX, Compile, *)`. The last one is found, which is `bar in Compile`.

### Rule 4: The subproject axis search path

- Rule 4: Given a scope, delegate scopes are searched by substituting the subproject axis in the following order:
  the given subproject, `ThisBuild`, and then `*` (`Global`).

**Exercise B**: Given the following build definition:

```scala
organization in ThisBuild := "com.example"

lazy val projB = (project in file("b"))
  .settings(
    name := "abc-" + organization.value,
    organization := "org.tempuri"
  )
```

What is the value of `name in projB` (`projB/name` in shell)?

1. `"abc-com.example"`
2. `"abc-org.tempuri"`
3. something else?

The answer is `abc-org.tempuri`.
So based on Rule 4, the first search path is `organization` scoped to `(projB, *, *)`,
which is defined in `projB` as `"org.tempuri"`.
This has higher precedence than the build-level setting `organization in ThisBuild`.

#### Scope axis precedence, again

**Exercise C**: Given the following build definition:

```scala
scalaVersion in (ThisBuild, packageBin) := "2.12.2"

lazy val projC = (project in file("c"))
  .settings(
    name := {
      "foo-" + (scalaVersion in packageBin).value
    },
    scalaVersion := "2.11.11"
  )
```

What is value of `name in projC`?

1. `"foo-2.12.2"`
2. `"foo-2.11.11"`
3. something else?

The answer is `foo-2.11.11`.
`scalaVersion` scoped to `(projC, *, packageBin)` is undefined.
Rule 2 finds `(projC, *, *)`. Rule 4 finds `(ThisBuild, *, packageBin)`.
In this case Rule 1 dictates that more specific value on the subproject axis wins,
which is `(projC, *, *)` that is defined to `"2.11.11"`.

**Exercise D**: Given the following build definition:

```scala
scalacOptions in ThisBuild += "-Ywarn-unused-import"

lazy val projD = (project in file("d"))
  .settings(
    test := {
      println((scalacOptions in (Compile, console)).value)
    },
    scalacOptions in console -= "-Ywarn-unused-import",
    scalacOptions in Compile := scalacOptions.value // added by sbt
  )
```

What would you see if you ran `projD/test`?

1. `List()`
2. `List(-Ywarn-unused-import)`
3. something else?

The answer is `List(-Ywarn-unused-import)`.
Rule 2 finds `(projD, Compile, *)`,
Rule 3 finds `(projD, *, console)`,
and Rule 4 finds `(ThisBuild, *, *)`.
Rule 1 selects `(projD, Compile, *)`
because it has the subproject axis `projD`, and the configuration axis has higher
precedence over the task axis.

Next, `scalacOptions in Compile` refers to `scalacOptions.value`,
we next need to find a delegate for `(projD, *, *)`.
Rule 4 finds `(ThisBuild, *, *)` and thus it resolves to `List(-Ywarn-unused-import)`.

### Inspect command lists the delegates

You might want to look up quickly what is going on.
This is where `inspect` can be used.

```
Hello> inspect projD/compile:console::scalacOptions
[info] Task: scala.collection.Seq[java.lang.String]
[info] Description:
[info]  Options for the Scala compiler.
[info] Provided by:
[info]  {file:/Users/xxxx/}projD/compile:scalacOptions
[info] Defined at:
[info]  /Users/xxxx/build.sbt:47
[info] Reverse dependencies:
[info]  projD/compile:console
[info]  projD/*:test
[info] Delegates:
[info]  projD/compile:console::scalacOptions
[info]  projD/compile:scalacOptions
[info]  projD/*:console::scalacOptions
[info]  projD/*:scalacOptions
[info]  {.}/compile:console::scalacOptions
[info]  {.}/compile:scalacOptions
[info]  {.}/*:console::scalacOptions
[info]  {.}/*:scalacOptions
[info]  */compile:console::scalacOptions
[info]  */compile:scalacOptions
[info]  */*:console::scalacOptions
[info]  */*:scalacOptions
....
```

Note how "Provided by" shows that `projD/compile:console::scalacOptions`
is provided by `projD/compile:scalacOptions`.
Also under "Delegates", *all* of the possible delegate candidates
listed in the order of precedence!

- All the scopes with `projD` scoping on the subproject axis are listed first,
  then `ThisBuild` (`{.}`), and `*`.
- Within a subproject, scopes with `Compile` scoping on the configuration axis
  are listed first, then falls back to `*`.
- Finally, the task axis scoping lists the given task scoping `console::` and the one without.

### .value lookup vs dynamic dispatch

- Rule 5: A delegated scoped key and its dependent settings/tasks are evaluated without carrying the original context.

Note that scope delegation feels similar to class inheritance in an object-oriented language,
but there's a difference. In an OO language like Scala if there's a method named
`drawShape` on a trait `Shape`, its subclasses can override the behavior even when `drawShape` is used
by other methods in the `Shape` trait, which is called dynamic dispatch.

In sbt, however, scope delegation can delegate a scope to a more general scope,
like a project-level setting to a build-level settings,
but that build-level setting cannot refer to the project-level setting.

**Exercise E**: Given the following build definition:

```scala
lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.2",
      version      := scalaVersion.value + "_0.1.0"
    )),
    name := "Hello"
  )

lazy val projE = (project in file("e"))
  .settings(
    scalaVersion := "2.11.11"
  )
```

What will `projE/version` return?

1. `"2.12.2_0.1.0"`
2. `"2.11.11_0.1.0"`
3. something else?

The answer is `2.12.2_0.1.0`.
`projD/version` delegates to `version in ThisBuild`,
which depends on `scalaVersion in ThisBuild`.
Because of this reason, build level setting should be limited mostly to simple value assignments.

**Exercise F**: Given the following build definition:

```scala
scalacOptions in ThisBuild += "-D0"
scalacOptions += "-D1"

lazy val projF = (project in file("f"))
  .settings(
    scalacOptions in compile += "-D2",
    scalacOptions in Compile += "-D3",
    scalacOptions in (Compile, compile) += "-D4",
    test := {
      println("bippy" + (scalacOptions in (Compile, compile)).value.mkString)
    }
  )
```

What will `projF/test` show?

1. `"bippy-D4"`
2. `"bippy-D2-D4"`
3. `"bippy-D0-D3-D4"`
4. something else?

The answer is `"bippy-D0-D3-D4"`. This is a variation of an exercise
originally created by [Paul Phillips](https://gist.github.com/paulp/923154ab2d61882195cdea47483592ca).

It's a great demonstration of all the rules because `someKey += "x"` expands to

```scala
someKey += {
  val old = someKey.value
  old :+ "x"
}
```

Retrieving the old value would cause delegation, and due to Rule 5,
it will go to another scoped key.
Let's get rid of `+=` first, and annotate the delegates for old values:

```scala
scalacOptions in ThisBuild := {
  // scalacOptions in Global <- Rule 4
  val old = (scalacOptions in ThisBuild).value
  old :+ "-D0"
}

scalacOptions := {
  // scalacOptions in ThisBuild <- Rule 4
  val old = scalacOptions.value
  old :+ "-D1"
}

lazy val projF = (project in file("f"))
  .settings(
    scalacOptions in compile := {
      // scalacOptions in ThisBuild <- Rules 2 and 4
      val old = (scalacOptions in compile).value
      old :+ "-D2"
    },
    scalacOptions in Compile := {
      // scalacOptions in ThisBuild <- Rules 3 and 4
      val old = (scalacOptions in Compile).value
      old :+ "-D3"
    },
    scalacOptions in (Compile, compile) := {
      // scalacOptions in (projF, Compile) <- Rules 1 and 2
      val old = (scalacOptions in (Compile, compile)).value
      old :+ "-D4"
    },
    test := {
      println("bippy" + (scalacOptions in (Compile, compile)).value.mkString)
    }
  )
```

This becomes:

```scala
scalacOptions in ThisBuild := {
  Nil :+ "-D0"
}

scalacOptions := {
  List("-D0") :+ "-D1"
}

lazy val projF = (project in file("f"))
  .settings(
    scalacOptions in compile := List("-D0") :+ "-D2",
    scalacOptions in Compile := List("-D0") :+ "-D3",
    scalacOptions in (Compile, compile) := List("-D0", "-D3") :+ "-D4",
    test := {
      println("bippy" + (scalacOptions in (Compile, compile)).value.mkString)
    }
  )
```

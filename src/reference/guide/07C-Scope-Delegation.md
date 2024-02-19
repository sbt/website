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

To summarize what we've learned so far:

- A scope is a tuple of components in three axes: the subproject axis, the configuration axis, and the task axis.
- There's a special scope component `Zero` for any of the scope axes.
- There's a special scope component `ThisBuild` for **the subprojects axis** only.
- `Test` extends `Runtime`, and `Runtime` extends `Compile` configuration.
- A key placed in build.sbt is scoped to `\${current subproject} / Zero / Zero` by default.
- A key can be scoped using `/` operator.

Now let's suppose we have the following build definition:

@@snip [build.sbt]($root$/src/sbt-test/ref/scope-delegation/x/build.sbt) { #fig1 }

Inside of `foo`'s setting body a dependency on the scoped key `Test / bar` is declared.
However, despite `Test / bar` being undefined in `projX`,
sbt is still able to resolve `Test / bar` to another scoped key,
resulting in `foo` initialized as `2`.

sbt has a well-defined fallback search path called *scope delegation*.
This feature allows you to set a value once in a more general scope,
allowing multiple more-specific scopes to inherit the value.

### Scope delegation rules

Here are the rules for scope delegation:

- Rule 1: Scope axes have the following precedence: the subproject axis, the configuration axis, and then the task axis.
- Rule 2: Given a scope, delegate scopes are searched by substituting the task axis in the following order:
  the given task scoping, and then `Zero`, which is non-task scoped version of the scope.
- Rule 3: Given a scope, delegate scopes are searched by substituting the configuration axis in the following order:
  the given configuration, its parents, their parents and so on, and then `Zero` (same as unscoped configuration axis).
- Rule 4: Given a scope, delegate scopes are searched by substituting the subproject axis in the following order:
  the given subproject, `ThisBuild`, and then `Zero`.
- Rule 5: A delegated scoped key and its dependent settings/tasks are evaluated without carrying the original context.

We will look at each rule in the rest of this page.

### Rule 1: Scope axis precedence

- Rule 1: Scope axes have the following precedence: the subproject axis, the configuration axis, and then the task axis.

In other words, given two scope candidates, if one has more specific value on the subproject axis,
it will always win regardless of the configuration or the task scoping.
Similarly, if subprojects are the same, one with more specific configuration value will always win regardless
of the task scoping. We will see more rules to define *more specific*.

### Rule 2: The task axis delegation

- Rule 2: Given a scope, delegate scopes are searched by **substituting** the task axis in the following order:
  the given task scoping, and then `Zero`, which is non-task scoped version of the scope.

Here we have a concrete rule for how sbt will generate delegate scopes given a key.
Remember, we are trying to show the search path given an arbitrary `(xxx / yyy).value`.

**Exercise A**: Given the following build definition:

```scala
lazy val projA = (project in file("a"))
  .settings(
    name := {
      "foo-" + (packageBin / scalaVersion).value
    },
    scalaVersion := "2.11.11"
  )
```

What is the value of `projA / name`?

1. `"foo-2.11.11"`
2. `"foo-$example_scala_version$"`
3. something else?

The answer is `"foo-2.11.11"`.
Inside of `.settings(...)`, `scalaVersion` is automatically scoped to `projA / Zero / Zero`,
so `packageBin / scalaVersion` becomes `projA / Zero / packageBin / scalaVersion`.
That particular scoped key is undefined.
By using Rule 2, sbt will substitute the task axis to `Zero` as `projA / Zero / Zero` (or `projA / scalaVersion`).
That scoped key is defined to be `"2.11.11"`.

### Rule 3: The configuration axis search path

- Rule 3: Given a scope, delegate scopes are searched by substituting the configuration axis in the following order:
  the given configuration, its parents, their parents and so on, and then `Zero` (same as unscoped configuration axis).

The example for that is `projX` that we saw earlier:

@@snip [build.sbt]($root$/src/sbt-test/ref/scope-delegation/x/build.sbt) { #fig1 }

If we write out the full scope again, it's `projX / Test / Zero`.
Also recall that `Test` extends `Runtime`, and `Runtime` extends `Compile`.

`Test / bar` is undefined, but due to Rule 3 sbt will look for
`bar` scoped in `projX / Test / Zero`, `projX / Runtime / Zero`, and then
`projX / Compile / Zero`. The last one is found, which is `Compile / bar`.

### Rule 4: The subproject axis search path

- Rule 4: Given a scope, delegate scopes are searched by substituting the subproject axis in the following order:
  the given subproject, `ThisBuild`, and then `Zero`.

**Exercise B**: Given the following build definition:

```scala
ThisBuild / organization := "com.example"

lazy val projB = (project in file("b"))
  .settings(
    name := "abc-" + organization.value,
    organization := "org.tempuri"
  )
```

What is the value of `projB / name`?

1. `"abc-com.example"`
2. `"abc-org.tempuri"`
3. something else?

The answer is `abc-org.tempuri`.
So based on Rule 4, the first search path is `organization` scoped to `projB / Zero / Zero`,
which is defined in `projB` as `"org.tempuri"`.
This has higher precedence than the build-level setting `ThisBuild / organization`.

#### Scope axis precedence, again

**Exercise C**: Given the following build definition:

@@snip [build.sbt]($root$/src/sbt-test/ref/scope-delegation/x/build.sbt) { #fig_c }

What is value of `projC / name`?

1. `"foo-2.12.2"`
2. `"foo-2.11.11"`
3. something else?

The answer is `foo-2.11.11`.
`scalaVersion` scoped to `projC / Zero / packageBin` is undefined.
Rule 2 finds `projC / Zero / Zero`. Rule 4 finds `ThisBuild / Zero / packageBin`.
In this case Rule 1 dictates that more specific value on the subproject axis wins,
which is `projC / Zero / Zero` that is defined to `"2.11.11"`.

**Exercise D**: Given the following build definition:

@@snip [build.sbt]($root$/src/sbt-test/ref/scope-delegation/x/build.sbt) { #fig_d }

What would you see if you ran `projD/test`?

1. `List()`
2. `List(-Ywarn-unused-import)`
3. something else?

The answer is `List(-Ywarn-unused-import)`.
Rule 2 finds `projD / Compile / Zero`,
Rule 3 finds `projD / Zero / console`,
and Rule 4 finds `ThisBuild / Zero / Zero`.
Rule 1 selects `projD / Compile / Zero`
because it has the subproject axis `projD`, and the configuration axis has higher
precedence over the task axis.

Next, `Compile / scalacOptions` refers to `scalacOptions.value`,
we next need to find a delegate for `projD / Zero / Zero`.
Rule 4 finds `ThisBuild / Zero / Zero` and thus it resolves to `List(-Ywarn-unused-import)`.

### Inspect command lists the delegates

You might want to look up quickly what is going on.
This is where `inspect` can be used.

```
sbt:projd> inspect projD / Compile / console / scalacOptions
[info] Task: scala.collection.Seq[java.lang.String]
[info] Description:
[info]  Options for the Scala compiler.
[info] Provided by:
[info]  ProjectRef(uri("file:/tmp/projd/"), "projD") / Compile / scalacOptions
[info] Defined at:
[info]  /tmp/projd/build.sbt:9
[info] Reverse dependencies:
[info]  projD / test
[info]  projD / Compile / console
[info] Delegates:
[info]  projD / Compile / console / scalacOptions
[info]  projD / Compile / scalacOptions
[info]  projD / console / scalacOptions
[info]  projD / scalacOptions
[info]  ThisBuild / Compile / console / scalacOptions
[info]  ThisBuild / Compile / scalacOptions
[info]  ThisBuild / console / scalacOptions
[info]  ThisBuild / scalacOptions
[info]  Zero / Compile / console / scalacOptions
[info]  Zero / Compile / scalacOptions
[info]  Zero / console / scalacOptions
[info]  Global / scalacOptions
```

Note how "Provided by" shows that `projD / Compile / console / scalacOptions`
is provided by `projD / Compile / scalacOptions`.
Also under "Delegates", *all* of the possible delegate candidates
listed in the order of precedence!

- All the scopes with `projD` scoping on the subproject axis are listed first,
  then `ThisBuild`, and `Zero`.
- Within a subproject, scopes with `Compile` scoping on the configuration axis
  are listed first, then falls back to `Zero`.
- Finally, the task axis scoping lists the given task scoping `console /` and the one without.

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

What will `projE / version` return?

1. `"2.12.2_0.1.0"`
2. `"2.11.11_0.1.0"`
3. something else?

The answer is `2.12.2_0.1.0`.
`projE / version` delegates to `ThisBuild / version`,
which depends on `ThisBuild / scalaVersion`.
Because of this reason, build level setting should be limited mostly to simple value assignments.

**Exercise F**: Given the following build definition:

```scala
ThisBuild / scalacOptions += "-D0"
scalacOptions += "-D1"

lazy val projF = (project in file("f"))
  .settings(
    compile / scalacOptions += "-D2",
    Compile / scalacOptions += "-D3",
    Compile / compile / scalacOptions += "-D4",
    test := {
      println("bippy" + (Compile / compile / scalacOptions).value.mkString)
    }
  )
```

What will `projF / test` show?

1. `"bippy-D4"`
2. `"bippy-D2-D4"`
3. `"bippy-D0-D3-D4"`
4. something else?

The answer is `"bippy-D0-D3-D4"`. This is a variation of an exercise
originally created by [Paul Phillips](https://gist.github.com/paulp/923154ab2d61882195cdea47483592ca).

It's a great demonstration of all the rules because `someKey += "x"` expands to

```scala
someKey := {
  val old = someKey.value
  old :+ "x"
}
```

Retrieving the old value would cause delegation, and due to Rule 5,
it will go to another scoped key.
Let's get rid of `+=` first, and annotate the delegates for old values:

```scala
ThisBuild / scalacOptions := {
  // Global / scalacOptions <- Rule 4
  val old = (ThisBuild / scalacOptions).value
  old :+ "-D0"
}

scalacOptions := {
  // ThisBuild / scalacOptions <- Rule 4
  val old = scalacOptions.value
  old :+ "-D1"
}

lazy val projF = (project in file("f"))
  .settings(
    compile / scalacOptions := {
      // ThisBuild / scalacOptions <- Rules 2 and 4
      val old = (compile / scalacOptions).value
      old :+ "-D2"
    },
    Compile / scalacOptions := {
      // ThisBuild / scalacOptions <- Rules 3 and 4
      val old = (Compile / scalacOptions).value
      old :+ "-D3"
    },
    Compile / compile / scalacOptions := {
      // projF / Compile / scalacOptions <- Rules 1 and 2
      val old = (Compile / compile / scalacOptions).value
      old :+ "-D4"
    },
    test := {
      println("bippy" + (Compile / compile / scalacOptions).value.mkString)
    }
  )
```

This becomes:

```scala
ThisBuild / scalacOptions := {
  Nil :+ "-D0"
}

scalacOptions := {
  List("-D0") :+ "-D1"
}

lazy val projF = (project in file("f"))
  .settings(
    compile / scalacOptions := List("-D0") :+ "-D2",
    Compile / scalacOptions := List("-D0") :+ "-D3",
    Compile / compile / scalacOptions := List("-D0", "-D3") :+ "-D4",
    test := {
      println("bippy" + (Compile / compile / scalacOptions).value.mkString)
    }
  )
```

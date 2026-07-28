<a name="multiple-scopes"></a>

### Getting values from multiple scopes

#### Introduction

The general form of an expression that gets values from multiple scopes
is:

```scala
<setting-or-task>.all(<scope-filter>).value
```

**NOTE!** Make sure to assign the `ScopeFilter` as a `val`! This is an
implementation detail requirement of the `.all` macro.

The `all` method is implicitly added to tasks and settings. It accepts a
`ScopeFilter` that will select the `Scopes`. The result has type
`Seq[T]`, where `T` is the key's underlying type.

#### Example

A common scenario is getting the sources for all subprojects for
processing all at once, such as passing them to scaladoc. The task that
we want to obtain values for is `sources` and we want to get the values
in all non-root projects and in the `Compile` configuration. This looks
like:

```scala
lazy val core = project

lazy val util = project

val filter = ScopeFilter( inProjects(core, util), inConfigurations(Compile) )

lazy val root = project.settings(
   sources := {
      // each sources definition is of type Seq[File],
      //   giving us a Seq[Seq[File]] that we then flatten to Seq[File]
      val allSources: Seq[Seq[File]] = sources.all(filter).value
      allSources.flatten
   }
)
```

The next section describes various ways to construct a ScopeFilter.

#### ScopeFilter

A basic `ScopeFilter` is constructed by the `ScopeFilter.apply` method.
This method makes a `ScopeFilter` from filters on the parts of a
`Scope`: a `ProjectFilter`, `ConfigurationFilter`, and `TaskFilter`. The
simplest case is explicitly specifying the values for the parts:

```scala
val filter: ScopeFilter =
   ScopeFilter(
      inProjects( core, util ),
      inConfigurations( Compile, Test )
   )
```

##### Unspecified filters

If the task filter is not specified, as in the example above, the
default is to select scopes without a specific task (global). Similarly,
an unspecified configuration filter will select scopes in the global
configuration. The project filter should usually be explicit, but if
left unspecified, the current project context will be used.

##### More on filter construction

The example showed the basic methods `inProjects` and
`inConfigurations`. This section describes all methods for constructing
a `ProjectFilter`, `ConfigurationFilter`, or `TaskFilter`. These methods
can be organized into four groups:

- Explicit member list (`inProjects`, `inConfigurations`, `inTasks`)
- Global value (`inGlobalProject`, `inGlobalConfiguration`,
  `inGlobalTask`)
- Default filter (`inAnyProject`, `inAnyConfiguration`, `inAnyTask`)
- Project relationships (`inAggregates`, `inDependencies`)

See the [API documentation](../api/sbt/ScopeFilter$$Make.html) for
details.

##### Combining ScopeFilters

`ScopeFilters` may be combined with the `&&`, `||`, `--`, and `-`
methods:

- `a && b` Selects scopes that match both a and b
- `a || b` Selects scopes that match either a or b
- `a -- b` Selects scopes that match a but not b
- `-b` Selects scopes that do not match b

For example, the following selects the scope for the `Compile` and
`Test` configurations of the `core` project and the global configuration
of the `util` project:

```scala
val filter: ScopeFilter =
   ScopeFilter( inProjects(core), inConfigurations(Compile, Test)) ||
   ScopeFilter( inProjects(util), inGlobalConfiguration )
```

#### More operations

The `all` method applies to both settings (values of type
`Initialize[T]`) and tasks (values of type `Initialize[Task[T]]`). It
returns a setting or task that provides a `Seq[T]`, as shown in this
table:

<table class="table table-striped">
  <tr>
    <th>Target</th>
    <th>Result</th>
  </tr>

  <tr>
    <td><tt>Initialize[T] </tt></td>
    <td><tt>Initialize[Seq[T]]</tt></td>
  </tr>

  <tr>
    <td><tt>Initialize[Task[T]]</tt></td>
    <td><tt>Initialize[Task[Seq[T]]]</tt></td>
  </tr>
</table>

This means that the `all` method can be combined with methods that
construct tasks and settings.

##### Missing values

Some scopes might not define a setting or task. The `?` and `??` methods
can help in this case. They are both defined on settings and tasks and
indicate what to do when a key is undefined.

<table class="table table-striped">
  <tr>
    <td><tt>?</tt></td>
    <td><tt>On a setting or task with underlying type T, this accepts no
    arguments and returns a setting or task (respectively) of type
    Option[T]. The result is None if the setting/task is undefined and
    Some[T] with the value if it is.</tt></td>
  </tr>

  <tr>
    <td><tt>??</tt></td>
    <td><tt>On a setting or task with underlying type T, this accepts an
    argument of type T and uses this argument if the setting/task is
    undefined.</tt></td>
  </tr>
</table>

The following contrived example sets the maximum errors to be the
maximum of all aggregates of the current project.

```scala
// select the transitive aggregates for this project, but not the project itself
val filter: ScopeFilter =
   ScopeFilter( inAggregates(ThisProject, includeRoot=false) )

maxErrors := {
   // get the configured maximum errors in each selected scope,
   // using 0 if not defined in a scope
   val allVersions: Seq[Int] =
      (maxErrors ?? 0).all(filter).value
   allVersions.max
}
```

##### Multiple values from multiple scopes

The target of `all` is any task or setting, including anonymous ones.
This means it is possible to get multiple values at once without
defining a new task or setting in each scope. A common use case is to
pair each value obtained with the project, configuration, or full scope
it came from.

- `resolvedScoped`: Provides the full enclosing ScopedKey (which is a Scope +
  `AttributeKey[_]`)
- `thisProject`: Provides the Project associated with this scope (undefined at the
  global and build levels)
- `thisProjectRef`: Provides the ProjectRef for the context (undefined at the global and
  build levels)
- `configuration`: Provides the Configuration for the context (undefined for the global
  configuration)

For example, the following defines a task that prints non-Compile
configurations that define sbt plugins. This might be used to identify
an incorrectly configured build (or not, since this is a fairly
contrived example):

```scala
// Select all configurations in the current project except for Compile
lazy val filter: ScopeFilter = ScopeFilter(
   inProjects(ThisProject),
   inAnyConfiguration -- inConfigurations(Compile)
)

// Define a task that provides the name of the current configuration
//   and the set of sbt plugins defined in the configuration
lazy val pluginsWithConfig: Initialize[Task[ (String, Set[String]) ]] =
   Def.task {
      ( configuration.value.name, definedSbtPlugins.value )
   }

checkPluginsTask := {
   val oddPlugins: Seq[(String, Set[String])] =
      pluginsWithConfig.all(filter).value
   // Print each configuration that defines sbt plugins
   for( (config, plugins) <- oddPlugins if plugins.nonEmpty )
      println(s"\$config defines sbt plugins: \${plugins.mkString(", ")}")
}
```

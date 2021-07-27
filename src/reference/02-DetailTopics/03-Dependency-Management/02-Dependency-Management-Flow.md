---
out: Dependency-Management-Flow.html
---

  [Library-Dependencies]: Library-Dependencies.html
  [TTL]: https://get-coursier.io/docs/ttl

Dependency Management Flow
--------------------------

There's a
[getting started page][Library-Dependencies] about
library management, which you may want to read first.

This page explains the relationship between the `compile` task
and library dependency management.

#### Background

`update` resolves dependencies according to the settings in a build
file, such as `libraryDependencies` and `resolvers`. Other tasks use the
output of `update` (an `UpdateReport`) to form various classpaths. Tasks
that in turn use these classpaths, such as `compile` or `run`, thus
indirectly depend on `update`. This means that before `compile` can run,
the `update` task needs to run. However, resolving dependencies on every
`compile` would be unnecessarily slow and so `update` must be particular
about when it actually performs a resolution.

In addition, sbt 1.x introduced the notion of Library Management API (LM API),
which abstracted the notion of library management.
As of sbt 1.3.0, there are two implementations for the LM API:
one based on Coursier, and the other based on Apache Ivy.

#### Caching and Configuration

1.  If no library dependency settings have changed since
    the last successful resolution and the retrieved files are
    still present, sbt does not ask dependency resolver (like Coursier)
    to perform resolution.
2.  Changing the settings, such as adding or removing dependencies
    or changing the version or other attributes of a dependency, will
    automatically cause resolution to be performed.
3.  Directly running the `update` task (as opposed to a task that
    depends on it) will force resolution to run, whether or not
    configuration changed.
4.  Clearing the task cache by running `clean` will also cause
    resolution to be performed.
5.  Overriding all of the above, `update / skip := true` will tell sbt
    to never perform resolution. Note that this can cause dependent
    tasks to fail.

#### Notes on SNAPSHOTs

Repeatability of the build is paramount, especially when you share
the build with someone else.
`SNAPSHOT` versions are convenient way of locally testing something,
but its use should be limited only to the local machine
because it introduces mutability to the build, which makes it brittle,
and the dependency resolution slower as the publish date must be
checked over the network even when the artifacts are locally cached.

By default, `SNAPSHOT` artifacts in Coursier are given [24h time-to-live][TTL] (TTL) to avoid network IO. If you need to force re-resolution of `SNAPSHOTS`,
run sbt with `COURSIER_TTL` environment variable set to `0s`.

---
out: Cached-Resolution.html
---

  [1711]: https://github.com/sbt/sbt/issues/1711
  [Library-Management]: Library-Management.html

Cached Resolution
-----------------

Cached Resolution is an **experimental** feature of sbt added since 0.13.7 to address the scalability performance of dependency resolution.

### Setup

To set up Cached Resolution include the following setting in your project's build:

```scala
updateOptions := updateOptions.value.withCachedResolution(true)
```

### Dependency as a graph

A project declares its own library dependency using `libraryDependencies` setting. The libraries you added also bring in their transitive dependencies. For example, your project may depend on dispatch-core 0.11.2; dispatch-core 0.11.2 depends on async-http-client 1.8.10; async-http-client 1.8.10 depends on netty 3.9.2.Final, and so forth. If we think of each library to be a node with arrows going out to dependent nodes, we can think of the entire dependencies to be a graph -- specifically a [directed acyclic graph](https://en.wikipedia.org/wiki/Directed_acyclic_graph).

This graph-like structure, which was adopted from Apache Ivy, allows us to define [override rules and exclusions][Library-Management] transitively, but as the number of the node increases, the time it takes to resolve dependencies grows significantly. See [Motivation](#motivation) section later in this page for the full description.

### Cached Resolution

The Cached Resolution feature is akin to incremental compilation, which only recompiles the sources that have been changed since the last `compile`. Unlike the Scala compiler, Ivy does not have the concept of separate compilation, so that needed to be implemented.

Instead of resolving the full dependency graph, the Cached Resolution feature creates  minigraphs -- one for each direct dependency appearing in all related subprojects. These minigraphs are resolved using Ivy's resolution engine, and the result is stored locally under `$global_base$/dependency/` (or what's specified by `sbt.dependency.base` flag) shared across all builds. After all minigraphs are resolved, they are stitched together by applying the conflict resolution algorithm (typically picking the latest version).

When you add a new library to your project, Cached Resolution feature will check for the minigraph files under `$global_base$/dependency/` and load the previously resolved nodes, which incurs negligible I/O overhead, and only resolve the newly added library. The intended performance improvement is that the second and third subprojects can take advantage of the resolved minigraphs from the first one and avoid duplicated work. The following figure illustrates projects A, B, and C, all hitting the same set of json files.

<br>
![fig1](files/cached-resolution.png)

The actual speedup will vary case by case, but you should see significant speedup if you have many subprojects. An initial report from a user showed a change from 260s to 25s. Your mileage may vary.

### Caveats and known issues

Cached Resolution is an **experimental** feature, and you might run into some issues. When you see them please report to GitHub Issue or sbt-dev list.

#### First runs

The first time you run, Cached Resolution will likely be slow since it needs to resolve all minigraphs and save the result into the filesystem. Whenever you add a new node the system has not seen, it will save the minigraph. The second run onwards should be faster, but comparing full-resolution `update` with second run onwards might not be a fair comparison.

#### Ivy fidelity is not guaranteed

Some of the Ivy behavior doesn't make sense, especially around Maven emulation. For example, it seems to treat all transitive dependencies introduced by Maven-published library as `force()` even when the original `pom.xml` doesn't say to:

```
\$ cat ~/.ivy2/cache/com.ning/async-http-client/ivy-1.8.10.xml | grep netty
    <dependency org="io.netty" name="netty" rev="3.9.2.Final" force="true" conf="compile->compile(*),master(*);runtime->runtime(*)"/>
```

There are also some issues around multiple dependencies to the same library with different [Maven classifiers](https://maven.apache.org/pom.html#Maven_Coordinates). In these cases, reproducing the exact result as normal `update` may not make sense or is downright impossible.

#### SNAPSHOT and dynamic dependencies

When a minigraph contains either a SNAPSHOT or dynamic dependency, the graph is considered dynamic, and it will be invalidated after a single task execution.
Therefore, if you have any SNAPSHOT in your graph, your experience may degrade.
(This could be improved in the future)

A setting key called `updateOptions` customizes the details of
managed dependency resolution with the `update` task. One of its flags is
called `latestSnapshots`, which controls the behavior of the chained
resolver. Up until 0.13.6, sbt was picking the first `-SNAPSHOT`
revision it found along the chain.  When `latestSnapshots` is enabled
(default: `true`), it will look into all resolvers on the chain, and
compare them using the publish date.

The tradeoff is probably a longer resolution time if you have many
remote repositories on the build or you live away from the severs. So
here's how to disable it:

```scala
    updateOptions := updateOptions.value.withLatestSnapshots(false)
```

<a name="motivation"></a>

### Motivation

sbt internally uses Apache Ivy to resolve library dependencies. While sbt has benefited from not having to reinvent its own dependency resolution engine all these years, we are increasingly seeing scalability challenges especially for projects with both multiple subprojects and large dependency graph. There are several factors involved in sbt's resolution scalability:

- Number of transitive nodes (libraries) in the graph
- Exclusion and override rules
- Number of subprojects
- Configurations
- Number of repositories and their availability
- Classifiers (additional sources and docs used by IDE)

Of the above factors, the one that has the most impact is the number of transitive nodes.

1. The more nodes there are the greater the chance of version conflicts. Conflicts are resolved typically by picking the latest version within the same library.
2. The more nodes there are, the more it needs to backtrack to check for exclusion and override rules.

Exclusion and override rules are applied transitively, so any time a new node is introduced to the graph it needs to check its parent node's rules, its grandparent node's rules, great-grandparent node's rules, etc.

sbt treats configurations and subprojects to be independent dependency graph. This allows us to include arbitrary libraries for different configurations and subprojects, but if the dependency resolution is slow, the linear scaling starts to hurt. There have been prior efforts to cache the result of library dependencies, but it still resulted in full resolution when `libraryDependencies` has changed.

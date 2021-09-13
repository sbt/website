---
out: Remote-Caching.html
---

Remote Caching
--------------

sbt 1.4.0 / Zinc 1.4.0 virtualizes the file paths tracked during incremental compilation, and uses content hash for change detection. With these combination, we can realize repeatable build, also known as _build as function_.

This enables **experimental** remote caching (cached compilation) feature. The idea is for a team of developers and/or a continuous integration (CI) system to share build outputs. If the build is repeatable, the output from one machine can be reused by another machine, which can make the build significantly faster.

#### Usage

```scala
ThisBuild / pushRemoteCacheTo := Some(MavenCache("local-cache", file("/tmp/remote-cache")))
```

Then from machine 1, call `pushRemoteCache`. This will publish the `*.class` and Zinc Analysis artifacts to the location. Next, from machine 2, call `pullRemoteCache`.

#### Remote caching via Maven repository

As of sbt 1.4.0, we're reusing the Maven publishing and resolution mechanism to exchange the cached build outputs. This is likely to easy to get started using existing infrastructure such as Bintray.

In the future, we might consider simpler cache server like plain HTTP server that uses `PUT` and `GET`. This would require someone to host an HTTP server somewhere, but provisioning them might become simpler.


#### ThisBuild / rootPaths

To abstract machine-specific paths such as your working directory and Coursier cache directory, sbt keeps a map of root paths in `ThisBuild / rootPaths`. If your build adds special paths for your source or output directory, add them to `ThisBuild / rootPaths`.

If you need to guarantee that `ThisBuild / rootPaths` contains all necessary paths you can set  `ThisBuild / allowMachinePath` to `false`.


#### remoteCacheId

As of sbt 1.4.2, `remoteCacheId` uses hash of content hashes for input sources.

---
out: Bintray-For-Plugins.html
---

  [UsingSonatype]: Using-Sonatype.html

Bintray For Plugins
-------------------

**We no longer use Bintray to host plugins.**

First and foremost, we would like to thank JFrog for their continued support of sbt project and the Scala ecosystem.
Between 2014 and April, 2021 sbt hosted its community plugin repository on
[bintray.com/sbt](https://bintray.com/sbt).

When JFrog sunsetted their Bintray product, they have proactively contacted us
and granted Scala Center open source sponsorship that allows us to use an online Artifactory instance.

As of 2021-04-18, we have migrated all sbt plugins and sbt 0.13 artifacts to the Artifactory instance,
and redirected <https://repo.scala-sbt.org/scalasbt/> to point to it as well,
so existing builds should continue to work without making any changes today and after May 1st.
For plugin hosting, we will operate this as a read-only repository.
Any new plugin releases should migrate to using [Sonatype OSS][UsingSonatype].

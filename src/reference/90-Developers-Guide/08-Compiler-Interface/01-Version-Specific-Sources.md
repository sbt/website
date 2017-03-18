---
out: Version-Specific-Sources.html
---

Fetching the most specific sources
----------------------------------

Because the compiler interface is recompiled against each Scala version
in use in your project, its source must stay compatible with all the Scala
versions that sbt supports (from Scala 2.8 to the latest version of Scala).

This comes at great cost for both the sbt maintainers and the Scala
compiler authors:

1. The compiler authors cannot remove old and deprecated public APIs from
   the Scala compiler.
1. sbt cannot use new APIs defined in the Scala compiler.
1. sbt must implement [all kinds of hackery](https://github.com/sbt/sbt/blob/0.13/compile/interface/src/main/scala/xsbt/Compat.scala#L6)
   to remain source-compatible with all versions of the Scala compiler and support
   new features.

To circumvent this problem, a new mechanism that allows sbt to fetch the
version of the sources for the compiler interface that are the most specific
for the Scala version in use has been implemented in sbt.

For instance, for a project that is compiled using Scala 2.11.8-M2, sbt
will look for the following version of the sources for the compiler interface,
in this order:

1. 2.11.8-M2
1. 2.11.8
1. 2.11
1. The default sources.

This new mechanism allows both the Scala compiler and sbt to move forward and
enjoy new APIs while being certain than users of older versions of Scala will
still be able to use sbt.

Finally, another advantage of this technique is that it relies on Ivy to
retrieve the sources of the compiler bridge, but can be easily ported for use
with Maven, which is the distribution mechanism that the sbt maintainers would
like to use to distribute sbt's modules.

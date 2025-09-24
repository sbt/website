Source dependency plugin
========================

~~~admonish note
The recipe section of the documentation focuses on the objectives
with minimal explanations.
~~~


Objective
---------

I want to use a plugin hosted on a git repository, without publishing to the Central Repo.

Steps
-----

1. Host an sbt 2.x plugin on a git repository, built using sbt 2.x.
2. Add the following to `project/plugins.sbt`:
   ```scala
   // In project/plugins.sbt
   lazy val jmhRef = ProjectRef(
     uri("https://github.com/eed3si9n/sbt-jmh.git#303c3e98e1d1523e6a4f99abe09c900165028edb"),
     "plugin")
   BareBuildSyntax.dependsOn(jmhRef)
   ```
3. When you start sbt, it will automatically clone the repository under `$HOME/.sbt/2/staging/`.

In the above, `https://github.com/eed3si9n/sbt-jmh.git` is the HTTP endpoint for a plugin hosted on GitHub, and `303c3e98e1d1523e6a4f99abe09c900165028edb` is a commit id on the default branch.

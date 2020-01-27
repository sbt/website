---
out: Advanced-Configurations-Example.html
---

  [Basic-Def]: Basic-Def.html

Advanced configurations example
-------------------------------

This is an example [.sbt build definition][Basic-Def]
that demonstrates using configurations to group dependencies.

The `utils` module provides utilities for other modules. It uses
configurations to group dependencies so that a dependent project doesn't
have to pull in all dependencies if it only uses a subset of
functionality. This can be an alternative to having multiple utilities
modules (and consequently, multiple utilities jars).

In this example, consider a `utils` project that provides utilities
related to both Scalate and Saxon. It therefore needs both Scalate and
Saxon on the compilation classpath and a project that uses all of the
functionality of 'utils' will need these dependencies as well. However,
project `a` only needs the utilities related to Scalate, so it doesn't
need Saxon. By depending only on the `scalate` configuration of `utils`,
it only gets the Scalate-related dependencies.

@@snip [custom-config]($root$/src/sbt-test/ref/example-custom-config/build.sbt) {}

---
out: Nightly-Builds.html
---

  [Manual-Installation]: Manual-Installation.html

Nightly Builds
--------------

The latest development versions of $app_version$ are available as nightly
builds on [Typesafe Snapshots]($typesafe_ivy_snapshots$).

To use a nightly build, the instructions are the same for
[normal manual setup][Manual-Installation] except:

1.  Download the launcher jar from one of the subdirectories of
    |nightly-launcher|. They should be listed in chronological order, so
    the most recent one will be last.
2.  The version number is the name of the subdirectory and is of the
    form `$app_version$.x-yyyyMMdd-HHmmss`. Use this in a build.properties
    file.
3.  Call your script something like `sbt-nightly` to retain access to a
    stable sbt launcher. The documentation will refer to the script as
    sbt, however.

Related to the third point, remember that an `sbt.version` setting in
`<build-base>/project/build.properties` determines the version of sbt to
use in a project. If it is not present, the default version associated
with the launcher is used. This means that you must set
`sbt.version=yyyyMMdd-HHmmss` in an existing
`<build-base>/project/build.properties`. You can verify the right
version of sbt is being used to build a project by running `about`.

To reduce problems, it is recommended to not use a launcher jar for one
nightly version to launch a different nightly version of sbt.

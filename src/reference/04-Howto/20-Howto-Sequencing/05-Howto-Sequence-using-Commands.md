---
out: Howto-Sequence-using-Commands.html
---

### How to sequence using commands

If all you care about is the side effects, and you really just want to emulate humans typing in one command after another, a custom command might be just want you need. This comes in handy for release procedures.

Here's from the build script of sbt itself:

```scala
  commands += Command.command("releaseNightly") { state =>
    "stampVersion" ::
      "clean" ::
      "compile" ::
      "publish" ::
      "bintrayRelease" ::
      state
  }
```

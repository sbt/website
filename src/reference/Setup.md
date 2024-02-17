Installing sbt runner
=====================

To build an sbt project, you'll need to take these steps:

- Install JDK (We recommend Eclipse Adoptium Temurin JDK 8, 11, or 17, or Zulu JDK 8 for macOS with ARM chips).
- Install sbt runner.

sbt runner is a script that invokes a declared version of sbt, downloading it beforehand if necessary. This allows build authors to precisely control
the sbt version, instead of relying on users' machine environment.

### Prerequisites

sbt runs on all major operating systems; however, it requires JDK 8 or higher to run.

```bash
java -version
# openjdk version "1.8.0_352"
```

### Install sbt with **cs setup**

Follow [Install](https://www.scala-lang.org/download/) page, and install Scala using Coursier.

```bash
cs setup
```

This should install the latest stable version of `sbt`.

### Installing from SDKMAN

To install both JDK and sbt, consider using [SDKMAN](https://sdkman.io/).

```bash
sdk install java $(sdk list java | grep -o "\b8\.[0-9]*\.[0-9]*\-tem" | head -1)
sdk install sbt
```

### Universal packages

- [sbt-{{sbt_runner_version}}.zip][ZIP]
- [sbt-{{sbt_runner_version}}.tgz][TGZ]
- [sbt-{{sbt_runner_version}}.msi][MSI]

Verify the sbt runner
---------------------

```bash
sbt --script-version
# {{sbt_runner_version}}
```

  [MSI]: https://github.com/sbt/sbt/releases/download/v{{sbt_runner_version}}/sbt-{{sbt_runner_version}}.msi
  [ZIP]: https://github.com/sbt/sbt/releases/download/v{{sbt_runner_version}}/sbt-{{sbt_runner_version}}.zip
  [TGZ]: https://github.com/sbt/sbt/releases/download/v{{sbt_runner_version}}/sbt-{{sbt_runner_version}}.tgz

Installing sbt runner
=====================

To build an sbt project, you'll need to take these steps:

- Install JDK (We recommend Eclipse Adoptium Temurin JDK 17).
- Install sbt runner.

sbt runner is a script that invokes a declared version of sbt, downloading it beforehand if necessary. This allows build authors to precisely control
the sbt version, instead of relying on users' machine environment.

### Prerequisites

sbt runs on all major operating systems; however, sbt 2.x requires JDK 17 or higher to run.

```bash
java --version
# openjdk 17.0.12 2024-07-16 LTS
```

### Installing from SDKMAN

To install both JDK and sbt, consider using [SDKMAN](https://sdkman.io/).

```bash
sdk install java $(sdk list java | grep -o "\b17\.[0-9]*\.[0-9]*\-zulu" | head -1)
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

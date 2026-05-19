
  [GA]: https://docs.github.com/en/actions
  [GA-Reference]: https://docs.github.com/en/free-pro-team@latest/actions/reference
  [Setup-Java]: https://github.com/actions/setup-java
  [GA-Matrix]: https://docs.github.com/en/free-pro-team@latest/actions/reference/workflow-syntax-for-github-actions#jobsjob_idstrategy
  [sbt-github-actions]: https://github.com/sbt/sbt-github-actions

GitHub Actions setup
====================

~~~admonish note
The recipe section of the documentation focuses on the objectives
with minimal explanations.

See also [GitHub Actions documentation](https://docs.github.com/en/actions) for general concepts around GitHub Actions.
~~~

Objective
---------

I want to build and test my project on GitHub Actions.

### Basic setup

Setting up your build for GitHub Actions is mostly about setting up `.github/workflows/ci.yml`. Here's what a minimal CI workflow could look like using [setup-java][Setup-Java]:

```yml
name: CI
on:
  pull_request:
  push:
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - name: Checkout
      uses: actions/checkout@v6
    - name: Setup JDK
      uses: actions/setup-java@v5
      with:
        distribution: temurin
        java-version: 17
        cache: sbt
    - name: Setup sbt
      uses: sbt/setup-sbt@v1
    - name: Build and Test
      run: sbt -v test
```

### Opting out of disk cache

By default setup-sbt enables the disk cache on sbt 2.x. This can be opted out:

```yml
- uses: sbt/setup-sbt@v1
  with:
    disk-cache: false
```

### Build matrix

Here's a sample that uses build matrix:

```yml
name: CI
on:
  pull_request:
  push:
jobs:
  test:
    strategy:
      fail-fast: false
      matrix:
        include:
          - os: ubuntu-latest
            java: 17
            distribution: temurin
            jobtype: 1
          - os: ubuntu-latest
            java: 17
            distribution: temurin
            jobtype: 2
          - os: windows-latest
            java: 17
            distribution: temurin
            jobtype: 2
          - os: ubuntu-latest
            java: 17
            distribution: temurin
            jobtype: 3
    runs-on: ${{curly_open}} matrix.os {{curly_close}}}
    env:
      JAVA_OPTS: -Xms2048M -Xmx2048M -Xss6M -Dfile.encoding=UTF-8
    steps:
    - name: Checkout
      uses: actions/checkout@v6
    - name: Setup JDK
      uses: actions/setup-java@v5
      with:
        distribution: ${{curly_open}} matrix.distribution {{curly_close}}}
        java-version: ${{curly_open}} matrix.java {{curly_close}}}
        cache: sbt
    - name: Setup sbt
      uses: sbt/setup-sbt@v1
    - name: Build and test (1)
      if: ${{curly_open}} matrix.jobtype == 1 {{curly_close}}}
      shell: bash
      run: |
        sbt -v "mimaReportBinaryIssues; scalafmtCheckAll; test;"
    - name: Build and test (2)
      if: ${{curly_open}} matrix.jobtype == 2 {{curly_close}}}
      shell: bash
      run: |
        sbt -v "scripted actions/*"
    - name: Build and test (3)
      if: ${{curly_open}} matrix.jobtype == 3 {{curly_close}}}
      shell: bash
      run: |
        sbt -v "scripted dependency-management/*"
```

### sbt-github-actions

There's also [sbt-github-actions][sbt-github-actions], an sbt plugin by Daniel Spiewak that can generate the workflow files, and keep the settings in `build.sbt` file.

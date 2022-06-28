---
out: GitHub-Actions-with-sbt.html
---

  [GA]: https://docs.github.com/en/free-pro-team@latest/actions
  [GA-Reference]: https://docs.github.com/en/free-pro-team@latest/actions/reference
  [Setup-Java]: https://github.com/actions/setup-java
  [GA-Matrix]: https://docs.github.com/en/free-pro-team@latest/actions/reference/workflow-syntax-for-github-actions#jobsjob_idstrategy
  [sbt-github-actions]: https://github.com/djspiewak/sbt-github-actions

Setting up GitHub Actions with sbt
----------------------------------

[GitHub Actions][GA] is a workflow system by GitHub that supports continuous integration (CI) and continuous deployment (CD). As CI/CD feature was introduced in [2019](https://github.blog/2019-08-08-github-actions-now-supports-ci-cd/), it's a newcomer in the CI/CD field, but it quickly rised to the de-facto standard CI solution for open source Scala projects.


### Set `project/build.properties`

Continuous integration is a great way of checking that your code works outside of your machine.
If you haven't created one already, make sure to create `project/build.properties` and explicitly set the
`sbt.version` number:

```yml
sbt.version=$app_version$
```

Your build will now use $app_version$.

### Read the GitHub Actions manual

A treasure trove of Github Actions tricks can be found in the Github Actions [official documentation][GA], including the [Reference][GA-Reference].
Use this guide as an inspiration, but consult the official source for more details.

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
      uses: actions/checkout@v2
    - name: Setup JDK
      uses: actions/setup-java@v3
      with:
        distribution: temurin
        java-version: 8
    - name: Build and Test
      run: sbt -v +test
```

### Custom JVM options

The default JVM options are provided by the official sbt runner adopted by [setup-java][Setup-Java], and it should work for most cases. If you do decide to customize it, use `-v` option to let the script output the current options first:

```
# Executing command line:
java
-Dfile.encoding=UTF-8
-Xms1024m
-Xmx1024m
-Xss4M
-XX:ReservedCodeCacheSize=128m
-jar
/usr/share/sbt/bin/sbt-launch.jar
```

We can define `JAVA_OPTS` and `JVM_OPTS` environment variables to override this.

```yml
name: CI
on:
  pull_request:
  push:
jobs:
  test:
    runs-on: ubuntu-latest
    env:
      # define Java options for both official sbt and sbt-extras
      JAVA_OPTS: -Xms2048M -Xmx2048M -Xss6M -XX:ReservedCodeCacheSize=256M -Dfile.encoding=UTF-8
      JVM_OPTS:  -Xms2048M -Xmx2048M -Xss6M -XX:ReservedCodeCacheSize=256M -Dfile.encoding=UTF-8
    steps:
    - name: Checkout
      uses: actions/checkout@v2
    - name: Setup JDK
      uses: actions/setup-java@v3
      with:
        distribution: temurin
        java-version: 8
    - name: Build and Test
      run: sbt -v +test
```

Again, let's check the log to see if the flags are taking effect:

```
# Executing command line:
[process_args] java_version = '8'
java
-Xms2048M
-Xmx2048M
-Xss6M
-XX:ReservedCodeCacheSize=256M
-Dfile.encoding=UTF-8
-jar
/usr/share/sbt/bin/sbt-launch.jar
+test
```

### Caching

You can speed up your `sbt` builds on GitHub Actions by caching various artifacts in-between the jobs.

The action `setup-java` has built-in support for caching artifacts downloaded by
sbt when loading the build or when building the project.

To use it, set the input parameter `cache` of the action `setup-java` to the value `"sbt"`:

```yml
    - name: Setup JDK
      uses: actions/setup-java@v3
      with:
        distribution: temurin
        java-version: 8
        cache: sbt
    - name: Build and test
      run: sbt -v +test
```

Note the added line `cache: sbt`.

Overall, the use of caching should shave off a few minutes of build time per job.

### Build matrix

When creating a continous integration job, it's fairly common to split up the task into multiple jobs that runs in parallel. For example, we could:

- Run identical tests on JDK 8, JDK 11, Linux, macOS, and Windows
- Run different subset of tests on the same JDK, OS, and other setups

Both use cases are possible using [the build matrix][GA-Matrix]. The point here is that we would like to mostly reuse the steps except for a few variance. For tasks that do not overlap in steps (like testing vs deployment), it might be better to just create a different job or a new workflow.

Here's an example of forming a build matrix using JDK version and operating system.

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
            java: 8
          - os: ubuntu-latest
            java: 17
          - os: windows-latest
            java: 17
    runs-on: \${{ matrix.os }}
    steps:
    - name: Checkout
      uses: actions/checkout@v2
    - name: Setup JDK
      uses: actions/setup-java@v3
      with:
        distribution: temurin
        java-version: \${{ matrix.java }}
    - name: Build and test
      shell: bash
      run: sbt -v +test
```

Note that there's nothing magical about the `os` or `java` keys in the build matrix.

> The keys you define become properties in the `matrix` context and you can reference the property in other areas of your workflow file.

You can create an arbitrary key to iterate over! We can use this and create a key named `jobtype` to split the work too.

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
            jobtype: 1
          - os: ubuntu-latest
            java: 17
            jobtype: 2
          - os: ubuntu-latest
            java: 17
            jobtype: 3
    runs-on: \${{ matrix.os }}
    steps:
    - name: Checkout
      uses: actions/checkout@v2
    - name: Setup JDK
      uses: actions/setup-java@v3
      with:
        distribution: temurin
        java-version: \${{ matrix.java }}
    - name: Build and test (1)
      if: \${{ matrix.jobtype == 1 }}
      shell: bash
      run: |
        sbt -v "mimaReportBinaryIssues; scalafmtCheckAll; +test;"
    - name: Build and test (2)
      if: \${{ matrix.jobtype == 2 }}
      shell: bash
      run: |
        sbt -v "scripted actions/*"
    - name: Build and test (3)
      if: \${{ matrix.jobtype == 3 }}
      shell: bash
      run: |
        sbt -v "dependency-management/*"
```

### Sample .github/workflows/ci.yml setting

Here's a sample that puts them all together. Remember, most of the sections are optional.

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
            jobtype: 1
          - os: ubuntu-latest
            java: 17
            jobtype: 2
          - os: windows-latest
            java: 17
            jobtype: 2
          - os: ubuntu-latest
            java: 17
            jobtype: 3
    runs-on: \${{ matrix.os }}
    env:
      # define Java options for both official sbt and sbt-extras
      JAVA_OPTS: -Xms2048M -Xmx2048M -Xss6M -XX:ReservedCodeCacheSize=256M -Dfile.encoding=UTF-8
      JVM_OPTS:  -Xms2048M -Xmx2048M -Xss6M -XX:ReservedCodeCacheSize=256M -Dfile.encoding=UTF-8
    steps:
    - name: Checkout
      uses: actions/checkout@v2
    - name: Setup JDK
      uses: actions/setup-java@v3
      with:
        distribution: temurin
        java-version: \${{ matrix.java }}
        cache: sbt
    - name: Build and test (1)
      if: \${{ matrix.jobtype == 1 }}
      shell: bash
      run: |
        sbt -v "mimaReportBinaryIssues; scalafmtCheckAll; +test;"
    - name: Build and test (2)
      if: \${{ matrix.jobtype == 2 }}
      shell: bash
      run: |
        sbt -v "scripted actions/*"
    - name: Build and test (3)
      if: \${{ matrix.jobtype == 3 }}
      shell: bash
      run: |
        sbt -v "dependency-management/*"
```

### sbt-github-actions

There's also [sbt-github-actions][sbt-github-actions], an sbt plugin by Daniel Spiewak that can generate the workflow files, and keep the settings in `build.sbt` file.

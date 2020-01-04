---
out: Travis-CI-with-sbt.html
---

  [Travis]: https://travis-ci.com/
  [Travis-org]: https://travis-ci.org/
  [Travis-Docs]: http://docs.travis-ci.com/
  [Travis-Scala]: http://docs.travis-ci.com/user/languages/scala/
  [Travis-build-configuration]: http://docs.travis-ci.com/user/build-configuration/
  [Travis-container]: http://docs.travis-ci.com/user/workers/container-based-infrastructure/
  [Travis-caching]: http://docs.travis-ci.com/user/caching/
  [Travis-speeding]: http://docs.travis-ci.com/user/speeding-up-the-build/
  [Travis-notifications]: http://docs.travis-ci.com/user/notifications/
  [Travis-encryption]: http://docs.travis-ci.com/user/encryption-keys/
  [Travis-database]: http://docs.travis-ci.com/user/database-setup/
  [Travis-installing]: http://docs.travis-ci.com/user/installing-dependencies/
  [Travis-deploy]: http://docs.travis-ci.com/user/deployment/
  [Travis-timeout]: http://docs.travis-ci.com/user/build-timeouts/
  [sbt-extras]: https://github.com/paulp/sbt-extras
  [cookbook-sbt]: https://github.com/travis-ci/travis-cookbooks/blob/master/cookbooks/travis_sbt_extras/templates/default/sbtopts.erb
  [cookbook-jvm]: https://github.com/travis-ci/travis-cookbooks/blob/master/cookbooks/travis_sbt_extras/templates/default/jvmopts.erb

Setting up Travis CI with sbt
-----------------------------

[Travis CI][Travis] is a hosted continuous integration service for open source and private projects. Many of the OSS projects hosted on GitHub uses [open source edition of Travis CI][Travis-org] to validate pushes and pull requests. We'll discuss some of the best practices setting up Travis CI.

### Set `project/build.properties`

Continuous integration is a great way of checking that your code works outside of your machine.
If you haven't created one already, make sure to create `project/build.properties` and explicitly set the
`sbt.version` number:

```yml
sbt.version=$app_version$
```

Your build will now use $app_version$.

### Read the Travis manual

A treasure trove of Travis tricks can be found in the Travis's [official documentation][Travis-Docs].
Use this guide as an inspiration, but consult the official source for more details.

### Basic setup

Setting up your build for Travis CI is mostly about setting up `.travis.yml`.
[Scala][Travis-Scala] page says the basic file can look like:

```yml
language: scala

jdk: openjdk8

scala:
   - 2.10.4
   - $example_scala_version$
```

By default Travis CI executes `sbt ++\$TRAVIS_SCALA_VERSION test`.
Let's specify that explicitly:

```yml
language: scala

jdk: openjdk8

scala:
   - 2.10.4
   - $example_scala_version$

script:
   - sbt ++\$TRAVIS_SCALA_VERSION test
```

More info on `script` section can be found in [Configuring your build][Travis-build-configuration].

As noted on the [Scala][Travis-Scala] page, Travis CI uses [paulp/sbt-extras][sbt-extras] as the `sbt` command.
This becomes relevant when you want to override JVM options, which we'll see later.

### Plugin build setup

For sbt plugins, there is no need for cross building on Scala, so the following is all you need:

```yml
language: scala

jdk: openjdk8

script:
   - sbt scripted
```

Another source of good information is to read the output by Travis CI itself to learn about how the virtual environment is set up.
For example, from the following output we learn that it is using `JVM_OPTS` environment variable to pass in the JVM options.

```
\$ export JVM_OPTS=@/etc/sbt/jvmopts
\$ export SBT_OPTS=@/etc/sbt/sbtopts
```

### Custom JVM options

The default [sbt][cookbook-sbt] and [JVM][cookbook-jvm] options are set by Travis CI people,
and it should work for most cases.
If you do decide to customize it, read what they currently use as the defaults first.
Because Travis is already using the environment variable `JVM_OPTS`, we can instead create a file `travis/jvmopts`:

```
-Dfile.encoding=UTF8
-Xms2048M
-Xmx2048M
-Xss6M
-XX:ReservedCodeCacheSize=256M
```

and then write out the `script` section with `-jvm-opts` option:

```
script:
   - sbt ++\$TRAVIS_SCALA_VERSION -jvm-opts travis/jvmopts test
```

After making the change, confirm on the Travis log to see if the flags are taking effect:

```
# Executing command line:
java
-Dfile.encoding=UTF8
-Xms2048M
-Xmx2048M
-Xss6M
-XX:ReservedCodeCacheSize=256M
-jar
/home/travis/.sbt/launchers/$app_version$/sbt-launch.jar
```

It seems to be working. One downside of setting all of the parameters is that we might be left behind when the environment updates and the default values gives us more memory in the future.

Here's how we can add just a few JVM options:

```
script:
   - sbt ++\$TRAVIS_SCALA_VERSION -Dfile.encoding=UTF8 -J-XX:ReservedCodeCacheSize=256M -J-Xms1024M test
```

sbt-extra script passes any arguments starting with either `-D` or `-J` directly to JVM.

Again, let's check the Travis log to see if the flags are taking effect:

```
# Executing command line:
java
-Xms2048M
-Xmx2048M
-Xss6M
-Dfile.encoding=UTF8
-XX:ReservedCodeCacheSize=256M
-Xms1024M
-jar
/home/travis/.sbt/launchers/$app_version$/sbt-launch.jar
```

**Note**: This duplicates the `-Xms` flag as intended, which might not the best thing to do.

### Caching

You can speed up your `sbt` builds on Travis CI by using their [caching][Travis-caching] feature.

Here's a sample `cache:` configuration that you can use:

```yml
cache:
  directories:
    - \$HOME/.cache/coursier
    - \$HOME/.ivy2/cache
    - \$HOME/.sbt
```

**Note**: Coursier uses different [cache location](https://get-coursier.io/docs/cache) depending on the OS, so the above needs to be changed accordingly for macOS or Windows images.

You'll also need the following snippet to avoid unnecessary cache updates:

```yml
before_cache:
  - rm -fv \$HOME/.ivy2/.sbt.ivy.lock
  - find \$HOME/.ivy2/cache -name "ivydata-*.properties" -print -delete
  - find \$HOME/.sbt        -name "*.lock"               -print -delete
```

With the above changes combined Travis CI will tar up the cached directories and uploads them to a cloud storage provider.
Overall, the use of caching should shave off a few minutes of build time per job.

### Build matrix

We've already seen the example of Scala cross building.

```yml
language: scala

jdk: openjdk8

scala:
   - 2.10.4
   - $example_scala_version$

script:
   - sbt ++\$TRAVIS_SCALA_VERSION test
```

We can also form a build matrix using environment variables:

```yml
env:
  global:
    - SOME_VAR="1"

  # This splits the build into two parts 
  matrix:
    - TEST_COMMAND="scripted sbt-assembly/*"
    - TEST_COMMAND="scripted merging/* caching/*"

script:
   - sbt "\$TEST_COMMAND"
```

Now two jobs will be created to build this sbt plugin, simultaneously running different integration tests.
This technique is described in [Parallelizing your builds across virtual machines][Travis-speeding].

### Notification

You can configure Travis CI to [notify you][Travis-notifications].

> By default, email notifications will be sent to the committer and the commit author, if they are members of the repository[...].
>
> And it will by default send emails when, on the given branch:
>
> - a build was just broken or still is broken
> - a previously broken build was just fixed

The default behavior looks reasonable, but if you want, we can override the `notifications` section to email you on successful builds too, or to use some other channel of communication like IRC.

```yml
# Email specific recipient all the time
notifications:
  email:
    recipients:
      - one@example.com
  on_success: always # default: change
```

This might also be a good time to read up on [encryption][Travis-encryption] using the command line `travis` tool.

```
\$ travis encrypt one@example.com
```

### Dealing with flaky network or tests

For builds that are more prone to flaky network or tests, Travis CI has created some tricks
described in the page [My builds is timing out][Travis-timeout].

Starting your command with `travis_retry` retries the command three times if the return code is non-zero.
With caching, hopefully the effect of flaky network is reduced, but it's an interesting one nonetheless.
Here are some cautionary words from the documentation:

> We recommend careful use of `travis_retry`, as overusing it can extend your build time when there could be a deeper underlying issue.

Another tidbit about Travis is the output timeout:

> Our builds have a global timeout and a timeout that's based on the output. If no output is received from a build for 10 minutes, it's assumed to have stalled for unknown reasons and is subsequently killed.

There's a function called `travis_wait` that can extend this to 20 minutes.

### More things

There are more thing you can do, such as [set up databases][Travis-database], [installing Ubuntu packages][Travis-installing], and [deploy continuously][Travis-deploy].

Travis offers the ability to run tests in parallel, and also imposes
time limits on builds.  If you have an especially long-running suite
of scripted tests for your plugin, you can run a subset of scripted
tests in a directory, for example:

```
    - TEST_COMMAND="scripted tests/*1of3"
    - TEST_COMMAND="scripted tests/*2of3"
    - TEST_COMMAND="scripted tests/*3of3"
```

Will create three chunks and run each of the chunks separately for the
directory `tests`.

### Sample setting

Here's a sample that puts them all together. Remember, most of the sections are optional.

```yml
language: scala

jdk: openjdk8

env:
  # This splits the build into two parts
  matrix:
    - TEST_COMMAND="scripted sbt-assembly/*"
    - TEST_COMMAND="scripted merging/* caching/*"

script:
  - sbt -Dfile.encoding=UTF8 -J-XX:ReservedCodeCacheSize=256M "\$TEST_COMMAND"

before_cache:
  - rm -fv \$HOME/.ivy2/.sbt.ivy.lock
  - find \$HOME/.ivy2/cache -name "ivydata-*.properties" -print -delete
  - find \$HOME/.sbt        -name "*.lock"               -print -delete

cache:
  directories:
    - \$HOME/.cache/coursier
    - \$HOME/.ivy2/cache
    - \$HOME/.sbt
```

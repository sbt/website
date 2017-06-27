scala-sbt.org
=============


This project is the source for scala-sbt.org. See [contributors](https://github.com/sbt/website/graphs/contributors) for the list of documentation contributors.

scala-sbt.org is powered by two static site engines.

[nanoc](http://nanoc.ws/) is used for the landing pages.

[Pamflet](http://pamflet.databinder.net/Pamflet.html), a Scala-based documentation engine written by @n8han (and some contributions from @eed3si9n) generates 0.13+ documentations.

These two engines are driven by [sbt-site](https://github.com/sbt/sbt-site) and [sbt-ghpages](https://github.com/sbt/sbt-ghpages). We will shortly use pandoc to also generate pdf files.

## Attention plugin authors

The source for [Community plugins](http://www.scala-sbt.org/release/docs/Community-Plugins.html) page is at [src/reference/01-General-Info/02-Community-Plugins.md](https://github.com/sbt/website/blob/master/src/reference/01-General-Info/02-Community-Plugins.md).
Fork this project, add your plugin and send us a pull request if your plugin is not already on it.

## Setup

Currently, nanoc requires Ruby 2.1 or greater.

You'll need the following gems for running nanoc:

```
$ gem install nanoc:4.0.2
$ gem install redcarpet
$ gem install nokogiri
```

If you're running ubuntu, you'll need to also install ruby-dev for the native-code in redcarpet, and
pandoc/latex for PDF generation:

```
$ sudo add-apt-repository ppa:texlive-backports/ppa
$ sudo apt-get update
$ sudo apt-get install ruby-dev pandoc latex-cjk-all texlive-full
```

On Mac

- download and install MacTEX
- `sudo tlmgr update --self --all`
- follow https://oku.edu.mie-u.ac.jp/~okumura/texwiki/?TeX%20Live%2FMac#bcb0d462
- `brew install pandoc`

## Usage

Currently, Java 7 is required to build the site.  If you have multiple
versions of Java installed on your system, set it to Java 7 (also
known as version 1.7).  One method for choosing the Java version is to
override the value of `JAVA_HOME` in the environment sbt runs.

```
$ env JAVA_HOME="$(/usr/libexec/java_home -v 1.7)" sbt
```

To make site locally, from sbt shell:

```
> makeSite
```

To push site, from sbt shell:

```
> ghpagesPushSite
```

Beware of https://github.com/sbt/sbt-ghpages/issues/25

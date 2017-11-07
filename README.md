scala-sbt.org
=============

This project is the source for scala-sbt.org. See [contributors](https://github.com/sbt/website/graphs/contributors) for the list of documentation contributors.

scala-sbt.org is powered by two static site engines.

[nanoc](http://nanoc.ws/) is used for the landing pages.

[Pamflet](http://www.foundweekends.org/pamflet/), a Scala-based documentation engine written by @n8han (and some contributions from @eed3si9n) generates 0.13+ documentations.

These two engines are driven by [sbt-site](https://github.com/sbt/sbt-site) and [sbt-ghpages](https://github.com/sbt/sbt-ghpages). We will shortly use pandoc to also generate pdf files.

## Attention plugin authors

The source for [Community plugins](http://www.scala-sbt.org/release/docs/Community-Plugins.html) page is at [src/reference/01-General-Info/02-Community-Plugins.md](https://github.com/sbt/website/edit/1.x/src/reference/01-General-Info/02-Community-Plugins.md).
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

To make site locally, from sbt shell:

```
> makeSite
```

To push site, from sbt shell:

```
> ghpagesPushSite
```

Beware of https://github.com/sbt/sbt-ghpages/issues/25

## Releasing new sbt

- Update `project/build.properties`
- Update `project/Docs.scala`
- Update `src/nanoc/nanoc.yaml`
- Update `src/reference/template.properties`

## Including code examples

To include a validated code examples, create a scripted test under `src/sbt-test`,
and in the markdown include as:

```
// This includes the entire file as Scala code snippet
@@snip [build.sbt]($root$/src/sbt-test/ref/basic/build.sbt) {}

or

// This includes snippet between a line containing #example another line with #example
@@snip [build.sbt]($root$/src/sbt-test/ref/basic/build.sbt) { #example }

or

// This specifies syntax highlight
@@snip [build.sbt]($root$/src/sbt-test/ref/basic/build.sbt) { #example type=text }
```

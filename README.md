scala-sbt.org
=============

This project is the source for [scala-sbt.org](https://www.scala-sbt.org). It generates the contents of the site in [sbt/sbt.github.com](@ghRepo) for delivery via GitHub Pages.

See [contributors](https://github.com/sbt/website/graphs/contributors) for the list of documentation contributors.

[scala-sbt.org](https://www.scala-sbt.org) is powered by:

* [nanoc](http://nanoc.ws/) to generate the landing pages.

* [Pamflet](http://www.foundweekends.org/pamflet/), a Scala-based documentation engine written by [@n8han][] (and some contributions from [@eed3si9n][]) generates the sbt 0.13/1.x documentation.

* [Pandoc](https://pandoc.org/) [2.3.1](https://pandoc.org/releases.html#pandoc-2.3.1-28-september-2018), to generate pdf files.

[@n8han]: https://github.com/n8han
[@eed3si9n]: https://github.com/eed3si9n

The site generation is driven by [sbt-site](https://github.com/sbt/sbt-site) and [sbt-ghpages](https://github.com/sbt/sbt-ghpages).

## Attention plugin authors

The source for [Community plugins](https://www.scala-sbt.org/release/docs/Community-Plugins.html) page is at [src/reference/01-General-Info/02-Community-Plugins.md](https://github.com/sbt/website/edit/develop/src/reference/01-General-Info/02-Community-Plugins.md).
Add your plugin to this page and send send us a pull request if your plugin is not already on it.

## Setup

### Minimum setup

Currently, nanoc requires Ruby 2.1 or greater.

You'll need the following gems for running nanoc:

```
$ gem install nanoc:4.0.2
$ gem install redcarpet
$ gem install nokogiri
```

If you're running Ubuntu, you'll need to also install ruby-dev for the native-code in redcarpet:

```
$ sudo apt-get install ruby-dev
```

Also, if you're on Ubuntu you might see an error like this:

```
zlib is missing; necessary for building libxml2
```

If this is the case, run this:

```
$ sudo apt-get install zlib1g-dev
```

### Full setup

The PDF generation is optional, and requires the following additional steps to install 
[TeX Live](https://www.tug.org/texlive/) and [Pandoc](https://pandoc.org/).

#### On Ubuntu

```
$ sudo add-apt-repository ppa:texlive-backports/ppa
$ sudo apt-get update
$ sudo apt-get install pandoc latex-cjk-all texlive-full
```

#### On Mac

These steps are derived from Haruhiko Okumura's instructions at
[TeX Live/Mac](https://texwiki.texjp.org/?TeX%20Live%2FMac#bcb0d462 (in Japanese).

- install [MacTEX](http://www.tug.org/mactex), either via 
  [downloaded pkg](http://www.tug.org/mactex/mactex-download.html) or 
  [homebrew mactex formulae](https://formulae.brew.sh/cask/mactex)
- update TeX Live package manager with `sudo tlmgr update --self --all` (this may take a while)
- `brew install pandoc`

## Usage

To make the site locally, from sbt shell:

```
> makeSite
```

Then open `target/site/index.html`.

To push site, from sbt shell:

```
> ghpagesPushSite
```

Beware that sbt-ghpages interacts badly if your home directory is a git repository: https://github.com/sbt/sbt-ghpages/issues/25

## Releasing new sbt

- Make sure you **enable** pdf generation: `sbt -Dsbt.website.generate_pdf`
- Update `sbt.version` in `project/build.properties`
- Update `targetSbtFullVersion` in `project/Docs.scala`
- Add last release to "Previous releases" in `src/nanoc/nanoc.yaml`
- Update `sbtVersion`, `windowsBuild` and `sbtVersionForScalaDoc` in `src/reference/template.properties`

## Dollar sign

Pamflet uses dollar sign (`$`) as the template variable character.
So if you use it in the document, use need to escape it with backslash: `\$`.

Here's how to check for unescaped dollar signs.

```
$ brew install ripgrep
$ rg '^([^\$]*)[^\\]\$([^\$]*)$' -g '*.md' src

src/reference/02-DetailTopics/03-Dependency-Management/04-Proxy-Repositories.md
79:  export SBT_CREDENTIALS="$HOME/.ivy2/.credentials"
```

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

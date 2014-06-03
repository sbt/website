scala-sbt.org
=============

This project will be the source for scala-sbt.org.

## Setup

You'll need the following gems for running nanoc:

```
$ gem install nanoc
$ gem install redcarpet
$ gem install nokogiri
```

If you're running ubuntu, you'll need to also install ruby-dev for the native-code in redcarpet:

```
$ apt-get install ruby-dev
```

## Usage

To make site locally, from sbt shell:

```
> makeSite
```

To push site, from sbt shell:

```
> ghpagesPushSite
```

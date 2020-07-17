---
out: Contributing-to-sbt.html
---

  [sbt-dev]: https://groups.google.com/d/forum/sbt-dev
  [Update-Report]: Update-Report.html

Contributing to sbt
-------------------

Below is a running list of potential areas of contribution. This list
may become out of date quickly, so you may want to check on the
[sbt-dev mailing list][sbt-dev] if you are interested in a specific topic.

1.  There are plenty of possible visualization and analysis
    opportunities.
    -   'compile' produces an Analysis of the source code containing
        -   Source dependencies
        -   Inter-project source dependencies
        -   Binary dependencies (jars + class files)
        -   data structure representing the
            [API](https://github.com/sbt/zinc/tree/v1.1.0/internal/compiler-interface) of the
            source code There is some code already for generating dot
            files that isn't hooked up, but graphing dependencies and
            inheritance relationships is a general area of work.
    -   'update' produces an [Update Report][Update-Report] mapping
        Configuration/ModuleID/Artifact to the retrieved File
    -   Ivy produces more detailed XML reports on dependencies. These
        come with an XSL stylesheet to view them, but this does not
        scale to large numbers of dependencies. Working on this is
        pretty straightforward: the XML files are created in `~/.ivy2`
        and the `.xsl` and `.css` are there as well, so you don't even need
        to work with sbt. Other approaches described in [the email
        thread](https://groups.google.com/group/simple-build-tool/browse_thread/thread/7761f8b2ce51f02c/129064ea836c9baf)
    -   Tasks are a combination of static and dynamic graphs and it
        would be useful to view the graph of a run
    -   Settings are a static graph and there is code to generate the
        dot files, but isn't hooked up anywhere.

2.  There is support for dependencies on external projects, like on
    GitHub. To be more useful, this should support being able to update
    the dependencies. It is also easy to extend this to other ways of
    retrieving projects. Support for svn and hg was a recent
    contribution, for example.
3.  If you like parsers, sbt commands and input tasks are written using
    custom parser combinators that provide tab completion and error
    handling. Among other things, the efficiency could be improved.
4.  The javap task hasn't been reintegrated
5.  Implement enhanced 0.11-style warn/debug/info/error/trace commands.
    Currently, you set it like any other setting:

```
set logLevel := Level.Warn
```

> or
> :   set Test / logLevel := Level.Warn
>
You could make commands that wrap this, like:

```
warn Test/run
```

Also, trace is currently an integer, but should really be an abstract
data type.

6. Each sbt version has more aggressive incremental compilation and
reproducing bugs can be difficult. It would be helpful to have a mode
that generates a diff between successive compilations and records the
options passed to scalac. This could be replayed or inspected to try to
find the cause.

### Documentation

1.  There's a lot to do with this documentation. If you check it out
    from git, there's a directory called Dormant with some content that
    needs going through.
2.  the main page mentions external project references (e.g.
    to a git repository) but doesn't have anything to link to that explains
    how to use those.
3.  API docs are much needed.
4.  Find useful answers or types/methods/values in the other docs, and
    pull references to them up into /faq or /Name-Index so people can
    find the docs. In general the /faq should feel a bit more like a
    bunch of pointers into the regular docs, rather than an alternative
    to the docs.
5.  A lot of the pages could probably have better names, and/or little
    2-4 word blurbs to the right of them in the sidebar.

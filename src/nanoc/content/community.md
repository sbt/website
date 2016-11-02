---
title: Get Involved
---

  [so]: https://stackoverflow.com/questions/tagged/sbt
  [sub]: https://typesafe.com/how/subscription
  [ml]: https://groups.google.com/d/forum/sbt-dev
  [github]: https://github.com/sbt/sbt
  [twitter]: https://twitter.com/scala_sbt
  [Community-Plugins]: release/docs/Community-Plugins.html
  [issues]: https://github.com/sbt/sbt/issues
  [website]: https://github.com/sbt/website
  [gitter1]: https://gitter.im/sbt/sbt
  [gitter2]: https://gitter.im/sbt/sbt-dev
  [waffle]: https://waffle.io/sbt/sbt
  [community-label]: https://github.com/sbt/sbt/labels/Community
  [327]: https://github.com/sbt/sbt/issues/327
  [831]: https://github.com/sbt/sbt/issues/831

<h2 id="how-can-I-get-help">How can I get help? <a href="#how-can-I-get-help" class="header-link"><span class="header-link-content">&nbsp;</span></a></h2>

For community support use [StackOverflow's sbt tag][so]:

-   State the problem or question clearly and provide enough
    context. Code examples and `build.sbt` are often useful when
    appropriately edited.

For professional support, [Lightbend](https://developer.lightbend.com/), the maintainer of Scala compiler and sbt, provides:

- [Lightbend Subscription](https://www.lightbend.com/platform/subscription), which includes [Expert Support](https://www.lightbend.com/services/expert-support)
- [Training](https://www.lightbend.com/services/training)
- [Consulting](https://www.lightbend.com/services/consulting)

There's also the [Gitter sbt/sbt][gitter1] channel, but the options above are recommended.

<h2 id="how-can-I-help">How can I help? <a href="#how-can-I-help" class="header-link"><span class="header-link-content">&nbsp;</span></a></h2>

sbt is an open source project and everyone is encouraged to get involved!
Join the community of developers building sbt and related tools like Play and Activator.

### Twitter

Follow [@scala_sbt][twitter] on twitter for updates.

### Contribute to StackOverflow

Stack Overflow is a Q&A site for programmers. 
Asking and answering questions on [StackOverflow's sbt tag][so] is a great way to share knowledge about sbt.
Users can vote on each others' contributions and earn reputation points.

### Report bugs

When you find a bug in sbt we want to hear about it!
Your bug reports play an important part in making sbt more reliable and usable.
sbt uses [GitHub to track issues][issues].
The developers need three things from you: **Steps**, **Problems**, and **Expectations**.
See [#327][327] and [#831][831] for example.

#### Steps

When you report bugs, make sure to distinguish facts and opinions.
What we need first is the exact **Steps** to reproduce your problems on **our computers**.
If not reproducible tests, include `build.sbt`, version numbers, your method of running `sbt`,
example code, or anything else you think might help.
If we cannot reproduce the problem in one way or the other, the problem can't be fixed.
Telling us the error messages is not enough.

#### Problems

Next, describe the **Problems**, or what *you think* is the problem.
It might be obvious to you that it's a problem, but it could actually be an intentional behavior.

#### Expectations

The same goes for **Expectations**. Describe what *you think* should've happened. 

#### Notes

Add an optional **Notes** section to describe your analysis.

### Create plugins

Plugins extend the power of sbt and Play.
Writing a new plugin is often easier than patching sbt's core code. 
Check out the list of [community plugins][Community-Plugins].

plugin and sbt core development is discussed on the [sbt-dev list][ml] and
on [Gitter sbt/sbt-dev][gitter2].
You can ask on the list or the chat if you have any questions regarding
plugin and sbt core development.

### Patch the core

sbt's code is hosted on Github [sbt/sbt][github] repository,
and sbt's documentation (including this page) is on [sbt/website][website] repository.

You are welcome to work on any feature you like, but if you'd like some good ideas

- look for issues tagged with the [Community label][community-label]
- check out [waffle sbt/sbt][waffle]'s Ready or Backlogs list
- talk to us on [Gitter sbt/sbt-dev][gitter2]

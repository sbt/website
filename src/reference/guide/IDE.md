
  [metals]: https://scalameta.org/metals/
  [intellij]: https://www.jetbrains.com/idea/
  [vscode]: https://code.visualstudio.com/
  [neovim]: https://neovim.io/

sbt with IDEs
=============

While it's possible to code Scala with just an editor and sbt,
most programmers today use an Integrated Development Environment, or IDE for short.
Two of the popular IDEs in Scala are [Metals][metals] and [IntelliJ IDEA][intellij], and they both integrate with sbt builds.

A few of the advantages of using the IDEs are:

- Jump to definition
- Code completion based on static types
- Listing compilation errors, and jumping to the error positions
- Interactive debugging

Here are a few recipes on how to configure the IDEs to integrate with sbt:

- [Use sbt as Metals build server](../recipes/use-sbt-as-metals-build-server.md)
- [Import to IntelliJ IDEA](../recipes/import-to-intellij.md)

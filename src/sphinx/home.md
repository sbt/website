sbt
===

sbt is a build tool for Scala, Java, and
[more](https://github.com/d40cht/sbt-cpp). It requires Java 1.6 or
later.

Install
-------

See the setup instructions \</Getting-Started/Setup\>.

Features
--------

-   Little or no configuration required for simple projects
-   Scala-based build definition \</Getting-Started/Basic-Def\> that can
    use the full flexibility of Scala code
-   Accurate incremental recompilation using information extracted from
    the compiler
-   Continuous compilation and testing with
    triggered execution \</Detailed-Topics/Triggered-Execution\>
-   Packages and publishes jars
-   Generates documentation with scaladoc
-   Supports mixed Scala/Java \</Detailed-Topics/Java-Sources\> projects
-   Supports testing \</Detailed-Topics/Testing\> with ScalaCheck,
    specs, and ScalaTest. JUnit is supported by a plugin.
-   Starts the Scala REPL with project classes and dependencies on the
    classpath
-   Modularization supported with
    sub-projects \</Getting-Started/Multi-Project\>
-   External project support (list a git repository as a dependency!)
-   Parallel task execution \</Detailed-Topics/Parallel-Execution\>,
    including parallel test execution
-   Library management support \</Getting-Started/Library-Dependencies\>:
    inline declarations, external Ivy or Maven configuration files, or
    manual management

Getting Started
---------------

To get started, *please read* the
Getting Started Guide \</Getting-Started/Welcome\>. You will save
yourself a *lot* of time if you have the right understanding of the big
picture up-front. All documentation may be found via the
table of contents \<index\>.

Use [Stack Overflow](http://stackoverflow.com/tags/sbt) for questions.
Use the sbt-dev mailing list\_ for discussing sbt development. Use the
\#sbt irc channel for questions and discussions.

This documentation can be forked [on
GitHub](https://github.com/sbt/sbt/). Feel free to make corrections and
add documentation. The documentation source is located in the src/sphinx
subdirectory of the GitHub project.

Documentation for 0.7.x has been [archived here](http://www.scala-sbt.org/0.7.7/docs/home.html). This documentation
applies to sbt |version|.

---
out: IDE.html
---

  [metals]: https://scalameta.org/metals/
  [intellij]: https://www.jetbrains.com/idea/
  [lsp]: https://microsoft.github.io/language-server-protocol/
  [vscode]: https://code.visualstudio.com/
  [bsp]: https://build-server-protocol.github.io/
  [vscode-debugging]: https://code.visualstudio.com/docs/editor/debugging
  [intellij-debugging]: https://www.jetbrains.com/help/idea/debugging-code.html

IDE Integration
---------------

While it's possible to code Scala with just an editor and sbt,
most programmers today use an Integrated Development Environment, or IDE for short.
Two of the popular IDEs in Scala are [Metals][metals] and [IntelliJ IDEA][intellij],
and they both integrate with sbt builds.

- [Using sbt as Metals build server](#metals)
- [Using sbt as IntelliJ IDEA build server](#intellij-bsp)
- [Importing to IntelliJ IDEA](#intellij-import)

<a id="metals"></a>
### Using sbt as Metals build server

[Metals][metals] is an open source _language server_ for Scala, which can
act as the backend for [VS Code][vscode] and other editors that support [LSP][lsp].
Metals in turn supports different _build servers_ including sbt via the [Build Server Protocol][bsp] (BSP).

To use Metals on VS Code:

1. Install Metals from Extensions tab:<br>
   ![Metals](files/metals0.png)
2. Open a directory containing a `build.sbt` file.
3. From the menubar, run View > Command Palette... (`Cmd-Shift-P` on macOS) "Metals: Switch build server", and select "sbt"<br>
   ![Metals](files/metals2.png)
4. Once the import process is complete, open a Scala file to see that code completion works:<br>
   ![Metals](files/metals3.png)

Use the following setting to opt-out some of the subprojects from BSP.

```scala
bspEnabled := false
```

When you make changes to the code and save them (`Cmd-S` on macOS), Metals will invoke sbt to do
the actual building work.

#### Interactive debugging on VS Code

1. Metals supports interactive debugging by setting break points in the code:<br>
  ![Metals](files/metals4.png)
2. Interactive debugging can be started by right-clicking on an unit test, and selecting "Debug Test."
   When the test hits a break point, you can inspect the values of the variables:<br>
   ![Metals](files/metals5.png)

See [Debugging][vscode-debugging] page on VS Code documentation for more details on how to navigate an interactive debugging session.

#### Logging into sbt session

While Metals uses sbt as the build server, we can also log into the same sbt session using a thin client.

- From Terminal section, type in `sbt --client`<br>
  ![Metals](files/metals6.png)

This lets you log into the sbt session Metals has started. In there you can call `testOnly` and other tasks with
the code already compiled.

<a id="intellij-bsp"></a>
### Using sbt as IntelliJ IDEA build server

[IntelliJ IDEA][intellij] is an IDE created by JetBrains, and the Community Edition is open source under Apache v2 license.
In addition to its own compilation engine, IntelliJ supports different _build servers_ including sbt via the [Build Server Protocol][bsp] (BSP).

To use sbt as build server on IntelliJ:

1. Install Scala plugin on the Plugins tab:<br>
   ![IntelliJ](files/intellij1.png)
2. To use the BSP approach, do not use Open button on the Project tab:<br>
   ![IntelliJ](files/intellij7.png)
3. From menubar, click New > "Project From Existing Sources", or Find Action (`Cmd-Shift-P` on macOS) and
   type "Existing" to find "Import Project From Existing Sources":<br>
   ![IntelliJ](files/intellij8.png)
4. Open a `build.sbt` file. Select **BSP** when prompted:<br>
   ![IntelliJ](files/intellij9.png)
5. Select **sbt (recommended)** as the tool to import the BSP workspace:<br>
   ![IntelliJ](files/intellij10.png)
6. Once the import process is complete, open a Scala file to see that code completion works:<br>
   ![IntelliJ](files/intellij11.png)

Use the following setting to opt-out some of the subprojects from BSP.

```scala
bspEnabled := false
```

- Open Preferences, search BSP and check "build automatically on file save", and uncheck "export sbt projects to Bloop before import":<br>
  ![IntelliJ](files/intellij12.png)

When you make changes to the code and save them (`Cmd-S` on macOS), IntelliJ will invoke sbt to do
the actual building work.

See also Igal Tabachnik's [Using BSP effectively in IntelliJ and Scala](https://hmemcpy.com/2021/09/bsp-and-intellij/) for more details.

#### Interactive debugging with IntelliJ IDEA

1. IntelliJ supports interactive debugging by setting break points in the code:<br>
   ![IntelliJ](files/intellij4.png)
2. Interactive debugging can be started by right-clicking on an unit test, and selecting "Debug '&lt;test name&gt;'."
   When the test hits a break point, you can inspect the values of the variables:<br>
   ![IntelliJ](files/intellij5.png)

See [Debug Code][intellij-debugging] page on IntelliJ documentation for more details on how to navigate an interactive debugging session.

#### Logging into sbt session

We can also log into the existing sbt session using the thin client.

- From Terminal section, type in `sbt --client`
  ![IntelliJ](files/intellij6.png)

This lets you log into the sbt session IntelliJ has started. In there you can call `testOnly` and other tasks with
the code already compiled.

<a id="intellij-import"></a>
### Importing to IntelliJ IDEA

IntelliJ integrates with many build tools, including sbt, to import the project.
This is a more traditional approach that might be more stable in some cases.

To import a build to IntelliJ IDEA:

1. Install Scala plugin on the Plugins tab.
2. From Projects, open a directory containing a `build.sbt` file.<br>
   ![IntelliJ](files/intellij2.png)
3. Once the import process is complete, open a Scala file to see that code completion works.

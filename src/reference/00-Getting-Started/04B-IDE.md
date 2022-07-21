---
out: IDE.html
---

  [metals]: https://scalameta.org/metals/
  [intellij]: https://www.jetbrains.com/idea/
  [intellij-scala-plugin-2021-2]: https://blog.jetbrains.com/scala/2021/07/27/intellij-scala-plugin-2021-2/#Compiler-based_highlighting
  [lsp]: https://microsoft.github.io/language-server-protocol/
  [vscode]: https://code.visualstudio.com/
  [neovim]: https://neovim.io/
  [bsp]: https://build-server-protocol.github.io/
  [vscode-debugging]: https://code.visualstudio.com/docs/editor/debugging
  [intellij-debugging]: https://www.jetbrains.com/help/idea/debugging-code.html
  [nvim-metals]: https://github.com/scalameta/nvim-metals
  [lsp.lua]: https://github.com/scalameta/nvim-metals/discussions/39#discussion-82302

IDE Integration
---------------

While it's possible to code Scala with just an editor and sbt,
most programmers today use an Integrated Development Environment, or IDE for short.
Two of the popular IDEs in Scala are [Metals][metals] and [IntelliJ IDEA][intellij],
and they both integrate with sbt builds.

- [Using sbt as Metals build server](#metals)
- [Importing to IntelliJ IDEA](#intellij-import)
- [Using sbt as IntelliJ IDEA build server](#intellij-bsp)
- [Using Neovim as Metals frontend](#nvim-metals)

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

<a id="intellij-import"></a>
### Importing to IntelliJ IDEA

[IntelliJ IDEA][intellij] is an IDE created by JetBrains, and the Community Edition is open source under Apache v2 license.
IntelliJ integrates with many build tools, including sbt, to import the project.
This is a more traditional approach that might be more reliable than using BSP approach.

To import a build to IntelliJ IDEA:

1. Install Scala plugin on the Plugins tab:<br>
   ![IntelliJ](files/intellij1.png)
2. From Projects, open a directory containing a `build.sbt` file.<br>
   ![IntelliJ](files/intellij2.png)
3. Once the import process is complete, open a Scala file to see that code completion works.

IntelliJ Scala plugin uses its own lightweight compilation engine to detect errors, which is fast but sometimes incorrect. Per [compiler-based highlighting][intellij-scala-plugin-2021-2], IntelliJ can be configured to use the Scala compiler for error highlighting.

#### Interactive debugging with IntelliJ IDEA

1. IntelliJ supports interactive debugging by setting break points in the code:<br>
   ![IntelliJ](files/intellij4.png)
2. Interactive debugging can be started by right-clicking on an unit test, and selecting "Debug '&lt;test name&gt;'."
　　Alternatively, you can click the green "run" icon on the left part of the editor near the unit test.
   When the test hits a break point, you can inspect the values of the variables:<br>
   ![IntelliJ](files/intellij5.png)

See [Debug Code][intellij-debugging] page on IntelliJ documentation for more details on how to navigate an interactive debugging session.

<a id="intellij-bsp"></a>
### Using sbt as IntelliJ IDEA build server (advanced)

Importing the build to IntelliJ means that you're effectively using IntelliJ as the build tool and the compiler while you code (see also [compiler-based highlighting][intellij-scala-plugin-2021-2]).
While many users are happy with the experience, depending on the code base some of the compilation errors may be false, it may not work well with plugins that generate sources, and generally you might want to code with the identical build semantics as sbt.
Thankfully, modern IntelliJ supports alternative _build servers_ including sbt via the [Build Server Protocol][bsp] (BSP).

The benefit of using BSP with IntelliJ is that you're using sbt to do the actual build work, so if you are the kind of programmer who had sbt session up on the side, this avoids double compilation.

<table class="table table-striped">
  <tr>
    <th><nobr></th>
    <th>Import to IntelliJ</th>
    <th>BSP with IntelliJ</th>
  </tr>
  <tr>
    <td>Reliability</td>
    <td>✅ Reliable behavior</td>
    <td>⚠️ Less mature. Might encounter UX issues.</td>
  </tr>
  <tr>
    <td>Responsiveness</td>
    <td>✅</td>
    <td>⚠️</td>
  </tr>
  <tr>
    <td>Correctness</td>
    <td>⚠️ Uses its own compiler for type checking, but can be configured to use scalac</td>
    <td>✅ Uses Zinc + Scala compiler for type checking</td>
  </tr>
  <tr>
    <td>Generated source</td>
    <td>❌ Generated source requires resync</td>
    <td>✅</td>
  </tr>
  <tr>
    <td>Build reuse</td>
    <td>❌ Using sbt side-by-side requires double build</td>
    <td>✅</td>
  </tr>
</table>

To use sbt as build server on IntelliJ:

1. Install Scala plugin on the Plugins tab.
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

#### Logging into sbt session

We can also log into the existing sbt session using the thin client.

- From Terminal section, type in `sbt --client`
  ![IntelliJ](files/intellij6.png)

This lets you log into the sbt session IntelliJ has started. In there you can call `testOnly` and other tasks with
the code already compiled.

<a id="nvim-metals"></a>
### Using Neovim as Metals frontend (advanced)

[Neovim][neovim] is a modern fork of Vim that supports [LSP][lsp] out-of-box,
which means it can be configured as a frontend for Metals.

Chris Kipp, who is a maintainer of Metals, created [nvim-metals][nvim-metals] plugin that provides comprehensive Metals support on Neovim. To install nvim-metals, create `lsp.lua` under `\$XDG_CONFIG_HOME/nvim/lua/` based on Chris's [lsp.lua][lsp.lua] and adjust to your preference. For example, comment out its plugins section and load the listed plugins using the plugin manager of your choice such as vim-plug.

In `init.vim`, the file can be loaded as:

```
lua << END
require('lsp')
END
```

Per `lsp.lua`, `g:metals_status` should be displayed on the status line, which can be done using lualine.nvim etc.

1. Next, open a Scala file in an sbt build using Neovim.
2. Run `:MetalsInstall` when prompted.
3. Run `:MetalsStartServer`.
4. If the status line is set up, you should see something like "Connecting to sbt" or "Indexing."<br>
   <img src="files/nvim0.png" width="900">
5. Code completion works when you're in Insert mode, and you can tab through the candidates:<br>
   <img src="files/nvim1.png" width="900">

- A build is triggered upon saving changes, and compilation errors are displayed inline:<br>
  <img src="files/nvim2.png" width="900">

#### Go to definition

1. You can jump to definition of the symbol under cursor by using `gD` (exact keybinding can be customized):<br>
   <img src="files/nvim3.png" width="900">
2. Use `Ctrl-O` to return to the old buffer.

#### Hover

- To display the type information of the symbol under cursor, like hovering, use `K` in Normal mode:<br>
   <img src="files/nvim4.png" width="900">

#### Listing diagnostics

1. To list all compilation errors and warnings, use `<leader>aa`:<br>
   <img src="files/nvim5.png" width="900">
2. Since this is in the standard quickfix list, you can use the command such as `:cnext` and `:cprev` to nagivate through the errors and warnings.
3. To list just the errors, use `<leader>ae`.

#### Interactive debugging with Neovim

1. Thanks to nvim-dap, Neovim supports interactive debugging. Set break points in the code using `<leader>dt`:<br>
   <img src="files/nvim6.png" width="900">
2. Nagivate to a unit test, confirm that it's built by hovering (`K`), and then
   "debug continue" (`<leader>dc`) to start a debugger.
   Choose "1: RunOrTest" when prompted.
3. When the test hits a break point, you can inspect the values of the variables by debug hovering (`<leader>dK`):<br>
   <img src="files/nvim7.png" width="900">
4. "debug continue" (`<leader>dc`) again to end the session.

See [nvim-metals][nvim-metals] regarding further details.

#### Logging into sbt session

We can also log into the existing sbt session using the thin client.

1. In a new vim window type `:terminal` to start the built-in terminal.
2. Type in `sbt --client`<br>
   <img src="files/nvim8.png" width="900">

Even though it's inside Neovim, tab completion etc works fine inside.

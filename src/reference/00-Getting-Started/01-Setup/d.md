---
out: Installing-sbt-native-client.html
---

Installing sbtn
-----------------------

sbtn is a client that is able to connect to a running sbt server to issue
commands. Because sbt builds may take some time to load, using sbtn can
significantly improve the experience of using sbt from the command line. Binary
distributions are available for 64 bit Linux, MacOS and Windows. sbtn includes
custom tab completions for bash, fish, powershell and zsh so that you can have
an experience similar to the embedded sbt shell directly from the command line.

### Installing using built in sbt wizard

If you have previously installed sbt $app_version$ or greater, then you can install sbtn
by running

```
sbt installSbtn
```

or simply run `installSbtn` in an interactive sbt shell session. The wizard will
download and install the native binary into a subdirectory of the sbt global
directory, typically found in `~/.sbt/1.0/bin` on most systems. In addition to
downloading the native binary, you will be prompted to setup sbtn for your
shell. The wizard is able to configure your shell environment for bash, fish,
powershell or zsh by updating the standard configuration files. The wizard can
update your shell configuration to add `~/sbt/1.0/bin` to the shell
[PATH](https://kb.iu.edu/d/acar) variable. It also add the commands necessary to
enable the custom tab completions.

### Installing using Coursier

[Coursier](https://get-coursier.io) can be used to install to sbtn using the
`cs` command line tool. Simply run:

```
cs install sbtn
```

### Manual installation

Manual installation of sbtn requires that you download the native binary for
your operating system, optionally download a shell completion script to add custom
tab completions to your shell of choice and optionally update the configuration
file for your shell so that sbtn and its tab completions are available from the
shell prompt. The completion scripts are rarely updated so these will not typically need to be
updated along with sbt. The sbtn binary, on the other hand, should ideally be
updated with each new sbt release.


Binary releases of sbtn are available at
[sbtn-dist](https://github.com/sbt/sbtn-dist/tags). The latest platform specific
releases can be found at:

* Linux <https://github.com/sbt/sbtn-dist/releases/download/v$app_version$/sbtn-x86_64-pc-linux-$app_version$.tar.gz>

* MacOS <https://github.com/sbt/sbtn-dist/releases/download/v$app_version$/sbtn-x86_64-apple-darwin-$app_version$.tar.gz>

* Windows <https://github.com/sbt/sbtn-dist/releases/download/v$app_version$/sbtn-x86_64-pc-win32-$app_version$.zip>

Download and extract the binary into any directory, BIN_DIR. If BIN_DIR is not
on your shell's PATH, you will need to update your shell's configuration file to
add BIN_DIR to the PATH or else you will have to invoke sbtn using the full path
(\$BIN_DIR/sbtn or \$BIN_DIR/sbtn.exe on windows). Instructions for updating the
shell configuration file follow.

### Shell configuration

In order to set up tab completions, a completion script must be downloaded into
a directory, COMPLETIONS_DIR. The completion scripts can be downloaded for

* bash <https://raw.githubusercontent.com/sbt/sbt/v$app_version$/client/completions/sbtn.bash>

* fish <https://raw.githubusercontent.com/sbt/sbt/v$app_version$/client/completions/sbtn.fish>

* powershell <https://raw.githubusercontent.com/sbt/sbt/v$app_version$/client/completions/sbtn.ps1>

* zsh <https://raw.githubusercontent.com/sbt/sbt/v$app_version$/client/completions/_sbtn>

Instructions to set up the shell PATH and tab completions for each supported
shell follows.

### bash
The default bash configuration file is `\$USER_HOME/.bashrc`. The following two
lines will make sbtn and its tab completions available in your bash shell
instances:

```
export PATH=\$PATH:\$BIN_DIR
source \$COMPLETIONS_DIR/sbtn.bash

```

### fish
The default fish shell configuration file is
`\$USER_HOME/.config/fish/config.fish`. The following two lines will make sbtn
and its tab completions available in your fish shell instances:

```
set PATH \$PATH \$BIN_DIR
source \$COMPLETIONS_DIR/sbtn.fish
```

### powershell
The default powershell shell configuration file can be found by running `echo
\$PROFILE` in a powershell session. The following two lines will make sbtn
and its tab completions available in your powershell shell instances:

```
\$env:Path += ";\$BIN_DIR"
. "\$COMPLETIONS_DIR\sbtn.ps1"
```

### zsh
The default zsh configuration file is `\$USER_HOME/.zshrc`. The following three
lines will make sbtn and its tab completions available in your zsh
instances:

```
fpath=(\$fpath \$COMPLETIONS)
autoload -Uz compinit; compinit
path=(\$path \$BIN_DIR)
```

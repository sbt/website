---
out: Manual-Installation.html
---

  [sbt-launch.jar]: $launcher_release_base$$app_version$/sbt-launch.jar

手动安装 sbt
-----------------------

手动安装需要下载 [sbt-launch.jar][sbt-launch.jar]，然后创建脚本来运行它。

### Unix

将 [sbt-launch.jar][sbt-launch.jar] 文件放在 `~/bin` 下。
创建一个脚本来运行这个 jar，脚本 `~/bin/sbt` 内容如下:

```
SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M"
java \$SBT_OPTS -jar `路径名 \$0`/sbt-launch.jar "\$@"
```

给该脚本赋予可执行权限：

```
\$ chmod u+x ~/bin/sbt
```

### Windows

在 Windows 上手动安装的步骤根据是否使用 Cygwin 和终端的不同而不同。 在任何情况下，将 batch 文件或者脚本文件添加到 path 中，使得可以在任意路径下的命令行中敲 `sbt` 来运行 sbt。
同时，如果需要的话，根据机器调节一下 JVM 的参数设置。

#### Non-Cygwin

对于使用标准 Windows 终端的非 Cygwin 用户，创建如 `sbt.bat` 的 batch 文件：

```
set SCRIPT_DIR=%~dp0
java -Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M -jar "%SCRIPT_DIR%sbt-launch.jar" %*
```

然后将下载好的 [sbt-launch.jar][sbt-launch.jar] 放在和 `sbt.bat` 相同的路径下。

#### Cygwin 和标准的 Windows 终端

如果使用 Cygwin 和标准的 Windows 终端，创建如下的 bash 脚本 `~/bin/sbt`：

```
SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M"
java \$SBT_OPTS -jar sbt-launch.jar "\$@"
```

用下载好的 [sbt-launch.jar][sbt-launch.jar] 文件的路径替换掉 sbt-launch.jar，如果需要的话记得使用 cygpath。给脚本赋予可执行权限：

```
\$ chmod u+x ~/bin/sbt
```

#### Cygwin 和 Ansi 终端

如果使用 Cygwin 和 Ansi 终端（支持 Ansi 转义序列并且可以通过 stty 配置），创建一个 bash 文件 `~/bin/sbt`：

```
SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M"
stty -icanon min 1 -echo > /dev/null 2>&1
java -Djline.terminal=jline.UnixTerminal -Dsbt.cygwin=true \$SBT_OPTS -jar sbt-launch.jar "\$@"
stty icanon echo > /dev/null 2>&1
```

用下载好的 [sbt-launch.jar][sbt-launch.jar] 文件的路径替换掉 sbt-launch.jar，如果需要的话记得使用 cygpath。给脚本赋予可执行权限：

```
\$ chmod u+x ~/bin/sbt
```

为了让退格（backspace）能够在 Scala 的控制台中正常工作，你需要确保你的退格键发送的是删除符（erase character），和在 stty 中配置的一样。对于默认的 cygwin 终端（mintty），
在设置选项 -> 键中，如果你的删除符是 cygwin 默认的 ^H，“退格发送 ^H” 需要被选中。

> **注意：** 当前其他的配置还不支持。请 [提交 pull request](https://github.com/sbt/sbt/blob/0.13/CONTRIBUTING.md) 实现或者描述已经支持的配置。

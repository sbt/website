---
out: Manual-Installation.html
---

  [sbt-launch.jar]: $launcher_release_base$$app_version$/sbt-launch.jar

手动安装sbt
-----------------------

手动安装需要下载[sbt-launch.jar][sbt-launch.jar], 然后创建脚本来运行它.

### Unix

将[sbt-launch.jar][sbt-launch.jar]文件放在`~/bin`下.

创建一个脚本来运行这个jar, 脚本`~/bin/sbt`内容如下:

```
SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M"
java \$SBT_OPTS -jar `路径名 \$0`/sbt-launch.jar "\$@"
```

给该脚本赋予可执行权限:

```
\$ chmod u+x ~/bin/sbt
```

### Windows

在Windows上手动安装的步骤根据是否使用Cygwin和终端的不同而不同. 在任何情况下, 将batch文件或者脚本文件添加到path中, 使得可以在任意路径下的命令行中敲`sbt`来运行sbt.
同时, 如果需要的话, 根据机器调节一下JVM的参数设置.

#### Non-Cygwin

对于使用标准Windows终端的非Cygwin用户, 创建如`sbt.bat`的batch文件:

```
set SCRIPT_DIR=%~dp0
java -Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M -jar "%SCRIPT_DIR%sbt-launch.jar" %*
```

然后将下载好的[sbt-launch.jar][sbt-launch.jar]放在和`sbt.bat`相同的路径下.

#### Cygwin和标准的Windows终端

如果使用Cygwin和标准的Windows终端, 创建如下的bash脚本`~/bin/sbt`:

```
SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M"
java \$SBT_OPTS -jar sbt-launch.jar "\$@"
```

用下载好的[sbt-launch.jar][sbt-launch.jar]文件的路径替换掉sbt-launch.jar, 如果需要的话记得使用cygpath. 给脚本赋予可执行权限:

```
\$ chmod u+x ~/bin/sbt
```

#### Cygwin和Ansi终端

如果使用Cygwin和Ansi终端(支持Ansi转义序列并且可以通过stty配置), 创建一个bash文件`~/bin/sbt`:

```
SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M"
stty -icanon min 1 -echo > /dev/null 2>&1
java -Djline.terminal=jline.UnixTerminal -Dsbt.cygwin=true \$SBT_OPTS -jar sbt-launch.jar "\$@"
stty icanon echo > /dev/null 2>&1
```

用下载好的[sbt-launch.jar][sbt-launch.jar]文件的路径替换掉sbt-launch.jar, 如果需要的话记得使用cygpath. 给脚本赋予可执行权限:

```
\$ chmod u+x ~/bin/sbt
```

为了让退格(backspace)能够在scala的控制台中正常工作, 你需要确保你的退格键发送的是删除符(erase character), 和在stty中配置的一样. 对于默认的cygwin终端(mintty), 
在设置选项 -> 键中, "退格发送 ^H"需要被选中如果你的删除符是cygwin默认的^H.

> **注意:** 当前其他的配置还不支持. 请[提交pull request](https://github.com/sbt/sbt/blob/0.13/CONTRIBUTING.md)实现或者描述已经支持的配置.

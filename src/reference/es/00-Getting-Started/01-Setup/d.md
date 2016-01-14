---
out: Manual-Installation.html
---

  [sbt-launch.jar]: $launcher_release_base$$app_version$/sbt-launch.jar

Installing sbt manually
-----------------------

La instalación manual requiere la descarga de [sbt-launch.jar][sbt-launch.jar] y la
creación de un script para ejecutarlo.

### Unix

Ponga [sbt-launch.jar][sbt-launch.jar] en `~/bin`.

Cree un script para ejecutar el jar, mediante la creación de `~/bin/sbt`
con el siguiente contenido:

```
#!/bin/bash
SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M"
java \$SBT_OPTS -jar `dirname \$0`/sbt-launch.jar "\$@"
```

Haga el script ejecutable con:

```
\$ chmod u+x ~/bin/sbt
```

### Windows

La instalación manual para Windows varía según el tipo de terminal y
dependiendo de si Cygwin es usado o no. En todos los casos, ponga el
archivo batch o el script en el *path* de modo que pueda iniciar `sbt`
en cualquier directorio mediante teclear `sbt` en la línea de comandos.
También, ajuste los settings de la JVM de acuerdo con su máquina si es
necesario.

#### Non-Cygwin

Para usuarios que no utilizan Cygwin, pero que usan la terminal
standard de Windows, cree un archivo batch `sbt.bat`:

```
set SCRIPT_DIR=%~dp0
java -Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M -jar "%SCRIPT_DIR%sbt-launch.jar" %*
```

y ponga el [sbt-launch.jar][sbt-launch.jar] que descargó en el mismo directorio que
archivo batch.

#### Cygwin con la terminal standard de Windows

Si utiliza Cygwin con la terminal standard de Windows, cree un
script de bash `~/bin/sbt`:

```
SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M"
java \$SBT_OPTS -jar sbt-launch.jar "\$@"
```

Reemplace `sbt-launch.jar` con la ruta hasta el [sbt-launch.jar][sbt-launch.jar] que
descargó y recuerde utilizar `cygpath` si es necesario. Haga el scrip
ejecutable con:

```
\$ chmod u+x ~/bin/sbt
```

#### Cygwin con una terminal Ansi

Si utiliza Cygwin con una terminal Ansi (que soporte secuentas de
escape Ansi y que sea configurable mediante `stty`), cree un script
`~/bin/sbt`:

```
SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M"
stty -icanon min 1 -echo > /dev/null 2>&1
java -Djline.terminal=jline.UnixTerminal -Dsbt.cygwin=true \$SBT_OPTS -jar sbt-launch.jar "\$@"
stty icanon echo > /dev/null 2>&1
```

Reemplace `sbt-launch.jar` con la ruta hasta el [sbt-launch.jar][sbt-launch.jar] que
descargó y recuerde utilizar `cygpath` si es necesario. Entonces, haga
que el script sea ejecutable con:

```
\$ chmod u+x ~/bin/sbt
```

Para que la tecla *backspace* funcione correctamente en la consola de
scala, necesita asegurarse de que dicha tecla esté enviando el caracter
de borrado, de acuerdo a la configuración de `stty`. Para la terminal por
default de cygwin (mintty) puede encontrar una configuración en Options
-> Keys "Backspace sends ^H" que necesitará estar palomeada si su
tecla de borrado envía el caracter por default de cygwin `^H`.

> **Note:** Otras configuraciones no están actualmente soportadas. Por favor envíe
> [pull requests](https://github.com/sbt/sbt/blob/0.13/CONTRIBUTING.md)
> implementando o describiendo dicho soporte.

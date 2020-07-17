---
out: Running.html
---

  [ByExample]: sbt-by-example.html
  [Setup]: Setup.html
  [Triggered-Execution]: ../docs/Triggered-Execution.html
  [Command-Line-Reference]: ../docs/Command-Line-Reference.html

Ejecución
---------

Esta página describe cómo usar sbt una vez has configurado tu proyecto.
Se supone que has [instalado sbt][Setup] y leído [sbt mediante ejemplos][ByExample].

### El shell de sbt

Lanza sbt en el directorio de tu proyecto sin argumentos:

```
\$ sbt
```

Ejecutar sbt sin argumentos hace que se inicie un shell de sbt.
El shell de sbt tiene un prompt de comandos (¡con autocompletado e historial!).

Por ejemplo, podrías escribir `compile` en el shell:

```
> compile
```

Para lanzar `compile` de nuevo pulsa la tecla arriba y pulsa `Intro`.

Para ejecutar tu programa, escribe `run`.

Para salir del shell escribe `exit` o usa Ctrl+D (Unix) o Ctrl+Z (Windows).

### Modo por lotes

Puedes tambien ejecutar sbt en modo por lotes, proporcionando una lista de
comandos sbt separados por espacio como argumentos. Aquellos comandos de sbt que
necesiten argumentos pueden ser pasados como un único argumento entrecomillado,
por ejemplo:

```
\$ sbt clean compile "testOnly TestA TestB"
```

En este ejemplo, `testOnly` tiene como argumentos `TestA` y `TestB`.
Los comandos son ejecutados secuencialmente: (`clean`, `compile` y luego
`testOnly`).

**Note**: El modo por lotes implica levantar una JVM y JIT cada vez, por lo que
**la construcción será mucho más lenta**.
Para el desarrollo del día a día recomendamos utilizar el shell de sbt o
construir y testear continuamente tal y como se explica más abajo.

A partir de sbt 0.13.16, se lanza un mensaje informativo cuando sbt se utiliza
en el modo por lotes.

```
\$ sbt clean compile
[info] Executing in batch mode. For better performance use sbt's shell
...
```

Sólo será mostrado con `sbt compile`.
Puede ser desactivado con `suppressSbtShellNotification := true`.

### Construir y testear continuamente

Para acelerar el ciclo editar-compilar-testear puedes indicarle a sbt que
recompile automáticamente o lance tests cada vez que guardes un fichero fuente.

Haz que un comando se ejecute cuando uno o más ficheros fuente cambien
prefijando dicho comando con `~`. Por ejemplo, prueba en el shell de sbt:

```
> ~testQuick
```

Pulsa `Intro` para dejar de observar los cambios.

Puedes utilizar el prefijo `~` tanto en el shell de sbt como en modo por lotes.

<!-- TODO: Triggered -> disparada?? -->
Para más información mira [Ejecución disparada][Triggered-Execution].

### Comandos comunes

A continuación presentamos una lista con algunos de los comandos más comunes de sbt.
Para una lista más completa mira
[Referencia de línea de comandos][Command-Line-Reference].

<table class="table table-striped">
  <tr>
    <td><tt>clean</tt></td>
    <td>Borra todos los ficheros generados (en el directorio <tt>target</tt>).</td>
  </tr>
  <tr>
    <td><tt>compile</tt></td>
    <td>Compila los ficheros fuente principales (en los directorios <tt>src/main/scala</tt> y
   <tt>src/main/java</tt>).</td>
  </tr>
  <tr>
    <td><tt>test</tt></td>
    <td>Compila y ejecuta todos los tests.</td>
  </tr>
  <tr>
    <td><tt>console</tt></td>
    <td>Inicia el intérprete de Scala con un classpath que incluye los ficheros fuente
    compilados y todas sus dependencias. Para volver a sbt, escribe <tt>:quit</tt> o pulsa
   Ctrl+D (Unix) o Ctrl+Z (Windows).</td>
  </tr>
  <tr>
    <td><nobr><tt>run &lt;argument&gt;*</tt></nobr></td>
    <td>Ejecuta la clase principal del proyecto en la misma máquina virtual que sbt</td>
  </tr>
  <tr>
    <td><tt>package</tt></td>
    <td>Crea un fichero jar conteniendo los ficheros de
    <tt>src/main/resources</tt> y las clases compiladas de <tt>src/main/scala</tt> y
    <tt>src/main/java</tt>.</td>
  </tr>
  <tr>
    <td><tt>help &lt;comando&gt;</tt></td>
    <td>Muestra ayuda detallada para el comando especificado.
    Si no se proporciona ningún comando entonces mostrará una breve descripción de cada comando.</td>
  </tr>
  <tr>
    <td><tt>reload</tt></td>
    <td>Recarga la definición de construcción (los ficheros <tt>build.sbt</tt>, <tt>project/*.scala</tt>,
    <tt>project/*.sbt</tt>). Necesario si cambias la definición de construcción.</td>
  </tr>
</table>

### Autocompletado

El shell de sbt tiene autocompletado, incluso con un prompt vacío.
Una convención especial de sbt es que si se presiona tab una vez se se mostrarán
las opciones más probables mientras que si se pulsa más veces se mostrarán aún más
opciones.

### Comandos históricos

El shell de sbt recuerda el histórico de comandos, incluso si sales de sbt y lo
reinicias. La forma más sencilla de acceder al histórico es con la tecla arriba.
Los siguientes comandos están soportados:

<table class="table table-striped">
  <tr>
    <td><tt>!</tt></td>
    <td>Muestra la ayuda para comandos históricos.</td>
  </tr>
  <tr>
    <td><tt>!!</tt></td>
    <td>Ejecuta el comando previo otra vez.</td>
  </tr>
  <tr>
    <td><tt>!:</tt></td>
    <td>Muestra todos los comandos escritos hasta el momento.</td>
  </tr>
  <tr>
    <td><tt>!:n</tt></td>
    <td>Muestra los últimos <tt>n</tt> comandos.</td>
  </tr>
  <tr>
    <td><tt>!n</tt></td>
    <td>Ejecuta el comando con índice <tt>n</tt>, como se muestra en el comando <tt>!:</tt>.</td>
  </tr>
  <tr>
    <td><tt>!-n</tt></td>
    <td>Ejecuta el n-ésimo comando anterior a este.</td>
  </tr>
  <tr>
    <td><tt>!string</tt></td>
    <td>Ejecuta el comando más reciente que empiece con 'string.'</td>
  </tr>
  <tr>
    <td><tt>!?string</tt></td>
    <td>Ejecuta el comando más reciente que contenga 'string.'</td>
  </tr>
</table>

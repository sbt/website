---
out: Running.html
---

  [Hello]: Hello.html
  [Setup]: Setup.html
  [Triggered-Execution]: http://www.scala-sbt.org/release/docs/Detailed-Topics/Triggered-Execution.html
  [Command-Line-Reference]: http://www.scala-sbt.org/release/docs/Detailed-Topics/Command-Line-Reference.html

Ejecución
---------

Esta página describe cómo utilizar `sbt` una vez que usted a configurado
su proyecto. Se asume que usted ha [instalado sbt][Setup] y que ha
creado un proyecto [Hello, World][Hello] u otro proyecto.

### Modo interactivo

Ejecute sbt en el directorio de su proyecto sin argumentos:

```
\$ sbt
```

Ejecutar sbt sin ningún argumento en la línea de comandos, inicia sbt en
modo interactivo. El modo interactivo tiene una línea de comandos (¡con
*tab completion* e historia!).

Por ejemplo, usted puede teclear `compile` en el prompt de sbt:

```
> compile
```

Para key:`compile` de nuevo, presione la tecla "arriba" y entonces
enter.

Para ejecutar su programa nuevamente, teclee `run`.

Para dejar el modo interactivo, teclee `exit` o utilice Ctrl+D (Unix) o
Ctrl+Z (Windows).

### Modo Batch (por lotes)

También puede ejecutar sbt en modo batch, especificando una lista
separada por espacios de comandos de sbt como argumentos. Para comandos
de sbt que toman argumentos, pase el comando y los argumentos como uno
solo a `sbt` mediante encerrarlos entre comillas. Por ejemplo:

```
\$ sbt clean compile "testOnly TestA TestB"
```

En este ejemplo, la *key* `testOnly` tiene argumentos, `TestA` y
`TestB`. Los comandos se ejecutarán en sequencia (`clean`, `compile`, y
entonces `testOnly`).

### Construcción y test continuos

Para acelerar el ciclo de edición-compilación-prueba, puede pedir a sbt
que recompile automáticamente o que ejecute los tests siempre que se
guarde un archivo de código fuente.

Puede conseguir que un comando se ejecute siempre que uno o más archivos
de código fuente cambien al agregar como prefijo `~`. Por ejemplo, en
modo interactivo, intente:

```
> ~ compile
```

Presione enter para dejar de observar sus cambios.

Usted puede usar el prefijo `~` ya sea en modo interactivo o en modo
*batch*.

Vea [Triggered Execution][Triggered-Execution] para más detalles.

### Comandos comunes

Aquí encontrará algunos de los comandos de sbt más comunes. Para una
lista más completa, vea [Command Line Reference][Command-Line-Reference].

<table>
  <tr>
    <td><tt>clean</tt></td>
    <td>Borra todos los archivos generados (en el directorio <tt>target</tt>).</td>
  </tr>
  <tr>
    <td><tt>compile</tt></td>
    <td>Compila los archivos de código fuente de main (en los
    directorios <tt>src/main/scala</tt> y
   <tt>src/main/java</tt>).</td>
  </tr>
  <tr>
    <td><tt>test</tt></td>
    <td>Compila y ejecuta todos los tests.</td>
  </tr>
  <tr>
    <td><tt>console</tt></td>
    <td>Inicia el interprete de Scala con un classpath que incluye
    el código fuente compilado y todas las dependencias. Para regresar a
    sbt, teclee :quit, Ctrl+D (Unix), o Ctrl+Z (Windows).</td>
  </tr>
  <tr>
    <td><nobr><tt>run &lt;argument&gt;*</tt></nobr></td>
    <td>Ejecuta la clase principal para el proyecto en la
    misma máquina virtual que sbt.</td>
  </tr>
  <tr>
    <td><tt>package</tt></td>
    <td>crea un archivo jar que contiene los archivos en
    <tt>src/main/resources</tt> y las clases compiladas de <tt>src/main/scala</tt> y
    <tt>src/main/java</tt>.</td>
  </tr>
  <tr>
    <td><tt>help &lt;command&gt;</tt></td>
    <td>Despliega ayuda detallada para el comando
    especificado. Si no se proporciona ningún comando, despliega una
    breve descripción de todos los comandos.</td>
  </tr>
  <tr>
    <td><tt>reload</tt></td>
    <td>Recarga la definición de la construcción (los archivos
    <tt>build.sbt</tt>, <tt>project/*.scala</tt>,
    <tt>project/*.sbt</tt>). Este comando es
    necario si cambia la definición de la construcción.</td>
  </tr>
</table>

### Tab completion

El modo interactivo tiene *tab completion*, incluyendo el caso cuando se
tiene un prompt vacio. Una convención especial de sbt es que presionar
tab una vez puede mostrar únicamente un subconjunto de *completions* más
probables, mientras que presionarlo más veces muestra opciones más
verbosas.

### Comandos de historia

El modo interactivo recuerda la historia, incluso si usted sale de sbt y
lo reinicia. La manera más simple de acceder a la historia es con la
tecla "arriba". También se soportan los siguientes comandos:

<table>
  <tr>
    <td><tt>!</tt></td>
    <td>Muestra la ayuda para los comandos de historia.</td>
  </tr>
  <tr>
    <td><tt>!!</tt></td>
    <td>Ejecuta el comando previo de nuevo.</td>
  </tr>
  <tr>
    <td><tt>!:</tt></td>
    <td>Muestra todos los comandos previos.</td>
  </tr>  
  <tr>
    <td><tt>!:n</tt></td>
    <td>Muestra los <tt>n</tt> comandos previos.</td>
  </tr>
  <tr>
    <td><tt>!n</tt></td>
    <td>Ejecuta el comando con índice <tt>n</tt>, como se indica con el
    comando <tt>!:</tt>.</td>
  </tr>
  <tr>
    <td><tt>!-n</tt></td>
    <td>Ejecuta el comando n-th previo a este.</td>
  </tr>
  <tr>
    <td><tt>!cadena</tt></td>
    <td>Ejecuta el comando más reciente que comienza con 'cadena'.</td>
  </tr>
  <tr>
    <td><tt>!?cadena</tt></td>
    <td>Ejecuta el comando más reciente que contenga 'cadena'.</td>
  </tr>
</table>

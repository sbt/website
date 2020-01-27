---
out: Custom-Settings.html
---

  [Basic-Def]: Basic-Def.html
  [Task-Graph]: Task-Graph.html
  [Using-Plugins]: Using-Plugins.html
  [Organizing-Build]: Organizing-Build.html
  [Input-Tasks]: ../docs/Input-Tasks.html
  [Plugins]: ../docs/Plugins.html
  [Tasks]: ../docs/Tasks.html

Entradas y tareas personalizadas
--------------------------------

Esta página sirve de introducción para crear entradas y tareas personalizadas.

Para entender esta página, asegúrate de que has leído las páginas anteriores de
la Guía de inicio, en particular [Definiciones de construcción][Basic-Def] y
[Grafos de tareas][Task-Graph].

### Definir una clave

[Keys](../../api/sbt/Keys\$.html) está lleno de ejemplos que ilustran cómo definir
claves. La mayoría de las claves están implementadas en
[Defaults](https://github.com/sbt/sbt/blob/develop/main/src/main/scala/sbt/Defaults.scala).

Una clave tiene uno de tres posibles tipos: `SettingKey` y `TaskKey` son
descritos en [Definiciones de construcción][Basic-Def]. Para saber más acerca de
`InputKey` puedes ver la página [Tareas con entrada][Input-Tasks].

Algunos ejemplos de [Keys](../../api/sbt/Keys\$.html):

```scala
val scalaVersion = settingKey[String]("The version of Scala used for building.")
val clean = taskKey[Unit]("Deletes files produced by the build, such as generated sources, compiled classes, and task caches.")
```

Los constructores de claves toman como parámetros dos cadenas de caracteres: el
nombre de la clave (`"scalaVersion"`) y una descripción
(`"The version of Scala used for building."`).

Como recordarás, en [Definición de construcción][Basic-Def] se explica que el
tipo del parámetro `T` en `SettingsKey[T]` indica el tipo del valor que tiene la
entrada. `T` en `TaskKey[T]` indica el tipo del resultado de la tarea. También
recordarás que una entrada tiene un valor fijo único hasta la siguiente recarga
del proyecto, mientras que una tarea es recalculada para cada
"ejecución de la tarea" (cada vez que alguien escribe un comando en el prompt
interactivo de sbt o utiliza el modo por lotes).

Las claves pueden estar definidas en un [fichero .sbt][Basic-Def], un
[fichero .scala][Organizing-Build] o un [autoplugin][Using-Plugins].
Cualesquiera `val` encontrados bajo el objeto `autoImport` de un autoplugin
habilitado será importado automáticamente en tus ficheros `.sbt`.

### Implementar una tarea

Una vez que hayas definido una clave para tu tarea necesitarás completarla con
una definición de tarea. Puedes tanto definir tu propia tarea como redefinir
una ya existente. Para cualquiera de los dos casos hay que utilizar `:=` para
asociar cierto código con la clave tarea.

```scala
val sampleStringTask = taskKey[String]("A sample string task.")
val sampleIntTask = taskKey[Int]("A sample int task.")

ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "$example_scala_version$"

lazy val library = (project in file("library"))
  .settings(
    sampleStringTask := System.getProperty("user.home"),
    sampleIntTask := {
      val sum = 1 + 2
      println("sum: " + sum)
      sum
    }
  )
```

Si la tarea tiene dependencias deberías referenciar su valor utilizando `value`
tal cual se explicó en [Grafos de tareas][Task-Graph].

La parte más difícil sobre cómo implementar las tareas normalmente no tiene nada que ver
con sbt, ya que las tareas son simplemente código de Scala. La parte difícil
sería escribir el "cuerpo" de tu tarea para que haga aquello que estás
intentando hacer. Por ejemplo, puede que estés intentando formatear un texto en
HTML para lo cual puede que requieras la utilización de una biblioteca de HTML
(puede que necesites
[añadir una dependencia de biblioteca a tu definición de construcción][Using-Plugins]
y escribir código basado en dicha biblioteca).

sbt tiene algunas bibliotecas útiles y funciones convenientes, en particular
puedes utilizar la APIs de [IO](../../api/sbt/io/IO\$.html) para manipular ficheros
y directorios.

### Semántica de ejecución de las tareas

Cuando una tarea personalizada utiliza `value` para depender de otras tareas, algo importante a tener en cuenta es la semántica de ejecución de las tareas.
Por semántica de ejecución nos referimos a *cuándo* exactamente estas tareas son
evaluadas.

Si tomamos por ejemplo `sampleIntTask`, cada línea del cuerpo de la tarea
debería de ser evaluada estrictamente una tras otra.
Eso es semántica secuencial:

```scala
sampleIntTask := {
  val sum = 1 + 2        // primera
  println("sum: " + sum) // segunda
  sum                    // tercera
}
```

En realidad, la JVM puede ejecutar `sum` en línea y hacer que valga 3, pero el
*efecto* observable de la tarea permanecerá intacto como si cada línea fuese
ejecutada una tras otra.

Supongamos ahora que definimos dos o más tareas personalizadas `startServer` y
`stopServer`, y modificamos `sampleIntTask` como sigue:

```scala
val startServer = taskKey[Unit]("start server")
val stopServer = taskKey[Unit]("stop server")
val sampleIntTask = taskKey[Int]("A sample int task.")
val sampleStringTask = taskKey[String]("A sample string task.")

ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "$example_scala_version$"

lazy val library = (project in file("library"))
  .settings(
    startServer := {
      println("starting...")
      Thread.sleep(500)
    },
    stopServer := {
      println("stopping...")
      Thread.sleep(500)
    },
    sampleIntTask := {
      startServer.value
      val sum = 1 + 2
      println("sum: " + sum)
      stopServer.value // THIS WON'T WORK
      sum
    },
    sampleStringTask := {
      startServer.value
      val s = sampleIntTask.value.toString
      println("s: " + s)
      s
    }
  )
```

Al ejecutar `sampleIntTask` desde el prompt interactivo de sbt el resultado
será el siguiente:

```
> sampleIntTask
stopping...
starting...
sum: 3
[success] Total time: 1 s, completed Dec 22, 2014 5:00:00 PM
```

Para revisar qué ha sucedido vamos a mirar la notación gráfica de
`sampleIntTask`:

![task-dependency](../files/task-dependency00.png)

A diferencia de las llamadas a métodos normales de Scala, el hecho de llamar al método
`value` en tareas no hará que sea evaluado estrictamente. En su lugar,
simplemente servirá para declarar que `sampleIntTask` depende de
las tareas `startServer` y `stopServer`.
Cuando `sampleIntTask` es invocado, el motor de tareas de sbt hará lo siguiente:

- evaluará las dependencias de las tareas *antes* de evaluar `sampleIntTask`
  (orden parcial)
- intentará evaluar las dependencias de las tareas en paralelo si son
  independientes (paralelización)
- cada dependencia de la tarea será evaluada una única vez por cada ejecución
  del comando (deduplicación)

#### Deduplicación de dependencias de tareas

Para demostrar el último punto, podemos ejecutar `sampleStringTask` desde el
prompt interactivo de sbt.

```
> sampleStringTask
stopping...
starting...
sum: 3
s: 3
[success] Total time: 1 s, completed Dec 22, 2014 5:30:00 PM
```

Debido a que `sampleStringTask` depende tanto de `startServer` como de
`sampleIntTask`, y `sampleIntTask` depende a su vez de `startServer`, ésta
aparece dos veces listada como dependencia.
Si fuese una llamada normal a un método de Scala, ésta sería evaluada dos
veces, pero debido a que `value` se usa simplemente para indicar la
dependencia de otra tarea, ésta es evaluada sólo una vez. A continuación se
muestra una notación gráfica de la evaluación de `sampleStringTask`:

![task-dependency](../files/task-dependency01.png)

Si no hubiésemos deduplicado las dependencias de tareas habríamos acabado
compilando el código fuente de los tests muchas veces cuando la tarea `test`
hubiese sido invocada, ya que `Test / compile` aparece muchas veces como
dependencia de `Test / test`.

#### Tarea de limpieza

Entonces ¿cómo se podría implementar la tarea `stopServer`?
La noción de tarea de limpieza no encaja en el modelo de ejecución de tareas
debido a que las tareas tratan de seguir dependencias.
La última operación debería ser la tarea que depende de otras tareas
intermedias. Por ejemplo `stopServer` debería depender de `sampleStringTask` por
lo que `stopServer` debería de ser `sampleStringTask`.

```scala
lazy val library = (project in file("library"))
  .settings(
    startServer := {
      println("starting...")
      Thread.sleep(500)
    },
    sampleIntTask := {
      startServer.value
      val sum = 1 + 2
      println("sum: " + sum)
      sum
    },
    sampleStringTask := {
      startServer.value
      val s = sampleIntTask.value.toString
      println("s: " + s)
      s
    },
    sampleStringTask := {
      val old = sampleStringTask.value
      println("stopping...")
      Thread.sleep(500)
      old
    }
  )
```

Para demostrar que esto funciona ejecutemos `sampleStringTask` desde el prompt
interactivo:

```
> sampleStringTask
starting...
sum: 3
s: 3
stopping...
[success] Total time: 1 s, completed Dec 22, 2014 6:00:00 PM
```

![task-dependency](../files/task-dependency02.png)

#### Usar Scala

Otra forma de asegurarse de que algo sucede después de algo es usando Scala.
Si se implementa una función simple en `project/ServerUtil.scala` por ejemplo
se podrá escribir:

```scala
sampleIntTask := {
  ServerUtil.startServer
  try {
    val sum = 1 + 2
    println("sum: " + sum)
  } finally {
    ServerUtil.stopServer
  }
  sum
}
```

Ya que las llamadas a métodos normales siguen la semántica secuencial todo
sucede en orden.
No hay deduplicación, por lo que ya no hay que preocuparse por eso.

### Conversión en plugins

Si te has encontrado con un montón de código personalizado podrías considerar
moverlo a un plugin para reutilizarlo a lo largo de múltiples construcciones.
Es muy fácil crear un plugin, como se [mostró antes][Using-Plugins]
y [explicó con más detalle aquí][Plugins].

Esta página ha sido solo un aperitivo. Hay mucho mucho más sobre tareas
personalizadas en la página de [Tareas][Tasks].

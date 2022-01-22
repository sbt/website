---
out: Task-Graph.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Make]: https://en.wikipedia.org/wiki/Make_(software)
  [Ant]: https://ant.apache.org/
  [Rake]: https://ruby.github.io/rake/

Grafos de tareas
----------------

Continuando con la [definición de construcción][Basic-Def], esta página explica
la definición de `build.sbt` con más detalle.

En lugar de pensar en la configuración como pares clave-valor una analogía más
apropiada sería pensar en ella como un _grafo acíclico dirigido_ (GAD) de tareas
donde los vértices significan **sucede antes de**.
Lo llamaremos _grafo de tareas_.

### Terminología

Repasemos los términos clave antes de seguir profundizando.

- Expresión de entrada/tarea: entrada dentro de `.settings(...)`.
- Clave: parte izquierda de una expresión. Puede ser de tipo
  `SettingKey[A]`, `TaskKey[A]` o `InputKey[A]`.
- Entrada: Definida por una expressión de entrada con `SettingKey[A]`.
  El valor es calculado una única vez durante la carga.
- Tarea: Definida por una expresión de tarea con `TaskKey[A]`.
  El valor es calculado cada vez que es invocado.

### Declarando dependencia de otras tareas

En el DSL de `build.sbt` se utiliza el método `.value` para expresar una
dependencia de otra tarea o entrada. El método `value` es especial y sólo puede
ser llamado como argumento de `:=` (o `+=` o `++=`, los cuales veremos más
adelante).

Como primer ejemplo supongamos que redefinimos `scalacOption` para que dependa de
las tareas `update` y `clean`. A continuación se muestran las definiciones de
estas claves (tal cual están definidas en [Keys](../../api/sbt/Keys\$.html)).

**Nota**: Los valores calculados abajo no tienen mucho sentido para
`scalacOptions` pero sirven a modo de demostración.

```scala
val scalacOptions = taskKey[Seq[String]]("Options for the Scala compiler.")
val update = taskKey[UpdateReport]("Resolves and optionally retrieves dependencies, producing a report.")
val clean = taskKey[Unit]("Deletes files produced by the build, such as generated sources, compiled classes, and task caches.")
```

A continuación se muestra cómo podemos redefinir `scalacOptions`:

```scala
scalacOptions := {
  val ur = update.value  // la tarea update sucede antes de scalacOptions
  val x = clean.value    // la tarea clean sucede antes de scalacOptions
  // ---- scalacOptions empieza aquí ----
  ur.allConfigurations.take(3)
}
```

`update.value` y `clean.value` declaran dependencias de tarea, mientras que
`ur.allConfigurations.take(3)` es el cuerpo de la tarea.

`.value` no es un método de Scala normal. El DSL de `build.sbt` utiliza una
macro para procesarlo fuera del cuerpo de la tarea. **Ambas tareas, `update`
y `clean`, ya han sido completadas en el momento en el que el motor
de tareas evalúa el cuerpo de `scalacOptions`, sin importar que esas líneas
aparezcan en el cuerpo.**

Mira el siguiente ejemplo:

```scala
ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "$example_scala_version$"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    scalacOptions := {
      val out = streams.value // la tarea streams sucede antes de scalacOptions
      val log = out.log
      log.info("123")
      val ur = update.value   // la tarea update sucede antes de scalacOptions
      log.info("456")
      ur.allConfigurations.take(3)
    }
  )
```

Después, desde el shell de sbt si se escribe `scalacOptions`:

```
> scalacOptions
[info] Updating {file:/xxx/}root...
[info] Resolving jline#jline;2.14.1 ...
[info] Done updating.
[info] 123
[info] 456
[success] Total time: 0 s, completed Jan 2, 2017 10:38:24 PM
```

Incluso aunque `val ur = ...` aparezca entre `log.info("123")` y
`log.info("456")` la evaluación de la tarea `update` se ha realizado antes de
tales líneas.

Aquí hay otro ejemplo:

```scala
ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "$example_scala_version$"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    scalacOptions := {
      val ur = update.value  // la tarea update sucede antes de scalacOptions
      if (false) {
        val x = clean.value  // la tarea clean sucede antes de scalacOptions
      }
      ur.allConfigurations.take(3)
    }
  )
```

Después, si desde el shell de sbt se lanza `run` y luego `scalacOptions`:

```
> run
[info] Updating {file:/xxx/}root...
[info] Resolving jline#jline;2.14.1 ...
[info] Done updating.
[info] Compiling 1 Scala source to /Users/eugene/work/quick-test/task-graph/target/scala-2.12/classes...
[info] Running example.Hello
hello
[success] Total time: 0 s, completed Jan 2, 2017 10:45:19 PM
> scalacOptions
[info] Updating {file:/xxx/}root...
[info] Resolving jline#jline;2.14.1 ...
[info] Done updating.
[success] Total time: 0 s, completed Jan 2, 2017 10:45:23 PM
```

Si compruebas `target/scala-2.12/classes/` verás que no existe ya que la tarea
`clean` ha sido ejecutado incluso si aparece dentro de `if (false)`.

Otra cosa importante a tener en cuenta es que no hay garantías sobre el orden
en el que las tareas `update` y `clean` son ejecutadas. Podría ejecutarse
primero `update` y luego `clean`, primero `clean` y luego `update` o ambas
ser ejecutadas en paralelo.

### Llamadas a .value en línea

Como se ha explicado anteriormente, `.value` es un método especial que es usado
para expresar dependencias de otras tareas y entradas.
Hasta que te familiarices con `build.sbt` te recomendamos que pongas todas las
llamadas `.value` al principio del cuerpo.

Sin embargo, a medida que te vayas sintiendo más cómodo, puedes optar por poner
dichas llamadas a `.value` en línea ya que puede hacer que la tarea/entrada sea
más concisa, sin tener que utilizar variables.

A continuación se muestran unos cuantos ejemplos de llamadas en línea.

```scala
scalacOptions := {
  val x = clean.value
  update.value.allConfigurations.take(3)
}
```

Fíjate en que, aunque las llamadas a `.value` estén en línea o en cualquier parte
del cuerpo de la tarea, siguen siendo evaluadas antes de entrar al cuerpo de
la tarea.

#### Inspeccionar la tarea

En el ejemplo anterior, `scalacOptions` tiene una *dependencia* de las tareas
`update` y `clean`. Si pones ese ejemplo en `build.sbt` y ejecutas la consola
interactiva de sbt y luego escribes `inspect scalacOptions` deberías de ver
algo similar (en parte):

```
> inspect scalacOptions
[info] Task: scala.collection.Seq[java.lang.String]
[info] Description:
[info]  Options for the Scala compiler.
....
[info] Dependencies:
[info]  *:clean
[info]  *:update
....
```

Así es cómo sbt sabe qué tareas dependen de otras.

Por ejemplo, si se lanza `inspect tree compile` verás que depende de otra clave
`incCompileSetup` que a su vez depende de otras claves como
`dependencyClasspath`.
Sigue recorriendo las dependencias y verás cómo ocurre la mágia.

```
> inspect tree compile
[info] compile:compile = Task[sbt.inc.Analysis]
[info]   +-compile:incCompileSetup = Task[sbt.Compiler\$IncSetup]
[info]   | +-*/*:skip = Task[Boolean]
[info]   | +-compile:compileAnalysisFilename = Task[java.lang.String]
[info]   | | +-*/*:crossPaths = true
[info]   | | +-{.}/*:scalaBinaryVersion = 2.12
[info]   | |
[info]   | +-*/*:compilerCache = Task[xsbti.compile.GlobalsCache]
[info]   | +-*/*:definesClass = Task[scala.Function1[java.io.File, scala.Function1[java.lang.String, Boolean]]]
[info]   | +-compile:dependencyClasspath = Task[scala.collection.Seq[sbt.Attributed[java.io.File]]]
[info]   | | +-compile:dependencyClasspath::streams = Task[sbt.std.TaskStreams[sbt.Init\$ScopedKey[_ <: Any]]]
[info]   | | | +-*/*:streamsManager = Task[sbt.std.Streams[sbt.Init\$ScopedKey[_ <: Any]]]
[info]   | | |
[info]   | | +-compile:externalDependencyClasspath = Task[scala.collection.Seq[sbt.Attributed[java.io.File]]]
[info]   | | | +-compile:externalDependencyClasspath::streams = Task[sbt.std.TaskStreams[sbt.Init\$ScopedKey[_ <: Any]]]
[info]   | | | | +-*/*:streamsManager = Task[sbt.std.Streams[sbt.Init\$ScopedKey[_ <: Any]]]
[info]   | | | |
[info]   | | | +-compile:managedClasspath = Task[scala.collection.Seq[sbt.Attributed[java.io.File]]]
[info]   | | | | +-compile:classpathConfiguration = Task[sbt.Configuration]
[info]   | | | | | +-compile:configuration = compile
[info]   | | | | | +-*/*:internalConfigurationMap = <function1>
[info]   | | | | | +-*:update = Task[sbt.UpdateReport]
[info]   | | | | |
....
```

Cuando escribes `compile` sbt automáticamente realiza un `update`, por ejemplo.
Esto funciona simplemente porque los valores de entrada requeridos por `compile`
necesitan que sbt lance un `update` primero.

De esta forma, todas las dependencias de construcción en sbt son *automáticas*
en lugar de tener que ser declaradas de forma explícita. Si usas el valor de
una clave en otra computación entonces la computación dependerá de dicha clave.

#### Definir una tarea que depende de otra configuración

`scalacOptions` es una clave tarea.
Supongamos que ya ha sido establecida a algún valor, pero que quieres filtrar
`"-Xfatal-warnings"` y `"-deprecation"` para las versiones distintas a la 2.12.

```scala
lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    organization := "com.example",
    scalaVersion := "$example_scala_version$",
    version := "0.1.0-SNAPSHOT",
    scalacOptions := List("-encoding", "utf8", "-Xfatal-warnings", "-deprecation", "-unchecked"),
    scalacOptions := {
      val old = scalacOptions.value
      scalaBinaryVersion.value match {
        case "2.12" => old
        case _      => old filterNot (Set("-Xfatal-warnings", "-deprecation").apply)
      }
    }
  )
```

A continuación se muestra cómo aparecería en el shell de sbt:

```
> show scalacOptions
[info] * -encoding
[info] * utf8
[info] * -Xfatal-warnings
[info] * -deprecation
[info] * -unchecked
[success] Total time: 0 s, completed Jan 2, 2017 11:44:44 PM
> ++2.11.8!
[info] Forcing Scala version to 2.11.8 on all projects.
[info] Reapplying settings...
[info] Set current project to Hello (in build file:/xxx/)
> show scalacOptions
[info] * -encoding
[info] * utf8
[info] * -unchecked
[success] Total time: 0 s, completed Jan 2, 2017 11:44:51 PM
```

Ahora, cojamos estas dos claves (desde [Keys](../../api/sbt/Keys\$.html)):

```scala
val scalacOptions = taskKey[Seq[String]]("Options for the Scala compiler.")
val checksums = settingKey[Seq[String]]("The list of checksums to generate and to verify for dependencies.")
```

**Nota**: `scalacOptions` y `checksums` no tienen nada que ver la una con la
otra. Simplemente son dos claves con el mismo tipo de valor, donde una es una
tarea.

Es posible compilar un `build.sbt` en donde `scalacOptions` hace referencia a
`checksums`, pero no en el sentido contrario.
Por ejemplo, lo siguiente está permitido:

```scala
// La tarea scalacOptions puede estar definida en
// terminos de la entrada checksums
scalacOptions := checksums.value
```

No hay forma de ir en la *otra* dirección. Es decir, una clave entrada no puede
depender de una clave tarea. Esto se debe a que una clave entrada es computada
una única vez cuando se carga el proyecto, por lo que una tarea no sería
re-ejecutada cada vez y las tareas esperan justamente lo contrario.

```scala
// Mal ejemplo: La entrada checksums no puede ser definida en términos de la
// tarea scalacOptions
checksums := scalacOptions.value
```

#### Definir una entrada que depende de otras entradas

En términos de ejecución, podemos pensar en las entradas como un tipo especial
de tarea que se evalúa durante la carga del proyecto.

Consideremos definir la organización del proyecto para que coincida con el
nombre del proyecto.

```scala
// nombramos nuestra organizacón basándonos en el proyecto
// (ambos son SettingKey[String])
organization := name.value
```

A continuación se muestra un ejemplo más realista.
Esto cambia el valor de la clave `Compile / scalaSource` a un directorio
diferente sólo cuando `scalaBinaryVersion` es `"2.11"`.

```scala
Compile / scalaSource := {
  val old = (Compile / scalaSource).value
  scalaBinaryVersion.value match {
    case "2.11" => baseDirectory.value / "src-2.11" / "main" / "scala"
    case _      => old
  }
}
```

### ¿Cuál es el propósito del DSL de build.sbt?

El DSL  de `build.sbt` es un lenguage específico del dominio utilizado para
construir un GAD de entradas y tareas. Las expresiones de la configuración
construyen entradas, tareas y las dependencias entre ellas.

Esta estructura es común a [Make][Make] (1976), [Ant][Ant] (2000) y
[Rake][Rake] (2003).

#### Introducción a Make

La sintáxis básica de un Makefile tiene este aspecto:

```
objetivo: dependencias
[tab] comando 1
[tab] comando 2
```

En un Makefile el primer objetivo que aparece listado corresponde al objetivo
predeterminado (por convenio se suele nombrar `all`).

1. Make comprueba si las dependencias del objetivo han sido construidas y
   construye cualesquiera dependencias que no hayan sido construídas aún.
2. Make ejecuta los comandos en orden.

Echemos un ojo a un `Makefile`:

```
CC=g++
CFLAGS=-Wall

all: hello

hello: main.o hello.o
    \$(CC) main.o hello.o -o hello

%.o: %.cpp
    \$(CC) \$(CFLAGS) -c \$< -o \$@
```

Al ejecutar `make` sin parámetros se coge el primer objetivo listado (por
convenio `all`).
El objetivo lista `hello` como su dependencia, la cual aún no ha sido
construida, por lo que Make construirá `hello`.

Después, Make comprueba si las dependencias del objetivo `hello` han sido
construidas. `hello` tiene dos objetivos: `main.o` y `hello.o`.
Una vez dichos objetivos han sido creados utilizando la última regla de
concordancia de patrones el comando de sistema es ejecutado para enlazar
`main.o` y `hello.o` en `hello`.

Cuando trabajas con `make` te puedes enfocar en qué objetivos quieres mientras
que Make calcula la secuencia y el orden exacto de comandos que necesitan ser
lanzados para construir los productos intermedios.
Se puede considerar como programación orientada a dependencias o
programanción basada en flujo. Make es en realidad considerado como un sistema
híbrido porque mientras que su DSL describe las dependencias entre tareas las
acciones son delegadas a comandos del sistema.

#### Rake

Este sistema híbrido es continuado por los sucesores de Make tales como Ant,
Rake y sbt.
Echemos un vistazo a la sintaxis básica para un Rakefile:

```ruby
task name: [:prereq1, :prereq2] do |t|
  # acciones (puede referenciar prerequisitos tales como t.name, etc...)
end
```

La innovación hecha por Rake fue que usaba un lenguage de programación para
describir las acciones en lugar de comandos del sistema.

#### Ventajas de la programación híbrida basada en flujo

Existen varias razones para organizar la construcción de esta forma.

La primera es evitar la duplicación. Con la programación basada en flujo una
tarea es ejecutada una única vez incluso cuando de ella dependen múltiples
tareas.
Por ejemplo, incluso cuando múltiples tareas a lo largo del grafo de tareas
dependen de `Compile / compile` la compilación será ejecutada exactamente una
única vez.

La segunda es la paralelización. Utilizando el grafo de tareas el motor
de tareas puede programar tareas mutuamente no dependientes en paralelo.

La tercera es la separación de cometidos y la flexibilidad.
El grafo de tareas permite al usuario cablear las tareas juntas de diferentes
formas, mientras que sbt y los plugins pueden proporcionar varias
características tales como compilación y gestión de dependencias de bibliotecas
como funciones que pueden ser reutilizadas.

### Resumen

La estructura central de las definiciones de construcción es un GAD de tareas
donde los vértices denotan relaciones "sucede antes de".
`build.sbt` es un DSL diseñado para expresar programación orientada a
dependencias o programación basada en flujo, similar a un `Makefile` o
`Rakefile`.

La razón clave para utilizar programación basada en flujo es evitar
duplicaciones, procesar en paralelo y la personalización.

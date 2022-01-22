---
out: sbt-by-example.html
---

  [Basic-Def]: Basic-Def.html
  [Setup]: Setup.html
  [Running]: Running.html
  [Essential-sbt]: https://www.scalawilliam.com/essential-sbt/

sbt mediante ejemplos
---------------------

Esta página supone que has [instalado sbt 1][Setup].

Empecemos mostrando algunos ejemplos en lugar de explicar cómo
o por qué sbt funciona.

### Crear una construcción sbt mínima

```
\$ mkdir foo-build
\$ cd foo-build
\$ touch build.sbt
```

### Iniciar el shell de sbt

```
\$ sbt
[info] Updated file /tmp/foo-build/project/build.properties: set sbt.version to 1.1.4
[info] Loading project definition from /tmp/foo-build/project
[info] Loading settings from build.sbt ...
[info] Set current project to foo-build (in build file:/tmp/foo-build/)
[info] sbt server started at local:///Users/eed3si9n/.sbt/1.0/server/abc4fb6c89985a00fd95/sock
sbt:foo-build>
```

### Salir del shell de sbt

Para salir del shell de sbt, escribe `exit` o pulsa Ctrl+D (Unix)
o Ctrl+Z (Windows).

```
sbt:foo-build> exit
```

### Compilar un proyecto

Como convención, usaremos el prompt `sbt:...>` o `>` para indicar
que estamos en un shell de sbt interactivo.

```
\$ sbt
sbt:foo-build> compile
```

### Recompilar cuando el código cambie

Si prefijas el comando `compile` (o cualquier otro comando) con `~` harás que
dicho comando sea re-ejecutado automáticamente en cuanto uno de los ficheros
fuente dentro del proyecto sea modificado. Por ejemplo:

```
sbt:foo-build> ~compile
[success] Total time: 0 s, completed May 6, 2018 3:52:08 PM
1. Waiting for source changes... (press enter to interrupt)
```

### Crear un fichero fuente

Deja el comando anterior ejecutándose.
Desde un shell diferente (o desde tu gestor de ficheros) crea la siguiente
estructura de directorios `src/main/scala/example` en el directorio del
proyecto. Después, crea el fichero `Hello.scala` en el directorio `example`
utilizando tu editor de texto favorito con este contenido:

```scala
package example

object Hello {
  def main(args: Array[String]): Unit = {
    println("Hello")
  }
}
```

Este nuevo fichero debería de ser detectado por el comando en ejecución:

```
[info] Compiling 1 Scala source to /tmp/foo-build/target/scala-2.12/classes ...
[info] Done compiling.
[success] Total time: 2 s, completed May 6, 2018 3:53:42 PM
2. Waiting for source changes... (press enter to interrupt)
```

Pulsa `Intro` para salir de `~compile`.

### Ejecutar un comando previo

Desde el shell de sbt, pulsa la tecla arriba dos veces para encontrar el comando
`compile` que habías ejecutado al principio.

```
sbt:foo-build> compile
```

### Obtener ayuda

Usa el comando `help` para obtener ayuda básica sobre los comandos disponibles.

```
sbt:foo-build> help

  about      Displays basic information about sbt and the build.
  tasks      Lists the tasks defined for the current project.
  settings   Lists the settings defined for the current project.
  reload     (Re)loads the current project or changes to plugins project or returns from it.
  new        Creates a new sbt build.
  projects   Lists the names of available projects or temporarily adds/removes extra builds to the session.
  project    Displays the current project or changes to the provided `project`.

....
```

### Mostrar la descripción de una tarea específica

```
sbt:foo-build> help run
Runs a main class, passing along arguments provided on the command line.
```

### Ejecutar tu aplicación

```
sbt:foo-build> run
[info] Packaging /tmp/foo-build/target/scala-2.12/foo-build_2.12-0.1.0-SNAPSHOT.jar ...
[info] Done packaging.
[info] Running example.Hello
Hello
[success] Total time: 1 s, completed May 6, 2018 4:10:44 PM
```

### Establecer `ThisBuild / scalaVersion` desde el shell de sbt

```
sbt:foo-build> set ThisBuild / scalaVersion := "$example_scala213$"
[info] Defining ThisBuild / scalaVersion
```

### Comprobar la entrada `scalaVersion`:

```
sbt:foo-build> scalaVersion
[info] $example_scala213$
```

### Guardar la sesión actual en build.sbt

Podemos guardar la configuración temporal utilizando `session save`.

```
sbt:foo-build> session save
[info] Reapplying settings...
```

El fichero `build.sbt` ahora debería de contener:

```scala
ThisBuild / scalaVersion := "$example_scala213$"
```

### Dar un nombre a tu proyecto

Utilizando un editor, modifica `build.sbt` con el siguiente contenido:

@@snip [name]($root$/src/sbt-test/ref/example-name/build.sbt) {}

### Recargar la construcción

Usa el comando `reload` para recargar la construcción.
El comando hace que el fichero `build.sbt` sea releído y su configuración
aplicada.

```
sbt:foo-build> reload
[info] Loading project definition from /tmp/foo-build/project
[info] Loading settings from build.sbt ...
[info] Set current project to Hello (in build file:/tmp/foo-build/)
sbt:Hello>
```

Fíjate en cómo el prompt ha cambiado a `sbt:Hello>`.

### Añadir ScalaTest a `libraryDependencies`

Utilizando un editor, modifica `build.sbt` de la siguiente manera:

@@snip [scalatest]($root$/src/sbt-test/ref/example-scalatest/build.sbt) {}

Usa el comando `reload` para reflejar los cambios de `build.sbt`.

```
sbt:Hello> reload
```

### Lanzar tests

```
sbt:Hello> test
```

### Lanzar tests incrementales continuamente

```
sbt:Hello> ~testQuick
```

### Escribir un test

Con el comando anterior ejecutándose, crea un fichero llamado
`src/test/scala/HelloSpec.scala` utilizando un editor:

@@snip [scalatest]($root$/src/sbt-test/ref/example-scalatest/src/test/scala/HelloSpec.scala) {}

`~testQuick` debería de coger el cambio:

```
2. Waiting for source changes... (press enter to interrupt)
[info] Compiling 1 Scala source to /tmp/foo-build/target/scala-2.12/test-classes ...
[info] Done compiling.
[info] HelloSpec:
[info] - Hello should start with H *** FAILED ***
[info]   assert("hello".startsWith("H"))
[info]          |       |          |
[info]          "hello" false      "H" (HelloSpec.scala:5)
[info] Run completed in 135 milliseconds.
[info] Total number of tests run: 1
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 0, failed 1, canceled 0, ignored 0, pending 0
[info] *** 1 TEST FAILED ***
[error] Failed tests:
[error]   HelloSpec
[error] (Test / testQuick) sbt.TestsFailedException: Tests unsuccessful
```

### Hacer que el test pase

Utilizando un editor, cambia `src/test/scala/HelloSpec.scala` a:

@@snip [scalatest]($root$/src/sbt-test/ref/example-scalatest/changes/HelloSpec.scala) {}

Confirma que el test pasa, luego pulsa `Intro` para salir del test continuo.

### Añadir una dependencia de biblioteca

Utilizando un editor, modifica `build.sbt` de esta forma:

@@snip [example-library]($root$/src/sbt-test/ref/example-library/build.sbt) {}

Usa el comando `reload` para reflejar los cambios de `build.sbt`.

### Usar el REPL de Scala

Podemos averiguar qué tiempo hace actualmente en Nueva York.

```scala
sbt:Hello> console
[info] Starting scala interpreter...
Welcome to Scala 2.12.7 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_171).
Type in expressions for evaluation. Or try :help.

scala> :paste
// Entering paste mode (ctrl-D to finish)

import scala.concurrent._, duration._
import gigahorse._, support.okhttp.Gigahorse
import play.api.libs.json._

Gigahorse.withHttp(Gigahorse.config) { http =>
  val baseUrl = "https://www.metaweather.com/api/location"
  val rLoc = Gigahorse.url(baseUrl + "/search/").get.
    addQueryString("query" -> "New York")
  val fLoc = http.run(rLoc, Gigahorse.asString)
  val loc = Await.result(fLoc, 10.seconds)
  val woeid = (Json.parse(loc) \\ 0 \\ "woeid").get
  val rWeather = Gigahorse.url(baseUrl + s"/\$woeid/").get
  val fWeather = http.run(rWeather, Gigahorse.asString)
  val weather = Await.result(fWeather, 10.seconds)
  ({Json.parse(_: String)} andThen Json.prettyPrint)(weather)
}

// press Ctrl+D

// Exiting paste mode, now interpreting.

import scala.concurrent._
import duration._
import gigahorse._
import support.okhttp.Gigahorse
import play.api.libs.json._
res0: String =
{
  "consolidated_weather" : [ {
    "id" : 6446939314847744,
    "weather_state_name" : "Light Rain",
    "weather_state_abbr" : "lr",
    "wind_direction_compass" : "WNW",
    "created" : "2019-02-21T04:39:47.747805Z",
    "applicable_date" : "2019-02-21",
    "min_temp" : 0.48000000000000004,
    "max_temp" : 7.84,
    "the_temp" : 2.1700000000000004,
    "wind_speed" : 5.996333145703094,
    "wind_direction" : 293.12257757287307,
    "air_pressure" : 1033.115,
    "humidity" : 77,
    "visibility" : 14.890539250775472,
    "predictability" : 75
  }, {
    "id" : 5806299509948416,
    "weather_state_name" : "Heavy Cloud",
...

scala> :q // para salir
```

### Crear un subproyecto

Cambia `build.sbt` como sigue:

@@snip [example-sub1]($root$/src/sbt-test/ref/example-sub1/build.sbt) {}

Usa el comando `reload` para reflejar los cambios de `build.sbt`.

### Listar todos los subproyectos

```
sbt:Hello> projects
[info] In file:/tmp/foo-build/
[info]   * hello
[info]     helloCore
```

### Compilar el subproyecto

```
sbt:Hello> helloCore/compile
```

### Añadir ScalaTest al subproyecto

Cambia `build.sbt` como sigue:

@@snip [example-sub2]($root$/src/sbt-test/ref/example-sub2/build.sbt) {}

### Difundir comandos

Añade aggregate para que el comando enviado a `hello` sea difundido también a `helloCore`:

@@snip [example-sub3]($root$/src/sbt-test/ref/example-sub3/build.sbt) {}

Tras un `reload`, `~testQuick` se ejecuta ahora en ambos subproyectos:

```scala
sbt:Hello> ~testQuick
```

Pulsa `Intro` para salir del test continuo.

### Hacer que hello dependa de helloCore

Usa `.dependsOn(...)` para añadir dependencias sobre otros subproyectos.
Además, movamos la dependencia de Gigahorse a `helloCore`.

@@snip [example-sub4]($root$/src/sbt-test/ref/example-sub4/build.sbt) {}

### Parsear JSON con Play JSON

Vamos a añadir Play JSON a `helloCore`.

@@snip [example-weather-build]($root$/src/sbt-test/ref/example-weather/build.sbt) {}

Tras un `reload`, añade `core/src/main/scala/example/core/Weather.scala`:

```scala
package example.core

import gigahorse._, support.okhttp.Gigahorse
import scala.concurrent._, duration._
import play.api.libs.json._

object Weather {
  lazy val http = Gigahorse.http(Gigahorse.config)

  def weather: Future[String] = {
    val baseUrl = "https://www.metaweather.com/api/location"
    val locUrl = baseUrl + "/search/"
    val weatherUrl = baseUrl + "/%s/"
    val rLoc = Gigahorse.url(locUrl).get.
      addQueryString("query" -> "New York")
    import ExecutionContext.Implicits.global
    for {
      loc <- http.run(rLoc, parse)
      woeid = (loc \\ 0  \\ "woeid").get
      rWeather = Gigahorse.url(weatherUrl format woeid).get
      weather <- http.run(rWeather, parse)
    } yield (weather \\\\ "weather_state_name")(0).as[String].toLowerCase
  }

  private def parse = Gigahorse.asString andThen Json.parse
}
```

Ahora, cambia `src/main/scala/example/Hello.scala` como sigue:

```scala
package example

import scala.concurrent._, duration._
import core.Weather

object Hello {
  def main(args: Array[String]): Unit = {
    val w = Await.result(Weather.weather, 10.seconds)
    println(s"Hello! The weather in New York is \$w.")
    Weather.http.close()
  }
}
```

Vamos a ejecutar la aplicación para ver si funciona:

```
sbt:Hello> run
[info] Compiling 1 Scala source to /tmp/foo-build/core/target/scala-2.12/classes ...
[info] Done compiling.
[info] Compiling 1 Scala source to /tmp/foo-build/target/scala-2.12/classes ...
[info] Packaging /tmp/foo-build/core/target/scala-2.12/hello-core_2.12-0.1.0-SNAPSHOT.jar ...
[info] Done packaging.
[info] Done compiling.
[info] Packaging /tmp/foo-build/target/scala-2.12/hello_2.12-0.1.0-SNAPSHOT.jar ...
[info] Done packaging.
[info] Running example.Hello
Hello! The weather in New York is mostly cloudy.
```

### Añadir el plugin sbt-native-packager

Utilizando un editor, crea `project/plugins.sbt`:

@@snip [example-weather-plugins]($root$/src/sbt-test/ref/example-weather/changes/plugins.sbt) {}

Después cambia `build.sbt` como sigue para añadir `JavaAppPackaging`:

@@snip [example-weather-build2]($root$/src/sbt-test/ref/example-weather/changes/build.sbt) {}

### Recargar y crear una distribución .zip

```
sbt:Hello> reload
...
sbt:Hello> dist
[info] Wrote /tmp/foo-build/target/scala-2.12/hello_2.12-0.1.0-SNAPSHOT.pom
[info] Wrote /tmp/foo-build/core/target/scala-2.12/hello-core_2.12-0.1.0-SNAPSHOT.pom
[info] Your package is ready in /tmp/foo-build/target/universal/hello-0.1.0-SNAPSHOT.zip
```

Así es cómo puedes ejecutar la app una vez empaquetada:

```
\$ /tmp/someother
\$ cd /tmp/someother
\$ unzip -o -d /tmp/someother /tmp/foo-build/target/universal/hello-0.1.0-SNAPSHOT.zip
\$ ./hello-0.1.0-SNAPSHOT/bin/hello
Hello! The weather in New York is mostly cloudy.
```

### Dockerizar tu app

```
sbt:Hello> Docker/publishLocal
....
[info] Successfully built b6ce1b6ab2c0
[info] Successfully tagged hello:0.1.0-SNAPSHOT
[info] Built image hello:0.1.0-SNAPSHOT
```

Así es cómo puedes ejecutar la app Dockerizada:

```
\$ docker run hello:0.1.0-SNAPSHOT
Hello! The weather in New York is mostly cloudy
```

### Establecer la versión

Cambia `build.sbt` como sigue:

@@snip [example-weather-build3]($root$/src/sbt-test/ref/example-weather/changes/build3.sbt) {}

### Cambiar `scalaVersion` temporalmente

```
sbt:Hello> ++2.12.14!
[info] Forcing Scala version to 2.12.14 on all projects.
[info] Reapplying settings...
[info] Set current project to Hello (in build file:/tmp/foo-build/)
```

Comprueba la entrada `scalaVersion`:

```
sbt:Hello> scalaVersion
[info] helloCore / scalaVersion
[info]  2.12.14
[info] scalaVersion
[info]  2.12.14
```

Esta entrada se esfumará tras un `reload`.

### Inspeccionar la tarea `dist`

Para saber más acerca de `dist`, prueba `help` e `inspect`.

```scala
sbt:Hello> help dist
Creates the distribution packages.
sbt:Hello> inspect dist
```
Para llamar a `inspect` recursivamente en las tareas dependientes utiliza `inspect tree`.

```scala
sbt:Hello> inspect tree dist
[info] dist = Task[java.io.File]
[info]   +-Universal / dist = Task[java.io.File]
....
```

### Modo por lotes

También puedes ejecutar sbt en por lotes, pasando comandos de sbt directamente
desde el terminal.

```
\$ sbt clean "testOnly HelloSpec"
```

**Note**: El modo por lotes implica levantar una JVM y JIT cada vez, por lo que
**la construcción será mucho más lenta**.
Para el desarrollo del día a día recomendamos utilizar el shell de sbt
o tests continuos como `~testQuick`.

### El comando `new`

Puedes utilizar el comando `new` para generar rápidamente una construcción
para un simple "Hola mundo".

```
\$ sbt new sbt/scala-seed.g8
....
A minimal Scala project.

name [My Something Project]: hello

Template applied in ./hello
```

Cuando se te pregunte por el nombre del proyecto escribe `hello`.

Esto creará un nuevo proyecto en un directorio llamado `hello`.

### Créditos

Esta página está basada en el tutorial [Essential sbt][essential-sbt]
escrito por William "Scala William" Narmontas.

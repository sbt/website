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
[info] Updated file /tmp/foo-build/project/build.properties: set sbt.version to 1.9.3
[info] welcome to sbt 1.9.3 (Eclipse Adoptium Java 17.0.8)
[info] Loading project definition from /tmp/foo-build/project
[info] loading settings for project foo-build from build.sbt ...
[info] Set current project to foo-build (in build file:/tmp/foo-build/)
[info] sbt server started at local:///Users/eed3si9n/.sbt/1.0/server/abc4fb6c89985a00fd95/sock
[info] started sbt server
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
[success] Total time: 0 s, completed 28 Jul 2023, 13:32:35
[info] 1. Monitoring source files for foo-build/compile...
[info]    Press <enter> to interrupt or '?' for more options.
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
[info] Build triggered by /tmp/foo-build/src/main/scala/example/Hello.scala. Running 'compile'.
[info] compiling 1 Scala source to /tmp/foo-build/target/scala-2.12/classes ...
[success] Total time: 0 s, completed 28 Jul 2023, 13:38:55
[info] 2. Monitoring source files for foo-build/compile...
[info]    Press <enter> to interrupt or '?' for more options.
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

<command> (; <command>)*                       Runs the provided semicolon-separated commands.
about                                          Displays basic information about sbt and the build.
tasks                                          Lists the tasks defined for the current project.
settings                                       Lists the settings defined for the current project.
reload                                         (Re)loads the current project or changes to plugins project or returns from it.
new                                            Creates a new sbt build.
new                                            Creates a new sbt build.
projects                                       Lists the names of available projects or temporarily adds/removes extra builds to the session.

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
[info] running example.Hello
Hello
[success] Total time: 0 s, completed 28 Jul 2023, 13:40:31
```

### Establecer `ThisBuild / scalaVersion` desde el shell de sbt

```
sbt:foo-build> set ThisBuild / scalaVersion := "$example_scala213$"
[info] Defining ThisBuild / scalaVersion
[info] The new value will be used by Compile / bspBuildTarget, Compile / dependencyTreeCrossProjectId and 50 others.
[info]  Run `last` for details.
[info] Reapplying settings...
[info] set current project to foo-build (in build file:/tmp/foo-build/)
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
[info] set current project to foo-build (in build file:/tmp/foo-build/)
[warn] build source files have changed
[warn] modified files:
[warn]   /tmp/foo-build/build.sbt
[warn] Apply these changes by running `reload`.
[warn] Automatically reload the build when source changes are detected by setting `Global / onChangedBuildSource := ReloadOnSourceChanges`.
[warn] Disable this warning by setting `Global / onChangedBuildSource := IgnoreSourceChanges`.
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
[info] welcome to sbt 1.9.3 (Eclipse Adoptium Java 17.0.8)
[info] loading project definition from /tmp/foo-build/project
[info] loading settings for project hello from build.sbt ...
[info] set current project to Hello (in build file:/tmp/foo-build/)
sbt:Hello>
```

Fíjate en cómo el prompt ha cambiado a `sbt:Hello>`.

### Añadir toolkit-test a `libraryDependencies`

Utilizando un editor, modifica `build.sbt` de la siguiente manera:

@@snip [scalatest]($root$/src/sbt-test/ref/example-test/build.sbt) {}

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
`src/test/scala/example/HelloSuite.scala` utilizando un editor:

@@snip [example-test]($root$/src/sbt-test/ref/example-test/src/test/scala/example/HelloSuite.scala) {}

`~testQuick` debería de coger el cambio:

```
[info] 2. Monitoring source files for hello/testQuick...
[info]    Press <enter> to interrupt or '?' for more options.
[info] Build triggered by /tmp/foo-build/src/test/scala/example/HelloSuite.scala. Running 'testQuick'.
[info] compiling 1 Scala source to /tmp/foo-build/target/scala-2.13/test-classes ...
HelloSuite:
==> X HelloSuite.Hello should start with H  0.004s munit.FailException: /tmp/foo-build/src/test/scala/example/HelloSuite.scala:4 assertion failed
3:  test("Hello should start with H") {
4:    assert("hello".startsWith("H"))
5:  }
at munit.FunSuite.assert(FunSuite.scala:11)
at HelloSuite.\$anonfun\$new\$1(HelloSuite.scala:4)
[error] Failed: Total 1, Failed 1, Errors 0, Passed 0
[error] Failed tests:
[error]         HelloSuite
[error] (Test / testQuick) sbt.TestsFailedException: Tests unsuccessful
```

### Hacer que el test pase

Utilizando un editor, cambia `src/test/scala/example/HelloSuite.scala` a:

@@snip [scalatest]($root$/src/sbt-test/ref/example-test/changes/HelloSuite.scala) {}

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
Welcome to Scala 2.13.11 (OpenJDK 64-Bit Server VM, Java 17).
Type in expressions for evaluation. Or try :help.

scala> :paste
// Entering paste mode (ctrl-D to finish)

import sttp.client4.quick._
import sttp.client4.Response

val newYorkLatitude: Double = 40.7143
val newYorkLongitude: Double = -74.006
val response: Response[String] = quickRequest
    .get(
        uri"https://api.open-meteo.com/v1/forecast?latitude=\$newYorkLatitude&longitude=\$newYorkLongitude&current_weather=true"
    )
    .send()

println(ujson.read(response.body).render(indent = 2))

// press Ctrl+D

// Exiting paste mode, now interpreting.

{
    "latitude": 40.710335,
    "longitude": -73.99307,
    "generationtime_ms": 0.36704540252685547,
    "utc_offset_seconds": 0,
    "timezone": "GMT",
    "timezone_abbreviation": "GMT",
    "elevation": 51,
    "current_weather": {
        "temperature": 21.3,
        "windspeed": 16.7,
        "winddirection": 205,
        "weathercode": 3,
        "is_day": 1,
        "time": "2023-08-04T10:00"
    }
}
import sttp.client4.quick._
import sttp.client4.Response
val newYorkLatitude: Double = 40.7143
val newYorkLongitude: Double = -74.006
val response: sttp.client4.Response[String] = Response({"latitude":40.710335,"longitude":-73.99307,"generationtime_ms":0.36704540252685547,"utc_offset_seconds":0,"timezone":"GMT","timezone_abbreviation":"GMT","elevation":51.0,"current_weather":{"temperature":21.3,"windspeed":16.7,"winddirection":205.0,"weathercode":3,"is_day":1,"time":"2023-08-04T10:00"}},200,,List(:status: 200, content-encoding: deflate, content-type: application/json; charset=utf-8, date: Fri, 04 Aug 2023 10:09:11 GMT),List(),RequestMetadata(GET,https://api.open-meteo.com/v1/forecast?latitude=40.7143&longitude...

scala> :q // to quit
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

### Añadir toolkit-test al subproyecto

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

### Parsear JSON con uJson

Vamos a añadir uJson de toolkit a `helloCore`.

@@snip [example-weather-build]($root$/src/sbt-test/ref/example-weather/build.sbt) {}

Tras un `reload`, añade `core/src/main/scala/example/core/Weather.scala`:

```scala
package example.core

import sttp.client4.quick._
import sttp.client4.Response

object Weather {
    def temp() = {
        val response: Response[String] = quickRequest
            .get(
                uri"https://api.open-meteo.com/v1/forecast?latitude=40.7143&longitude=-74.006&current_weather=true"
            )
            .send()
        val json = ujson.read(response.body)
        json.obj("current_weather")("temperature").num
    }
}
```

Ahora, cambia `src/main/scala/example/Hello.scala` como sigue:


```scala
package example

import example.core.Weather

object Hello {
    def main(args: Array[String]): Unit = {
        val temp = Weather.temp()
        println(s"Hello! The current temperature in New York is \$temp C.")
    }
}
```

Vamos a ejecutar la aplicación para ver si funciona:

```
sbt:Hello> run
[info] compiling 1 Scala source to /tmp/foo-build/core/target/scala-2.13/classes ...
[info] compiling 1 Scala source to /tmp/foo-build/target/scala-2.13/classes ...
[info] running example.Hello
Hello! The current temperature in New York is 22.7 C.
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
[info] Wrote /private/tmp/foo-build/target/scala-2.13/hello_2.13-0.1.0-SNAPSHOT.pom
[info] Main Scala API documentation to /tmp/foo-build/target/scala-2.13/api...
[info] Main Scala API documentation successful.
[info] Main Scala API documentation to /tmp/foo-build/core/target/scala-2.13/api...
[info] Wrote /tmp/foo-build/core/target/scala-2.13/hello-core_2.13-0.1.0-SNAPSHOT.pom
[info] Main Scala API documentation successful.
[success] All package validations passed
[info] Your package is ready in /tmp/foo-build/target/universal/hello-0.1.0-SNAPSHOT.zip
```

Así es cómo puedes ejecutar la app una vez empaquetada:

```
\$ /tmp/someother
\$ cd /tmp/someother
\$ unzip -o -d /tmp/someother /tmp/foo-build/target/universal/hello-0.1.0-SNAPSHOT.zip
\$ ./hello-0.1.0-SNAPSHOT/bin/hello
Hello! The current temperature in New York is 22.7 C.
```

### Dockerizar tu app

```
sbt:Hello> Docker/publishLocal
....
[info] Built image hello with tags [0.1.0-SNAPSHOT]
```

Así es cómo puedes ejecutar la app Dockerizada:

```
\$ docker run hello:0.1.0-SNAPSHOT
Hello! The current temperature in New York is 22.7 C.
```

### Establecer la versión

Cambia `build.sbt` como sigue:

@@snip [example-weather-build3]($root$/src/sbt-test/ref/example-weather/changes/build3.sbt) {}

### Cambiar `scalaVersion` temporalmente

```
sbt:Hello> ++3.3.0!
[info] Forcing Scala version to 3.3.0 on all projects.
[info] Reapplying settings...
[info] Set current project to Hello (in build file:/tmp/foo-build/)
```

Comprueba la entrada `scalaVersion`:

```
sbt:Hello> scalaVersion
[info] helloCore / scalaVersion
[info]  3.3.0
[info] scalaVersion
[info]  3.3.0
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
\$ sbt clean "testOnly HelloSuite"
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

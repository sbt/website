---
out: Organizing-Build.html
---

  [Basic-Def]: Basic-Def.html
  [Task-Graph]: Task-Graph.html
  [Using-Plugins]: Using-Plugins.html
  [Library-Dependencies]: Library-Dependencies.html
  [Multi-Project]: Multi-Project.html
  [Plugins]: ../docs/Plugins.html

Organizar la construcción
-------------------------

Esta página explica la organización de la estructura de la construcción.

Por favor, lee primero las páginas anteriores de la Guía de inicio, en particular [Definiciones de construcción][Basic-Def], [Grafos de tareas][Task-Graph],
[Dependencias de bibliotecas][Library-Dependencies] y
[Construcciones multiproyecto][Multi-Project] antes de leer esta página.

### sbt es recursivo

`build.sbt` esconde cómo trabaja en realidad sbt. Las construcciones de sbt son
definidas con código de Scala. Ese código, en sí mismo, ha de ser construido.
¿Qué mejor forma de hacerlo que con sbt?

El directorio `project` *es otra construcción dentro de tu construcción*, la
cual sabe cómo construir tu construcción. Para distinguir las construcciones a
veces utilizamos el término **construcción propia** para referirnos a tu
construcción y **metaconstrucción** para referirnos a la construcción que está
en `project`. Los proyectos dentro de la metaconstrucción pueden hacer lo que
cualquier otro proyecto puede.
*Tu definición de construcción es un proyecto sbt.*

Y así hasta el infinito. Si quieres, puedes personalizar la definición de
construcción de la definición de construcción del proyecto, creando un
directorio `project/project/`.

Aquí se explica con una ilustración.

```
hello/                     # el directorio base del proyecto raíz
                           # de tu construcción

    Hello.scala            # un fichero fuente en el proyecto raíz de tu
                           # construcción (también podría estar en
                           # src/main/scala)

    build.sbt              # build.sbt es parte del código fuente para el
                           # proyecto raíz de la metaconstrucción dentro de
                           # project/; la definición de construcción para
                           # tu construcción

    project/               # directorio base del proyecto raíz de la
                           # metaconstrucción

        Dependencies.scala # un fichero fuente en el proyecto raíz de la
                           # metaconstrucción, es decir, un fichero fuente en
                           # la definición de construcción de la definición
                           # de construcción para tu construcción

        assembly.sbt       # esto es parte del código fuente para el proyecto
                           # raíz de la meta-metaconstrucción en
                           # project/project; la definición de construcción de
                           # la definición de construcción

        project/           # directorio base del proyecto raíz de la
                           # meta-metaconstrucción; el proyecto de la definición
                           # de construcción para la definición de construcción

            MetaDeps.scala # fichero fuente en el proyecto raíz de la
                           # meta-metaconstrucción en project/project/
```

*¡No te preocupes!* La mayor parte del tiempo no necesitarás nada de eso. Pero
comprender los principios puede resultar útil.

Por cierto, cada vez que se utilizan ficheros acabados en `.scala` o `.sbt`,
el que se llamen `build.sbt` y `Dependencies.scala` son meras convenciones. Esto
significa que se permiten múltiples ficheros.

### Declarar dependencias en un único lugar

Una forma de aprovechar el hecho de que los ficheros `.scala` bajo `project`
acaban convirtiéndose en parte de la definición de construcción es crear
`project/Dependencies.scala` para declarar las dependencias en un único lugar.

```scala
import sbt._

object Dependencies {
  // Versions
  lazy val akkaVersion = "$example_akka_version$"

  // Libraries
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % akkaVersion
  val specs2core = "org.specs2" %% "specs2-core" % "$example_specs2_version$"

  // Projects
  val backendDeps =
    Seq(akkaActor, specs2core % Test)
}
```

El objeto `Dependencies` estará disponible en `build.sbt`. Para simplificar aún
más el poder usar los `val` definidos en él, puedes importar `Dependencies._` en
tu fichero `build.sbt`.

```scala
import Dependencies._

ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "$example_scala_version$"

lazy val backend = (project in file("backend"))
  .settings(
    name := "backend",
    libraryDependencies ++= backendDeps
  )
```

Esta técnica es útil cuando tienes construcciones multiproyecto que se empiezan
a volver inmanejables y quieres asegurar que las dependencias en los
subproyectos son consistentes.

### Cuándo usar ficheros `.scala`

En los ficheros `.scala` puedes escribir cualquier código de Scala, incluyendo
clases y objetos de primer nivel.

La solución recomendada es definir la mayoría de la configuración en un
fichero `build.sbt` de un multiproyecto y utilizar ficheros `project/*.scala`
para implementar tareas o compartir valores, tales como claves. El uso de
ficheros `.scala` también dependerá de cuán familiarizado estéis tú o tu equipo
con Scala.

### Definir autoplugins

Para usuarios más avanzados, otra forma de organizar tu construcción es definir
[autoplugins][Plugins] de usar y tirar en `project/*.scala`.
Al definir plugins disparados, los autoplugins pueden ser usados como una forma
conveniente de inyectar tareas y comandos personalizados a lo largo de los
subproyectos.

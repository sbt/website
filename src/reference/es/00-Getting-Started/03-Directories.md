---
out: Directories.html
---

  [ByExample]: sbt-by-example.html
  [Setup]: Setup.html
  [Organizing-Build]: Organizing-Build.html

Estructura de directorios
-------------------------

Esta página supone que has [instalado sbt][Setup] y leído
[sbt mediante ejemplos][ByExample].

### Directorio base

En la terminología de sbt, el "directorio base" es el directorio que contiene
el proyecto. Así pues, si has creado un proyecto `hello` que contiene
`/tmp/foo-build/build.sbt` tal y como se muestra en [sbt mediante ejemplos][ByExample],
el directorio base será `/tmp/foo-build`.

### Código fuente.

sbt emplea de forma predeterminada la misma estructura de directorios utilizado
por [Maven](https://maven.apache.org/) para ficheros fuente
(todas las rutas son relativas al directorio base):

```
src/
  main/
    resources/
       <ficheros del jar principal aquí>
    scala/
       <fuentes principales de Scala>
    scala-2.12/
       <main Scala 2.12 specific sources>
    java/
       <fuentes principales de Java>
  test/
    resources
       <ficheros del jar de test aquí>
    scala/
       <fuentes de test de Scala>
    scala-2.12/
       <test Scala 2.12 specific sources>
    java/
       <fuentes de test de Java>
```

Cualquier otro directorio en `src/` es ignorado.
También son ignorados todos los directorios ocultos.

El código fuente puede estar situado en el directorio base del proyecto como
`hello/app.scala`, lo cual puede estar bien para proyectos pequeños,
aunque para proyectos normales la gente suele estructurar los proyectos en el
directorio `src/main/` para tener las cosas ordenadas.

El hecho de que se permita código fuente del tipo `*.scala` en el directorio
base puede parecer algo raro, pero veremos que este hecho es relevante
[más adelante][Organizing-Build].

### Ficheros de definición de construcción

La definición de construcción es descrita en `build.sbt`
(en realidad cualquier fichero llamado `*.sbt`)
en el directorio base del proyecto.

```
build.sbt
```

### Ficheros auxiliares

Además de `build.sbt`, el directorio `project` puede contener ficheros `.scala`
que definen objetos auxiliares y plugins puntuales.

Para más información mira [Organizar la construcción][Organizing-Build].

```
build.sbt
project/
  Dependencies.scala
```

Puede que veas ficheros `.sbt` dentro de `project/`
pero no son equivalentes a los ficheros `.sbt` del directorio base del proyecto.
La explicación a esto vendrá [más adelante][Organizing-Build],
ya que primero necesitarás conocer algunos conceptos.

### Construir productos

Los ficheros generados (clases compiladas, jars empaquetados,
ficheros gestionados, caches y documentación) son escritos en el directorio
`target` de forma predeterminada.

### Configurar el control de versiones

Tu `.gitignore` (o el equivalente para otros sistemas de control de versiones)
debería de contener:

```
target/
```

Fíjate en que deliberadamente acaba con `/`
(para coincidir sólo con directorios) y que deliberadamente no empieza con `/`
(para coincidir con `project/target/` además del `target/` en la raíz).

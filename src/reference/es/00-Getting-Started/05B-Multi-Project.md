---
out: Multi-Project.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Directories]: Directories.html
  [Organizing-Build]: Organizing-Build.html

Construcciones multiproyecto
----------------------------

Esta página introduce múltiples subproyectos en una única construcción.

Por favor, lee primero las páginas anteriores de la Guía de inicio, en
particular necesitarás entender el fichero [build.sbt][Basic-Def] antes de leer
esta página.

### Múltiples subproyectos

Puede ser útil mantener múltiples subproyectos relacionados en una única
construcción, especialmente si unos dependen de otros y sueles modificarlos
todos a la vez.

Cada subproyecto en una construcción tiene sus propios directorios de código
fuente, genera su propio fichero jar cuando ejecutas `package` y en general
funcionan como cualquier otro proyecto.

Un proyecto se define declarando un `lazy val` de tipo
[Project](../../api/sbt/Project.html). Por ejemplo:

```scala
lazy val util = (project in file("util"))

lazy val core = (project in file("core"))
```

El nombre del `val` es usado como ID de subproyecto, el cual es usado para
referirse al subproyecto en el shell de sbt.

Opcionalmente,  el directorio base puede ser omitido si es el mismo que el
nombre del val.

```scala
lazy val util = project

lazy val core = project
```

#### Configuración común de construcción

Para extraer configuración común a lo largo de múltiples proyectos puedes
definir la configuración en el ámbito de `ThisBuild`.
Para ello la parte derecha tiene que ser un valor puro o una configuración en el
ámbito de `Global` o `ThisBuild` y no puede haber configuraciones
predeterminadas en el ámbito de subproyectos. (Ver [Ámbitos][Scopes])

```scala
ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "$example_scala_version$"

lazy val core = (project in file("core"))
  .settings(
    // otras configuraciones
  )

lazy val util = (project in file("util"))
  .settings(
    // otras configuraciones
  )
```

Ahora podemos incrementar `version` en un único lugar y se verá reflejado a lo
largo de los subproyectos cuando recargues la construcción.

#### Configuración común

Otra forma de extraer configuración común a lo largo de múltiples proyectos es
crear una secuencia llamada `commonSettings` y llamar al método `settings` en
cada proyecto.

```scala
lazy val commonSettings = Seq(
  target := { baseDirectory.value / "target2" }
)

lazy val core = (project in file("core"))
  .settings(
    commonSettings,
    // otras configuraciones
  )

lazy val util = (project in file("util"))
  .settings(
    commonSettings,
    // otras configuraciones
  )
```

### Dependencias

Los proyectos en la construcción pueden ser completamente independientes uno
de otro, pero por lo general estarán relacionados mediante algún tipo de
dependencia. Hay dos tipos de dependencias: agregadas y de classpath.

#### Agregación

Una agregación implica que al ejecutar una tarea en el proyecto que agrega también
será ejecutada en los proyectos agregados. Por ejemplo:

```scala
lazy val root = (project in file("."))
  .aggregate(util, core)

lazy val util = (project in file("util"))

lazy val core = (project in file("core"))
```

En el ejemplo de arriba, el proyecto raíz agrega `util` y `core`. Si ejecutas sbt
con dos subproyectos como los del ejemplo e intentas compilar podrás ver cómo
los tres proyectos son compilados.

*En el proyecto que hace la agregación*, el proyecto raíz en este caso, puedes
controlar la agregación a nivel de tarea. Por ejemplo, para evitar agregar la
tarea `update`:

```scala
lazy val root = (project in file("."))
  .aggregate(util, core)
  .settings(
    update / aggregate := false
  )

[...]
```

`update / aggregate` es la clave agregada en el ámbito de la tarea `update`
(ver [Ámbitos][Scopes]).

Nota: La agregación ejecutará las tareas agregadas en paralelo y sin orden
predeterminado entre ellas.

#### Dependencias de classpath

Un proyecto puede depender del código de otro proyecto. Esto se hace añadiendo
una llamada al método `dependsOn`. Por ejemplo, si `core` necesita `util` en su
classpath deberías de definir `core` así:

```scala
lazy val core = project.dependsOn(util)
```

A partir de ahora el código de `core` puede utilizar las clases de `util`. Esto
crea además un orden entre proyectos a la hora de compilarlos. `util` deberá ser
actualizado y compilado antes de que `core` pueda ser compilado.

Para depender de múltiples proyectos puedes utilizar múltiples argumentos en
`dependsOn`, como `dependsOn(bar, baz)`.

##### Dependencias de classpath por configuración

`core dependsOn(util)` implica que la configuración de `compile` en `core`
dependerá de la configuración de `compile` en `util`. Esto se puede escribir
de forma más explícita como `dependsOn(util % "compile->compile")`.

La `->` en `"compile->compile"` significa "depende de", por lo que
`"test->compile"` significa que la configuración de `test` en `core` depende
de la configuración de `compile` en `util`.

Omitir la parte de `->config` implica `->compile`, por lo que
`dependsOn(util % "test")` significa que la configuración de `test` en `core`
depende de la configuración de `Compile` en `util`.

Una declaración útil es `"test->test"` que significa que `test` depende de `test`.
Esto permite poner código auxiliar para testear en `util/src/test/scala` y luego
usar dicho código en `core/src/test/scala`, por ejemplo.

Puedes declarar múltiples configuraciones para una dependencia, separadas por
punto y coma. Por ejemplo, `dependsOn(util % "test->test;compile->compile")`.

### Dependencias inter-proyecto

En proyectos extremadamente grandes con muchos ficheros y muchos subproyectos,
el rendimiento de sbt puede ser menos óptimo al tener que observar qué ficheros
han cambiado en una sesión interactiva por tener que realizar muchas operaciones de E/S.

sbt posee las entradas `trackingInternalDependencies` y `exportToInternal`.
Éstas pueden ser utilizadas para controlar si la compilación de subproyectos
dependientes ha de ser lanzada automáticamente o no cuando se llama a `compile`.
Ambas claves pueden tomar uno de estos tres valores: `TrackLevel.NoTracking`,
`TrackLevel.TrackIfMissing` y `TrackLevel.TrackAlways`. De forma predeterminada
ambas son establecidas a `TrackLevel.TrackAlways`.

Cuando `trackInternalDependencies` es establecido a `TrackLevel.TrackIfMissing`,
sbt no volverá a intentar compilar dependencias internas (inter-proyecto)
automáticamente, a menos que no haya ficheros `*.class` (o un fichero JAR
cuando `exportJars` sea `true`) en el directorio de salida.

Cuando la entrada es establecida a `TrackLevel.NoTracking` la compilación de
dependencias internas es omitida. Fíjate en que el classpath aún sigue siendo anexado
y que el grafo de dependencias aún sigue mostrándolas como dependencias.
La razón es ahorrar el sobrecoste de E/S para observar cambios en una
construcción con muchos subproyectos durante el desarrollo. A continuación se
muestra cómo establecer todos los subproyectos a `TrackIfMissing`.

```scala
lazy val root = (project in file(".")).
  aggregate(....).
  settings(
    inThisBuild(Seq(
      trackInternalDependencies := TrackLevel.TrackIfMissing,
      exportJars := true
    ))
  )
```

La entrada `exportToInternal` permite al proyecto del cual se depende optar si
puede ser vigilado internamente o no, lo cual puede resultar útil si lo que se
quiere es hacer seguimiento de la mayoría de los subproyectos excepto unos
cuantos. La intersección de las entradas `trackInternalDependencies` y
`exportToInternal` será usada para determinar el nivel de seguimiento real.
A continuación se muestra un ejemplo de un proyecto optando de ser seguido o no:

```scala
lazy val dontTrackMe = (project in file("dontTrackMe")).
  settings(
    exportToInternal := TrackLevel.NoTracking
  )
```

### Proyecto raíz predeterminado

Si un proyecto no está definido para el directorio raíz en la construcción, sbt
creará uno de forma predeterminada que agrega a los otros proyectos de la
construcción.

Debido a que el proyecto `hello-foo` ha sido definido con `base = file("foo")`,
él estará contenido en el subdirectorio `foo`. Sus fuentes pueden estar tanto en
`foo`, como `foo/Foo.scala` o en `foo/src/main/scala`. La
[estructura de directorios][Directories] habitual se aplica a `foo` a excepción
de los ficheros de definición de construcción.

### Navegando por los proyectos interactivamente

En el prompt interactivo de sbt, escribe `proyectos` para listar tus proyectos y
`project <projectname>` para seleccionar el proyecto actual. Al ejecutar una
tarea como `compile` ésta se ejecutará sobre el proyecto actual. Por eso no hay
por qué compilar el proyecto raíz necesariamente, es posible compilar solamente
un subproyecto.

Puedes ejecutar una tarea en otro proyecto especificando explícitamente el ID de
proyecto, como en `subproyecto/compile`.

### Código común

Las definiciones en los ficheros `.sbt` no son visibles en otros ficheros
`.sbt`. Para poder compartir código entre ficheros `.sbt` hay que definir uno o
más ficheros de Scala en el directorio `project/` en la construcción raíz.

Para más información ver [Organizando la construcción][Organizing-Build].

### Appendix: Subproject build definition files

Cualquier fichero `.sbt` en `foo`, por ejemplo `foo/build.sbt`, será mezclado
con la definición de construcción para la construcción principal, pero con
ámbito del proyecto `hello-foo`.

Si todo tu proyecto está en `hello`, intenta definir una versión diferente
(`version := "0.6"`) en `hello/build.sbt`, `hello/foo/build.sbt`, y
`hello/bar/build.sbt`. Ahora `show version` en el prompt interactivo de sbt
debería de tener este aspecto (respetando las versiones que hayas definido):

```
> show version
[info] hello-foo/*:version
[info]  0.7
[info] hello-bar/*:version
[info]  0.9
[info] hello/*:version
[info]  0.5
```

`hello-foo/*:version` está definido en `hello/foo/build.sbt`,
`hello-bar/*:version` está definido en `hello/bar/build.sbt` y
`hello/*:version` está definido en `hello/build.sbt`. Recuerda la
[sintaxis para claves con ámbito][Scopes]. Cada clave `version` está en el
ámbito de un proyecto, basado en la ubicación de `build.sbt`. Pero los tres
`build.sbt` forman parte de la misma definición de construcción.

Style choices:

- Each subproject's settings can go into `*.sbt` files in the base directory of that project,
  while the root `build.sbt` declares only minimum project declarations in the form of `lazy val foo = (project in file("foo"))` without the settings.
- We recommend putting all project declarations and settings in the root `build.sbt` file
  in order to keep all build definition under a single file. However, it up to you.

No puedes tener un subdirectorio de proyecto o ficheros `project/*.scala` en los
subproyectos. `foo/project/Build.scala` sería ignorado.

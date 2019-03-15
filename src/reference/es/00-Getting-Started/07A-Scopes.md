---
out: Scopes.html
---

  [Basic-Def]: Basic-Def.html
  [Task-Graph]: Task-Graph.html
  [Library-Dependencies]: Library-Dependencies.html
  [Multi-Project]: Multi-Project.html
  [Inspecting-Settings]: ../docs/Inspecting-Settings.html
  [Scope-Delegation]: Scope-Delegation.html

Ámbitos
-------

Esta página explica los ámbitos. Se supone que has leído y entendido las
páginas anteriores, [Definiciones de construcción][Basic-Def] y
[Grafos de tareas][Task-Graph].

### Toda la historia sobre claves

[Anteriormente][Basic-Def] fingimos que una clave tal como `name` correspondía
a una única entrada en el mapa de pares clave-valor de sbt. Esto fue una
simplificación.

En realidad cada clave puede tener un valor asociado en más de un contexto,
llamado *ámbito*.

Algunos ejemplos concretos:

- si tienes múltiples proyectos (también llamados subproyectos) en tu definición
  de construcción una clave puede tener diferentes valores en cada proyecto.
- la clave `compile` puede tener un valor para tus fuentes principales y otro
  diferente para tus ficheros de test, si quisieras compilarlos de forma
  distinta.
- la clave `packageOptions` (que contiene opciones para crear paquetes jar)
  puede tener diferentes valores cuando se empaquetan ficheros de clases
  (`packageBin`) o paquetes de código fuente (`packageSrc`).

*No existe un único valor para una clave `name` data*, porque el valor puede
diferir según el ámbito.

Sin embargo, existe un único valor para una cierta clave en un ámbito.

Si piensas en sbt cuando procesa una lista de entradas para generar un mapa
de clave-valor que describan un proyecto, tal y como se
[explicó antes][Basic-Def], las claves en ese mapa de clave-valor son claves
con *ámbito*. Cada entrada definida en la definición de construcción (por
ejemplo en `build.sbt`) se aplica también a una clave con ámbito.

Frecuentemente el ámbito está implícito o existe uno predeterminado, pero si
el ámbito predeterminado no es el que te interesa deberás mencionar
explícitamente el ámbito que deseas en `build.sbt`.

### Ejes de ámbito

Un *eje de ámbito* es un constructor de tipo similar a `Option[A]` que es usado
para formar un componente en un ámbito.

Existen tres ejes de ámbito:

- El eje de subproyecto
- El eje de configuración
- El eje de tareas

Si el concepto de *eje* no te resulta familiar podemos pensar en un cubo de
color RGB como ejemplo:

![color cube](../files/rgb_color_solid_cube.png)

En el modelo de color RGB todos los colores son representados por un punto en el
cubo cuyos ejes corresponden a las componentes rojo, verde y azul codificadas
por un número. De forma similar un ámbito total en sbt está formado por el valor
de la **tupla** de un subproyecto, una configuración y un tarea:

```scala
projA / Compile / console / scalacOptions
```

Que es la sintaxis de barra, introducida en sbt 1.1, equivalente a:

```scala
scalacOptions in (
  Select(projA: Reference),
  Select(Compile: ConfigKey),
  Select(console.key)
)
```

#### Ámbito con el eje de subproyecto

Si [pones múltiples proyectos en una única construcción][Multi-Project], cada
proyecto necesitará su propia configuración. Es decir, las claves pueden tener
un ámbito u otro de acuerdo al proyecto.

El eje de proyecto puede también ser establecido a `ThisBuild`, que quiere decir
la "construcción entera" por lo que una entrada se aplica a toda la construcción
en lugar de a un único proyecto.

La configuración a nivel de construcción es frecuentemente utilizada como
configuración predeterminada cuando un proyecto no define una entrada
específica. Explicaremos configuraciones a nivel de construcción más adelante en
esta página.

#### Ámbito con el eje de configuración

Una *configuración de dependencia* (o simplemente "configuración") define un
grafo de dependencias de bibliotecas, potencialmente con su propio classpath,
ficheros fuentes, paquetes generados, etc... El concepto de configuración de
dependencia proviene de Ivy, el cual es usado por sbt para gestionar
[dependencias][Library-Dependencies] y de los [ámbitos de Maven](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Scope).

Algunas configuraciones que verás en sbt:

- `Compile` que define la construcción de los ficheros principales
  (`src/main/scala`).
- `Test` que define cómo construir los tests (`src/test/scala`).
- `Runtime` que define el classpath para la tarea `run`.

De forma predeterminada, todas las claves asociadas a la compilación,
empaquetado y ejecución tienen como ámbito una configuración y por tanto pueden
funcionar de forma diferente en cada configuración. Los ejemplos más ovios son
las claves tarea `compile`, `package` y `run`, y todas las claves que afectan
a dichas claves (tales como `sourceDirectories`, `scalacOptions` o
`fullClasspath`) también tienen una configuración como ámbito.

Otra cosa a tener en cuenta sobre una configuración es que puede extender otras
configuraciones. La siguiente figura muestra la relación de extensión entre las
configuraciones más comunes.

![dependency configurations](../files/sbt-configurations.png)

`Test` y `IntegrationTest` extienden `Runtime`; `Runtime` extiende `Compile`;
`CompileInternal` extiende `Compile`, `Optional`, y `Provided`.

#### Ámbito con el eje de tarea

La configuración puede afectar a cómo funciona una tarea. Por ejemplo, la tarea
`packageSrc` es afectada por la entrada `packageOptions`.

Para soportar esto, una clave tarea (tal como `packageSrc`) puede tener un
ámbito para otra clave (tal como `packageOptions`).

Las distintas tareas que construyen un paquete (`packageSrc`, `packageBin`,
`packageDoc`) pueden compartir claves relacionadas con el empaquetado, tales
como `artifactName` y `packageOptions` Esas claves pueden tener distintos
valores para cada tarea de empaquetado.

#### Componentes con ámbito `Zero`

Cada eje de ámbito puede ser rellenado tanto con una instancia del tipo del eje
(análogamente a como ocurre con `Some(_)`), o con el valor especial `Zero`.
Podemos pensar en `Zero` como `None`.

`Zero` es un comodín universal para todos los ejes de ámbito pero su uso directo
debería de estar reservado para sbt y, en todo caso, para los autores de
plugins.

`Global` es un ámbito que establece `Zero` para todos los ejes:
`Zero / Zero / Zero`. En otras palabras, `Global / clave` es un atajo para
`Zero / Zero / Zero / clave`.

### Referenciar ámbitos en la definición de construcción

Si creas una entrada en `build.sbt` con una clave plana entonces tendrá como ámbito
(subproyecto actual / configuración `Zero` / tarea `Zero`):

```scala
lazy val root = (project in file("."))
  .settings(
    name := "hello"
  )
```

Si ejecutas sbt y lanzas `inspect name` podrás comprobar que es proporcionada por
`ProjectRef(uri("file:/private/tmp/hello/"), "root") / name`, es decir,
el proyecto es `ProjectRef(uri("file:/Users/xxx/hello/"), "root")` y ni el
ámbito de la configuración ni el de la tarea son mostrados (lo que significa
que son `Zero`).

Una clave plana a la derecha también tiene como ámbito
(subproyecto actual / configuración `Zero` / tarea `Zero`):

@@snip [build.sbt]($root$/src/sbt-test/ref/scopes/build.sbt) { #unscoped }

Los tipos de cualesquiera de los ejes de ámbito están extendidos para tener un
operador `/`. El argumento de `/` puede ser una clave u otro eje de ámbito. Así
que por ejemplo, aunque no hay ninguna razón de peso para hacer lo siguiente,
se podría tener una instancia de la clave `name` en el ámbito de la
configuración `Compile`:

@@snip [build.sbt]($root$/src/sbt-test/ref/scopes/build.sbt) { #confScoped }

o podrías establecer `name` en el ámbito de la tarea `packageBin` (algo inútil,
sólo es un ejemplo).

@@snip [build.sbt]($root$/src/sbt-test/ref/scopes/build.sbt) { #taskScoped }

o podrías establecer `name` con múltiples ejes de ámbito, por ejemplo con la
tarea `packageBin` en la configuración `Compile`:

@@snip [build.sbt]($root$/src/sbt-test/ref/scopes/build.sbt) { #confAndTaskScoped }

o puedes utilizar `Global`:

@@snip [build.sbt]($root$/src/sbt-test/ref/scopes/build.sbt) { #global }

(`Global / concurrentRestrictions` es convertido implícitamente a 
`Zero / Zero / Zero / concurrentRestrictions`, estableciendo todos los ejes a
ámbito `Zero`. De forma predeterminada las tareas y las configuraciones ya son
`Zero` de forma predeterminada por lo que la única utilidad de emplear esto es
la de establecer el proyecto a `Zero` en lugar de a
`ProjectRef(uri("file:/tmp/hello/"), "root") / Zero / Zero / concurrentRestrictions`)

### Referenciar ámbitos desde el shell de sbt

En la línea de comandos y en el shell de sbt, sbt muestra (y analiza) claves
con ámbito como esta:

```
ref / Config / intask / key
```

- `ref` identifica el eje de subproyecto. Puede ser `<project-id>`,
  `ProjectRef(uri("file:..."), "id")`, o `ThisBuild` para denotar el ámbito de
  la construcción entera.
- `Config` identifica el eje de configuración utilizando el identificador de
  Scala empezando por mayúscula.
- `intask` identifica el eje de tarea.
- `key` identifica la clave a la que se le aplica el ámbito.

`Zero` puede aparecer en cada eje.

Si se omiten partes del ámbito de la clave, éstas serán inferidas siguiendo las
siguientes reglas:

- el proyecto actual será utilizado si el proyecto es omitido
- la configuración dependiente de la clave será autodetectada si se omite la
  configuración o la tarea.

Para más información ver [Interactuar con el sistema de configuración][Inspecting-Settings].

### Ejemplos de la notación de claves con ámbito

- `fullClasspath` especifica simplemente una clave, por lo que los ámbitos
  predeterminados son utilizados: proyecto actual, una configuración dependiente
  de la clave y el ámbito de tarea `Zero`.
- `Test / fullClasspath` emplea una configuración, por lo que es `fullClasspath`
  en la configuración `Test`, con valores predeterminados para el resto de ejes.
- `root / fullClasspath` especifica el proyecto `root`, donde el proyecto es
  identificado por el identificador de proyecto.
- `root / Zero / fullClasspath` especifica el proyecto `root` y `Zero` para la
  configuración, en lugar de la predeterminada.
- `doc / fullClasspath` especifica la clave `fullClasspath` en el ámbito de la
  tarea `doc`, con los valores predeterminados para el eje del proyecto y el de
  la configuración.
- `ProjectRef(uri("file:/tmp/hello/"), "root") / Test / fullClasspath`
  especifica el proyecto `ProjectRef(uri("file:/tmp/hello/"), "root")`.
  Además especifca la configuración `Test`, dejando el eje de tarea al valor
  predeterminado.
- `ThisBuild / version` establece el eje de subproyecto a la "construcción
  entera" donde la construcción es `ThisBuild`, con la configuración
  predeterminada.
- `Zero / fullClasspath` establece el eje de subproyecto a `Zero`,
  con la configuración predeterminada.
- `root / Compile / doc / fullClasspath` establece todos los ejes de ámbito.

### Inspeccionar ámbitos

En el shell de sbt, puedes utilizar el comando `inspect` para comprender las
claves y sus ámbitos. Prueba `inspect test / fullClasspath`:

```
\$ sbt
sbt:Hello> inspect Test / fullClasspath
[info] Task: scala.collection.Seq[sbt.internal.util.Attributed[java.io.File]]
[info] Description:
[info]  The exported classpath, consisting of build products and unmanaged and managed, internal and external dependencies.
[info] Provided by:
[info]  ProjectRef(uri("file:/tmp/hello/"), "root") / Test / fullClasspath
[info] Defined at:
[info]  (sbt.Classpaths.classpaths) Defaults.scala:1639
[info] Dependencies:
[info]  Test / dependencyClasspath
[info]  Test / exportedProducts
[info]  Test / fullClasspath / streams
[info] Reverse dependencies:
[info]  Test / testLoader
[info] Delegates:
[info]  Test / fullClasspath
[info]  Runtime / fullClasspath
[info]  Compile / fullClasspath
[info]  fullClasspath
[info]  ThisBuild / Test / fullClasspath
[info]  ThisBuild / Runtime / fullClasspath
[info]  ThisBuild / Compile / fullClasspath
[info]  ThisBuild / fullClasspath
[info]  Zero / Test / fullClasspath
[info]  Zero / Runtime / fullClasspath
[info]  Zero / Compile / fullClasspath
[info]  Global / fullClasspath
[info] Related:
[info]  Compile / fullClasspath
[info]  Runtime / fullClasspath
```

En la primera línea, se puede apreciar que esta es una tarea (y no una entrada,
tal y como se explica en [Definiciones de construcción][Basic-Def]).
El valor resultante de la tarea es del tipo
`scala.collection.Seq[sbt.Attributed[java.io.File]]`.

"Provided by" indica la clave con ámbito que define el valor, en este caso
`ProjectRef(uri("file:/tmp/hello/"), "root") / Test / fullClasspath` (que es la
clave `fullClasspath` en el ámbito de la configuración `Test` y el proyecto
`ProjectRef(uri("file:/tmp/hello/"), "root")` project).

"Dependencies" fue explicado en detalle en la [página anterior][Task-Graph].

"Delegates" será explicado más adelante.

Si ejecutas `inspect fullClasspath` (en oposición al ejemplo de arriba,
inspect `Test / fullClasspath`) podrás apreciar la diferencia. Debido a que la
configuración es omitida, es autodetectada como `Compile`.
`inspect Compile / fullClasspath` debería por tanto ser lo mismo que
`inspect fullClasspath`.

Si ejecutas `inspect ThisBuild / Zero / fullClasspath` podrás obtener otro ejemplo.
De forma predeterminada `fullClasspath` no está definido en la ámbito de la
configuración `Zero`.

Una vez más, para más información ver
[Interactuar con el sistema de configuración][Inspecting-Settings].

### Cuándo especificar un ámbito

Un ámbito necesita ser especificado si la clave en cuestión ya está asociada
a otro ámbito.
Por ejemplo, la tarea `compile`, de forma predeterminada, tiene como ámbito
las configuraciones `Compile` y `Test` y no existe fuera de dichos ámbitos.

Para cambiar el valor asociado con la clave `compile` necesitas escribir
`Compile / compile` o `Test / compile`. Utilizar solamente `compile` definiría
una nueva tarea `compile` en el ámbito del proyecto actual, en lugar de
sobrescribir la tarea de compilación estándar, la cual tiene como ámbito una
configuración.

Si obtienes un error como *"Reference to undefined setting"*, con frecuencia
significará que no has especificado un ámbito, o que has especificado el ámbito
equivocado. La clave que estás utilizando puede estar definida en otro ámbito.
sbt intentará sugerir lo que querías decir como parte del mensaje de error.
Busca cosas tipo "Did you mean Compile / compile?"

Una forma de pensar en esto es que un nombre es solo una *parte* de una clave.
En realidad, todas las claves consisten tanto en un nombre como en un ámbito
(donde el ámbito tiene tres ejes).
La expresión completa `Compile / packageBin / packageOptions` es un nombre de
clave, dicho de otra forma.
`packageOptions` a secas también es un nombre de clave, pero uno diferente (uno
donde los ámbitos son implícitamente establecidos: proyecto actual,
configuración `Zero` y tarea `Zero`).

### Configuración a nivel de construcción

Una técnica avanzada para extraer configuración común a todos los subproyectos
es definir valores en el ámbito de `ThisBuild`.

Si una clave que tiene como ámbito un subproyecto en particular no se encuentra,
sbt la buscará en el ámbito de `ThisBuild`.
Usando este mecanismo, podemos definir valores predeterminados a nivel de
construcción para claves usadas con frecuencia tales como `version`,
`scalaVersion` y `organization`.

```scala
ThisBuild / organization := "com.example",
ThisBuild / scalaVersion := "$example_scala_version$",
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    publish / skip := true
  )

lazy val core = (project in file("core"))
  .settings(
    // other settings
  )

lazy val util = (project in file("util"))
  .settings(
    // other settings
  )
```

Convenientemente, existe una función `inThisBuild(...)` que definirá tanto
la clave como el valor al ámbito de `ThisBuild`. Definir valores aquí sería
equivalente a prefijar cada uno con `ThisBuild /` allá donde fuera posible.

Debido a la naturaleza de la [delegación de ámbito][Scope-Delegation] que
explicaremos más adelante, la configuración a nivel de construcción debería ser
utilizada sólo para valores puros o para valores en el ámbito de `Global` o
`ThisBuild`.

### Delegación de ámbito

Una clave con ámbito puede no haber sido definida, si no tiene un valor asociado
en su ámbito.

Para cada eje de ámbito, sbt tiene un camino de búsqueda alternativo consistente
en otros valores con ámbito. Habitualmente, si una clave no tiene asociado un
valor en un ámbito específico, sbt intentará obtener un valor de un ámbito más
general, tal como el ámbito `ThisBuild`.

Esta característica te permitirá establecer un valor una única vez en un
ámbito general, permitiendo múltiples ámbitos específicos que heredan el valor.
Lo discutiremos con mas detalle más tarde en
[Delegación de ámbito][Scope-Delegation].

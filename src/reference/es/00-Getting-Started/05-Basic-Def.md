---
out: Basic-Def.html
---

  [Task-Graph]: Task-Graph.html
  [Bare-Def]: Bare-Def.html
  [Full-Def]: Full-Def.html
  [Running]: Running.html
  [Library-Dependencies]: Library-Dependencies.html
  [Input-Tasks]: ../docs/Input-Tasks.html

Definiciones de construcción
----------------------------

Esta página describe las definiciones de construcción (build definitions), 
incluyendo algo de "teoría" y la sintaxis de `build.sbt`. Se supone que has 
instalado una versión reciente de sbt, como sbt $app_version$, que sabes cómo 
[usar sbt][Running] y que has leído las páginas anteriores de la Guía de inicio.

Esta página explica la definición de construcción de `build.sbt`.

### Especificar la versión de sbt

Como parte de tu definición de construcción debes de especificar la versión de 
sbt que tu construcción utiliza.

Esto permitirá a la gente con diferentes versiones del lanzador de sbt 
construir los mismos proyectos con resultados consistentes. Para hacer esto, 
crea un fichero llamado `project/build.properties` en el que se especifica la 
versión de sbt como sigue:

```
sbt.version=$app_version$
```

Si la versión requerida no está disponible localmente, el lanzador `sbt` se la 
descargará por ti. Si este fichero no está presente, el lanzador `sbt` eligirá 
una versión arbitraria, lo cual no es aconsejable debido a que hará que tu 
construcción no sea portable.

### ¿Qué es una definición de construcción?

Una *definición de construcción* es definida en `build.sbt` y consiste en un 
conjunto de proyectos (de tipo [`Project`](../../api/sbt/Project.html)). Debido a 
que el término *project* puede ser ambiguo, con frecuencia utilizaremos el *subproyecto* para referirnos a ellos en esta guía.

Por ejemplo, en `build.sbt` se define el subproyecto ubicado en el directorio 
actual así:

@@snip [build.sbt]($root$/src/sbt-test/ref/basic/build.sbt) {}

Cada subproyecto es configurado como pares clave-valor.

Por ejemplo, una clave es `name` y se mapea a una cadena de texto, el nombre 
de tu subproyecto. Los pares clave-valor se listan bajo el método 
`.settings(...)` de tal forma:

@@snip [build.sbt]($root$/src/sbt-test/ref/basic/build.sbt) {}

### Cómo define build.sbt configuraciones

`build.sbt` define subproyectos, los cuales contienen una secuencia de pares 
clave-valor llamados *expresiones de configuración* (expression settings) 
utilizando un *DSL de build.sbt*

```scala
ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "$example_scala_version$"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "hello"
  )
```

Echemos un vistazo más de cerca al DSL de build.sbt:

![setting expression](../files/setting-expression.png)<br>
<br>

Cada entrada es llamada una *expresion de configuración*. De entre ellas, 
algunas son también llamadas expresiones de tareas. Veremos la diferencia entre 
ellas más tarde en esta página.

Una expresión de configuración consiste en tres partes:

1. La parte izquierda, que es una *clave*.
2. Un *operador*, que en este caso es `:=`
3. La parte derecha es llamada *cuerpo* o *cuerpo de configuración*.

A la izquierda, `name`, `version` y `scalaVersion` son *claves*. Una clave es 
una instancia de
[`SettingKey[T]`](../../api/sbt/SettingKey.html),
[`TaskKey[T]`](../../api/sbt/TaskKey.html) o
[`InputKey[T]`](../../api/sbt/InputKey.html)
donde `T` es el tipo esperado. Los tipos de claves son explicados más abajo.

Debido a que la clave `name` tiene tipo `SettingsKey[String]`, el operador `:=` 
aplicado a `name` también está tipado como `String`. Si usas el tipo incorrecto 
la definición de construcción no compilará:

```scala
lazy val root = (project in file("."))
  .settings(
    name := 42  // no compila
  )
```

En `build.sbt` también se pueden entremezclar `val`, `lazy val` y `def`. Los 
`object` y `class` de primer nivel no están permitidos en `build.sbt`. De 
hacerlo deberían ir en el directorio `project/` como ficheros fuente de Scala.

### Claves

#### Tipos

Hay tres sabores de claves:

- `SettingKey[T]`: una clave para un valor calculado una única vez (el valor
  es calculado cuando se carga un subproyecto)
- `TaskKey[T]`: una clave para un valor, llamado una *tarea* que ha de ser
  recalculado cada vez, potencialmente con efectos colaterales.
- `InputKey[T]`: una clave para una tarea con argumentos de línea de comandos
  como entrada. Para más información mira [Tareas de entrada][Input-Tasks].

#### Claves preconfiguradas

Las claves preconfiguradas son simplemente campos de un objeto llamado 
[Keys](../../api/sbt/Keys\$.html). Un `build.sbt` tiene de forma implícita un 
`import sbt.Keys._`, por lo que `sbt.Keys.name` puede ser accedido como `name`.

#### Claves personalizadas

Las claves personalizadas pueden ser definidas con sus respectivos métodos de 
creación: `settingKey`, `taskKey` e `inputKey`. Cada método espera el tipo del valor 
asociado con la clave además de su descripción. El nombre de la clave es tomado 
del `val` al cual la clave es asignada. Por ejemplo, para definir una clave para 
una nueva tarea llamada `hello`,

```scala
lazy val hello = taskKey[Unit]("An example task")
```

Aquí hemos usado el hecho de que un fichero `.sbt` puede contener `val` y `def` 
además de configuración. Todas estas definiciones son evaluadas antes de la 
configuración sin importar en qué lugar del fichero han sido definidas.

> **Nota:** Tipicamente, se usan `lazy val` en lugar de `val` para evitar problemas de orden
> durante la inicialización.

#### Claves de tarea vs claves de entradas

Se dice de `TaskKey[T]` que define una *tarea*. Las tareas son operaciones 
tales como `compile` o `package`. A su vez pueden devolver `Unit` (`Unit` es el 
tipo de Scala para `void`) o pueden devolver un valor relacionado con la tarea, 
por ejemplo el de `package` es un `TaskKey[File]` y su valor es el fichero jar que 
crea.

Cada vez que inicias la ejecución de una tarea, por ejemplo escribiendo 
`compile` en el prompt interactivo de sbt, sbt volverá a ejecutar cualesquiera 
tareas implicadas exactamente una vez.

Los pares clave-valor de sbt que describen al subproyecto pueden ser 
almacenados en forma de cadena fija para configuraciones tales como `name`, 
pero tienen que poder guardar código ejecutable para tareas tales como 
`compile`. Incluso si dicho código ejecutable acaba devolviendo una cadena, 
tiene que ser ejecutado cada vez.

*Una cierta clave siempre se refiere o bien a una tarea o a una entrada plana.* 
El que tenga que ser ejecutada cada vez o no es una propiedad de la clave, no 
del
valor.

### Definir tareas y entradas de configuración

Utilizando `:=` se puede asignar un valor a una entrada y una computación a una 
tarea. Para una entrada, el valor será computado una única vez durante la carga 
del proyecto. Para una tarea, la computación será evaluada cada vez que la 
tarea sea ejecutada.

Por ejemplo, para implementar la tarea `hello` de la sección anterior:

```scala
lazy val hello = taskKey[Unit]("An example task")

lazy val root = (project in file("."))
  .settings(
    hello := { println("Hello!") }
  )
```

Ya vimos un ejemplo sobre cómo definir entradas cuando definimos el nombre del 
proyecto:

```scala
lazy val root = (project in file("."))
  .settings(
    name := "hello"
  )
```

#### Tipos para tareas y entradas

Desde una perspectiva del sistema de tipos, el `Setting` creado a partir de una 
clave tarea es ligeramente diferente de la creada para una clave entrada. 
`taskKey := 42` resulta en `Setting[Task[T]]` mientras que `settingKey := 42` 
resulta en `Setting[T]`. En la mayoría de los casos esto no supone ninguna 
diferencia; la clave tarea aún sigue creando un valor de tipo `T` cuando la 
tarea es ejecutada.

La diferencia de tipos entre `T` y `Task[T]` tiene la siguiente implicación: 
una entrada no puede depender de una tarea, debido a que una entrada es 
evaluada una única vez durante la carga de un proyecto y no es re-evaluada. Más 
información sobre esto en [Grafos de tareas][Task-Graph].

### Claves en el shell de sbt

En el shell de sbt, puedes escribir el nombre de cualquier tarea para ejecutar 
dicha tarea. Esta es la razón por la que al escribir `compile` se ejecuta la tarea `compile`. 
`compile` es una clave tarea.

Si escribes el nombre de una clave entrada en lugar de una clave tarea el valor 
de la clave entrada será mostrado. Escribir el nombre de una clave tarea 
ejecuta la tarea pero muestra el valor resultante. Para ver el resultado de una 
tarea utiliza `show <tarea>` en lugar de simplemente `<tarea>`. La convención 
para nombres de claves es utilizar `camelCase` por lo que el nombre de linea de 
comandos y los identificadores de Scala son los mismos.

Para aprender más acerca de cualquier clave escribe `inspect <clave>` en el 
prompt interactivo de sbt. Alguna de la información que `inspect` muestra no 
tendrá sentido (de momento), pero al principio de todo mostrará el tipo del 
valor y una breve descripción de esa entrada.

### Importaciones en build.sbt

Puedes incluir sentencias `import` al principio de `build.sbt`. No necesitan 
estar separadas por líneas en blanco.

Existen algunas importaciones implícitas predeterminadas:

```scala
import sbt._
import Keys._
```

(Además, si tienes autoplugins, los nombres marcados bajo `autoImport` serán 
importados.)

### Definiciones de construcción .sbt planas

La configuración puede ser escrita directamente en el fichero `build.sbt` en 
lugar de ponerla dentro de la llamada a `.settings(...)`. Podemos llamar a esto 
el "estilo plano".

```scala
ThisBuild / version := "1.0"
ThisBuild / scalaVersion := "$example_scala_version$"
```

Esta sintaxis es la recomendada para configuraciones con ámbito `ThisBuild` plugins añadidos.
Mira secciones posteriores acerca de los ámbitos y los plugins.

### Añadir dependencias de biblioteca

Para depender de bibliotecas de terceros hay dos opciones. La primera es copiar 
jars en `lib/` (dependencias no gestionadas) y la otra es añadir dependencias 
gestionadas, que en `build.sbt` tienen este aspecto:

```scala
val derby = "org.apache.derby" % "derby" % "10.4.1.3"

ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "$example_scala_version$"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "Hello",
    libraryDependencies += derby
  )
```

Así es como se puede añadir una dependencia gestionada de la biblioteca Apache 
Derby versión 10.4.1.3.

La clave `libraryDependencies` posee dos complejidades: `+=` en lugar de `:=` y 
el método `%`. `+=` añade al valor antiguo de la clave en lugar de 
reemplazarlo, como se explica en [Grafos de tareas][Task-Graph]. El método `%` 
es utilizado para construir un identificador de módulo Ivy a partir de cadenas, 
como se explica en [Dependencias de bibliotecas][Library-Dependencies].

De momento nos saltaremos los detalles de las dependencias de bibliotecas hasta 
más tarde en la Guía de inicio. Hay una [página entera][Library-Dependencies] 
que habla de ellas más tarde.

---
out: Library-Dependencies.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Task-Graph]: Task-Graph.html
  [external-maven-ivy]: ../docs/Library-Management.html#External+Maven+or+Ivy
  [Cross-Build]: ../docs/Cross-Build.html
  [Resolvers]: ../docs/Resolvers.html
  [Library-Management]: ../docs/Library-Management.html

Dependencias de bibliotecas
---------------------------

Esta página asume que has leído las páginas anteriores de la Guía de inicio, en
particular [Definiciones de construcción][Basic-Def], [Ámbitos][Scopes] y
[Grafos de tareas][Task-Graph].

Las dependencias de bibliotecas pueden ser añadidas de dos formas:

- las *dependencias no gestionadas* son jars copiados en el directorio `lib`
- las *dependencias gestionadas* son configuradas en la definición de
  construcción y descargadas automáticamente desde repositorios

### Dependencias no gestionadas

La mayoría de la gente utiliza dependencias gestionadas en lugar de no
gestionadas. Pero las segundas son más simples de explicar cuando se empieza
con las dependencias.

Las dependencias no gestionadas funcionan así: copia jars en `lib` y serán
añadidas en el classpath del proyecto. Y no hay más.

También puedes copiar jars para tests como
[ScalaCheck](https://scalacheck.org/),
[Specs2](http://specs2.org) y 
[ScalaTest](https://www.scalatest.org/) en `lib`.

Las dependencias en `lib` aparecen en todos los classpaths (para `compile`,
`test`, `run` y `console`). Si quieres cambiar el classpath para una sola de esas tareas deberías de ajustar `Compile / dependencyClasspath` o
`Runtime / dependencyClasspath`, por ejemplo.

No hay que añadir nada en `build.sbt` para empezar a utilizar dependencias no
gestionadas, aunque puedes cambiar la clave `unmanagedBase` si quisieras
utilizar un directorio diferente a `lib`.

Para utilizar `custom_lib` en lugar de `lib`:

```scala
unmanagedBase := baseDirectory.value / "custom_lib"
```

`baseDirectory` es el directorio raíz del proyecto, por lo que aquí se está
cambiando `unmanagedBase` para que dependa de `baseDirectory` utilizando el
método especial `value`, como se explicó en [Grafos de tareas][Task-Graph].

Existe también una tarea llamada `unmanagedJars` la cual lista los jars del
directorio `unmanagedBase`. Si quieres utilizar múltiples directorios o hacer
algo más complejo tendrías que reemplazar por completo la tarea `unmanagedJars`
con una que hiciese algo diferente, por ejemplo, devolver una lista para la
configuración `Compile` en lugar de los ficheros en el directorio `lib`:

```scala
Compile / unmanagedJars := Seq.empty[sbt.Attributed[java.io.File]]
```

### Dependencias gestionadas

sbt utiliza [Apache Ivy](https://ant.apache.org/ivy/) para implementar
dependencias gestionadas, por lo que no tendrás demasiados problemas si ya estás
familiarizado con Ivy o Maven.

#### La clave `libraryDependencies`

La mayor parte del tiempo, podrás simplemente listar tus dependencias en la
entrada `libraryDependencies`. Es incluso posible escribir un fichero POM de
Maven o un fichero de configuración de Ivy para configurar externamente tus
dependencias y hacer que sbt use esos ficheros de configuración externos.
Puedes obtener más información sobre cómo se puede hacer
[aquí][external-maven-ivy].

Declarar una dependencia es parecido a esto, donde `groupId`, `artifactId` y
`revision` son cadenas de caracteres:

```scala
libraryDependencies += groupID % artifactID % revision
```

o a esto, donde `configuration` puede ser una cadena de caracteres o una
`Configuration` (such as `Test`):

```scala
libraryDependencies += groupID % artifactID % revision % configuration
```
`libraryDependencies` está declarado en 
[Keys](../../api/sbt/Keys\$.html#libraryDependencies:sbt.SettingKey[Seq[sbt.librarymanagement.ModuleID]])
de esta forma:

```scala
val libraryDependencies = settingKey[Seq[ModuleID]]("Declares managed dependencies.")
```

El método `%` crea un objeto de tipo `ModuleID` a partir de cadenas de
caracteres, que luego puede ser añadido a `libraryDependencies`.

Por supuesto, sbt (via Ivy) tiene que saber de dónde descargar los módulos.
Si tu módulo está en uno de los repositorios predeterminados con los que cuenta
sbt entonces será suficiente. Por ejemplo, Apache Derby está en el repositorio
estándar de Maven2:

```scala
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3"
```

Si escribes eso en `build.sbt` y luego ejecutas `update`, sbt debería descargar
Derby en `\$COURSIER_CACHE/https/repo1.maven.org/maven2/org/apache/derby/`. (Por cierto, `update` es una
dependencia de `compile` por lo que no hay necesidad de estar escribiendo 
`update` todo el rato.)

Por supuesto, puedes también utilizar `++=` para añadir en un único paso una
serie de dependencias:

```scala
libraryDependencies ++= Seq(
  groupID % artifactID % revision,
  groupID % otherID % otherRevision
)
```

En contadas ocasiones necesitarás utilizar `:=` con `libraryDependencies`.

#### Obteniendo la versión de Scala correcta con `%%`

Si usas `groupID %% artifactID % revision` en lugar de
`groupID % artifactID % revision` (la diferencia está en el doble `%%` tras 
`groupId`), sbt añadirá la versión del binario de Scala de tu proyecto al nombre
del artefacto. Esto simplemente es un atajo. Podrías escribir esto sin el `%%`:

```scala
libraryDependencies += "org.scala-stm" % "scala-stm_2.13" % "$example_scala_stm_version$"
```

Si asumimos que `scalaVersion` para tu construcción es `$example_scala213$` lo siguiente es
idéntico a lo anterior (fíjate en el doble `%%` tras `"org.scala-stm"`):

```scala
libraryDependencies += "org.scala-stm" %% "scala-stm" % "$example_scala_stm_version$"
```

La idea es que muchas dependencias son compiladas para múltiples versiones de
Scala y con toda seguridad lo que querrás será obtener aquella que ofrece
compatibilidad binaria con tu proyecto.

Para más información ver [Construcción cruzada][Cross-Build].

#### Revisiones Ivy

`revision` en `groupID % artifactID % revision` no tiene por qué ser una versión
fija. Ivy puede seleccionar la última revisión de un módulo de acuerdo a las
restricciones que especifiques. En lugar de una revisión fija como `"1.6.1"`,
puedes especificar `"latest.integration"`, `"2.9.+"`, or `"[1.0,)"`.
Para más información ver la documentación de
[Revisiones Ivy](https://ant.apache.org/ivy/history/2.3.0/ivyfile/dependency.html#revision).

<!-- TODO: Add aliases -->

En ocasiones, un "rango de versiones" de Maven es utilizado para especificar una
dependencia (transitiva o de otro tipo), tales como `[1.3.0,)`. Si una versión
específica de la dependencia es declarada en la construcción y satisface el
rango entonces sbt usará la versión especificada. En otro caso, Ivy tendrá que
encontrar la última versión. Esto puede ocasionar un comportamiento inesperado
donde la versión efectiva va cambiando a lo largo del tiempo, incluso cuando hay
una versión específica de la biblioteca que satisface la condición del rango.

<!-- TODO: Maven version ranges will be replaced with its lower bound if the
build so that when a satisfactory version is found in the dependency
graph it will be used.  You can disable this behavior using the JVM
flag `-Dsbt.modversionrange=false`. -->

#### Resolvedores

No todos los paquetes viven en el mismo servidor. sbt utiliza el repositorio
estándar de Maven2 de forma predeterminada. Si tu dependencia no está en uno de
los repositorios predeterminados tendrás que añadir un *resolvedor* para que Ivy
lo pueda encontrar.

Para añadir un repositorio adicional utiliza:

```scala
resolvers += name at location
```

con el método especial `at` entre dos cadenas de caracteres.

Por ejemplo:

```scala
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
```

La clave `resolvers` está definida
[Keys](../../api/sbt/Keys\$.html#resolvers:sbt.SettingKey[Seq[sbt.librarymanagement.Resolver]])
de la siguiente forma:

```scala
val resolvers = settingKey[Seq[Resolver]]("The user-defined additional resolvers for automatically managed dependencies.")
```

El método `at` crea un objecto de tipo `Resolver` a partir de dos cadenas de
caracteres.

sbt puede buscar en tu repositorio local de Maven si lo añades como repositorio:

```scala
resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
```

o, convenientemente:

```scala
resolvers += Resolver.mavenLocal
```

Para más información sobre cómo definir otros tipos de repositorios ver
[Resolvedores][Resolvers].

#### Sobrescribiendo resolvedores predeterminados

`resolvers` no contiene los resolvedores predeterminados, solo los adicionales
añadidos por tu definición de construcción.

sbt combina `resolvers` con algunos repositorios predeterminados para formar
`externalResolvers`.

Por tanto, para cambiar o eliminar los resolvedores predeterminados tendrías que
sobrescribir `externalResolvers` en lugar de `resolvers`.

#### Dependencias por configuración

A menudo una dependencia es necesaria para tu código de test
(en `src/test/scala`, el cual es compilado por la configuración `Test`)
pero no así tu código fuente principal.

Si quieres añadir una dependencia que aparezca solo en el classpath de la
configuración `Test` pero no en la de `Compile` puedes añadir un `% "test"`
de la siguiente forma:

```scala
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % "test"
```

También puedes utilizar la versión tipada de la configuración `Test` de esta
forma:

```scala
libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % Test
```

Ahora, si escribes `show compile:dependencyClasspath` en el prompt interactivo
de sbt no deberías ver listado el jar de Derby. Pero si escribes
`show Test/dependencyClasspath` sí que deberías de verlo.

Habitualmente, las dependencias relacionadas con tests tales como 
[ScalaCheck](https://scalacheck.org/),
[Specs2](http://specs2.org) y 
[ScalaTest](https://www.scalatest.org/) debería de ir definidas con `% "test"`.

Existen más detalles, consejos y trucos relacionados con las dependencias de
bibliotecas en [esta página][Library-Management].

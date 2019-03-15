---
out: Scope-Delegation.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html

Delegación de ámbito (resolución de .value)
-------------------------------------------

Esta página explica la delegación de ámbito. Se supone que has leído y
comprendido las páginas anteriores, [Definiciones de construcción][Basic-Def] y
[Ámbitos][Scopes].

Ahora que hemos cubierto todos los detalles de los ámbitos, podemos explicar la
resolución de `.value` en detalle. Te puedes saltar esta sección si es la
primera vez que lees esta página.

Resumamos lo que hemos aprendido hasta ahora:

- Un ámbito es una tupla de componentes de tres ejes: el eje de subproyecto,
  el eje de configuración y el eje de tarea.
- Existe el componente de ámbito especial `Zero` utilizado por los tres ejes
  de ámbito.
- Existe el componente de ámbito especial `ThisBuild` utilizado únicamente por
  *el eje de subproyecto*.
- `Test` extiende `Runtime` y `Runtime` extiende la configuración `Compile`.
- Una clave definida en build.sbt tiene como ámbito a
  `\${current subproject} / Zero / Zero` de forma predeterminada.
- Una clave puede especificar un ámbito utilizando el operador `/`.

Ahora supongamos que tenemos la siguiente definición de construcción:

@@snip [build.sbt]($root$/src/sbt-test/ref/scope-delegation/x/build.sbt) { #fig1 }

Dentro del cuerpo de la entrada de `foo`, la clave con ámbito `Test / bar`
ha sido declarada.
Sin embargo, a pesar de que `Test / bar` no está definida en `projX`, sbt sigue
siendo capaz de resolver `Test / bar` utilizando otra clave con ámbito,
lo que lleva a que `foo` sea inicializado a `2`.

sbt tiene un camino alternativo bien definido llamado *delegación de ámbito*.
Esto permite establecer un valor una única vez en un ámbito más general,
permitiendo que ámbitos más específicos hereden tal valor.

### Reglas de delegación de ámbito

Estas son las reglas para la delegación de ámbito:

- Regla 1: los ejes de ámbito tienen la siguiente precedencia:
  el eje de subproyecto, el eje de configuración y finalmente el eje de tarea.
- Regla 2: Dado un ámbito, la delegación de ámbitos es utilizada sustituyendo
  el eje de tarea en el siguiente orden:
  el ámbito de dicha tarea y luego
  `Zero`, que es la versión tarea-nula con ámbito de este ámbito.
- Regla 3: Dado un ámbito, la delegación de ámbitos es utilizada sustituyendo
  el eje de configuración en el siguiente orden:
  la propia configuración, sus ancestros y luego `Zero` (equivalente a un eje
  de configuración sin ámbito).
- Regla 4: Dado un ámbito, la delegación de ámbitos es utilizada sustituyendo
  el eje de subproyecto en el siguiente orden:
  el propio proyecto, `ThisBuild` y luego `Zero`.
- Regla 5: Una clave con delegación de ámbito y sus entradas/tareas dependientes
  son evaluadas sin acarrear el contexto original.

Estudiaremos cada regla en el resto de esta página.

### Regla 1: Precedencia de ejes de ámbito

- Regla 1: los ejes de ámbito tienen la siguiente precedencia:
  el eje de subproyecto, el eje de configuración y finalmente el eje de tarea.

En otras palabras, dados dos ámbitos candidatos, si uno de ellos tiene un valor
más específico en el eje de subproyecto entonces dicho eje siempre ganará sin
importar el ámbito de la configuración o la tarea.
De forma similar, si los subproyectos son los mismos, ganará el que tenga
un valor más específico en el eje de configuración, sin importar lo que tenga
en el ámbito de tarea.
Veremos más reglas para definir *más específico*.

### Regla 2: Delegación del eje de tarea

- Regla 2: Dado un ámbito, la delegación de ámbitos es utilizada
  **sustituyendo** el eje de tarea en el siguiente orden:
  el ámbito de dicha tarea y luego
  `Zero`, que es la versión tarea-nula con ámbito de este ámbito.

Aquí tenemos una regla concreta que muestra cómo sbt empleará la delegación de ámbitos
dada una clave.
Recuerda, estamos intentando mostrar el camino de búsqueda a partir de un
`(xxx / yyy).value` cualquiera.

**Ejercicio A**: Dada la siguiente definición de construcción:

```scala
lazy val projA = (project in file("a"))
  .settings(
    name := {
      "foo-" + (packageBin / scalaVersion).value
    },
    scalaVersion := "2.11.11"
  )
```
¿Cuál es el valor de `projA / name`?

1. `"foo-2.11.11"`
2. `"foo-$example_scala_version$"`
3. ¿otra cosa?

La respuesta es `"foo-2.11.11"`.
Dentro de `.settings(...)`, `scalaVersion` tiene automáticamente un ámbito de
`projA / Zero / Zero`, por lo que `packageBin / scalaVersion` se convierte en
`projA / Zero / packageBin / scalaVersion`.
Esa clave con ámbito en particular no está definida. Utilizando la regla 2, sbt
sustituirá el eje de tarea a `Zero` como `projA / Zero / Zero` o
(`projA / scalaVersion`).
Esta clave con ámbito está definida como `"2.11.11"`.

### Regla 3: Resolución del eje de configuración

- Regla 3: Dado un ámbito, la delegación de ámbitos es utilizada sustituyendo
  el eje de configuración en el siguiente orden:
  la propia configuración, sus ancestros y luego `Zero` (equivalente a un eje
  de configuración sin ámbito).

El ejemplo es el `projX` que vimos antes:

@@snip [build.sbt]($root$/src/sbt-test/ref/scope-delegation/x/build.sbt) { #fig1 }

El ámbito completo es `projX / Test / Zero`.
Además, recordemos que `Test` extiende `Runtime` y que `Runtime` extiende
`Compile`.

`Test / bar` no está definido pero, debido a la Regla 3, sbt buscará `bar` con
ámbito `projX / Test / Zero`, `projX / Runtime / Zero` y finalmente
`projX / Compile / Zero`. Este último es encontrado, el cual es `Compile / bar`.

### Regla 4: Resolución del eje de subproyecto

- Regla 4: Dado un ámbito, la delegación de ámbitos es utilizada sustituyendo
  el eje de subproyecto en el siguiente orden:
  el propio proyecto, `ThisBuild` y luego `Zero`.

**Ejercicio B**: Dada la siguiente definición de construcción:

```scala
ThisBuild / organization := "com.example"

lazy val projB = (project in file("b"))
  .settings(
    name := "abc-" + organization.value,
    organization := "org.tempuri"
  )
```

¿Cuál es el valor de `projB / name`?

1. `"abc-com.example"`
2. `"abc-org.tempuri"`
3. ¿otra cosa?

La respuesta es `abc-org.tempuri`.
Aplicando la Regla 4, el primer intento se hace mirando `organization` con
ámbito `projB / Zero / Zero`, el cuál está definido en `projB` como
`"org.tempuri"`.
Éste tiene mayor precedencia que la configuración a nivel de construcción
`ThisBuild / organization`.

#### Precedencia de ejes de ámbito, otra vez

**Ejercicio C**: Dada la siguiente definición de construcción:

@@snip [build.sbt]($root$/src/sbt-test/ref/scope-delegation/x/build.sbt) { #fig_c }

¿Cuál es el valor de `projC / name`?

1. `"foo-2.12.2"`
2. `"foo-2.11.11"`
3. ¿otra cosa?

La respuesta es `foo-2.11.11`.
`scalaVersion` con ámbito `projC / Zero / packageBin` no está definida.
La Regla 2 encuentra `projC / Zero / Zero`.
La regla 4 encuentra `ThisBuild / Zero / packageBin`.
En este caso la Regla 1 dice que el valor más específico del eje de subproyecto
gana, el cual es `projC / Zero / Zero`, que está definido como `"2.11.11"`.

**Ejercicio D**: Dada la siguiente definición de construcción:

@@snip [build.sbt]($root$/src/sbt-test/ref/scope-delegation/x/build.sbt) { #fig_d }

¿Qué saldría si ejecutamos `projD / test`?

1. `List()`
2. `List(-Ywarn-unused-import)`
3. ¿otra cosa?

La respuesta es `List(-Ywarn-unused-import)`.
La Regla 2 encuentra `projD / Compile / Zero`,
la Regla 3 encuentra `projD / Zero / console`,
y la Regla 4 encuentra `ThisBuild / Zero / Zero`.
La Regla 1 elige `projD / Compile / Zero`
porque tiene tiene `projD` en el eje de subproyecto y dicho eje tiene mayor
precedencia que el eje de tarea.

Después, `Compile / scalacOptions` hace referencia a `scalacOptions.value`,
luego necesitamos encontrar un delegado para `projD / Zero / Zero`.
La Regla 4 encuentra `ThisBuild / Zero / Zero` que finalmente resuelve a
`(-Ywarn-unused-import)`.

### El comando inspect para listar delegaciones

Para saber qué está pasando puedes utilizar el comando `inspect`.

```
sbt:projd> inspect projD / Compile / console / scalacOptions
[info] Task: scala.collection.Seq[java.lang.String]
[info] Description:
[info]  Options for the Scala compiler.
[info] Provided by:
[info]  ProjectRef(uri("file:/tmp/projd/"), "projD") / Compile / scalacOptions
[info] Defined at:
[info]  /tmp/projd/build.sbt:9
[info] Reverse dependencies:
[info]  projD / test
[info]  projD / Compile / console
[info] Delegates:
[info]  projD / Compile / console / scalacOptions
[info]  projD / Compile / scalacOptions
[info]  projD / console / scalacOptions
[info]  projD / scalacOptions
[info]  ThisBuild / Compile / console / scalacOptions
[info]  ThisBuild / Compile / scalacOptions
[info]  ThisBuild / console / scalacOptions
[info]  ThisBuild / scalacOptions
[info]  Zero / Compile / console / scalacOptions
[info]  Zero / Compile / scalacOptions
[info]  Zero / console / scalacOptions
[info]  Global / scalacOptions
```

Fíjate en que "Provided by" muestra que `projD / Compile / console / scalacOptions`
es proporcionado por `projD / Compile / scalacOptions`.
Además, bajo "Delegates", *todos* los posibles candidatos son listados en orden
de precedencia.

- Todos los ámbitos con ámbito `projD` en el eje de subproyecto son listados
  primero, luego `ThisBuild` y luego `Zero`.
- Dentro de un subproyecto, los ámbitos que utilizan `Compile` en el eje de
  configuración son listados primero y luego los de `Zero`.
- Finalmente, se listan los ejes de tarea con ámbito `console` y luego los que
  no lo tienen.

### Resolución de .value vs enlace dinámico

- Regla 5: Una clave con delegación de ámbito y sus entradas/tareas dependientes
  son evaluadas sin acarrear el contexto original.

Fíjate en que la delegación de ámbito se parece mucho a la herencia de clases de
un lenguaje orientado a objetos, aunque con una diferencia:
En un lenguaje orientado a objetos como Scala, cuando existe un método llamado
`drawShape` en un trait `Shape` sus subclases pueden sobrescribir el
comportamiento incluso cuando `drawShape` es utilizado por otros métodos en el
trait, lo es lo que se llama enlace dinámico.

Sin embargo, en sbt la delegación de ámbito puede delegar un ámbito a uno más
general, como una configuración a nivel de proyecto hacia una configuración a
nivel de construcción, pero dicha configuración a nivel de construcción no puede
hacer referencia a la configuración a nivel de proyecto.

**Ejercicio E**: Dada la siguiente definición de construcción:

```scala
lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.2",
      version      := scalaVersion.value + "_0.1.0"
    )),
    name := "Hello"
  )

lazy val projE = (project in file("e"))
  .settings(
    scalaVersion := "2.11.11"
  )
```

¿Qué devolverá `projE / version`?

1. `"2.12.2_0.1.0"`
2. `"2.11.11_0.1.0"`
3. ¿otra cosa?

La respuesta es `2.12.2_0.1.0`.
`projD / version` delega en `ThisBuild / version`,
que a su vez depende de `ThisBuild / scalaVersion`.
Debido a esto, la configuración a nivel de construcción debería limitarse
únicamente a asignaciones simples de valores.

**Ejercicio F**: Dada la siguiente definición de construcción:

```scala
ThisBuild / scalacOptions += "-D0"
scalacOptions += "-D1"

lazy val projF = (project in file("f"))
  .settings(
    compile / scalacOptions += "-D2",
    Compile / scalacOptions += "-D3",
    Compile / compile / scalacOptions += "-D4",
    test := {
      println("bippy" + (Compile / compile / scalacOptions).value.mkString)
    }
  )
```

¿Qué mostrará `projF / test`?

1. `"bippy-D4"`
2. `"bippy-D2-D4"`
3. `"bippy-D0-D3-D4"`
4. ¿otra cosa?

La respuesta es `"bippy-D0-D3-D4"`.
Esta es una variación de un ejercicio creado originalmente por
[Paul Phillips](https://gist.github.com/paulp/923154ab2d61882195cdea47483592ca).

Es una gran demostración de todas las reglas porque `someKey += "x"` se expande
a

```scala
someKey := {
  val old = someKey.value
  old :+ "x"
}
```

Al obtener el valor antiguo se dispara la delegación y debido a la Regla 5
se irá a otra clave con ámbito.
Librémonos del `+=` primero y anotemos los delegados para valores antiguos:

```scala
ThisBuild / scalacOptions := {
  // Global / scalacOptions <- Regla 4
  val old = (ThisBuild / scalacOptions).value
  old :+ "-D0"
}

scalacOptions := {
  // ThisBuild / scalacOptions <- Regla 4
  val old = scalacOptions.value
  old :+ "-D1"
}

lazy val projF = (project in file("f"))
  .settings(
    compile / scalacOptions := {
      // ThisBuild / scalacOptions <- Reglas 2 y 4
      val old = (compile / scalacOptions).value
      old :+ "-D2"
    },
    Compile / scalacOptions := {
      // ThisBuild / scalacOptions <- Reglas 3 y 4
      val old = (Compile / scalacOptions).value
      old :+ "-D3"
    },
    Compile / compile / scalacOptions := {
      // projF / Compile / scalacOptions <- Reglas 1 y 2
      val old = (Compile / compile / scalacOptions).value
      old :+ "-D4"
    },
    test := {
      println("bippy" + (Compile / compile / scalacOptions).value.mkString)
    }
  )
```

Esto se convierte en:

```scala
ThisBuild / scalacOptions := {
  Nil :+ "-D0"
}

scalacOptions := {
  List("-D0") :+ "-D1"
}

lazy val projF = (project in file("f"))
  .settings(
    compile / scalacOptions := List("-D0") :+ "-D2",
    Compile / scalacOptions := List("-D0") :+ "-D3",
    Compile / compile / scalacOptions := List("-D0", "-D3") :+ "-D4",
    test := {
      println("bippy" + (Compile / compile / scalacOptions).value.mkString)
    }
  )
```

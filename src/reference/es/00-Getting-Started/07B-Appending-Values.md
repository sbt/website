---
out: Appending-Values.html
---

  [Scopes]: Scopes.html

Añadir valores
--------------

### Añadir a valores existentes: `+=` y `++=`

La asignación `:=` es la transformación más simple, pero las claves tienen
también otros métodos. Si `T` en `SettingKey[T]` es una secuencia, es decir,
si el tipo del valor de una clave es una secuencia, podrás añadir elementos a la
secuencia en lugar de reemplazarla.

- `+=` añadirá un único elemento a la secuencia
- `++=` concatenará otra secuencia

Por ejemplo, la clave `Compile / sourceDirectories` tiene un `Seq[File]` por
valor. De forma predeterminada el valor de esta clave incluye `src/main/scala`.
Si quieres que además se compile el código fuente de un directorio llamado
source (en el caso de que por ejemplo no utilices una estructura de directorios
estándar) podrás añadir dicho directorio:

```scala
Compile / sourceDirectories += new File("source")
```

O convenientemente puedes utilizar la función `file()` del paquete sbt.

```scala
Compile / sourceDirectories += file("source")
```

(`file()` crea un nueva instancia de `File`)

Puedes usar `++=` para añadir más de un directorio a la vez:

```scala
Compile / sourceDirectories ++= Seq(file("sources1"), file("sources2"))
```

Donde `Seq(a, b, c, ...)` es la sintaxis estándar de Scala para construir una
secuencia.

Por supuesto, para reemplazar totalmente los directorios de código fuente
predeterminados puedes seguir utilizando `:=`:

```scala
Compile / sourceDirectories := Seq(file("sources1"), file("sources2"))
```

#### Cuándo no está definida una configuración

Cuando una entrada utiliza `:=`, `+=` o `++=` para crear una dependencia sobre
sí misma o sobre el valor de otra clave, el valor sobre el que depende debe
existir. De lo contrario sbt se quejará. Puede que por ejemplo diga
*"Reference to undefined setting"*. Cuando esto suceda asegúrate de que estás
utilizando la clave en el [ámbito][Scopes] donde está definida.

Es posible crear referencias circulares, lo cual es un error. sbt te lo dirá
si lo haces.

#### Tareas basadas en valores de otras claves

Puedes calcular el valor de algunas tareas o entradas definiendo o añadiendo
el valor de otra tarea. Esto se hace utilizando `Def.task` como argumento de
`:=`, `+=`, or `++=`.

Como ejemplo, considera añadir un generador de código fuente utilizando
el directorio base del proyecto y el classpath de compilación.

```scala
Compile / sourceGenerators += Def.task {
  myGenerator(baseDirectory.value, (Compile / managedClasspath).value)
}
```

### Añadir con dependencia: `+=` y `++=`

Otras claves pueden ser utilizadas cuando se añade a una entrada o tarea
existente, al igual que se hacía para asignar con `:=`.

Por ejemplo, digamos que tienes un informe de cobertura de código cuyo nombre
se basa en el nombre del proyecto y que quieres añadirlo a la lista de ficheros
que se borran cuando se invoca `clean`:

```scala
cleanFiles += file("coverage-report-" + name.value + ".txt")
```

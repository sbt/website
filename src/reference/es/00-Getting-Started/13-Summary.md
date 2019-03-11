---
out: Summary.html
---

  [Basic-Def]: Basic-Def.html
  [Scopes]: Scopes.html
  [Using-Plugins]: Using-Plugins.html
  [getting-help]: ../docs/faq.html#getting-help

Guía de inicio - resumen
------------------------

Esta página resume la Guía de inicio.

Para usar sbt existe un pequeño número de conceptos que se han de comprender.
Cada uno tiene su curva de aprendizaje, pero siendo optimistas, sbt no es mucho
más que esos mismos conceptos. sbt usa un pequeño conjunto de poderosos
conceptos para hacer todo lo que hace.

Si has leído la Guía de inicio de principio a fin ahora ya sabes lo que
necesitas saber.

### sbt: Los conceptos esenciales

- Los fundamentos de Scala. Es indudablemente útil estar familiarizado con la
  sintaxis de Scala. [Programming in
  Scala](https://www.artima.com/shop/programming_in_scala_3ed), escrito por el
  creador de Scala, es una magnífica introducción.
- [Definiciones de construcción][Basic-Def]
- La definición de construcción es un gran GAD de tareas y sus dependencias.
- Para crear una entrada, emplea uno de los pocos métodos de una clave: `:=`,
  `+=` o `++=`.
- Cada entrada tiene un valor de un tipo en particular, determinado por la
  clave.
- Las *tareas* son entradas especiales donde la computación que produce el valor
  de la clave es re-evaluado cada vez que se lanza una tarea. Las entradas que no
  son tareas computan su valor una única vez, durante la carga de la definición
  de construcción.
- [Ámbitos][Scopes]
- Cada clave puede tener múltiples valores, con distintos ámbitos.
- Los ámbitos pueden utilizar tres ejes: configuración, proyecto y tarea.
- Los ámbitos permiten tener diferente comportamiento por proyecto, por tarea
  o por configuración.
- Una configuración es una clase de construcción, como la principal (`Compile`)
  o la de test (`Test`).
- El eje de proyecto soporta además el ámbito de "construcción entera".
- Un ámbito puede *delegar* en otros ámbitos más generales.
- La mayoría de la configuración debe ir en `build.sbt` y el uso de ficheros
  `.scala` debería de estar reservado para definir clases e implementaciones de
  tareas más complejas.
- La definición de construcción es un proyecto sbt como los demás, ubicado en el
  directorio `project`.
- Los [plugins][Using-Plugins] extienden la definición de construcción
- Los plugins se pueden añadir con el método `addSbtPlugin` en
  `project/plugins.sbt` (y NO en el fichero `build.sbt` del directorio base del
  proyecto).

Si algunos de estos puntos no te queda claro, por favor,
[solicita ayuda][getting-help], vuelve atrás y vuelve a leer, o haz algunos
experimentos utilizando el modo interactivo de sbt.

¡Buena suerte!

### Notas avanzadas

<!-- TODO: Link to reference. The rest of this wiki consists of deeper dives and less-commonly-needed
information. -->

Ya que sbt es código abierto, ¡no olvides que también puedes echarle un vistazo
al [código fuente](https://github.com/sbt/sbt)!

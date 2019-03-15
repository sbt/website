---
out: Using-Plugins.html
---

  [Basic-Def]: Basic-Def.html
  [Library-Dependencies]: Library-Dependencies.html
  [Multi-Project]: Multi-Project.html
  [global-vs-local-plugins]: ../docs/Best-Practices.html#global-vs-local-plugins
  [Community-Plugins]: ../docs/Community-Plugins.html
  [Plugins]: ../docs/Plugins.html
  [Plugins-Best-Practices]: ../docs/Plugins-Best-Practices.html
  [Task-Graph]: Task-Graph.html

Usar plugins
------------

Por favor, lee primero las páginas anteriores de la Guía de inicio, en particular [Definiciones de construcción][Basic-Def], [Grafos de tareas][Task-Graph] y 
[Dependencias de bibliotecas][Library-Dependencies] antes de leer esta página.

### ¿Qué es un plugin?

Un plugin extiende la definición de construcción, usualmente añadiendo nueva configuración. La nueva configuración puede incluir nuevas tareas. Por ejemplo, un plugin puede añadir una tarea `codeCoverage` para generar un informe de cobertura de código de test.

### Declarar un plugin

Si tu proyecto está en un directorio llamado `hello` y quieres añadir el plugin `sbt-site` a la definición de construcción, crea el fichero `hello/project/site.sbt` y declara la dependencia del plugin pasando el módulo Ivy del plugin a `addSbtPlugin`:

```scala
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.7.0")
```
Si quieres añadir `sbt-assembly`, crea `hello/project/assembly.sbt` con el siguiente contenido:

```scala
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.2")
```
No todos los plugins están ubicados en uno de los repositorios predeterminados. La documentación del plugin te puede instar a que añadas el repositorio donde se encuentra:

```scala
resolvers += Resolver.sonatypeRepo("public")
```

Por lo general los plugins proporcionan configuraciones que son añadidas a la del proyecto para habilitar la funcionalidad del plugin. Esto es explicado en la siguiente sección:

### Habilitando e inhabilitando autoplugins

Un plugin puede declarar que su configuración debería de ser automáticamente añadida a la definición de construcción, en cuyo caso no tienes que hacer nada para añadirla.

A partir de sbt 0.13.5 se introdujo la nueva característica [autoplugins][Plugins] la cual permite asegurar que la configuración de un plugin está disponible en el proyecto de forma automática y de un modo seguro. Muchos autoplugins deberían contar con su configuración predeterminada automáticamente, sin embargo esto requiere la activación explícita.

Si estás utilizando un autoplugin que requiere activación explícita entonces tendrás que añadir lo siguiente en tu `build.sbt`:

```scala
lazy val util = (project in file("util"))
  .enablePlugins(FooPlugin, BarPlugin)
  .settings(
    name := "hello-util"
  )
```

El método `enablePlugins` permite a los proyectos definir explícitamente qué autoplugins quieren utilizar.

Los proyectos también pueden excluir plugins con el método `disablePlugins`. Por ejemplo, si queremos eliminar la configuración de `IvyPlugin` en `util` tendríamos que modificar nuestro `build.sbt` de la siguiente manera:

```scala
lazy val util = (project in file("util"))
  .enablePlugins(FooPlugin, BarPlugin)
  .disablePlugins(plugins.IvyPlugin)
  .settings(
    name := "hello-util"
  )
```

Los autoplugins deberían documentar si necesitan o no ser explícitamente habilitados. Si sientes curiosidad por saber qué autoplugins están habilitados para un proyecto, puedes ejecutar el comando `plugins` en la consola de sbt.

Por ejemplo:

```
> plugins
In file:/home/jsuereth/projects/sbt/test-ivy-issues/
        sbt.plugins.IvyPlugin: enabled in scala-sbt-org
        sbt.plugins.JvmPlugin: enabled in scala-sbt-org
        sbt.plugins.CorePlugin: enabled in scala-sbt-org
        sbt.plugins.JUnitXmlReportPlugin: enabled in scala-sbt-org
```
Aquí, la salida de `plugins` nos está mostrando que los plugins predeterminados de sbt están todos habilitados. La configuración predeterminada de sbt es proporcionada via tres plugins:

1.  `CorePlugin`: Proporciona los controles de paralelismo de tareas del núcleo.
2.  `IvyPlugin`: Proporciona los mecanismos para publicar/resolver módulos.
3.  `JvmPlugin`: Proporciona los mecanismos para compilar/testear/ejecutar/empaquetar proyectos de Java/Scala.

Adicionalmente, `JUnitXmlReportPlugin` proporciona soporte experimental para generar junit-xml.

Algunos plugins no automáticos antiguos a menudo necesitan que su configuración sea añadida explícitamente, por lo que las [construcciones multiproyecto][Multi-Project] pueden tener diferentes tipos de proyectos.
La documentación del plugin indicará cómo configurarlo, pero típicamente para plugins antiguos esto implica añadir la configuración base del plugin y modificarla según sea necesario.

Por ejemplo, para el plugin `sbt-site`, necesitarías crear un fichero `site.sbt` con el siguiente contenido para habilitarlo para ese proyecto:

```scala
site.settings
```

Si la construcción define múltiples proyectos, entonces añádelo directamente al proyecto:

```scala
// inhabilitar el plugin site para el proyecto ´util´
lazy val util = (project in file("util"))

// habilitar el plugin site para el proyecto `core`
lazy val core = (project in file("core"))
  .settings(site.settings)
```

### Plugins globales

Los plugins pueden ser instalados para todos tus proyectos a la vez
declarándolos en `$global_plugins_base$`. `$global_plugins_base$` es un proyecto sbt cuyo classpath es exportado a todos los proyectos de la definición de construcción. Más o menos, cualquier fichero `.sbt` y `.scala` en `$global_plugins_base$` se comporta como si estuviera en el directorio `project/` de cada uno de los proyectos.

Puedes crear `$global_plugins_base$build.sbt` y poner expresiones `addSbtPlugin()` ahí para añadir plugins a todos tus proyectos a la vez.
Debido a que hacer eso incrementaría la dependencia a nivel local, esta característica debería ser utilizada con moderación. Para más información ver [Buenas prácticas][global-vs-local-plugins].

### Plugins disponibles

Existe [una lista de plugins disponibles][Community-Plugins].

Algunos plugins especialmente famosos son:

- aquellos para los IDEs (para importar un proyecto sbt en tu IDE)
- aquellos que soportan frameworks web, tales como [xsbt-web-plugin](https://github.com/earldouglas/xsbt-web-plugin)

Para más información, incluyendo cómo desarrollar plugins, ver [Plugins][Plugins].
Para saber más acerca de las mejores prácticas ver [Plugins - Mejores prácticas][Plugins-Best-Practices].

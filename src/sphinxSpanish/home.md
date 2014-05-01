sbt
===

sbt es una herramienta de construcción para Scala, Java, y
[más](https://github.com/d40cht/sbt-cpp). Requiere Java 1.6 o posterior.

Instalación
-----------

Vea las instrucciones de instalación \</Getting-Started/Setup\>.

Características
---------------

-   Poca o ninguna configuración requerida para proyectos simples.
-   doc:build definition \</Getting-Started/Basic-Def\> basada en Scala
    que puede utilizar la flexibilidad completa del código de Scala.
-   Recompilación incremental precisa usando la información extraída del
    compilador.
-   Compilación y *testing* continuos con
    triggered execution \</Detailed-Topics/Triggered-Execution\>
-   Empaqueta y publica jars.
-   Genera documentación con scaladoc.
-   Soporta proyectos mixtos de
    Scala/Java \</Detailed-Topics/Java-Sources\>.
-   Soporta testing \</Detailed-Topics/Testing\> con ScalaCheck, specs,
    y ScalaTest. JUnit es soportado con un plugin.
-   Inicia una sesión de Scala REPL (*Read-Eval-Print-Loop*) con las
    clases del proyecto y dependencias en el classpath.
-   Modularización soportada con
    sub-proyectos \</Getting-Started/Multi-Project\>.
-   Soporte para proyectos externos (¡enliste un repositorio de git como
    una dependencia!).
-   Parallel task execution \</Detailed-Topics/Parallel-Execution\>
    (ejecución de tareas en paralelo), incluyendo ejecución de *tests*
    (pruebas) en paralelo.
-   Soporte para manejo de dependencias (librerías) \</Getting-Started/Library-Dependencies\>:
    declaraciones *inline*, archivos de configuración externos de Ivy o
    Maven, o manejo manual de dependencias.

Iniciando
---------

Para iniciar, *por favor lea* la
Guía para empezar \</Getting-Started/Welcome\>. Se ahorrará *mucho*
tiempo si tiene una compresión correcta del panorama general de
antemano. Toda la documentación puede encontrarse vía la
tabla de contenidos \<index\>.

Utilice [Stack Overflow](http://stackoverflow.com/tags/sbt) para
preguntar. Use la sbt-dev mailing list\_ para discutir el desarrollo de
sbt. Utilice el canal \#sbt de irc para preguntas y discusiones.

Puede hacer un *fork* de esta documentación en
[GitHub](https://github.com/sbt/sbt/). Siéntase libre de hacer
correcciones y añadiduras a la documentación. El código fuente de esta
documentación está localizado en el subdirectorio src/sphinx del
proyecto de Github.

La documentación para la versión 0.7.x ha sido [archivada
aquí](http://www.scala-sbt.org/0.7.7/docs/home.html). Esta documentación
sirve para la versión |version| de sbt.

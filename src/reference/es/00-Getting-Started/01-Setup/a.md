---
out: Installing-sbt-on-Mac.html
---

  [ZIP]: $sbt_native_package_base$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$/sbt-$app_version$.tgz
  [Manual-Installation]: Manual-Installation.html
  [oraclejdk8]: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

Instalar sbt on Mac
-------------------

### Instalar JDK

Sigue el link para instalar [Java SE Development Kit 8][oraclejdk8].

### Instalar desde un paquete universal

Descarga el paquete [ZIP][ZIP] o [TGZ][TGZ] y descomprímelo.

### Instalar desde un paquete de terceros

> **Nota:** Puede que algunos paquetes de terceros no proporcionen la última versión.
> Por favor, asegúrate de reportar cualquier problema con dichos paquetes a sus 
> respectivos mantenedores.

#### [Homebrew](http://mxcl.github.com/homebrew/)

```
\$ brew install sbt@1
```

#### [Macports](http://macports.org/)

```
\$ port install sbt
```

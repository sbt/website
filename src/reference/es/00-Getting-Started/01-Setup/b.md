---
out: Installing-sbt-on-Windows.html
---

  [MSI]: $sbt_native_package_base$/v$app_version$/sbt-$windows_app_version$.msi
  [ZIP]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.tgz
  [AdoptiumOpenJDK]: https://adoptium.net

Instalar sbt en Windows
-----------------------

### Install sbt with **cs setup**

Follow [Install](https://www.scala-lang.org/download/) page, and install Scala using Coursier. This should install the latest stable version of `sbt`.

### Instalar JDK

Sigue el link para instalar [JDK 8 u 11][AdoptiumOpenJDK].

### Instalar desde un paquete universal

Descarga el paquete [ZIP][ZIP] o [TGZ][TGZ] y descomprímelo.

### Instalador Windows

Descarga el [instalador msi][MSI] e instálalo.

### Instalar desde un paquete de terceros

> **Nota:** Puede que algunos paquetes de terceros no proporcionen la última versión.
> Por favor, asegúrate de reportar cualquier problema con dichos paquetes a sus
> respectivos mantenedores.

#### [Scoop](https://scoop.sh/)

```
\$ scoop install sbt
```

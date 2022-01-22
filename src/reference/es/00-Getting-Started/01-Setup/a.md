---
out: Installing-sbt-on-Mac.html
---

  [MSI]: $sbt_native_package_base$/v$app_version$/sbt-$windows_app_version$.msi
  [ZIP]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.tgz
  [Manual-Installation]: Manual-Installation.html
  [AdoptiumOpenJDK]: https://adoptium.net

Instalar sbt on macOS
---------------------

### Instalar JDK

Sigue el link para instalar [JDK 8 u 11][AdoptiumOpenJDK].

Or use [SDKMAN!](https://sdkman.io/):

```
\$ sdk list java
\$ sdk install java 11.0.4.hs-adpt
```

### Instalar desde un paquete universal

Descarga el paquete [ZIP][ZIP] o [TGZ][TGZ] y descomprímelo.

### Instalar desde un paquete de terceros

> **Nota:** Puede que algunos paquetes de terceros no proporcionen la última versión.
> Por favor, asegúrate de reportar cualquier problema con dichos paquetes a sus 
> respectivos mantenedores.

#### [Homebrew](https://brew.sh/)

```
\$ brew install sbt
```

#### [SDKMAN!](https://sdkman.io/)

```
\$ sdk install sbt
```

---
out: Installing-sbt-on-Mac.html
---

  [ZIP]: $sbt_native_package_base$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$/sbt-$app_version$.tgz
  [Manual-Installation]: Manual-Installation.html
  [AdoptOpenJDK]: https://adoptopenjdk.net/

Instalar sbt on macOS
---------------------

### Instalar JDK

Sigue el link para instalar [JDK 8 u 11][AdoptOpenJDK].

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

#### [Macports](http://macports.org/)

```
\$ port install sbt
```

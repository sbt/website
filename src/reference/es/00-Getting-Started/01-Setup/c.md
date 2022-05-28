---
out: Installing-sbt-on-Linux.html
---

  [MSI]: $sbt_native_package_base$/v$app_version$/sbt-$windows_app_version$.msi
  [ZIP]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$/v$app_version$/sbt-$app_version$.tgz
  [RPM]: $sbt_rpm_package_base$sbt-$app_version$.rpm
  [DEB]: $sbt_deb_package_base$sbt-$app_version$.deb
  [Manual-Installation]: Manual-Installation.html
  [website127]: https://github.com/sbt/website/issues/127
  [cert-bug]: https://bugs.launchpad.net/ubuntu/+source/ca-certificates-java/+bug/1739631
  [openjdk-devel]: https://pkgs.org/download/java-1.8.0-openjdk-devel

Installing sbt on Linux
-----------------------

### Install sbt with **cs setup**

Follow [Install](https://www.scala-lang.org/download/) page, and install Scala using Coursier. This should install the latest stable version of `sbt`.

### Installing from SDKMAN

To install both JDK and sbt, consider using [SDKMAN](https://sdkman.io/).

@@snip [install.sh]($root$/src/includes/install.sh) {}

Using Coursier or SDKMAN has two advantages.

1. They will install the official packaging by Eclipse Adoptium, as opposed to the ["mystery meat OpenJDK builds"](https://mail.openjdk.java.net/pipermail/jdk8u-dev/2019-May/009330.html).
2. They will install `tgz` packaging of sbt that contains all JAR files. (DEB and RPM packages do not to save bandwidth)

### Instalar JDK

Primero desberás de instalar JDK. Recomendamos **Eclipse Adoptium Temurin JDK 8**, **JDK 11**, u **JDK 17**.

Los detalles sobre el nombre de los paquetes cambian de una distribución a otra.
Por ejemplo, Ubuntu xenial (16.04LTS) usa
[openjdk-8-jdk](https://packages.ubuntu.com/hu/xenial/openjdk-8-jdk).
La familia Redhat lo llama
[java-1.8.0-openjdk-devel][openjdk-devel].

### Instalar desde un paquete universal

Descarga el paquete [ZIP][ZIP] o [TGZ][TGZ] y descomprímelo.

### Ubuntu y otras distribuciones basadas en Debian

Los paquetes [DEB][DEB] son oficialmente soportados por sbt.

Ubuntu y otras distribuciones basadas en Debian usan el formato DEB, pero por lo
general no necesitas instalar software desde un fichero DEB local.
En su lugar lo que se utiliza son los gestores de paquetes, tanto desde la línea de
comandos (p.e. `apt-get`, `aptitude`) o con una interfaz gráfica de usuario
(p.e. Synaptic).
Ejecuta lo siguiente desde el terminal para instalar `sbt`
(necesitarás tener privilegios de administrador para hacerlo, de ahí el `sudo`).

    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | sudo tee /etc/apt/sources.list.d/sbt_old.list
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo apt-key add
    sudo apt-get update
    sudo apt-get install sbt

Los gestores de paquetes utilizan los repositorios para buscar los paquetes que
se desean instalar.
Sólo tienes que añadir el repositorio en aquellos ficheros utilizados por tu
gestor de paquetes.

Una vez `sbt` haya sido instalado podrás gestionar el paquete en `aptitude` o
Synaptic después de que hayas actualizado la caché de paquetes. También podrás
ver el repositorio recién añadido al final de la lista en
Preferencias del sistema -> Software y actualizaciones -> Otro software:

![Ubuntu Software & Updates Screenshot](../files/ubuntu-sources.png "Ubuntu Software & Updates Screenshot")

**Nota**: Se han reportado errores de SSL en Ubuntu: `Server access Error:
java.lang.RuntimeException: Unexpected error:
java.security.InvalidAlgorithmParameterException: the trustAnchors parameter
must be non-empty url=https://repo1.maven.org/maven2/org/scala-sbt/sbt/1.1.0/sbt-1.1.0.pom`,
los cuales aparentemente impiden a OpenJDK 9 utilizar el formato PKCS12 para
`/etc/ssl/certs/java/cacerts` [cert-bug][cert-bug].
Según <https://stackoverflow.com/a/50103533/3827> esto ha sido arreglado en
Ubuntu Cosmic (18.10) aunque Ubuntu Bionic LTS (18.04) aún sigue esperando una
release. Mira las respuesta para encontrar soluciones.

### Red Hat Enterprise Linux y otras distribuciones basadas en RPM

Los paquetes [RPM][RPM] son oficialmente soportados por sbt.

Red Hat Enterprise Linux y otras distribuciones basadas en RPM utilizan el
formato RPM. Ejecuta lo siguiente desde el terminal para instalar `sbt`
(necesitarás tener privilegios de administrador para hacerlo, de ahí el `sudo`).

    # remove old Bintray repo file
    sudo rm -f /etc/yum.repos.d/bintray-rpm.repo
    curl -L https://www.scala-sbt.org/sbt-rpm.repo > sbt-rpm.repo
    sudo mv sbt-rpm.repo /etc/yum.repos.d/
    sudo yum install sbt

On Fedora (31 and above), use `sbt-rpm.repo`:

    # remove old Bintray repo file
    sudo rm -f /etc/yum.repos.d/bintray-rpm.repo
    curl -L https://www.scala-sbt.org/sbt-rpm.repo > sbt-rpm.repo
    sudo mv sbt-rpm.repo /etc/yum.repos.d/
    sudo dnf install sbt

> **Nota:** Por favor, reporta cualquier problema con estos paquetes al proyecto
> [sbt](https://github.com/sbt/sbt)

### Gentoo

El árbol oficial contiene ebuilds para sbt. Para instalar la última versión
disponible escribe:

    emerge dev-java/sbt

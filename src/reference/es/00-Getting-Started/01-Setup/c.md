---
out: Installing-sbt-on-Linux.html
---

  [ZIP]: $sbt_native_package_base$/$download_version_str$zip
  [TGZ]: $sbt_native_package_base$/$download_version_str$tgz
  [RPM]: $sbt_rpm_package_base$sbt-$app_version$.rpm
  [DEB]: $sbt_deb_package_base$sbt-$app_version$.deb
  [Manual-Installation]: Manual-Installation.html

<!-- TODO: Translate to Spanish -->

Installing sbt on Linux
-----------------------

### Installing from a universal package

Download [ZIP][ZIP] or [TGZ][TGZ] package and expand it.

### RPM and DEB

The following packages are also officially supported:

  - [RPM][RPM] package
  - [DEB][DEB] package

> **Note:** Por favor reporte cualquier problema que se tenga con los paquetes
> arriba mencionados al projecto
> [sbt-launcher-package](https://github.com/sbt/sbt-launcher-package).

### Gentoo

En el árbol oficial no hay ebuild para sbt. Pero existen [ebuilds](https://github.com/whiter4bbit/overlays/tree/master/dev-java/sbt-bin) para
hacer un *merge* de sbt a partir de los binarios.
Para hacer un merge de sbt a partir de estos ebuilds, puede hacer lo
siguiente:

    \$ mkdir -p /usr/local/portage && cd /usr/local/portage
    \$ git clone git://github.com/whiter4bbit/overlays.git
    \$ echo "PORTDIR_OVERLAY=\$PORTDIR_OVERLAY /usr/local/portage/overlays" >> /etc/make.conf
    \$ emerge sbt-bin

> **Note:** Por favor reporte cualquier problema con el ebuild
> [aquí](https://github.com/whiter4bbit/overlays/issues).

### Instalación manual

See instruction to install manually.

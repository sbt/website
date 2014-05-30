---
out: Installing-sbt-on-Linux.html
---

  [ZIP]: $sbt_native_package_base$$app_version$/sbt-$app_version$.zip
  [TGZ]: $sbt_native_package_base$$app_version$/sbt-$app_version$.tgz
  [RPM]: $sbt_rpm_package_base$sbt-$app_version$.rpm
  [DEB]: $sbt_deb_package_base$sbt-$app_version$.deb
  [Manual-Installation]: Manual-Installation.html
 
Installing sbt on Linux
-----------------------

### Installing from a universal package

Download [ZIP][ZIP] or [TGZ][TGZ] package and expand it.

### RPM and DEB

The following packages are also officially supported:

  - [RPM][RPM] package
  - [DEB][DEB] package

> **Note:** Please report any issues with these to the
> [sbt-launcher-package](https://github.com/sbt/sbt-launcher-package)
> project.

### Gentoo

In the official tree there is no ebuild for sbt. But there are
[ebuilds](https://github.com/whiter4bbit/overlays/tree/master/dev-java/sbt-bin) to merge sbt from binaries.
To merge sbt from this ebuilds you can do:

    \$ mkdir -p /usr/local/portage && cd /usr/local/portage
    \$ git clone git://github.com/whiter4bbit/overlays.git
    \$ echo "PORTDIR_OVERLAY=\$PORTDIR_OVERLAY /usr/local/portage/overlays" >> /etc/make.conf
    \$ emerge sbt-bin

> **Note:** Please report any issues with the ebuild
> [here](https://github.com/whiter4bbit/overlays/issues).

### Installaing manually

See instruction to install manually.

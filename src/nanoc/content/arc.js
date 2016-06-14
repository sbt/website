$(function () {
  var os_name = 'Unknown';
  var distro = 'Unknown';
  var packager = 'Unknown';
  var ua = navigator.userAgent;
  if (ua.match('Win')) os_name = 'Windows';
  if (ua.match('Mac')) os_name = 'MacOS';
  if (ua.match('X11')) os_name = 'UNIX';
  if (ua.match('Linux')) {
    os_name = 'Linux';
    if (ua.match('CentOS')) {
      distro = 'CentOS';
      packager = 'rpm';
    } // if
    if (ua.match('Debian')) {
      distro = 'Debian';
      packager = 'deb';
    } // if
    if (ua.match('Fedora')) {
      distro = 'Fedora';
      packager = 'rpm';
    } // if
    if (ua.match('Gentoo')) { 
      distro = 'Gentoo';
      packager = 'ebuild';
    } // if
    if (ua.match('Kubuntu')) {
      distro = 'Kubuntu';
      packager = 'deb';
    }
    if (ua.match('Mandriva Linux')) {
      distro = 'Mandriva';
      packager = 'rpm';
    }
    if (ua.match('Mageia')) {
      distro = 'Mageia';
      packager = 'rpm';
    }
    if (ua.match('Red Hat')) {
      distro = 'Red Hat';
      packager = 'rpm';
    }
    if (ua.match('Slackware')) distro = 'Slackware';
    if (ua.match('SUSE')) {
      distro = 'SUSE';
      packager = 'rpm';
    }
    if (ua.match('Turbolinux')) {
      distro = 'Turbolinux';
      packager = 'rpm';
    }
    if (ua.match('Ubuntu')) {
      distro = 'Ubuntu';
      packager = 'deb';
    }
  } // if

  if (os_name !== 'MacOS') {
    $('div.arc_mac').hide();
  } // if
  if (os_name !== 'Windows') {
    $('div.arc_windows').hide();  
  }
  if (os_name === 'Linux') {
    if (packager !== 'ebuild' && packager !== 'Unknown') {
      $('div.distro_gentoo').hide();
    } // if
    if (packager !== 'rpm' && packager !== 'Unknown') {
      $('div.distro_redhat').hide();
    } // if
    if (packager !== 'deb' && packager !== 'Unknown') {
      $('div.distro_debian').hide();
    } // if    
  } else {
    $('div.arc_linux').hide();
  }
});

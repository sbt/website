$(function () {
  var os_name = 'Unknown';
  var ua = navigator.userAgent;
  if (ua.match('Win')) os_name = 'Windows';
  if (ua.match('Mac')) os_name = 'MacOS';
  if (ua.match('X11')) os_name = 'UNIX';
  if (ua.match('Linux')) os_name = 'Linux';

  if (os_name !== 'MacOS') {
    $('div.arc_mac').hide();
  } // if
  if (os_name !== 'Windows') {
    $('div.arc_windows').hide();
  }
  if (os_name !== 'Linux') {
    $('div.arc_linux').hide();
  }
});

jQuery(document).ready(function ($) {
  initOldVersionWarnings($);
});

function initOldVersionWarnings($) {
  var site = splitPath();
  console.log(site);
  if (site.v === 'snapshot') {
    console.log("Detected SNAPSHOT sbt version...");
    showSnapshotWarning(site);
  } else {
    for (var series in ["1.0", "0.13", "0.12", "0.7"]) {
      if (site.v.startsWith(series)) {
        return showVersionWarning(site, series);
      }
    }
  }
}

function splitPath() {
  var base = '' + window.location;
  var path = window.location.pathname;
  if (base.startsWith("file://")) {
    path = path.substring(path.indexOf("/", path.indexOf("sbt")))
  }
  base = base.substring(0, base.indexOf(path));
  var versionEnd = path.indexOf("/", 1);
  var project = "sbt";
  var version = path.substring(1, versionEnd);
  var rest = path.substring(versionEnd + 1);
  return {"b":base, "p":project, "v":version, "r":rest};
}

function getInstead(akkaVersionsData, project, instead) {
  if (Array.isArray(instead)) {
    var found = akkaVersionsData[instead[0]][instead[1]];
    var proj = instead[0];
  } else {
    var found = akkaVersionsData[project][instead];
    var proj = project;
  }
  return {"latest":found.latest, "project":proj};
}

function targetUrl(samePage, site, instead) {
  var page = site.r;
  if (samePage !== true) {
    if (page.substring(0, 5) == 'scala') {
      page = 'scala.html';
    } else if (page.substring(0, 4) == 'java') {
      page = 'java.html';
    } else {
      page = 'docs/index.html';
    }
  }
  else if (page == 'docs/home.html') {
    page = 'docs/index.html';
  }
  var project = site.p;
  return site.b + '/' + instead + '/' + page;
}

function showVersionWarning(site, series) {
  var version = site.v;
  var $floatyWarning = $('<div id="floaty-warning"/>');

  // console.log("Current version info", seriesInfo);

  var latestSeries = availableDocumentationVersions[0];
  var isOutdated = !version.startsWith(latestSeries);
  var isLatestInSeries = version == latestSeries;
  var needsToShow = false;

  if (isOutdated) {
    needsToShow = true;
    $floatyWarning.addClass("warning");

    var instead = latestSeries;
    var insteadSeries = targetUrl(false, site, instead);
    var insteadPage = targetUrl(true, site, instead);

    $floatyWarning
        .append(
            '<p><span style="font-weight: bold">This version of sbt (' + site.p + ' ' + version + ') is outdated and not supported! </span></p>' +
            '<p>Please upgrade to the latest version in <a href="' + insteadSeries + '">' + instead + '</a> series as soon as possible.</p>' +
            '<p id="samePageLink"></p>');
    $.ajax({
      url: insteadPage,
      type: 'HEAD',
      success: function() {
        $('#samePageLink').html('<a href="' + insteadPage + '">Click here to go to the same page on the ' + instead + ' version of the docs.</a>');
      }
    });
  }
  else if (!isLatestInSeries) {
    var instead = latestSeries;
    var insteadPage = targetUrl(true, site, instead);

    needsToShow = true;
    $floatyWarning.addClass("warning");
    $floatyWarning
        .append(
            '<p>' +
            'You are browsing the docs for sbt ' + version + ', ' +
            'however the latest doc in this series is: ' +
            '<a href="' + targetUrl(true, site, instead) + '">' + instead + '</a>. <br/>' +
            '</p>' +
            '<p id="samePageLink"></p>');
    $.ajax({
      url: insteadPage,
      type: 'HEAD',
      success: function() {
        $('#samePageLink').html('<a href="' + insteadPage + '">Click here to go to the same page on the ' + instead + ' version of the docs.</a>');
      }
    });
  }

  if (needsToShow && !versionWasAcked(site.p, version)) {
    var style = '';
    if (site.p != 'akka-stream-and-http-experimental') {
      style = 'style="color:black"'
    }
    var $close = $('<button id="close-floaty-window" ' + style + '>Dismiss Warning for a Day</button>')
        .click(function () {
          ackVersionForADay(site.p, version);
          $floatyWarning.hide();
        });

    $floatyWarning
        .hide()
        // .append($close)
        .prependTo("body")
        .show()
  }
}

function showSnapshotWarning(site) {
  if (!versionWasAcked(site.p, 'snapshot')) {
    var $floatyWarning = $('<div id="floaty-warning" class="warning"/>');
    
    var instead = { 'latest' : 'current' };
    var insteadSeries = targetUrl(false, site, instead);
    var insteadPage = targetUrl(true, site, instead);

    $floatyWarning
        .append(
            '<p><span style="font-weight: bold">You are browsing the snapshot documentation, which most likely does not correspond to the artifacts you are using! </span></p>' +
            '<p>We recommend that you head over to <a href="' + insteadSeries + '">the latest stable version</a> instead.</p>' +
            '<p id="samePageLink"></p>');
    $.ajax({
      url: insteadPage,
      type: 'HEAD',
      success: function() {
        $('#samePageLink').html('<a href="' + insteadPage + '">Click here to go to the same page on the latest stable version of the docs.</a>');
      }
    });
    var style = '';
    if (site.p != 'akka-stream-and-http-experimental') {
      style = 'style="color:black"'
    }
    var $close = $('<button id="close-floaty-window" ' + style + '>Dismiss Warning for a Day</button>')
        .click(function () {
          ackVersionForADay(site.p, 'snapshot');
          $floatyWarning.hide();
        });
    $floatyWarning
        .hide()
        // .append($close)
        .prependTo("body")
        .show()
  }
}

// --- ack outdated versions ---

function ackVersionCookieName(project, version) {
  return "ack-" + project + "-" + version;
}

function ackVersionForADay(project, version) {
  function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
  }
  setCookie(ackVersionCookieName(project, version), 'true', 1)
}
function versionWasAcked(project, version) {
  function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
      var c = ca[i];
      while (c.charAt(0)==' ') c = c.substring(1);
      if (c.indexOf(name) == 0) return c.substring(name.length,c.length);
    }
    return "";
  }

  return getCookie(ackVersionCookieName(project, version)) === 'true';
}

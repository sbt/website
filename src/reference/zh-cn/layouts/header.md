<link href="https://fonts.googleapis.com/css?family=Roboto:100normal,100italic,300normal,300italic,400normal,400italic,500normal,500italic,700normal,700italic,900normal,900italicc" rel="stylesheet" type="text/css"/>
<link href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:400,600,700,900,400italic,700italic" rel="stylesheet" type="text/css">
<div class="container-fluid top nav">
  <div class="row w-100">
    <div class="col-md-4">
      <div class="logo">
        <a href="../../../index.html"><img src="../files/sbt-logo.svg" alt="sbt"></a>
        <span class="versions"><select id="versions"></select></span>
      </div>
    </div>
    <div class="col-md-8">
      <div class="nav" id="topbar">
        <a href="../../../learn.html">Learn</a>
        <a href="../../../download.html">Download</a>
        <a href="../../../community.html">Get Involved</a>
        <a id="source-code" href="https://github.com/sbt/sbt"><img src="../files/github-logo-teal.svg" alt="Source code" class="social"></a>
        <a id="twitter" href="https://twitter.com/scala_sbt"><img src="../files/twitter-logo-teal.svg" alt="sbt on Twitter" class="social"></a>
        <a id="edit-on-github" href="https://github.com/sbt/website/edit/develop/src/reference/$page.localPath$"><img src="../files/octicon-pencil.svg" alt="Edit on GitHub"></a>
      </div>
    </div>
  </div>
</div>
<script type="text/javascript" async>
(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
})(window,document,'script','//www.google-analytics.com/analytics.js','ga');
ga('create', 'UA-41449189-1', 'scala-sbt.org');
ga('send', 'pageview');
</script>
<script type="text/javascript" async>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
  ga('create', 'UA-23127719-1', 'lightbend.com', {'allowLinker': true, 'name': 'tsTracker'});
  ga('tsTracker.require', 'linker');
  ga('tsTracker.linker:autoLink', ['lightbend.com','playframework.com','scala-lang.org','scaladays.org','spray.io','akka.io','scala-sbt.org']);
  ga('tsTracker.send', 'pageview');
</script>
<script type="text/javascript">
\$(function() {
var scrollDown = function() {
if (window.location.hash !== "") {
  setTimeout(function() { \$(window).scrollTop(\$(window).scrollTop() - 120); }, 100);
}
}
scrollDown();
\$(window).bind('hashchange', function() {
scrollDown();
});
});
</script>

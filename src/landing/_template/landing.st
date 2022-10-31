<!DOCTYPE HTML>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>sbt - The interactive build tool</title>
    <meta name="description" content="sbt is the interactive build tool for Scala, Java, and more. Define your tasks in Scala. Run them in parallel from the interactive shell.">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="assets/bootstrap_style.min.css">
    <link rel="stylesheet" href="assets/bootstrap-theme.min.css">
    <link rel="stylesheet" href="assets/github-theme.css">
    <link rel="stylesheet" href="assets/stylesheet.css?2021-07-04">
    <link href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:400,700,400italic,700italic" rel="stylesheet" type="text/css">
    <link rel='shortcut icon' href="assets/favicon.ico" />
    <link rel="icon" type="image/png" sizes="32x32" href="assets/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="96x96" href="assets/favicon-96x96.png">
    <link rel="icon" type="image/png" sizes="16x16" href="assets/favicon-16x16.png">
    <meta name="google-site-verification" content="I28z-8Rg5JifsOR9NpTD9C9dFYutH_Dz8QWOHUDs6lg">

    <!-- OneTrust Cookies Consent Notice (Production Standard, scala-sbt.org, en-GB) start -->
    <script src="https://optanon.blob.core.windows.net/consent/d759c5db-6821-46e0-a988-6bc699efb74e.js" type="text/javascript" charset="UTF-8"></script>
    <script type="text/javascript">
      function OptanonWrapper() {
        //callback when OneTrust is loaded and ready
      }
    </script>
    <!-- OneTrust Cookies Consent Notice (Production Standard, scala-sbt.org, en-GB) end -->

    <link rel="stylesheet"href="https://cdn.jsdelivr.net/npm/docsearch.js@2/dist/cdn/docsearch.min.css" />
  </head>
  <body class="landing">
    <header>
      <nav>
        <div class="container">
          <div class="row">
            <div class="col-md-2 col-xs-3">
              <div class="logo">
                <a href="index.html">
                  <img src="assets/sbt-logo.svg" alt="sbt">
                </a>
              </div>
            </div>
            <div class="col-md-7 col-xs-9">
              <div class="nav" id="topbar">
                <a href="learn.html">Learn</a>
                <a href="download.html">Download</a>
                <a href="community.html">Get Involved</a>
                <a id="source-code" href="https://github.com/sbt/sbt"><img src="assets/github-logo-teal.svg" alt="Source code" class="social"></a>
                <a id="twitter" href="https://twitter.com/scala_sbt"><img src="assets/twitter-logo-teal.svg" alt="sbt on Twitter" class="social"></a>
              </div>
            </div>
            <div class="col-md-3">
              <div class="docsearch">
                <input type="text" id="doc-search-bar" placeholder="Search...">
                <ul class="result-container" id="result-container" style="display: none;"></ul>
              </div>
            </div>
          </div>
        </div>
      </nav>
      <div class="header-content">
        <div class="container">
          <div class="row">
            <div class="col-md-12">
              <div id="main" class="text-center">
                <h1>The interactive build tool</h1>
                <span class="statement">
                Define your tasks in Scala. Run them in parallel from sbt's interactive shell.
                </span><br><br>
                <a href="download.html" class="btn" role="button" style="margin-right: 2em">Get sbt $sbtVersion$ (latest stable)</a>

                <a href="/1.x/docs/" class="btn" role="button" style="margin-left: 2em">Documentation</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>

    <div class="fw-wrapper white">
      <div class="container content">
<div class="row">
  <h2>Simple things easy</h2>
  <div class="col-md-9">
<h3>Hello, world!</h3>
<pre><code class="language-scala prettyprint"><span class="typ">ThisBuild</span> / scalaVersion := <span class="str">"$scala3ExampleVersion$"</span>
</code></pre>
You just need one line of <code>build.sbt</code> to get started with Scala. Learn more on <a href="1.x/docs/sbt-by-example.html">sbt by Example</a> page.

<h3>sbt new</h3>
Choose from community-maintained <a href="https://github.com/search?o=desc&p=1&q=g8&s=stars&type=Repositories">Giter8 templates</a> to jump start your project:

<pre><code class="prettyprint">\$ sbt new scala/scala-seed.g8
\$ sbt new playframework/play-scala-seed.g8
\$ sbt new akka/akka-http-quickstart-scala.g8
\$ sbt new http4s/http4s.g8
\$ sbt new holdenk/sparkProjectTemplate.g8
</code></pre>
  </div>
</div> <!-- row -->

<div class="row">
  <h2 class="text-center">Why sbt?</h2>
  <div class="col-md-3">
<h3>For Scala and Java</h3>
sbt is built for Scala and Java projects. It is the build tool of choice for <a href="https://scalacenter.github.io/scala-developer-survey-2019/#which-build-tools-do-you-use">93.6%</a> of the Scala developers (2019). One of the examples of Scala-specific feature is the ability to <i>cross build</i> your project against multiple Scala versions.
  </div>

  <div class="col-md-3">
<h3>Typesafe and parallel</h3>
<code>build.sbt</code> is a Scala-based DSL to express parallel processing task graph. Typos in <code>build.sbt</code> will be caught as a compilation error.
  </div>

  <div class="col-md-3">
<h3>Speedy iteration</h3>
With Zinc incremental compiler and file watch (<code>~</code>), edit-compile-test loop is fast and incremental.
  </div>

  <div class="col-md-3">
<h3>Extensible</h3>
Adding support for new tasks and platforms (like Scala.js) is as easy as writing <code>build.sbt</code>. Join <a href="https://www.scala-sbt.org/1.x/docs/Community-Plugins.html">100+ community-maintained plugins</a> to share and reuse sbt tasks.
  </div>
</div> <!-- row -->

<div class="row">
  <h2 class="text-center">Beyond the build tool</h2>
  <div class="col-md-6 testimonial">
Previously 21% of the developer's time was spent in redeploying WebLogic application, which was unacceptable. We had to find a solution: some way of productivity to go high. This is one of the reasons why we have Play and Akka. We were really impressed with the hot reload. Now the developers are clearly more productive.<br>
<i>— Keyur Shah, Associate Fellow, Verizon</i>
  </div>

  <div class="col-md-6 testimonial">
sbt is a core critical component of Tapad's tech stack. Its use beyond the build tool is largely responsible for the massive efficiency gains that the engineering organization has come to appreciate. sbt and its interactive shell comprise the interface to the standardized application testing, release, and deployment platform. Artifacts and applications are delivered, updated, and scaled across four global data centers.<br>
<i>— Jeffrey Olchovy, CTO, Tapad</i>
  </div>

  <div class="col-md-6 testimonial">
You can use sbt-native-packager to build native formats like Docker, sbt-release has nice steps to take care of versioning, sbt-bintray can be used to release to Bintray. There's a pattern: You bring in a plugin. It has some settings. You read the documentations to learn about the setting. And it does its jobs.<br>
<i>— Gabriel Asman, Software Developer, Ovo Energy</i>
  </div>
</div> <!-- row -->

        $page.contents$
      </div>
    </div>
    <div class="fw-wrapper navy-ltr support-strip">
      <div class="container">
        <div class="row">
          <div class="col-md-12">
            <div class="support-item">
              <div class="support-icon">
                <svg class="svg-icon svg-icon-chat" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 97.5 85.2" enable-background="new 0 0 97.5 85.2"><path stroke="#fff" stroke-width="4.282" stroke-linecap="round" stroke-miterlimit="10" d="M27 29.5h-16.3c-4.7 0-8.6 3.9-8.6 8.6v25.7c0 4.7 3.9 8.6 8.6 8.6h2.7c.8 0 1.5.7 1.5 1.5v7.8c0 1.3 1.6 2 2.5 1l9.5-9.5c.5-.5 1.2-.8 2-.8h20.2c4.7 0 8.6-3.9 8.6-8.6v-7.8" fill="none"/><path fill="#fff" d="M85 0h-40c-6.9 0-12.5 5.6-12.5 12.5v33.4c0 2.2 1.8 4.1 4.1 4.1h29.9c.7 0 1.3.3 1.8.7l10 10c1.6 1.6 4.3.5 4.3-1.8v-6.5c0-1.4 1.1-2.5 2.5-2.5 6.9 0 12.5-5.6 12.5-12.5v-25c-.1-6.8-5.8-12.4-12.6-12.4z"/></svg>
              </div>
              <div class="support-detail">
                <h2>Community Support</h2>
                <a href="https://stackoverflow.com/questions/tagged/sbt">StackOverflow</a>
              </div>
            </div>
            <div class="support-item">
              <div class="support-icon">
                <svg id="lightbend-icon-reverse" class="svg-icon svg-icon-lightbend-reverse" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 302 262"><title>lightbend-icon</title><g id="icon"><path d="M1,195v56a10,10,0,0,0,10,10H291a10,10,0,0,0,10-10V195a557.85,557.85,0,0,1-150,20A557.85,557.85,0,0,1,1,195Z" style="fill:#fff"/><path d="M291,1H11A10,10,0,0,0,1,11V176a539.94,539.94,0,0,0,150,21,539.94,539.94,0,0,0,150-21V11A10,10,0,0,0,291,1Z" style="fill:#fff"/></g></svg>
              </div>
              <div class="support-detail">
                <h2>Commercial Support</h2>
                <a href="https://www.lightbend.com/services/expert-support">Lightbend Subscription</a>
                <a href="https://www.lightbend.com/services/training">Training</a>
                <a href="https://www.lightbend.com/services/consulting">Consulting</a>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>

    <footer>
      <div class="container footer">
        <div class="row">
          <div class="col-md-8 sbt">
            <nav>
              <a href="index.html">
                <img src="assets/sbt-logo-reverse.svg" alt="sbt">
              </a>
              <a href="learn.html">Learn</a>
              <a href="download.html">Download</a>
              <a href="community.html">Get Involved</a>
              <a href="thank-you.html">Thank You</a>
            </nav>
            <p class="legal">
              &copy; 2016-<script>document.write(new Date().getFullYear())</script> Lightbend, Inc. |
              <a href="https://www.lightbend.com/legal/licenses" target="_blank">Licenses</a> |
              <a href="https://www.lightbend.com/legal/terms" target="_blank">Terms</a> |
              <a href="https://www.lightbend.com/legal/privacy" target="_blank">Privacy Policy</a> |
              <a href="cookie.html">Cookie Listing</a> |
              <a class="optanon-toggle-display">Cookie Settings</a>
            </p>
          </div>
          <div class="col-md-4 text-right ts">
            <a href="https://www.lightbend.com">
              <img src="assets/lightbend_reverse.png" alt="Lightbend, Inc.">
            </a>

          </div>
        </div>
      </div>
    </footer>
    <!-- Placed at the end -->
    <script src="assets/jquery.min.js" type="text/javascript"></script>
    <script src="assets/bootstrap.min.js" type="text/javascript"></script>
    <script type="text/plain" class="optanon-category-2" async>
    (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
    (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
    m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
    })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
    ga('create', 'UA-41449189-1', 'scala-sbt.org');
    ga('send', 'pageview');
    </script>
    <script type="text/plain" class="optanon-category-2" async>
      (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
      m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
      })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
      ga('create', 'UA-23127719-1', 'typesafe.com', {'allowLinker': true, 'name': 'tsTracker'});
      ga('tsTracker.require', 'linker');
      ga('tsTracker.linker:autoLink', ['typesafe.com','playframework.com','scala-lang.org','scaladays.org','spray.io','akka.io','scala-sbt.org']);
      ga('tsTracker.send', 'pageview');
    </script>
    <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/docsearch.js@2/dist/cdn/docsearch.min.js"></script>
    <script type="text/javascript"> docsearch({
      apiKey: 'e47ee877a07ea1c48722f08430d54913',
      indexName: 'scala-sbt',
      inputSelector: '#doc-search-bar',
      debug: false // Set debug to true if you want to inspect the dropdown
    });
    </script>
  </body>
</html>

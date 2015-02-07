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
            <a href="https://groups.google.com/forum/#!forum/sbt-dev">Mailing List</a>
            <a href="https://stackoverflow.com/questions/tagged/sbt">StackOverflow</a>
          </div>
        </div>
        <div class="support-item">
          <div class="support-icon">
            <svg class="svg-icon svg-icon-typesafe" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 154 154" enable-background="new 0 0 154 154"><path fill="#fff" d="M49.7 114c-4.8 0-8.7-5.1-8.7-9.9v-.2c0-4.8 3.9-7.9 8.7-7.9h100c.9-3 1.6-7 2-11h-84c-4.8 0-8.7-3.7-8.7-8.5s3.9-8.5 8.7-8.5h83.7c-.5-4-1.2-8-2.3-11h-63.4c-4.8 0-8.7-3.7-8.7-8.5s3.9-8.5 8.7-8.5h56c-13.1-23-37.2-37.8-64.7-37.8-41.4 0-75 33.3-75 74.7s33.6 75.6 75 75.6c28.4 0 53.1-15.4 65.8-38.4h-93.1z"/></svg>
          </div>
          <div class="support-detail">
            <h2>Commercial Support</h2>
            <a href="http://typesafe.com/subscription">Typesafe Subscription</a>
            <a href="http://typesafe.com/subscription/training">Training</a>
            <a href="http://typesafe.com/subscription/consulting">Consulting</a>
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
          <a href="<%= @items['/'].path %>index.html">
            <img src="<%= @items['/typesafe_sbt_reverse/'].path %>" alt="sbt">
          </a>
          <a href="<%= @items['/documentation/'].path %>">Documentation</a>
          <a href="<%= @items['/download/'].path %>">Download</a>
          <a href="<%= @items['/community/'].path %>">Community</a>
        </nav>
      </div>
      <div class="col-md-4 text-right ts">
        &copy; 2015 Typesafe Inc.
        <a href="https://typesafe.com">
          <img src="<%= @items['/typesafe_reverse/'].path %>" alt="Typesafe, Inc.">
        </a>
       
      </div>
    </div>
  </div>
</footer>
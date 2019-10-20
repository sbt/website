$(document).ready(function() {
   // array of versions provided by short js file in root so that
   // old documentation versions do not need to be 
   var versions = availableDocumentationVersions;
   var releasePath = 'release/';
   var snapshotPath = 'snapshot/';
   var docsPath = "docs/";

   // the selected version is in the pathname of the current url
   var selected = document.location.pathname.split('/')[1];

   // get the version drop-down
   var select = $("#versions");

   // populate the options with the latest list of versions
   for(var i = 0; i < versions.length; i++) {
      var v = versions[i];
      var sel = '';
      if (v == selected) sel = 'selected ';
      var label = 'Version ' + v;
      if (i == 0) label = 'Latest version';
      select.append('<option ' + sel + 'value="' + v + '">' + label + '</option>');
   }

   // check if primary exists, go there if it does, or go to fallback if it does not
   var gotoIfExists = function(primary, fallback) {
      $.ajax({
        type: 'HEAD',
        url: primary,
        success: function() { document.location.href = primary },
        error: function() { document.location.href = fallback },
      });
   };

   // return a new URL String with its path transformed by function f: String => String
   var mapPath = function(urlString, f) {
      var u = document.createElement('a');
      u.href = urlString;
      u.pathname = f(u.pathname);
      return u.href;
   };

   // when an option is selected, switch to that version of the current page,
   //  but if it doesn't exist, go to the index for that version
   select.change(function() {
      var newV = $(this).val();
      var newPath = newV + '/';
      var oldLoc = document.location.href;

      var changeVersion = function(oldPathname) {
         var changed = oldPathname.replace(selected + '/', newPath).replace(snapshotPath, newPath).replace(releasePath, newPath);
         // This occurs for the unversioned /index.html. Redirect to the versioned path in this case.
         if (changed == oldPathname)
             changed = newPath + docsPath + 'home.html';
         return changed;
      };
      var newVersionIndex = function(pathname) { return newPath + docsPath + 'index.html'; };

      gotoIfExists( mapPath(oldLoc, changeVersion), mapPath(oldLoc, newVersionIndex) );
   });
});

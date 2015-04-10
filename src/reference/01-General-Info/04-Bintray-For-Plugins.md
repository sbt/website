---
out: Bintray-For-Plugins.html
---

Bintray For Plugins
-------------------

**This is currently in Beta mode.**

sbt hosts their community plugin repository on
[Bintray](https://bintray.com/sbt).
Bintray is a repository hosting site, similar to github, which allows users to contribute their own
plugins, while sbt can aggregate them together in a common repository.

This document walks you through the means to create your own repository
for hosting your sbt plugins and then linking them into the sbt shared
repository. This will make your plugins available for all sbt users
without additonal configuration (besides declaring a dependency on your
plugin).

To do this, we need to perform the following steps:

### Create an account on Bintray

First, go to <https://bintray.com>. Click on the sign in link on the top
left, and then the sign up button.

*Note: If you had an account on repo.scala-sbt.org previous, please use
the same email address when you create this account.*

### Create a repository for your sbt plugins

Now, we'll create a repository to host our personal sbt plugins. In
bintray, create a generic repository called `sbt-plugins`.

First, go to your user page and click on the `new repository` link:


<img src="files/bintray-new-repo-link.png" style="width: 100%; height: 100%">

You should see the following dialog:

<img src="files/bintray-new-repo-dialog.png" style="width: 100%; height: 100%">

Fill it out similarly to the above image, the settings are:

- Name:   sbt-plugins
- Type:   Generic
- Desc:   My sbt plugins
- Tags:   sbt

Once this is done, you can begin to configure your sbt-plugins to
publish to bintray.

### Add the bintray-sbt plugin to your build.

First, add the bintray-sbt to your plugin build.

First, create a `project/bintray.sbt` file

```scala
addSbtPlugin("me.lessis" % "bintray-sbt" % "$bintray_sbt_version$")
```

Next, a make sure your `build.sbt` file has the following settings

```scala
import bintray.Keys._

lazy val commonSettings = Seq(
  version in ThisBuild := "<YOUR PLUGIN VERSION HERE>",
  organization in ThisBuild := "<INSERT YOUR ORG HERE>"
)

lazy val root = (project in file(".")).
  settings(commonSettings ++ bintrayPublishSettings: _*).
  settings(
    sbtPlugin := true,
    name := "<YOUR PLUGIN HERE>",
    description := "<YOUR DESCRIPTION HERE>",
    // This is an example.  bintray-sbt requires licenses to be specified 
    // (using a canonical name).
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    publishMavenStyle := false,
    repository in bintray := "sbt-plugins",
    bintrayOrganization in bintray := None
  )
```

Make sure your project has a valid license specified, as well as unique
name and organization.

### Make a release

> *Note: bintray does not support snapshots. We recommend using
[git-revisions supplied by the sbt-git plugin](https://github.com/sbt/sbt-git#versioning-with-git)*.

Once your build is configured, open the sbt console in your build and run

```
sbt> publish
```

The plugin will ask you for your credentials. If you don't know where
they are, you can find them on [Bintray](https://bintray.com).

1.  Login to the website with your credentials.
2.  Click on your username
3.  Click on edit profile
4.  Click on API Key

This will get you your password. The bintray-sbt plugin will save your
API key for future use.

*NOTE: We have to do this before we can link our package to the sbt
org.*

### Linking your package to the sbt organization

Now that your plugin is packaged on bintray, you can include it in the
community sbt repository. To do so, go to the
[Community sbt repository](https://bintray.com/sbt/sbt-plugin-releases)
screen.

1.  Click the green `include my package` button and select your plugin.
    <img src="files/bintray-include-my-package.png" style="width: 100%; height: 100%">
2.  Search for your plugin by name and click on the link.
    <img src="files/bintray-link-plugin-search.png" style="width: 100%; height: 100%">
3.  Your request should be automatically filled out, just click send
    <img src="files/bintray-include-package-form.png" style="width: 100%; height: 100%">
4.  Shortly, one of the sbt repository admins will approve your link
    request.

From here on, any releases of your plugin will automatically appear in
the community sbt repository. Congratulations and thank you so much for
your contributions!

### Linking your package to the sbt organization (sbt org admins)

If you're a member of the sbt organization on bintray, you can link your
package to the sbt organization, but via a different means. To do so,
first navigate to the plugin you wish to include and click on the link
button:

<img src="files/bintray-org-member-link-button.png" style="width: 100%; height: 100%">

After clicking this you should see a link like the following:

<img src="files/bintray-org-member-link-dialog.png" style="width: 100%; height: 100%">

Click on the `sbt/sbt-plugin-releases` repository and you're done! Any
future releases will be included in the sbt-plugin repository.

### Summary

After setting up the repository, all new releases will automatically be
included the sbt-plugin-releases repository, available for all users.
When you create a new plugin, after the initial release you'll have to
link it to the sbt community repository, but the rest of the setup
should already be completed. Thanks for you contributions and happy
hacking.

import sbt._
import Keys._
import com.typesafe.sbt.git.GitRunner
import com.typesafe.sbt.{SbtGhPages, SbtGit, SbtSite, site=>sbtsite}
import SbtGhPages.{ghpages, GhPagesKeys => ghkeys}
import SbtGit.{git, GitKeys}
import SbtSite.SiteKeys

object Docs {
  lazy val Redirect = config("redirect")
  lazy val RedirectTutorial = config("redirect-tutorial")

  // New documents will live under /x.y/ instead of /x.y.z/.
  // Currently the following files needs to be manually updated:
  // - src/nanoc/nanoc.yaml
  // - src/reference/template.properties
  lazy val targetSbtBinaryVersion = "1.0"
  lazy val targetSbtFullVersion = "1.0.0-M5"

  val zeroTwelveGettingStarted = List("Setup.html", "Hello.html", "Directories.html", "Running.html", "Basic-Def.html",
    "Scopes.html", "More-About-Settings.html", "Library-Dependencies.html",
    "Multi-Project.html", "Using-Plugins.html", "Full-Def.html", "Summary.html")
  val zeroThirteenGettingStarted = List("Setup.html",
    "Installing-sbt-on-Mac.html", "Installing-sbt-on-Windows.html", "Installing-sbt-on-Linux.html", "Manual-Installation.html",
    "Activator-Installation.html", "Hello.html", "Directories.html", "Running.html", "Basic-Def.html", "Scopes.html",
    "More-About-Settings.html", "Library-Dependencies.html", "Multi-Project.html", "Using-Plugins.html",
    "Custom-Settings.html", "Organizing-Build.html", "Summary.html", "Bare-Def.html", "Full-Def.html")
  val languages = List("ja", "zh-cn", "es")

  def redirectTutorialSettings: Seq[Setting[_]] = Seq(
      mappings in RedirectTutorial := {
        val output = target.value / RedirectTutorial.name
        val s = streams.value
        generateRedirect("../docs/Getting-Started.html", output / "index.html", s.log)
        zeroThirteenGettingStarted foreach { x =>
          generateRedirect(s"../docs/$x", output / x, s.log)
        }
        languages foreach { lang =>
          generateRedirect(s"../../docs/$lang/Getting-Started.html", output / lang / "index.html", s.log)
          zeroThirteenGettingStarted foreach { x =>
            generateRedirect(s"../../docs/$lang/$x", output / lang / x, s.log)
          }
        }
        output ** AllPassFilter --- output x relativeTo(output)
      }
    )

  def redirectSettings: Seq[Setting[_]] = Seq(
    mappings in Redirect := {
      val output = target.value / Redirect.name
      val s = streams.value
      val gettingStarted = output / "Getting-Started"
      generateRedirect("../Getting-Started.html", gettingStarted / "Welcome.html", s.log)
      zeroTwelveGettingStarted foreach { x =>
        generateRedirect(s"../$x", gettingStarted / x, s.log)
      }
      val dt = output / "Detailed-Topics"
      generateRedirect("../Detailed-Topics.html", dt / "index.html", s.log)
      generateRedirect("../Tasks-and-Commands.html", dt / "Command-Details-Index.html", s.log)
      generateRedirect("../Sbt-Launcher.html", dt / "Launcher.html", s.log)
      generateRedirect("../Migrating-from-sbt-0.7.x.html", dt / "Migrating-from-sbt-0.7.x-to-0.10.x.html", s.log)
      generateRedirect("../Incremental-Recompilation.html", dt / "Understanding-incremental-recompilation.html", s.log)
      Seq("Artifacts.html", "Best-Practices.html", "Classpaths.html", "Command-Line-Reference.html",
          "Compiler-Plugins.html", "Configuration-Index.html", "Configuring-Scala.html", "Console-Project.html",
          "Cross-Build.html", "Dependency-Management-Flow.html", "Dependency-Management-Index.html",
          "Forking.html", "Global-Settings.html", "Inspecting-Settings.html", "Java-Sources.html",
          "Library-Management.html", "Local-Scala.html", "Macro-Projects.html", "Mapping-Files.html",
          "Parallel-Execution.html", "Parsing-Input.html", "Paths.html", "Plugins-and-Best-Practices.html",
          "Process.html", "Proxy-Repositories.html", "Publishing.html", "Resolvers.html",
          "Running-Project-Code.html", "Scripts.html", "Setup-Notes.html", "TaskInputs.html",
          "Tasks-and-Commands.html", "Tasks.html", "Testing.html", "Triggered-Execution.html",
          "Update-Report.html") foreach { x =>
        generateRedirect(s"../$x", dt / x, s.log)
      }
      val arc = output / "Architecture"
      generateRedirect("../Developers-Guide.html", arc / "index.html", s.log)
      generateRedirect("../Core-Principles.html", arc / "Core-Principles.html", s.log)
      generateRedirect("../Setting-Initialization.html", arc / "Setting-Initialization.html", s.log)
      val cmm = output / "Community"
      generateRedirect("../General-Info.html", cmm / "index.html", s.log)
      generateRedirect("../Contributing-to-sbt.html", cmm / "Opportunities.html", s.log)
      Seq("Bintray-For-Plugins.html", "ChangeSummary_0.12.0.html", "ChangeSummary_0.13.0.html",
          "Changes.html", "Community-Plugins.html", "Credits.html", "Nightly-Builds.html",
          "Repository-Rules.html", "Using-Sonatype.html") foreach { x =>
        generateRedirect(s"../$x", cmm / x, s.log)
      }
      val ext = output / "Extending"
      Seq("Build-Loaders.html", "Build-State.html", "Command-Line-Applications.html",
          "Commands.html", "Input-Tasks.html",  "Plugins-Best-Practices.html", "Plugins.html",
          "Settings-Core.html") foreach { x =>
        generateRedirect(s"../$x", ext / x, s.log)
      }
      val lnc = output / "Launcher"
      generateRedirect("../sbt-Launcher.html", lnc / "index.html", s.log)
      generateRedirect("../Launcher-Getting-Started.html", lnc / "GettingStarted.html", s.log)
      generateRedirect("../Launcher-Architecture.html", lnc / "Architecture.html", s.log)
      generateRedirect("../Launcher-Configuration.html", lnc / "Configuration.html", s.log)
      val howto = output / "Howto"
      generateRedirect("../Howto.html", howto / "index.html", s.log)
      generateRedirect("../Howto-Classpaths.html", howto / "classpaths.html", s.log)
      generateRedirect("../Howto-Customizing-Paths.html", howto / "defaultpaths.html", s.log)
      generateRedirect("../Howto-Generating-Files.html", howto / "geratefiles.html", s.log)
      generateRedirect("../Howto-Inspect-the-Build.html", howto / "inspect.html", s.log)
      generateRedirect("../Howto-Interactive-Mode.html", howto / "interactive.html", s.log)
      generateRedirect("../Howto-Logging.html", howto / "logging.html", s.log)
      generateRedirect("../Howto-Project-Metadata.html", howto / "metadata.html", s.log)
      generateRedirect("../Howto-Package.html", howto / "package.html", s.log)
      generateRedirect("../Howto-Running-Commands.html", howto / "runningcommands.html", s.log)
      generateRedirect("../Howto-Scala.html", howto / "scala.html", s.log)
      generateRedirect("../Howto-Scaladoc.html", howto / "scaladoc.html", s.log)
      generateRedirect("../Howto-Triggered.html", howto / "triggered.html", s.log)
      val exp = output / "Examples"
      generateRedirect("../Examples.html", exp / "index.html", s.log)
      generateRedirect("../Basic-Def-Examples.html", exp / "Quick-Configuration-Examples.html", s.log)
      generateRedirect("../Full-Def-Example.html", exp / "Full-Configuration-Example.html", s.log)
      generateRedirect("../Advanced-Configurations-Example.html", exp / "Advanced-Configurations-Example.html", s.log)
      generateRedirect("../Advanced-Command-Example.html", exp / "Advanced-Command-Example.html", s.log)

      output ** AllPassFilter --- output x relativeTo(output)
    }
  )

  def generateRedirect(path: String, linkFile: File, log: Logger): File = {
    val page = s"""<!DOCTYPE html SYSTEM "about:legacy-compat">
<html>
  <head>
    <meta charset="utf-8"/>
    <meta content="width=device-width, initial-scale=1" name="viewport"/>
    <meta http-equiv="refresh" content="0;url=$path"></meta>
    <title>This page has moved</title>
  </head>
  <body>
    <p>This page has moved to <a href="$path">$path</a>.</p>
  </body>
</html>
"""
    IO.write(linkFile, page, IO.utf8)
    log.debug(s"generated ${linkFile.toString} that links to $path")
    linkFile
  }


  def customGhPagesSettings: Seq[Setting[_]] = ghpages.settings ++ Seq(
    git.remoteRepo := "git@github.com:sbt/sbt.github.com.git",
    GitKeys.gitBranch in ghkeys.updatedRepository := Some("master"),
    ghkeys.synchLocal := syncLocalImpl.value
  )

  // This task is responsible for updating the gh-pages branch on some temp dir.
  // On the branch there are files that was generated in some other ways such as:
  // - Scaladoc API doc
  // - SXR hyperlinked source
  // - older documentation
  //
  // This task's job is to call "git rm" on files and directories that this project owns
  // and then copy over the newly generated files.
  val syncLocalImpl = Def.task {
    // sync the generated site
    val repo = ghkeys.updatedRepository.value
    val fullVersioned = repo / targetSbtFullVersion
    val versioned = repo / targetSbtBinaryVersion
    val git = GitKeys.gitRunner.value
    val s = streams.value

    // remove symlinks
    // uncomment after sbt 1.0
    // val apiLink = versioned / "api"
    // val sxrLink = versioned / "sxr"
    // val releaseLink = repo / "release"
    // if (apiLink.exists) apiLink.delete
    // if (sxrLink.exists) sxrLink.delete
    // if (releaseLink.exists) releaseLink.delete

    gitRemoveFiles(repo, IO.listFiles(versioned).toList, git, s)

    // uncomment afer sbt 1.0
    // gitRemoveFiles(repo, (repo * "*.html").get.toList, git, s)

    val mappings =  for {
      (file, target) <- SiteKeys.siteMappings.value if siteInclude(file)
    } yield (file, repo / target)
    IO.copy(mappings)

    // symlink API and SXR
    // uncomment after sbt 1.0
    // symlink(s"../$targetSbtFullVersion/api/", apiLink, s.log)
    // symlink(s"../$targetSbtFullVersion/sxr/", sxrLink, s.log)
    // symlink(s"$targetSbtBinaryVersion/", releaseLink, s.log)
    repo
  }

  def gitRemoveFiles(dir: File, files: List[File], git: GitRunner, s: TaskStreams): Unit = {
    if(!files.isEmpty)
      git(("rm" :: "-r" :: "-f" :: "--ignore-unmatch" :: files.map(_.getAbsolutePath)) :_*)(dir, s.log)
    ()
  }

  def siteInclude(f: File) = true

  // TODO: platform independence/use symlink from Java 7
  def symlink(path: String, linkFile: File, log: Logger): Unit =
    "ln" :: "-s" :: path :: linkFile.getAbsolutePath :: Nil ! log match {
      case 0 => ()
      case code => println(code) // sys.error("Could not create symbolic link '" + linkFile.getAbsolutePath + "' with path " + path)
    }
}

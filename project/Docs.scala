import sbt._
import Keys._
import com.typesafe.sbt.git.GitRunner
import com.typesafe.sbt.{SbtGhPages, SbtGit, SbtSite, site=>sbtsite}
import SbtGhPages.{ghpages, GhPagesKeys => ghkeys}
import SbtGit.{git, GitKeys}
import SbtSite.SiteKeys

object Docs {
  // New documents will live under /x.y/ instead of /x.y.z/.
  // Currently the following files needs to be manually updated:
  // - src/nanoc/nanoc.yaml
  // - src/reference/template.properties
  // - src/tutorial/template.properties
  lazy val targetSbtBinaryVersion = "0.13"
  lazy val targetSbtFullVersion = "0.13.2"

  def customGhPagesSettings: Seq[Setting[_]] = ghpages.settings ++ Seq(
    git.remoteRepo := "git@github.com:sbt/website.git",
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
    gitRemoveFiles(repo, IO.listFiles(versioned).toList, git, s)
    gitRemoveFiles(repo, (repo * "*.html").get.toList, git, s)
    val mappings =  for {
      (file, target) <- SiteKeys.siteMappings.value if siteInclude(file)
    } yield (file, repo / target)
    IO.copy(mappings)

    // symlink API and SXR
    symlink((fullVersioned / "api").getAbsolutePath, versioned / "api", s.log)
    symlink((fullVersioned / "sxr").getAbsolutePath, versioned / "sxr", s.log)

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
      case code => sys.error("Could not create symbolic link '" + linkFile.getAbsolutePath + "' with path " + path)
    }
}
